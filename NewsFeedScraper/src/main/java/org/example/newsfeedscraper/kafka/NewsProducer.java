package org.example.newsfeedscraper.kafka;

import lombok.RequiredArgsConstructor;
import org.example.newsfeedscraper.entity.NewsArticle;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NewsProducer {
    private final KafkaTemplate<String, NewsArticle> kafkaTemplate;

    @Value("${app.kafka.topic}")
    private String topic;

    public void sendNews(NewsArticle newsArticle) {
        kafkaTemplate.send(topic, newsArticle);
    }
}
