package cn.linkey.rulelib.S016;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.rule.LinkeyRule;

/**
 * @RuleName:参与度最多前10名
 * @author admin
 * @version: 8.0
 * @Created: 2014-10-20 11:51
 */
final public class R_S016_E018 implements LinkeyRule {

    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        Document viewDoc = (Document) params.get("GridDoc"); //grid配置文档
        String eventName = (String) params.get("EventName");//事件名称
        if (eventName.equals("onViewOpen")) {
            return onViewOpen(viewDoc);
        }
        return "1";
    }

    public String onViewOpen(Document viewDoc) throws Exception {
        //如果不返回1则表示退出本视图并输出返回的字符串
        //图表JsHeader中用[X]字段名[/X]标识,viewDoc.s("字段名","10,20,30")来动态输出计算好的数据,
        String sql = "select  Userid,count(*) as num from BPM_InsUserList group by Userid order by num desc";
        String countsql;
        String username = "[";
        String value = "[";
        Document[] document = Rdb.getAllDocumentsBySql(sql);
        int i = 0;
        for (Document doc : document) {

            countsql = "select cnname from bpm_orguserlist where userid='" + doc.g("userid") + "'";
            Document d = Rdb.getDocumentBySql(countsql);
            if (!d.isNull() && i < 10) {
                username = username + "'" + d.g("cnname") + "',";
                i++;
                value = value + doc.g("num") + ",";
            }
        }
        username = username.substring(0, username.length() - 1);
        value = value.substring(0, value.length() - 1);
        username = username + "]";
        value = value + "]";
        viewDoc.s("username", username);
        viewDoc.s("value", value);
        return "1";
    }

}