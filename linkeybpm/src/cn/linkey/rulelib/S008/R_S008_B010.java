package cn.linkey.rulelib.S008;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.form.HtmlParser;
import cn.linkey.form.ModForm;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.DateUtil;
import cn.linkey.util.Tools;
import cn.linkey.wf.ProcessEngine;

/**
 * 本类主要生成流程处理单的Html Header以及通用操作条
 * 
 * @author Administrator
 *
 */
public class R_S008_B010 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        ProcessEngine linkeywf = BeanCtx.getLinkeywf();
        Document formDoc = (Document) params.get("FormDoc"); /* 传入的表单模型文档对像 */
        StringBuilder formBody = new StringBuilder(2000); /* 传入的表单HTML代码 */

        
        formBody.append(formDoc.g("HtmlHeader"));

        /* 获得js header */
        HtmlParser htmlParser = ((HtmlParser) BeanCtx.getBean("HtmlParser"));
        formBody.append("\n<script>\n");
        String jsHeader = formDoc.g("JsHeader");
        if (Tools.isBlank(jsHeader)) {
            jsHeader = Rdb.getValueBySql("Select DefaultCode from BPM_DevDefaultCode where CodeType='ProcessForm_JsHeader'");
        }
        else {
            jsHeader = htmlParser.parserJsTagValue(jsHeader);/* 分析JS标签 */
            jsHeader = htmlParser.parserXTagValue(linkeywf.getDocument(), jsHeader); /* 分析一次x标签的值 */
        }
        formBody.append(jsHeader); /* 主表单的js代码不管是否只读都需要输出 */
        if (!linkeywf.isReadOnly()) {
            /* 只读时因为当前用户没有所处的环节，所以不需要拼接子表单的js代码 */
            String subFormNumber = linkeywf.getCurNodeSubFormList(true); /* 获得当前环节的所有子表单包含处理单 */

            /* 循环所有子表单并追加JsHeader */
            if (Tools.isNotBlank(subFormNumber)) {
                String[] subFormList = Tools.split(subFormNumber, ",");
                for (String formNumber : subFormList) {
                    Document subFormDoc = ((ModForm) BeanCtx.getBean("ModForm")).getFormDoc(formNumber);
                    if (!subFormDoc.isNull()) {
                        jsHeader = htmlParser.parserJsTagValue(subFormDoc.g("JsHeader"));/* 分析JS标签 */
                        jsHeader = htmlParser.parserXTagValue(linkeywf.getDocument(), jsHeader); /* 分析[X][/X]标签 */
                        formBody.append(jsHeader);
                    }
                }
            }
        }

        /* 追加Body标签 */
        formBody.append("\n</script>\n");
        formBody.append("<div class=\"portlet box green form-fit\"><div class=\"portlet-title\"></div><div class=\"portlet-body form\">");
        formBody.append("<form action='#' name='linkeyform' id='linkeyform' ><div class=\"form-body\">\n");

        if (linkeywf.getIsNewProcess()) {
            linkeywf.getDocument().s("WF_DocCreated", DateUtil.getNow());
            linkeywf.getDocument().s("WF_CurrentNodeName", linkeywf.getCurrentNodeName());
            linkeywf.getDocument().s("WF_AddName_CN", BeanCtx.getUserName());
            linkeywf.getDocument().s("WF_DocUnid", linkeywf.getDocUnid());
            linkeywf.getDocument().s("WF_Processid", linkeywf.getProcessid());
            linkeywf.getDocument().s("WF_ProcessName", linkeywf.getProcessName()); //中英文转换	
        }
        String configid = "EngineFormTopBar";
        if (!BeanCtx.getCountry().equals("CN")) {
            configid = configid.concat("_" + BeanCtx.getCountry());
        }

        //2015.4.8修改，以应用中的配置为优先
        String configHtml = "";
        if (Tools.isNotBlank(linkeywf.getAppid())) {
            configHtml = BeanCtx.getAppConfig(linkeywf.getAppid(), configid);
        }
        if (Tools.isBlank(configHtml)) {
            configHtml = BeanCtx.getSystemConfig(configid); //如果为空则到系统通用配置中去找
        }

        formBody.append(Tools.parserStrByDocument(linkeywf.getDocument(), configHtml));
        //修改结束

        formBody.trimToSize();
        return formBody.toString(); /* 返回1表示运行成功 */
    }

}
