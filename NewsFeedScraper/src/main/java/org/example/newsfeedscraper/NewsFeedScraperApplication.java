package org.example.newsfeedscraper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class NewsFeedScraperApplication {

    public static void main(String[] args) {
        SpringApplication.run(NewsFeedScraperApplication.class, args);
    }

}
