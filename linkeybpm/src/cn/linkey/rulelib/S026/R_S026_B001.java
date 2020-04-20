package cn.linkey.rulelib.S026;

import java.util.HashMap;
import java.util.Map;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.form.HtmlParser;
import cn.linkey.form.ModForm;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * @RuleName:使用表单设置模拟数据
 * @author admin
 * @version: 8.0
 * @Created: 2014-09-11 09:06
 */
final public class R_S026_B001 implements LinkeyRule {
	
	String uiType = "3";  //20180205 暂时写死easyui类型
	
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        //params为运行本规则时所传入的参数
        String formNumber = BeanCtx.g("formnumber", true);
        String formHtml = getElementHtml(formNumber);//获得表单的html代码
        BeanCtx.p(formHtml);
        return "";
    }

    public String getElementHtml(String formNumber) throws Exception {
        ModForm insModForm = (ModForm) BeanCtx.getBean("ModForm");
        Document formDoc = null;
        String docUnid = BeanCtx.g("wf_docunid", true);
        if (Tools.isBlank(docUnid)) {
            docUnid = BeanCtx.g("WF_DocUnid", true);
        } //兼容大写
        formDoc = insModForm.getFormDoc(formNumber); // 表单
        if (formDoc.isNull()) {
            return "Error:The form " + formNumber + " does not exist!"; // 看表单模型是否存在
        }

        //获得已有的文档数据
        String sqlTableName = "BPM_SimFormList";
        Document dataDoc;
        String sql = "select * from " + sqlTableName + " where WF_OrUnid='" + docUnid + "'";
        dataDoc = Rdb.getDocumentBySql(sqlTableName, sql); // 默认数据源,数据文档对像
        if (dataDoc.isNull()) {
            dataDoc.s("WF_OrUnid", docUnid);
        } // 强制指定文档unid

        /* .组装html header */
        StringBuilder formBody = new StringBuilder();
        formBody.append("<!DOCTYPE html><html><head><title>");
        formBody.append(formDoc.g("Title"));
        formBody.append("</title>");
        String htmlHeaderConfig = formDoc.g("HeaderConfigid");
        if (Tools.isBlank(htmlHeaderConfig)) {
            htmlHeaderConfig = "AppFormHtmlHeader";
        }
        formBody.append(BeanCtx.getSystemConfig(htmlHeaderConfig));
        formBody.append(formDoc.g("HtmlHeader"));

        /* .运行绑定表单字段的事件 */
        String docStatus = "EDIT"; // 编辑状态
        ((ModForm) BeanCtx.getBean("ModForm")).initAppFormFieldConfig(formDoc);//实始化App表单的字段配置信息
        HashMap<String, Map<String, String>> formFieldConfig = BeanCtx.getMainFormFieldConfig();
        HashMap<String, Object> params = new HashMap<String, Object>(); //准备运行规则的参数
        params.put("Document", dataDoc);
        for (String fieldName : formFieldConfig.keySet()) {
            Map<String, String> fieldMapObject = formFieldConfig.get(fieldName);
            String rule = fieldMapObject.get("rule"); // 规则编号
            if (rule != null) {
                String ruleoption = fieldMapObject.get("ruleoption"); // 规则运行方式NEW,EDIT,READ
                if (Tools.isBlank(ruleoption) || ruleoption.indexOf(docStatus) != -1) {
                    params.put("FieldName", fieldMapObject.get("name")); // 字段名称
                    //params.put("FieldConfig", fieldMapObject); // 字段配置值
                    BeanCtx.getExecuteEngine().run(rule, params); //运行字段数据源并传入字段名称和文档对像
                }
            }
        }

        /* 运行表单事件 */
        String ruleNum = formDoc.g("EventRuleNum");
        if (Tools.isNotBlank(ruleNum)) {
            params = new HashMap<String, Object>();
            params.put("FormDoc", formDoc);
            params.put("DataDoc", dataDoc);
            params.put("EventName", "onFormOpen");
            params.put("ReadOnly", "0");
            String ruleStr = BeanCtx.getExecuteEngine().run(ruleNum, params); // 运行表单打开事件
            if (!ruleStr.equals("1")) {
                // 说明事件中要退出本次表单打开
                return ruleStr;
            }
        }

        /* .获得js header */
        String appid = dataDoc.g("WF_Appid");
        if (Tools.isBlank(appid)) {
            appid = formDoc.g("WF_Appid");
        }
        HtmlParser htmlParser = ((HtmlParser) BeanCtx.getBean("HtmlParser"));
        formBody.append("\n<script>\n var WF_GoUrl=\"" + formDoc.g("SaveGoUrl") + "\",WF_Appid=\"" + appid + "\",WF_DocUnid=\"" + docUnid + "\";\n");
        String jsHeader = formDoc.g("JsHeader");
        if (Tools.isBlank(jsHeader)) {
            jsHeader = Rdb.getValueBySql("Select DefaultCode from BPM_DevDefaultCode where CodeType='Form_JsHeader'");
        }
        else {
            jsHeader = htmlParser.parserJsTagValue(jsHeader);/* 分析JS标签 */
            jsHeader = htmlParser.parserXTagValue(dataDoc, jsHeader); /* 分析x标签 */
        }
        formBody.append(jsHeader); /* 主表单的js代码不管是否只读都需要输出 */

        /* 追加Body标签 */
        String bodyTag = formDoc.g("BodyTag");
        if (Tools.isBlank(bodyTag)) {
            bodyTag = "style=\"margin:0px;\"  scroll=\"auto\"";
        }

        formBody.append(
                "function SaveAppDocument(btnAction){var isValid = $(\"form\").form('validate');if (!isValid){alert(\"表单验证未通过!\");return false;}if(btnAction==undefined){btnAction=\"save\";}	var postData=\"WF_Action=\"+btnAction+\"&wf_docunid=\"+WF_DocUnid;	var chkStr=getNoCheckedBoxValue();	if(chkStr!=\"\"){postData+=\"&\"+chkStr;}	var textShowStr=getFieldTextShowData();	if(textShowStr!=\"\"){postData+=\"&\"+textShowStr;}	postData+=\"&\"+$(\"form\").serialize();	mask();	$.post(\"r?wf_num=R_S026_B002\",postData,function(data){		unmask();		var rs = eval('(' + data + ')');alert(rs.msg);	});}");
        formBody.append("\n</script>\n</head>\n<body " + bodyTag + " >\n");
        if (formDoc.g("ShowMask").equals("1")) {
            formBody.append("<div class=\"datagrid-mask\" id='bodymask' style=\"display:block;width:100%;height:100%\" ></div>");
            formBody.append("<div class=\"datagrid-mask-msg\" id='bodymask-msg' style=\"display:block;left:45%;\" >Loading...</div>");
        }
        formBody.append(
                "<div class=\"toptoolbar\" >| <a href=\"#\" id=\"btnQuit\" class=\"easyui-linkbutton\" plain=\"true\" data-options=\"iconCls:'icon-save'\" onclick=\"SaveAppDocument('Quit');return false;\">保存退出</a>| <a href=\"#\" id=\"btnSave\" class=\"easyui-linkbutton\" plain=\"true\" data-options=\"iconCls:'icon-save'\" onclick=\"SaveAppDocument('save');return false;\">保存</a>| <a href=\"#\" id=\"btnBack\" class=\"easyui-linkbutton\" plain=\"true\" data-options=\"iconCls:'icon-remove'\" onclick=\"window.close();return false;\">关闭窗口</a></div><div class=\"toptoolbarhr\"></div>");
        formBody.append("\n<form method='post' name='linkeyform' id='linkeyform' >\n");

        /* 获得表单html代码并填上字段内容 */
        String insFormBodyHtml = formDoc.g("FormBody"); /* 从表单模板中获得要显示的HTML代码 */
        // insFormBodyHtml=Rdb.deCode(insFormBodyHtml, false); //对HTML内容字段进行解码操作
        insFormBodyHtml = htmlParser.parserHtml(dataDoc, insFormBodyHtml, true, false, "", uiType);/* 解析html代码 */

        /* .添加表单尾吧 */
        formBody.append(insFormBodyHtml);
        //formBody.append(getFormHiddenField(dataDoc, formDoc, readOnly));
        formBody.append("</form><div id=\"win\"></div></body></html>");

        return formBody.toString(); // 输出
    }

}