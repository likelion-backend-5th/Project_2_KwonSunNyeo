package com.likelion.sns.article.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class ArticleUpdateDto {
    private String title;
    private String content;
    private List<MultipartFile> imagesToAdd;
    private List<Long> imageIdsToRemove;
}
