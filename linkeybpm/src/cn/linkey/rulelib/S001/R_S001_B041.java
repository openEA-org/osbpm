package cn.linkey.rulelib.S001;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * @RuleName:清空数据库表中的文档
 * @author admin
 * @version: 8.0
 * @Created: 2014-04-17 12:46
 */
final public class R_S001_B041 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) {
        //params为运行本规则时所传入的参数
        String delType = BeanCtx.g("Action");
        String tableName = "";
        if (delType.equals("T")) {
            tableName = "BPM_DocTrash";
            
            // ------------20181121清空回收站时删除文档中携带的附件数据库记录和附件文件-----------
            Document[] docs = Rdb.getAllDocumentsBySql("SELECT * FROM BPM_DocTrash");
            for(Document doc : docs) {
            	String sourceOrUnid = doc.g("SourceOrUnid");
            	if(Tools.isNotBlank(sourceOrUnid)) {
            		doc.s("WF_OrUnid", sourceOrUnid);
            		doc.removeAllAttachments(true);
            	}
            }
            // -----------------------------20181121 End-----------------------------
            
        }
        else if (delType.equals("L")) {
            tableName = "BPM_ConsoleLOG";
        }
        else if (delType.equals("C")) {
            tableName = "BPM_ConsoleError";
        }
        String sql = "delete from " + tableName;
        Rdb.execSql(sql);
        BeanCtx.p(Tools.jmsg("ok", "Success to empty!"));
        return "";
    }
}