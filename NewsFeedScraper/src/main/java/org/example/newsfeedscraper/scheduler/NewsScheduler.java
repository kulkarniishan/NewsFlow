package org.example.newsfeedscraper.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.newsfeedscraper.entity.NewsArticle;
import org.example.newsfeedscraper.entity.NewsSourceProperties;
import org.example.newsfeedscraper.kafka.NewsProducer;
import org.example.newsfeedscraper.service.NewsFetcherService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class NewsScheduler {
    private final NewsFetcherService newsFetcherService;
    private final NewsProducer newsProducer;
    private final NewsSourceProperties newsSourceProperties;

    @Scheduled(fixedRateString = "${app.scheduler.rate}")
    public void fetchAndSend() {
        if (newsSourceProperties.getNewsSources() == null || newsSourceProperties.getNewsSources().isEmpty()) {
            log.error("No news sources configured or found.");
            return;
        }

        newsSourceProperties.getNewsSources().parallelStream().forEach(newsSource -> {
            try {
                List<NewsArticle> newsArticles = newsFetcherService.fetchFromRSS(newsSource.getFeedUrl(), newsSource.getId());
                log.info("Fetched {} news articles for source ID: {}", newsArticles.size(), newsSource.getDisplayName());
                newsArticles.forEach(newsProducer::sendNews);
            } catch (Exception e) {
                log.error("Failed to fetch and send news articles for source ID: {}", newsSource.getDisplayName(), e);
            }
        });
    }
}
