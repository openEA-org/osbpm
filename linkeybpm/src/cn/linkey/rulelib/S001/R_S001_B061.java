package cn.linkey.rulelib.S001;

import java.util.HashMap;
import java.util.LinkedHashSet;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.rule.LinkeyRule;

/**
 * @RuleName:应用角色删除规则
 * @author admin
 * @version: 8.0
 * @Created: 2014-06-06 11:02
 */
final public class R_S001_B061 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) {
        //params为运行本规则时所传入的参数
        String tableName = (String) params.get("TableName"); //获得要设计所在数据库表名
        String appid = (String) params.get("WF_Appid");//获得要删除的应用编号
        String sql = "select * from " + tableName + " where WF_Appid='" + appid + "'";
        LinkedHashSet<Document> roledc = Rdb.getAllDocumentsSetBySql(tableName, sql);

        //先删除角色成员的文档
        for (Document roledoc : roledc) {
            sql = "delete from BPM_OrgRoleMembers where RoleNumber='" + roledoc.g("RoleNumber") + "'";
            Rdb.execSql(sql);
        }

        //删除角色的文档
        sql = "delete from " + tableName + " where WF_Appid='" + appid + "'";
        int i = Rdb.execSql(sql);

        return String.valueOf(i);
    }
}