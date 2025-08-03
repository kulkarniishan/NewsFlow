package org.example.newsenricher.config;
import java.io.InputStream;
import java.util.Map;
import org.yaml.snakeyaml.Yaml;

public class AppConfig {
    private final Map<String, Object> config;

    public AppConfig() {
        Yaml yaml = new Yaml();
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("application.yaml");
        config = yaml.load(inputStream);
    }

    public String getKafkaBootstrapServers() {
        return ((Map<String, String>) config.get("kafka")).get("bootstrapServers");
    }

    public String getKafkaTopic() {
        return ((Map<String, String>) config.get("kafka")).get("topic");
    }

    public String getSparkAppName() {
        return ((Map<String, String>) config.get("spark")).get("appName");
    }

    public String getSparkMaster() {
        return ((Map<String, String>) config.get("spark")).get("master");
    }

    public String getNerApiUrl() {
        return ((Map<String, String>) config.get("nerApi")).get("url");
    }
}
