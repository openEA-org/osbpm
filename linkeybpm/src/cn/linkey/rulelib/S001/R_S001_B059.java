package cn.linkey.rulelib.S001;

import java.util.HashMap;
import java.util.LinkedHashSet;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.rule.LinkeyRule;

/**
 * @RuleName:应用角色打包规则
 * @author admin
 * @version: 8.0
 * @Created: 2014-06-06 11:02
 */
final public class R_S001_B059 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) {
        //params为运行本规则时所传入的参数
        LinkedHashSet<Document> appdc = (LinkedHashSet<Document>) params.get("AppDc"); //获取打包的文档集合对像
        String tableName = (String) params.get("TableName"); //获得要打包的数据库表名
        String appid = (String) params.get("WF_Appid");//获得要打包的应用编号

        //先加入角色的文档
        String sql = "select * from " + tableName + " where WF_Appid='" + appid + "'";
        LinkedHashSet<Document> roledc = Rdb.getAllDocumentsSetBySql(tableName, sql);
        for (Document roledoc : roledc) {
            roledoc.s("WF_OrTableName", tableName);
        }
        appdc.addAll(roledc);

        //再加入角色成员的文档
        for (Document roledoc : roledc) {
            sql = "select * from BPM_OrgRoleMembers where RoleNumber='" + roledoc.g("RoleNumber") + "'";
            LinkedHashSet<Document> memberdc = Rdb.getAllDocumentsSetBySql(sql);
            for (Document mdoc : memberdc) {
                mdoc.s("WF_OrTableName", "BPM_OrgRoleMembers");
            }
            appdc.addAll(memberdc);
        }

        return "";
    }
}