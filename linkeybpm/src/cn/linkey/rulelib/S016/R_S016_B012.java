package cn.linkey.rulelib.S016;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.doc.Documents;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.wf.ModNode;

/**
 * @RuleName:本月流程健康度最差Top3
 * @author admin
 * @version: 8.0
 * @Created: 2014-07-02 10:05
 */
final public class R_S016_B012 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        //params为运行本规则时所传入的参数
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        //Date date=new Date();

        Calendar cal = Calendar.getInstance();
        //cal.set(Calendar.YEAR, 2014);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        Date d1 = cal.getTime();
        cal.add(Calendar.MONTH, 1);
        cal.add(Calendar.DAY_OF_MONTH, -1);
        Date d2 = cal.getTime();

        String sDate1 = sdf.format(d1) + " 00:00";
        String sDate2 = sdf.format(d2) + " 23:59";

        ModNode modNode = new ModNode();
        String ProcessName = "";

        String sql = "select top 3 a.DocNum1,a.Processid,b.DocNum2,round(cast(a.DocNum1 as float )/isnull(a.DocNum1+b.DocNum2,0),2) as bl from";
        sql = sql + " (select COUNT(distinct DocUnid) as DocNum1,Processid from BPM_InsUserList where OverDateNum<>'0' and StartTime<>'' and CONVERT(DATETIME,StartTime,120) between '" + sDate1
                + "' and '" + sDate2 + "' group by Processid) a";
        sql = sql + " left join";
        sql = sql + " (select COUNT(distinct DocUnid) as DocNum2,Processid from BPM_InsUserList where OverDateNum='0' and StartTime<>'' and CONVERT(DATETIME,StartTime,120) between '" + sDate1
                + "' and '" + sDate2 + "' group by Processid) b";
        sql = sql + " on a.Processid = b.Processid";
        sql = sql + " where b.DocNum2 is not null";
        sql = sql + " order by bl desc";
        Document[] dc = Rdb.getAllDocumentsBySql("", sql);
        for (Document doc : dc) {
            //获取流程名称
            Document modNodeDoc = modNode.getNodeDoc(doc.g("Processid"), "Process");
            if (!modNodeDoc.isNull()) {
                ProcessName = modNodeDoc.g("NodeName");
            }
            else {
                ProcessName = "";
            }
            if (ProcessName.length() > 6)
                ProcessName = ProcessName.substring(0, 6) + "...";
            doc.s("ProcessName", ProcessName);
        }
        BeanCtx.print(Documents.dc2json(dc, ""));
        //BeanCtx.print("[{\"ProcessName\":\"流程1\",\"DocNum1\":\"3\",\"DocNum2\":\"4\"},{\"ProcessName\":\"流程2\",\"DocNum1\":\"6\",\"DocNum2\":\"8\"}]");

        return "";
    }
}