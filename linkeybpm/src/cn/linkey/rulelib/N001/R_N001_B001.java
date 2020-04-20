package cn.linkey.rulelib.N001;

import java.util.HashMap;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.linkey.app.AppUtil;
import cn.linkey.app.AppViewGrid;
import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.form.HtmlParser;
import cn.linkey.org.LinkeyUser;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * @RuleName:urlview视图解析
 * @author admin
 * @version: 8.0
 * @Created: 2015-10-08 14:02
 */
final public class R_N001_B001 extends AppViewGrid implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        //params为运行本规则时所传入的参数
        String viewNum = BeanCtx.g("v", true);
        if (Tools.isBlank(viewNum)) {
            viewNum = (String) params.get("XTagValue");//获得视图编号
        }
        if (Tools.isBlank(viewNum)) {
            return "Can't found view...";
        }
        return getElementHtml(viewNum);
    }

    /**
     * 打开视图运行并输出view 的 html代码
     */
    public String getElementHtml(String gridNum) throws Exception {
        Document gridDoc = AppUtil.getDocByid("BPM_GridList", "GridNum", gridNum, true);
        if (gridDoc.isNull()) {
            return "Error:The view does not exist!";
        }

        boolean readOnly = false;
        if (BeanCtx.g("wf_action", true).equals("read")) {
            readOnly = true;
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

        //这句放在执行事件规则的后面，以免用户传入规则进来执行或取消，不然有安全性问题
        gridDoc.appendFromRequest(BeanCtx.getRequest(), true); // 把请求参数初始化到文档中去

        /* 2.组装html header */
        StringBuilder formBody = new StringBuilder();

        /* 3.获得js header */
        HtmlParser htmlParser = ((HtmlParser) BeanCtx.getBean("HtmlParser"));
        formBody.append("\n<script>\n var FormNum=\"" + gridDoc.g("NewFormNum") + "\",GridNum=\"" + gridNum + "\";WF_Appid=\"" + gridDoc.g("WF_Appid") + "\";\n");
        formBody.append("\nfunction formatlink(v,r){ return \"<a href='' onclick=\\\"RowDblClick(0,'\"+r.WF_OrUnid+\"');return false;\\\" >\"+v+\"</a>\";}");
        formBody.append("\nfunction GroupFormat(value,rows){return value + ' - ' + rows.length + ' " + BeanCtx.getMsg("Common", "items") + "';}");
        formBody.append("\n$(window).resize(function(){$('#dg').datagrid('resize', {width:function(){return document.body.clientWidth;},height:function(){return document.body.clientHeight;}});});\n");

        String jsHeader = gridDoc.g("JsHeader");
        if (Tools.isBlank(jsHeader)) {
            jsHeader = Rdb.getValueBySql("select DefaultCode from BPM_DevDefaultCode where CodeType='Grid_JsHeader'");
        }
        else {
            jsHeader = htmlParser.parserJsTagValue(jsHeader);/* 分析共享标签 */
            jsHeader = htmlParser.parserXTagValue(gridDoc, jsHeader); /* 分析x标签 */
        }
        formBody.append(jsHeader);

        /* 4.追加Body标签 */
        formBody.append("\n</script>\n");

        //增加一个portal的效果
        formBody.append("<div class=\"portlet box grey-cascade\"><div class=\"portlet-title\">");
        formBody.append("<div class=\"caption\"><i class=\"fa fa-table\"></i>").append(gridDoc.g("GridName")).append("</div>");
        formBody.append("<div class=\"tools\"><a href=\"javascript:;\" class=\"collapse\"></a></div></div><div class=\"portlet-body\">");

        /* 追加操作按扭的代码 */
        formBody.append(getToolbar(gridDoc, readOnly));

        // 5.生成grid主体的配置文档
        formBody.append(getGridColumn(gridDoc));

        formBody.append("</div></div>");

        return formBody.toString();

    }

    public String getGridColumn(Document gridDoc) {

        // 1.首先组合data-options
        StringBuilder dataoptions = new StringBuilder(gridDoc.g("dataoptions")); // 已有data-options
        if (dataoptions.length() != 0) {
            dataoptions.append(",");
        }

        dataoptions.append("fit:false,striped:true,scrollbarSize:0,autoRowHeight:false"); // 自动行高

        if (gridDoc.g("RemoteSort").equals("1")) {
            dataoptions.append(",remoteSort:true"); // 允许远程排序
        }
        else {
            dataoptions.append(",remoteSort:false"); // 当前页排序
        }
        dataoptions.append(",border:true"); // 显示边框
        if (gridDoc.g("ShowRowNumber").equals("1")) {
            dataoptions.append(",rownumbers: true"); // 显示行号
        }
        if (gridDoc.g("ShowPageBar").equals("1")) {
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
        if (Tools.isNotBlank(url) && (url.startsWith("D_") || url.startsWith("R_"))) {
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

        columnConfig = columnConfig.substring(spos, columnConfig.lastIndexOf("]") + 1);
        JSONArray jsonArr = JSON.parseArray(columnConfig);
        for (int i = 0; i < jsonArr.size(); i++) {
            JSONObject colConfigItem = (JSONObject) jsonArr.get(i);
            String mobile = colConfigItem.getString("Mobile");
            if (Tools.isBlank(mobile)) {
                mobile = "Y";
            } //默认不在移动设备中显示
            if (BeanCtx.isMobile() && !mobile.equals("Y")) {
                continue; //跳过显示列
            }

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
                else if (formatter.equals("icon")) {
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
    public String getToolbar(Document gridDoc, boolean readOnly) {
        boolean isToolBarFlag = false; //是否有工具条示记，false表示没有，不输出<div>标签
        String toolbarJson = gridDoc.g("ToolbarConfig");
        if (Tools.isBlank(toolbarJson)) {
            toolbarJson = Rdb.getValueBySql("select DefaultCode from BPM_DevDefaultCode where CodeType='Grid_ToolBar'");
        }
        LinkeyUser linkeyUser = (LinkeyUser) BeanCtx.getBean("LinkeyUser");
        StringBuilder toolbarHtml = new StringBuilder();
        toolbarHtml.append("<div class=\"table-toolbar\"><div class=\"row\">");

        //在url中传入wf_action=read时不显示操作条，wf_action=edit或空时显示
        toolbarHtml.append("<div class=\"col-md-6\">");
        if (!readOnly) {
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
                        isToolBarFlag = true;
                        String btnName = toolbarItem.getString("btnName");
                        if (btnName.startsWith("L_")) {
                            btnName = BeanCtx.getLabel(btnName);
                        }
                        String icons = toolbarItem.getString("iconCls").replace("icon-", "fa-");
                        toolbarHtml.append("<button id=\"" + toolbarItem.getString("btnid") + "\"  onclick=\"" + toolbarItem.getString("clickEvent")
                                + ";return false;\" class=\"btn green\"><i class=\"fa " + icons + "\"></i> " + btnName + "</button>");
                    }
                }
            }
        }
        else {
            //不显示按扭
            if (isToolBarFlag) {
                toolbarHtml.append("<img src='linkey/bpm/images/icons/s.gif' height='20px'>");
            }
        }
        toolbarHtml.append("</div>");

        toolbarHtml.append("<div class=\"col-md-6\" ><div class='pull-right'>");
        String searchHtml = "<font size='3'>" + BeanCtx.getMsg("Common", "Search") + ":</font>" + "<input class=\"easyui-searchbox\" data-options=\"searcher:GridDoSearch,prompt:'"
                + BeanCtx.getMsg("Common", "SearchPrompt") + "'\" id=\"SearchKeyWord\" style=\"width:150px;height:30px\"></span>";
        toolbarHtml.append(searchHtml);
        toolbarHtml.append("</div></div>");

        toolbarHtml.append("</div></div>");

        return toolbarHtml.toString();
    }

}