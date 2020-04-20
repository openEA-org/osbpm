package cn.linkey.app.layui;

import java.util.HashMap;

import org.apache.commons.lang3.StringEscapeUtils;

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
import cn.linkey.util.Tools;

/**
 * 
 * Copyright © 2018 A Little Bao. All rights reserved.
 * 
 * @ClassName: AppViewGrid.java
 * @Description: 本类负责生成layui_view grid视图
 *
 * @version: v1.0.0
 * @author: Alibao
 * @date: 2018年1月16日 上午11:35:55
 *
 *        Modification History: Date Author Version Description
 *        ---------------------------------------------------------* 2018年1月16日
 *        Alibao v1.0.0 修改原因
 */
public class AppViewGrid implements AppElement {

	private String groudBtn = "0"; // 按钮组判断

	@Override
	public void run(String wf_num) throws Exception {
		getElementHtml(wf_num); // 输出视图的html代码
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

		// 这句放在执行事件规则的后面，以免用户传入规则进来执行或取消，不然有安全性问题
		gridDoc.appendFromRequest(BeanCtx.getRequest(), true); // 把请求参数初始化到文档中去

		/* 2.组装html header */
		StringBuilder formBody = new StringBuilder();
		formBody.append("<!DOCTYPE html><html><head><title>");
		formBody.append(gridDoc.g("GridName"));
		formBody.append("</title>");
		String htmlHeaderConfig = "layui_AppGridHtmlHeader";
		if (BeanCtx.isMobile()) {
			htmlHeaderConfig = htmlHeaderConfig.concat("_Mobile");
		}
		// if(!BeanCtx.getCountry().equals("CN")){
		// htmlHeaderConfig=htmlHeaderConfig.concat("_").concat(BeanCtx.getCountry());
		// }

		// 读取html头文件，如果应用中配置有则以应用优先2015.4.8
		String configHtml = BeanCtx.getAppConfig(gridDoc.g("WF_Appid"), htmlHeaderConfig);
		if (Tools.isBlank(configHtml)) {
			configHtml = BeanCtx.getSystemConfig(htmlHeaderConfig);
		}
		configHtml = configHtml.replace("{LANG}", BeanCtx.getUserLocale().getLanguage() + "_" + BeanCtx.getCountry());
		configHtml = configHtml.replace("{version}", BeanCtx.getSystemConfig("static_version"));
		// 读取结束

		formBody.append(configHtml);
		// 20180116
		/*
		 * if (Tools.isNotBlank(gridDoc.g("GroupField"))) { formBody.
		 * append("<script type=\"text/javascript\" src=\"linkey/bpm/easyui/datagrid-groupview.js\"></script>"
		 * );// 分组显示时需要 }
		 */

		/* 3.获得js header */
		HtmlParser htmlParser = ((HtmlParser) BeanCtx.getBean("HtmlParser"));
		// 20180116
		// formBody.append("\n<script>\n");
		formBody.append("\n<script>\n var FormNum=\"" + gridDoc.g("NewFormNum") + "\",GridNum=\"" + gridNum
				+ "\";WF_Appid=\"" + gridDoc.g("WF_Appid") + "\";\n");
		// formBody.append("\n<script>\n var FormNum=\"" +
		// gridDoc.g("NewFormNum") + "\",GridNum=\"" + gridNum +
		// "\";WF_Appid=\"" + gridDoc.g("WF_Appid") + "\";\n");
		// formBody.append("\nfunction formatlink(v,r){ return \"<a href=''
		// onclick=\\\"RowDblClick(0,'\"+r.WF_OrUnid+\"');return false;\\\"
		// >\"+v+\"</a>\";}");
		// formBody.append("\nfunction GroupFormat(value,rows){return value + '
		// - ' + rows.length + ' " + BeanCtx.getMsg("Common", "items") + "';}");
		// formBody.append("\n$(window).resize(function(){$('#dg').datagrid('resize',
		// {width:function(){return
		// document.body.clientWidth;},height:function(){return
		// document.body.clientHeight;}});});\n");
		String jsHeader = gridDoc.g("JsHeader");
		if (Tools.isBlank(jsHeader)) {
			jsHeader = Rdb
					.getValueBySql("select DefaultCode from BPM_DevDefaultCode where CodeType='layui_Grid_JsHeader'");
		} else {
			jsHeader = htmlParser.parserJsTagValue(jsHeader);/* 分析共享标签 */
			jsHeader = htmlParser.parserXTagValue(gridDoc, jsHeader); /* 分析x标签 */
		}
		formBody.append(jsHeader);

		// 20180205 添加按钮组判断
		if ("1".equals(gridDoc.g("groudBtn"))) {
			groudBtn = "1";
		} else {
			groudBtn = "0";
		}

		/* 4.追加Body标签 */
		formBody.append("\n</script>\n</head>\n<body style=\"margin:0px;\">\n");

		formBody.append(getToolbar(gridDoc, readOnly)); /* 追加操作按扭的js代码 */

		// 5.生成grid主体的配置文档
		/* 获得列的配置字符串 */
		// String columnStr=getGridColumn(gridDoc);
		formBody.append(getGridColumn(gridDoc));

		formBody.append("<div id=\"win\"></div></body></html>");
		// BeanCtx.out("formBody="+formBody.toString());
		BeanCtx.p(formBody.toString());
		return "";

	}

