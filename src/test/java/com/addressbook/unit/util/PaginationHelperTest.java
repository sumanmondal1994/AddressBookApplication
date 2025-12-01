package com.addressbook.unit.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.project.dto.response.PagedResponse;
import com.project.util.PaginationHelper;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Pagination Helper Unit Tests")
class PaginationHelperTest {

    private PaginationHelper paginationHelper;

    @BeforeEach
    void setUp() {
        paginationHelper = new PaginationHelper();
    }

    @Nested
    @DisplayName("Sanitize Pageable Tests")
    class SanitizePageableTests {

        @Test
        @DisplayName("Should return same pageable when size is within bounds")
        void testSanitizePageableValidSize() {
            Pageable pageable = PageRequest.of(0, 50);

            Pageable result = paginationHelper.sanitizePageable(pageable);

            assertThat(result.getPageNumber()).isEqualTo(0);
            assertThat(result.getPageSize()).isEqualTo(50);
        }

        @Test
        @DisplayName("Should use default size when page size exceeds maximum")
        void testSanitizePageableExceedsMax() {
            Pageable pageable = PageRequest.of(0, 500);

            Pageable result = paginationHelper.sanitizePageable(pageable);

            assertThat(result.getPageNumber()).isEqualTo(0);
            assertThat(result.getPageSize()).isEqualTo(20); // Default size
        }

        @Test
        @DisplayName("Should preserve sort when sanitizing pageable")
        void testSanitizePageablePreservesSort() {
            Sort sort = Sort.by(Sort.Direction.DESC, "name");
            Pageable pageable = PageRequest.of(2, 25, sort);

            Pageable result = paginationHelper.sanitizePageable(pageable);

            assertThat(result.getPageNumber()).isEqualTo(2);
            assertThat(result.getPageSize()).isEqualTo(25);
            assertThat(result.getSort()).isEqualTo(sort);
        }

        @Test
        @DisplayName("Should accept maximum page size")
        void testSanitizePageableAtMaxSize() {
            Pageable pageable = PageRequest.of(0, 100);

            Pageable result = paginationHelper.sanitizePageable(pageable);

            assertThat(result.getPageSize()).isEqualTo(100);
        }

        @Test
        @DisplayName("Should reject page size of 101")
        void testSanitizePageableJustOverMax() {
            Pageable pageable = PageRequest.of(0, 101);

            Pageable result = paginationHelper.sanitizePageable(pageable);

            assertThat(result.getPageSize()).isEqualTo(20); // Default size
        }
    }

    @Nested
    @DisplayName("Create Paged Response Tests")
    class CreatePagedResponseTests {

        @Test
        @DisplayName("Should create paged response from page with content")
        void testCreatePagedResponseWithContent() {
            List<String> content = Arrays.asList("item1", "item2", "item3");
            Page<String> page = new PageImpl<>(content, PageRequest.of(0, 10), 3);

            PagedResponse<String> response = paginationHelper.createPagedResponse(
                    page, str -> str.toUpperCase());

            assertThat(response.getContent()).containsExactly("ITEM1", "ITEM2", "ITEM3");
            assertThat(response.getPage()).isEqualTo(0);
            assertThat(response.getSize()).isEqualTo(10);
            assertThat(response.getTotalElements()).isEqualTo(3);
            assertThat(response.getTotalPages()).isEqualTo(1);
            assertThat(response.isFirst()).isTrue();
            assertThat(response.isLast()).isTrue();
            assertThat(response.isEmpty()).isFalse();
        }

        @Test
        @DisplayName("Should create empty paged response")
        void testCreatePagedResponseEmpty() {
            Page<String> emptyPage = new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 10), 0);

            PagedResponse<String> response = paginationHelper.createPagedResponse(
                    emptyPage, str -> str.toUpperCase());

            assertThat(response.getContent()).isEmpty();
            assertThat(response.getTotalElements()).isEqualTo(0);
            assertThat(response.getTotalPages()).isEqualTo(0);
            assertThat(response.isEmpty()).isTrue();
        }

        @Test
        @DisplayName("Should create paged response for middle page")
        void testCreatePagedResponseMiddlePage() {
            List<String> content = Arrays.asList("item1", "item2");
            Page<String> page = new PageImpl<>(content, PageRequest.of(1, 2), 6);

            PagedResponse<String> response = paginationHelper.createPagedResponse(
                    page, str -> str);

            assertThat(response.getPage()).isEqualTo(1);
            assertThat(response.getSize()).isEqualTo(2);
            assertThat(response.getTotalElements()).isEqualTo(6);
            assertThat(response.getTotalPages()).isEqualTo(3);
            assertThat(response.isFirst()).isFalse();
            assertThat(response.isLast()).isFalse();
        }

        @Test
        @DisplayName("Should apply mapper function correctly")
        void testCreatePagedResponseAppliesMapper() {
            List<Integer> numbers = Arrays.asList(1, 2, 3);
            Page<Integer> page = new PageImpl<>(numbers, PageRequest.of(0, 10), 3);

            PagedResponse<String> response = paginationHelper.createPagedResponse(
                    page, num -> "Number: " + num);

            assertThat(response.getContent())
                    .containsExactly("Number: 1", "Number: 2", "Number: 3");
        }
    }

    @Nested
    @DisplayName("Getter Tests")
    class GetterTests {

        @Test
        @DisplayName("Should return max page size of 100")
        void testGetMaxPageSize() {
            assertThat(paginationHelper.getMaxPageSize()).isEqualTo(100);
        }

        @Test
        @DisplayName("Should return default page size of 20")
        void testGetDefaultPageSize() {
            assertThat(paginationHelper.getDefaultPageSize()).isEqualTo(20);
        }
    }
}
