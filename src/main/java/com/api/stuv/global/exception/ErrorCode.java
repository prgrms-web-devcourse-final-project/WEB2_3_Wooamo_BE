package com.api.stuv.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // USER
    USER_ALREADY_EXIST(400, -1001, "해당 이메일이 이미 존재합니다."),
    CODE_EXPIRED(400, -1001, "인증 코드가 만료되었습니다."),
    WRONG_SIGNUP(400, -1002, "올바르지 않은 회원가입입니다."),
    WRONG_VERITIFICATION_CODE(400, -1002, "인증번호가 올바르지 않습니다."),
    INVALID_PASSWORD(400, -1003, "비빌번호가 올바르지 않습니다."),
    USER_NOT_FOUND(404, -1004, "존재하지 않는 사용자입니다."),

    // TOKEN
    EMPTY_JWT_TOKEN(400, -2000, "JWT 토큰이 없습니다."),
    INVALID_ACCESS_TOKEN(400, -2001, "유효하지 않은 토큰입니다."),
    EXPIRED_ACCESS_TOKEN(400, -2002, "어세스 토큰이 만료되었습니다."),
    EXPIRED_REFRESH_TOKEN(400, -2003, "리프레시 토큰이 만료되었습니다."),
    INVALID_REFRESH_TOKEN(400, -2004, "잘못된 리프레시 토큰입니다."),
    INVALID_AUTHORITY_TOKEN(400, -2005, "권한 정보가 없는 토큰입니다."),

    // COMMON
    NOT_FOUND(404, -3000, "잘못된 경로입니다."),
    BAD_REQUEST(400, -3001, "유효하지 않은 요청입니다."),
    INVALID_REQUEST_BODY(400, -3002, "전달된 데이터가 올바르지 않습니다."),
    METHOD_NOT_ALLOWED(405, -3003,"잘못된 Http Method 입니다."),
    INTERNAL_SERVER_ERROR(500, -3004, "서버 내부 오류입니다."),
    UNAUTHORIZED(401, -3005, "토큰 정보가 만료되었거나 존재하지 않습니다."),
    FORBIDDEN(403, -3006, "접근 권한이 없습니다."),
    INVALID_SORT_TYPE(400, -3007, "올바르지 않은 정렬 타입입니다."),

    // FRIEND
    FRIEND_NOT_FOUND(404, -4000, "해당 친구를 찾을 수 없습니다."),
    FRIEND_REQUEST_NOT_FOUND(404, -4002, "친구 요청을 찾을 수 없습니다."),
    FRIEND_REQUEST_ALREADY_EXIST(400, -4003, "이미 친구 요청을 보냈습니다."),
    FRIEND_REQUEST_NOT_AUTHORIZED(403, -4004, "해당 친구 요청에 대한 수락 권한이 없습니다."),
    FRIEND_REQUEST_ALREADY_ACCEPTED(400, -4005, "이미 친구 요청을 수락했습니다."),
    ;

    private final int status;
    private final int code;
    private final String message;
}
