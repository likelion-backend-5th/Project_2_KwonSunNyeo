package com.likelion.sns.article.dto;

import com.likelion.sns.comment.dto.CommentResponseDto;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class ArticleResponseDto {
    private Long articleId;
    private String title;
    private String content;
    private String username;
    private List<String> imageUrls;
    private List<CommentResponseDto> comments;
}
