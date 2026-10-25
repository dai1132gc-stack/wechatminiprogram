package com.rentalplatform.common.enums;

/** 监测指标：仅 HCHO/TVOC/CO2 参与超标判定，温湿度不判超标（技术方案 §12.3）。 */
public enum MetricType {
    HCHO,
    TVOC,
    CO2,
    TEMPERATURE,
    HUMIDITY
}
