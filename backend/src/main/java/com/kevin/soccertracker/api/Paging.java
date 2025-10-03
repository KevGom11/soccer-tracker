package com.kevin.soccertracker.api;

import java.util.Collections;
import java.util.List;

public final class Paging {
    private Paging() {}

    public static <T> ApiPage<T> slice(List<T> fullList, int page, int size) {
        int safePage = Math.max(0, page);
        int safeSize = Math.max(1, size);

        long count = fullList.size();
        int from = Math.min(safePage * safeSize, (int) count);
        int to = Math.min(from + safeSize, (int) count);

        List<T> slice = (from >= to) ? Collections.emptyList() : fullList.subList(from, to);
        return ApiPage.of(slice, count, safePage, safeSize);
    }
}
