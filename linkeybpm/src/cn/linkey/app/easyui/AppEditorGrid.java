package cn.linkey.app.easyui;

import java.util.HashMap;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.linkey.app.AppElement;
import cn.linkey.app.AppUtil;
import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.form.HtmlParser;
import cn.linkey.org.LinkeyUser;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * 本类负责生成editor grid视图
 * 
 * @author Administrator 本类为单例类
 */
public class AppEditorGrid implements AppElement {

    @Override
    public void run(String wf_num) throws Exception {
        String action = BeanCtx.g("WF_Action");
        if (action.equals("btnClick")) {
            btnClick(wf_num);
        }
        else {
            open(wf_num);
        }
    }

    /**
     * 打开视图
     */
    public void open(String gridNum) throws Exception {
        BeanCtx.p(getElementHtml(gridNum));
    }

    /**
     * 打开视图运行并输出view 的 html代码
     */
    public String getElementHtml(String gridNum) throws Exception {
        Document gridDoc = AppUtil.getDocByid("BPM_GridList", "GridNum", gridNum, true);
        if (gridDoc.isNull()) {
            return "Error:The view does not exist!";
        }

        //在url中强制指定视图为只读状态
        boolean readOnly = false;
        if (BeanCtx.g("wf_action", true).equals("read")) {
            readOnly = true;
        }

        // 1.运行view打开事件
        String ruleNum = gridDoc.g("EventRuleNum");
        if (Tools.isNotBlank(ruleNum)) {
            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put("GridDoc", gridDoc);
            params.put("EventName", "onGridOpen");
            String openMsg = BeanCtx.getExecuteEngine().run(ruleNum, params); // 如果事件返回1则表示执行成功
            if (!openMsg.equals("1")) { // 如果事件不返回1则表示退出打开grid并输出msg消息
                return openMsg;
            }
        }
        gridDoc.appendFromRequest(BeanCtx.getRequest(), true); // 把请求参数初始化到文档中去

        /* 2.组装html header */
        StringBuilder formBody = new StringBuilder();
        formBody.append("<!DOCTYPE html><html><head><title>");
        formBody.append(gridDoc.g("GridName"));
        formBody.append("</title>");
        String htmlHeaderConfig = "AppGridHtmlHeader";
        if (BeanCtx.isMobile()) {
            htmlHeaderConfig = htmlHeaderConfig.concat("_Mobile");
        }
        //		if(!BeanCtx.getCountry().equals("CN")){
        //			htmlHeaderConfig=htmlHeaderConfig.concat("_").concat(BeanCtx.getCountry());
        //		}

        //读取html头文件，如果应用中配置有则以应用优先2015.4.8
        String configHtml = BeanCtx.getAppConfig(gridDoc.g("WF_Appid"), htmlHeaderConfig);
        if (Tools.isBlank(configHtml)) {
            configHtml = BeanCtx.getSystemConfig(htmlHeaderConfig);
        }
        configHtml = configHtml.replace("{LANG}", BeanCtx.getUserLocale().getLanguage() + "_" + BeanCtx.getCountry());
        configHtml = configHtml.replace("{version}", BeanCtx.getSystemConfig("static_version"));
        //读取结束

        formBody.append(configHtml);
        if (Tools.isNotBlank(gridDoc.g("GroupField"))) {
            formBody.append("<script type=\"text/javascript\" src=\"linkey/bpm/easyui/datagrid-groupview.js\"></script>");// 分组显示时需要
        }

        /* 获得列的配置字符串,这个要在append jsheader之前执行，因为有可能在editor控件中改变girddoc.jsheader值的值 */
        String jsHeader = gridDoc.g("JsHeader");
        if (Tools.isBlank(jsHeader)) {
            gridDoc.s("JsHeader", jsHeader = Rdb.getValueBySql("select DefaultCode from BPM_DevDefaultCode where CodeType='EditorGrid_JsHeader'"));
        }
        String columnStr = getGridColumn(gridDoc, readOnly); //这里面有可能追加编辑控件的js header代码进来

        /* 3.获得js header */
        HtmlParser htmlParser = ((HtmlParser) BeanCtx.getBean("HtmlParser"));
        formBody.append("\n<script>\n var FormNum=\"" + gridDoc.g("NewFormNum") + "\",GridNum=\"" + gridNum + "\";WF_Appid=\"" + gridDoc.g("WF_Appid") + "\";\n");
        formBody.append("\nfunction formatlink(v,r){ return \"<a href='' onclick=\\\"RowDblClick(0,'\"+r.WF_OrUnid+\"');return false;\\\" >\"+v+\"</a>\";}");
        formBody.append("\nfunction GroupFormat(value,rows){return value + ' - ' + rows.length + ' " + BeanCtx.getMsg("Common", "items") + "';}");
        formBody.append("\n$(window).resize(function(){$('#dg').datagrid('resize', {width:function(){return document.body.clientWidth;},height:function(){return document.body.clientHeight;}});});\n");
        jsHeader = gridDoc.g("JsHeader"); //重新取一次，因为编辑控件中有可能追加了js代码
        jsHeader = htmlParser.parserJsTagValue(jsHeader);/* 分析JS标签 */
        jsHeader = htmlParser.parserXTagValue(gridDoc, jsHeader); /* 分析x标签 */
        formBody.append(jsHeader);

        /* 4.追加Body标签 */
        formBody.append("\n</script>\n</head>\n<body>\n");
        formBody.append(getToolbar(gridDoc, readOnly)); /* 追加操作按扭的js代码 */

        // 5.生成grid主体的配置文档
        formBody.append(columnStr);

        formBody.append("<div id=\"win\"></div></body></html>");

        return formBody.toString(); // 输出到浏览器

    }

