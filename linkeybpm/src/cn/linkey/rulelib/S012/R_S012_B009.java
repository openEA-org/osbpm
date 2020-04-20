package cn.linkey.rulelib.S012;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;

/**
 * @RuleName:执行SQL更新开发平台配置
 * @author admin
 * @version: 8.0
 * @Created: 2015-04-08 13:02
 */
final public class R_S012_B009 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        //params为运行本规则时所传入的参数
        String sql = "update BPM_AppPluginConfig set url='r?wf_num=V_S001_G110&WF_Appid={appid}' where Folderid='018002'";
        Rdb.execSql(sql);
        BeanCtx.p("ok");
        return "";
    }
}