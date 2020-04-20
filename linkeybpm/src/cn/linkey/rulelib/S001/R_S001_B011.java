package cn.linkey.rulelib.S001;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.doc.Documents;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * 获得所有表单列表
 * 
 * @author Administrator
 */
public class R_S001_B011 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) {
        BeanCtx.getResponse().setContentType("text/json;charset=utf-8");
        String appid = BeanCtx.g("appid");
        String formType = BeanCtx.g("FormType"); //表单类型
        if (Tools.isBlank(formType)) {
            formType = "1";
        } // 默认为应用表单
        String sql = "";
        if (Tools.isBlank(appid)) {
            sql = "select FormNumber,FormName,WF_Appid from BPM_FormList where FormType='" + formType + "' order by FormNumber";
        }
        else {
            sql = "select FormNumber,FormName,WF_Appid from BPM_FormList where WF_Appid='" + appid + "' and FormType='" + formType + "' order by FormNumber";
        }
        // 		BeanCtx.out(sql);
        Document[] dc = Rdb.getAllDocumentsBySql(sql);
        String jsonStr = Documents.dc2json(dc, "", true);
        BeanCtx.print(jsonStr); // 输出json字符串

        return "";
    }
}