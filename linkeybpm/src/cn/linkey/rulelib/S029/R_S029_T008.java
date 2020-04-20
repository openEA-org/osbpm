package cn.linkey.rulelib.S029;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.rule.LinkeyRule;

/**
 * @RuleName:定时发送待办到统一待办中(5分钟执行一次)
 * @author admin
 * @version: 8.0
 * @Created: 2014-12-22 22:07
 */
final public class R_S029_T008 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        //这里编写定时执行的代码

        //获得需要立即发送的待办
        String sql = "select * from BPM_ToDoBox where ToDoType='1' and Status='1'";
        Document[] dc = Rdb.getAllDocumentsBySql(sql);
        for (Document doc : dc) {
            String sendTo = doc.g("SendTo");
            String fromUserid = doc.g("Userid");
            String nodeName = doc.g("NodeName");
            String Subject = doc.g("Subject");
            //这里执行发送待办的程序
            doc.s("Status", "0"); //表示已发送成功
            doc.save();
        }

        //获得所有需要取消的待办
        sql = "select * from BPM_ToDoBox where ToDoType='1' and Status='3'";
        dc = Rdb.getAllDocumentsBySql(sql);
        for (Document doc : dc) {
            String sendTo = doc.g("SendTo");
            String fromUserid = doc.g("Userid");
            String nodeName = doc.g("NodeName");
            String Subject = doc.g("Subject");
            //这里执行取消待办的程序
            doc.remove(false);
        }

        return "";
    }
}