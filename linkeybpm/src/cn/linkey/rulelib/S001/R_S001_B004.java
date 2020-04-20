package cn.linkey.rulelib.S001;

import java.util.HashMap;
import cn.linkey.dao.Rdb;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;

/**
 * 清除设计元素的事件
 * 
 * @author Administrator 编号 R_S001_B004
 */
public class R_S001_B004 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) {
        String sqlTableName = BeanCtx.g("WF_TableName");
        String elid = BeanCtx.g("WF_Elid");
        String docunid = BeanCtx.g("wf_docunid", true);

        // 首先清删除设计元素中的事件编号
        String sql = "update " + sqlTableName + " set EventRuleNum='' where WF_OrUnid='" + docunid + "'";
        Rdb.execSql(sql);

        // 清除规则中的代码
        sql = "delete from BPM_RuleList where RuleNum='" + elid + "'";
        Rdb.execSql(sql);
        BeanCtx.print("{\"Status\":\"ok\",\"msg\":\"" + BeanCtx.getMsg("Designer", "ClearEvent") + "\"}");
        return "";
    }

}
