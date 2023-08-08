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
     * 피드 좋아요 추가
     */
    @PostMapping
    public ResponseEntity<MessageResponseDto> likeArticle(
            @PathVariable Long articleId,
            Authentication auth
    ) {
        log.info("#log# 사용자 [{}]에 의해 피드 아이디 [{}]에 좋아요 추가 요청 받음", auth.getName(), articleId);
        service.likeArticle(articleId);
        log.info("#log# 사용자 [{}]에 의해 피드 아이디 [{}]에 좋아요 추가 성공", auth.getName(), articleId);
        MessageResponseDto response = new MessageResponseDto();
        response.setMessage("피드 좋아요가 완료되었습니다.");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * DELETE
     * 피드 좋아요 취소
     */
    @DeleteMapping
    public ResponseEntity<MessageResponseDto> unlikeArticle(
            @PathVariable Long articleId,
            Authentication auth
    ) {
        log.info("#log# 사용자 [{}]에 의해 피드 아이디 [{}] 좋아요 취소 요청 받음", auth.getName(), articleId);
        service.unlikeArticle(articleId);
        log.info("#log# 사용자 아이디 [{}]에 의해 피드 아이디 [{}] 좋아요 취소 성공", auth.getName(), articleId);
        MessageResponseDto response = new MessageResponseDto();
        response.setMessage("피드 좋아요 취소가 완료되었습니다.");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
