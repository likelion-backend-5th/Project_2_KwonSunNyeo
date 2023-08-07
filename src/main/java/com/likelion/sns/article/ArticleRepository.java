package com.likelion.sns.article;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleRepository extends JpaRepository<ArticleEntity, Long> {
}
