package cn.linkey.rulelib.S003;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * @RuleName:获得子流程列表
 * @author admin
 * @version: 8.0
 * @Created: 2014-06-13 23:15
 */
final public class R_S003_B063 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        //params为运行本规则时所传入的参数
        String sql = "select WF_OrUnid,Subject,WF_ProcessName,WF_CurrentNodeName,WF_Author_CN,WF_DocCreated,WF_Status,WF_EndTime from BPM_AllDocument where WF_MainDocUnid='"
                + BeanCtx.g("DocUnid", true) + "' and WF_MainNodeid='" + BeanCtx.g("Nodeid", true) + "'";
        Document[] dc = Rdb.getAllDocumentsBySql(sql);
        String str = "";
        int i = 0;
        for (Document doc : dc) {
            i++;
            String url = "rule?wf_num=R_S003_B052&wf_docunid=" + doc.getDocUnid();
            if (Tools.isNotBlank(str)) {
                str += ",";
            }
            if (!doc.g("WF_Status").equals("ARC")) {
                str += i + "." + doc.g("Subject") + "[" + doc.g("WF_CurrentNodeName").replace(",", " ") + "/" + doc.g("WF_Author_CN").replace(",", " ") + "/" + doc.g("WF_DocCreated") + "]$" + url;
            }
            else {
                str += i + "." + doc.g("Subject") + "[" + doc.g("WF_CurrentNodeName").replace(",", " ") + "/" + doc.g("WF_EndTime") + "]$" + url;
            }
        }
        BeanCtx.p("{\"item\":\"" + str + "\"}");
        return "";
    }
}