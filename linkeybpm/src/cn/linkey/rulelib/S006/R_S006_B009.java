package cn.linkey.rulelib.S006;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * @RuleName:重置所有用户密码为pass
 * @author admin
 * @version: 8.0
 * @Created: 2014-04-25 17:27
 */
final public class R_S006_B009 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) {
        //params为运行本规则时所传入的参数
        String sql = "select * from BPM_OrgUserList";
        Document[] dc = Rdb.getAllDocumentsBySql(sql);
        int i = 0;
        for (Document doc : dc) {
            doc.s("Password", Tools.md5("pass"));
            doc.removeItem("lang");
            doc.s("LANG", "zh,CN");
            doc.save();
            i++;
        }
        BeanCtx.p("共初始化(" + i + ")个用户的密码为pass");
        return "";
    }
}