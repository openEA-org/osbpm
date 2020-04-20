package cn.linkey.rulelib.S017;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.doc.Documents;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.DateUtil;
import cn.linkey.util.Tools;

/**
 * @RuleName:获取我申请的流程(JSON)
 * @author admin
 * @version: 8.0
 * @Created: 2014-06-30 16:55
 */
final public class R_S017_B005 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        //params为运行本规则时所传入的参数
        //示例参数:{"PageSize":"20","PageNum":"1","Appid":"*","SearchWord":""}

        String pageSize = (String) params.get("PageSize");
        String pageNum = (String) params.get("PageNum");
        String appid = (String) params.get("Appid");
        String userid = (String) params.get("userid");
        String searchWord = (String) params.get("SearchWord");

        if (Tools.isBlank(pageSize)) {
            pageSize = "20";
        }
        if (Tools.isBlank(pageNum)) {
            pageNum = "1";
        }
        String sql = "select WF_DocNumber,Subject,WF_Author_CN,WF_AddName_CN,WF_DocCreated,WF_ProcessName,WF_CurrentNodeName,WF_OrUnid,WF_Processid from BPM_ALLDOCUMENT where WF_AddName='" + userid + "'";
        //增加按应用取的条件
        if (appid != null && !appid.equals("*")) {
            sql += " and WF_Appid='" + appid + "'";
        }
        //增加搜索条件
        if (Tools.isNotBlank(searchWord)) {
            sql += " and Subject like '%" + searchWord + "%'";
        }
        //增加排序功能
        sql += " order by WF_DocCreated desc";

        String totalNum = String.valueOf(Rdb.getCountBySql(sql));
        Document[] dc = Rdb.getAllDocumentsBySql("BPM_UserToDo", sql, Integer.valueOf(pageNum), Integer.valueOf(pageSize));
        //格式化总耗时
        for (Document doc : dc) {
            String difTime = DateUtil.getAllDifTime(doc.g("WF_DocCreated"), DateUtil.getNow());
            int min = Integer.valueOf(difTime);
            if (min > 60) {
                difTime = String.valueOf(min / 60) + "(小时)";
            }
            else {
                difTime = min + "(分钟)";
            }
            doc.s("TotalTime", difTime);
        }

        String jsonStr = "{\"total\":" + totalNum + ",\"rows\":" + Documents.dc2json(dc, "") + "}";
        return jsonStr;

    }
}