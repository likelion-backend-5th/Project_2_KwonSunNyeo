package com.likelion.sns.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CustomExceptionCode {
    /*
     * 400
     */
    PASSWORD_CHECK_ERROR(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다."),
    PROFILE_IMAGE_EMPTY(HttpStatus.BAD_REQUEST, "프로필 이미지를 찾을 수 없습니다."),
    /*
     * 401
     */
    EMPTY_JWT(HttpStatus.UNAUTHORIZED, "JWT는 필수입니다."),
    INVALID_JWT(HttpStatus.UNAUTHORIZED, "유효하지 않은 JWT입니다."),
    EXPIRED_JWT(HttpStatus.UNAUTHORIZED, "만료된 JWT입니다."),
    /*
     * 404
     */
    NOT_FOUND_USER(HttpStatus.NOT_FOUND, "해당 사용자를 찾을 수 없습니다."),
    /*
     * 409
     */
    ALREADY_EXIST_USER(HttpStatus.CONFLICT, "이미 존재하는 사용자 이름입니다."),
    /*
     * 415
     */
    UNSUPPORTED_IMAGE_FORMAT(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "지원하지 않는 이미지 파일 형식입니다."),
    /*
     * 500
     */
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버에서 문제가 발생했습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
