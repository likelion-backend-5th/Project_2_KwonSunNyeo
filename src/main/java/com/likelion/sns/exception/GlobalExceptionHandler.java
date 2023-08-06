package com.likelion.sns.exception;

import com.likelion.sns.user.dto.MessageResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    // 유효성 검사에 발생하는 예외
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<MessageResponseDto> handleCustomException(MethodArgumentNotValidException e) {
        MessageResponseDto response = new MessageResponseDto();
        String errorMessage = Objects.requireNonNull(e.getBindingResult().getFieldError()).getDefaultMessage();
        response.setMessage(errorMessage);
        return new ResponseEntity<>(response, e.getStatusCode());
    }
    // 사용자의 요청에 발생하는 예외
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<MessageResponseDto> handleCustomException(ResponseStatusException e) {
        MessageResponseDto response = new MessageResponseDto();
        response.setMessage(e.getReason());
        return new ResponseEntity<>(response, e.getStatusCode());
    }
    // 개발자의 요청에 발생하는 예외
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<MessageResponseDto> handleCustomException(CustomException e) {
        MessageResponseDto response = new MessageResponseDto();
        response.setMessage(e.getExceptionCode().getMessage());
        return new ResponseEntity<>(response, e.getExceptionCode().getHttpStatus());
    }
}
