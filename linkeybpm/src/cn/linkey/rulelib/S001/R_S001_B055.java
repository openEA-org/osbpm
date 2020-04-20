package cn.linkey.rulelib.S001;

import java.util.HashMap;
import java.util.LinkedHashSet;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.rule.LinkeyRule;

/**
 * @RuleName:通用打包单个设计规则
 * @author admin
 * @version: 8.0
 * @Created: 2014-06-06 11:02
 */
final public class R_S001_B055 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) {
        //params为运行本规则时所传入的参数
        LinkedHashSet<Document> appdc = (LinkedHashSet<Document>) params.get("AppDc"); //获取打包的文档集合对像
        String tableName = (String) params.get("TableName"); //获得要打包的数据库表名
        String appid = (String) params.get("WF_Appid");//获得要打包的应用编号
        String sql = "select * from " + tableName + " where WF_Appid='" + appid + "'";
        LinkedHashSet<Document> dc = Rdb.getAllDocumentsSetBySql(tableName, sql);
        for (Document doc : dc) {
            doc.s("WF_OrTableName", tableName);
            appdc.add(doc);
        }
        return "";
    }
}