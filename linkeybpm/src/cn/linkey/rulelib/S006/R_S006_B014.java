package cn.linkey.rulelib.S006;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;

/**
 * @RuleName:重新修正DominoBPM版的部门唯一编号
 * @author admin
 * @version: 8.0
 * @Created: 2014-04-27 21:43
 */
final public class R_S006_B014 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) {
        // params为运行本规则时所传入的参数
        String sql = "select * from BPM_OrgDeptList";
        Document[] dc = Rdb.getAllDocumentsBySql(sql);
        int i = 1;
        for (Document doc : dc) {
            String deptnum = "000000" + i;
            deptnum = deptnum.substring(deptnum.length() - 3);
            doc.s("Deptid", "DP" + doc.g("OrgClass") + deptnum);
            doc.save();
            i++;
        }
        BeanCtx.p("共修正(" + i + ")个部门的编号!");
        return "";
    }
}