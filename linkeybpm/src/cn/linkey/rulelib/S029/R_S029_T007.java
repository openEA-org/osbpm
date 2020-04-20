package cn.linkey.rulelib.S029;

import java.util.HashMap;

import com.qq.weixin.mp.aes.WeiXinUtil;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;

/**
 * @RuleName:发待办给企业微信号
 * @author admin
 * @version: 8.0
 * @Created: 2014-11-18 12:18
 */
final public class R_S029_T007 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        //这里编写定时执行的代码
        String sql = "select Subject,NodeName,ToDoType,SendTo,WF_OrUnid,Userid from BPM_ToDoBox order by WF_DocCreated";
        Document[] dc = Rdb.getAllDocumentsBySql(sql);
        String httpServer = WeiXinUtil.getHttpServer() + "/r?wf_num=R_S003_B036";
        for (Document doc : dc) {
            String url = httpServer + "&wf_docunid=" + doc.g("DocUnid") + "&mobile=1";
            String subject = doc.g("Subject");
            String nodeName = doc.g("NodeName");
            String userName = BeanCtx.getLinkeyUser().getCnName(doc.g("Userid")); //发送者中文名
            String sendTo = doc.g("SendTo");
            String jsonStr = "";
            if (doc.g("ToDoType").equals("1")) {
                //待办
                jsonStr = WeiXinUtil.getNewsJsonStr("您有一个新待办:" + subject, "当前状态:" + nodeName + "来自:" + userName, url, "");
            }
            else {
                //待阅
                jsonStr = WeiXinUtil.getNewsJsonStr("您有一个新待阅:" + subject, "来自:" + userName, url, "");
            }
            String rs = WeiXinUtil.sendNews(sendTo, "", "", "1", jsonStr.toString());
            if (rs.indexOf("\"errmsg\":\"ok\"") != -1) {
                sql = "delete from BPM_ToDoBox where WF_OrUnid='" + doc.getDocUnid() + "'";
                Rdb.execSql(sql);
                BeanCtx.out("成功发送一个待办给微信(" + sendTo + ")");
            }
            else {
                BeanCtx.out("发送待办给微信(" + sendTo + ")时失败!");
            }
        }
        return "";
    }
}