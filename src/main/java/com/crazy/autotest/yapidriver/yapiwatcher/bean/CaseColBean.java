package com.crazy.autotest.yapidriver.yapiwatcher.bean;
import lombok.Data;

import java.util.List;

/**
 * yapi 项目测试用例集合bean
 */
@Data
public class CaseColBean {
    private int index;
    private int _id;
    private String name;
    private int project_id;
    private String desc;
    private int uid;
    private long add_time;
    private List<CaseBean> caseList;
}
