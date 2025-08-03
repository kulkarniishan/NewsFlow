package org.example.newsenricher.config;

import org.apache.spark.sql.SparkSession;

public class SparkConfig {
    public static SparkSession createSparkSession(AppConfig appConfig) {
        return SparkSession.builder().appName(appConfig.getSparkAppName()).master(appConfig.getSparkMaster()).getOrCreate();
    }
}
