package org.example.newsenricher.processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.ForeachWriter;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.functions;
import org.apache.spark.sql.streaming.StreamingQuery;
import org.example.newsenricher.config.AppConfig;
import org.example.newsenricher.util.HttpClientUtil;
import org.example.newsenricher.util.SchemaUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class NewsEnrichmentProcessor {

    public static StreamingQuery process(Dataset<Row> df, AppConfig appConfig) {
        Dataset<Row> parsed = df
                .selectExpr("CAST(value AS STRING)")
                .select(functions.from_json(functions.col("value"), SchemaUtil.getNewsSchema()).as("data"))
                .select("data.*");

        final String nerApiUrl = appConfig.getNerApiUrl();
        final ObjectMapper mapper = new ObjectMapper();

        try {
            return parsed.writeStream()
                    .foreachBatch((batchDF, batchId) -> {
                        try {
                            log.info("Processing batch {} with {} records", batchId, batchDF.count());

                            List<String> texts = new ArrayList<>();
                            batchDF.toLocalIterator().forEachRemaining(row -> {
                                texts.add(row.getAs("title") + " " + row.getAs("description"));
                            });

                            int batchSize = 200;
                            for (int i = 0; i < texts.size(); i += batchSize) {
                                int end = Math.min(i + batchSize, texts.size());
                                List<String> subTexts = texts.subList(i, end);
                                Map<String, List<String>> payload = new HashMap<>();
                                payload.put("texts", subTexts);
                                log.info("Length of sub-batch [{}-{}]: {}", i, end, subTexts.size());
                                try {
                                    String response = HttpClientUtil.postJson(
                                            nerApiUrl,
                                            mapper.writeValueAsString(payload)
                                    );

                                    log.info("NER response for batch {}, sub-batch [{}-{}]: {}", batchId, i, end, response);
                                    // TODO: Save enriched output
                                } catch (Exception e) {
                                    log.error("Sub-batch [{}-{}] enrichment failed in batch {}", i, end, batchId, e);
                                }
                            }
                        } catch (Exception e) {
                            log.error("Batch {} enrichment failed", batchId, e);
                        }
                    })
                    .start("news-enrichment-query");
        } catch (
                Exception e) {
            log.error("Streaming query failed", e);
            throw new RuntimeException("Streaming job failed", e);
        }
    }
}