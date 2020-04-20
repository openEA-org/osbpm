package cn.linkey.rulelib.S001;

import cn.linkey.app.AppUtil;
import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;
import java.util.HashMap;

/**
 * @RuleName:图形报表
 * @author admin
 * @version: 8.0
 * @Created: 2014-06-23 11:04
 */
final public class R_S001_E071 implements LinkeyRule {

    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        //获取事件运行参数
        Document formDoc = (Document) params.get("FormDoc"); //表单配置文档
        Document doc = (Document) params.get("DataDoc"); //数据主文档
        String eventName = (String) params.get("EventName");//事件名称
        if (eventName.equals("onFormOpen")) {
            String readOnly = (String) params.get("ReadOnly"); //1表示只读，0表示编辑
            return onFormOpen(doc, formDoc, readOnly);
        }
        else if (eventName.equals("onFormSave")) {
            return onFormSave(doc, formDoc);
        }
        return "1";
    }

    public String onFormOpen(Document doc, Document formDoc, String readOnly) {
        if (readOnly.equals("1")) {
            return "1";
        }
        if (doc.isNewDoc()) {
            //自动获得编号
            String appid = BeanCtx.g("WF_Appid", true);
            String sql = "select GridNum from BPM_GridList where WF_Appid='" + appid + "' and GridType='8' order by GridNum desc";
            String newGridNum = AppUtil.getElNewNum(sql);
            if (Tools.isBlank(newGridNum)) {
                newGridNum = "V_" + appid + "_R001";
            }
            doc.s("GridNum", newGridNum);

            //获得图表模板
            sql = "select Remark,CodeType from BPM_DevDefaultCode where CodeType<>'Echarts_body' and CodeType like 'Echarts_%'";
            String chartType = Rdb.getValueForSelectTagBySql(sql, "");
            doc.s("ChartType", chartType);
        }
        return "1";
    }

    public String onFormSave(Document doc, Document formDoc) {
        doc.s("DataSource", "");
        doc.s("Status", "1");
        doc.s("GridType", "8");
        return "1";
    }

}