package cn.linkey.rulelib.S005;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.form.HtmlParser;
import cn.linkey.form.ModForm;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * @RuleName:打印流程处理单
 * @author admin
 * @version: 8.0
 * @Created: 2014-06-09 14:12
 */
final public class R_S005_B004 implements LinkeyRule {
	
	String uiType = "3";  //20180205 暂时写死easyui类型
	
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        //params为运行本规则时所传入的参数
        String docUnid = BeanCtx.g("DocUnid", true);
        if (Tools.isBlank(docUnid)) {
            BeanCtx.p("文档id不能为空!");
            return "";
        }
        String sql = "select WF_Processid from BPM_AllDocument where WF_OrUnid='" + docUnid + "'";
        String processid = Rdb.getValueBySql(sql);
        if (Tools.isBlank(processid)) {
            BeanCtx.p("文档还处于新建状态,不能打印!");
            return "";
        }

        //获得打印表单或模板
        Document pdoc = Rdb.getDocumentBySql("select * from BPM_ModProcessList where Processid='" + processid + "'");
        String printTemplate = pdoc.g("PrintTemplate");
        if (Tools.isNotBlank(printTemplate)) {
            //使用word模板来打印
            String url = "form?wf_num=F_S024_A003&Processid=" + processid + "&DocUnid=" + docUnid;
            BeanCtx.getResponse().sendRedirect(url);
        }

        //使用普通表单来打印
        String printForm = pdoc.g("PrintForm");
        if (Tools.isBlank(printForm)) {
            printForm = pdoc.g("FormNumber"); //使用主表单来打印
        }

        //获得主文档
        Document dataDoc = Rdb.getDocumentBySql("select * from BPM_AllDocument where WF_OrUnid='" + docUnid + "'");
        
        // 20190110 修复流程指定自定义的业务数据表后，打印无法显示的问题
        String sqlTemp = "SELECT * FROM BPM_ModProcessList WHERE Processid='" + dataDoc.g("WF_Processid") + "'";
	    Document docTemp = Rdb.getDocumentBySql(sqlTemp);
	    String extendTableName = docTemp.g("ExtendTableName");
	    if (Tools.isNotBlank(extendTableName) && !"xmldata".equalsIgnoreCase(extendTableName)) {
	    	sqlTemp = "SELECT * FROM " + extendTableName + " WHERE WF_OrUnid='" + docUnid + "'";
	    	Document extendDoc = Rdb.getDocumentBySql(sqlTemp);
	    	HashMap<String, String> extendDocAllItems = extendDoc.getAllItems();
	    	for (String itemKey : extendDocAllItems.keySet()) {
	    		if (!dataDoc.hasItem(itemKey)) {
	    			dataDoc.s(itemKey, extendDocAllItems.get(itemKey));
	    		}
	    	}
	    }
	    // 20190110 END
        
        //获得表单配置
        ModForm insModForm = (ModForm) BeanCtx.getBean("ModForm");
        Document formDoc = null;
        formDoc = insModForm.getFormDoc(printForm); // 表单
        insModForm.initAppFormFieldConfig(formDoc); //设置表单字段配置信息

        //运行表单打开事件
        String ruleNum = formDoc.g("EventRuleNum");
        if (Tools.isNotBlank(ruleNum)) {
            params = new HashMap<String, Object>();
            params.put("FormDoc", formDoc);
            params.put("DataDoc", dataDoc);
            params.put("EventName", "onFormOpen");
            params.put("ReadOnly", "1");
            String ruleStr = BeanCtx.getExecuteEngine().run(ruleNum, params); // 运行表单打开事件
            if (!ruleStr.equals("1")) {
                // 说明事件中要退出本次表单打开
                return ruleStr;
            }
        }

        HtmlParser htmlParser = ((HtmlParser) BeanCtx.getBean("HtmlParser"));
        String insFormBodyHtml = formDoc.g("FormBody");
        insFormBodyHtml = htmlParser.parserHtml(dataDoc, insFormBodyHtml, true, true, "", uiType);/* 解析html代码 */

        StringBuilder formBody = new StringBuilder();
        formBody.append("<!DOCTYPE html><html><head><title>");
        formBody.append(dataDoc.g("Subject"));
        formBody.append("</title>");
        formBody.append(BeanCtx.getSystemConfig("AppFormHtmlHeader"));
        formBody.append("</head><script>function formonload(){}</script><body style=\"margin:5px 5px 0px 5px;\" scroll=auto >");
        formBody.append(insFormBodyHtml);
        formBody.append("<input type='hidden' name='WF_DocUnid' id='WF_DocUnid' value='" + docUnid + "'>");
        formBody.append("</body></html>");
        BeanCtx.p(formBody.toString());

        return "";
    }
}