package com.likelion.sns.user;

import com.likelion.sns.user.dto.MessageResponseDto;
import com.likelion.sns.user.dto.UserRegisterDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RequiredArgsConstructor
@Slf4j
@RequestMapping("/users")
@RestController
public class UserController {
    private final UserService service;

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
}
