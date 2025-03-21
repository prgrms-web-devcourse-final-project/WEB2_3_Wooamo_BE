package com.api.stuv.domain.user.controller;

import com.api.stuv.domain.auth.util.TokenUtil;
import com.api.stuv.domain.timer.dto.response.RankInfoResponse;
import com.api.stuv.domain.timer.dto.response.RankResponse;
import com.api.stuv.domain.timer.service.TimerService;
import com.api.stuv.domain.user.dto.ImageUrlDTO;
import com.api.stuv.domain.user.dto.request.*;
import com.api.stuv.domain.user.dto.response.*;
import com.api.stuv.domain.user.service.KakaoService;
import com.api.stuv.domain.user.service.TodoService;
import com.api.stuv.domain.user.service.UserService;
import com.api.stuv.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "User API", description = "유저 인증 관련 API")
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;
    private final KakaoService kakaoService;
    private final TodoService todoService;
    private final TimerService timerService;
    private final TokenUtil tokenUtil;

    @Value("${frontend.server}")
    private String url;

    @Operation(summary = "인증메일 전송 API", description = "인증 메일을 전송합니다.")
    @PostMapping("/auth/send")
    public ResponseEntity<ApiResponse<Void>> sendCertificateEmail(@RequestBody UserRequest userRequest){
        userService.sendCertificateEmail(userRequest.email());

        return ResponseEntity.ok()
                .body(ApiResponse.success());
    }

    @Operation(summary = "메일 인증 API", description = "메일로 보낸 인증 코드를 확인합니다")
    @PostMapping("/auth/check")
    public ResponseEntity<ApiResponse<Void>> checkCertificateEmail(@RequestBody @Valid EmailCertificationRequest emailCertificationRequest){
        userService.checkCertificateEmail(emailCertificationRequest);

        return ResponseEntity.ok()
                .body(ApiResponse.success());
    }

    @Operation(summary = "회원가입 API", description = "회원가입 API입니다.")
    @PostMapping("/register")
    private ResponseEntity<ApiResponse<Void>> registerUser(@RequestBody UserRequest userRequest){
        userService.registerUser(userRequest);

        return ResponseEntity.ok()
                .body(ApiResponse.success());
    }

    @Operation(summary = "카카오 로그인 API", description = "카카오 로그인 API 입니다.")
    @PostMapping("/kakaoLogin")
    private ResponseEntity<ApiResponse<LoginResponse>> kakaoLogin(@RequestBody KakaoCodeRequest code, HttpServletResponse response, HttpServletRequest request) throws IOException {
        return ResponseEntity.ok()
                .body(ApiResponse.success(kakaoService.kakaoLogin(code, response, request)));
    }

    @Operation(summary = "닉네임 중복확인 API", description = "등록하려는 닉네임이 중복인지 확인합니다.")
    @PostMapping("/nickname")
    private ResponseEntity<ApiResponse<Void>> duplicateNickname(@RequestBody @Valid UserRequest userRequest){
        userService.checkDuplicateNickname(userRequest.nickname());

        return ResponseEntity.ok()
                .body(ApiResponse.success());
    }

    @Operation(summary = "타인 정보 가져오기 API", description = "타인의 정보를 가져옵니다.")
    @GetMapping("/{userId}")
    private ResponseEntity<ApiResponse<UserInformationResponse>> getUserInformation(@PathVariable("userId") Long userId){
        return ResponseEntity.ok()
                .body(ApiResponse.success(userService.getUserInformation(userId)));

    }

    @Operation(summary = "내 정보 가져오기 API", description = "본인의 정보를 가져옵니다.")
    @GetMapping
    private ResponseEntity<ApiResponse<MyInformationResponse>> getMyInformation(){
        return ResponseEntity.ok()
                .body(ApiResponse.success(userService.getMyInformation()));
    }


    @Operation(summary = "프로필 수정 API", description = "자기소개와 링크를 수정합니다.")
    @PutMapping
    private ResponseEntity<ApiResponse<ModifyProfileResponse>> modifyProfile(@RequestBody @Valid ModifyProfileRequest modifyProfileRequest){
        return ResponseEntity.ok()
                .body(ApiResponse.success(userService.modifyProfile(modifyProfileRequest)));
    }

    @Operation(summary = "투두리스트 추가 API", description = "오늘의 투두 리스트를 추가합니다.")
    @PostMapping("/todo")
    private ResponseEntity<ApiResponse<AddTodoResponse>> addTodoList(@RequestBody @Valid AddTodoRequest addTodoRequest){

        return ResponseEntity.ok()
                .body(ApiResponse.success(todoService.addTodoList(addTodoRequest)));
    }

    @Operation(summary = "투두리스트 조회 API", description = "오늘의 투두 리스트를 조회합니다.")
    @GetMapping("/todo")
    private ResponseEntity<ApiResponse<List<GetTodoListResponse>>> addTodoList(){

        return  ResponseEntity.ok()
                .body(ApiResponse.success(todoService.getTodoList()));
    }

    @Operation(summary = "투두리스트 삭제 API", description = "오늘의 투두 리스트를 삭제합니다.")
    @DeleteMapping("/todo/{todoId}")
    private ResponseEntity<ApiResponse<Void>> deleteTodoList(@PathVariable("todoId") Long todoId) {
        todoService.deleteTodoList(todoId);

        return ResponseEntity.ok()
                .body(ApiResponse.success());
    }

    @Operation(summary = "투두리스트 수정 API", description = "오늘의 투두 리스트를 수정합니다.")
    @PutMapping("/todo/{todoId}")
    private ResponseEntity<ApiResponse<Void>> ModifyTodoList(@PathVariable("todoId") Long todoId, @RequestBody @Valid ModifyTodoRequest modifyTodoRequest) {
        todoService.modifyTodoList(todoId, modifyTodoRequest);

        return ResponseEntity.ok()
                .body(ApiResponse.success());
    }

    @Operation(summary = "본인 작성 게시글 조회 API", description = "본인이 작성했던 글 목록을 조회하는 API 입니다.")
    @GetMapping("/board/{userId}")
    private ResponseEntity<ApiResponse<List<UserBoardListResponse>>> getUserBoardList(@PathVariable("userId") Long userId) {

        return ResponseEntity.ok()
                .body(ApiResponse.success(userService.getUserBoardList(userId)));
    }

    @Operation(summary = "개인 퀘스트 진행여부 조회 API", description = "개인의 일일 퀘스트 진행 상황을 조회합니다")
    @GetMapping("/quest")
    private  ResponseEntity<ApiResponse<UserQuestStateResponse>> userQuestState(){
        return ResponseEntity.ok()
                .body(ApiResponse.success(userService.userQuestState()));
    }

    @Operation(summary = "개인 퀘스트 보상 받기 API", description = "개인의 일일 퀘스트 보상을 받습니다.")
    @PostMapping("/reward")
    private ResponseEntity<ApiResponse<Void>> userQuestReward() {
        userService.userQuestReward();

        return ResponseEntity.ok()
                .body(ApiResponse.success());
    }

    @Operation(summary = "코스튬 조회 API", description = "사용자의 코스튬 목록을 조회합니다.")
    @GetMapping("/costume")
    public ResponseEntity<ApiResponse<List<ImageUrlDTO>>> getUserCostume() {

        return ResponseEntity.ok()
                .body(ApiResponse.success(userService.getUserCostume()));
    }

    @Operation(summary = "코스튬 변경 API", description = "사용자의 코스튬 목록을 조회합니다.")
    @PatchMapping("/costume/{costumeId}")
    public ResponseEntity<ApiResponse<ChangeUserCostume>> changeUserCostume(@PathVariable("costumeId") Long costumeId) {
        return ResponseEntity.ok()
                .body(ApiResponse.success(userService.changeUserCostume(costumeId)));
    }

    @Operation(summary = "회원 공부 시간 랭킹 조회 API", description = "회원의 주간 공부시간 랭킹을 조회합니다.")
    @GetMapping("/ranking")
    public ResponseEntity<ApiResponse<RankResponse>> userRankingNumber() {
        return ResponseEntity.ok()
                .body(ApiResponse.success(timerService.getUserRank()));
    }

    @Operation(summary = "공부 시간 랭킹 상위 3명 조회 API", description = "주간 랭킹의 상위 3명의 정보를 조회합니다.")
    @GetMapping("/topranking")
    public ResponseEntity<ApiResponse<List<RankInfoResponse>>> topUserRanking() {
        return ResponseEntity.ok()
                .body(ApiResponse.success(timerService.getTopRankUser()));
    }

    @Operation(summary = "로그인 확인 여부 API", description = "로그인 확인 여부를 조회합니다")
    @GetMapping("/isLogin")
    public ResponseEntity<ApiResponse<Boolean>> isLogin() {
        try {
            Long userId = tokenUtil.getUserId();

            return ResponseEntity.ok()
                    .body(ApiResponse.success(true));
        } catch (Exception e) {
            return ResponseEntity.ok()
                    .body(ApiResponse.success(false));
        }

    }
}
