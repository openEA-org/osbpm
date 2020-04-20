package cn.linkey.rulelib.S016;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * @RuleName:保存字段列
 * @author admin
 * @version: 8.0
 * @Created: 2014-10-31 14:09
 */
final public class R_S016_B022 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        //params为运行本规则时所传入的参数
        String alldata = BeanCtx.g("alldata", true);
        String wf_docunid = BeanCtx.g("wf_docunid", true);
        Document document = new Document("BPM_DataAnalyse");
        String reportformname = BeanCtx.g("reportformname", true);
        String process = BeanCtx.g("process", true);
        String datafw = BeanCtx.g("datafw", true);
        String starttime = BeanCtx.g("starttime", true);
        String endtime = BeanCtx.g("endtime", true);
        String sortfield = BeanCtx.g("sortfield", true);
        String isdesc = BeanCtx.g("isdesc", true);
        String returnnum = BeanCtx.g("returnnum", true);
        String expression = BeanCtx.g("expression", true);
        String checksql = "select fieldshow from BPM_DataAnalyse where wf_orunid='" + wf_docunid + "'";
        Document doc = Rdb.getDocumentBySql(checksql);
        //System.out.println(checksql);
        if (doc.g("fieldshow").length() < 4 && alldata.length() < 4)
            alldata = "[{\"FieldName\":\"WF_DocCreated\",\"FieldDescribe\":\"状态\"}]";
        //System.out.println("alldata"+alldata);
        //System.out.println("wf_docunid:"+wf_docunid);
        //String sql="update App_DataAnalyse  set FieldShow ='"+alldata+"' where wf_orunid='"+wf_docunid+"'";
        //   System.out.println("reportformname:"+reportformname);
        // System.out.println("wf_orunid:"+wf_docunid);
        // System.out.println("process:"+process);
        if (process.equals("-无-")) {
            process = "";
        }
        document.s("reportformname", reportformname);
        document.s("wf_orunid", wf_docunid);
        document.s("process", process);
        document.s("datafw", datafw);
        document.s("starttime", starttime);
        document.s("endtime", endtime);
        document.s("sortfield", sortfield);
        document.s("isdesc", isdesc);
        document.s("returnnum", returnnum);
        document.s("expression", expression);
        document.s("fieldshow", alldata);
        document.save();
        // Rdb.setAutoCommit(false);
        //Connection conn=Rdb.getConnection();
        //conn.setAutoCommit(false);
        // PreparedStatement ps = conn.prepareStatement(sql);
        // int i =ps.executeUpdate();
        //sm.close();
        //  conn.commit();
        //  conn.close();
        //  System.out.println(i);
        // if(i==1)
        // {
        //   System.out.println("excute success");
        BeanCtx.p(Tools.jmsg("ok", "保存成功"));
        //  }
        // else  BeanCtx.p(Tools.jmsg("error", "error"));
        return "";
    }
}