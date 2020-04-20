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
 * @RuleName:年度流程效率趋势分析
 * @author admin
 * @version: 8.0
 * @Created: 2014-07-01 09:40
 */
final public class R_S016_B009 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        // params为运行本规则时所传入的参数
        // 1月到本月为止
        // 判断流程效率提高或降低
        // 年度所有文档平均耗时分钟，本月所有文档平均耗时分钟，比率最小效率提高越高
        // 效率趋势最高，效率趋势最低
        // 耗时为0的问题，但实际生产环境也不存在
        // having avg(datediff(n,WF_DocCreated,WF_EndTime))<>0排除平均为0的情况
        // isnull(a.DifMin1,0)分母为0也不会报错
        // b.DifMin2 is not null本月必须有数据

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        // Date date=new Date();

        Calendar cal = Calendar.getInstance();
        // cal.set(Calendar.YEAR, 2014);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        Date d1 = cal.getTime();
        cal.add(Calendar.MONTH, 1);
        cal.add(Calendar.DAY_OF_MONTH, -1);
        Date d2 = cal.getTime();

        String sDate1 = sdf.format(d1) + " 00:00";
        String sDate2 = sdf.format(d2) + " 23:59";
        // 月初月末
        // 年初
        cal.set(Calendar.DAY_OF_YEAR, 1);
        Date d0 = cal.getTime();
        String sDate0 = sdf.format(d0) + " 00:00";

        ModNode modNode = new ModNode();
        String ProcessName = "";

        StringBuffer sbsql = new StringBuffer();
        sbsql.append("select top 1 a.DifMin1,a.WF_Processid,b.DifMin2,round(cast(b.DifMin2 as float )/isnull(a.DifMin1,0),2) as bl from ");
        sbsql.append(
                "(select avg(datediff(n,WF_DocCreated,WF_EndTime)) as DifMin1,WF_Processid from BPM_ArchivedData where WF_DocCreated<>'' and WF_EndTime<>'' and CONVERT(DATETIME,WF_DocCreated,120) between '"
                        + sDate0 + "' and '" + sDate2 + "' group by WF_Processid having avg(datediff(n,WF_DocCreated,WF_EndTime))<>0) a ");
        sbsql.append("left join ");
        sbsql.append(
                "(select avg(datediff(n,WF_DocCreated,WF_EndTime)) as DifMin2,WF_Processid from BPM_ArchivedData where WF_DocCreated<>'' and WF_EndTime<>'' and CONVERT(DATETIME,WF_DocCreated,120) between '"
                        + sDate1 + "' and '" + sDate2 + "' group by WF_Processid) b ");
        sbsql.append("on a.WF_Processid = b.WF_Processid ");
        sbsql.append("where b.DifMin2 is not null ");
        sbsql.append("order by bl");
        String sql = sbsql.toString();
        // BeanCtx.out(sql);
        // BeanCtx.setDebug();
        // Document doc = Rdb.getDocumentBySql(sql);
        String processid1 = "";
        /*
         * try{ ResultSet rs = Rdb.getResultSet(sql); while(rs.next()){ processid1 = rs.getString("WF_Processid"); } Rdb.closers(rs); }catch(Exception e){ e.printStackTrace(); }
         */
        Document doc1 = Rdb.getDocumentBySql("", sql);
        if (!doc1.isNull()) {
            processid1 = doc1.g("WF_Processid");
        }
        // 效率降低最快
        sbsql.setLength(0);
        sbsql.append("select top 1 a.DifMin1,a.WF_Processid,b.DifMin2,round(cast(b.DifMin2 as float )/isnull(a.DifMin1,0),2) as bl from ");
        sbsql.append(
                "(select avg(datediff(n,WF_DocCreated,WF_EndTime)) as DifMin1,WF_Processid from BPM_ArchivedData where WF_DocCreated<>'' and WF_EndTime<>'' and CONVERT(DATETIME,WF_DocCreated,120) between '"
                        + sDate0 + "' and '" + sDate2 + "' group by WF_Processid having avg(datediff(n,WF_DocCreated,WF_EndTime))<>0) a ");
        sbsql.append("left join ");
        sbsql.append(
                "(select avg(datediff(n,WF_DocCreated,WF_EndTime)) as DifMin2,WF_Processid from BPM_ArchivedData where WF_DocCreated<>'' and WF_EndTime<>'' and CONVERT(DATETIME,WF_DocCreated,120) between '"
                        + sDate1 + "' and '" + sDate2 + "' group by WF_Processid) b ");
        sbsql.append("on a.WF_Processid = b.WF_Processid ");
        sbsql.append("where b.DifMin2 is not null ");
        sbsql.append("order by bl desc");
        sql = sbsql.toString();
        String processid2 = "";
        Document doc2 = Rdb.getDocumentBySql("", sql);
        if (!doc2.isNull()) {
            processid2 = doc2.g("WF_Processid");
        }

        // 输出集合
        LinkedHashSet<Document> dc = new LinkedHashSet<Document>();

        if (!processid1.equals("")) {
            sql = "select avg(datediff(n,WF_DocCreated,WF_EndTime)) as DifMin,datepart(MONTH,WF_DocCreated) as Mon from BPM_ArchivedData where WF_Processid='" + processid1
                    + "' and CONVERT(DATETIME,WF_DocCreated,120) between '" + sDate0 + "' and '" + sDate2 + "' group by datepart(m,WF_DocCreated)";
            Document[] dc1 = Rdb.getAllDocumentsBySql("", sql);
            sql = "select avg(datediff(n,WF_DocCreated,WF_EndTime)) as DifMin,datepart(MONTH,WF_DocCreated) as Mon from BPM_ArchivedData where WF_Processid='" + processid2
                    + "' and CONVERT(DATETIME,WF_DocCreated,120) between '" + sDate0 + "' and '" + sDate2 + "' group by datepart(m,WF_DocCreated)";
            Document[] dc2 = Rdb.getAllDocumentsBySql("", sql);

            for (int i = 1; i <= d1.getMonth() + 1; i++) {
                doc1 = getDocByFieldAndValue(dc1, "Mon", Integer.toString(i));
                doc2 = getDocByFieldAndValue(dc2, "Mon", Integer.toString(i));
                Document doc = new Document("");
                doc.s("Mon", Integer.toString(i) + "月");
                if (doc1 != null) {
                    doc.s("processid1", doc1.g("DifMin"));
                }
                else {
                    doc.s("processid1", "0");
                }
                if (doc2 != null) {
                    doc.s("processid2", doc2.g("DifMin"));
                }
                else {
                    doc.s("processid2", "0");
                }
                dc.add(doc);
            }
        }

        BeanCtx.p(Documents.dc2json(dc, ""));

        return "";
    }

    public Document getDocByFieldAndValue(Document[] dc, String fieldName, String fieldValue) {
        // Document doc = new Document("");
        for (Document doc : dc) {
            if (doc.g(fieldName).equals(fieldValue)) {
                return doc;
            }
        }
        return null;
    }
}