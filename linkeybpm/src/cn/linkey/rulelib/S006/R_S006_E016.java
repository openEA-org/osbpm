package cn.linkey.rulelib.S006;

import java.util.HashMap;

import cn.linkey.dao.RdbCache;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * @RuleName:注册岗位成员
 * @author admin
 * @version: 8.0
 * @Created: 2014-04-24 12:29
 */
final public class R_S006_E016 implements LinkeyRule {

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
        else {
            doc.s("Folderid", doc.g("OrgClass") + "_" + doc.g("Folderid") + "_" + doc.g("Deptid"));
        }
        return "1"; //成功必须返回1，否则表示退出并显示返回的字符串
    }

    public String onFormSave(Document doc, Document formDoc) {
        //当表单存盘前
        doc.s("RoleType", "2");
        String deptid = doc.g("Deptid");
        Document deptdoc = BeanCtx.getLinkeyUser().getDeptDoc(deptid);
        if (!deptdoc.isNull()) {
            doc.s("Folderid", deptdoc.g("Folderid"));
            doc.s("OrgClass", deptdoc.g("OrgClass"));
        }

        //清除成员的缓存，重新计算角色
        String member = doc.g("Member");
        String[] memberArray = Tools.split(member);
        for (String userid : memberArray) {
            RdbCache.remove("UserCacheStrategy", userid);
        }

        return "1"; //成功必须返回1，否则表示退出存盘
    }

}