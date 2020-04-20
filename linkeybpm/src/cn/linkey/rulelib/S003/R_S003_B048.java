package cn.linkey.rulelib.S003;

import java.util.*;
import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.doc.Documents;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.org.LinkeyUser;
import cn.linkey.util.Tools;

/**
 * @RuleName:获得流程的流转记录
 * @author admin
 * @version: 8.0
 * @Created: 2014-05-08 12:36
 */
final public class R_S003_B048 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) {
        //params为运行本规则时所传入的参数
        LinkeyUser linkeyUser = BeanCtx.getLinkeyUser();
        String nodeid = BeanCtx.g("Nodeid", true);
        String docUnid = BeanCtx.g("DocUnid", true);
        String isRead = BeanCtx.g("IsRead", true);
        if (Tools.isBlank(isRead)) {
            isRead = "0";
        }
        String sql = "";
        if (Tools.isBlank(nodeid)) {
            sql = "select * from BPM_AllRemarkList where DocUnid='" + docUnid + "' and IsReadFlag='" + isRead + "' order by EndTime";
        }
        else {
            sql = "select * from BPM_AllRemarkList where DocUnid='" + docUnid + "' and IsReadFlag='" + isRead + "' and Nodeid='" + nodeid + "' order by EndTime";
        }
        LinkedHashSet<Document> dc = Rdb.getAllDocumentsSetBySql(sql);

        //追加正在处理的用户
        if (isRead.equals("0")) {
            if (Tools.isBlank(nodeid)) {
                sql = "select Userid,StartTime,FirstReadTime,ExceedTime,LimitTime from BPM_InsUserList where DocUnid='" + docUnid + "' and Status='Current'";
            }
            else {
                sql = "select * from BPM_InsUserList where DocUnid='" + docUnid + "' and Status='Current' and Nodeid='" + nodeid + "'";
            }
            LinkedHashSet<Document> curdc = Rdb.getAllDocumentsSetBySql(sql);
            for (Document doc : curdc) {
                doc.s("OverTimeFlag", "0");
                doc.s("ActionName", "<font color=red>处理中</font>");
                doc.s("UserName", linkeyUser.getCnName(doc.g("Userid")));
                doc.s("DeptName", linkeyUser.getDeptNameByUserid(doc.g("Userid"), false));
                if (Tools.isNotBlank(doc.g("LimitTime"))) {
                    doc.s("ExceedTime", doc.g("LimitTime")); //时限
                }
                if (doc.g("ExceedTime").equals("0")) {
                    doc.s("ExceedTime", "-无-");
                }
                if (Tools.isNotBlank(doc.g("FirstReadTime"))) {
                    doc.s("Remark", "<font color=red>已阅(" + doc.g("FirstReadTime") + ")</font>");
                }
            }
            dc.addAll(curdc);
        }

        String jsonStr = "";
		//20180713 add
        if (Rdb.getDbType().equals("ORACLE")) {
            jsonStr = Documents.dc2json(dc, "rows", true);
        }
        else {
            jsonStr = Documents.dc2json(dc, "rows");
        }
        BeanCtx.p(jsonStr);

        return "";
    }
}