    /**
     * 生成插入表单中的视图的HTML代码
     * 
     * @param gridNum 视图编号
     * @param readOnly true表示输出只读模式,false表示编辑模式
     * @return
     */
    public String getElementBody(String gridNum, boolean readOnly) throws Exception {
        Document gridDoc = AppUtil.getDocByid("BPM_GridList", "GridNum", gridNum, true);
        if (gridDoc.isNull()) {
            return "";
        }

        //在url中强制指定视图为只读状态
        if (BeanCtx.g("wf_action", true).equals("read")) {
            readOnly = true;
        }

        // 1.运行view打开事件
        String ruleNum = gridDoc.g("EventRuleNum");
        if (Tools.isNotBlank(ruleNum)) {
            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put("GridDoc", gridDoc);
            params.put("EventName", "onGridOpen");
            String openMsg = BeanCtx.getExecuteEngine().run(ruleNum, params); // 如果事件返回1则表示执行成功
            if (!openMsg.equals("1")) { // 如果事件不返回1则表示退出打开grid并输出msg消息
                return openMsg;
            }
        }

        /* 2.组装html header */
        StringBuilder formBody = new StringBuilder();

        /* 获得列的配置字符串,这个要在append jsheader之前执行，因为有可能在editor控件中改变girddoc.jsheader值的值 */
        gridDoc.s("fit", "false");
        String jsHeader = gridDoc.g("JsHeader");
        if (Tools.isBlank(jsHeader)) {
            gridDoc.s("JsHeader", jsHeader = Rdb.getValueBySql("select DefaultCode from BPM_DevDefaultCode where CodeType='EditorGrid_JsHeader'"));
        }
        String columnStr = getGridColumn(gridDoc, readOnly);

        /* 3.获得js header */
        HtmlParser htmlParser = ((HtmlParser) BeanCtx.getBean("HtmlParser"));
        formBody.append("\n<script>\n var FormNum=\"" + gridDoc.g("NewFormNum") + "\",GridNum=\"" + gridNum + "\";\n");
        formBody.append("\nfunction formatlink(v,r){ return \"<a href='' onclick=\\\"RowDblClick(0,'\"+r.WF_OrUnid+\"');return false;\\\" >\"+v+\"</a>\";}");
        formBody.append("\nfunction GroupFormat(value,rows){return value + ' - ' + rows.length + ' " + BeanCtx.getMsg("Common", "items") + "';}");
        //作为插入视图时不支持resize功能，否则在兼容模式下会出错
        //formBody.append("\n$(window).resize(function(){$('#dg').datagrid('resize', {width:function(){return document.body.clientWidth;},height:function(){return document.body.clientHeight;}});});\n");
        jsHeader = gridDoc.g("JsHeader");
        jsHeader = htmlParser.parserJsTagValue(jsHeader);/* 分析JS标签 */
        jsHeader = htmlParser.parserXTagValue(gridDoc, jsHeader); /* 分析x标签 */
        formBody.append(jsHeader);

        /* 4.追加Body标签 */
        formBody.append("\n</script>\n");
        if (!readOnly) {
            //只有编辑模式才显示操作按扭
            formBody.append(getToolbar(gridDoc, readOnly)); /* 追加操作按扭的js代码 */
        }
        else {
            formBody.append("<div id='toptoolbar'></div>"); //不显示操作按扭
        }

        // 5.生成grid主体的配置文档
        formBody.append(columnStr);

        return formBody.toString(); // 返回视图体
    }

