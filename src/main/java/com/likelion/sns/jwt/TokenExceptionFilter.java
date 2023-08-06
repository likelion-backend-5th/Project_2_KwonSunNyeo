package com.likelion.sns.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.likelion.sns.exception.CustomException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class TokenExceptionFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            log.info("#log# 필터 체인을 통과하는 요청 처리 시작");
            filterChain.doFilter(request, response);
            log.info("#log# 필터 체인을 통과하는 요청 처리 완료");
        } catch (CustomException e) {
            log.warn("#log# 필터 체인에서 예외 발생 [{}]", e.getExceptionCode().getMessage());
            setResponse(e.getExceptionCode().getHttpStatus(), response, e);
        }
    }

    public void setResponse(
            HttpStatus status,
            HttpServletResponse response,
            Throwable exception
    ) throws IOException {
        response.setStatus(status.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("message", exception.getMessage());
        ObjectMapper objectMapper = new ObjectMapper();
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
