package com.gtbabc.xxljob.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gtbabc.xxljob.model.XxlJobInfo;
import com.gtbabc.xxljob.response.XxlJobAdminPageModel;
import com.gtbabc.xxljob.response.XxlJobAdminResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.Map;

/**
 * xxl-job group service
 */
@Service
@Slf4j
public class XxlJobInfoService {
    @Autowired
    private XxlJobWebClient xxlJobWebClient;

    @Autowired
    private ObjectMapper objectMapper;

    public XxlJobInfo getXxlJobInfo(int jobGroupId, String executorHandler) {
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("jobGroup", String.valueOf(jobGroupId));
        queryParams.add("executorHandler", executorHandler);
        queryParams.add("triggerStatus", "-1");
        queryParams.add("jobDesc", null);
        queryParams.add("author", null);
        try {
            String result = xxlJobWebClient.post("/jobinfo/pageList", queryParams, Map.of(), MediaType.APPLICATION_FORM_URLENCODED);
            XxlJobAdminResponse<XxlJobAdminPageModel<XxlJobInfo>> response = objectMapper.readValue(result, new TypeReference<>() {
            });
            List<XxlJobInfo> xxlJobInfos = response.getData().getData();
            // 模糊查询，返回第一个匹配的
            if (!xxlJobInfos.isEmpty()) {
                for (XxlJobInfo xxlJobInfo : xxlJobInfos) {
                    if (xxlJobInfo.getExecutorHandler().equals(executorHandler)) {
                        return xxlJobInfo;
                    }
                }
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException("get job info list error", e);
        }
    }

    public boolean isExistXxlJobInfo(int jobGroupId, String executorHandler) {
        return getXxlJobInfo(jobGroupId, executorHandler) != null;
    }

    public void autoRegisterXxlJobInfo(XxlJobInfo xxlJobInfo) {
        MultiValueMap<String, String> requestParams = convertToFormData(xxlJobInfo);
        try {
            String result = xxlJobWebClient.post("/jobinfo/add", requestParams, Map.of(), MediaType.APPLICATION_FORM_URLENCODED);
            XxlJobAdminResponse<String> response = objectMapper.readValue(result, new TypeReference<>() {
            });
            if (response.getCode() != 200) {
                throw new RuntimeException("xxl-job auto register job info fail! Msg:" + response.getMsg());
            }
            log.info("xxl-job auto register job info success! task id:{}", response.getData());
        } catch (Exception e) {
            throw new RuntimeException("Auto register job info error", e);
        }
    }

    private MultiValueMap<String, String> convertToFormData(XxlJobInfo xxlJobInfo) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();

        formData.add("jobGroup", String.valueOf(xxlJobInfo.getJobGroup()));
        formData.add("jobDesc", xxlJobInfo.getJobDesc());
        formData.add("author", xxlJobInfo.getAuthor());
        formData.add("scheduleType", xxlJobInfo.getScheduleType());
        formData.add("scheduleConf", xxlJobInfo.getScheduleConf());
        formData.add("glueType", xxlJobInfo.getGlueType());
        formData.add("executorHandler", xxlJobInfo.getExecutorHandler());
        formData.add("executorRouteStrategy", xxlJobInfo.getExecutorRouteStrategy());
        formData.add("misfireStrategy", xxlJobInfo.getMisfireStrategy());
        formData.add("executorBlockStrategy", xxlJobInfo.getExecutorBlockStrategy());
        formData.add("executorTimeout", String.valueOf(xxlJobInfo.getExecutorTimeout()));
        formData.add("executorFailRetryCount", String.valueOf(xxlJobInfo.getExecutorFailRetryCount()));
        formData.add("glueRemark", xxlJobInfo.getGlueRemark());
        formData.add("triggerStatus", String.valueOf(xxlJobInfo.getTriggerStatus()));

        return formData;
    }
}
