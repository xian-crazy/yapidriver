package com.crazy.autotest.yapidriver.yapiwatcher.bean;
import lombok.Data;

/**
 * yapi 测试用例infobean
 */
@Data
public class CaseBean {
    private int index;
    private int _id;
    private int uid;
    private int project_id;
    private int col_id;
    private int interface_id;
    private String casename;
    private String path;
    private String project_name;
    private String group_name;
}
