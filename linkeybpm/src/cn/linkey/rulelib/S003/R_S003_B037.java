package cn.linkey.rulelib.S003;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * @RuleName:加入常用办理意见
 * @author admin
 * @version: 8.0
 * @Created: 2014-05-06 10:56
 */
final public class R_S003_B037 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) {
        //params为运行本规则时所传入的参数
        String remark = BeanCtx.g("Remark");
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
        doc.save();
        BeanCtx.p(Tools.jmsg("ok", BeanCtx.getMsg("Common", "AddMyRemarkInfo")));
        return "";
    }
}