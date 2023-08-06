package com.likelion.sns.user;

import com.likelion.sns.jwt.TokenUtils;
import com.likelion.sns.user.dto.MessageResponseDto;
import com.likelion.sns.user.dto.TokenResponseDto;
import com.likelion.sns.user.dto.UserLoginDto;
import com.likelion.sns.user.dto.UserRegisterDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@RequiredArgsConstructor
@Slf4j
@RequestMapping("/users")
@RestController
public class UserController {
    private final UserService service;
    private final CustomUserDetailsManager manager;
    private final PasswordEncoder passwordEncoder;
    private final TokenUtils tokenUtils;

    /**
     * POST /register
     * 회원가입
     */
    @PostMapping("/register")
    public ResponseEntity<MessageResponseDto> register(
            @Valid @RequestBody UserRegisterDto dto
    ) {
        log.info("#log# 사용자 [{}] 등록 요청 받음", dto.getUsername());
        if (!dto.getPassword().equals(dto.getPasswordCheck())) {
            log.warn("#log# 사용자 [{}] 등록 실패. 비밀번호 불일치", dto.getUsername());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "비밀번호가 일치하지 않습니다.");
        }
        service.register(dto);
        log.info("#log# 사용자 [{}] 등록 성공", dto.getUsername());
        MessageResponseDto response = new MessageResponseDto();
        response.setMessage("회원가입이 완료되었습니다.");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * POST /login
     * 로그인
     */
    @PostMapping("/login")
    public ResponseEntity<TokenResponseDto> login(
            @Valid @RequestBody UserLoginDto dto
    ) {
        log.info("#log# 사용자 [{}] 로그인 요청 받음", dto.getUsername());
        UserDetails userDetails = manager.loadUserByUsername(dto.getUsername());
        if (!passwordEncoder.matches(dto.getPassword(), userDetails.getPassword())) {
            log.warn("#log# 사용자 [{}] 로그인 실패. 비밀번호 불일치", dto.getUsername());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "비밀번호가 일치하지 않습니다.");
        }
        log.info("#log# 사용자 [{}] 로그인 성공", dto.getUsername());
        TokenResponseDto response = new TokenResponseDto();
        response.setMessage("로그인이 완료되었습니다.");
        response.setToken(tokenUtils.generateToken(userDetails));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * PUT /{username}/profile-image
     * 프로필 이미지 업데이트
     */
    @PutMapping(
            value = "/{username}/profile-image",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<MessageResponseDto> updateProfileImage(
            @PathVariable("username") String username,
            @RequestParam("image") MultipartFile profileImage
    ) {
        log.info("#log# 사용자 [{}] 프로필 이미지 업데이트 요청 받음", username);
        manager.updateProfileImage(username, profileImage);
        log.info("#log# 사용자 [{}] 프로필 이미지 업데이트 성공", username);
        MessageResponseDto response = new MessageResponseDto();
        response.setMessage("프로필 이미지 업데이트가 완료되었습니다.");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
