package com.likelion.sns.comment;

import com.likelion.sns.article.ArticleEntity;
import com.likelion.sns.article.ArticleRepository;
import com.likelion.sns.comment.dto.CommentRequestDto;
import com.likelion.sns.exception.CustomException;
import com.likelion.sns.exception.CustomExceptionCode;
import com.likelion.sns.user.UserEntity;
import com.likelion.sns.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final ArticleRepository articleRepository;

    /**
     * 댓글 등록
     */
    public void postComment(
            Long articleId,
            CommentRequestDto dto
    ) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<UserEntity> userEntityOptional = userRepository.findByUsername(username);
        if (userEntityOptional.isEmpty()) {
            throw new CustomException(CustomExceptionCode.NOT_FOUND_USER);
        }
        UserEntity user = userEntityOptional.get();
        Optional<ArticleEntity> articleEntityOptional = articleRepository.findById(articleId);
        if (articleEntityOptional.isEmpty()) {
            throw new CustomException(CustomExceptionCode.NOT_FOUND_ARTICLE);
        }
        ArticleEntity article = articleEntityOptional.get();
        if (article.getDeletedAt() != null) {
            throw new CustomException(CustomExceptionCode.NOT_FOUND_ARTICLE);
        }
        CommentEntity comment = new CommentEntity();
        comment.setContent(dto.getContent());
        comment.setUser(user);
        comment.setArticle(article);
        CommentEntity savedComment = commentRepository.save(comment); // 데이터베이스 저장
        log.info("#log# 피드 [{}]의 댓글 아이디 [{}] 등록 완료", article.getTitle(), savedComment.getId());
    }

    /**
     * 댓글 수정
     */
    @Transactional
    public void updateComment(
            Long commentId,
            String content,
            String username
    ) {
        Optional<CommentEntity> optionalCommentEntity = commentRepository.findById(commentId);
        if (optionalCommentEntity.isEmpty()) {
            throw new CustomException(CustomExceptionCode.NOT_FOUND_COMMENT);
        }
        CommentEntity commentEntity = optionalCommentEntity.get();
        if (commentEntity.getArticle().getDeletedAt() != null) {
            throw new CustomException(CustomExceptionCode.NOT_FOUND_ARTICLE);
        }
        if (!commentEntity.getUser().getUsername().equals(username)) {
            throw new CustomException(CustomExceptionCode.UNAUTHORIZED_ACCESS);
        }
        commentEntity.setContent(content);
        log.info("사용자 [{}]의 피드 댓글 아이디 [{}] 수정 완료", username, commentId);
    }

    /**
     * 댓글 삭제
     */
    @Transactional
    public void deleteComment(
            Long commentId,
            String username
    ) {
        Optional<CommentEntity> optionalCommentEntity = commentRepository.findById(commentId);
        if (optionalCommentEntity.isEmpty()) {
            throw new CustomException(CustomExceptionCode.NOT_FOUND_COMMENT);
        }
        CommentEntity commentEntity = optionalCommentEntity.get();
        if (commentEntity.getArticle().getDeletedAt() != null) {
            throw new CustomException(CustomExceptionCode.NOT_FOUND_ARTICLE);
        }
        if (!commentEntity.getUser().getUsername().equals(username)) {
            throw new CustomException(CustomExceptionCode.UNAUTHORIZED_ACCESS);
        }
        commentEntity.setDeletedAt(LocalDateTime.now());
        log.info("#log# 사용자 [{}]의 피드 댓글 아이디 [{}] 정보 삭제 완료. 데이터베이스 존재", username, commentId);
    }
}
