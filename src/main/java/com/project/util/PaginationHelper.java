package com.project.util;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.project.dto.response.PagedResponse;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Utility class for pagination operations.
 */
@Component
public class PaginationHelper {

    private static final int MAX_PAGE_SIZE = 100;
    private static final int DEFAULT_PAGE_SIZE = 20;

    public Pageable sanitizePageable(Pageable pageable) {
        int size = pageable.getPageSize();
        if (size <= 0 || size > MAX_PAGE_SIZE) {
            size = DEFAULT_PAGE_SIZE;
        }
        return PageRequest.of(pageable.getPageNumber(), size, pageable.getSort());
    }

    public <E, R> PagedResponse<R> createPagedResponse(Page<E> page, Function<E, R> mapper) {
        List<R> content = page.getContent().stream()
                .map(mapper)
                .collect(Collectors.toList());

        return PagedResponse.<R>builder()
                .content(content)
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .first(page.isFirst())
                .empty(page.isEmpty())
                .build();
    }

    public int getMaxPageSize() {
        return MAX_PAGE_SIZE;
    }

    public int getDefaultPageSize() {
        return DEFAULT_PAGE_SIZE;
    }
}
