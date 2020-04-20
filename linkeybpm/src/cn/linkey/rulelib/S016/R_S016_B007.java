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

/**
 * @RuleName:本月流程处理量
 * @author admin
 * @version: 8.0
 * @Created: 2014-06-27 09:29
 */
final public class R_S016_B007 implements LinkeyRule {
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

        String sql = "select count(*) as TotalNum from BPM_MainData where WF_Status='Current' and WF_DocCreated<>'' and CONVERT(DATETIME,WF_DocCreated,120) between '" + sDate1 + "' and '" + sDate2
                + "'";
        String totalNum = Rdb.getValueBySql(sql);

        //LinkedHashSet<Document> dc =new LinkedHashSet<Document>();
        //dc.add(doc);
        Document[] dc = new Document[2];
        Document doc = new Document("");
        doc.s("Field_Title", "流转中数量");
        doc.s("Field_Value", totalNum);
        doc.s("Field_Color", "#00FF66");

        dc[0] = doc;

        sql = "select count(*) as TotalNum from BPM_ArchivedData where WF_EndTime<>'' and CONVERT(DATETIME,WF_EndTime,120) between '" + sDate1 + "' and '" + sDate2 + "'";
        totalNum = Rdb.getValueBySql(sql);
        doc = new Document("");
        doc.s("Field_Title", "已归档数量");
        doc.s("Field_Value", totalNum);
        doc.s("Field_Color", "#0066FF");
        dc[1] = doc;

        BeanCtx.print(Documents.dc2json(dc, ""));

        return "";
    }
}