package com.gtbabc.xxljob.service;

import com.gtbabc.xxljob.config.XxlJobProperties;
import com.gtbabc.xxljob.model.XxlJobInfo;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class XxlJobAdminEndpointTest {

    @Test
    void usesXxlJob34InsertEndpointForJobGroup() {
        XxlJobWebClient client = mock(XxlJobWebClient.class);
        XxlJobGroupService service = new XxlJobGroupService();
        ReflectionTestUtils.setField(service, "xxlJobWebClient", client);
        ReflectionTestUtils.setField(service, "xxlJobProperties", properties());
        ReflectionTestUtils.setField(service, "objectMapper", new tools.jackson.databind.ObjectMapper());
        when(client.post(eq("/jobgroup/insert"), any(), eq(Map.of()), eq(MediaType.APPLICATION_FORM_URLENCODED)))
                .thenReturn("{\"code\":200,\"data\":null}");

        service.autoRegisterXxlJobGroup();

        verify(client).post(eq("/jobgroup/insert"), any(), eq(Map.of()), eq(MediaType.APPLICATION_FORM_URLENCODED));
    }

    @Test
    void usesXxlJob34InsertEndpointForJobInfo() {
        XxlJobWebClient client = mock(XxlJobWebClient.class);
        XxlJobInfoService service = new XxlJobInfoService();
        ReflectionTestUtils.setField(service, "xxlJobWebClient", client);
        ReflectionTestUtils.setField(service, "objectMapper", new tools.jackson.databind.ObjectMapper());
        when(client.post(eq("/jobinfo/insert"), any(), eq(Map.of()), eq(MediaType.APPLICATION_FORM_URLENCODED)))
                .thenReturn("{\"code\":200,\"data\":\"1\"}");

        service.autoRegisterXxlJobInfo(jobInfo());

        ArgumentCaptor<Object> request = ArgumentCaptor.forClass(Object.class);
        verify(client).post(eq("/jobinfo/insert"), request.capture(), eq(Map.of()), eq(MediaType.APPLICATION_FORM_URLENCODED));
        assertThat(request.getValue()).isNotNull();
    }

    private static XxlJobProperties properties() {
        XxlJobProperties properties = new XxlJobProperties();
        XxlJobProperties.Executor executor = new XxlJobProperties.Executor();
        executor.setAppname("test-executor");
        executor.setTitle("Test Executor");
        properties.setExecutor(executor);
        return properties;
    }

    private static XxlJobInfo jobInfo() {
        XxlJobInfo info = new XxlJobInfo();
        info.setJobGroup(1);
        info.setJobDesc("test");
        info.setAuthor("codex");
        info.setScheduleType("NONE");
        info.setGlueType("BEAN");
        info.setExecutorHandler("testHandler");
        info.setExecutorRouteStrategy("FIRST");
        info.setMisfireStrategy("DO_NOTHING");
        info.setExecutorBlockStrategy("SERIAL_EXECUTION");
        return info;
    }
}
