package com.api.stuv.domain.admin.service;

import com.api.stuv.domain.admin.dto.MemberDetailDTO;
import com.api.stuv.domain.admin.dto.request.CostumeRequest;
import com.api.stuv.domain.admin.dto.response.AdminPartyAuthDetailResponse;
import com.api.stuv.domain.admin.exception.CostumeNotFound;
import com.api.stuv.domain.admin.exception.InvalidPointFormat;
import com.api.stuv.domain.image.entity.EntityType;
import com.api.stuv.domain.image.entity.ImageFile;
import com.api.stuv.domain.image.exception.ImageFileNotFound;
import com.api.stuv.domain.image.repository.ImageFileRepository;
import com.api.stuv.domain.image.service.ImageService;
import com.api.stuv.domain.image.service.S3ImageService;
import com.api.stuv.domain.admin.dto.response.AdminPartyGroupResponse;
import com.api.stuv.domain.party.repository.member.GroupMemberRepository;
import com.api.stuv.domain.party.repository.party.PartyGroupRepository;
import com.api.stuv.domain.shop.entity.Costume;
import com.api.stuv.domain.shop.repository.CostumeRepository;
import com.api.stuv.global.exception.DateOutOfRangeException;
import com.api.stuv.global.exception.ErrorCode;
import com.api.stuv.global.exception.NotFoundException;
import com.api.stuv.global.response.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final CostumeRepository costumeRepository;
    private final ImageFileRepository imageFileRepository;
    private final S3ImageService s3ImageService;
    private final ImageService imageService;
    private final PartyGroupRepository partyGroupRepository;
    private final GroupMemberRepository groupMemberRepository;

    @Transactional
    public void createCostume(CostumeRequest request, MultipartFile file) {
        if(file == null || file.isEmpty()) {throw new ImageFileNotFound();}
        if(request.point().compareTo(BigDecimal.ZERO) < 0) {throw new InvalidPointFormat();}
        Costume costume = Costume.createCostumeContents(request.costumeName(), request.point());
        costumeRepository.save(costume);
        imageService.handleImage(costume.getId(), file, EntityType.COSTUME);
    }

    @Transactional
    public void modifyCostume(long costumeId, CostumeRequest request){
        Costume costume = costumeRepository.findById(costumeId).orElseThrow(CostumeNotFound::new);
        if(request.point().compareTo(BigDecimal.ZERO) < 0) {throw new InvalidPointFormat();}
        costume.modifyCostumeContents(request.costumeName(), request.point());
        costumeRepository.save(costume);
    }

    public void deleteCostume(Long costumeId) {
        Costume costume = costumeRepository.findById(costumeId).orElseThrow(CostumeNotFound::new);
        ImageFile imageFile = imageFileRepository.findByEntityIdAndEntityType(costumeId, EntityType.COSTUME).orElseThrow(ImageFileNotFound::new);
        s3ImageService.deleteImageFile(EntityType.COSTUME, costumeId, imageFile.getNewFilename());
        imageFileRepository.deleteById(costumeId);
        costumeRepository.delete(costume);
    }

    public PageResponse<AdminPartyGroupResponse> getAllPartyGroupsWithApprovedStatus(Pageable pageable) {
        return partyGroupRepository.findAllPartyGroupsWithApproved(pageable);
    }

    public AdminPartyAuthDetailResponse getPartyAuthDetailWithMembers(Long partyId, LocalDate date) {
        AdminPartyAuthDetailResponse response = partyGroupRepository.findPartyGroupById(partyId);
        if (response == null) throw new NotFoundException(ErrorCode.PARTY_NOT_FOUND);
        if (date == null) date = response.startDate();
        if (date.isBefore(response.startDate()) || date.isAfter(response.endDate())) throw new DateOutOfRangeException(ErrorCode.PARTY_INVALID_DATE);

        List<MemberDetailDTO> members = groupMemberRepository.findMemberListWithConfirmedByDate(partyId, date);

        return AdminPartyAuthDetailResponse.from(response, members);
    }
}
