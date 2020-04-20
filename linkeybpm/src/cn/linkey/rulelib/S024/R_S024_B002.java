package cn.linkey.rulelib.S024;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * @RuleName:流程表单打印时获得文档的所有字段JSON
 * @author admin
 * @version: 8.0
 * @Created: 2014-07-26 12:30
 */
final public class R_S024_B002 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        //params为运行本规则时所传入的参数
        String jsonStr = "{}";
        String processid = BeanCtx.g("Processid", true);
        String docUnid = BeanCtx.g("DocUnid", true);
        String fdList = BeanCtx.g("FdList", true);

        //再看是否有权打开此文档
        String sql = "select * from BPM_AllDocument where WF_OrUnid='" + docUnid + "'";
        Document doc = Rdb.getDocumentBySql(sql);
        if (doc.isNull()) {
            BeanCtx.p(jsonStr);
            return "";
        }
        if (doc.hasItem("WF_AllReaders")) {
            if (!BeanCtx.getLinkeyUser().inRoles(BeanCtx.getUserid(), doc.g("WF_AllReaders"))) {
                BeanCtx.p(jsonStr);
                return "";
            }
        }

        //输出所有字段的json
        StringBuilder jStr = new StringBuilder();
        String[] fdArray = Tools.split(fdList);
        for (String fdName : fdArray) {
            if (jStr.length() > 0) {
                jStr.append(",");
            }
            String sFdValue = "";
            if (!fdName.substring(0, 7).equals("Remark_")) {
                sFdValue = doc.g(fdName);
            }
            else {
                sFdValue = GetDocRemarkType(docUnid, fdName.substring(7));
            }

            jStr.append("\"" + fdName + "\":\"").append(Tools.encode(sFdValue)).append("\"");
        }
        jStr.insert(0, "{").append("}");
        BeanCtx.p(jStr.toString());

        return "";
    }

    public String GetDocRemarkType(String docUnid, String remarkType) {
        if (docUnid.equals(""))
            return "";
        String sRemarkAll = "";
        String sql = "";
        if (Tools.isBlank(remarkType) || remarkType.equalsIgnoreCase("All")) {
            sql = "select UserName,EndTime,DeptName,Remark from BPM_AllRemarkList where DocUnid='" + docUnid + "'";
        }
        else {
            sql = "select UserName,EndTime,DeptName,Remark from BPM_AllRemarkList where DocUnid='" + docUnid + "' and remarkType='" + remarkType + "'";
        }
        Document[] dc = Rdb.getAllDocumentsBySql(sql);
        //加子数据
        for (Document doc : dc) {
            String sRemark = doc.g("Remark") + "          " + doc.g("UserName") + "/" + doc.g("DeptName") + " " + doc.g("EndTime");
            if (Tools.isBlank(sRemarkAll)) {
                sRemarkAll = sRemark;
            }
            else {
                sRemarkAll = sRemarkAll + "<br>" + sRemark;
            }
        }
        return sRemarkAll;
    }

}