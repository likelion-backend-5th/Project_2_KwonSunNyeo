package com.likelion.sns.comment.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CommentResponseDto {
    private Long commentId;
    private Long articleId;
    private String username;
    private String content;
}
