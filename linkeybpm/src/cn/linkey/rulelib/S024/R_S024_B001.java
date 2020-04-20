package cn.linkey.rulelib.S024;

import java.util.*;
import cn.linkey.dao.*;
import cn.linkey.util.*;
import cn.linkey.doc.*;
import cn.linkey.factory.*;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.form.*;

/**
 * @RuleName:打印时获得文档的所有字段JSON
 * @author admin
 * @version: 8.0
 * @Created: 2014-07-26 12:30
 */
final public class R_S024_B001 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        //params为运行本规则时所传入的参数
        String jsonStr = "{}";
        String formNumber = BeanCtx.g("FormNumber", true);
        String docUnid = BeanCtx.g("DocUnid", true);
        String fdList = BeanCtx.g("FdList");

        //获得表单配置
        ModForm modForm = (ModForm) BeanCtx.getBean("ModForm");
        Document formDoc = modForm.getFormDoc(formNumber);
        if (formDoc.isNull()) {
            BeanCtx.p(jsonStr);
            return "";
        }

        //先看是否有权打开表单
        String roles = formDoc.g("Roles");
        if (!BeanCtx.getLinkeyUser().inRoles(BeanCtx.getUserid(), roles)) {
            BeanCtx.p(jsonStr);
            return "";
        }

        //再看是否有权打开此文档
        String sql = "select * from " + formDoc.g("SqlTableName") + " where WF_OrUnid='" + docUnid + "'";
        Document doc = Rdb.getDocumentBySql(sql);
        if (doc.isNull()) {
            BeanCtx.p(jsonStr);
            return "";
        }
        if (doc.hasItem("WF_AllReaders")) {
            if (!BeanCtx.getLinkeyUser().inRoles(BeanCtx.getUserid(), doc.g("WF_AllReaders"))) {
                BeanCtx.p(jsonStr);
                return "";
            }
        }

        //输出所有字段的json
        StringBuilder jStr = new StringBuilder();
        String[] fdArray = Tools.split(fdList);
        for (String fdName : fdArray) {
            if (jStr.length() > 0) {
                jStr.append(",");
            }
            jStr.append("\"" + fdName + "\":\"").append(Tools.encode(doc.g(fdName))).append("\"");
        }
        jStr.insert(0, "{").append("}");
        BeanCtx.p(jStr.toString());

        return "";
    }
}