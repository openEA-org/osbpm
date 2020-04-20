package cn.linkey.rulelib.S013;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * @RuleName:更新字号编号
 * @author admin
 * @version: 8.0
 * @Created: 2014-06-10 14:14
 */
final public class R_S013_B007 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) {
        //params为运行本规则时所传入的参数
        BeanCtx.getResponse().setContentType("text/json;charset=utf-8");
        String zhihaoid = BeanCtx.g("zhihaoid", true);
        String number = BeanCtx.g("number", true);
        if (!Tools.isBlank(zhihaoid) && !Tools.isBlank(number)) {
            String sql = "update BPM_ZhiHao set CurrentNo='" + number + "' where WF_OrUnid='" + zhihaoid + "'";
            Rdb.execSql(sql);
            BeanCtx.print("{\"msg\":\"success\"}");
        }
        else {
            BeanCtx.print("{\"msg\":\"failure\"}");
        }

        return "";
    }
}