package com.likelion.sns.config;

import com.likelion.sns.jwt.TokenExceptionFilter;
import com.likelion.sns.jwt.TokenFilter;
import com.likelion.sns.jwt.TokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
@Configuration
public class WebSecurityConfig {
    private final TokenUtils tokenUtils;

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET,"/articles")
                        .permitAll() // 모든 사용자 접근 가능
                        .requestMatchers("/users/register", "/users/login")
                        .permitAll() // 모든 사용자 접근 가능
                        .anyRequest().authenticated() // 그 외 요청은 인증 필요
                )
                .sessionManagement(
                        sessionManagement -> sessionManagement
                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                /*
                 * 토큰 필터 추가
                 * UsernamePasswordAuthenticationFilter 이전에 실행되도록 설정
                 */
                .addFilterBefore(
                        new TokenFilter(tokenUtils),
                        UsernamePasswordAuthenticationFilter.class
                )
                /*
                 * 토큰 예외 처리 필터 추가
                 * TokenFilter 이전에 실행되도록 설정
                 */
                .addFilterBefore(
                        new TokenExceptionFilter(),
                        TokenFilter.class
                );
        return http.build();
    }
}
