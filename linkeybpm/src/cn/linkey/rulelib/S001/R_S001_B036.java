package cn.linkey.rulelib.S001;

import java.util.*;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;
import cn.linkey.dao.Rdb;

/**
 * 应用规则全部标记为编译后运行
 * 
 * @author Administrator
 *
 */
public class R_S001_B036 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) {
        String appid = BeanCtx.g("WF_Appid", true); //应用编号列表
        if (Tools.isBlank(appid)) {
            BeanCtx.p("{\"Status\":\"error\",\"msg\":\"Error:appid can't be empty!\"}");
        }
        String sql = "update BPM_RuleList set CompileFlag='1' where WF_Appid='" + appid + "'";
        int i = Rdb.execSql(sql);

        BeanCtx.p("{\"Status\":\"ok\",\"msg\":\"1.共成功标记(" + i + ")个规则\"}");
        return "";
    }

}