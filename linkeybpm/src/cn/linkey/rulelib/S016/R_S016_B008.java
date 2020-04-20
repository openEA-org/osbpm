package cn.linkey.rulelib.S016;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.doc.Documents;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.wf.ModNode;

/**
 * @RuleName:本月超时文档统计
 * @author admin
 * @version: 8.0
 * @Created: 2014-06-30 14:33
 */
final public class R_S016_B008 implements LinkeyRule {
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

        String sql = "select top 10 COUNT(distinct DocUnid) as DocNum,Processid from BPM_InsUserList where OverDateNum<>'0' ";
        sql = sql + "and StartTime<>'' and CONVERT(DATETIME,StartTime,120) between '" + sDate1 + "' and '" + sDate2 + "' ";
        sql = sql + "group by Processid";
        LinkedHashSet<Document> dcs = new LinkedHashSet<Document>();
        Document[] dc = Rdb.getAllDocumentsBySql(sql);
        int dclen = dc.length;
        int i = 0;
        int j = 0;
        for (Document doc : dc) {
            i++;
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
            //1.当流程总数大于等于10条，直接把第9条以后的都算其它，不用管是否超10条
            //2.当流程总数大于10条，第10条应为第10条和其它流程的总和，刚好10条不用计算
            if (i < 10) { //1
                dcs.add(doc);
                if (dclen >= 10) {
                    String sCount = doc.g("DocNum");
                    if (sCount.equals(""))
                        sCount = "0";
                    j = j + Integer.parseInt(sCount);
                }
            }
        }

        if (dclen >= 10) {
            //获取其它流程总数
            sql = "select COUNT(distinct DocUnid) as DocNum from BPM_InsUserList where OverDateNum='0' ";
            sql = sql + "and StartTime<>'' and CONVERT(DATETIME,StartTime,120) between '" + sDate1 + "' and '" + sDate2 + "' ";
            Document doc = Rdb.getDocumentBySql(sql);
            if (!doc.isNull()) {
                String sCount = doc.g("DocNum");
                if (sCount.equals(""))
                    sCount = "0";
                doc.s("DocNum", Integer.toString(Integer.parseInt(sCount) - j));
                doc.s("ProcessName", "其它流程");
                //Documents.addDoc(dc, doc);
                dcs.add(doc);
            }
        }

        BeanCtx.print(Documents.dc2json(dcs, ""));

        return "";
    }
}