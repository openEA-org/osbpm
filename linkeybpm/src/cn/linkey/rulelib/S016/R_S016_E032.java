package cn.linkey.rulelib.S016;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.dao.SqlType;
import cn.linkey.doc.Document;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * @RuleName:待办平均延时前10用户
 * @author admin
 * @version: 8.0
 * @Created: 2014-10-22 10:32
 */
final public class R_S016_E032 implements LinkeyRule {

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
    	//String sql = "select distinct Userid,avg(convert(int,overdatenum)) as time from BPM_InsUserList where Status='current' and OverDateNum<>0 group by userid order by time desc";
    	//String DBType = Tools.getProperty("DBType");
    	//20190328 改为自动获取当前数据库类型
    	String DBType = Rdb.getDbType();
    	String sql = SqlType.createSql(DBType, "R_S016_E032_SQL1");
    	
    	String value = "[";
        String nodename = "[";
        String namesql = "";
        int i = 0;
        Document[] document = Rdb.getAllDocumentsBySql(sql);
        if (Rdb.getCountBySql(sql) > 0) {
            for (Document doc : document) {
                namesql = "select cnname from BPM_OrgUserList where Userid='" + doc.g("userid") + "'";
                if (Rdb.getDocumentBySql(namesql).g("cnname").length() > 0 && i < 10) {
                    nodename = nodename + "'" + Rdb.getDocumentBySql(namesql).g("cnname") + "',";
                    value = value + "{value:" + doc.g("time") + ",name:'" + Rdb.getDocumentBySql(namesql).g("cnname") + "'},";
                    i++;
                }
            }
            nodename = nodename.substring(0, nodename.length() - 1);
            value = value.substring(0, value.length() - 1);
            nodename = nodename + "]";
            value = value + "]";
        }
        else {
            nodename = "['']";
            value = "['']";
        }
        viewDoc.s("nodename", nodename);
        viewDoc.s("value", value);
        return "1";
    }

}