package com.likelion.sns.article;

import com.likelion.sns.article.dto.ArticleListResponseDto;
import com.likelion.sns.article.dto.ArticleRegisterDto;
import com.likelion.sns.article.dto.ArticleResponseDto;
import com.likelion.sns.article.dto.ArticleUpdateDto;
import com.likelion.sns.user.dto.MessageResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@RequestMapping("/articles")
@RestController
public class ArticleController {
    private final ArticleService service;

    /**
     * POST /{username}
     * 피드 등록
     */
    @PostMapping("{username}")
    public ResponseEntity<MessageResponseDto> postArticle(
            @Valid ArticleRegisterDto dto,
            @RequestParam(required = false) List<MultipartFile> images,
            Authentication auth
    ) {
        log.info("#log# 사용자 [{}]에 의해 피드 [{}] 등록 요청 받음", auth.getName(), dto.getTitle());
        service.postArticle(dto, images);
        log.info("#log# 사용자 [{}]에 의해 피드 [{}] 등록 성공", auth.getName(), dto.getTitle());;
        MessageResponseDto response = new MessageResponseDto();
        response.setMessage("피드 등록이 완료되었습니다.");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * GET
     * 피드 전체 목록 조회
     */
    @GetMapping
    public ResponseEntity<List<ArticleListResponseDto>> getAllArticles() {
        List<ArticleListResponseDto> articles = service.getAllArticles();
        return new ResponseEntity<>(articles, HttpStatus.OK);
    }

    /**
     * GET /{username}/{articleId}
     * 피드 단일 조회
     */
    @GetMapping("{username}/{articleId}")
    public ResponseEntity<ArticleResponseDto> getArticle(
            @PathVariable Long articleId
    ) {
        ArticleResponseDto article = service.getArticle(articleId);
        return new ResponseEntity<>(article, HttpStatus.OK);
    }

    /**
     * PUT /{username}/{articleId}
     * 피드 수정
     */
    @PutMapping(value = "{username}/{articleId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MessageResponseDto> updateArticle(
            @PathVariable Long articleId,
            @Valid @ModelAttribute ArticleUpdateDto dto,
            @RequestParam(value = "imagesToAdd", required = false)
            List<MultipartFile> imagesToAdd,
            Authentication auth
    ) {
        log.info("#log# 사용자 [{}]에 의해 피드 아이디 [{}] 정보 수정 요청 받음", auth.getName(), articleId);
        service.updateArticle(articleId, dto, auth.getName(), imagesToAdd);
        log.info("#log# 사용자 [{}]에 의해 피드 아이디 [{}] 정보 수정 성공", auth.getName(), articleId);
        MessageResponseDto response = new MessageResponseDto();
        response.setMessage("피드 수정이 완료되었습니다.");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * DELETE /{username}/{articleId}
     * 피드 삭제
     */
    @DeleteMapping("/{username}/{articleId}")
    public ResponseEntity<MessageResponseDto> deleteArticle(
            @PathVariable Long articleId,
            Authentication auth
    ) {
        log.info("#log# 사용자 [{}]에 의해 피드 아이디 [{}] 정보 삭제 요청 받음", auth.getName(), articleId);
        service.deleteArticle(articleId, auth.getName());
        log.info("#log# 사용자 [{}]에 의해 피드 아이디 [{}] 정보 삭제 성공", auth.getName(), articleId);
        MessageResponseDto response = new MessageResponseDto();
        response.setMessage("피드 삭제가 완료되었습니다.");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
