package com.rentalplatform.common.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 分页响应体：字段固定为 list / total / pageNum / pageSize / pages，
 * 与 OpenAPI（server/docs/openapi.yaml）中 *Page schema 一致，前端禁止另行命名。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> {

    private List<T> list;
    private long total;
    private long pageNum;
    private long pageSize;
    private long pages;

    public static <T> PageResult<T> of(List<T> list, long total, long pageNum, long pageSize) {
        long pages = pageSize == 0 ? 0 : (total + pageSize - 1) / pageSize;
        return new PageResult<>(list, total, pageNum, pageSize, pages);
    }
}