    public String getGridColumn(Document gridDoc, boolean readOnly) {

        // 1.首先组合data-options
        StringBuilder dataoptions = new StringBuilder(gridDoc.g("dataoptions")); // 已有data-options
        if (dataoptions.length() != 0) {
            dataoptions.append(",");
        }
        dataoptions.append("toolbar:toptoolbar");
        if (gridDoc.g("fit").equals("1")) {
            dataoptions.append(",fit:false"); // 平铺窗口
        }
        else {
            dataoptions.append(",fit:true"); // 平铺窗口
        }
        if (gridDoc.g("autoHeight").equals("1")) {
            dataoptions.append(",autoRowHeight:true"); // 自动行高
        }
        else {
            dataoptions.append(",autoRowHeight:false"); //固定行高
        }
        if (gridDoc.g("RemoteSort").equals("1")) {
            dataoptions.append(",remoteSort:true"); // 允许远程排序
        }
        else {
            dataoptions.append(",remoteSort:false"); // 当前页排序
        }
        if (gridDoc.g("border").equals("1")) {
            dataoptions.append(",border:true"); // 显示边框
        }
        else {
            dataoptions.append(",border:false"); //不显示边框
        }
        if (gridDoc.g("ShowRowNumber").equals("1")) {
            dataoptions.append(",rownumbers: true"); // 显示行号
        }
        
        //20181109 解决打印界面分页展示数据而无法展示全部数据的问题
        if (gridDoc.g("ShowPageBar").equals("1") && !"print".equals(BeanCtx.g("wf_action"))) {
        //-------------------20181109 新增 End -----------------------------------------
        
        //if (gridDoc.g("ShowPageBar").equals("1")) {
            dataoptions.append(",pagination:true"); // 显示分页条
        }
        if (gridDoc.g("fitColumns").equals("1")) {
            dataoptions.append(",fitColumns:true"); // 平铺列
        }
        if (gridDoc.g("MutliSelect").equals("1")) {
            dataoptions.append(",singleSelect:false"); // 允许多选
        }
        else {
            dataoptions.append(",singleSelect:true"); // 单选
        }
        if (Tools.isNotBlank(gridDoc.g("SortName"))) {
            dataoptions.append(",sortName:'" + gridDoc.g("SortName") + "'"); // 显示时的排序字段
        }
        if (gridDoc.g("SortOrder").equals("1")) {
            dataoptions.append(",sortOrder:'desc'"); // 显示时的排序方式
        }
        if (Tools.isNotBlank(gridDoc.g("GroupField"))) {
            dataoptions.append(",view:groupview,groupField:'" + gridDoc.g("GroupField") + "',groupFormatter:GroupFormat"); // 分组显示
        }
        if (gridDoc.g("multiSort").equals("1")) {
            dataoptions.append(",multiSort:true"); // 允许多选
        }
        String rowDblClick = gridDoc.g("RowDblClick");
        if (Tools.isNotBlank(rowDblClick)) {
            dataoptions.append(",onDblClickRow:" + rowDblClick); // 双击行事件
        }
        String rowClick = gridDoc.g("RowClick");
        if (Tools.isNotBlank(rowClick)) {
            dataoptions.append(",onClickRow:" + rowClick); // 单击行事件
        }
        String onRowContextMenu = gridDoc.g("RowContextMenu");
        if (Tools.isNotBlank(onRowContextMenu)) {
            dataoptions.append(",onRowContextMenu:" + onRowContextMenu); // 右键菜单
        }
        String pageSize = gridDoc.g("PageSize");
        if (Tools.isBlank(pageSize)) {
            pageSize = "25";
        }
        String pageList = gridDoc.g("PageList");
        if (Tools.isBlank(pageList)) {
            pageList = "20,25,40,60";
        }
        dataoptions.append(",pageSize:" + pageSize); // 每页显示行
        dataoptions.append(",pageList:" + pageList); // 每页显示行可选项
        String url = gridDoc.g("DataSource");
        if (Tools.isNotBlank(url) && url.substring(0, 2).equals("D_")) {
            url = "r?wf_num=" + url;
        }
        String qry = BeanCtx.getRequest().getQueryString();
        if (Tools.isNotBlank(qry)) {
            //qry=Tools.decodeUrl(qry).replace("wf_num=", "wf_gridnum=");
            url = url + "&" + qry.replace("wf_num=", "wf_gridnum=");
        }
        if (Tools.isNotBlank(gridDoc.g("DataSourceParams"))) {
            url += "&" + gridDoc.g("DataSourceParams"); //加入默认传入的参数到url中
        }
        if (BeanCtx.getLinkeywf() != null) {
            url += "&ParentDocUnid=" + BeanCtx.getLinkeywf().getDocUnid();
        }
        url += "&rdm='+Math.random()";//增加一个随时数
        dataoptions.append(",url:'" + url); // 数据源地址

        // 2.组合表格体
        StringBuilder tableStr = new StringBuilder("<table id=\"dg\" class=\"easyui-datagrid\" data-options=\"");
        tableStr.append(dataoptions);
        tableStr.append("\"><thead><tr>");

        // 3.显示复选框
        if (gridDoc.g("ShowCheckBox").equals("1")) {
            tableStr.append("<th data-options=\"checkbox:true\"></th>");
        }

        // 3.组合列配置
        String columnConfig = gridDoc.g("ColumnConfig");
        int spos = columnConfig.indexOf("[");
        if (spos == -1) {
            BeanCtx.showErrorMsg("Error:The grid column config is null!");
            return "Error:The grid column config is null!";
        }

        HashMap<String, String> editorCtlidList = new HashMap<String, String>(); //编辑控件id的集合和参数id为key,value为控件配置参数
        columnConfig = columnConfig.substring(spos, columnConfig.lastIndexOf("]") + 1);
        JSONArray jsonArr = JSON.parseArray(columnConfig);
        for (int i = 0; i < jsonArr.size(); i++) {
            JSONObject colConfigItem = (JSONObject) jsonArr.get(i);
            String colName = colConfigItem.getString("ColName");
            String fdName = colConfigItem.getString("FdName");
            if (colName != null) {
                if (colName.startsWith("L_")) {
                    colName = BeanCtx.getLabel(colName);
                } //替换国际化标签
            }
            if (Tools.isBlank(colName) && !fdName.equals("icon")) {
                colName = fdName;
            }
            String dataoption = "field:'" + fdName + "',width:" + colConfigItem.getString("ColWidth");
            String align = colConfigItem.getString("Align");
            if (Tools.isNotBlank(align)) {
                dataoption += ",align:'" + align + "'";
            }
            String sort = colConfigItem.getString("Sort");
            if (sort == null || !sort.equals("N")) {
                dataoption += ",sortable:true";
            }
            if (Tools.isNotBlank(colConfigItem.getString("dataoptions"))) {
                dataoption += "," + colConfigItem.getString("dataoptions");
            }
            String formatter = colConfigItem.getString("Format"); // 格式化
            if (Tools.isNotBlank(formatter)) {
                if (formatter.equals("link")) {
                    dataoption += ",formatter:formatlink";
                }
                else if (formatter.indexOf("color") != -1) {
                    String color = formatter.substring(6, formatter.length() - 1);
                    dataoption += ",formatter:function(v,r){return '<font color=" + color + " >'+v+'</font>';}";
                }
                else if (formatter.indexOf("icon") != -1) {
                    colName = "";
                    dataoption += ",formatter:function(v,r){return '<img src=linkey/bpm/images/icons/doclist.gif >';}";
                }
                else if (formatter.indexOf(".gif") != -1 || formatter.indexOf(".png") != -1) {
                    dataoption += ",formatter:function(v,r){return '<img src=linkey/bpm/images/icons/" + formatter + " >';}";
                }
                else {
                    dataoption += ",formatter:" + colConfigItem.getString("FormatFun");
                }
            }
            if (!readOnly) { //只读模式下不显示编辑控件
                String editorCtlid = colConfigItem.getString("EditorCtlid"); //编辑控件
                if (Tools.isNotBlank(editorCtlid)) {
                    String paramStr = colConfigItem.getString("CtlParams");
                    editorCtlidList.put(editorCtlid, paramStr); //把编辑控件的id和参数加入map中，用来生成jsheader时使用
                    dataoption += getEditorControlConfig(editorCtlid, paramStr);
                }
            }
            tableStr.append("<th data-options=\"" + dataoption + "\" >" + colName + "</th>");
        }

        if (!readOnly) { //只读模式下不显示编辑控件
            getEditorControlJsCode(gridDoc, editorCtlidList); //追加编辑控件的js code 到grid的jsheader中去
        }

        tableStr.append("</thead></table>");

        return tableStr.toString();
    }

