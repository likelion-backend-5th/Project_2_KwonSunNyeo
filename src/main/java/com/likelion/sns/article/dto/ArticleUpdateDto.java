package com.likelion.sns.article.dto;

import lombok.Data;

import java.util.List;

@Data
public class ArticleUpdateDto {
    private String title;
    private String content;
    private List<Long> imageIdsToRemove;
}
