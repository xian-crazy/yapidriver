package com.crazy.autotest.yapidriver.yapiwatcher;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.NameFilter;
import com.crazy.autotest.yapidriver.tools.BeanUtils;
import com.crazy.autotest.yapidriver.tools.Bt;
import com.crazy.autotest.yapidriver.yapiwatcher.bean.*;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.http.Cookies;
import io.restassured.response.Response;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import java.util.*;
import java.util.stream.Collectors;

public class Yapi {
    private static Logger LOG = LoggerFactory.getLogger(Yapi.class);
    private static Properties properties = Bt.GetProv("/config/yapi/projectinfo.properties");
    public static String yapiaddr = properties.getProperty("yapi.addr").trim();
    public static final String RESPONSE_NOT_JSON="response_not_json";

    public static NameFilter filter=new NameFilter() {
        @Override
        public String process(Object object, String name, Object value) {
           if(object instanceof  EnvBean) {
               if ("id".equals(name)) {
                   return "_id";
               }
           }
            return name;
        }
    };

    private static Cookies cookies=login();


    @Step("获取待测试监控空间")
    public  List<SpaceBean> intiSpaceList(){
        LOG.info("获取待测试监控空间");
        List<SpaceBean> spaceBeans=this.getSpace();
        List<SpaceBean> spaceBeansRes= new ArrayList<>();
        List<Integer> spaceIdlist=new ArrayList<>();
        String spaceIds=System.getProperty("spaceIds");
        if(StringUtils.isEmpty(spaceIds)){
            spaceIds=properties.getProperty("spaceIds");
        }
         List<String> spaceIdlistStr=Arrays.asList(spaceIds.split(","));
            for(String id:spaceIdlistStr){
                spaceIdlist.add(Integer.parseInt(id.trim()));
            }


        for(SpaceBean spaceBean:spaceBeans){
            if(spaceIdlist.contains(spaceBean.get_id())){
                spaceBeansRes.add(spaceBean);
               // LOG.error(spaceBean.getGroup_name()+":"+spaceBean.get_id());
            }
        }

        return  spaceBeansRes;
    }



    @Step("获取项目基本信息")
    private void setProjectFullInfo(ProjectBean projectBean){
        LOG.info("获取项目基本信息");
        Response res=RestAssured
                .given()
                .param("token",projectBean.getToken())
                .when().get("http://"+yapiaddr+"/api/project/get");
        res.then().log().all();
        Assert.assertEquals(res.body().jsonPath().getInt("errcode"),0,"获取项目基本信息"+res.body().jsonPath().getString("errmsg"));
        ProjectBean r = res.body().jsonPath().getObject("data",ProjectBean.class);
        //LOG.info(r.isIs_json5()+" ,"+r.isStrice());
        BeanUtils.merge(projectBean,r);
//        LOG.info(projectBean.isIs_json5()+" ,"+projectBean.isStrice());

    }

    @Step("获取项目接口分类列表")
    private  List<CatMenuBean> getProjectCatMenu(String projectToken){
        LOG.info("获取项目接口分类列表");
        Response res=RestAssured
                .given()
                .param("token",projectToken)
                .when().get("http://"+yapiaddr+"/api/interface/getCatMenu");
        res.then().log().all();
        Assert.assertEquals(res.body().jsonPath().getInt("errcode"),0,"获取项目接口分类列表"+res.body().jsonPath().getString("errmsg"));
       List<CatMenuBean> r = res.body().jsonPath().getList("data",CatMenuBean.class);
//        for(CatMenuBean catMenuBean:r){
//            quvAssert.assertFalse(StringUtils.isEmpty(catMenuBean.getDesc()),"验证分类 ["+catMenuBean.getName()+"] 描述是否为空");
//        }
        return r;
    }

