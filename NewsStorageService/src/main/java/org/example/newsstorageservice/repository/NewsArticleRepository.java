package org.example.newsstorageservice.repository;

import org.example.newsstorageservice.entity.NewsArticle;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsArticleRepository extends JpaRepository<NewsArticle, Long> {
    boolean existsByHash(String hash);
}
