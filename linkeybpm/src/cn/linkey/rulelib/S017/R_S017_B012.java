package cn.linkey.rulelib.S017;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.doc.Documents;
import cn.linkey.rule.LinkeyRule;

/**
 * @RuleName:Engine_获得流转记录服务
 * @author admin
 * @version: 8.0
 * @Created: 2014-07-01 23:04
 */
final public class R_S017_B012 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        //params为运行本规则时所传入的参数
        //示例参数:{"RemarkType":"ALL","WF_DocUnid":"634b66890da9d041f709da1021a9b6c1463d","IsRead":"0"}
        String sql = "";
        String remarkType = (String) params.get("RemarkType");
        String docUnid = (String) params.get("WF_DocUnid");
        String isReadFlag = (String) params.get("IsRead"); //0表示办理意见，1表示阅读记录

        if (remarkType.equals("ALL")) {
            sql = "select * from BPM_AllRemarkList where DocUnid='" + docUnid + "' and IsReadFlag='" + isReadFlag + "' order by EndTime";
        }
        else {
            sql = "select * from BPM_AllRemarkList where DocUnid='" + docUnid + "' and IsReadFlag='" + isReadFlag + "' and RemarkType='" + remarkType + "' order by EndTime";
        }
        Document[] dc = Rdb.getAllDocumentsBySql(sql);
        return Documents.dc2json(dc, "rows");

    }
}