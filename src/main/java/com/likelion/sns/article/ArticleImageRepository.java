package com.likelion.sns.article;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArticleImageRepository extends JpaRepository<ArticleImageEntity, Long> {
    List<ArticleImageEntity> findAllByArticle(ArticleEntity articleEntity);
}
