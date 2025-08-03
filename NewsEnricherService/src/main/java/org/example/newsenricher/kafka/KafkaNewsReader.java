package org.example.newsenricher.kafka;

import lombok.RequiredArgsConstructor;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.streaming.StreamingQuery;
import org.apache.spark.sql.streaming.StreamingQueryException;
import org.example.newsenricher.config.AppConfig;
import org.example.newsenricher.processor.NewsEnrichmentProcessor;

@RequiredArgsConstructor
public class KafkaNewsReader {
    private final SparkSession sparkSession;
    private final AppConfig appConfig;

    public void readKafkaStreamAndProcess() {
        System.out.println(appConfig.getKafkaBootstrapServers() + " " + appConfig.getKafkaTopic());
        Dataset<Row> df = sparkSession.readStream()
                .format("kafka")
                .option("kafka.bootstrap.servers", appConfig.getKafkaBootstrapServers())
                .option("subscribe", appConfig.getKafkaTopic())
                .option("startingOffsets", "earliest")
                .load();

        try {
            StreamingQuery query = NewsEnrichmentProcessor.process(df, appConfig);
            query.awaitTermination();  // This blocks the main thread
        } catch (StreamingQueryException e) {
            throw new RuntimeException("StreamingQueryException", e);
        }
    }
}
