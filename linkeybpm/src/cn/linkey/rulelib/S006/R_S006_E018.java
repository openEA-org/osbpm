package cn.linkey.rulelib.S006;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * @RuleName:修改密码
 * @author admin
 * @version: 8.0
 * @Created: 2014-05-16 13:34
 */
final public class R_S006_E018 implements LinkeyRule {

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
        return "1"; //成功必须返回1，否则表示退出并显示返回的字符串
    }

    public String onFormSave(Document doc, Document formDoc) {
        //当表单存盘前
        String oldpwd = Tools.md5(doc.g("oldpwd"));
        String sql = "select * from BPM_OrgUserList where Userid='" + BeanCtx.getUserid() + "' and Password='" + oldpwd + "'";
        if (!Rdb.hasRecord(sql)) {
            return "旧密码错误!";
        }
        else {
            sql = "update BPM_OrgUserList set Password='" + Tools.md5(doc.g("newpwd")) + "' where Userid='" + BeanCtx.getUserid() + "'";
            int i = Rdb.execSql(sql);
            if (i > 0) {
                return "密码修改成功,下次登录请用新密码!";
            }
            else {
                return "密码修改失败!";
            }
        }
    }

}