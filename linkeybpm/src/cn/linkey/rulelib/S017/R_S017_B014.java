package cn.linkey.rulelib.S017;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;

/**
 * @RuleName:Engine_获得常用办理意见
 * @author admin
 * @version: 8.0
 * @Created: 2014-07-01 23:04
 */
final public class R_S017_B014 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        //params为运行本规则时所传入的参数
        //无需传参数
        String sql = "select WF_MyRemark from BPM_UserProfile where Userid='" + BeanCtx.getUserid() + "'";
        String remark = Rdb.getValueBySql(sql);
        remark = remark.replace("\n", ",");
        return remark;
    }
}