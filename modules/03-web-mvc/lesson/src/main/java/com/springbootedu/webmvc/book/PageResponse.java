package com.springbootedu.webmvc.book;

import java.util.List;

/**
 * Lesson 3.4 — one page of results plus what a client needs to build paging links.
 */
public record PageResponse<T>(List<T> content, int page, int size, long totalElements, int totalPages) {
}
