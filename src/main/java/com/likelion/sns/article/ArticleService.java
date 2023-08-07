package com.likelion.sns.article;

import com.likelion.sns.article.dto.ArticleListResponseDto;
import com.likelion.sns.article.dto.ArticleRegisterDto;
import com.likelion.sns.article.dto.ArticleResponseDto;
import com.likelion.sns.article.dto.ArticleUpdateDto;
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

    /**
     * 피드 등록
     */
    public void postArticle(
            ArticleRegisterDto dto,
            String username,
            List<MultipartFile> images
    ) {
        UserEntity user = userRepository.findByUsername(username).get();
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
                String imageUrl = postArticleImage(username, image, index);
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
            String username,
            MultipartFile image,
            int index
    ) {
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
            Long articleId = article.getId();
            Long userId = article.getUser().getId();
            String username = article.getUser().getUsername();
            return new ArticleListResponseDto(
                    articleId, userId, username, article.getTitle(), mainImage);
        }).collect(Collectors.toList());
    }

    /**
     * 피드 단일 조회
     */
    public ArticleResponseDto getArticle(Long id) {
        Optional<ArticleEntity> OptionalArticle = articleRepository.findByIdAndDeletedAtIsNull(id);
        if (OptionalArticle.isEmpty()) {
            throw new CustomException(CustomExceptionCode.NOT_FOUND_ARTICLE);
        }
        ArticleEntity articleEntity = OptionalArticle.get();
        List<String> imageUrls = articleEntity.getArticleImages().stream()
                .map(ArticleImageEntity::getImageUrl)
                .collect(Collectors.toList());
        return ArticleResponseDto.builder()
                .id(articleEntity.getId())
                .title(articleEntity.getTitle())
                .content(articleEntity.getContent())
                .username(articleEntity.getUser().getUsername())
                .imageUrls(imageUrls)
                .build();
    }

    /**
     * 피드 수정
     */
    @Transactional
    public void updateArticle(
            Long id,
            ArticleUpdateDto dto,
            String username,
            List<MultipartFile> imagesToAdd
    ) {
        Optional<ArticleEntity> optionalArticleEntity = articleRepository.findByIdAndDeletedAtIsNull(id);
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
        if (dto.getImagesToAdd() != null && !dto.getImagesToAdd().isEmpty()) {
            addImages(dto.getImagesToAdd(), articleEntity);
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
                log.warn("#log# 이미지 [{}] 경로 없음", imageId);
                continue;
            }
            try {
                Path path = Paths.get(imageEntity.getImageUrl());
                if (Files.exists(path)) {
                    log.info("#log# 파일 경로 [{}] 확인. 삭제 시도", path);
                    Files.delete(path);
                    log.info("#log# 피드 [{}]의 이미지 아이디 [{}] 삭제 완료", articleEntity.getTitle(), imageEntity.getImageUrl());
                } else {
                    log.warn("#log# 피드 [{}]의 이미지 아이디 [{}] 정보 없음", articleEntity.getTitle(), imageEntity.getImageUrl());
                }
            } catch (IOException e) {
                log.error("#log# 피드 [{}]의 이미지 [{}] 삭제 실패: {}", articleEntity.getTitle(), imageEntity.getImageUrl(), e.getMessage());
                throw new CustomException(CustomExceptionCode.INTERNAL_ERROR);
            }
            log.info("#log# 데이터베이스에서 이미지 [{}] 삭제 시도", imageId);
            articleImageRepository.deleteById(imageId);
            log.info("#log# 데이터베이스에서 이미지 [{}] 삭제 완료", imageId);
        }
    }

    private void addImages(
            List<MultipartFile> imagesToAdd,
            ArticleEntity articleEntity
    ) {
        for (MultipartFile file : imagesToAdd) {
            String imageUrl = saveImage(file);
            ArticleImageEntity imageEntity = new ArticleImageEntity();
            imageEntity.setImageUrl(imageUrl);
            imageEntity.setArticle(articleEntity);
            articleImageRepository.save(imageEntity);
        }
    }

    private String saveImage(
            MultipartFile image
    ) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return postArticleImage(username, image, 1);
    }
}