    /**
     * 根据编辑控件的id号，获取编辑控件的配置值
     * 
     * @param editorCtlid 编辑控件的id
     * @return 返回editor的字符串
     */
    public String getEditorControlConfig(String editorCtlid, String paramStr) {
        String sql = "select ControlConfig from BPM_GridControlList where Controlid='" + editorCtlid + "'";
        String configStr = Rdb.getValueBySql(sql);
        if (Tools.isBlank(configStr)) {
            return "";
        }
        else {
            if (paramStr != null) {
                String[] params = Tools.split(paramStr);
                for (String arg : params) {
                    configStr = Tools.replaceOne(configStr, "{?}", arg);
                }
            }
            return "," + configStr;
        }
    }

    /**
     * 获得编辑控件的所有js header 自定义的js代码
     * 
     * @param gridDoc 配置文档
     * @param editorCtlidList 编辑控件的id集合
     * @return 返回组合好的js代码
     */
    public void getEditorControlJsCode(Document gridDoc, HashMap<String, String> editorCtlidList) {
        if (editorCtlidList.size() == 0) {
            return;
        }
        String sqlWhere = "";
        int i = 0;
        for (String id : editorCtlidList.keySet()) {
            if (i == 0) {
                i = 1;
            }
            else {
                sqlWhere = sqlWhere + " or ";
            }
            sqlWhere += "Controlid='" + id + "'";
        }
        String sql = "select Controlid,JsHeader from BPM_GridControlList where UseJsCode='1' and (" + sqlWhere + ")";
        StringBuilder jsHeader = new StringBuilder(gridDoc.g("JsHeader"));
        HashMap<String, String> dataMap = Rdb.getMapDataBySql(sql);
        for (String ctrid : dataMap.keySet()) {
            jsHeader.append("\n");
            String jscode = dataMap.get(ctrid);
            String paramString = editorCtlidList.get(ctrid);//获得控件参数
            if (paramString != null) {
                String[] params = Tools.split(paramString);
                for (String arg : params) {
                    jscode.replaceFirst("{?}", arg);
                }
            }
            jsHeader.append(jscode);
        }
        gridDoc.s("JsHeader", jsHeader.toString());
    }

