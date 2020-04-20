package cn.linkey.rulelib.S016;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;

/**
 * @RuleName:字段列数据源
 * @author admin
 * @version: 8.0
 * @Created: 2014-10-31 10:17
 */
final public class R_S016_B021 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        //params为运行本规则时所传入的参数
        String wf_orunid = BeanCtx.g("wf_docunid");
        //wf_orunid="24252525256";
        // System.out.println("wf_orunid"+wf_orunid);
        String fieldshowjson = "";
        String sql = "select fieldshow from BPM_DataAnalyse where wf_orunid='" + wf_orunid + "'";
        fieldshowjson = Rdb.getDocumentBySql(sql).g("fieldshow");
        BeanCtx.print(fieldshowjson);
        return "";
    }
}