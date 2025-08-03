package org.example.newsstorageservice.dto;

import lombok.Data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Locale;

@Data
public class NewsArticleDto {
    private String title;
    private String link;
    private String sourceId;
    private String publishedAt;
    private String description;

    public Instant getPublishedAtAsInstant() {
        try {
            return new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH)
                    .parse(publishedAt)
                    .toInstant();
        } catch (ParseException e) {
            return Instant.now(); // or null/fallback
        }
    }
}