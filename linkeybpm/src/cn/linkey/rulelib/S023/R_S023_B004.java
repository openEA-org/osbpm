package cn.linkey.rulelib.S023;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.DateUtil;
import cn.linkey.util.Tools;

/**
 * @RuleName:获得我委托的
 * @author admin
 * @version: 8.0
 * @Created: 2014-07-30 15:46
 */
final public class R_S023_B004 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        //params为运行本规则时所传入的参数
        String tableName = "BPM_MainData";
        String start = BeanCtx.g("start", true);
        String limit = BeanCtx.g("limit", true);
        if (Tools.isBlank(start)) {
            start = "1";
        }
        if (Tools.isBlank(limit)) {
            limit = "20";
        }

        // BeanCtx.out("start="+start+" limit="+limit);

        int pageNum = (Integer.parseInt(start) / Integer.parseInt(limit)) + 1;
        if (pageNum == 0) {
            pageNum = 1;
        }

        StringBuilder jsonStr = new StringBuilder();

        //BeanCtx.out("pageNum="+pageNum+" limit="+limit);

        String listCode = "<li onclick=\"window.open('r?wf_num=R_S003_B036&wf_docunid={WF_OrUnid}&mobile=1','_self')\" ><h2>{NO}.{Subject}</h2><span style=\"color:#666;font-size:9pt\"><p>流程名称:{WF_ProcessName}  申请人:{WF_AddName_CN}<br>总耗时:{TotalTime}</p></span></li>";

        int no = (pageNum - 1) * Integer.parseInt(limit);
        ;
        String sql = "select WF_DocNumber,Subject,WF_Author_CN,WF_AddName_CN,WF_DocCreated,WF_ProcessName,WF_CurrentNodeName,WF_OrUnid from " + tableName
                + " where ','+WF_SourceEntrustUserid+',' like '%," + BeanCtx.getUserid() + ",%' order by WF_DocCreated DESC";
        int totalNum = Rdb.getCountBySql(sql);
        Document[] dc = Rdb.getAllDocumentsBySql(tableName, sql, pageNum, Integer.parseInt(limit));
        for (Document doc : dc) {
            no++;
            doc.s("NO", no);
            String startTime = doc.g("WF_DocCreated");
            String difTime = DateUtil.getAllDifTime(startTime, DateUtil.getNow());
            int min = Integer.valueOf(difTime);
            if (min > 60) {
                difTime = String.valueOf(min / 60) + "(小时)";
            }
            else {
                difTime = min + "(分钟)";
            }
            doc.s("TotalTime", difTime);
            jsonStr.append(Tools.encodeJson(Tools.parserStrByDocument(doc, listCode)));
        }

        //是否还有数据的标记
        String moreDataFlag = "1";
        if (Integer.parseInt(start) >= totalNum) {
            moreDataFlag = "0";
        }

        jsonStr.insert(0, "{\"TotalNum\":\"" + totalNum + "\",\"MoreFlag\":\"" + moreDataFlag + "\",\"ItemList\":\"").append("\"}");
        //BeanCtx.out(jsonStr.toString());
        BeanCtx.p(jsonStr.toString());

        return "";
    }
}