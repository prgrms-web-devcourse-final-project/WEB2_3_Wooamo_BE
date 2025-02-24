package com.api.stuv.domain.admin.service;

import com.api.stuv.domain.party.dto.response.AdminPartyGroupResponse;
import com.api.stuv.domain.party.entity.PartyGroup;
import com.api.stuv.domain.party.entity.PartyStatus;
import com.api.stuv.domain.party.repository.PartyGroupRepository;
import com.api.stuv.global.response.PageResponse;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AdminServiceTest {

    @InjectMocks
    private AdminService adminService;

    @Mock
    private PartyGroupRepository partyGroupRepository;

    @Test
    @DisplayName("승인 여부와 함께 모든 파티 그룹을 조회한다")
    public void getAllPartyGroupsWithApprovedStatusTest() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<PartyGroup> groups = List.of(
                new PartyGroup("파티1", "설명1", BigDecimal.valueOf(100), 10L, LocalDate.now(), LocalDate.now().plusDays(10), PartyStatus.APPROVED),
                new PartyGroup("파티2", "설명2", BigDecimal.valueOf(200), 20L, LocalDate.now(), LocalDate.now().plusDays(5), PartyStatus.PENDING)
        );

        List<AdminPartyGroupResponse> responseList = groups.stream()
                .map(pg -> new AdminPartyGroupResponse(pg.getId(), pg.getName(), pg.getUsersCount(), 2L, pg.getStartDate(), pg.getEndDate(), pg.getStatus().getText()))
                .toList();

        Page<AdminPartyGroupResponse> mockPage = new PageImpl<>(responseList, pageable, responseList.size());

        when(partyGroupRepository.findAllPartyGroupsWithApproved(any(Pageable.class)))
                .thenReturn(PageResponse.of(mockPage));

        // When
        PageResponse<AdminPartyGroupResponse> response = adminService.getAllPartyGroupsWithApprovedStatus(pageable);

        // Then
        assertThat(response.getTotalElements()).isEqualTo(2);
        assertThat(response.getContents()).hasSize(2);
        assertThat(response.getContents().get(0).name()).isEqualTo("파티1");
        assertThat(response.getContents().get(1).name()).isEqualTo("파티2");
    }
}


