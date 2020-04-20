package cn.linkey.rulelib.S016;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.wf.ModNode;

/**
 * @RuleName:年度流程效率趋势分析
 * @author admin
 * @version: 8.0
 * @Created: 2014-07-01 17:59
 */
final public class R_S016_E001 implements LinkeyRule {

    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        Document gridDoc = (Document) params.get("GridDoc"); //grid配置文档
        Document doc = (Document) params.get("DataDoc"); //数据主文档
        String eventName = (String) params.get("EventName");//事件名称
        if (eventName.equals("onGridOpen")) {
            return onGridOpen(gridDoc);
        }
        else if (eventName.equals("onDocDelete")) {
            return onDocDelete(doc, gridDoc);
        }
        else if (eventName.equals("onDocCopy")) {
            return onDocCopy(doc, gridDoc);
        }
        else if (eventName.equals("onBtnClick")) {
            return onBtnClick(doc, gridDoc);
        }
        return "1";
    }

    public String onGridOpen(Document gridDoc) throws Exception {
        //如果不返回1则表示退出本视图并输出返回的字符串
        //通过gridDoc.s("WF_SearchBar","自定义操作条上的搜索框HTML代码");
        //更改流程名称
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
        //月初月末
        //年初
        cal.set(Calendar.DAY_OF_YEAR, 1);
        Date d0 = cal.getTime();
        String sDate0 = sdf.format(d0) + " 00:00";

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
        //BeanCtx.out(sql);
        //BeanCtx.setDebug();
        //Document doc = Rdb.getDocumentBySql(sql);
        String processid1 = "";
        /*
         * try{ ResultSet rs = Rdb.getResultSet(sql); while(rs.next()){ processid1 = rs.getString("WF_Processid"); } Rdb.closers(rs); }catch(Exception e){ e.printStackTrace(); }
         */
        Document doc1 = Rdb.getDocumentBySql("", sql);
        if (!doc1.isNull()) {
            processid1 = doc1.g("WF_Processid");
        }
        //效率降低最快
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

        //获取流程名称
        ModNode modNode = new ModNode();
        String ProcessName1 = "";
        Document modNodeDoc = modNode.getNodeDoc(processid1, "Process");
        if (!modNodeDoc.isNull()) {
            ProcessName1 = "效率提高最快：" + modNodeDoc.g("NodeName");
        }
        else {
            ProcessName1 = "效率提高最快流程";
        }
        if (ProcessName1.length() > 15)
            ProcessName1 = ProcessName1.substring(0, 15) + "...";
        //
        String ProcessName2 = "";
        modNodeDoc = modNode.getNodeDoc(processid2, "Process");
        if (!modNodeDoc.isNull()) {
            ProcessName2 = "效率降低最快：" + modNodeDoc.g("NodeName");
        }
        else {
            ProcessName2 = "效率降低最快流程";
        }
        if (ProcessName2.length() > 15)
            ProcessName2 = ProcessName2.substring(0, 15) + "...";
        //更改配置
        gridDoc.s("Field_Title", ProcessName1 + "," + ProcessName2);

        return "1";
    }

    public String onDocDelete(Document doc, Document gridDoc) throws Exception {
        //如果不返回1则表示退出本次删除操作，并弹出返回的字符串为提示消息

        return "1";
    }

    public String onDocCopy(Document doc, Document gridDoc) throws Exception {
        //如果不返回1则表示退出本次拷贝操作，并弹出返回的字符串为提示消息

        return "1";
    }

    public String onBtnClick(Document doc, Document gridDoc) throws Exception {
        //返回操作完成后的提示信息
        //String action=BeanCtx.g("WF_Btnid"); 获得按扭编号

        return "";
    }

}