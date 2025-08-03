package org.example.newsfeedscraper.service;

import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import org.example.newsfeedscraper.entity.NewsArticle;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.net.URI;
import java.util.List;

@Service
public class NewsFetcherService {
    public List<NewsArticle> fetchFromRSS(String feedUrl, String sourceId) throws Exception {
        if (feedUrl == null || feedUrl.trim().isEmpty()) {
            throw new IllegalArgumentException("Feed URL cannot be null or empty");
        }

        SyndFeedInput input = new SyndFeedInput();
        InputStream inputStream = URI.create(feedUrl).toURL().openStream();
        SyndFeed feed = input.build(new XmlReader(inputStream));

        return feed.getEntries().stream().map(entry ->
                new NewsArticle(entry.getTitle(),
                        entry.getLink(),
                        entry.getPublishedDate().toString(),
                        sourceId,
                        entry.getDescription().getValue()
                )).toList();
    }
}
