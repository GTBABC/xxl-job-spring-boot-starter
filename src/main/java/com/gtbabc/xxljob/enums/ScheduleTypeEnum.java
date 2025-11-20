package com.gtbabc.xxljob.enums;

/**
 * 调度类型
 * 无: NONE
 * CRON 表达式: CRON
 * 固定频率(秒): FIX_RATE
 */
public enum ScheduleTypeEnum {

    /**
     * 不调度
     */
    NONE("NONE", "无"),

    /**
     * 按 CRON 表达式调度
     */
    CRON("CRON", "CRON 表达式"),

    /**
     * 固定频率（单位：秒）
     */
    FIX_RATE("FIX_RATE", "固定频率(秒)");

    /**
     * 传递/存储用的值
     */
    private final String value;

    /**
     * 中文描述
     */
    private final String desc;

    ScheduleTypeEnum(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public String getValue() {
        return value;
    }

    public String getDesc() {
        return desc;
    }

    /**
     * 根据 value 反查枚举
     */
    public static ScheduleTypeEnum of(String value) {
        if (value == null) {
            return null;
        }
        for (ScheduleTypeEnum item : values()) {
            if (item.value.equals(value)) {
                return item;
            }
        }
        return null;
    }
}
