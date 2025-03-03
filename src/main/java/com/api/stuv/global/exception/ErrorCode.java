package com.api.stuv.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // USER
    USER_ALREADY_EXIST(400, -1000, "해당 이메일이 이미 존재합니다."),
    NICKNAME_ALREADY_EXIST(409, -1001, "해당 닉네임이 이미 존재합니다."),
    CODE_EXPIRED(400, -1002, "인증 코드가 만료되었습니다."),
    WRONG_SIGNUP(400, -1003, "올바르지 않은 회원가입입니다."),
    WRONG_VERIFICATION_CODE(400, -1004, "인증번호가 올바르지 않습니다."),
    NOT_VERIFICATION_EMAIL(400, -1005, "인증되지 않은 이메일입니다."),
    INVALID_PASSWORD(400, -1006, "아이디 또는 비밀번호가 올바르지 않습니다."),
    USER_NOT_FOUND(404, -1007, "존재하지 않는 사용자입니다."),
    DATA_ACCESS_API(401, -1008, "데이터를 받아오지 못했습니다"),
    QUEST_ALREADY_REWARD(400, -1009, "보상을 이미 받았습니다."),
    REWARD_CONDITION_NOT_MET(403, -1010, "보상을 받을 조건이 충족되지 않았습니다."),

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
    INVALID_PAGE_SIZE(400, -3008, "올바르지 않은 페이지 사이즈입니다."),
    INVALID_PAGE_NUMBER(400, -3009, "올바르지 않은 페이지 번호입니다."),
    HTTP_API_ERROR(400, -3010, "HTTP API에서 오류가 발생했습니다."),
    JSON_PARSING_ERROR(400, -3011, "JSON 파싱이 잘못되었습니다."),
    ARGUMENT_TYPE_MISMATCH(400, -3012, "올바르지 않은 파라미터입니다."),
    DATE_FORMAT_MISMATCH(400, -3013, "날짜 형식이 올바르지 않습니다. (yyyy-MM-dd)"),
    INVALID_ARGUMENT_METHOD(400, -3014, "데이터 유효성 검증에 실패했습니다."),
    REDIS_NOT_CONNECTED(500, -3015, "Redis 서버에 연결할 수 없습니다."),
    DATABASE_ERROR(500, -3100, "데이터베이스 처리 중 오류가 발생했습니다."),

    // FRIEND
    FRIEND_NOT_FOUND(404, -4000, "해당 친구를 찾을 수 없습니다."),
    FRIEND_REQUEST_NOT_FOUND(404, -4002, "친구 요청을 찾을 수 없습니다."),
    FRIEND_REQUEST_ALREADY_EXIST(400, -4003, "이미 친구 요청을 보냈습니다."),
    FRIEND_REQUEST_NOT_AUTHORIZED(403, -4004, "해당 친구 요청에 대한 수락 권한이 없습니다."),
    FRIEND_DELETE_NOT_AUTHORIZED(403, -4006, "해당 친구 삭제 권한이 없습니다."),
    FRIEND_REQUEST_ALREADY_ACCEPTED(400, -4005, "해당 유저와는 이미 친구 상태입니다."),
    FRIEND_REQUEST_SELF(400, -4007, "자기 자신에게 친구 요청을 보낼 수 없습니다."),

    // BOARD
    BOARD_NOT_FOUND(404, -5000, "해당 게시글을 찾을 수 없습니다."),
    BOARD_NOT_AUTHORIZED(403, -5001, "해당 게시글에 대한 권한이 없습니다."),

    // COMMENT
    COMMENT_NOT_FOUND(404, -6000, "해당 댓글을 찾을 수 없습니다."),
    COMMENT_ALREADY_CONFIRM(400, -6001, "이미 채택된 댓글이 있습니다."),
    COMMENT_NOT_AUTHORIZED(403, -6002, "해당 댓글에 대한 권한이 없습니다."),
    BOARD_NOT_QUESTION(400, -6003, "질문 게시글이 아닙니다."),
    COMMENT_BY_WRITER(400, -6004, "작성자는 채택할 수 없습니다."),
    CONFIRMED_COMMENT(400, -6005, "채택된 댓글은 삭제할 수 없습니다."),

    // COSTUME
    COSTUME_NOT_FOUND(404, -7000, "해당 코스튬을 찾을 수 없습니다."),
    COSTUME_ALREADY_PRESENT(400, -7001, "이미 구매한 코스튬 입니다."),
    COSTUME_NOT_PURCHASE(400, -7992, "구매할 수 있는 코스튬이 없습니다"),

    // IMAGE
    IMAGE_NAME_NOT_FOUND(404, -8000, "해당 이미지를 찾을 수 없습니다."),

    // TODO
    TODOLIST_NOT_FOUND(409, -9000, "TODOLIST가 없습니다."),
    TODO_NOT_FOUND(404, -900, "해당 TODO를 찾을 수 없습니다."),
    TODO_SAVE_FAILED(400, -9002, "TODO 저장에 실패했습니다."),

    // CHAT SOCKET
    CHAT_ROOM_NOT_FOUND(404, -10000, "채팅방을 찾을 수 없습니다."),
    USER_ALREADY_IN_CHAT_ROOM(409, -10001, "이미 참여 중인 사용자입니다."),
    CHAT_ROOM_ALREADY_EXISTS(409, -10002, "이미 존재하는 채팅방입니다."),
    CHAT_ROOM_MAX_MEMBERS_EXCEEDED(403, -10003, "채팅방 최대 인원 초과"),

    // PARTY
    PARTY_NOT_FOUND(404, -11000, "해당 팟을 팢을 수 없습니다."),
    PARTY_INVALID_DATE(400, -11001, "해당 날짜는 팟의 인증 기간이 아닙니다."),
    CONFIRM_NOT_FOUND(404, -11002, "해당 회원의 인증을 찾을 수 없습니다."),

    //TIMER
    TIMER_NOT_EXIST(404, -12000, "저장된 타이머가 없습니다."),
    CATEGORY_NOT_FOUND(404, -12001, "타이머 카테고리를 찾을 수 없습니다."),

    // PAYMENTS
    PAYMENTS_MISMATCH(404, -13000, "결제 금액이 일치하지 않습니다."),
    PAYMENTS_NOT_FOUND(400, -13001, "승인에 실패했습니다. 다시 시도해주세요"),
    POINT_NOT_ENOUGH(400, -13002, "포인트가 부족합니다.")
    ;

    private final int status;
    private final int code;
    private final String message;
}
