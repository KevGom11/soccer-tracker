package com.kevin.soccertracker.api;

import java.util.List;

public record ApiPage<T>(
        List<T> data,
        long count,     // total elements across all pages
        int page,       // zero-based page index
        int size,       // page size requested
        int totalPages  // derived: ceil(count / size)
) {
    public static <T> ApiPage<T> of(List<T> data, long count, int page, int size) {
        int safePage = Math.max(0, page);
        int safeSize = Math.max(1, size);
        int totalPages = (count == 0) ? 0 : (int) Math.ceil((count * 1.0) / safeSize);
        return new ApiPage<>(data, count, safePage, safeSize, totalPages);
    }
}
