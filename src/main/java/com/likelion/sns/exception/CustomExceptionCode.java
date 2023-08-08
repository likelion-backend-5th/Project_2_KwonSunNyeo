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
    NOT_LIKED_YET(HttpStatus.BAD_REQUEST, "아직 좋아요를 누르지 않은 상태입니다."),
    /*
     * 401
     */
    EMPTY_JWT(HttpStatus.UNAUTHORIZED, "JWT는 필수입니다."),
    INVALID_JWT(HttpStatus.UNAUTHORIZED, "유효하지 않은 JWT입니다."),
    EXPIRED_JWT(HttpStatus.UNAUTHORIZED, "만료된 JWT입니다."),
    /*
     * 403
     */
    UNAUTHORIZED_ACCESS(HttpStatus.FORBIDDEN, "잘못된 접근입니다."),
    CANNOT_LIKE_OWN_ARTICLE(HttpStatus.FORBIDDEN, "자신의 피드에는 좋아요를 할 수 없습니다."),
    /*
     * 404
     */
    NOT_FOUND_USER(HttpStatus.NOT_FOUND, "해당 사용자를 찾을 수 없습니다."),
    NOT_FOUND_ARTICLE(HttpStatus.NOT_FOUND, "해당 게시글을 찾을 수 없습니다."),
    NOT_FOUND_IMAGE(HttpStatus.NOT_FOUND, "해당 이미지를 찾을 수 없습니다."),
    NOT_FOUND_COMMENT(HttpStatus.NOT_FOUND, "해당 댓글을 찾을 수 없습니다."),
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
