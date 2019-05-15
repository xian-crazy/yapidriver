package com.crazy.autotest.yapidriver.yapiwatcher;

import com.alibaba.fastjson.JSON;

import com.crazy.autotest.yapidriver.yapiwatcher.bean.ERB;
import org.apache.commons.lang3.StringUtils;
import org.everit.json.schema.ValidationException;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;

public class AllureYapiAdapter {
    protected static Logger log  = LoggerFactory.getLogger(AllureYapiAdapter.class);
    private static DocumentBuilder builder=null;
    private  Document document = builder.newDocument();
    private  Element root=null;
    private  Element style=null;
    private Element gdoc=null;
    private Element div0=null;
    private Element h2=null;
    private Element h3=null;
    
    static {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        //创建一个 DocumentBuilder
        //有异常抛出，用 try catch 捕获
        try {
             builder = factory.newDocumentBuilder();
            //创建一个全新的 XML 文档：Document
            // document = builder.newDocument();
        }catch (Exception e){

        }
    }

    public static AllureYapiAdapter newIt(){
        return new AllureYapiAdapter();
    }


    public  String descriptionHtml(String description) {

        StringWriter writer=new StringWriter();
        try {
            //先添加一个根元素：root，并指定标签：languages
            documentInit();
            h3.setTextContent(description);
            appendChilds(root,style,gdoc);
            gdoc.appendChild(div0);
            appendChilds(div0,h3);
            
            documentTransformer(root,writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return writer.toString();
    }
    

    public  String descriptionERb(ERB erb, RuntimeException e) {

        StringWriter writer=new StringWriter();
        try {
            //先添加一个根元素：root，并指定标签：languages
            documentInit();
            h2.setTextContent(erb.getName());
            if(null!=e&&(e instanceof  ValidationException)) {
                Element exception = newEl("a", "onclick", "javascript:document.getElementById('schemaValidationInfo').scrollIntoView();");
                exception.setTextContent("schema校验详情");
                exception.setAttribute("style", "color:red");
                exception.setAttribute("href","javascript:void(0);");
                gdoc.appendChild(exception);
            }

            Element h3=newEl("h3",null,null);
            h3.setTextContent("基本信息");

            Element base=newDiv(YDR1.rowcasereport);
            Element baseinfo =newDiv(YDR1.col_21);
            baseinfo.setTextContent("空间："+erb.getProject().getSpace().getGroup_name()+"  项目："+erb.getProject().getName()+"  用例集合："+erb.getCaseColName());
            base.appendChild(baseinfo);
            Element casename=newNode("用例名",erb.getName());
            Element env=newNode("执行环境",erb.getEnv());
            Element Method=newNode("Method",erb.getMethod());
            Element Path=newNode("Path",erb.getPath());

            Element divreq=newDiv(null);
            Element h3r=newEl("h3",null,null);
            h3r.setTextContent("Request");
            Element url=newNode("Url",erb.getUrl());
            Element qHeaders=newJsonNode("Req_Header",JSON.toJSONString(erb.getHeaders()));
            Element qBody=newJsonNode("Req_Body",erb.getData());
            Element qParams=newJsonNode("Req_Params",erb.getParams());
          //  divreq.appendChild(h3r).appendChild(url).appendChild(qHeaders).appendChild(qBody).appendChild(qParams);
            appendChilds(divreq,h3r,url,qHeaders,qBody,qParams);

            Element divrep=newDiv(null);
            Element h3p=newEl("h3",null,null);
            h3p.setTextContent("Reponse");
            Element pHeaders=newJsonNode("Rep_Header",erb.getRes_header());
            Element pBody=newJsonNode("Rep_Body",erb.getRes_body());
            appendChilds(divrep,h3p,pHeaders,pBody);


            appendChilds(root,style,gdoc);

            gdoc.appendChild(div0);
            appendChilds(div0,h2,h3,base,casename,env,Method,Path,divreq,divrep);

            if(null!=e) {

                Element divv = newDiv(null);
                Element h3v = newEl("h3", "id", "schemaValidationInfo");
                h3v.setTextContent("schema校验信息");
                if(e instanceof ValidationException) {
                    ValidationException v=(ValidationException)e;
                    Element Validationinfo = newJsonNode("Validation_info", v.toJSON().toString());
                    Element schema = newJsonNode("schema", v.getViolatedSchema().toString());
                    appendChilds(divv, h3v, Validationinfo, schema);

                }else if(e instanceof JSONException){
                    JSONException j=(JSONException)e;
                    Element mesg = newNode("校验异常信息", j.getMessage());
                    Element schema = newNode("提示信息", "请检查接口定义，响应是否开启了json-schema");
                    //divv.appendChild(h3v).appendChild(Validationinfo).appendChild(schema);
                    appendChilds(divv, h3v, mesg, schema);
                }
                div0.appendChild(divv);
            }
            documentTransformer(root,writer);
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    return writer.toString();
    }
    
    private void documentInit(){
        //先添加一个根元素：root，并指定标签：languages
         root=newDiv(YDR1.yapi);
         style=document.createElement("style");
        style.setTextContent(YDR1.style);
         gdoc=newDiv(YDR1.gdoc);
         div0=newDiv(null);
         h2=newEl("h2","id",YDR1.id0);
         h3=newEl("h3","id",YDR1.id0);
    }
    
    private void documentTransformer( Element root,StringWriter writer) throws TransformerException {
        //最后，将 root 添加到 document 中
        //实现 XML 数据的节点一层一层包装，最终包装到 root 和 document 中
        document.appendChild(root);
        //创建一个 TransformerFactory，同样通过静态方法调用类的newInstance()方法
        //获取一个新的实例
        TransformerFactory transformerFactory=TransformerFactory.newInstance();
        //创建一个 Transformer，可以将 XML 文档转换成其他格式
        Transformer transformer=transformerFactory.newTransformer();
//最后将 StringWriter 转换为 字符串，输出只有一行，是纯粹的XML内容，丢失了换行符、缩进符
        transformer.transform(new DOMSource(document), new StreamResult(writer));
        //  System.out.println(writer.toString());
    }
    

    private void appendChilds(Element tag,Element...els){
        for(Element el:els){
            tag.appendChild(el);
        }
    }


    private  Element newJsonNode(String name,String json){
        String id="yapi"+name;
        Element node=newDiv(YDR1.rowcasereport);
        Element nodeName =newDiv(YDR1.col_3);
        nodeName.setTextContent(name);
       // nodeName.setAttribute("name",name);
        node.appendChild(nodeName);
        Element nodeContent =newDiv(YDR1.col_21);
        Element code =newEl("code",null,null);
        Element pre =newEl("pre","id",id);
        code.appendChild(pre);
        nodeContent.appendChild(code);
        Element script =newEl("script",null,null);
        script.setTextContent("document.getElementById(\""+id+"\").innerText=JSON.stringify("+json+",null,2)");
        nodeContent.appendChild(script);
        node.appendChild(nodeContent);
        return node;
    }

    private  Element newNode(String name,String content){
        Element node=newDiv(YDR1.rowcasereport);
        Element nodeName =newDiv(YDR1.col_3);
        nodeName.setTextContent(name);
//        nodeName.setAttribute("name",name);
        Element nodeContent =newDiv(YDR1.col_21);
        nodeContent.setTextContent(content);
        node.appendChild(nodeName);
        node.appendChild(nodeContent);
        return node;
    }

    private  Element newEl(String name,String attrkey,String attrvalue){
        Element el=document.createElement(name);
        if(StringUtils.isNotEmpty(attrvalue)) {
            el.setAttribute(attrkey, attrvalue);
        }
        return el;
    }
    private  Element newEl(String name,String clazz){
        return newEl(name,"class",clazz);
    }
    private  Element newDiv(String clazz){
        return newEl("div",clazz);
    }


    @Test
    public void tt(){
        ERB erb=new ERB();
        erb.setCaseColName("bbbb");
        erb.setEnv("ffff");
        erb.setName("bbbbbb");
       log.info( AllureYapiAdapter.newIt().descriptionERb(erb,null));
    }

}

/**
 * yapiDesReport
 */
class YDR1{
    public static final String yapi="yapi-run-auto-test";
    public static final String gdoc="g-doc";
    public static final String id0="0";
    public static final String rowcasereport="row case-report";
    public static final String col_21="col-21";
    public static final String col_3="col-3 case-report-title";

    public static final String style="@charset \"UTF-8\";" +
            "h2," +
            "h3," +
            "blockquote {" +
            "margin: 0;" +
            "padding: 0;" +
            "font-weight: normal;" +
            " -webkit-font-smoothing: antialiased;" +
            "}" +
            "" +
            ".yapi-run-auto-test {" +
            "font-family: -apple-system, BlinkMacSystemFont, \"Segoe UI\", Roboto, \"Helvetica Neue\", Helvetica, \"PingFang SC\"," +
            "\"Hiragino Sans GB\", \"Microsoft YaHei\", SimSun, sans-serif;" +
            "font-size: 13px;" +
            "color: #393838;" +
            "position: relative;" +
            "}" +
            "" +
            ".yapi-run-auto-test" +
            "h2," +
            "h3{" +
            "color: #97cc64;" +
            "line-height: 36px;" +
            "}" +
            "" +
            ".yapi-run-auto-test h2 {" +
            "font-size: 28px;" +
            "padding-top: 10px;" +
            "padding-bottom: 10px;" +
            "}" +
            "" +
            ".yapi-run-auto-test h3 {" +
            "clear: both;" +
            "font-weight: 400;" +
            "" +
            "border-left: 3px solid #59d69d;" +
            "padding-left: 8px;" +
            "font-size: 18px;" +
            "}" +
            "" +
            ".yapi-run-auto-test code," +
            "pre {" +
            "font-family: Monaco, Andale Mono, Courier New, monospace;" +
            "}" +
            "" +
            ".yapi-run-auto-test code {" +
            "background-color: #fee9cc;" +
            "color: rgba(0, 0, 0, 0.75);" +
            "padding: 1px 3px;" +
            "font-size: 12px;" +
            " -webkit-border-radius: 3px;" +
            " -moz-border-radius: 3px;" +
            "border-radius: 3px;" +
            "}" +
            "" +
            ".yapi-run-auto-test pre {" +
            "display: block;" +
            "padding: 14px;" +
            "margin: 0 0 18px;" +
            "line-height: 16px;" +
            "font-size: 11px;" +
            "border: 1px solid #d9d9d9;" +
            "white-space: pre-wrap;" +
            "background: #f6f6f6;" +
            "overflow-x: auto;" +
            "}" +
            "" +
            ".yapi-run-auto-test pre code {" +
            "background-color: #f6f6f6;" +
            "color: #737373;" +
            "font-size: 14px;" +
            "padding: 0;" +
            "}" +
            "" +
            ".yapi-run-auto-test .case-report {" +
            "margin: 5px;" +
            "display: flex;" +
            "}" +
            "" +
            ".yapi-run-auto-test .case-report .case-report-title {" +
            "font-size: 14px;" +
            "text-align: right;" +
            "padding-right: 20px;" +
            "}" +
            "" +
            ".yapi-run-auto-test .col-3 {" +
            "display: block;" +
            "box-sizing: border-box;" +
            "color: #24a023;" +
            "width: 8%;" +
            "}" +
            "" +
            ".yapi-run-auto-test .col-21 {" +
            "display: block;" +
            "box-sizing: border-box;" +
            "width: 87.5%;" +
            "}"+
            ".link {" +
            " color: blue;" +
             "}";
}


