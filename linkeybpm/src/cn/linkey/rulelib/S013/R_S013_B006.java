package cn.linkey.rulelib.S013;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * @RuleName:获取字号编号
 * @author admin
 * @version: 8.0
 * @Created: 2014-06-10 09:11
 */
final public class R_S013_B006 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) {
        // params为运行本规则时所传入的参数
        BeanCtx.getResponse().setContentType("text/json;charset=utf-8");
        // BeanCtx.getResponse().setContentType("application/json");
        // BeanCtx.getResponse().setCharacterEncoding("UTF-8");
        // BeanCtx.getResponse().setHeader("Cache-Control", "no-cache");

        String zhihaoid = BeanCtx.g("zhihaoid", true);
        String zhihaoNumber = "";
        String sql = "";
        if (!Tools.isBlank(zhihaoid)) {
            sql = "select CurrentNo from BPM_ZhiHao where WF_OrUnid='" + zhihaoid + "'";
            Document doc = Rdb.getDocumentBySql(sql);
            if (!doc.isNull()) {
                zhihaoNumber = doc.g("CurrentNo");
                int i = Integer.parseInt(zhihaoNumber);
                i++;
                zhihaoNumber = Integer.toString(i);
            }
        }
        BeanCtx.p("{\"msg\":\"" + zhihaoNumber + "\"}");

        return "";
    }
}