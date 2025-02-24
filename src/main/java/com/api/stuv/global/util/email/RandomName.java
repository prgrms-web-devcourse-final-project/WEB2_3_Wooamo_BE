package com.api.stuv.global.util.email;

import com.api.stuv.domain.user.response.GenerateNicknameResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class RandomName {
    private final WebClient webClient;
    private static final String NICKNAME_GENERATOR_URI = "https://nickname.hwanmoo.kr/?format=json";


    public String getRandomName(){
        GenerateNicknameResponse response = webClient.get()
                .uri(NICKNAME_GENERATOR_URI)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .bodyToMono(GenerateNicknameResponse.class)
                .block();

        String randomNickname = String.join("", response.getWords());
        return randomNickname;
    }
}
