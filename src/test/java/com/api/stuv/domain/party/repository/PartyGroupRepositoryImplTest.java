package com.api.stuv.domain.party.repository;

import com.api.stuv.domain.party.dto.response.PartyGroupResponse;
import com.api.stuv.domain.party.service.PartyService;
import com.api.stuv.global.response.PageResponse;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class PartyGroupRepositoryImplTest {

    @Mock
    private JPAQueryFactory factory;

    @Mock
    private JPAQuery<PartyGroupResponse> query;

    @Mock
    private JPAQuery<Long> countQuery;

    @Mock
    private PartyGroupRepository partyRepository;

    @InjectMocks
    private PartyService partyService;

    @Test
    void getPendingPartyGroupsWithSearch_ShouldReturnPageResponse() {
        // GIVEN: Mock 데이터 준비
        List<PartyGroupResponse> mockData = List.of(
                new PartyGroupResponse(1L, "파티1", 10L, 5L, LocalDate.of(2025, 2, 22), LocalDate.of(2025, 2, 26)),
                new PartyGroupResponse(2L, "파티2", 20L, 8L, LocalDate.of(2025, 2, 23), LocalDate.of(2025, 2, 28))
        );

        Pageable pageable = PageRequest.of(0, 10);

        // WHEN: Mocking된 partyRepository가 호출될 때 가짜 데이터 반환
        when(partyRepository.getPendingPartyGroupsWithSearch(anyString(), any(Pageable.class)))
                .thenReturn(PageResponse.of(new PageImpl<PartyGroupResponse>(mockData, PageRequest.of(0, 10), mockData.size())));

        // ACT
        PageResponse<PartyGroupResponse> result = partyService.getPendingPartyGroupsWithSearch("파티", pageable);

        // ASSERT: 결과 검증 (AssertJ)
        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContents()).hasSize(2);
        assertThat(result.getContents())
                .extracting(PartyGroupResponse::name)
                .containsExactly("파티1", "파티2");
    }
}