    /**
     * 获得表单按扭的js代码
     * 
     * @param toolbarXml 按扭的xml描述
     * @param readOnly 文档是否只读
     * @param isNewDoc 是否新文档
     * @return
     */
    public String getToolbar(Document gridDoc, boolean readOnly) {
        String toolbarJson = gridDoc.g("ToolbarConfig");
        if (Tools.isBlank(toolbarJson)) {
            String sql = "select DefaultCode from BPM_DevDefaultCode where CodeType='EditorGrid_ToolBar'";
            toolbarJson = Rdb.getValueBySql(sql);
        }
        LinkeyUser linkeyUser = (LinkeyUser) BeanCtx.getBean("LinkeyUser");
        StringBuilder toolbarHtml = new StringBuilder();
        toolbarHtml.append("<div class=\"toptoolbar\" id=\"toptoolbar\" >");

        //先显示搜索条，兼容ie7
        if (gridDoc.g("ShowSearchBar").equals("1")) {
            String searchHtml = gridDoc.g("WF_SearchBar");
            if (Tools.isBlank(searchHtml)) {
                // 使用默认的搜索条
                searchHtml = "<span style='float:right;padding-right:20px'>" + BeanCtx.getMsg("Common", "Search") + ":<input class=\"easyui-searchbox\" data-options=\"searcher:GridDoSearch,prompt:'"
                        + BeanCtx.getMsg("Common", "SearchPrompt") + "'\" id=\"SearchKeyWord\" style=\"width:150px;\"></span>";
            }
            toolbarHtml.append(searchHtml);
        }

        //在url中传入wf_action=read时不显示操作条，wf_action=edit或空时显示
        if (!readOnly) {
            int spos = toolbarJson.indexOf("[");
            toolbarJson = toolbarJson.substring(spos, toolbarJson.lastIndexOf("]") + 1);
            JSONArray jsonArr = JSON.parseArray(toolbarJson);
            for (int i = 0; i < jsonArr.size(); i++) {
                JSONObject toolbarItem = (JSONObject) jsonArr.get(i);
                String roleNumber = toolbarItem.getString("RoleNumber"); // 绑定角色编号
                if (linkeyUser.inRoles(BeanCtx.getUserid(), roleNumber)) {
                    String hiddenfd = toolbarItem.getString("hiddenfd"); // 隐藏字段
                    if (Tools.isBlank(hiddenfd) || gridDoc.g(hiddenfd).equals("true")) {
                        String btnName = toolbarItem.getString("btnName");
                        if (btnName.startsWith("L_")) {
                            btnName = BeanCtx.getLabel(btnName);
                        }
                        toolbarHtml.append("| <a href=\"#\" id=\"" + toolbarItem.getString("btnid") + "\" class=\"easyui-linkbutton\" plain=\"true\" data-options=\"iconCls:'" + toolbarItem.getString("iconCls") + "'\" onclick=\"" + toolbarItem.getString("clickEvent") + ";return false;\">" + btnName + "</a>");
                    }
                }
            }
        }
        else {
            //不显示按扭
            toolbarHtml.append("<img src='linkey/bpm/images/icons/s.gif' height='20px'>");
        }
        toolbarHtml.append("</div>");
        return toolbarHtml.toString();
    }

