package cn.linkey.rulelib.S001;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.doc.Documents;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;

/**
 * 获得所有验证规则列表
 * 
 * @author Administrator
 */
public class R_S001_B014 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) {
    	String UIType = BeanCtx.g("UIType"); //20180315新增UI 类型判断
        String sql = "select validType,validTypeName from BPM_FormValidTypeConfig where UIType='" + UIType + "'";
        Document[] dc = Rdb.getAllDocumentsBySql(sql);
        String jsonStr = Documents.dc2json(dc, "", true);
        BeanCtx.print(jsonStr); // 输出json字符串

        return "";
    }
}
