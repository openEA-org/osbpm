package cn.linkey.rulelib.S017;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;

/**
 * @RuleName:Engine_增加常用办理意见服务
 * @author admin
 * @version: 8.0
 * @Created: 2014-07-01 23:04
 */
final public class R_S017_B015 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        //params为运行本规则时所传入的参数
        //参数示例:{"Remark":"同意办理"}
        String remark = (String) params.get("Remark");
        String sql = "select * from BPM_UserProfile where Userid='" + BeanCtx.getUserid() + "'";
        Document doc = Rdb.getDocumentBySql(sql);
        if (doc.isNull()) {
            doc.s("WF_MyRemark", remark);
            doc.s("Userid", BeanCtx.getUserid());
            doc.s("UserName", BeanCtx.getUserName());
        }
        else {
            doc.s("WF_MyRemark", doc.g("WF_MyRemark") + "\n" + remark);
        }
        int i = doc.save();
        return String.valueOf(i);
    }
}