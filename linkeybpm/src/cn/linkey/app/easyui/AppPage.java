package cn.linkey.app.easyui;

import java.util.HashMap;

import cn.linkey.app.AppElement;
import cn.linkey.app.AppUtil;
import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.factory.IIIIILIIIIIIIII;
import cn.linkey.form.HtmlParser;
import cn.linkey.util.DateUtil;
import cn.linkey.util.Tools;

/**
 * 本类主要负打开应用页面并输出html
 * 
 * @author Administrator 本类为单实例类
 */
public class AppPage implements AppElement {
    @Override
    public void run(String pageNum) throws Exception {
        BeanCtx.p(getElementHtml(pageNum)); //输出页面的html代码
    }

    /**
     * 获得页面的body内容，不输出js header
     */
    public String getElementBody(String pageNum, boolean readOnly) throws Exception {
        //获得页面配置的eldoc
        Document pageDoc = AppUtil.getDocByid("BPM_PageList", "PageNum", pageNum, true);
        if (pageDoc.isNull()) {
            return "Error:the page " + pageNum + " does not exist!";
        }

        Document dataDoc = BeanCtx.getDocumentBean("");
        dataDoc.appendFromRequest(BeanCtx.getRequest(), true); // 把请求参数初始化到文档中去

        /* 1.组装html header */
        StringBuilder formBody = new StringBuilder();

        /* 3.运行页面事件 */
        String ruleNum = pageDoc.g("EventRuleNum");
        if (Tools.isNotBlank(ruleNum)) {
            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put("PageDoc", pageDoc);
            params.put("DataDoc", dataDoc);
            params.put("EventName", "onPageOpen");
            String ruleStr = BeanCtx.getExecuteEngine().run(ruleNum, params); // 运行表单打开事件
            if (!ruleStr.equals("1")) {
                // 说明事件中要退出本次页面打开
                return ruleStr;
            }
        }

        HtmlParser htmlParser = ((HtmlParser) BeanCtx.getBean("HtmlParser"));
        String insFormBodyHtml = pageDoc.g("PageBody"); /* 从page配置中获得要显示的HTML代码 */
        insFormBodyHtml = htmlParser.parserXTagValue(dataDoc, insFormBodyHtml); /* 分析[X][/X]标签即可，不用解析字段和其他标签 */
        //		if(insFormBodyHtml.indexOf("[/APP]")!=-1){
        //			insFormBodyHtml=htmlParser.parserAppTagValue(insFormBodyHtml); //分析[Page][/Page]标签
        //		}
        formBody.append(insFormBodyHtml);

        return formBody.toString(); // 输出到浏览器
    }

