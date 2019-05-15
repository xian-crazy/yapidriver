package com.crazy.autotest.yapidriver.yapiwatcher.bean;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * yapi 项目环境配置bean
 */
@Data
public class EnvBean {
    private List<Map<String,String>> header;
    private List<Map<String,String>> global;
    private String _id;
    private String name;
    private String domain;

}