    @Step("获取某个分类下接口列表")
    public  List<InterFaceBean> getCatApilist(String projectToken,int catid){
        LOG.info("获取某个分类下接口列表");

        Response res=RestAssured
                .given()
                .param("token",projectToken)
                .param("catid",catid)
                .param("page",1)
                .param("limit",Integer.MAX_VALUE)
                .when().get("http://"+yapiaddr+"/api/interface/list_cat");
        res.then().log().all();
        Assert.assertEquals(res.body().jsonPath().getInt("errcode"),0,"获取某个分类下接口列表"+res.body().jsonPath().getString("errmsg"));
        List<InterFaceBean> r = evalInterFaceBean(res);
        return r;
    }

    @Step("获取项目下接口列表")
    public  List<InterFaceBean> getProjectApilist(String projectToken,int project_id){
        LOG.info("获取项目下接口列表");

        Response res=RestAssured
                .given()
                .param("token",projectToken)
                .param("project_id",project_id)
                .param("page",1)
                .param("limit",Integer.MAX_VALUE)
                .when().get("http://"+yapiaddr+"/api/interface/list");
        res.then().log().all();
        Assert.assertEquals(res.body().jsonPath().getInt("errcode"),0,"获取项目下接口列表"+res.body().jsonPath().getString("errmsg"));
        List<InterFaceBean> r = evalInterFaceBean(res);
        return r;
    }
    private  List<InterFaceBean> evalInterFaceBean(Response res){
        List<InterFaceBean> r = res.body().jsonPath().getList("data.list",InterFaceBean.class);
//        for(InterFaceBean interFaceBean:r){
//            quvAssert.assertEquals(interFaceBean.getStatus(),"done","验证接口 ["+interFaceBean.getTitle()+"] 定义状态是否为已完成");
//        }
        return r;
    }


    @Step("获取分类及接口列表")
    public  List<CatMenuBean> getCatAndApilist(ProjectBean projectBean){
        LOG.info("获取分类及接口列表");

        String token=projectBean.getToken();
        Response res=RestAssured
                .given()
                .param("token",token)
                .param("project_id",projectBean.get_id())
                .param("page",1)
                .param("limit",Integer.MAX_VALUE)
                .when().get("http://"+yapiaddr+"/api/interface/list_menu");
        res.then().log().all();
        Assert.assertEquals(res.body().jsonPath().getInt("errcode"),0,"获取分类及接口列表"+res.body().jsonPath().getString("errmsg"));
        List<CatMenuBean> r = res.body().jsonPath().getList("data",CatMenuBean.class);
        for(CatMenuBean catMenuBean:r){
            for(InterFaceBean interFaceBean:catMenuBean.getList()) {
                this.setApiResBodySchema(token,interFaceBean);
//                interFaceBean.setProject_name(projectBean.getName());
                interFaceBean.setCat_name(catMenuBean.getName());
//                interFaceBean.setGroup_id(projectBean.getGroup_id());
//                interFaceBean.setGroup_name(projectBean.getSpace().getGroup_name());
                interFaceBean.setProject(projectBean);
            }
        }
        return r;
    }

    @Step("获取接口响应schema")
    private void setApiResBodySchema(String projectToken,InterFaceBean interFaceBean){
        LOG.info("获取接口响应schema");

        Response res=RestAssured
                .given().log().all()
                .param("token",projectToken)
                .param("id",interFaceBean.get_id())
                .when().get("http://"+yapiaddr+"/api/interface/get");
        res.then().log().all();
        Assert.assertEquals(res.body().jsonPath().getInt("errcode"),0,"获取接口响应schema"+res.body().jsonPath().getString("errmsg"));
//        quvAssert.assertTrue(res.body().jsonPath().getBoolean("data.res_body_is_json_schema"),"获取接口响应schema是否打开");
        String r = res.body().jsonPath().getString("data.res_body");
        interFaceBean.setResBody(r);
        interFaceBean.setRes_body_is_json_schema(res.body().jsonPath().getBoolean("data.res_body_is_json_schema"));
//        int/
      //  return interFaceBean;
    }


