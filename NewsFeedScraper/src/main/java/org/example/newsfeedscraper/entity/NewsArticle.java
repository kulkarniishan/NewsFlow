package org.example.newsfeedscraper.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewsArticle {
    private String title;
    private String link;
    private String publishedAt;
    private String sourceId;
    private String description;
}
