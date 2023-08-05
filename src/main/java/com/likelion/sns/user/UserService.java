package com.likelion.sns.user;

import com.likelion.sns.user.dto.UserRegisterDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {
    private final CustomUserDetailsManager manager;
    private final PasswordEncoder passwordEncoder;

    /**
     * 회원가입
     */
    public void register(UserRegisterDto dto) {
        CustomUserDetails user = CustomUserDetails.builder()
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .build();
        manager.createUser(user); // 데이터베이스 저장
    }
}
