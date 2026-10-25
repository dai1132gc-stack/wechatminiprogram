package com.rentalplatform.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 数据质量标记（固件 Algorithm/quality.c 同步判定）：
 * 前端表现见《设计方案》§1.1 —— 补传画虚线、越界灰点、预热断线等。
 */
@Getter
@AllArgsConstructor
public enum QualityFlag {

    NORMAL(0, "正常"),
    BACKFILL(1, "补传"),
    OUT_OF_RANGE(2, "越界"),
    JUMP(3, "跳变"),
    SENSOR_FAULT(4, "传感器故障"),
    STALE(5, "数据陈旧"),
    WARMING(6, "预热中");

    private final int code;
    private final String label;
}
