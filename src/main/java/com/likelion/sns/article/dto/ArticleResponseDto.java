package com.likelion.sns.article.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ArticleResponseDto {
    private Long id;
    private String title;
    private String content;
    private String username;
    private List<String> imageUrls;
}