	/**
	 * 生成插入表单中的视图的HTML代码
	 * 
	 * @return
	 */
	public String getElementBody(String gridNum, boolean readOnly) throws Exception {
		Document gridDoc = AppUtil.getDocByid("BPM_GridList", "GridNum", gridNum, true);
		if (gridDoc.isNull()) {
			return "";
		}

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

		/* 3.获得js header */
		HtmlParser htmlParser = ((HtmlParser) BeanCtx.getBean("HtmlParser"));
		formBody.append("\n<script>\n var FormNum=\"" + gridDoc.g("NewFormNum") + "\",GridNum=\"" + gridNum + "\";\n");
		formBody.append(
				"\nfunction formatlink(v,r){ return \"<a href='' onclick=\\\"RowDblClick(0,'\"+r.WF_OrUnid+\"');return false;\\\" >\"+v+\"</a>\";}");
		formBody.append("\nfunction GroupFormat(value,rows){return value + ' - ' + rows.length + ' "
				+ BeanCtx.getMsg("Common", "items") + "';}");
		// 作为插入视图时不支持resize功能，否则在兼容模式下会出错
		// formBody.append("\n$(window).resize(function(){$('#dg').datagrid('resize',
		// {width:function(){return
		// document.body.clientWidth;},height:function(){return
		// document.body.clientHeight;}});});\n");
		String jsHeader = gridDoc.g("JsHeader");
		if (Tools.isBlank(jsHeader)) {
			jsHeader = Rdb
					.getValueBySql("select DefaultCode from BPM_DevDefaultCode where CodeType='layui_Grid_JsHeader'");
		} else {
			jsHeader = htmlParser.parserJsTagValue(jsHeader);/* 分析JS标签 */
			jsHeader = htmlParser.parserXTagValue(gridDoc, jsHeader); /* 分析x标签 */
		}
		formBody.append(jsHeader);

		/* 4.追加Body标签 */
		formBody.append("\n</script>\n");
		formBody.append(getToolbar(gridDoc, readOnly)); /* 追加操作按扭的代码 */

		// 5.生成grid主体的配置文档
		formBody.append(getGridColumn(gridDoc));
		return formBody.toString();
	}

