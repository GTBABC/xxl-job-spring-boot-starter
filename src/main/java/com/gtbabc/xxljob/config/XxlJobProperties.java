package com.gtbabc.xxljob.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * xxl-job 配置属性
 */
@ConfigurationProperties(prefix = XxlJobProperties.PREFIX)
@Data
public class XxlJobProperties {
    public static final String PREFIX = "xxl.job";

    /**
     * 开启自动装配
     */
    private boolean enabled = true;

    /**
     * xxl-job-admin 配置
     */
    private Admin admin;

    /**
     * 执行器配置
     */
    private Executor executor;

    @Data
    public static class Admin {
        /**
         * xxl-job-admin server 地址
         */
        private String addresses;

        /**
         * xxl-job accessToken
         */
        private String accessToken;

        /**
         * 登录用户名
         */
        private String username;

        /**
         * 登录密码
         */
        private String password;
    }

    @Data
    public static class Executor {
        /**
         * 执行器应用名称
         */
        private String appname;

        /**
         * 执行器地址
         */
        private String address;

        /**
         * 执行器IP
         */
        private String ip;

        /**
         * 执行器端口号
         */
        private int port;

        /**
         * 执行器日志路径
         */
        private String logpath;

        /**
         * 执行器日志保留天数
         */
        private int logretentiondays = 30;

        /**
         * 执行器标题
         */
        private String title;

        /**
         * 执行器地址类型 0=自动注册、1=手动录入
         */
        private int addressType = 0;

        /**
         * 执行器地址列表，多地址逗号分隔(手动录入)
         */
        private String addressList;
    }
}
