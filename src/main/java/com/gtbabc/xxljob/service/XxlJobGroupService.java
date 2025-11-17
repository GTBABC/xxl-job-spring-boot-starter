package com.gtbabc.xxljob.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gtbabc.xxljob.config.XxlJobProperties;
import com.gtbabc.xxljob.model.XxlJobGroup;
import com.gtbabc.xxljob.response.XxlJobAdminPageModel;
import com.gtbabc.xxljob.response.XxlJobAdminResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * xxl-job group service
 */
@Service
@Slf4j
public class XxlJobGroupService {
    @Autowired
    private XxlJobWebClient xxlJobWebClient;

    @Autowired
    private XxlJobProperties xxlJobProperties;

    @Autowired
    private ObjectMapper objectMapper;

    public XxlJobGroup getXxlJobGroup() {
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("appname", xxlJobProperties.getExecutor().getAppname());
        queryParams.add("title", xxlJobProperties.getExecutor().getTitle());
        try {
            String result = xxlJobWebClient.post("/jobgroup/pageList", queryParams, Map.of(), MediaType.APPLICATION_FORM_URLENCODED);
            XxlJobAdminResponse<XxlJobAdminPageModel<XxlJobGroup>> response = objectMapper.readValue(result, new TypeReference<>() {
            });
            List<XxlJobGroup> xxlJobGroups = response.getData().getData();
            // 模糊查询，返回第一个匹配的
            if (!xxlJobGroups.isEmpty()) {
                for (XxlJobGroup xxlJobGroup : xxlJobGroups) {
                    if (xxlJobGroup.getAppname().equals(xxlJobProperties.getExecutor().getAppname()) && xxlJobGroup.getTitle().equals(xxlJobProperties.getExecutor().getTitle())) {
                        return xxlJobGroup;
                    }
                }
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException("get job group list error", e);
        }
    }

    public boolean isExistXxlJobGroup() {
        return getXxlJobGroup() != null;
    }

    public void autoRegisterXxlJobGroup() {
        int addressType = xxlJobProperties.getExecutor().getAddressType();
        String addressList = xxlJobProperties.getExecutor().getAddressList();
        MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("appname", xxlJobProperties.getExecutor().getAppname());
        requestParams.add("title", xxlJobProperties.getExecutor().getTitle());
        requestParams.add("addressType", String.valueOf(addressType));
        if (addressType == 1) {
            if (!StringUtils.hasText(addressList)) {
                throw new RuntimeException("In manual entry mode, the executor address list cannot be empty.");
            }
            requestParams.add("addressList", addressList);
        }
        try {
            String result = xxlJobWebClient.post("/jobgroup/save", requestParams, Map.of(), MediaType.APPLICATION_FORM_URLENCODED);
            XxlJobAdminResponse<String> response = objectMapper.readValue(result, new TypeReference<>() {
            });
            if (response.getCode() != 200) {
                throw new RuntimeException("xxl-job auto register job group fail! Msg:" + response.getMsg());
            }
            log.info("xxl-job auto register job group success!");
        } catch (Exception e) {
            throw new RuntimeException("Auto register job group error", e);
        }
    }
}