	public String getGridColumn(Document gridDoc) {

//		// 1.首先组合data-options
//		StringBuilder layData = new StringBuilder(gridDoc.g("dataoptions")); // 已有data-options
//		if (layData.length() != 0) {
//			layData.append(",");
//		} else {
//			layData.append("{");
//		}
//		layData.append("cellMinWidth: 50");
//
//		
//		if (gridDoc.g("ShowPageBar").equals("1")) {
//			layData.append(",page: true"); // 显示分页条
//		}
//
//		String pageSize = gridDoc.g("PageSize"); // 显示记录数
//		if (Tools.isBlank(pageSize)) {
//			pageSize = "25";
//		}
//		String pageList = gridDoc.g("PageList"); // 选择分页数
//		if (Tools.isBlank(pageList)) {
//			pageList = "20,25,40,60";
//		}
//		String ShowRowNumberFirst = ""; // 分页工具类 ，显示首页
//		if (gridDoc.g("ShowRowNumberFirst").equals("1")) {
//			ShowRowNumberFirst = ",first:'首页'";
//		}
//		String ShowRowNumberEnd = ""; // 分页工具类 ，显示尾页
//		if (gridDoc.g("ShowRowNumberEnd").equals("1")) {
//			ShowRowNumberEnd = ",last:'尾页'";
//		}
//
//		String openSkipPage = "";
//		String openSkipPage2 = "";
//		if (gridDoc.g("openSkipPage").equals("1")) {
//			openSkipPage = ",skip: true";
//			openSkipPage2 = ", 'skip'";
//		}
//
//		String initPageNum = gridDoc.g("initPageNum");// 自定义初始页
//		if (Tools.isBlank(initPageNum)) {
//			initPageNum = "1";
//		}
//		String showPageNumlist = gridDoc.g("showPageNumlist"); // 显示多少个连续页面
//		if (Tools.isBlank(showPageNumlist)) {
//			showPageNumlist = "3";
//		}
//
//		layData.append(",id:'dg'"); // 添加ID
//
//		String pagetools = ",page: {layout: ['limit', 'count', 'prev', 'page', 'next'" + openSkipPage2 + "],limits:"
//				+ pageList + ",limit:" + pageSize + ",curr: " + initPageNum + ",groups: " + showPageNumlist
//				+ ShowRowNumberFirst + ShowRowNumberEnd + openSkipPage + "}";
//
//		layData.append(pagetools); // 分页工具栏
//
//		String url = gridDoc.g("DataSource");
//		if (Tools.isNotBlank(url) && (url.startsWith("D_") || url.startsWith("R_"))) {
//			url = "r?wf_num=" + url;
//		}
//		String qry = BeanCtx.getRequest().getQueryString();
//		if (Tools.isNotBlank(qry)) {
//			// qry=Tools.decodeUrl(qry).replace("wf_num=", "wf_gridnum=");
//			url = url + "&" + qry.replace("wf_num=", "wf_gridnum=");
//		}
//		if (Tools.isNotBlank(gridDoc.g("DataSourceParams"))) {
//			url += "&" + gridDoc.g("DataSourceParams"); // 加入默认传入的参数到url中
//		}
//		url += "&rdm='+Math.random()}";// 增加一个随时数
//		layData.append(",url:'" + url); // 数据源地址
		
		// 1.首先组合data-options
				StringBuilder layData = new StringBuilder(gridDoc.g("dataoptions")); // 已有data-options
				if (layData.length() != 0) {
					layData.append(",");
				}else{
					layData.append("{");
				}
				//layData.append("elem: '#dg'");
				layData.append("cellMinWidth: 50");

				String pageSize = gridDoc.g("PageSize"); // 显示记录数
				if (Tools.isBlank(pageSize)) {
					pageSize = "25";
				}
				String pageList = gridDoc.g("PageList"); // 选择分页数
				if (Tools.isBlank(pageList)) {
					pageList = "20,25,40,60";
				}
				String ShowRowNumberFirst = ""; // 分页工具类 ，显示首页
				if (gridDoc.g("ShowRowNumberFirst").equals("1")) {
					ShowRowNumberFirst = ",first:'首页'";
				}
				String ShowRowNumberEnd = ""; // 分页工具类 ，显示尾页
				if (gridDoc.g("ShowRowNumberEnd").equals("1")) {
					ShowRowNumberEnd = ",last:'尾页'";
				}

				String openSkipPage = "";
				String openSkipPage2 = "";
				if (gridDoc.g("openSkipPage").equals("1")) {
					openSkipPage = ",skip: true";
					openSkipPage2 = ", 'skip'";
				}

				String initPageNum = gridDoc.g("initPageNum");// 自定义初始页
				if (Tools.isBlank(initPageNum)) {
					initPageNum = "1";
				}
				String showPageNumlist = gridDoc.g("showPageNumlist"); // 显示多少个连续页面
				if (Tools.isBlank(showPageNumlist)) {
					showPageNumlist = "3";
				}

				layData.append(",id:'dg'"); // 添加ID

				String requestData = ",request: {";// 自定义请求参数名字
				String responseData = ",response: {";// 自定义接收参数名字
				String whereData = ",where: {"; // 自定义参数
				// responseData += gridDoc.g("setJSONFormat");

				String pagetools = ",page: {layout: ['limit', 'count', 'prev', 'page', 'next'" + openSkipPage2 + "],limits:"
						+ pageList + ",limit:" + pageSize + ",curr: " + initPageNum + ",groups: " + showPageNumlist
						+ ShowRowNumberFirst + ShowRowNumberEnd + openSkipPage + "}";

				if (gridDoc.g("ShowPageBar").equals("1")) {
					layData.append(pagetools); // 显示分页、分页工具栏
					requestData += "pageName: 'page',limitName: 'rows'"; // 添加分页参数
				}

				layData.append(requestData + "}"); // 修改自定义请求参数名字
				layData.append(responseData + "}"); // 修改自定义接收参数名字

				String url = gridDoc.g("DataSource");
				if (Tools.isNotBlank(url) && (url.startsWith("D_") || url.startsWith("R_"))) {
					url = "r?wf_num=" + url;
				}
				String qry = BeanCtx.getRequest().getQueryString();
				if (Tools.isNotBlank(qry)) {
					// qry=Tools.decodeUrl(qry).replace("wf_num=", "wf_gridnum=");
					url = url + "&" + qry.replace("wf_num=", "wf_gridnum=");
				}
				if (Tools.isNotBlank(gridDoc.g("DataSourceParams"))) {
					url += "&" + gridDoc.g("DataSourceParams"); // 加入默认传入的参数到url中
				}
				url += "&rdm='+Math.random()";// 增加一个随时数
				layData.append(",url:'" + url); // 数据源地址

				String sortOrder = "asc";
				if (gridDoc.g("SortOrder").equals("1")) {
					sortOrder = "desc";
				}
				if (Tools.isNotBlank(gridDoc.g("SortName"))) {
					if (gridDoc.g("RemoteSort").equals("1")) { // 远程排序
						String sortStrOrg = gridDoc.g("SortName");
						whereData += ("sort:'" + sortStrOrg + "',order:'" + sortOrder + "'");
					} else {
						String[] sortNames = gridDoc.g("SortName").split(",");
						String sortStr = "";
						for (String sortName : sortNames) {
							sortStr += (",field:'" + sortName + "'");
						}
						sortStr = sortStr.substring(1);
						layData.append(",initSort: {" + sortStr + ", type:'" + sortOrder + "'}"); // 显示时的排序字段
																									// 排序方式
					}
				}

				layData.append(whereData + "}"); // 传递参数

				if (Tools.isNotBlank(gridDoc.g("layStyle"))) { // 自定义皮肤风格
					layData.append(",skin: '" + gridDoc.g("layStyle") + "'");
				}

				if (Tools.isNotBlank(gridDoc.g("layStyleLG"))) { // 自定义视图(表格)大小
					layData.append(",size: '" + gridDoc.g("layStyleLG") + "'");
				}

				if (gridDoc.g("striped").equals("1")) { // 开启交叉背景行
					layData.append(",even: true");
				}

				if (Tools.isNotBlank(gridDoc.g("notRowData"))) { // 没有数据时提示文本信息
					layData.append(",text: {none: '" + gridDoc.g("notRowData") + "'}");
				}

				if (gridDoc.g("notAutoHeight").equals("1")) { // 自定义表格高度
					if (Tools.isNotBlank(gridDoc.g("fixedHeight"))) {
						layData.append(",height: '" + gridDoc.g("fixedHeight") + "'");
					}
				}

		// 2.组合表格体
		// StringBuilder tableStr = new StringBuilder("<table id=\"dg\"
		// class=\"easyui-datagrid\" data-options=\"");
		StringBuilder tableStr = new StringBuilder("<table id=\"dg\" class=\"layui-table\" lay-data=\"");
		tableStr.append(layData); // layData
		tableStr.append("}"); 
		tableStr.append("\" lay-filter=\"dg\"><thead><tr>");
		

		// 2.5 显示行号
		if (gridDoc.g("ShowRowNumber").equals("1")) {
			tableStr.append("<th lay-data=\"{type:'numbers'}\"></th>");
		}

		// 3.显示复选框
		if (gridDoc.g("ShowCheckBox").equals("1")) {
			if (gridDoc.g("ShowCheckBoxSelect").equals("1")) {
				tableStr.append("<th lay-data=\"{type:'checkbox',LAY_CHECKED:'true'}\"></th>");
			}else{
				tableStr.append("<th lay-data=\"{type:'checkbox'}\"></th>");
			}
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
			} // 默认不在移动设备中显示
			if (BeanCtx.isMobile() && !mobile.equals("Y")) {
				continue; // 跳过显示列
			}

			String colName = colConfigItem.getString("ColName");
			String fdName = colConfigItem.getString("FdName");
			if (colName != null) {
				if (colName.startsWith("L_")) {
					colName = BeanCtx.getLabel(colName);
				} // 替换国际化标签
			}
			if (Tools.isBlank(colName) && !fdName.equals("icon")) {
				colName = fdName;
			}
//			String dataoption = "field:'" + fdName + "',width:" + colConfigItem.getString("ColWidth");
//			String align = colConfigItem.getString("Align");
//			if (Tools.isNotBlank(align)) {
//				dataoption += ",align:'" + align + "'";
//			}
//			String sort = colConfigItem.getString("Sort");
//			if (sort == null || !sort.equals("N")) {
//				dataoption += ",sort: true";
//			}
//			if (Tools.isNotBlank(colConfigItem.getString("dataoptions"))) {
//				dataoption += "," + colConfigItem.getString("dataoptions");
//			}
//			String formatter = colConfigItem.getString("Format"); // 格式化
//			if (Tools.isNotBlank(formatter)) {
//				if (formatter.equals("link")) {
//					dataoption += ",formatter:formatlink";
//				} else if (formatter.indexOf("color") != -1) {
//					String color = formatter.substring(6, formatter.length() - 1);
//					dataoption += ",formatter:function(v,r){return '<font color=" + color + " >'+v+'</font>';}";
//				} else if (formatter.equals("icon")) {
//					colName = "";
//					// dataoption += ",formatter:function(v,r){return '<img
//					// src=linkey/bpm/images/icons/doclist.gif >';}";
//					dataoption += "";
//				} else if (formatter.indexOf(".gif") != -1 || formatter.indexOf(".png") != -1) {
//					dataoption += ",formatter:function(v,r){return '<img src=linkey/bpm/images/icons/" + formatter
//							+ " >';}";
//				} else {
//					dataoption += ",formatter:" + colConfigItem.getString("FormatFun");
//				}
//			}
			StringBuffer layCols = new StringBuffer();
			layCols.append("field: '" + fdName + "', title: '" + colName + "'");
			
			String align = colConfigItem.getString("Align"); 
			if (Tools.isNotBlank(align)) {
				layCols.append(",align:'" + align + "'");
			}
			
			String colTemplet = colConfigItem.getString("colTemplet"); //自定义模板
			if (Tools.isNotBlank(colTemplet)) {
				if(!colTemplet.substring(0, 5).contains("<div>")){ //函数转义
					colTemplet = StringEscapeUtils.escapeHtml4(colTemplet);
					layCols.append(",templet:"+colTemplet);
				}else{                                            //直接赋值模版字符
					colTemplet = StringEscapeUtils.escapeHtml4(colTemplet);
					layCols.append(",templet:'"+colTemplet+"'");
				}
			}
			
			String sort = colConfigItem.getString("Sort"); //是否支持排序
			if (sort == null || !sort.equals("N")) {
				layCols.append(",sort: true");
			}
			
			String colWidth = colConfigItem.getString("ColWidth"); //固定列宽
			if (Tools.isNotBlank(colWidth)) {
				layCols.append(",minWidth:" + colWidth);
			}
			
			String minWidth = colConfigItem.getString("MinWidth"); //最小列宽
			if (Tools.isNotBlank(minWidth)) {
				layCols.append(",minWidth:" + minWidth);
			}
			
			if (Tools.isNotBlank(colConfigItem.getString("cloStyle"))) { // 此处要求传入的格式
																			// key:'value'
				layCols.append(",style:'" + colConfigItem.getString("cloStyle")+"'");
			}
			tableStr.append("<th lay-data=\"{" + layCols + "}\" >" + colName + "</th>");
		}
		tableStr.append("</thead></table>");

