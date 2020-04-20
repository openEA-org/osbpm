package cn.linkey.rulelib.S001;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;

import cn.linkey.app.AppUtil;
import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * @RuleName:流程复制规则
 * @author admin
 * @version: 8.0
 * @Created: 2014-06-06 11:02
 */
final public class R_S001_B066 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        //params为运行本规则时所传入的参数
        LinkedHashSet<Document> appdc = (LinkedHashSet<Document>) params.get("AppDc"); //获取打包的文档集合对像
        String tableName = (String) params.get("TableName"); //获得要打包的数据库表名
        String srcAppid = (String) params.get("SrcAppid");//获得要打包的应用编号
        String targetAppid = (String) params.get("TargetAppid");//目的应用编号
        String oldKeyStr = (String) params.get("OldKeyStr"); //旧的关键字
        String newKeyStr = (String) params.get("NewKeyStr"); //新的关键字

        //开始拷贝流程
        //首先获得此应用下的所有流程
        LinkedHashSet<Document> dc = new LinkedHashSet<Document>();
        String sql = "select Processid from BPM_ModProcessList where WF_Appid='" + srcAppid + "'";
        HashSet<String> processidList = Rdb.getValueSetBySql(sql);
        for (String processid : processidList) {
            LinkedHashSet<Document> subdc = AppUtil.getAllModDocByProcessid(processid);//获得一个流程的所有节点文档集合
            String newProcessid = Rdb.getNewUnid();//生成一个新的流程id
            for (Document doc : subdc) {
                Document targetDoc = BeanCtx.getDocumentBean(doc.g("WF_OrTableName"));
                String xmlStr = doc.toXmlStr(true);
                xmlStr.replace("_" + srcAppid + "_", "_" + targetAppid + "_");
                if (Tools.isNotBlank(oldKeyStr)) {
                    xmlStr = xmlStr.replace(oldKeyStr, newKeyStr);
                }
                targetDoc.appendFromXml(xmlStr);
                targetDoc.s("WF_Appid", targetAppid);
                targetDoc.s("WF_OrUnid", Rdb.getNewUnid());
                targetDoc.s("Processid", newProcessid);
                appdc.add(targetDoc);

            }
        }

        return "";
    }
}