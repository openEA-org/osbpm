package cn.linkey.rulelib.S016;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.dao.SqlType;
import cn.linkey.doc.Document;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * @RuleName:及时响应用户前10名
 * @author admin
 * @version: 8.0
 * @Created: 2014-10-20 15:32
 */
final public class R_S016_E023 implements LinkeyRule {

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
        
    	//20181019修改支持多数据库sql语句
    	//String sql = "SELECT top 10 userid,count(*) as num,avg(datediff(minute,convert(datetime,starttime),convert(datetime,FirstReadTime))) as time FROM BPM_InsRemarkList where FirstReadTime<>'' group by userid order by time asc ";
        //String DBType = Tools.getProperty("DBType");
    	//20190328 改为自动获取当前数据库类型
    	String DBType = Rdb.getDbType();
    	String sql = SqlType.createSql(DBType, "R_S016_E023_SQL1");
    	
    	String countsql;
        String username = "[";
        String value = "[";
        Document[] document = Rdb.getAllDocumentsBySql(sql);
        for (Document doc : document) {

            countsql = "select cnname from bpm_orguserlist where userid='" + doc.g("userid") + "'";
            Document d = Rdb.getDocumentBySql(countsql);
            if (!d.isNull()) {
                username = username + "'" + d.g("cnname") + "',";
                value = value + doc.g("time") + ",";
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