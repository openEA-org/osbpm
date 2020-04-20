package cn.linkey.rulelib.S001;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.doc.Documents;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * 获得所有Json Data数据源列表
 * 
 * @author Administrator
 */
public class R_S001_B008 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) {
        String appid = BeanCtx.g("appid", true);
        String sql = "";
        if (Tools.isBlank(appid)) {
            sql = "select Dataid,DataName,WF_Appid from BPM_DataSourceList where DataType='1'";
            sql += "union all select RuleNum,RuleName,WF_Appid from BPM_RuleList where RuleType='9'";
        }
        else {
            sql = "select Dataid,DataName,WF_Appid from BPM_DataSourceList where WF_Appid='" + appid + "' and DataType='1'";
            sql += "union all select RuleNum,RuleName,WF_Appid from BPM_RuleList where WF_Appid='" + appid + "' and RuleType='9'";
        }
        Document[] dc = Rdb.getAllDocumentsBySql(sql);
        String jsonStr = Documents.dc2json(dc, "", true);
        BeanCtx.print(jsonStr); // 输出json字符串

        return "";
    }
}