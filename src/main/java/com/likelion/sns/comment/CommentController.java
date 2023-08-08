package com.likelion.sns.comment;

import com.likelion.sns.comment.dto.CommentRequestDto;
import com.likelion.sns.exception.CustomException;
import com.likelion.sns.exception.CustomExceptionCode;
import com.likelion.sns.user.dto.MessageResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Slf4j
@RequestMapping("/articles/{username}/{articleId}/comments")
@RestController
public class CommentController {
    private final CommentService service;

    /**
     * POST
     * 댓글 등록
     */
    @PostMapping
    public ResponseEntity<MessageResponseDto> postComment(
            @PathVariable Long articleId,
            @Valid @RequestBody CommentRequestDto dto,
            Authentication auth
    ) {
        log.info("#log# 사용자 [{}]에 의해 피드 아이디 [{}]에 댓글 등록 요청 받음", auth.getName(), articleId);
        service.postComment(articleId, dto);
        log.info("#log# 사용자 [{}]에 의해 피드 아이디 [{}]에 댓글 등록 성공", auth.getName(), articleId);
        MessageResponseDto response = new MessageResponseDto();
        response.setMessage("댓글 등록이 완료되었습니다.");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * PUT /{commentId}
     * 댓글 수정
     */
    @PutMapping("/{commentId}")
    public ResponseEntity<MessageResponseDto> updateComment(
            @PathVariable Long commentId,
            @RequestBody CommentRequestDto dto,
            Authentication auth
    ) {
        log.info("#log# 사용자 [{}]에 의해 댓글 아이디 [{}] 수정 요청 받음", auth.getName(), commentId);
        try {
            service.updateComment(commentId, dto.getContent(), auth.getName());
        } catch (CustomException e) {
            if (e.getExceptionCode() == CustomExceptionCode.UNAUTHORIZED_ACCESS) {
                log.warn("#log# 사용자 [{}] 및 댓글의 작성자 불일치", auth.getName());
                throw e;
            }
        }
        log.info("#log# 사용자 [{}]에 의해 댓글 아이디 [{}] 수정 성공", auth.getName(), commentId);
        MessageResponseDto response = new MessageResponseDto();
        response.setMessage("댓글 수정이 완료되었습니다.");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * DELETE /{commentId}
     * 댓글 삭제
     */
    @DeleteMapping("/{commentId}")
    public ResponseEntity<MessageResponseDto> deleteComment(
            @PathVariable Long commentId,
            Authentication auth
    ) {
        log.info("#log# 사용자 [{}]에 의해 댓글 아이디 [{}] 삭제 요청 받음", auth.getName(), commentId);
        try {
            service.deleteComment(commentId, auth.getName());
        } catch (CustomException e) {
            if (e.getExceptionCode() == CustomExceptionCode.UNAUTHORIZED_ACCESS) {
                log.warn("#log# 사용자 [{}] 및 댓글의 작성자 불일치", auth.getName());
                throw e;
            }
        }
        log.info("#log# 사용자 [{}]에 의해 댓글 아이디 [{}] 삭제 성공", auth.getName(), commentId);
        MessageResponseDto response = new MessageResponseDto();
        response.setMessage("댓글 삭제가 완료되었습니다.");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
