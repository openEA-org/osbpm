package cn.linkey.rulelib.S007;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.doc.Documents;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * 获得所有角色列表按应用
 * 
 * @author Administrator
 */
public class R_S007_B005 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) {
        String appid = BeanCtx.g("WF_Appid", true);
        String sql = "";
        if (Tools.isBlank(appid)) {
            sql = "select RoleName,RoleNumber,WF_Appid from BPM_OrgRoleList";
        }
        else {
            sql = "select RoleName,RoleNumber,WF_Appid from BPM_OrgRoleList where WF_Appid='" + appid + "'";
        }
        Document[] dc = Rdb.getAllDocumentsBySql("BPM_OrgRoleList", sql);
        BeanCtx.print(Documents.dc2json(dc, "")); // 输出json字符串

        return "";
    }
}