package cn.linkey.rulelib.S001;

import java.util.HashMap;
import java.util.LinkedHashSet;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * @RuleName:应用数据库表打包规则
 * @author admin
 * @version: 8.0
 * @Created: 2014-06-06 11:02
 */
final public class R_S001_B057 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) {
        //params为运行本规则时所传入的参数
        LinkedHashSet<Document> appdc = (LinkedHashSet<Document>) params.get("AppDc"); //获取打包的文档集合对像
        String tableName = (String) params.get("TableName"); //获得要打包的数据库表名
        String appid = (String) params.get("WF_Appid");//获得要打包的应用编号
        String sql = "select * from " + tableName + " where WF_Appid='" + appid + "'";
        LinkedHashSet<Document> cfgdc = Rdb.getAllDocumentsSetBySql(tableName, sql);
        for (Document doc : cfgdc) {
            doc.s("WF_OrTableName", tableName);
            appdc.add(doc);
        }

        //看是否要打包数据库中的文档
        for (Document doc : cfgdc) {
            //	    	BeanCtx.out("initdata="+doc.g("InitData"));
            if (doc.g("InitData").equals("1")) {
                //说明要打包文档
                String dataSourceid = doc.g("DataSourceid");
                if (Tools.isBlank(dataSourceid)) {
                    dataSourceid = "default";
                }
                if (Rdb.isExistTable(dataSourceid, doc.g("TableName"))) {
                    //如果数据库表存在才执行
                    sql = "select * from " + doc.g("TableName");
                    Document[] datadc = Rdb.getAllDocumentsBySql(sql);
                    for (Document dataDoc : datadc) {
                        dataDoc.s("WF_OrTableName", doc.g("TableName"));
                        appdc.add(dataDoc);
                    }
                }
            }
        }

        return "";
    }
}