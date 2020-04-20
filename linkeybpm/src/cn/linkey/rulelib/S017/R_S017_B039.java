package cn.linkey.rulelib.S017;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.doc.Documents;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.DateUtil;
import cn.linkey.util.Tools;

/**
 * 流程实例接口，不包括已归档
 * 
 * @RuleName:动态调用WebService服务
 * @author admin
 * @version: 8.0
 * @Created: 2014-05-30 16:07
 */
final public class R_S017_B039 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {

        //params为运行本规则时所传入的参数
        //示例参数:{"lastTime":"2016-07-01 10:00:00", "Appid":"*"}

        String appid = (String) params.get("Appid");
        String beginTime = (String) params.get("beginTime");
        String endTime = (String) params.get("endTime");
        
        if (Tools.isBlank(beginTime)) {
            beginTime = DateUtil.getNow();
        }

        String sql = "select * from BPM_MainData where 1 = 1";
        String archiveSql = "select * from BPM_ArchivedData where 1 = 1";
        
        if (appid != null && !appid.equals("*")) {
            sql += " and WF_Appid='" + appid + "'";
            archiveSql += " and WF_Appid='" + appid + "'";
        }
        if (Tools.isNotBlank(beginTime)) {
            sql += " and WF_LastModified >= '" + beginTime + "'";
            archiveSql += " and WF_LastModified >= '" + beginTime + "'";
        }
        if (Tools.isNotBlank(endTime)) {
            sql += " and WF_LastModified < '" + endTime + "'";
            archiveSql += " and WF_LastModified < '" + endTime + "'";
        }
        //增加排序功能
        sql += " order by WF_DocCreated desc";
        archiveSql += " order by WF_DocCreated desc";

        String totalNum = String.valueOf(Rdb.getCountBySql(sql) + Rdb.getCountBySql(archiveSql));
        Document[] dc = Rdb.getAllDocumentsBySql("BPM_MainData", sql);
        Document[] archiveDc = Rdb.getAllDocumentsBySql("BPM_ArchivedData", archiveSql);
        //格式化已到达的总时间
        String docUnid;
        String processid;
        String tmpSql;
        Document[] tmpDoc;
        for (Document doc : dc) {
            docUnid = doc.g("WF_OrUnid");
            processid = doc.g("WF_processid");
            tmpSql = "select * from BPM_InsCopyUserList where DocUnid = '" + docUnid +"' and Processid = '" + processid + "'";
            tmpDoc = Rdb.getAllDocumentsBySql("BPM_InsCopyUserList", tmpSql);
            doc.s("CopyUserList", Documents.dc2json(tmpDoc, ""));

            tmpSql = "select * from BPM_InsNodeList where DocUnid = '" + docUnid +"' and Processid = '" + processid + "'";
            tmpDoc = Rdb.getAllDocumentsBySql("BPM_InsNodeList", tmpSql);
            doc.s("NodeList", Documents.dc2json(tmpDoc, ""));

            tmpSql = "select * from BPM_InsRemarkList where DocUnid = '" + docUnid +"' and Processid = '" + processid + "'";
            tmpDoc = Rdb.getAllDocumentsBySql("BPM_InsRemarkList", tmpSql);
            doc.s("RemarkList", Documents.dc2json(tmpDoc, ""));
            
            tmpSql = "select * from BPM_InsStayData where DocUnid = '" + docUnid +"' and Processid = '" + processid + "'";
            tmpDoc = Rdb.getAllDocumentsBySql("BPM_InsStayData", tmpSql);
            doc.s("StayData", Documents.dc2json(tmpDoc, ""));
            
            tmpSql = "select * from BPM_InsUserList where DocUnid = '" + docUnid +"' and Processid = '" + processid + "'";
            tmpDoc = Rdb.getAllDocumentsBySql("BPM_InsUserList", tmpSql);
            doc.s("UserList", Documents.dc2json(tmpDoc, ""));
        }
        for (Document doc : archiveDc) {
            docUnid = doc.g("WF_OrUnid");
            processid = doc.g("WF_processid");
            
            tmpSql = "select * from BPM_ArchivedRemarkList where DocUnid = '" + docUnid +"' and Processid = '" + processid + "'";
            tmpDoc = Rdb.getAllDocumentsBySql("BPM_ArchivedRemarkList", tmpSql);
            doc.s("ArchivedRemarkList", Documents.dc2json(tmpDoc, ""));
        }
        String jsonStr = "{\"total\":" + totalNum + ",\"rows\":" + Documents.dc2json(dc, "") + "}";
        return jsonStr;
    }

}