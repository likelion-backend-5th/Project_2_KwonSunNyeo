package com.likelion.sns.user.dto;

import lombok.Data;

@Data
public class TokenResponseDto extends MessageResponseDto {
    private String token;
}
