package com.likelion.sns.article;

import com.likelion.sns.article.dto.ArticleListResponseDto;
import com.likelion.sns.article.dto.ArticleRegisterDto;
import com.likelion.sns.article.dto.ArticleResponseDto;
import com.likelion.sns.article.dto.ArticleUpdateDto;
import com.likelion.sns.comment.CommentRepository;
import com.likelion.sns.comment.dto.CommentResponseDto;
import com.likelion.sns.exception.CustomException;
import com.likelion.sns.exception.CustomExceptionCode;
import com.likelion.sns.user.UserEntity;
import com.likelion.sns.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Service
public class ArticleService {
    private final ArticleRepository articleRepository;
    private final ArticleImageRepository articleImageRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    /**
     * 피드 등록
     */
    public void postArticle(
            ArticleRegisterDto dto,
            List<MultipartFile> images
    ) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<UserEntity> userEntityOptional = userRepository.findByUsername(username);
        if (userEntityOptional.isEmpty()) {
            throw new CustomException(CustomExceptionCode.NOT_FOUND_USER);
        }
        UserEntity user = userEntityOptional.get();
        ArticleEntity article = new ArticleEntity();
        article.setTitle(dto.getTitle());
        article.setContent(dto.getContent());
        article.setDraft(false);
        article.setUser(user);
        ArticleEntity savedArticle = articleRepository.save(article); // 데이터베이스 저장
        log.info("#log# 피드 [{}] 정보 등록 완료", savedArticle.getTitle());
        if (images != null) {
            int index = 1;
            for (MultipartFile image : images) {
                String imageUrl = postArticleImage(image, index);
                ArticleImageEntity imageEntity = new ArticleImageEntity();
                imageEntity.setArticle(savedArticle);
                imageEntity.setImageUrl(imageUrl);
                articleImageRepository.save(imageEntity);
                log.info("#log# 피드 [{}]의 이미지 [{}] 등록 완료", savedArticle.getTitle(), imageUrl);
                index++;
            }
        }
    }

    /**
     * 피드 이미지 등록
     */
    private String postArticleImage(
            MultipartFile image,
            int index
    ) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        String imageDir = String.format("article_images/%s", username);
        try {
            String imageFormat = Files.probeContentType(Paths.get(image.getOriginalFilename()));
            if (!imageFormat.startsWith("image")) {
                log.warn("#log# 사용자 [{}]의 피드 이미지 등록 실패. 지원하지 않는 이미지 파일 형식", username);
                throw new CustomException(CustomExceptionCode.UNSUPPORTED_IMAGE_FORMAT);
            }
            Path dirPath = Paths.get(imageDir);
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }
            String extension = imageFormat.split("/")[1];
            while (Files.exists(dirPath.resolve(String.format("image(%d).%s", index, extension)))) {
                index++;
            }
            String newFilename = String.format("image(%d).%s", index, extension);
            Path targetLocation = dirPath.resolve(newFilename);
            Files.copy(image.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            log.info("#log# 사용자 [{}]의 피드 이미지 [{}] 등록 완료", username, targetLocation);
            return targetLocation.toString();
        } catch (IOException e) {
            log.error("#log# 사용자 [{}]의 피드 이미지 등록 실패", username);
            throw new CustomException(CustomExceptionCode.INTERNAL_ERROR);
        }
    }

    /**
     * 피드 전체 목록 조회
     */
    public List<ArticleListResponseDto> getAllArticles() {
        List<ArticleEntity> articles = articleRepository.findAll();
        if (articles.isEmpty()) {
            throw new CustomException(CustomExceptionCode.NOT_FOUND_ARTICLE);
        }
        return articles.stream().map(article -> {
            String mainImage = article.getArticleImages().isEmpty() ? "defaultImage.png" : article.getArticleImages().get(0).getImageUrl();
            Long userId = article.getUser().getId();
            String username = article.getUser().getUsername();
            List<CommentResponseDto> comments = getCommentsForArticle(article.getId());
            return ArticleListResponseDto.builder()
                    .articleId(article.getId())
                    .userId(userId)
                    .username(username)
                    .title(article.getTitle())
                    .mainImage(mainImage)
                    .comments(comments)
                    .build();
        }).collect(Collectors.toList());
    }

    /**
     * 피드 단일 조회
     */
    public ArticleResponseDto getArticle(Long articleId) {
        Optional<ArticleEntity> OptionalArticle = articleRepository.findByIdAndDeletedAtIsNull(articleId);
        if (OptionalArticle.isEmpty()) {
            throw new CustomException(CustomExceptionCode.NOT_FOUND_ARTICLE);
        }
        ArticleEntity articleEntity = OptionalArticle.get();
        List<String> imageUrls = articleEntity.getArticleImages().stream()
                .map(ArticleImageEntity::getImageUrl)
                .collect(Collectors.toList());
        List<CommentResponseDto> comments = getCommentsForArticle(articleId);
        return ArticleResponseDto.builder()
                .articleId(articleEntity.getId())
                .title(articleEntity.getTitle())
                .content(articleEntity.getContent())
                .username(articleEntity.getUser().getUsername())
                .imageUrls(imageUrls)
                .comments(comments)
                .build();
    }

    /**
     * 피드 댓글 조회
     */
    private List<CommentResponseDto> getCommentsForArticle(Long articleId) {
        return commentRepository.findByArticleIdAndDeletedFalse(articleId).stream()
                .map(comment -> CommentResponseDto.builder()
                        .commentId(comment.getId())
                        .articleId(articleId)
                        .username(comment.getUser().getUsername())
                        .content(comment.getContent())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * 피드 수정
     */
    @Transactional
    public void updateArticle(
            Long articleId,
            ArticleUpdateDto dto,
            String username,
            List<MultipartFile> imagesToAdd
    ) {
        Optional<ArticleEntity> optionalArticleEntity = articleRepository.findByIdAndDeletedAtIsNull(articleId);
        if (optionalArticleEntity.isEmpty()) {
            throw new CustomException(CustomExceptionCode.NOT_FOUND_ARTICLE);
        }
        ArticleEntity articleEntity = optionalArticleEntity.get();
        if (!articleEntity.getUser().getUsername().equals(username)) {
            throw new CustomException(CustomExceptionCode.UNAUTHORIZED_ACCESS);
        }
        articleEntity.setTitle(dto.getTitle());
        articleEntity.setContent(dto.getContent());
        if (dto.getImageIdsToRemove() != null && !dto.getImageIdsToRemove().isEmpty()) {
            removeImages(dto.getImageIdsToRemove(), articleEntity);
        }
        if (imagesToAdd != null && !imagesToAdd.isEmpty()) {
            addImages(imagesToAdd, articleEntity);
        }
    }

    private void removeImages(
            List<Long> imageIdsToRemove,
            ArticleEntity articleEntity
    ) {
        for (Long imageId : imageIdsToRemove) {
            Optional<ArticleImageEntity> optionalImageEntity = articleImageRepository.findById(imageId);
            if (optionalImageEntity.isEmpty()) {
                throw new CustomException(CustomExceptionCode.NOT_FOUND_IMAGE);
            }
            ArticleImageEntity imageEntity = optionalImageEntity.get();
            if (imageEntity.getImageUrl() == null) {
                log.warn("#log# 이미지 아이디 [{}] 경로 없음", imageId);
                continue;
            }
            try {
                Path path = Paths.get(imageEntity.getImageUrl());
                if (Files.exists(path)) {
                    log.info("#log# 이미지 경로 [{}] 확인. 삭제 시도", path);
                    Files.delete(path);
                    log.info("#log# 피드 [{}]의 이미지 [{}] 삭제 완료", articleEntity.getTitle(), imageEntity.getImageUrl());
                } else {
                    log.warn("#log# 피드 [{}]의 이미지 [{}] 정보 없음", articleEntity.getTitle(), imageEntity.getImageUrl());
                }
            } catch (IOException e) {
                log.error("#log# 피드 [{}]의 이미지 [{}] 삭제 실패: {}", articleEntity.getTitle(), imageEntity.getImageUrl(), e.getMessage());
                throw new CustomException(CustomExceptionCode.INTERNAL_ERROR);
            }
            log.info("#log# 데이터베이스에서 이미지 아이디 [{}] 삭제 시도", imageId);
            articleImageRepository.deleteById(imageId);
            log.info("#log# 데이터베이스에서 이미지 아이디 [{}] 삭제 완료", imageId);
        }
    }

    private void addImages(
            List<MultipartFile> imagesToAdd,
            ArticleEntity articleEntity
    ) {
        int lastIndex = getLastImageIndex(articleEntity);
        for (MultipartFile file : imagesToAdd) {
            lastIndex++;
            String imageUrl = saveImage(file, lastIndex);
            ArticleImageEntity imageEntity = new ArticleImageEntity();
            imageEntity.setImageUrl(imageUrl);
            imageEntity.setArticle(articleEntity);
            articleImageRepository.save(imageEntity);
        }
    }

    private int getLastImageIndex(ArticleEntity articleEntity) {
        List<ArticleImageEntity> images = articleImageRepository.findAllByArticle(articleEntity);
        return images.size();
    }

    private String saveImage(
            MultipartFile image,
            int index
    ) {
        return postArticleImage(image, index);
    }

    /**
     * 피드 삭제
     */
    @Transactional
    public void deleteArticle(
            Long articleId,
            String username
    ) {
        Optional<ArticleEntity> optionalArticleEntity = articleRepository.findByIdAndDeletedAtIsNull(articleId);
        if (optionalArticleEntity.isEmpty()) {
            throw new CustomException(CustomExceptionCode.NOT_FOUND_ARTICLE);
        }
        ArticleEntity articleEntity = optionalArticleEntity.get();
        articleEntity.setDeletedAt(LocalDateTime.now());
        log.info("#log# 사용자 [{}]의 피드 아이디 [{}] 정보 삭제 완료. 데이터베이스 존재", username, articleId);
    }
}
