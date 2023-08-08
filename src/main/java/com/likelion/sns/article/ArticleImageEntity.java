package com.likelion.sns.article;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "article_images")
public class ArticleImageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", nullable = false)
    private ArticleEntity article;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;
}
