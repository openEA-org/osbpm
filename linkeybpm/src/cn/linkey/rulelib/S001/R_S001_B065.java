package cn.linkey.rulelib.S001;

import java.util.HashMap;
import java.util.LinkedHashSet;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * @RuleName:通用复制设计规则
 * @author admin
 * @version: 8.0
 * @Created: 2014-06-06 11:02
 */
final public class R_S001_B065 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        //params为运行本规则时所传入的参数
        LinkedHashSet<Document> appdc = (LinkedHashSet<Document>) params.get("AppDc"); //获取打包的文档集合对像
        String tableName = (String) params.get("TableName"); //获得要打包的数据库表名
        String srcAppid = (String) params.get("SrcAppid");//获得要打包的应用编号
        String targetAppid = (String) params.get("TargetAppid");//目的应用编号
        String oldKeyStr = (String) params.get("OldKeyStr"); //旧的关键字
        String newKeyStr = (String) params.get("NewKeyStr"); //新的关键字

        String sql = "select * from " + tableName + " where WF_Appid='" + srcAppid + "'";
        LinkedHashSet<Document> dc = Rdb.getAllDocumentsSetBySql(tableName, sql);
        for (Document doc : dc) {
            Document targetDoc = BeanCtx.getDocumentBean(tableName);
            String xmlStr = doc.toXmlStr(true);
            xmlStr = xmlStr.replace("_" + srcAppid + "_", "_" + targetAppid + "_");
            if (Tools.isNotBlank(oldKeyStr)) {
                xmlStr = xmlStr.replace(oldKeyStr, newKeyStr);
            }
            targetDoc.appendFromXml(xmlStr);
            targetDoc.s("WF_Appid", targetAppid);
            targetDoc.s("WF_OrUnid", Rdb.getNewUnid());
            appdc.add(targetDoc);
        }
        return "";
    }
}