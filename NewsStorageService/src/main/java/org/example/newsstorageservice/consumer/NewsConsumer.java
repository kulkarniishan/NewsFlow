package org.example.newsstorageservice.consumer;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.example.newsstorageservice.dto.NewsArticleDto;
import org.example.newsstorageservice.entity.NewsArticle;
import org.example.newsstorageservice.repository.NewsArticleRepository;
import org.example.newsstorageservice.util.HashUtil;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
public class NewsConsumer {
    private final NewsArticleRepository newsArticleRepository;
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @KafkaListener(topics = "${app.kafka.topic}")
    public void consume(String message) {
        try {
            NewsArticleDto newsArticleDto = objectMapper.readValue(message, NewsArticleDto.class);
            String hash = HashUtil.sha256(newsArticleDto.getTitle() + newsArticleDto.getSourceId());

            if (!newsArticleRepository.existsByHash(hash)) {
                NewsArticle newsArticle = NewsArticle.builder()
                        .title(newsArticleDto.getTitle())
                        .link(newsArticleDto.getLink())
                        .source(newsArticleDto.getSourceId())
                        .publishedAt(newsArticleDto.getPublishedAtAsInstant())
                        .description(newsArticleDto.getDescription())
                        .hash(hash)
                        .build();
                newsArticleRepository.save(newsArticle);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
