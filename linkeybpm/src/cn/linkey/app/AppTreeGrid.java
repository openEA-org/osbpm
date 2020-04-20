package cn.linkey.app;

import java.util.HashMap;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.form.HtmlParser;
import cn.linkey.org.LinkeyUser;
import cn.linkey.util.Tools;

/**
 * 本类负责生成view grid视图
 * 
 * @author Administrator 本类为单例类
 */
public class AppTreeGrid implements AppElement {

    @Override
    public void run(String wf_num) throws Exception {
        getElementHtml(wf_num); //输出视图的html代码
    }

    public String getElementBody(String wf_num, boolean readOnly) throws Exception {
        String gridNum = wf_num; // grid编号以url传入的参数为准
        Document gridDoc = AppUtil.getDocByid("BPM_GridList", "GridNum", gridNum, true);
        if (gridDoc.isNull()) {
            return "Error:The view does not exist!";
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
        String columnStr = getGridColumn(gridDoc);

        /* 3.获得js header */
        HtmlParser htmlParser = ((HtmlParser) BeanCtx.getBean("HtmlParser"));
        formBody.append("\n<script>\n var FormNum=\"" + gridDoc.g("NewFormNum") + "\",GridNum=\"" + gridNum + "\";WF_Appid=\"" + gridDoc.g("WF_Appid") + "\";\n");
        formBody.append("\nfunction formatlink(v,r){ return \"<a href='' onclick=\\\"RowDblClick(0,'\"+r.WF_OrUnid+\"');return false;\\\" >\"+v+\"</a>\";}");
        formBody.append("\nfunction GroupFormat(value,rows){return value + ' - ' + rows.length + ' " + BeanCtx.getMsg("Common", "items") + "';}");
        //作为插入视图时不支持resize功能，否则在兼容模式下会出错
        //formBody.append("\n$(window).resize(function(){$('#dg').datagrid('resize', {width:function(){return document.body.clientWidth;},height:function(){return document.body.clientHeight;}});});\n");
        String jsHeader = gridDoc.g("JsHeader");
        if (Tools.isBlank(jsHeader)) {
            jsHeader = Rdb.getValueBySql("select DefaultCode from BPM_DevDefaultCode where CodeType='TreeGrid_JsHeader'");
        }
        else {
            jsHeader = htmlParser.parserJsTagValue(jsHeader);/* 分析共享标签 */
            jsHeader = htmlParser.parserXTagValue(gridDoc, jsHeader); /* 分析x标签 */
        }
        formBody.append(jsHeader);

        /* 4.追加Body标签 */
        formBody.append("\n</script>\n");
        formBody.append(getToolbar(gridDoc)); /* 追加操作按扭的js代码 */

        // 5.生成grid主体的配置文档
        formBody.append(columnStr);

        return formBody.toString(); // 输出到浏览器
    }

    public String getElementHtml(String wf_num) throws Exception {
        String gridNum = wf_num; // grid编号以url传入的参数为准
        Document gridDoc = AppUtil.getDocByid("BPM_GridList", "GridNum", gridNum, true);
        if (gridDoc.isNull()) {
            return "Error:The view does not exist!";
        }

        //BeanCtx.out(BeanCtx.getRequest().getParameterMap().toString());

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

        /* 获得列的配置字符串,这个要在append jsheader之前执行，因为有可能在editor控件中改变girddoc.jsheader值的值 */
        String columnStr = getGridColumn(gridDoc);

        /* 3.获得js header */
        HtmlParser htmlParser = ((HtmlParser) BeanCtx.getBean("HtmlParser"));
        formBody.append("\n<script>\n var FormNum=\"" + gridDoc.g("NewFormNum") + "\",GridNum=\"" + gridNum + "\";WF_Appid=\"" + gridDoc.g("WF_Appid") + "\";\n");
        formBody.append("\nfunction formatlink(v,r){ return \"<a href='' onclick=\\\"RowDblClick(0,'\"+r.WF_OrUnid+\"');return false;\\\" >\"+v+\"</a>\";}");
        formBody.append("\nfunction GroupFormat(value,rows){return value + ' - ' + rows.length + ' " + BeanCtx.getMsg("Common", "items") + "';}");
        formBody.append("\n$(window).resize(function(){$('#dg').datagrid('resize', {width:function(){return document.body.clientWidth;},height:function(){return document.body.clientHeight;}});});\n");
        String jsHeader = gridDoc.g("JsHeader");
        if (Tools.isBlank(jsHeader)) {
            jsHeader = Rdb.getValueBySql("select DefaultCode from BPM_DevDefaultCode where CodeType='TreeGrid_JsHeader'");
        }
        else {
            jsHeader = htmlParser.parserJsTagValue(jsHeader);/* 分析JS标签 */
            jsHeader = htmlParser.parserXTagValue(gridDoc, jsHeader); /* 分析x标签 */
        }
        formBody.append(jsHeader);

        /* 4.追加Body标签 */
        formBody.append("\n</script>\n</head>\n<body>\n");
        formBody.append(getToolbar(gridDoc)); /* 追加操作按扭的js代码 */

        // 5.生成grid主体的配置文档
        formBody.append(columnStr);

        formBody.append("<div id=\"win\"></div></body></html>");

        BeanCtx.p(formBody.toString());
        return "";
    }

    public String getGridColumn(Document gridDoc) {

        // 1.首先组合data-options
        StringBuilder dataoptions = new StringBuilder(gridDoc.g("dataoptions")); // 已有data-options
        if (dataoptions.length() != 0) {
            dataoptions.append(",");
        }
        dataoptions.append("fit:true,toolbar:toptoolbar,border:false,autoRowHeight:false");
        if (gridDoc.g("RemoteSort").equals("1")) {
            dataoptions.append(",remoteSort:true"); // 允许远程排序
        }
        if (gridDoc.g("ShowRowNumber").equals("1")) {
            dataoptions.append(",rownumbers: true"); // 显示行号
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
        if (gridDoc.g("multiSort").equals("1")) {
            dataoptions.append(",multiSort:true"); // 单击行事件
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
            dataoptions.append(",onContextMenu:" + onRowContextMenu); // 右键菜单
        }
        String idField = gridDoc.g("IdField");
        if (Tools.isBlank(idField)) {
            idField = "id";
        }
        dataoptions.append(",idField:'" + idField + "'"); // tree grid 的关键id字段
        String treeField = gridDoc.g("TreeField");
        if (Tools.isBlank(treeField)) {
            treeField = "text";
        }
        dataoptions.append(",treeField:'" + treeField + "'"); // tree gird的关键字段

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
        url += "&rdm='+Math.random()";//增加一个随时数
        dataoptions.append(",url:'" + url); // 数据源地址

        // 2.组合表格体
        StringBuilder tableStr = new StringBuilder("<table id=\"dg\" class=\"easyui-treegrid\" data-options=\"");
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
            tableStr.append("<th data-options=\"" + dataoption + "\" >" + colName + "</th>");
        }
        tableStr.append("</thead></table>");

        return tableStr.toString();
    }

    /**
     * 获得表单按扭的js代码
     * 
     * @param toolbarXml 按扭的xml描述
     * @param readOnly 文档是否只读
     * @param isNewDoc 是否新文档
     * @return
     */
    public String getToolbar(Document gridDoc) {
        String toolbarJson = gridDoc.g("ToolbarConfig");
        if (Tools.isBlank(toolbarJson)) {
            toolbarJson = Rdb.getValueBySql("select DefaultCode from BPM_DevDefaultCode where CodeType='TreeGrid_ToolBar'");
        }
        LinkeyUser linkeyUser = (LinkeyUser) BeanCtx.getBean("LinkeyUser");
        StringBuilder toolbarHtml = new StringBuilder();
        toolbarHtml.append("<div class=\"toptoolbar\" id=\"toptoolbar\" >");

        int spos = toolbarJson.indexOf("[");
        toolbarJson = toolbarJson.substring(spos, toolbarJson.lastIndexOf("]") + 1);
        // BeanCtx.out(toolbarJson);
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
                    toolbarHtml.append("| <a href=\"#\" id=\"" + toolbarItem.getString("btnid") + "\" class=\"easyui-linkbutton\" plain=\"true\" data-options=\"iconCls:'"
                            + toolbarItem.getString("iconCls") + "'\" onclick=\"" + toolbarItem.getString("clickEvent") + ";return false;\">" + btnName + "</a>");
                }
            }
        }
        toolbarHtml.append("</div>");
        return toolbarHtml.toString();
    }

}
