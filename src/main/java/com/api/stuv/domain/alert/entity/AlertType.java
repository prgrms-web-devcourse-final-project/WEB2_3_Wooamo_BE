package com.api.stuv.domain.alert.entity;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum AlertType {
    COMMENT("님이 게시글에 댓글을 달았습니다."),
    FOLLOW("님이 친구요청을 보냈습니다."),
    CONFIRM("님의 답변이 채택되었습니다.");

    private final String text;
}
