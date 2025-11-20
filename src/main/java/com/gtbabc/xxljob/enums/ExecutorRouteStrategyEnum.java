package com.gtbabc.xxljob.enums;

/**
 * 执行器路由策略
 */
public enum ExecutorRouteStrategyEnum {

    FIRST("FIRST", "第一个"),
    LAST("LAST", "最后一个"),
    ROUND("ROUND", "轮询"),
    RANDOM("RANDOM", "随机"),
    CONSISTENT_HASH("CONSISTENT_HASH", "一致性HASH"),
    LEAST_FREQUENTLY_USED("LEAST_FREQUENTLY_USED", "最不经常使用"),
    LEAST_RECENTLY_USED("LEAST_RECENTLY_USED", "最近最久未使用"),
    FAILOVER("FAILOVER", "故障转移"),
    BUSYOVER("BUSYOVER", "忙碌转移"),
    SHARDING_BROADCAST("SHARDING_BROADCAST", "分片广播");

    /**
     * 传给前端/存数据库用的值
     */
    private final String value;

    /**
     * 中文描述
     */
    private final String desc;

    ExecutorRouteStrategyEnum(String value, String desc) {
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
    public static ExecutorRouteStrategyEnum of(String value) {
        if (value == null) {
            return null;
        }
        for (ExecutorRouteStrategyEnum item : values()) {
            if (item.value.equals(value)) {
                return item;
            }
        }
        return null;
    }
}
