package cn.linkey.app.easyui;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.linkey.app.AppElement;
import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.form.HtmlParser;
import cn.linkey.form.ModForm;
import cn.linkey.org.LinkeyUser;
import cn.linkey.util.Tools;

/**
 * 本类主要负打开应用表单并输出html
 * 
 * @author Administrator 本类为单实例类
 */
public class AppForm implements AppElement {

    @Override
    public void run(String wf_num) throws Exception {
        String method = (String) BeanCtx.getRequest().getMethod();
        if (method.equals("GET")) {
            openform(wf_num); // 打开表单
        }
        else {
            saveform(wf_num); // 保存表单
        }
    }

    /**
     * 运行打开表单
     * 
     * @param formNumber
     */
    public void openform(String formNumber) throws Exception {
        BeanCtx.print(getElementHtml(formNumber));
    }

    /**
     * 只输出表单的body部分不含js header 操作条，隐藏字段等
     * 
     * @param formNumber表单编号
     * @param readOnly true表示输出只读状态的表单,false表示可编辑状态
     */
    public String getElementBody(String formNumber, boolean readOnly) throws Exception {
        ModForm insModForm = (ModForm) BeanCtx.getBean("ModForm");
        Document formDoc = null;
        String docUnid = BeanCtx.g("wf_docunid", true);
        if (Tools.isBlank(docUnid)) {
            docUnid = BeanCtx.g("WF_DocUnid", true);
        } //兼容大写
        formDoc = insModForm.getFormDoc(formNumber); // 表单
        if (formDoc.isNull()) {
            return "Error:The form " + formNumber + " does not exist!";
        } // 看表单模型是否存在

        String sqlTableName = formDoc.g("SqlTableName");
        if (Tools.isBlank(docUnid)) {
            docUnid = Rdb.getNewid(sqlTableName);
        }
        Document dataDoc;
        if (Tools.isNotBlank(sqlTableName)) {
            String sql = "select * from " + sqlTableName + " where WF_OrUnid='" + docUnid + "'";
            dataDoc = Rdb.getDocumentBySql(sqlTableName, sql);
            if (dataDoc.isNull() && Tools.isNotBlank(docUnid)) {
                dataDoc.s("WF_OrUnid", docUnid);
            } // 强制指定文档unid
        }
        else {
            dataDoc = BeanCtx.getDocumentBean(sqlTableName);
        }
        dataDoc.appendFromRequest(BeanCtx.getRequest(), true); // 把请求参数初始化到文档中去
        if (dataDoc.isNull() && !dataDoc.hasItem("WF_Appid")) {
            dataDoc.s("WF_Appid", formDoc.g("WF_Appid"));
        } //新文档自动加上appid

        /* 1.组装html header */
        StringBuilder formBody = new StringBuilder();

        /* 2.运行绑定表单字段的事件 */
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
                if (Tools.isBlank(ruleoption)) {
                    ruleoption = "NEW";
                } //默认为新建
                if (Tools.isBlank(ruleoption) || ruleoption.indexOf(docStatus) != -1) {
                    params.put("FieldName", fieldMapObject.get("name")); // 字段名称
                    //params.put("FieldConfig", fieldMapObject); // 字段配置值
                    BeanCtx.getExecuteEngine().run(rule, params); //运行字段数据源并传入字段名称和文档对像
                }
            }
        }

        /* 3.运行表单事件 */
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

        //4.获得表单body并返回
        String appid = dataDoc.g("WF_Appid");
        if (Tools.isBlank(appid)) {
            appid = formDoc.g("WF_Appid");
        }
        HtmlParser htmlParser = ((HtmlParser) BeanCtx.getBean("HtmlParser"));
        String insFormBodyHtml = formDoc.g("FormBody"); /* 从表单模板中获得要显示的HTML代码 */
        insFormBodyHtml = htmlParser.parserHtml(dataDoc, insFormBodyHtml, true, readOnly, "","3");/* 解析html代码 */
        formBody.append(insFormBodyHtml);

        return formBody.toString(); //返回表单体
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
        formBody.append("<!DOCTYPE html><html><head><title>");
        formBody.append(formDoc.g("Title"));
        formBody.append("</title>");
        String htmlHeaderConfig = formDoc.g("HeaderConfigid");
        if (Tools.isBlank(htmlHeaderConfig)) {
            htmlHeaderConfig = "AppFormHtmlHeader";
        }
        if (BeanCtx.isMobile()) {
            htmlHeaderConfig = htmlHeaderConfig.concat("_Mobile");
        }
        //		if(!BeanCtx.getCountry().equals("CN")){
        //			htmlHeaderConfig=htmlHeaderConfig.concat("_").concat(BeanCtx.getCountry()); //多语言时
        //		}
        //读取html头文件，如果应用中配置有则以应用优先2015.4.8
        String configHtml = BeanCtx.getAppConfig(formDoc.g("WF_Appid"), htmlHeaderConfig);
        if (Tools.isBlank(configHtml)) {
            configHtml = BeanCtx.getSystemConfig(htmlHeaderConfig);
        }
        configHtml = configHtml.replace("{LANG}", BeanCtx.getUserLocale().getLanguage() + "_" + BeanCtx.getCountry());
        configHtml = configHtml.replace("{version}", BeanCtx.getSystemConfig("static_version"));
        //读取结束
        formBody.append(configHtml);
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
        formBody.append("\n</script>\n</head>\n<body " + bodyTag + " >\n");
        if (formDoc.g("ShowMask").equals("1")) {
            formBody.append("<div class=\"datagrid-mask\" id='bodymask' style=\"display:block;width:100%;height:100%\" ></div>");
            formBody.append("<div class=\"datagrid-mask-msg\" id='bodymask-msg' style=\"display:block;left:45%;\" >Loading...</div>");
        }
        formBody.append(getToolbar(dataDoc, formDoc.g("ToolbarConfig"), readOnly, dataDoc.isNewDoc())); /* 追加操作按扭的js代码 */
        formBody.append("\n<form method='post' name='linkeyform' id='linkeyform' >\n");

        /* 9.获得表单html代码并填上字段内容 */
        String insFormBodyHtml = formDoc.g("FormBody"); /* 从表单模板中获得要显示的HTML代码 */
        // insFormBodyHtml=Rdb.deCode(insFormBodyHtml, false); //对HTML内容字段进行解码操作
        insFormBodyHtml = htmlParser.parserHtml(dataDoc, insFormBodyHtml, true, readOnly, "", "3");/* 解析html代码 */

        /* 10.添加表单尾吧 */
        formBody.append(insFormBodyHtml);
        formBody.append(getFormHiddenField(dataDoc, formDoc, readOnly));
        formBody.append("</form><div id=\"win\"></div></body></html>");

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
        toolbarHtml.append("<div class=\"toptoolbar\" >");
        int spos = toolbarJson.indexOf("[");
        toolbarJson = toolbarJson.substring(spos, toolbarJson.lastIndexOf("]") + 1);
        String action = "EDIT"; // 与TbHidden进行对比看在什么状态下进行隐藏
        if (readOnly) {
            action = "READ";
        }
        else if (isNewDoc) {
            action = "NEW";
        }

        //1如果是READ状态则看当前用户是否有编辑权限如果没有就隐藏编辑按扭
        String noAuthors = ""; //默认是作者
        if (action.equals("READ")) {
            if (dataDoc.hasItem("WF_AllAuthors")) {
                if (!BeanCtx.getLinkeyUser().inRoles(BeanCtx.getUserid(), dataDoc.g("WF_AllAuthors"))) {
                    noAuthors = "NOAUTHOR"; //不是作者时
                }
            }
        }

        JSONArray jsonArr = JSON.parseArray(toolbarJson);
        int i = 0;
        for (i = 0; i < jsonArr.size(); i++) {
            JSONObject toolbarItem = (JSONObject) jsonArr.get(i);
            String hidden = toolbarItem.getString("hidden");
            if ((Tools.isBlank(hidden) || hidden.indexOf(action) == -1) && (Tools.isBlank(noAuthors) || hidden.indexOf(noAuthors) == -1)) { // 说明按扭不需要隐藏
                String roleNumber = toolbarItem.getString("RoleNumber"); // 绑定角色编号
                if (linkeyUser.inRoles(BeanCtx.getUserid(), roleNumber)) {
                    String hiddenfd = toolbarItem.getString("hiddenfd"); // 隐藏字段
                    if (Tools.isBlank(hiddenfd) || dataDoc.g(hiddenfd).equals("true")) {
                        String btnName = toolbarItem.getString("btnName");
                        if (btnName.startsWith("L_")) {
                            btnName = BeanCtx.getLabel(btnName);
                        }
                        toolbarHtml.append("| <a href=\"#\" id=\"" + toolbarItem.getString("btnid") + "\" class=\"easyui-linkbutton\" plain=\"true\" data-options=\"iconCls:'"
                                + toolbarItem.getString("iconCls") + "'\" onclick=\"" + toolbarItem.getString("clickEvent") + ";return false;\">" + btnName + "</a>");
                    }
                }
            }
        }
        if (i == 0) {
            return "";
        }
        toolbarHtml.append("</div><div class=\"toptoolbarhr\"></div>");
        return toolbarHtml.toString();
    }

    /**
     * 获得表单的隐藏字段
     * 
     * @param dataDoc 数据文档对像
     * @param formDoc 表单模型对像
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

    /**
     * 存盘应用表单
     * 
     * @return
     */
    public void saveform(String formNumber) throws Exception {
    	
        ModForm insModForm = (ModForm) BeanCtx.getBean("ModForm");
        Document formDoc = null;
        String docUnid = BeanCtx.g("WF_DocUnid", true);
        if (Tools.isBlank(docUnid)) {
            docUnid = BeanCtx.g("wf_docunid", true);
        } //兼容全小写
        formDoc = insModForm.getFormDoc(formNumber); // 表单
        if (formDoc.isNull()) {
            BeanCtx.p("{\"Status\":\"error\",\"msg\":\"Error:The form " + formNumber + " does not exist!\"}");
        }

        String sqlTableName = formDoc.g("SqlTableName");
        if (Tools.isBlank(docUnid)) {
            docUnid = Rdb.getNewid(sqlTableName);
        }

        // 2.获得已有的旧文档对像
        Document dataDoc;
        Connection conn = null;
        if (Tools.isNotBlank(sqlTableName)) {
            String sql = "select * from " + sqlTableName + " where WF_OrUnid='" + docUnid + "'";
            String dataSource = formDoc.g("DataSourceid");
            if (Tools.isNotBlank(dataSource) && !dataSource.equals("default")) {
                conn = Rdb.getNewConnection(dataSource);
                if (formDoc.g("isRollBack").equals("1")) { //开启事务
                    try {
                        conn.setAutoCommit(false);
                    }
                    catch (Exception e) {
                        BeanCtx.log(e, "E", dataSource + "数据源在开启事务时出错!");
                    }
                }
                dataDoc = Rdb.getDocumentBySql(conn, sqlTableName, sql); // 指定数据源的文档对像,conn为空时为当前链接
            }
            else {
                // 看是否指定开启事务
                if (formDoc.g("isRollBack").equals("1")) {
                    Rdb.setAutoCommit(false); // 开启事务
                }
                dataDoc = Rdb.getDocumentBySql(sqlTableName, sql); // 默认数据源的文档对像,conn为空时为当前链接
            }
        }
        else {
            dataDoc = BeanCtx.getDocumentBean(null); //表单中没有指定数据库表的情况下创建一个空文档
            dataDoc.setIsNull();
        }
        dataDoc.appendFromRequest(BeanCtx.getRequest()); // 把提交的字段填充旧文档对像

        if (dataDoc.isNewDoc()) {
            dataDoc.s("WF_OrUnid", docUnid);
        } // 如果是新文档则指定文档的unid,不然在存盘时会变成另一个新的unid而不是当前提交上来的wf_docunid值

        // 3.运行表单存盘事件
        String resultStr = "";
        String ruleNum = formDoc.g("EventRuleNum");
        //BeanCtx.out("AppForm->ruleNum="+ruleNum);
        if (Tools.isNotBlank(ruleNum)) {
            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put("FormDoc", formDoc);
            params.put("DataDoc", dataDoc);
            params.put("EventName", "onFormSave");
            String ruleStr = BeanCtx.getExecuteEngine().run(ruleNum, params); // 运行表单存盘事件
            if (!ruleStr.equals("1")) {
                // 说明事件中要退出本次存盘
                resultStr = "{\"Status\":\"Exit\",\"msg\":\"" + ruleStr + "\"}";
                //BeanCtx.out("AppForm->saveForm->"+resultStr);
                BeanCtx.print(resultStr);
                return;
            }
        }

        //4.看是否有读者域的字段，如果有则加入到 WF_AllReaders字段中去
        //		String fieldConfig = formDoc.g("FieldConfig"); // 字段配置属性
        //		if(fieldConfig.indexOf("\"fdtype\":\"reader\"")!=-1){
        //			int spos = fieldConfig.indexOf("[");
        //			if (spos != -1) {
        //				fieldConfig = fieldConfig.substring(spos, fieldConfig.length() - 1);
        //				if (Tools.isNotBlank(fieldConfig)) { /* 获得每个字段的字段类型 */
        //					JSONArray jsonArr = JSON.parseArray(fieldConfig);
        //					for (int i = 0; i < jsonArr.size(); i++) {
        //						JSONObject fieldItem = (JSONObject) jsonArr.get(i);
        //						String fdtype = fieldItem.getString("fdtype"); // 字段类型
        //						if (fdtype != null) {
        //							if(fdtype.equals("reader")){
        //								dataDoc.appendTextList("WF_AllReaders",dataDoc.g(fieldItem.getString("name")));
        //							}
        //						}
        //					}
        //				}
        //			}
        //		}

        // 5.存盘文档
        if (Tools.isNotBlank(sqlTableName)) {
            if (formDoc.g("NoEnCode").equals("1")) {
                BeanCtx.setDocNotEncode();
            }
            int i;
            if (conn == null) {
                //默认数据源
                i = dataDoc.save(null, sqlTableName);
            }
            else {
                //指定数据源
                i = dataDoc.save(conn, sqlTableName);
                try {
                    if (conn.getAutoCommit() == false) {
                        conn.commit(); //提交事务
                    }
                }
                catch (Exception e) {
                    BeanCtx.log(e, "E", "外部数据源事务提交时出错!");
                }
                Rdb.close(conn); //如果不是默认数据源则需要主动关闭
            }
            if (i > 0) {
                resultStr = "{\"Status\":\"ok\",\"msg\":\"" + BeanCtx.getMsg("Common", "AppDocumentSaved") + "\"}";
            }
            else {
                resultStr = "{\"Status\":\"error\",\"msg\":\"" + BeanCtx.getMsg("Common", "AppDocumentSaveError") + "\"}";
            }
        }
        else {
            //没有指定数据库表的情况下，执行操作即可
            resultStr = "{\"Status\":\"ok\",\"msg\":\"" + BeanCtx.getMsg("Common", "AppFormExeButton") + "\"}";
        }

        // 6.回滚事务,在BeanCtx.close()中会自动回滚
        if (BeanCtx.isRollBack()) {
            resultStr = "{\"Status\":\"error\",\"msg\":\"" + BeanCtx.getMsg("Common", "AppDocumentSaveError") + "\"}";
        }
        // BeanCtx.out("存盘表单("+sqlTableName+")数据="+i);
        BeanCtx.print(resultStr);

    }
}
