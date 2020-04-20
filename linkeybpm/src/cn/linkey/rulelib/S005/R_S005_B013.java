package cn.linkey.rulelib.S005;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * @RuleName:获取打印模版书签的值
 * @author admin
 * @version: 8.0
 * @Created: 2014-06-10 17:06
 */
final public class R_S005_B013 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) {
        //params为运行本规则时所传入的参数
        BeanCtx.getResponse().setContentType("text/json;charset=utf-8");
        String fdList = BeanCtx.g("FdList");
        fdList = Tools.decode(fdList);
        String[] fdArr = fdList.split(",");

        Document doc = new Document("");
        String sFieldList = "";
        String sFdName = "";
        String sFdValue = "";

        String docUnid = BeanCtx.g("DocUnid");
        if (!Tools.isBlank(docUnid)) {
            String sql = "select * from BPM_AllDocument where WF_OrUnid='" + docUnid + "'";
            doc = Rdb.getDocumentBySql(sql);
            if (!doc.isNull()) {
                //doc = new Document("");
            }
        }
        for (int i = 0; i < fdArr.length; i++) {
            sFdName = fdArr[i];
            if (!sFdName.equals("")) {
                if (!sFdName.substring(0, 7).equals("Remark_")) {
                    sFdValue = doc.g(sFdName);
                }
                else {
                    //sFdValue = "意见_"+sFdName.substring(7);
                    sFdValue = GetDocRemarkType(docUnid, sFdName.substring(7));
                }
                sFieldList = sFieldList.concat(",\"" + sFdName + "\":\"" + sFdValue + "\"");
            }

        }
        if (!sFieldList.equals("")) {
            if (sFieldList.substring(0, 1).equals(","))
                sFieldList = sFieldList.substring(1);
        }
        //BeanCtx.print("{\"Subject\":\"123\",\"Remark_All\":\"456\"}");
        //BeanCtx.out(sFieldList);
        BeanCtx.print("{" + sFieldList + "}");

        return "";
    }

    public String GetDocRemarkType(String docUnid, String remarkType) {
        if (docUnid.equals(""))
            return "";
        String sRemarkAll = "";
        String sql = "";
        if (Tools.isBlank(remarkType) || remarkType.equals("All")) {
            sql = "select UserName,EndTime,DeptName,Remark from BPM_AllRemarkList where DocUnid='" + docUnid + "'";
        }
        else {
            sql = "select UserName,EndTime,DeptName,Remark from BPM_AllRemarkList where DocUnid='" + docUnid + "' and remarkType='" + remarkType + "'";
        }
        Document[] dc = Rdb.getAllDocumentsBySql(sql);
        //加子数据
        for (Document doc : dc) {
            String sRemark = "\\n" + doc.g("Remark") + "          " + doc.g("UserName") + "/" + doc.g("DeptName") + " " + doc.g("EndTime");
            sRemarkAll = sRemarkAll + sRemark;
        }

        return sRemarkAll;
    }
}