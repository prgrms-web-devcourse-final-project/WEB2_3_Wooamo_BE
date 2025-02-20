package com.api.stuv.domain.user.response;

import lombok.Data;

import java.util.List;

@Data
public class GenerateNicknameResponse {
    private List<String> words;  // API 응답: {"words": ["멋진", "호랑이"]}
}
