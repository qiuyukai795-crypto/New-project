package com.example.dianping.model;

import java.io.Serializable;
import java.util.List;

public record PagedResult<T>(
        long page,
        long pageSize,
        long total,
        long totalPages,
        List<T> records
) implements Serializable {
    private static final long serialVersionUID = 1L;
}
