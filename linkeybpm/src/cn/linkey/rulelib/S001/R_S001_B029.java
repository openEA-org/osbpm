package cn.linkey.rulelib.S001;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.doc.Documents;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * 获得treedata数据源列表
 * 
 * @author Administrator
 */
public class R_S001_B029 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) {
        String appid = BeanCtx.g("appid", true);
        String sql = "";
        if (Tools.isBlank(appid)) {
            sql = "select Dataid as Dataid,DataName as DataName,WF_Appid from BPM_DataSourceList where DataType='2'";
        }
        else {
            sql = "select Dataid as Dataid,DataName as DataName,WF_Appid from BPM_DataSourceList where WF_Appid='" + appid + "' and DataType='2'";
        }
        Document[] dc = Rdb.getAllDocumentsBySql(sql);
        for (Document doc : dc) {
            doc.s("DataName", doc.g("DataName") + "(" + doc.g("Dataid") + ")");
        }
        BeanCtx.print(Documents.dc2json(dc, "")); // 输出json字符串

        return "";
    }
}