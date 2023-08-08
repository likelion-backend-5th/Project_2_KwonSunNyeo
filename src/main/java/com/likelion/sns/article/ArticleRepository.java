package com.likelion.sns.article;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ArticleRepository extends JpaRepository<ArticleEntity, Long> {
    Optional<ArticleEntity> findByIdAndDeletedAtIsNull(Long id);
}
