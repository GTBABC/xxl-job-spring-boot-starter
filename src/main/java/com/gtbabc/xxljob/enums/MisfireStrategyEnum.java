package com.gtbabc.xxljob.enums;

/**
 * 调度过期策略
 * 忽略: DO_NOTHING
 * 立即执行一次: FIRE_ONCE_NOW
 */
public enum MisfireStrategyEnum {

    /**
     * 忽略
     */
    DO_NOTHING("DO_NOTHING", "忽略"),

    /**
     * 立即执行一次
     */
    FIRE_ONCE_NOW("FIRE_ONCE_NOW", "立即执行一次");

    /**
     * 传递/存储用的值
     */
    private final String value;

    /**
     * 中文描述
     */
    private final String desc;

    MisfireStrategyEnum(String value, String desc) {
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
    public static MisfireStrategyEnum of(String value) {
        if (value == null) {
            return null;
        }
        for (MisfireStrategyEnum item : values()) {
            if (item.value.equals(value)) {
                return item;
            }
        }
        return null;
    }
}
