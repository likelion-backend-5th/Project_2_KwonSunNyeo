package com.likelion.sns.jwt;

import com.likelion.sns.user.CustomUserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.ArrayList;

@RequiredArgsConstructor
@Slf4j
@Component
public class TokenFilter extends OncePerRequestFilter {
    private final TokenUtils tokenUtils;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        String requestURI = request.getRequestURI();
        if ("/users/register".equals(requestURI) || "/users/login".equals(requestURI)) {
            log.warn("#log# /users/register 또는 /users/login 접근");
            filterChain.doFilter(request, response);
            return;
        }
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("#log# JWT 정보 없음");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "JWT는 필수입니다.");
        } else {
            String token = authHeader.split(" ")[1];
            if (!tokenUtils.isValidatedToken(token)) {
                if (tokenUtils.isExpiredToken(token)) {
                    log.warn("#log# 인증 실패. 만료된 JWT");
                    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "만료된 JWT입니다.");
                } else {
                    log.warn("#log# 인증 실패. 유효하지 않은 JWT");
                    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "유효하지 않은 JWT입니다.");
                }
            } else {
                String username = tokenUtils.parseClaims(token).getSubject();
                SecurityContext context = SecurityContextHolder.createEmptyContext();
                log.info("#log# 사용자 [{}] 인증 성공. 유효한 JWT", username);
                AbstractAuthenticationToken authenticationToken
                        = new UsernamePasswordAuthenticationToken(
                        CustomUserDetails.builder()
                                .username(username)
                                .build(), token, new ArrayList<>()
                );
                context.setAuthentication(authenticationToken);
                SecurityContextHolder.setContext(context);
            }
            filterChain.doFilter(request, response);
        }
    }
}
