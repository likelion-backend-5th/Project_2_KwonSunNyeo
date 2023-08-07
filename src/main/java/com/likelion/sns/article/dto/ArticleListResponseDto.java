package com.likelion.sns.article.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ArticleListResponseDto {
    private Long articleId;
    private Long userId;
    private String username;
    private String title;
    private String mainImage;
}
