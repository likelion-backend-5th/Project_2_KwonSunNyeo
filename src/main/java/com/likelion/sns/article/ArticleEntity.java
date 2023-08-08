package com.likelion.sns.article;

import com.likelion.sns.user.UserEntity;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "articles")
public class ArticleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Lob // Large Object, 큰 사이즈의 데이터를 다룰 때 사용
    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private Boolean draft = false;

    private LocalDateTime deletedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @OneToMany(mappedBy = "article")
    private List<ArticleImageEntity> articleImages;
}
