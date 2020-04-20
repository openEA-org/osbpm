package cn.linkey.rulelib.S017;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.doc.Documents;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * @RuleName:获取待阅服务(JSON)
 * @author admin
 * @version: 8.0
 * @Created: 2014-06-30 16:55
 */
final public class R_S017_B004 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        //params为运行本规则时所传入的参数
        //示例参数:{"PageSize":"20","PageNum":"1","Appid":"*"}

        String pageSize = (String) params.get("PageSize");
        String pageNum = (String) params.get("PageNum");
        String appid = (String) params.get("Appid");
        String userid = (String) params.get("userid");
        String wfAddName = (String) params.get("wfAddName");
        if (Tools.isBlank(pageSize)) {
            pageSize = "20";
        }
        if (Tools.isBlank(pageNum)) {
            pageNum = "1";
        }
        String sql = "select WF_DocNumber,Subject,WF_Author_CN,WF_AddName_CN,WF_DocCreated,WF_ProcessName,NodeName,WF_OrUnid from BPM_UserReadDoc where Userid='" + userid + "'";
        if (appid != null && !appid.equals("*")) {
            sql += " and WF_Appid='" + appid + "'";
        }
        if (Tools.isNotBlank(wfAddName)) {
            sql += " and WF_AddName_CN like '%" + wfAddName + "%'";
        }
        String totalNum = String.valueOf(Rdb.getCountBySql(sql));
        Document[] dc = Rdb.getAllDocumentsBySql("BPM_UserReadDoc", sql, Integer.valueOf(pageNum), Integer.valueOf(pageSize));
        String jsonStr = "{\"total\":" + totalNum + ",\"rows\":" + Documents.dc2json(dc, "") + "}";
        return jsonStr;
    }
}