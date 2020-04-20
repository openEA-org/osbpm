package cn.linkey.rulelib.S016;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.doc.Documents;
import cn.linkey.factory.BeanCtx;
import cn.linkey.org.LinkeyUser;
import cn.linkey.rule.LinkeyRule;

/**
 * @RuleName:本月用户申请量Top10
 * @author admin
 * @version: 8.0
 * @Created: 2014-07-02 09:20
 */
final public class R_S016_B011 implements LinkeyRule {
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

        LinkeyUser linkeyUser = (LinkeyUser) BeanCtx.getBean("LinkeyUser");

        String sql = "select top 10 COUNT(WF_OrUnid) as DocNum,WF_AddName from BPM_ArchivedData where";
        sql = sql + " WF_DocCreated<>'' and CONVERT(DATETIME,WF_DocCreated,120) between '" + sDate1 + "' and '" + sDate2 + "'";
        sql = sql + " group by WF_AddName";
        sql = sql + " order by DocNum desc";
        //LinkedHashSet<Document> dcs = new LinkedHashSet<Document>();
        Document[] dc = Rdb.getAllDocumentsBySql(sql);
        for (Document doc : dc) {
            doc.s("UserName", linkeyUser.getCnName(doc.g("WF_AddName")));
        }
        BeanCtx.print(Documents.dc2json(dc, ""));

        return "";
    }
}