package cn.linkey.rulelib.S017;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rest.RestUtil;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.DateUtil;
import cn.linkey.util.Tools;

/**
 * @RuleName:取消关注
 * @author admin
 * @version: 1.0
 */
final public class R_S017_B163 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) {
        //params为运行本规则时所传入的参数
        String docUnid = BeanCtx.g("docUnid");
        if (Tools.isBlank(docUnid)) {
            BeanCtx.p(RestUtil.formartResultJson("0", "docUnid不能为空"));
        }
        else {

    		String sql="delete from BPM_DocFavorites where DocUnid='"+docUnid+"' and Userid='"+BeanCtx.getUserid()+"'";
    		
            int i = Rdb.execSql(sql);
            if (i > 0) {
                BeanCtx.p(RestUtil.formartResultJson("1", "成功取消关注!"));
            }
            else {
                BeanCtx.p(RestUtil.formartResultJson("0", "取消关注失败!"));
            }
        }
        return "";
    }
}