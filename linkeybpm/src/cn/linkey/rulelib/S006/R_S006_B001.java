package cn.linkey.rulelib.S006;

import java.util.HashMap;
import java.util.LinkedHashSet;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.doc.Documents;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * 获得所有角色列表
 * 
 * @author Administrator
 */
public class R_S006_B001 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) {
        String appid = BeanCtx.g("WF_Appid", true);
        String sql = "";
        if (Tools.isBlank(appid)) {
            sql = "select RoleName,RoleNumber,WF_Appid from BPM_OrgRoleList where RoleType='1'";
        }
        else {
            if (!BeanCtx.g("default", true).equals("1")) {
                sql = "select RoleName,RoleNumber,WF_Appid from BPM_OrgRoleList where (WF_Appid='" + appid + "' or WF_Appid='S001') and RoleType='1'";
            }
            else {
                sql = "select RoleName,RoleNumber,WF_Appid from BPM_OrgRoleList where (WF_Appid='" + appid + "' or WF_Appid='S001' or WF_Appid='default') and RoleType='1'";
            }
        }
        LinkedHashSet<Document> dc = Rdb.getAllDocumentsSetBySql(sql);
        Document doc = BeanCtx.getDocumentBean("");
        doc.s("RoleName", "-所有用户-");
        doc.s("RoleNumber", "");
        doc.s("WF_Appid", "default");
        dc.add(doc);
        String jsonStr = Documents.dc2json(dc, "", true);
        BeanCtx.print(jsonStr); // 输出json字符串

        return "";
    }
}