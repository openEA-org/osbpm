package cn.linkey.rulelib.S002;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * @RuleName:应用到所有路由线
 * @author admin
 * @version: 8.0
 * @Created: 2016-04-11 11:45
 */
final public class R_S002_B025 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        //params为运行本规则时所传入的参数
        String Processid = BeanCtx.g("Processid", true);
        String sql = "select * from BPM_ModSequenceFlowList where Processid='" + Processid + "'";
        Document[] dc = Rdb.getAllDocumentsBySql(sql);
        int i = 0;
        for (Document doc : dc) {
            doc.s("SendMailFlag", BeanCtx.g("SendMailFlag"));
            doc.s("MailTitle", BeanCtx.g("MailTitle"));
            doc.s("SendTo", BeanCtx.g("SendTo"));
            doc.s("CopyTo", BeanCtx.g("CopyTo"));
            doc.s("MailBody", BeanCtx.g("MailBody"));
            doc.save();
            i++;
        }
        BeanCtx.p(Tools.jmsg("ok", String.valueOf(i)));
        return "";
    }
}