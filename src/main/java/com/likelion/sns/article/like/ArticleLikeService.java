package com.likelion.sns.article.like;

import com.likelion.sns.article.ArticleEntity;
import com.likelion.sns.article.ArticleRepository;
import com.likelion.sns.exception.CustomException;
import com.likelion.sns.exception.CustomExceptionCode;
import com.likelion.sns.user.UserEntity;
import com.likelion.sns.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Service
public class ArticleLikeService {
    private final ArticleLikeRepository articleLikeRepository;
    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;

    /**
     * 피드 좋아요 추가
     */
    @Transactional
    public void likeArticle(
            Long articleId
    ) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<UserEntity> optionalUserEntity = userRepository.findByUsername(username);
        if (optionalUserEntity.isEmpty()) {
            throw new CustomException(CustomExceptionCode.NOT_FOUND_USER);
        }
        UserEntity userEntity = optionalUserEntity.get();
        Optional<ArticleEntity> optionalArticleEntity = articleRepository.findByIdAndDeletedAtIsNull(articleId);
        if (optionalArticleEntity.isEmpty()) {
            throw new CustomException(CustomExceptionCode.NOT_FOUND_ARTICLE);
        }
        ArticleEntity articleEntity = optionalArticleEntity.get();
        Optional<ArticleLikeEntity> optionalArticleLikeEntity = articleLikeRepository.findByUserIdAndArticleId(userEntity.getId(), articleEntity.getId());
        if (optionalArticleLikeEntity.isPresent()) {
            throw new CustomException(CustomExceptionCode.ALREADY_LIKED);
        }
        ArticleLikeEntity articleLikeEntity = new ArticleLikeEntity();
        articleLikeEntity.setUser(userEntity);
        articleLikeEntity.setArticle(articleEntity);
        articleLikeRepository.save(articleLikeEntity);
        log.info("#log# 사용자 [{}]가 피드 [{}]에 좋아요 추가 완료", username, articleEntity.getTitle());
    }

    /**
     * 피드 좋아요 취소
     */
    @Transactional
    public void unlikeArticle(
            Long articleId
    ) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<UserEntity> optionalUserEntity = userRepository.findByUsername(username);
        if (optionalUserEntity.isEmpty()) {
            throw new CustomException(CustomExceptionCode.NOT_FOUND_USER);
        }
        UserEntity userEntity = optionalUserEntity.get();
        Optional<ArticleEntity> optionalArticleEntity = articleRepository.findByIdAndDeletedAtIsNull(articleId);
        if (optionalArticleEntity.isEmpty()) {
            throw new CustomException(CustomExceptionCode.NOT_FOUND_ARTICLE);
        }
        ArticleEntity articleEntity = optionalArticleEntity.get();
        Optional<ArticleLikeEntity> optionalArticleLikeEntity = articleLikeRepository.findByUserIdAndArticleId(userEntity.getId(), articleEntity.getId());
        if (optionalArticleLikeEntity.isEmpty()) {
            throw new CustomException(CustomExceptionCode.NOT_LIKED_YET);
        }
        articleLikeRepository.delete(optionalArticleLikeEntity.get());
        log.info("#log# 사용자 [{}]가 피드 [{}]에 좋아요 취소 완료", username, articleEntity.getTitle());
    }

    /**
     * 피드 좋아요 개수 조회
     */
    public Long countLikesForArticle(
            Long articleId
    ) {
        Long likeCount = articleLikeRepository.countByArticleId(articleId);
        log.info("#log# 피드 아이디 [{}]의 좋아요 개수 [{}] 조회 성공", articleId, likeCount);
        return likeCount;
    }
}
