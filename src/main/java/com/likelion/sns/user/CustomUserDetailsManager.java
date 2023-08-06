package com.likelion.sns.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Service
public class CustomUserDetailsManager implements UserDetailsManager {
    private final UserRepository userRepository;

    @Override
    public void createUser(UserDetails user) {
        log.info("#log# 사용자 [{}] 등록 시도", user.getUsername());
        if (this.userExists(user.getUsername())) {
            log.warn("#log# 사용자 [{}] 등록 실패. 사용자 이름 중복", user.getUsername());
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 존재하는 사용자 이름입니다.");
        }
        try {
            this.userRepository.save(((CustomUserDetails) user).newEntity());
        } catch (ClassCastException e) {
            log.error("#log# 사용자 [{}] 등록 실패", CustomUserDetails.class);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "서버에서 문제가 발생했습니다.");
        }
    }

    @Override
    public boolean userExists(String username) {
        log.info("#log# 사용자 [{}] 존재 여부 확인", username);
        return this.userRepository.existsByUsername(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("#log# 사용자 [{}] 정보 조회 시도", username);
        Optional<UserEntity> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isEmpty()) {
            log.warn("#log# 사용자 [{}] 정보 없음", username);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 사용자를 찾을 수 없습니다.");
        }
        return CustomUserDetails.fromEntity(optionalUser.get());
    }

    @Override
    public void updateUser(UserDetails user) {
        log.info("#log# 사용자 [{}] 정보 업데이트 시도", user.getUsername());
    }

    public void updateProfileImage(String username, MultipartFile image) {
        log.info("#log# 사용자 [{}] 프로필 이미지 업데이트 시도", username);
        if (image == null || image.isEmpty()) {
            log.warn("#log# 사용자 [{}] 프로필 이미지 없음", username);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "프로필 이미지를 찾을 수 없습니다.");
        }
        String imageDir = String.format("user_images/%s", username);
        try {
            String imageFormat = Files.probeContentType(Paths.get(image.getOriginalFilename()));
            if (!imageFormat.startsWith("image")) {
                log.warn("#log# 사용자 [{}] 프로필 이미지 업데이트 실패. 지원하지 않는 이미지 파일 형식", username);
                throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "지원하지 않는 이미지 파일 형식입니다.");
            }
            Path dirPath = Paths.get(imageDir);
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }
            String originalFilename = image.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String newFilename = "profile" + extension;
            Path targetLocation = dirPath.resolve(newFilename);
            Files.copy(image.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            log.error("#log# 사용자 [{}] 프로필 이미지 업데이트 실패", username);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "서버에서 문제가 발생했습니다.");
        }
    }

    @Override
    public void deleteUser(String username) {
        log.info("#log# 사용자 [{}] 정보 삭제 시도", username);
    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {
        log.info("#log# 사용자 비밀번호 변경 시도");
    }
}
