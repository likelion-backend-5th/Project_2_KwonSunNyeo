package com.likelion.sns.article.dto;

import com.likelion.sns.comment.dto.CommentResponseDto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class ArticleListResponseDto {
    private Long articleId;
    private Long userId;
    private String username;
    private String title;
    private String mainImage;
    private List<CommentResponseDto> comments;
}