    private static Cookies login(){
        LOG.info("yapi监控账户登陆");
        String email=properties.getProperty("admin.Email");
        String pws=properties.getProperty("admin.pwd");

        Response res=RestAssured
                .given()
                .contentType("application/json;charset=UTF-8")
                .body("{\"email\":\""+email+"\",\"password\":\""+pws+"\"}")
                .when()
                .post("http://"+yapiaddr+"/api/user/login");
        Assert.assertEquals(res.body().jsonPath().getInt("errcode"),0,"登陆失败！");
        return res.getDetailedCookies();
    }

    @Step("获取项目测试用例集")
    public  List<CaseColBean> getCaseList(int project_id ){
        LOG.info("获取项目测试用例集");

        Response res=RestAssured
                .given()
                .cookies(cookies)
                .param("project_id",project_id)
                .when().get("http://"+yapiaddr+"/api/col/list");
        res.then().log().all();
        Assert.assertEquals(res.body().jsonPath().getInt("errcode"),0,"获取项目测试用例集"+res.body().jsonPath().getString("errmsg"));
        List<CaseColBean> caseColBeanList=res.body().jsonPath().getList("data", CaseColBean.class);
        return caseColBeanList;
    }

    @Step("获取autotest用户可见空间")
    private  List<SpaceBean> getSpace(){
        LOG.info("获取autotest用户可见空间");

        Response res=RestAssured
                .given()
                .cookies(cookies)
                .when().get("http://"+yapiaddr+"/api/group/list");
        res.then().log().all();
        Assert.assertEquals(res.body().jsonPath().getInt("errcode"),0,"获取autotest用户可见空间"+res.body().jsonPath().getString("errmsg"));
        List<SpaceBean> all = res.body().jsonPath().getList("data",SpaceBean.class);//包含有公共开放的空间
        List<SpaceBean> autotest =new ArrayList<>();
        for(SpaceBean spaceBean:all){
            if(spaceBean.getRole().equals("owner")){
                setSpaceMembers(spaceBean);
                autotest.add(spaceBean);//只有当可见空间添加了autotest用户 为组长 才加入监控范围
                           }
        }
        return autotest;
    }



    @Step("获取autotest用户可见空间下项目")
    public  List<ProjectBean> getSpaceProject(SpaceBean spaceBean){
        LOG.info("获取autotest用户可见空间下项目");

        Response res=RestAssured
                .given()
                .cookies(cookies)
                .param("group_id",spaceBean.get_id())
                .param("page",1)
                .param("limit",99999)
                .when().get("http://"+yapiaddr+"/api/project/list");
        res.then().log().all();
        Assert.assertEquals(res.body().jsonPath().getInt("errcode"),0,"获取autotest用户可见空间下的项目"+res.body().jsonPath().getString("errmsg"));
        List<ProjectBean> r = res.body().jsonPath().getList("data.list",ProjectBean.class);
        for(ProjectBean projectBean:r){
            projectBean.setSpace(spaceBean);
            this.setProjectToken(projectBean);
            setProjectFullInfo(projectBean);
            boolean isDtag=hasDefalutTag(projectBean);
            boolean hasMockqaEnv=checkAndCreateMockEnv(projectBean);
            if(!projectBean.isIs_json5()||!projectBean.isStrice()||!isDtag||!hasMockqaEnv) {
                setProjectDefaultConfig(projectBean);
            }
           //checkAndCreateMockEnv(projectBean);
            LOG.debug("{}",projectBean.toString());


        }
        return r;
    }

