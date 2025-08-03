package org.example.newsstorageservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"hash"}))
public class NewsArticle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 1024)
    private String title;
    @Column(length = 1024)
    private String link;
    private String source;
    private Instant publishedAt;
    @Column(columnDefinition = "TEXT")
    private String description;
    private String hash;
}
