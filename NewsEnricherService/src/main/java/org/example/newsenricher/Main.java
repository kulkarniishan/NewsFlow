package org.example.newsenricher;

import org.apache.spark.sql.SparkSession;
import org.example.newsenricher.kafka.KafkaNewsReader;
import org.example.newsenricher.config.AppConfig;
import org.example.newsenricher.config.SparkConfig;
import org.example.newsenricher.processor.NewsEnrichmentProcessor;

public class Main {
    public static void main(String[] args) {
        AppConfig appConfig = new AppConfig();
        SparkSession spark = SparkConfig.createSparkSession(appConfig);
        KafkaNewsReader kafkaNewsReader = new KafkaNewsReader(spark, appConfig);
        kafkaNewsReader.readKafkaStreamAndProcess();
    }
}