    private void setProjectDefaultConfig(ProjectBean projectBean){
        LOG.info("设置项目默认配置");
        //checkAndCreateMockEnv(projectBean);
        JSONObject up=new JSONObject();
        up.put("basepath",projectBean.getBasepath());
        up.put("env",projectBean.getEnv());
        up.put("group_id",projectBean.getGroup_id());
        up.put("id",projectBean.get_id());
        up.put("is_json5",true);
        up.put("name",projectBean.getName());
        up.put("project_type",projectBean.getProject_type());
        up.put("strice",true);
        up.put("switch_notice",projectBean.isSwitch_notice());
        up.put("tag",projectBean.getTag());
        Response res=RestAssured
                .given().log().all()
                .cookies(cookies)
                .contentType("application/json;charset=UTF-8")
                .body(JSON.toJSONString(up,filter))
                .when().post("http://"+yapiaddr+"/api/project/up");
        res.then().log().all();
        Assert.assertEquals(res.body().jsonPath().getInt("errcode"),0,"设置项目默认配置（开启mock严格模式，开启json5）"+res.body().jsonPath().getString("errmsg"));
        projectBean.setIs_json5(true);
        projectBean.setStrice(true);
    }

    private boolean hasDefalutTag(ProjectBean projectBean){
        List<Map<String,String>> tags=  projectBean.getTag();
        boolean isDefalutTag=false;
        for(Map tag:tags){
            if(tag.get("name").equals(RESPONSE_NOT_JSON)){
                isDefalutTag=true;
                break;
            }
        }
        if(!isDefalutTag){
            Map<String,String> dtag=new HashMap<>();
            dtag.put("name",RESPONSE_NOT_JSON);
            dtag.put("desc","响应非json格式");
            tags.add(dtag);
        }
        LOG.info("isDefalutTag? "+isDefalutTag);
        return isDefalutTag;
    }
    private boolean checkAndCreateMockEnv(ProjectBean projectBean){
        boolean isMockEnv=false;
        boolean isqaEnv=false;
        List<EnvBean> envBeanList =projectBean.getEnv();
        String mockDomain="http://"+Yapi.yapiaddr+"/mock/"+projectBean.get_id();
        Map<String,EnvBean> envName=new HashMap<>();
        for(EnvBean envBean:envBeanList){
            envName.put(envBean.getName().trim().toLowerCase(),envBean);
        }

        if(envName.containsKey("mock")){
            if(!envName.get("mock").getDomain().equals(mockDomain)) {//,"mock环境地址是否配置正确");//http://restapi.quvideo.com/mock/128"
                envName.get("mock").setDomain(mockDomain);
            }else {
                isMockEnv=true;
            }
        }else{
            EnvBean mockenvBean=new EnvBean();
            mockenvBean.setName("mock");
            mockenvBean.setDomain(mockDomain);
            mockenvBean.setHeader(new ArrayList<>());
            mockenvBean.setGlobal(new ArrayList<>());
            envBeanList.add(mockenvBean);
        }

        if(envName.containsKey("qa")){
            if(!envName.get("qa").equals("qa")) {
                envName.get("qa").setName("qa");//将环境配置【qa】转换为小写
            }else {
                 isqaEnv=true;
            }
        }


        projectBean.setEnv(envBeanList);
       // setMockEnv(projectBean.get_id(),envBeanList);
        LOG.info("isMockEnv? "+isMockEnv);
        return isMockEnv&&isqaEnv;
    }


    /**
     *  使用setProjectDefaultConfig 方法统一设置
     * @param projectid
     * @param envBeanList
     */
    @Deprecated
    private void setMockEnv(int projectid,List<EnvBean> envBeanList){
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("id",projectid);
        jsonObject.put("env",envBeanList);
        Response res=RestAssured
                .given().log().all()
                .contentType("application/json;charset=UTF-8")
                .cookies(cookies)
                .body(JSON.toJSONString(jsonObject,filter))
                .when().post("http://"+yapiaddr+"/api/project/up_env");
        res.then().log().all();
        Assert.assertEquals(res.body().jsonPath().getInt("errcode"),0,"添加/修改mock环境是否成功");
    }
    @Step("获取项目token")//yapi当前的权限bug，只要项目被当前用户可见,即可获得项目的tocken
    private  void setProjectToken(ProjectBean projectBean){
        LOG.info("获取空间  "+projectBean.getSpace().getGroup_name()+"  下的项目"+projectBean.getName()+"token");
        Response res=RestAssured
                .given()
                .cookies(cookies)
                .param("project_id",projectBean.get_id())
                .when().get("http://"+yapiaddr+"/api/project/token");
        res.then().log().all();
        Assert.assertEquals(res.body().jsonPath().getInt("errcode"),0,"获取项目token:"+res.body().jsonPath().getString("errmsg"));
        String r = res.body().jsonPath().getString("data");
        projectBean.setToken(r);
    }



