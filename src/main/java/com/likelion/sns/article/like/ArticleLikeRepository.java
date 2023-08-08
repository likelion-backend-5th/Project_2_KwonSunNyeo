package com.likelion.sns.article.like;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ArticleLikeRepository extends JpaRepository<ArticleLikeEntity, Long> {
    Optional<ArticleLikeEntity> findByUserIdAndArticleId(Long userId, Long articleId);
    Long countByArticleId(Long articleId);
}
