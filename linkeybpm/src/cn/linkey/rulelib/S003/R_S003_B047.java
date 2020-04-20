package cn.linkey.rulelib.S003;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.org.LinkeyUser;
import cn.linkey.rule.LinkeyRule;

/**
 * @RuleName:查看流程图中获得已处理和正在处理的用户信息
 * @author admin
 * @version: 8.0
 * @Created: 2014-04-28 22:54
 */
final public class R_S003_B047 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) {
        //params为运行本规则时所传入的参数
        String action = BeanCtx.g("Action");
        String nodeid = BeanCtx.g("Nodeid");
        String docUnid = BeanCtx.g("DocUnid");
        StringBuilder userList = new StringBuilder();
        LinkeyUser linkeUser = BeanCtx.getLinkeyUser();

        //获得正在流转的用户
        if (action.equals("Current")) {
            String sql = "select Userid,StartTime from BPM_InsUserList where docUnid='" + docUnid + "' and Status='Current' and Nodeid='" + nodeid + "'";
            Document[] dc = Rdb.getAllDocumentsBySql(sql);
            for (Document doc : dc) {
                if (userList.length() > 0) {
                    userList.append(",");
                }
                userList.append(linkeUser.getCnName(doc.g("Userid")) + "(开始时间:" + doc.g("StartTime") + ")");
            }
        }

        //获得已处理用户
        if (action.equals("End")) {
            String sql = "select Userid,EndTime from BPM_InsUserList where docUnid='" + docUnid + "' and Status='End' and Nodeid='" + nodeid + "'";
            Document[] dc = Rdb.getAllDocumentsBySql(sql);
            for (Document doc : dc) {
                if (userList.length() > 0) {
                    userList.append(",");
                }
                userList.append(linkeUser.getCnName(doc.g("Userid")) + "(完成时间:" + doc.g("EndTime") + ")");
            }
        }

        BeanCtx.p("{\"item\":\"" + userList.toString() + "\"}");
        return "";
    }

}