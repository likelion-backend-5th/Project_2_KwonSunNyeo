package com.likelion.sns.article.like;

import com.likelion.sns.user.dto.MessageResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Slf4j
@RequestMapping("/articles/{username}/{articleId}/likes")
@RestController
public class ArticleLikeController {
    private final ArticleLikeService service;

    /**
     * POST
     * 피드 좋아요 추가/취소
     */
    @PostMapping
    public ResponseEntity<MessageResponseDto> toggleLikeArticle(
            @PathVariable Long articleId,
            Authentication auth
    ) {
        log.info("#log# 사용자 [{}]에 의해 피드 아이디 [{}]에 좋아요 토글 요청 받음", auth.getName(), articleId);
        boolean liked = service.likeArticle(articleId);
        MessageResponseDto response = new MessageResponseDto();
        if (liked) {
            response.setMessage("피드 좋아요가 완료되었습니다.");
        } else {
            response.setMessage("피드 좋아요 취소가 완료되었습니다.");
        }
        log.info("#log# 사용자 [{}]에 의해 피드 아이디 [{}]에 좋아요 토글 성공", auth.getName(), articleId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