    /**
     * 视图中的文档执行操作
     */
    public void btnClick(String gridNum) throws Exception {

        // 获得grid文档的配置参数
        Document gridDoc = AppUtil.getDocByid("BPM_GridList", "GridNum", gridNum, true);
        if (gridDoc.isNull()) {
            BeanCtx.print("{\"msg\":\"Error:The view does not exist!\"");
            return;
        }

        // 看是否开启事务
        if (gridDoc.g("isRollBack").equals("1")) {
            Rdb.setAutoCommit(false); // 开启事务
        }

        // 获得json数据源的数据库表名
        String sql = "select SqlTableName from BPM_DataSourceList where Dataid='" + gridDoc.g("DataSource") + "' and Status='1'";
        String sqlTableName = Rdb.getValueBySql(sql);
        String[] docArray = Tools.split(BeanCtx.g("WF_OrUnid", true));
        int i = 0;
        String ruleMsg = "";
        LinkeyRule insLinkeyRule = null;
        HashMap<String, Object> params = new HashMap<String, Object>();
        String ruleNum = gridDoc.g("EventRuleNum");
        if (Tools.isNotBlank(ruleNum)) {
            params.put("GridDoc", gridDoc);
            params.put("EventName", "onBtnClick");
            insLinkeyRule = (LinkeyRule) BeanCtx.getBeanByRuleNum(ruleNum); // Grid事件执行对像
        }
        for (String docUnid : docArray) {
            Document doc = Rdb.getDocumentBySql("select * from " + sqlTableName + " where WF_OrUnid='" + docUnid + "'");
            if (insLinkeyRule != null) {
                params.put("DataDoc", doc);
                ruleMsg = insLinkeyRule.run(params); // run grid onbtnClick doc
                i++;
            }
            doc.clear();
        }
        ruleMsg = "{\"msg\":\"" + ruleMsg.replace("{i}", String.valueOf(i)) + "}";
        if (BeanCtx.isRollBack()) {
            ruleMsg = "{\"msg\":\"" + BeanCtx.getMsg("Designer", "ViewBtnDocRollBack") + "\"}"; // 出错了需要回滚数据
        }
        BeanCtx.print(ruleMsg);

    }

}
