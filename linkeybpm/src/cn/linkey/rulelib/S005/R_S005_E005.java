package cn.linkey.rulelib.S005;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;

/**
 * @RuleName:保存和获取常用意见
 * @author admin
 * @version: 8.0
 * @Created: 2014-05-16 13:26
 */
final public class R_S005_E005 implements LinkeyRule {

    @Override
    public String run(HashMap<String, Object> params) {
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
        //当表单打开时
        if (readOnly.equals("1")) {
            return "1";
        } //如果是阅读状态则可不执行
        if (doc.isNewDoc()) {
            //可以对表单字段进行初始化如:doc.s("fdname",fdvalue),可以获取字段值 doc.g("fdname")

        }
        String sql = "select WF_MyRemark from BPM_UserProfile where Userid='" + BeanCtx.getUserid() + "'";
        doc.s("Remark", Rdb.getValueBySql(sql));

        return "1"; //成功必须返回1，否则表示退出并显示返回的字符串
    }

    public String onFormSave(Document doc, Document formDoc) {
        //当表单存盘前
        String remark = doc.g("Remark");
        remark = remark.replace("'", "''");
        String sql = "update BPM_UserProfile set WF_MyRemark='" + remark + "' where Userid='" + BeanCtx.getUserid() + "'";
        int i = Rdb.execSql(sql);
        if (i > 0) {
            return "常用意见成功保存！";
        }
        else {
            return "常用意见保存失败!";
        }
    }

}