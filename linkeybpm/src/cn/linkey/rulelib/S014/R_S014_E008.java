package cn.linkey.rulelib.S014;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;

/**
 * @RuleName:修改流转记录日记
 * @author admin
 * @version: 8.0
 * @Created: 2015-01-08 00:30
 */
final public class R_S014_E008 implements LinkeyRule {

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

    public String onFormOpen(Document doc, Document formDoc, String readOnly) throws Exception {
        //当表单打开时
        if (readOnly.equals("1")) {
            return "1";
        } //如果是阅读状态则可不执行
        if (doc.isNewDoc()) {
            //可以对表单字段进行初始化如:doc.s("fdname",fdvalue),可以获取字段值 doc.g("fdname")

        }
        return "1"; //成功必须返回1，否则表示退出并显示返回的字符串
    }

    public String onFormSave(Document doc, Document formDoc) throws Exception {
        //当表单存盘前
        String sql = "select Remark from BPM_InsRemarkList where WF_OrUnid='" + doc.getDocUnid() + "'";
        String remark = Rdb.getValueBySql(sql);
        BeanCtx.userlog(doc.getDocUnid(), "在流程监控中修改流转记录!", "意见由(" + remark + ")修改为:" + doc.g("Remark"));

        return "1"; //成功必须返回1，否则表示退出存盘
    }

}