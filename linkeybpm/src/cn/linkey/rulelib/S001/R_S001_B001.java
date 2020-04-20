package cn.linkey.rulelib.S001;

import java.util.*;
import cn.linkey.dao.*;
import cn.linkey.factory.*;
import cn.linkey.rule.LinkeyRule;

/**
 * @RuleName:获得应用开发首页
 * @author admin
 * @version: 8.0
 * @Created: 2014-06-05 16:51
 */
final public class R_S001_B001 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) {
        //params为运行本规则时所传入的参数 

        String htmlStr = Rdb.getValueBySql("select DefaultCode from BPM_DevDefaultCode where CodeType='AppDesigner'");
        htmlStr = htmlStr.replace("{UserName}", BeanCtx.getUserName());
        String sql = "select count(*) as TotalNum from BPM_OnlineUser";
        htmlStr = htmlStr.replace("{OnlineNum}", Rdb.getValueBySql(sql));

        BeanCtx.p(htmlStr);
        return "";
    }
}