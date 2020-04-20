package cn.linkey.rulelib.S002;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.doc.Documents;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;

/**
 * @RuleName:获得环节适用的事件列表
 * @author admin
 * @version: 8.0
 * @Created: 2014-05-01 14:04
 */
final public class R_S002_B006 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) {
        //params为运行本规则时所传入的参数
        String nodeType = BeanCtx.g("NodeType");
        String sql = "";
        if (Rdb.getDbType().equals("MSSQL")) {
            sql = "select EventName,Eventid from BPM_EngineEventList where ((','+NodeType+',' like '%," + nodeType + ",%' or NodeType='*') or NodeType='*') and (','+NoNodeType+',' not like '%,"
                    + nodeType + ",%')  order by EventName";
        }
        else if (Rdb.getDbType().equals("MYSQL")) {
            sql = "select EventName,Eventid from BPM_EngineEventList where ((concat(',',NodeType,',') like '%," + nodeType
                    + ",%' or NodeType='*') or NodeType='*') and (concat(',',NoNodeType,',') not like '%," + nodeType + ",%')  order by EventName";
        }
        else {
            sql = "select EventName,Eventid from BPM_EngineEventList where ((','||NodeType||',' like '%," + nodeType + ",%' or NodeType='*') or NodeType='*') and (','||NoNodeType||',' not like '%,"
                    + nodeType + ",%')  order by EventName";
        }
        //	    BeanCtx.out(sql);
        Document[] dc = Rdb.getAllDocumentsBySql(sql);
        String jsonStr = Documents.dc2json(dc, "", true);
        BeanCtx.p(jsonStr);

        return "";
    }
}