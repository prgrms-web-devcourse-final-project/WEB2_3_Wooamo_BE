package com.api.stuv.domain.user.service;

import com.api.stuv.domain.auth.util.TokenUtil;
import com.api.stuv.domain.friend.repository.FriendRepository;
import com.api.stuv.domain.image.entity.EntityType;
import com.api.stuv.domain.image.entity.ImageFile;
import com.api.stuv.domain.image.repository.ImageFileRepository;
import com.api.stuv.domain.image.service.S3ImageService;
import com.api.stuv.domain.socket.service.ChatRoomMemberService;
import com.api.stuv.domain.timer.repository.StudyTimeRepository;
import com.api.stuv.domain.user.dto.*;
import com.api.stuv.domain.user.dto.request.*;
import com.api.stuv.domain.user.dto.response.*;
import com.api.stuv.domain.user.entity.*;
import com.api.stuv.domain.user.repository.PointHistoryRepository;
import com.api.stuv.domain.user.repository.UserCostumeRepository;
import com.api.stuv.domain.user.repository.UserRepository;
import com.api.stuv.global.exception.BadRequestException;
import com.api.stuv.global.exception.DuplicateException;
import com.api.stuv.global.exception.ErrorCode;
import com.api.stuv.global.exception.NotFoundException;
import com.api.stuv.global.service.RedisService;
import com.api.stuv.global.util.email.RandomCode;
import com.api.stuv.global.util.email.RandomName;
import com.api.stuv.global.util.email.provider.EmailProvider;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;