    @Step("执行测试用例集")
    public List<ERB> excutCaseCols(ProjectBean projectbean, CaseColBean caseColBean, String token, Map<Integer,InterFaceBean> allInterFaceBeans,String env){
//        http://restapi.quvideo.com/api/open/run_auto_test?id=643&token=1b6a2e1b684bc3c727529676bcaac53888cac66a984fbe7b405dc6141459ac1f&env_11=qa&mode=json&email=false&download=false
        LOG.info("执行测试用例集");
        List<ERB> r=new ArrayList<>();
        List<EnvBean> envBeans=projectbean.getEnv();
        boolean iscontainEnv=false;
        for(EnvBean envBean:envBeans){
            iscontainEnv= envBean.getName().equals(env);
            if(iscontainEnv){
                Response res=RestAssured
                        .given()
                        .param("id",caseColBean.get_id())
                        .param("token",token)
                        .param("env_"+projectbean.get_id(),env)
                        .param("mode","json")
                        .param("email",false)
                        .param("download",false)
                        .when().get("http://"+yapiaddr+"/api/open/run_auto_test");
                res.then().log().all();
                 r = res.body().jsonPath().getList("list", ERB.class);
                List<CaseBean> caseBeans= caseColBean.getCaseList();
                Map<Integer,CaseBean> maps=new HashMap<>();
                for(CaseBean caseBean:caseBeans){
                    maps.put(caseBean.get_id(),caseBean);
                }
                int erbInterface_id=0;
                CaseBean caseBean=null;
                for(ERB excutResBean:r){
                    caseBean=maps.get(excutResBean.getId());
                    erbInterface_id=caseBean.getInterface_id();
//                    excutResBean.setSpaceName(projectbean.getSpaceBean().getGroup_name());
//                    excutResBean.setProjectName(projectbean.getName());
//                    excutResBean.setProjectId(projectbean.get_id());
                    excutResBean.setProject(projectbean);
                    excutResBean.setCaseColName(caseColBean.getName());
                    excutResBean.setCaze(caseBean);
                    excutResBean.setEnv(env);
                    excutResBean.setInterFaceBean(allInterFaceBeans.get(erbInterface_id));
                }
                return r;
            }
        }
        LOG.error("当前项目"+projectbean.getName()+"不包含"+env+"环境，请尽快配置");

        return r;
    }

    @Step("获取当前空间成员列表")
    public void setSpaceMembers(SpaceBean spaceBean){
        LOG.info("获取空间  "+spaceBean.getGroup_name()+" 成员列表");
        Response res=RestAssured
                .given()
                .cookies(cookies)
                .param("id",spaceBean.get_id())
                .when().get("http://"+yapiaddr+"/api/group/get_member_list");
        res.then().log().all();
        Assert.assertEquals(res.body().jsonPath().getInt("errcode"),0,"获取空间  "+spaceBean.getGroup_name()+" 成员列表:"+res.body().jsonPath().getString("errmsg"));
        List<UserBean> result = res.body().jsonPath().getList("data",UserBean.class);
       Map<Integer,UserBean>  userBeanMap=result.stream().collect(Collectors.toMap(UserBean::getUid,UserBean::thiz));
        spaceBean.setUserBeanMap(userBeanMap);



    }


}


