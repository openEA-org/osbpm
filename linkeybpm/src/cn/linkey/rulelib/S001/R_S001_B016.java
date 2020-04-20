package cn.linkey.rulelib.S001;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.doc.Documents;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;

/**
 * 获得所有表单的选择器列表
 * 
 * @author Administrator
 */
public class R_S001_B016 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) {
        String sql = "select Selectorid,SelectorName from BPM_FormSelectorConfig";
        Document[] dc = Rdb.getAllDocumentsBySql("BPM_FormSelectorConfig", sql);
        String jsonStr = Documents.dc2json(dc, "", true);
        BeanCtx.print(jsonStr); // 输出json字符串
        return "";
    }
}
