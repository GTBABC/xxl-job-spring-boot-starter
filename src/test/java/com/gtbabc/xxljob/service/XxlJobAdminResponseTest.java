package com.gtbabc.xxljob.service;

import com.gtbabc.xxljob.model.XxlJobGroup;
import com.gtbabc.xxljob.response.XxlJobAdminPageModel;
import com.gtbabc.xxljob.response.XxlJobAdminResponse;
import org.junit.jupiter.api.Test;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;

class XxlJobAdminResponseTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void deserializesAdminPageWithJackson3() throws Exception {
        String json = """
                {
                  "code": 200,
                  "msg": null,
                  "data": {
                    "offset": 0,
                    "pagesize": 10,
                    "total": 1,
                    "data": [
                      {
                        "id": 1,
                        "appname": "vnetd-core",
                        "title": "VNetD Core",
                        "addressType": 0
                      }
                    ]
                  }
                }
                """;

        XxlJobAdminResponse<XxlJobAdminPageModel<XxlJobGroup>> response =
                objectMapper.readValue(json, new TypeReference<>() {
                });

        assertThat(response.getCode()).isEqualTo(200);
        assertThat(response.getData().getData()).singleElement()
                .satisfies(group -> {
                    assertThat(group.getId()).isEqualTo(1);
                    assertThat(group.getAppname()).isEqualTo("vnetd-core");
                });
    }
}
