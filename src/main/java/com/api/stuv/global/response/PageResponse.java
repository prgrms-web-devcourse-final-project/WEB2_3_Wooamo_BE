package com.api.stuv.global.response;

import com.api.stuv.global.exception.BadRequestException;
import com.api.stuv.global.exception.ErrorCode;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.querydsl.jpa.impl.JPAQuery;
import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageResponse<T> {
    private final List<T> contents;
    private final int currentPage;
    private final int size;
    private final long totalElements;
    private final int totalPages;
    private final boolean hasNext;

    public PageResponse(Page<T> page) {
        this.contents = page.getContent();
        this.currentPage = page.getNumber() + 1;
        this.size = page.getSize();
        this.totalElements = page.getTotalElements();
        this.totalPages = page.getTotalPages();
        this.hasNext = page.hasNext();
    }

    public static <T> PageResponse<T> of(Page<T> page) {
        if ( page.getNumber() + 1 > page.getTotalPages() && page.getTotalPages() != 0 ) throw new BadRequestException(ErrorCode.INVALID_PAGE_NUMBER);
        return new PageResponse<>(page);
    }

    public static <K> PageResponse<K> applyPage(JPAQuery<K> query, Pageable pageable, Long count) {
        List<K> content = query.offset(pageable.getOffset()).limit(pageable.getPageSize()).fetch();
        return PageResponse.of(new PageImpl<>(content, pageable, count));
    }
}