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
 * 本类负责生成Custom grid视图
 * 
 * @author Administrator 本类为单例类
 */
public class AppCustomGrid implements AppElement {

    @Override
    public void run(String wf_num) throws Exception {
        if (Tools.isNotBlank(BeanCtx.g("WF_Action", true))) {
            ((AppElement) BeanCtx.getBean("readgridaction")).run(wf_num); // 兼容提交给customgrid的事件
        }
        else {
            getElementHtml(wf_num); // 输出视图的html代码
        }
    }

    /**
     * 获得视图的中间体包含js header
     */
    public String getElementBody(String gridNum, boolean readOnly) throws Exception {
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

        // 根据数据源自动生成url地址2015-8-6
        String url = gridDoc.g("DataSource");
        if (Tools.isNotBlank(url) && (url.startsWith("D_") || url.startsWith("R_"))) {
            url = "r?wf_num=" + url;
        }
        String qry = BeanCtx.getRequest().getQueryString();
        if (Tools.isNotBlank(qry)) {
            url = url + "&" + qry.replace("wf_num=", "wf_gridnum=");
        }
        url += "&rdm='+Math.random()";// 增加一个随时数
        gridDoc.s("dataurl", url);
        // 生成结束

        /* 2.组装html header */
        StringBuilder formBody = new StringBuilder();

        /* 3.获得js header */
        HtmlParser htmlParser = ((HtmlParser) BeanCtx.getBean("HtmlParser"));
        formBody.append("\n<script>\n var FormNum=\"" + gridDoc.g("NewFormNum") + "\",GridNum=\"" + gridNum + "\";WF_Appid=\"" + gridDoc.g("WF_Appid") + "\";\n");
        formBody.append("\nfunction formatlink(v,r){ return \"<a href='' onclick=\\\"RowDblClick(0,'\"+r.WF_OrUnid+\"');return false;\\\" >\"+v+\"</a>\";}");
        formBody.append("\nfunction GroupFormat(value,rows){return value + ' - ' + rows.length + ' " + BeanCtx.getMsg("Common", "items") + "';}");
        // 作为插入视图时不支持resize功能，否则在兼容模式下会出错
        String jsHeader = gridDoc.g("JsHeader");
        if (Tools.isBlank(jsHeader)) {
            jsHeader = Rdb.getValueBySql("select DefaultCode from BPM_DevDefaultCode where CodeType='CustomGrid_JsHeader'");
        }
        else {
            jsHeader = htmlParser.parserJsTagValue(jsHeader);/* 分析JS标签 */
            jsHeader = htmlParser.parserXTagValue(gridDoc, jsHeader); /* 分析x标签 */
        }
        formBody.append(jsHeader);

        /* 4.追加Body标签 */
        formBody.append("\n</script>\n");
        if (!readOnly) {
            // 只有编辑模式才显示操作按扭
            formBody.append(getToolbar(gridDoc)); /* 追加操作按扭的js代码 */
        }
        else {
            formBody.append("<div id='toptoolbar'></div>"); // 不显示操作按扭
        }

        // 5.生成grid主体的配置文档
        String gridBody = gridDoc.g("GridBody");
        gridBody = gridBody.replace("&amp;", "&").replace("&amp;", "&").replace("&lt;", "<").replace("&gt;", ">");
        formBody.append(gridBody);

        return formBody.toString(); // 返回字符串
    }

    /**
     * 获得视图的所有html代码
     */
    public String getElementHtml(String gridNum) throws Exception {
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
        // gridDoc.appendFromRequest(BeanCtx.getRequest(),true); // 把请求参数初始化到文档中去

        // 根据数据源自动生成url地址2015-8-6
        String url = gridDoc.g("DataSource");
        if (Tools.isNotBlank(url) && (url.startsWith("D_") || url.startsWith("R_"))) {
            url = "r?wf_num=" + url;
        }
        String qry = BeanCtx.getRequest().getQueryString();
        if (Tools.isNotBlank(qry)) {
            url = url + "&" + qry.replace("wf_num=", "wf_gridnum=");
        }
        gridDoc.s("dataurl", url);
        // 生成结束

        /* 2.组装html header */
        StringBuilder formBody = new StringBuilder();
        formBody.append("<!DOCTYPE html><html><head><title>");
        formBody.append(gridDoc.g("GridName"));
        formBody.append("</title>");

        // 读取html头文件，如果应用中配置有则以应用优先2015.4.8
        String configHtml = BeanCtx.getAppConfig(gridDoc.g("WF_Appid"), gridDoc.g("HtmlHeader"));
        if (Tools.isBlank(configHtml)) {
            configHtml = BeanCtx.getSystemConfig(gridDoc.g("HtmlHeader"));
        }
        configHtml = configHtml.replace("{LANG}", BeanCtx.getUserLocale().getLanguage() + "_" + BeanCtx.getCountry());
        configHtml = configHtml.replace("{version}", BeanCtx.getSystemConfig("static_version"));
        // 读取结束

        formBody.append(configHtml);

        /* 3.获得js header */
        HtmlParser htmlParser = ((HtmlParser) BeanCtx.getBean("HtmlParser"));
        formBody.append("\n<script>\n var FormNum=\"" + gridDoc.g("NewFormNum") + "\",GridNum=\"" + gridNum + "\";WF_Appid=\"" + gridDoc.g("WF_Appid") + "\";\n");
        formBody.append("\nfunction formatlink(v,r){ return \"<a href='' onclick=\\\"RowDblClick(0,'\"+r.WF_OrUnid+\"');return false;\\\" >\"+v+\"</a>\";}");
        formBody.append("\nfunction GroupFormat(value,rows){return value + ' - ' + rows.length + ' " + BeanCtx.getMsg("Common", "items") + "';}");
        formBody.append("\n$(window).resize(function(){$('#dg').datagrid('resize', {width:function(){return document.body.clientWidth;},height:function(){return document.body.clientHeight;}});});\n");
        String jsHeader = gridDoc.g("JsHeader");
        if (Tools.isBlank(jsHeader)) {
            jsHeader = Rdb.getValueBySql("select DefaultCode from BPM_DevDefaultCode where CodeType='CustomGrid_JsHeader'");
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
        // 2015.4.28修改，增加转义字符的替换，不然自定义视图会显示乱码
        String gridBody = gridDoc.g("GridBody");
        gridBody = gridBody.replace("&amp;", "&").replace("&amp;", "&").replace("&lt;", "<").replace("&gt;", ">");
        formBody.append(gridBody);
        // 修改结束

        formBody.append("<div id=\"win\"></div></body></html>");

        BeanCtx.p(formBody.toString());
        return "";
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
            String gridCodeType = "Grid_ToolBar";
            if (gridDoc.g("GridType").equals("2")) {
                gridCodeType = "EditorGrid_ToolBar";
            }
            toolbarJson = Rdb.getValueBySql("select DefaultCode from BPM_DevDefaultCode where CodeType='" + gridCodeType + "'");
        }
        LinkeyUser linkeyUser = (LinkeyUser) BeanCtx.getBean("LinkeyUser");
        StringBuilder toolbarHtml = new StringBuilder();
        toolbarHtml.append("<div class=\"toptoolbar\" id=\"toptoolbar\" >");

        // 在url中传入wf_action=read时不显示操作条，wf_action=edit或空时显示
        if (!BeanCtx.g("wf_action", true).equals("read")) {
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
        toolbarHtml.append("</div>");
        return toolbarHtml.toString();
    }

}
