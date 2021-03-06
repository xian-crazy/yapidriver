package com.crazy.autotest.yapidriver.yapiwatcher.bean;
import com.alibaba.fastjson.JSON;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

@Data
public class ERB {
    protected static Logger log  = LoggerFactory.getLogger(ERB.class);



    private int code;
    private Map<String, String> headers;
    private int id;
    private String method;
    private String name;
    private String params;
    private String path;
    private String res_body;
    private String res_header;
    private int status;
    private String url;
    private List<Map<String, String>> validRes;
//    private String spaceName;
//    private String projectName;
//    private int projectId;
    private ProjectBean project;
    private String caseColName;
    private CaseBean caze;
    private String env;
    private String data;
    private InterFaceBean interFaceBean;
    private String author;
//    private int interfaceid;
//    private String res_bodySchema;
//    private int interfaceProjectid;

    public String toString(){
        return "环境："+this.env+" Id:"+this.id+" 用例名"+this.name+"  URL"+this.url ;
    }

    public void setRes_header(Object obj){
        this.res_header= JSON.toJSONString(obj);
    }

    public String getAuthor(){
        return project.getSpace().getUserBeanMap().get(caze.getUid()).getUsername();
    }
    public void setRes_body(Object obj){
        res_body=  JSON.toJSONString(obj);

    }
    public void setData(Object obj){
        data=  JSON.toJSONString(obj);
    }
    public void setParams(Object obj){
        params=  JSON.toJSONString(obj);
    }

    }



