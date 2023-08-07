package com.likelion.sns.article;

import com.likelion.sns.article.dto.ArticleRegisterDto;
import com.likelion.sns.user.dto.MessageResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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
        service.postArticle(dto, auth.getName(), images);
        log.info("#log# 사용자 [{}]에 의해 피드 [{}] 등록 성공", auth.getName(), dto.getTitle());;
        MessageResponseDto response = new MessageResponseDto();
        response.setMessage("피드 등록이 완료되었습니다.");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
