package com.gtbabc.xxljob.config;

import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;


/**
 * xxl-job 自动装配
 */
@AutoConfiguration
@ConditionalOnClass(XxlJobSpringExecutor.class)
@ConditionalOnProperty(prefix = XxlJobProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(XxlJobProperties.class)
@ComponentScan(basePackages = "com.gtbabc.xxljob")
public class XxlJobAutoConfig {
    private final XxlJobProperties properties;

    public XxlJobAutoConfig(XxlJobProperties properties) {
        this.properties = properties;
    }

    @Bean
    @ConditionalOnMissingBean(XxlJobSpringExecutor.class)
    public XxlJobSpringExecutor xxlJobExecutor() {
        XxlJobSpringExecutor xxlJobSpringExecutor = new XxlJobSpringExecutor();
        xxlJobSpringExecutor.setAdminAddresses(properties.getAdmin().getAddresses());
        xxlJobSpringExecutor.setAppname(properties.getExecutor().getAppname());
        xxlJobSpringExecutor.setAddress(properties.getExecutor().getAddress());
        xxlJobSpringExecutor.setIp(properties.getExecutor().getIp());
        xxlJobSpringExecutor.setPort(properties.getExecutor().getPort());
        xxlJobSpringExecutor.setAccessToken(properties.getAdmin().getAccessToken());
        xxlJobSpringExecutor.setLogPath(properties.getExecutor().getLogpath());
        xxlJobSpringExecutor.setLogRetentionDays(properties.getExecutor().getLogretentiondays());
        return xxlJobSpringExecutor;
    }

}
