package cn.linkey.rulelib.S026;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * @RuleName:设置路由启用或禁止
 * @author admin
 * @version: 8.0
 * @Created: 2014-09-13 22:34
 */
final public class R_S026_B004 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        //params为运行本规则时所传入的参数
        String nodeid = BeanCtx.g("Nodeid");
        String routerRule = BeanCtx.g("RouterRule");
        String processid = BeanCtx.g("Processid");
        String dataDocUnid = BeanCtx.g("DataDocUnid"); //
        String simProcessUnid = BeanCtx.g("SimProcessUnid"); //仿真策略的WF_OrUnid
        String sql = "select * from BPM_SimNodeList where SimProcessUnid='" + simProcessUnid + "' and Nodeid='" + nodeid + "'";
        Document doc = Rdb.getDocumentBySql(sql);
        doc.s("Nodeid", nodeid);
        doc.s("NodeOwner", "");
        doc.s("SimProcessUnid", simProcessUnid);
        if (routerRule.equals("2")) {
            //强制路由
            doc.s("NodeStatus", "1");
            doc.save();
        }
        else if (routerRule.equals("3")) {
            //禁止路由
            doc.s("NodeStatus", "0");
            doc.save();
        }
        else if (routerRule.equals("4")) {
            doc.remove(false);
        }

        BeanCtx.p(Tools.jmsg("ok", ""));

        return "";
    }
}