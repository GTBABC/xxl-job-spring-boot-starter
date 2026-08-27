package com.gtbabc.xxljob.config;

import com.gtbabc.xxljob.service.XxlJobGroupService;
import com.gtbabc.xxljob.service.XxlJobInfoService;
import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.jackson.autoconfigure.JacksonAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import tools.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class XxlJobAutoConfigTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(JacksonAutoConfiguration.class, XxlJobAutoConfig.class))
            .withBean(XxlJobSpringExecutor.class, () -> mock(XxlJobSpringExecutor.class))
            .withPropertyValues(
                    "xxl.job.enabled=true",
                    "xxl.job.admin.addresses=http://localhost:8080",
                    "xxl.job.executor.appname=test-executor",
                    "xxl.job.executor.title=Test Executor"
            );

    @Test
    void loadsBoot4AutoConfigurationWithJackson3() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(ObjectMapper.class);
            assertThat(context).hasSingleBean(XxlJobGroupService.class);
            assertThat(context).hasSingleBean(XxlJobInfoService.class);
        });
    }

    @Test
    void backsOffWhenDisabled() {
        contextRunner
                .withPropertyValues("xxl.job.enabled=false")
                .run(context -> {
                    assertThat(context).doesNotHaveBean(XxlJobGroupService.class);
                    assertThat(context).doesNotHaveBean(XxlJobInfoService.class);
                });
    }
}
