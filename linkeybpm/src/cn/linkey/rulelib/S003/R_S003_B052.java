package cn.linkey.rulelib.S003;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * @RuleName:统一文档显示地址(邮件链接等用)
 * @author admin
 * @version: 8.0
 * @Created: 2014-05-16 17:39
 */
final public class R_S003_B052 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) {
        //params为运行本规则时所传入的参数
        String docUnid = BeanCtx.g("wf_docunid", true);
        if (Tools.isBlank(docUnid)) {
            docUnid = BeanCtx.g("wf_docunid", true);
        }
        String sql = "select WF_ArcFormNumber,WF_Status from BPM_AllDocument where WF_OrUnid='" + docUnid + "'";
        Document doc = Rdb.getDocumentBySql(sql);
        if (doc.isNull()) {
            BeanCtx.showErrorMsg("Error:文档不存在或已经被删除了!");
        }
        else {
            String url = "";
            //已归档了
            if (doc.g("WF_Status").equals("ARC")) {
                url = "rule?wf_num=R_S003_B062&wf_docunid=" + docUnid;
            }
            else {
                url = "rule?wf_num=R_S003_B036&wf_docunid=" + docUnid;
            }
            try {
                BeanCtx.getResponse().sendRedirect(url);
            }
            catch (Exception e) {
                BeanCtx.p("Rule Run Error!");
                e.printStackTrace();
            }
        }
        return "";
    }
}