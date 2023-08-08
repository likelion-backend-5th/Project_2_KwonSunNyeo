package com.likelion.sns.comment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CommentRequestDto {
    @NotBlank(message = "내용은 필수입니다.")
    private String content;
}
