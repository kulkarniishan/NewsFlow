package org.example.newsfeedscraper.entity;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "app.news")
@Data
public class NewsSourceProperties {
    private List<NewsSource> newsSources = new ArrayList<>();

    @Data
    public static class NewsSource {
        private String id;
        private String displayName;
        private String feedUrl;
    }

}
