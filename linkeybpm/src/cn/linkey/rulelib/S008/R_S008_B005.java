package cn.linkey.rulelib.S008;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.linkey.app.AppForm;
import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.form.HtmlParser;
import cn.linkey.form.ModForm;
import cn.linkey.org.LinkeyUser;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * @RuleName:form表单解析规则
 * @author admin
 * @version: 8.0
 * @Created: 2015-10-09 21:08
 */
final public class R_S008_B005 extends AppForm implements LinkeyRule {
	
	String uiType = "3";  //20180205 暂时写死easyui类型
	
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        //params为运行本规则时所传入的参数
        String viewNum = BeanCtx.g("v", true);
//        if (Tools.isBlank(viewNum)) {
//            viewNum = (String) params.get("XTagValue");//获得视图编号
//        }
//        String eleid = (String) params.get("eleid");
//        if (Tools.isNotBlank(eleid)) {
//            viewNum = eleid;
//        } //以R_S008_B003中传入的优先
        if (Tools.isBlank(viewNum)) {
            return "form does not exist";
        }
        String htmlStr=getElementHtml(viewNum);
        BeanCtx.p(htmlStr);
        
        return "";
    }

    /**
     * 获得表单的整个html代码
     */
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

        String sqlTableName = formDoc.g("SqlTableName");
        if (Tools.isBlank(docUnid)) {
            docUnid = Rdb.getNewid(sqlTableName);
        }
        Document dataDoc;
        if (Tools.isNotBlank(sqlTableName)) {
            String sql = "select * from " + sqlTableName + " where WF_OrUnid='" + docUnid + "'";
            String dataSourceid = formDoc.g("DataSourceid");
            if (Tools.isBlank(dataSourceid) || dataSourceid.equals("default")) {
                dataDoc = Rdb.getDocumentBySql(sqlTableName, sql); // 默认数据源,数据文档对像
            }
            else {
                Connection conn = Rdb.getNewConnection(dataSourceid);
                dataDoc = Rdb.getDocumentBySql(conn, sqlTableName, sql); // 指定数据源,数据文档对像
                Rdb.close(conn);
            }
            if (dataDoc.isNull()) {
                dataDoc.s("WF_OrUnid", docUnid);
            } // 强制指定文档unid
        }
        else {
            dataDoc = BeanCtx.getDocumentBean(sqlTableName);
            dataDoc.s("WF_OrUnid", docUnid);
            dataDoc.setIsNull();
        }

        //1.检测当前用户是否有权限查看本文档数据 getElementBody()方法不需要检测，因为getElementBody()只供程序调用
        if (!dataDoc.isNull()) {
            if (Tools.isNotBlank(formDoc.g("DataReadRoles"))) {
                String readRoles = Tools.parserStrByDocument(dataDoc, formDoc.g("DataReadRoles")); //分析表单数据的阅读权限
                //				BeanCtx.out("readRoles="+readRoles);
                if (!BeanCtx.getLinkeyUser().inRoles(BeanCtx.getUserid(), readRoles)) {
                    String htmlCode = BeanCtx.getSystemConfig("ErrorHtmlCode");
                    String msg = BeanCtx.getMsg("Common", "ErrorNoReadAcl", "");
                    if (Tools.isBlank(htmlCode)) {
                        htmlCode = "<center><font color=red>" + msg + "</color></center>";
                    }
                    else {
                        htmlCode = htmlCode.replace("{msg}", msg);
                    }
                    return htmlCode;
                }
            }
        }

        //2.看文档是阅读还是编辑状态
        dataDoc.appendFromRequest(BeanCtx.getRequest(), true); // 把请求参数初始化到文档中去
        if (dataDoc.isNull() && !dataDoc.hasItem("WF_Appid")) {
            dataDoc.s("WF_Appid", formDoc.g("WF_Appid"));
        } //新文档自动加上appid
        //		BeanCtx.out(dataDoc);
        boolean readOnly = true;
        String action = BeanCtx.g("wf_action", true);
        if (Tools.isBlank(action)) {
            action = BeanCtx.g("WF_Action", true);
        } //兼容
        if (Tools.isBlank(action)) {
            action = "open";
        }
        if (action.equalsIgnoreCase("edit") || dataDoc.isNewDoc()) {
            readOnly = false; // action=edit或者是新文档时可以编辑
        }

        //3.如果是编辑状态则需要检测WF_AllAuthors字段是否有权编辑本文档
        if (action.equalsIgnoreCase("edit") && !dataDoc.isNewDoc()) {
            if (Tools.isNotBlank(formDoc.g("DataEditRoles"))) {
                String editRoles = Tools.parserStrByDocument(dataDoc, formDoc.g("DataEditRoles")); //分析表单数据的修改权限
                //				BeanCtx.out("editRoles="+editRoles+" curRoles="+BeanCtx.getUserRoles(BeanCtx.getUserid()));
                if (!BeanCtx.getLinkeyUser().inRoles(BeanCtx.getUserid(), editRoles)) {
                    String htmlCode = BeanCtx.getSystemConfig("ErrorHtmlCode");
                    String msg = BeanCtx.getMsg("Common", "ErrorNoEditAcl", "");
                    if (Tools.isBlank(htmlCode)) {
                        htmlCode = "<center><font color=red>" + msg + "</color></center>";
                    }
                    else {
                        htmlCode = htmlCode.replace("{msg}", msg);
                    }
                    return htmlCode; // 有修改权限控制
                }
            }
        }

        /* 4.组装html header */
        StringBuilder formBody = new StringBuilder();
        formBody.append(formDoc.g("HtmlHeader"));
        /* 5.运行绑定表单字段的事件 */
        String docStatus = "EDIT"; // 编辑状态
        if (readOnly) {
            docStatus = "READ";
        } // 只读状态
        if (dataDoc.isNewDoc()) {
            docStatus = "NEW";
        } // 新建状态

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

        /* 6.运行表单事件 */
        String ruleNum = formDoc.g("EventRuleNum");
        if (Tools.isNotBlank(ruleNum)) {
            params = new HashMap<String, Object>();
            params.put("FormDoc", formDoc);
            params.put("DataDoc", dataDoc);
            params.put("EventName", "onFormOpen");
            if (readOnly) {
                params.put("ReadOnly", "1");
            }
            else {
                params.put("ReadOnly", "0");
            }
            String ruleStr = BeanCtx.getExecuteEngine().run(ruleNum, params); // 运行表单打开事件
            if (!ruleStr.equals("1")) {
                // 说明事件中要退出本次表单打开
                return ruleStr;
            }
        }

        /* 7.获得js header */
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

        /* 8.追加Body标签 */
        String bodyTag = formDoc.g("BodyTag");
        if (Tools.isBlank(bodyTag)) {
            bodyTag = "style=\"margin:0px;\"  scroll=\"auto\"";
        }
        formBody.append("\n</script>");
        if (formDoc.g("ShowMask").equals("1")) {
            formBody.append("<div class=\"datagrid-mask\" id='bodymask' style=\"display:block;width:100%;height:100%\" ></div>");
            formBody.append("<div class=\"datagrid-mask-msg\" id='bodymask-msg' style=\"display:block;left:45%;\" >Loading...</div>");
        }
        formBody.append(getToolbar(dataDoc, formDoc.g("ToolbarConfig"), readOnly, dataDoc.isNewDoc())); /* 追加操作按扭的js代码 */
        formBody.append("\n<form method='post' name='linkeyform' id='linkeyform' >\n");

        /* 9.获得表单html代码并填上字段内容 */
        String insFormBodyHtml = formDoc.g("FormBody"); /* 从表单模板中获得要显示的HTML代码 */
        // insFormBodyHtml=Rdb.deCode(insFormBodyHtml, false); //对HTML内容字段进行解码操作
        insFormBodyHtml = htmlParser.parserHtml(dataDoc, insFormBodyHtml, true, readOnly, "", uiType);/* 解析html代码 */

        /* 10.添加表单尾吧 */
        formBody.append(insFormBodyHtml);
        formBody.append(getFormHiddenField(dataDoc, formDoc, readOnly));
        formBody.append("</form>");

        formBody.append("\n<script type=\"text/javascript\" src=\"linkey/bpm/jscode/app_openform.js\"></script>");
        formBody.append("\n<script type=\"text/javascript\" src=\"linkey/bpm/jscode/uivalidate.js\"></script>");
        formBody.append("<script type=\"text/javascript\" src=\"linkey/bpm/easyui/jquery.easyui.min.js\"></script>");
        
        return formBody.toString(); // 输出到浏览器
    }

    /**
     * 获得表单按扭的js代码
     * 
     * @param toolbarXml 按扭的xml描述
     * @param readOnly文档是否只读
     * @param isNewDoc 是否新文档
     * @return
     */
    public String getToolbar(Document dataDoc, String toolbarJson, boolean readOnly, boolean isNewDoc) {
        if (Tools.isBlank(toolbarJson)) {
            toolbarJson = Rdb.getValueBySql("select DefaultCode from BPM_DevDefaultCode where CodeType='Form_Toolbar'");
        }

        LinkeyUser linkeyUser = (LinkeyUser) BeanCtx.getBean("LinkeyUser");
        StringBuilder toolbarHtml = new StringBuilder();
        int spos = toolbarJson.indexOf("[");
        toolbarJson = toolbarJson.substring(spos, toolbarJson.lastIndexOf("]") + 1);
        String action = "EDIT"; // 与TbHidden进行对比看在什么状态下进行隐藏
        if (readOnly) {
            action = "READ";
        }
        else if (isNewDoc) {
            action = "NEW";
        }

        JSONArray jsonArr = JSON.parseArray(toolbarJson);
        int i = 0;
        for (i = 0; i < jsonArr.size(); i++) {
            JSONObject toolbarItem = (JSONObject) jsonArr.get(i);
            String hidden = toolbarItem.getString("hidden");
            if ((Tools.isBlank(hidden) || hidden.indexOf(action) == -1)) { // 说明按扭不需要隐藏
                String roleNumber = toolbarItem.getString("RoleNumber"); // 绑定角色编号
                if (linkeyUser.inRoles(BeanCtx.getUserid(), roleNumber)) {
                    String hiddenfd = toolbarItem.getString("hiddenfd"); // 隐藏字段
                    if (Tools.isBlank(hiddenfd) || dataDoc.g(hiddenfd).equals("true")) {
                        String btnName = toolbarItem.getString("btnName");
                        if (btnName.startsWith("L_")) {
                            btnName = BeanCtx.getLabel(btnName);
                        }
                        //						toolbarHtml.append("| <a href=\"#\" id=\""+ toolbarItem.getString("btnid")+ "\" class=\"easyui-linkbutton\" plain=\"true\" data-options=\"iconCls:'"+ toolbarItem.getString("iconCls")
                        //										+ "'\" onclick=\""+ toolbarItem.getString("clickEvent")+ ";return false;\">"+btnName+ "</a>");
                        String icons = toolbarItem.getString("iconCls").replace("icon-", "fa-");
                        toolbarHtml.append("<button id=\"" + toolbarItem.getString("btnid") + "\"  onclick=\"" + toolbarItem.getString("clickEvent")
                                + ";return false;\" class=\"btn green\"><i class=\"fa " + icons + "\"></i> " + btnName + "</button>");
                    }
                }
            }
        }
        toolbarHtml.append("<div style=\"height:10px\"></div>");
        if (i == 0) {
            return "";
        }
        return toolbarHtml.toString();
    }
    
    /**
     * 获得表单的隐藏字段
     * @param dataDoc 数据文档对像
     * @param formDoc 表单模型对像
     * 20180131 表单做layui扩展，在此复制AppForm中此方法。
     * @return
     */
    public StringBuilder getFormHiddenField(Document dataDoc, Document formDoc, boolean readOnly) {
        StringBuilder tempStr = new StringBuilder(1200);
        tempStr.append("\n\n<!-- Hidden Field Begin--><div style='display:none'>\n");
        tempStr.append("<input name='WF_DocUnid' id='WF_DocUnid' value='" + dataDoc.getDocUnid() + "' >\n");
        tempStr.append("<input name='WF_FormNumber' id='WF_FormNumber' value='" + formDoc.g("FormNumber") + "' >\n");
        tempStr.append("<input name='WF_UserName' id='WF_UserName' value='" + BeanCtx.getUserid() + "' disabled >\n");
        String appid = dataDoc.g("WF_Appid");
        if (Tools.isBlank(appid)) {
            appid = formDoc.g("WF_Appid");
        }
        tempStr.append("<input name='WF_Appid' id='WF_Appid' value='" + appid + "' >\n");
        tempStr.append("<input name='WF_NewDocFlag' id='WF_NewDocFlag' value='" + dataDoc.isNewDoc() + "' disabled ><!-- true表示新文档-->\n");
        tempStr.append("<input name='WF_ReadOnly' id='WF_ReadOnly' value='" + readOnly + "' disabled ><!-- true表示只读状态,false表示编辑状态-->\n");
        tempStr.append("</div><!-- Hidden Field End-->\n");
        tempStr.trimToSize();
        return tempStr;
    }

}