package cn.linkey.app;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.form.HtmlParser;
import cn.linkey.util.Tools;

public class AppViewChart implements AppElement {
    public void run(String wf_num) throws Exception {
        BeanCtx.p(getElementHtml(wf_num));
    }

    public String getElementHtml(String gridNum) throws Exception {
        Document gridDoc = AppUtil.getDocByid("BPM_GridList", "GridNum", gridNum, true);
        if (gridDoc.isNull()) {
            return "Error:The view does not exist!";
        }

        //执行视图打开事件
        String ruleNum = gridDoc.g("EventRuleNum");
        if (Tools.isNotBlank(ruleNum)) {
            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put("GridDoc", gridDoc);
            params.put("EventName", "onViewOpen");
            String openMsg = BeanCtx.getExecuteEngine().run(ruleNum, params);
            if (!(openMsg.equals("1"))) {
                return openMsg;
            }
        }
        gridDoc.appendFromRequest(BeanCtx.getRequest(), true);

        //获得视图的主html代码
        String sql = "select DefaultCode from BPM_DevDefaultCode where CodeType='Echarts_body'";
        String formBody = Rdb.getValueBySql(sql);

        String jsHeader = gridDoc.g("JsHeader");
        if (Tools.isBlank(jsHeader)) {
            sql = "select DefaultCode from BPM_DevDefaultCode where CodeType='" + gridDoc.g("ChartType") + "'";
            jsHeader = Rdb.getValueBySql(sql);
        }
        else {
            HtmlParser htmlParser = ((HtmlParser) BeanCtx.getBean("HtmlParser"));
            jsHeader = htmlParser.parserJsTagValue(jsHeader);
            jsHeader = htmlParser.parserXTagValue(gridDoc, jsHeader);
        }
        String height = gridDoc.g("height");
        String width = gridDoc.g("width");
        if (Tools.isBlank(height)) {
            height = "100%";
        }
        if (Tools.isBlank(width)) {
            width = "100%";
        }
        formBody = formBody.replace("{PageTitle}", gridDoc.g("GridName")); //替换标题
        formBody = formBody.replace("{HEIGHT}", height); //替换高度
        formBody = formBody.replace("{WIDTH}", width); //替换宽度
        formBody = formBody.replace("{JSHEADER}", jsHeader); //替换jsheader

        return formBody;
    }

    public String getElementBody(String gridNum, boolean readOnly) throws Exception {
        return this.getElementHtml(gridNum);
    }
}