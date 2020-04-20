package cn.linkey.rulelib.S003;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.DateUtil;
import cn.linkey.util.Tools;

/**
 * @RuleName:加入收藏夹
 * @author admin
 * @version: 8.0
 * @Created: 2014-05-20 23:49
 */
final public class R_S003_B056 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) {
        //params为运行本规则时所传入的参数
        String docUnid = BeanCtx.g("DocUnid");
        if (Tools.isBlank(docUnid)) {
            BeanCtx.p(Tools.jmsg("error", "文档不存在收藏失败"));
        }
        else {
            String sql = "select * from BPM_DocFavorites where Userid='" + BeanCtx.getUserid() + "' and DocUnid='" + docUnid + "'";
            if (Rdb.hasRecord(sql)) {
                BeanCtx.p(Tools.jmsg("error", "文档已经在您的收藏夹中了!"));
                return "";
            }

            sql = "insert into BPM_DocFavorites(WF_OrUnid,DocUnid,Userid,WF_DocCreated)values('" + Rdb.getNewUnid() + "','" + docUnid + "','" + BeanCtx.getUserid() + "','" + DateUtil.getNow() + "')";
            int i = Rdb.execSql(sql);
            if (i > 0) {
                BeanCtx.p(Tools.jmsg("error", "文档已成功加入到您的收藏夹中!"));
            }
            else {
                BeanCtx.p(Tools.jmsg("error", "文档收藏失败!"));
            }
        }
        return "";
    }
}