@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserCostumeRepository userCostumeRepository;
    private final StudyTimeRepository studyTimeRepository;
    private final PointHistoryRepository pointHistoryRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final EmailProvider  emailProvider;
    private final RedisService redisService;
    private final S3ImageService s3ImageService;
    private final ChatRoomMemberService chatRoomMemberService;
    private final RandomName randomName;
    private final TokenUtil tokenUtil;
    private final ImageFileRepository imageFileRepository;
    private final FriendRepository friendRepository;

    public void registerUser(UserRequest userRequest) {
        String email = userRequest.email();

        if(!redisService.find(email, String.class).equals("Verified") || redisService.find(email, String.class) == null){
            throw new BadRequestException(ErrorCode.NOT_VERIFICATION_EMAIL);
        }

        //사용자 정보 저장
        User user = userRequest.from(userRequest, bCryptPasswordEncoder);
        userRepository.save(user);

        //userCostume 저장
        Long userId = user.getId();
        Long costumeId = user.getCostumeId();
        UserCostumeRequest userCostumeRequest = new UserCostumeRequest(userId, costumeId);
        UserCostume userCostume = userCostumeRequest.createUserCostumeRequeset(userId,  costumeId);
        userCostumeRepository.save(userCostume);
    }

    public void registerKakaoUser(KakaoUserRequest kakaoUserRequest) {
        String email = kakaoUserRequest.email();
        Long socialId = kakaoUserRequest.socialId();

        Boolean isExist = userRepository.existsByEmail(email);

        if(isExist){
            User user = userRepository.findByEmail(email);
            user.setSocialId(socialId);
            userRepository.save(user);

            return;
        }

        User user = kakaoUserRequest.kakaoFrom(kakaoUserRequest, socialId,  bCryptPasswordEncoder);
        userRepository.save(user);

        Long userId = user.getId();
        Long costumeId = user.getCostumeId();
        UserCostumeRequest userCostumeRequest = new UserCostumeRequest(userId, costumeId);
        UserCostume userCostume = userCostumeRequest.createUserCostumeRequeset(userId,  costumeId);
        userCostumeRepository.save(userCostume);
    }

    public void sendCertificateEmail(String email){
        Boolean isExist = userRepository.existsByEmail(email);

        if(isExist){
            throw new NotFoundException(ErrorCode.USER_ALREADY_EXIST);
        }

        String verificationCode = RandomCode.getRandomCode();
        emailProvider.sendMail(email, verificationCode);
    }

    @Transactional
    public void checkCertificateEmail(EmailCertificationRequest emailCertificationRequest){
        String userCode = emailCertificationRequest.code();
        String email = emailCertificationRequest.email();

        String code = redisService.find(email, String.class);

        if(code == null){
            //코드 만료
            throw new NotFoundException(ErrorCode.CODE_EXPIRED);
        }
        if(code.equals(userCode)){
            redisService.delete(email);
            redisService.save(email, "Verified", Duration.ofMinutes(10));
        } else {
            throw new NotFoundException(ErrorCode.WRONG_VERIFICATION_CODE);
        }
    }

    public void checkDuplicateNickname(String nickname){
        if(userRepository.existsByNickname(nickname)){
            throw new DuplicateException(ErrorCode.NICKNAME_ALREADY_EXIST);
        }
    }

    public UserInformationResponse getUserInformation(Long userId){
        Long myId = null;
        try {
            myId = tokenUtil.getUserId();
        } catch (Exception e) {
            myId = 0L;
        }
        Long friends = friendRepository.getTotalFriendListPage(userId);
        if (friends == null) friends = 0L;

        UserInformationDTO information = userRepository.getUserInformation(userId, myId, friends);
        return new UserInformationResponse(
                information.userId(),
                information.context(),
                information.link(),
                information.nickname(),
                information.newFilename() == null ? null : s3ImageService.generateImageFile(
                        EntityType.COSTUME,
                        information.costumeId(),
                        information.newFilename()
                ),
                information.status(),
                information.friends(),
                information.friendId()
        );
    }

    public MyInformationResponse getMyInformation(){
        Long myId = tokenUtil.getUserId();
        Long friends = friendRepository.getTotalFriendListPage(myId);
        if (friends == null) friends = 0L;
        MyInformationDTO information = userRepository.getUserByMyId(myId, friends);
        return new MyInformationResponse(
                information.userId(),
                information.context(),
                information.link(),
                information.nickname(),
                information.point(),
                information.role(),
                information.newFilename() == null ? null : s3ImageService.generateImageFile(
                        EntityType.COSTUME,
                        information.costumeId(),
                        information.newFilename()),
                information.friends()
        );
    }

    @Transactional
    public ModifyProfileResponse modifyProfile(ModifyProfileRequest modifyProfileRequest){
        Long userId = tokenUtil.getUserId();
        String context =  modifyProfileRequest.context();
        String link = modifyProfileRequest.link();

        User user = userRepository.findById(userId).orElseThrow(()->new NotFoundException(ErrorCode.USER_NOT_FOUND));

        user.modifyProfileRequest(context, link);
        userRepository.save(user);

        ModifyProfileResponse modifyProfileResponse = new ModifyProfileResponse(userId);


        return modifyProfileResponse;
    }

    public List<UserBoardListResponse> getUserBoardList(Long userId){

        List<UserBoardListDTO> responses = userRepository.getUserBoardList(userId);

        return responses.stream()
                .map(response -> new UserBoardListResponse(
                        response.boardId(),
                        response.title(),
                        response.context(),
                        response.boardType(),
                        response.createdAt(),
                        response.newFileName() == null ? null : s3ImageService.generateImageFile(
                                EntityType.BOARD,
                                response.boardId(),
                                response.newFileName()
                        )
                )).toList();
    }

    public UserQuestStateResponse userQuestState(){
        Long userId = tokenUtil.getUserId();

        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();         // 오늘 00:00:00
        LocalDateTime endOfDay = today.atTime(LocalTime.MAX);    // 오늘 23:59:59.999999

        if(pointHistoryRepository.findByUserIdAndTransactionType(userId, startOfDay, endOfDay) != null){
            return new UserQuestStateResponse("보상 완료");
        }

        List<Long> studyTimeList = studyTimeRepository.findStudyTimeByUserIdAndStudyDate(userId);
        long totalTime = (studyTimeList != null)
                ? studyTimeList.stream()
                .filter(Objects::nonNull)
                .mapToLong(Long::longValue)
                .sum()
                : 0L;

        return new UserQuestStateResponse(totalTime > 10800L ? "보상 받기" : "진행중");
    }


    @Transactional
    public void userQuestReward() {
        Long userId = tokenUtil.getUserId();

        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();         // 오늘 00:00:00
        LocalDateTime endOfDay = today.atTime(LocalTime.MAX);    // 오늘 23:59:59.999999

        if(pointHistoryRepository.findByUserIdAndTransactionType(userId, startOfDay, endOfDay) != null){
            throw new DuplicateException(ErrorCode.QUEST_ALREADY_REWARD);
        }

        List<Long> studyTimeList = studyTimeRepository.findStudyTimeByUserIdAndStudyDate(userId);
        long totalTime = (studyTimeList != null)
                ? studyTimeList.stream()
                .filter(Objects::nonNull)
                .mapToLong(Long::longValue)
                .sum()
                : 0L;

        BigDecimal point = RewardType.DAILY.getValue();
        if(totalTime > 10800L){
            user.updatePoint(point);
            userRepository.save(user);

            PointHistory pointHistory = new PointHistory(userId, HistoryType.PERSONAL, point, HistoryType.PERSONAL.getText());
            pointHistoryRepository.save(pointHistory);
        } else {
            throw new BadRequestException(ErrorCode.REWARD_CONDITION_NOT_MET);
        }
    }

    public List<ImageUrlDTO> getUserCostume() {
        Long userId = tokenUtil.getUserId();
        if(userId == null){
            throw new NotFoundException(ErrorCode.USER_NOT_FOUND);
        }

        List<ImageUrlDTO> responses  = userRepository.getUserCostume(userId);
        if(responses.isEmpty()) { throw new NotFoundException(ErrorCode.USER_NOT_FOUND); }
        return responses.stream()
                .map(costumeList -> new ImageUrlDTO(
                        costumeList.entityId(),
                        costumeList.newFileName() == null ? null : s3ImageService.generateImageFile(
                                EntityType.COSTUME,
                                costumeList.entityId(),
                                costumeList.newFileName()
                        )
                )).toList();
    }

    @Transactional
    public ChangeUserCostume changeUserCostume(Long costumeId) {
        Long userId = tokenUtil.getUserId();
        if(userId == null){
            throw new  NotFoundException(ErrorCode.USER_NOT_FOUND);
        }

        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));
        Long id = userCostumeRepository.findIdByCostumeId(costumeId, userId);
        user.changeUserCostume(id);
        userRepository.save(user);

        ImageFile newFilename = imageFileRepository.findByEntityIdAndEntityType(costumeId, EntityType.COSTUME).orElseThrow(() ->  new NotFoundException(ErrorCode.COSTUME_NOT_FOUND));
        String url = s3ImageService.generateImageFile(
                        EntityType.COSTUME,
                        costumeId,
                        newFilename.getNewFilename());

        ChangeUserCostume changeUserCostume = new ChangeUserCostume(url);

        chatRoomMemberService.updateUserProfileInCache(userId, url);

        return changeUserCostume;
    }

}
