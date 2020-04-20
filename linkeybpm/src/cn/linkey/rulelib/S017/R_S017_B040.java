package cn.linkey.rulelib.S017;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.doc.Documents;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * @RuleName:TODO_获取已阅服务(JSON)
 * @author  admin
 * @version: 8.0
 * @Created: 2016-07-20 17:03:08
 */
final public class R_S017_B040 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception  {

        //params为运行本规则时所传入的参数
        //示例参数:{"lastTime":"2016-07-01 10:00:00", "Appid":"*"}

        String appid = (String) params.get("Appid");
        String pageSize = (String) params.get("PageSize");
        String pageNum = (String) params.get("PageNum");
        String userid = (String) params.get("userid");
        String wfAddName = (String) params.get("wfAddName");
        String searchWord = (String) params.get("SearchWord");

        if (Tools.isBlank(pageSize)) {
            pageSize = "20";
        }
        if (Tools.isBlank(pageNum)) {
            pageNum = "1";
        }
        
        String sql = "select d.Subject, d.WF_AddName,d.WF_AddName_CN,d.WF_AllReaders,d.WF_CurrentNodeName, d.WF_DocNumber,d.WF_DocCreated, d.WF_ProcessName, r.FirstReadTime from BPM_AllRemarkList r left join BPM_AllDocument d on r.DocUnid = d.WF_OrUnid where r.Userid = '" + userid +"'  and r.IsReadFlag='1' and r.Actionid='Read' and r.Remark = '已阅' ";
        
        if (appid != null && !appid.equals("*")) {
            sql += " and d.WF_Appid='" + appid + "'";
        }
        if (Tools.isNotBlank(searchWord)) {
            sql += " and d.Subject like '%" + searchWord + "%'";
        }
        if (Tools.isNotBlank(wfAddName)) {
            sql += " and d.WF_AddName_CN like '%" + wfAddName + "%'";
        }
        sql += " order by r.FirstReadTime";

        String totalNum = String.valueOf(Rdb.getCountBySql(sql));
        Document[] dc = Rdb.getAllDocumentsBySql(sql, Integer.valueOf(pageNum), Integer.valueOf(pageSize));
       
        String jsonStr = "{\"total\":" + totalNum + ",\"rows\":" + Documents.dc2json(dc, "") + "}";
        return jsonStr;
    }
}