    /**
     * 获得页面的html代码
     */
    public String getElementHtml(String pageNum) throws Exception {

        //获得页面配置的eldoc
        Document pageDoc = AppUtil.getDocByid("BPM_PageList", "PageNum", pageNum, true);
        if (pageDoc.isNull()) {
            return "Error:the page " + pageNum + " does not exist!";
        }

        Document dataDoc = BeanCtx.getDocumentBean("");
        dataDoc.appendFromRequest(BeanCtx.getRequest(), true); // 把请求参数初始化到文档中去

        /* 1.组装html header */
        StringBuilder formBody = new StringBuilder();
        String noPageHeader = pageDoc.g("NoPageHeader");
        if (!noPageHeader.equals("1")) { //不输出html头
            formBody.append("<!DOCTYPE html><html><head><title>");
            formBody.append(pageDoc.g("Title"));
            formBody.append("</title>");
            String htmlHeaderConfig = pageDoc.g("HeaderConfigid");
            if (Tools.isBlank(htmlHeaderConfig)) {
                htmlHeaderConfig = "AppPageHtmlHeader";
            }
            
          //20181023 添加移动端页面JS
            if (BeanCtx.isMobile()) {
                htmlHeaderConfig = "ListMobileHtmlHeader";
            }
            
            //读取html头文件，如果应用中配置有则以应用优先2015.4.8
            String configHtml = BeanCtx.getAppConfig(pageDoc.g("WF_Appid"), htmlHeaderConfig);
            if (Tools.isBlank(configHtml)) {
                configHtml = BeanCtx.getSystemConfig(htmlHeaderConfig);
            }
            //读取结束
            formBody.append(configHtml);
        }
        formBody.append(pageDoc.g("HtmlHeader"));

        /* 3.运行页面事件 */
        String ruleNum = pageDoc.g("EventRuleNum");
        if (Tools.isNotBlank(ruleNum)) {
            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put("PageDoc", pageDoc);
            params.put("DataDoc", dataDoc);
            params.put("EventName", "onPageOpen");
            String ruleStr = BeanCtx.getExecuteEngine().run(ruleNum, params); // 运行表单打开事件
            if (!ruleStr.equals("1")) {
                // 说明事件中要退出本次页面打开
                return ruleStr;
            }
        }

        /* 4.获得js header */
        String jsHeader = pageDoc.g("JsHeader");
        HtmlParser htmlParser = ((HtmlParser) BeanCtx.getBean("HtmlParser"));
        if (noPageHeader.equals("1") || Tools.isNotBlank(jsHeader)) {
            formBody.append("\n<script>\n var WF_Appid=\"" + pageDoc.g("WF_Appid") + "\",WF_Userid=\"" + BeanCtx.getUserid() + "\";\n");
        }

        if (Tools.isBlank(jsHeader)) {
            if (!noPageHeader.equals("1")) {
                jsHeader = Rdb.getValueBySql("Select DefaultCode from BPM_DevDefaultCode where CodeType='Page_JsHeader'");
            }
        }
        else {
            jsHeader = htmlParser.parserJsTagValue(jsHeader);/* 分析JS标签 */
            jsHeader = htmlParser.parserXTagValue(dataDoc, jsHeader); /* 分析x标签 */
        }

        //追加jsheader
        formBody.append(jsHeader); /* 输出js header */
        if (noPageHeader.equals("1") || Tools.isNotBlank(jsHeader)) {
            formBody.append("\n</script>");
        }

        /* 5.追加Body标签 */
        if (!noPageHeader.equals("1")) {
            String bodyTag = pageDoc.g("BodyTag");
            if (Tools.isBlank(bodyTag)) {
                bodyTag = "style=\"margin:0px;\"  scroll=\"auto\"";
            }
            formBody.append("\n</head>\n<body " + bodyTag + " >\n");
        }
        if (pageDoc.g("ShowMask").equals("1")) {
            formBody.append("<div class=\"datagrid-mask\" id='bodymask' style=\"display:block;width:100%;height:100%\" ></div>");
            formBody.append("<div class=\"datagrid-mask-msg\" id='bodymask-msg' style=\"display:block;left:45%;\" >Loading...</div>");
        }
        //20190402 添加button 跳转到填写序列号
        if (this.isValidSysDate()) {
            return "Error:this system sn has expired! <button type=\"button\" onclick=\"window.location.href='form?wf_num=F_S001_A049'\">填写序列号</button>";
        }


        /* 6.获得html代码并填上字段内容 */
        String insFormBodyHtml = pageDoc.g("PageBody"); /* 从page配置中获得要显示的HTML代码 */
        insFormBodyHtml = htmlParser.parserXTagValue(dataDoc, insFormBodyHtml); /* 分析[X][/X]标签即可，不用解析字段和其他标签 */
        //		if(insFormBodyHtml.indexOf("[/APP]")!=-1){
        //			insFormBodyHtml=htmlParser.parserAppTagValue(insFormBodyHtml); //分析[Page][/Page]标签
        //		}

        /* 6.添加page尾吧 */
        formBody.append(insFormBodyHtml);
        if (!noPageHeader.equals("1")) {
            formBody.append("<div id=\"win\"></div></body></html>");
        }
        return formBody.toString(); // 输出到浏览器
    }

    private boolean isValidSysDate() {
        if (BeanCtx.getSystemConfig("SystemType").equals("1")) {
            return false;
        } //如果是正式用户则不进行检测1表示正式用户，2表示试用用户
        Document doc = Rdb.getDocumentBySql("select * from BPM_SystemInfo");
        String ctdName = doc.g("CtdName");
        ctdName = IIIIILIIIIIIIII.L1IIIL1L1IIIIIIIIIILLLIIIIIIIIIIIIIIIIIIIIIILLL(ctdName);
        String startDate = IIIIILIIIIIIIII.L1IIIL1L1IIIIIIIIIILLLIIIIIIIIIIIIIIIIIIIIIILLL(doc.g("EndDate"));
        if (Tools.isBlank(startDate)) {
            return true;
        }
        if (DateUtil.lessTime(startDate + " 00:00", DateUtil.getNow())) {
            return true;
        }
        else {
            return false;
        }
    }
}
