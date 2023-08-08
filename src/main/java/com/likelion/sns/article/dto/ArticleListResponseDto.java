package com.likelion.sns.article.dto;

import com.likelion.sns.comment.dto.CommentResponseDto;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class ArticleListResponseDto {
    private Long articleId;
    private Long userId;
    private String username;
    private String title;
    private String mainImage;
    private List<CommentResponseDto> comments;
}
