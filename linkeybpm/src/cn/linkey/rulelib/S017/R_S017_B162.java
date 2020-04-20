package cn.linkey.rulelib.S017;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rest.RestUtil;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.DateUtil;
import cn.linkey.util.Tools;

/**
 * @RuleName:加入收藏夹
 * @author admin
 * @version: 1.0
 */
final public class R_S017_B162 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) {
        //params为运行本规则时所传入的参数
        String docUnid = BeanCtx.g("docUnid");
        if (Tools.isBlank(docUnid)) {
            BeanCtx.p(RestUtil.formartResultJson("0", "docUnid不能为空"));
        }
        else {
            String sql = "select * from BPM_DocFavorites where Userid='" + BeanCtx.getUserid() + "' and DocUnid='" + docUnid + "'";
            if (Rdb.hasRecord(sql)) {
                BeanCtx.p(RestUtil.formartResultJson("0", "文档你已经关注过了!"));
                return "";
            }

            sql = "insert into BPM_DocFavorites(WF_OrUnid,DocUnid,Userid,WF_DocCreated)values('" + Rdb.getNewUnid() + "','" + docUnid + "','" + BeanCtx.getUserid() + "','" + DateUtil.getNow() + "')";
            int i = Rdb.execSql(sql);
            if (i > 0) {
                BeanCtx.p(RestUtil.formartResultJson("1", "文档已成功加入到您的关注列表中!"));
            }
            else {
                BeanCtx.p(RestUtil.formartResultJson("0", "文档关注失败!"));
            }
        }
        return "";
    }
}