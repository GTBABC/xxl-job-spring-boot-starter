package com.gtbabc.xxljob.response;

import lombok.Data;

import java.util.List;

@Data
public class XxlJobAdminPageModel<T> {
    /**
     * page offset
     */
    private int offset;
    /**
     * page size
     */
    private int pagesize;
    /**
     * page data
     */
    private List<T> data;
    /**
     * total records
     */
    private int total;
}
