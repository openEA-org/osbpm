package cn.linkey.rulelib.S003;

import java.util.*;

import org.apache.commons.lang3.StringEscapeUtils;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.form.HtmlParser;
import cn.linkey.form.ModForm;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * 本规则主要负责显示子表单的内容
 * 
 * @author Administrator
 *
 */
public class R_S003_B061 implements LinkeyRule {
	
	String uiType = "3";  //20180205 暂时写死easyui类型
	
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        String docUnid = BeanCtx.g("OrUnid");
        String sql = "select * from BPM_SubFormData where WF_OrUnid='" + docUnid + "'";
        Document doc = Rdb.getDocumentBySql(sql);
        if (doc.isNull()) {
            BeanCtx.p("Error:Can not find the document by " + docUnid + "!");
        }
        String formNumber = doc.g("FormNumber");
        ModForm modForm = (ModForm) BeanCtx.getBean("ModForm");
        Document formDoc = modForm.getFormDoc(formNumber);
        modForm.initAppFormFieldConfig(formDoc);//初始化字段属性配置
        if (formDoc.isNull()) {
            BeanCtx.p("Error:Can not find the subform by " + formNumber + "!");
        }
        
        //20180910 子表单显示方式 
        String formBody = "";
        String sql3 = "select CUSTOMSUBFORMBODY from BPM_MODTASKLIST where PROCESSID='" + doc.g("WF_PROCESSID") + "' and NODEID='" + doc.g("NODEID") + "'";
        
        if(Tools.isNotBlank(doc.g("FormBody")) && "1".equals(Rdb.getValueBySql(sql3))){ 
        	formBody = doc.g("FormBody");
        	formBody = StringEscapeUtils.unescapeHtml4(formBody);
        	HtmlParser insHtmlParser = (HtmlParser) BeanCtx.getBean("HtmlParser");/* 分析html代码并填上字段内容 */
        	formBody = insHtmlParser.parserHtml(doc, formBody, true, true, "", uiType);//解析子表单填上字段内容,这里是否子表单要传true因为这里的字段权限初始化只在MainFieldConfig上面
        }else{
        	formBody = formDoc.g("FormBody");
        	HtmlParser insHtmlParser = (HtmlParser) BeanCtx.getBean("HtmlParser");/* 分析html代码并填上字段内容 */
        	formBody = insHtmlParser.parserHtml(doc, formBody, true, true, "", uiType);//解析子表单填上字段内容,这里是否子表单要传true因为这里的字段权限初始化只在MainFieldConfig上面
        }
        //20180917 解决只读状态视图可编辑问题
        formBody = formBody.replace("wf_action=edit", "wf_action=read");
        
        BeanCtx.p("<html><head>");
        String headerConfigid = formDoc.g("HeaderConfigid");
        if (Tools.isBlank(headerConfigid)) { 
            headerConfigid = "ProcessFormHtmlHeader";
        }
        if (BeanCtx.isMobile()) {
            headerConfigid = headerConfigid + "_Mobile"; //移动端表头
        }
        // if(!BeanCtx.getCountry().equals("CN")){
        //     headerConfigid=headerConfigid.concat("_").concat(BeanCtx.getCountry()); //多语言时
        // }
        String configHtml = BeanCtx.getSystemConfig(headerConfigid);
        configHtml = configHtml.replace("{LANG}", BeanCtx.getUserLocale().getLanguage() + "_" + BeanCtx.getCountry());
        configHtml = configHtml.replace("{version}", BeanCtx.getSystemConfig("static_version"));
        BeanCtx.p(configHtml);
        
        /* 20180926.获得js header */
        StringBuilder formJS = new StringBuilder();
        formJS.append("<script>");
        
        String jsHeader = formDoc.g("JsHeader");
        formJS.append(jsHeader);
        
        formJS.append("\nfunction formonload(){var obj=parent.$(\"#subframe_" + docUnid + "\");var height=document.body.scrollHeight;obj.attr('height',height);try {formreload();} catch (e) {} }");
        formJS.append("</script>");  
        
        BeanCtx.p(formJS.toString()); //20180926 修改，允许子表单在只读状态加载js
//        BeanCtx.p("<script>function formonload(){var obj=parent.$(\"#subframe_" + docUnid + "\");var height=document.body.scrollHeight;obj.attr('height',height);try {formreload();} catch (e) {} };</script>");
        //BeanCtx.p("<script>function formonload(){var obj=parent.$(\"#subframe_" + docUnid + "\");var height='100px';obj.attr('height',height);};</script>");
        BeanCtx.p("</head><body>");
        BeanCtx.p(formBody);
        BeanCtx.p("</body></html>");

        return "";
    }
}