		return tableStr.toString();
	}

	/**
	 * 获得表单按扭的js代码
	 * 
	 * @param toolbarXml
	 *            按扭的xml描述
	 * @param readOnly
	 *            文档是否只读
	 * @param isNewDoc
	 *            是否新文档
	 * @return
	 */
	public String getToolbar(Document gridDoc, boolean readOnly) {
		boolean isToolBarFlag = false; // 是否有工具条示记，false表示没有，不输出<div>标签
		String toolbarJson = gridDoc.g("ToolbarConfig");
		if (Tools.isBlank(toolbarJson)) {
			toolbarJson = Rdb
					.getValueBySql("select DefaultCode from BPM_DevDefaultCode where CodeType='layui_Grid_ToolBar'");
		}
		LinkeyUser linkeyUser = (LinkeyUser) BeanCtx.getBean("LinkeyUser");
		StringBuilder toolbarHtml = new StringBuilder();
		toolbarHtml.append("<div class=\"toptoolbar\" id=\"toptoolbar\" style=\"padding-top: 10px;\">");

		// 先显示搜索条，兼容ie7
		if (gridDoc.g("ShowSearchBar").equals("1")) {
			/*
			 * String searchHtml = gridDoc.g("WF_SearchBar"); if
			 * (Tools.isBlank(searchHtml)) { // 使用默认的搜索条 isToolBarFlag = true;
			 * searchHtml = "<span style='float:right;padding-right:20px'>" +
			 * BeanCtx.getMsg("Common", "Search") +
			 * ":<input class=\"easyui-searchbox\" data-options=\"searcher:GridDoSearch,prompt:'"
			 * + BeanCtx.getMsg("Common", "SearchPrompt") +
			 * "'\" id=\"SearchKeyWord\" style=\"width:150px;\"></span>"; }
			 * toolbarHtml.append(searchHtml);
			 */

			String searchHtml = "<span style='float:right;padding-right:20px'><div class=\"demoTable\">  搜索关键字：  <div class=\"layui-inline\">    <input class=\"layui-input\" name=\"id\" id=\"demoReload\" autocomplete=\"off\">  </div>  <button class=\"layui-btn\" data-type=\"searchreload\">搜索</button></div></span>";
			toolbarHtml.append(searchHtml);
		}

		// 在url中传入wf_action=read时不显示操作条，wf_action=edit或空时显示
		if (!readOnly) {
			int spos = toolbarJson.indexOf("[");
			toolbarJson = toolbarJson.substring(spos, toolbarJson.lastIndexOf("]") + 1);
			// BeanCtx.out(toolbarJson);
			JSONArray jsonArr = JSON.parseArray(toolbarJson);
			if(groudBtn.equals("1")){
				toolbarHtml.append("<div class=\"layui-btn-group\">");
			}
			for (int i = 0; i < jsonArr.size(); i++) {
				JSONObject toolbarItem = (JSONObject) jsonArr.get(i);
				String roleNumber = toolbarItem.getString("RoleNumber"); // 绑定角色编号
				if (linkeyUser.inRoles(BeanCtx.getUserid(), roleNumber)) {
					String hiddenfd = toolbarItem.getString("hiddenfd"); // 隐藏字段
					if (Tools.isBlank(hiddenfd) || gridDoc.g(hiddenfd).equals("true")) {
						isToolBarFlag = true;
						String btnName = toolbarItem.getString("btnName");
						String btnClass = toolbarItem.getString("btnclass");  //获取自定义的按钮样式
						if(Tools.isBlank(btnClass)){
							btnClass = "layui-btn";
						}
						//20180307
						String btnFilter = toolbarItem.getString("btnfilter");  //表格提交过滤器
						if(Tools.isNotBlank(btnFilter)){
							btnFilter = "lay-submit=\"\" lay-filter=\"" + btnFilter + "\"";
						}
						
						String btntype = toolbarItem.getString("btntype");  //获取button类型，默认时，会默认提交表格，所以不想提交表格可以用reset
						if(Tools.isNotBlank(btntype)){
							btntype = " type=\"" + btntype + "\"";
						}else{
							btntype = "";
						}
						
						if (btnName.startsWith("L_")) { 
							btnName = BeanCtx.getLabel(btnName);
						}
						
						String iconCls = toolbarItem.getString("iconCls"); //20180424 添加按钮图标
						if(Tools.isNotBlank(iconCls)){
							btnName = "<i class=\"layui-icon\">" + iconCls + "</i>" + btnName;
						}
						 toolbarHtml.append("<button id=\"" + toolbarItem.getString("btnid") + "\" class=\""
									+ btnClass + "\" "+ btnFilter + btntype +" data-type=\"" + toolbarItem.getString("clickEvent") + "\">" + btnName
									+ "</button>");
					}
				}
			}
			if(groudBtn.equals("1")){
				toolbarHtml.append("</div>");
			}
			
		} else {
			// 不显示按扭
			if (isToolBarFlag) {
				toolbarHtml.append("<img src='linkey/bpm/images/icons/s.gif' height='20px'>");
			}
		}
		toolbarHtml.append("</div>");
		if (isToolBarFlag) {
			return toolbarHtml.toString();
		} else {
			return "<div id='toptoolbar'></div>"; // 没有工具条的时候什么也不输出
		}
	}


}
