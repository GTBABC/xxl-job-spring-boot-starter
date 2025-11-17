package com.gtbabc.xxljob.response;

import lombok.Data;

@Data
public class XxlJobAdminResponse<T> {
    private int code;

    private String msg;

    private T data;
}
