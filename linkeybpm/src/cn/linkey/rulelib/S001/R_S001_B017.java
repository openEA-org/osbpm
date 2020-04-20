package cn.linkey.rulelib.S001;

import java.util.HashMap;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.form.ModForm;

/**
 * 获得表单的所有字段属性
 * 
 * @author Administrator 编号:R_S001_B017
 */
public class R_S001_B017 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) {
        BeanCtx.getResponse().setContentType("text/json;charset=utf-8");

        String docunid = BeanCtx.g("wf_docunid", true); // 表单的unid值
        Document formDoc = ((ModForm) BeanCtx.getBean("ModForm")).getFormDocByDocUnid(docunid);
        String fieldConfig = "{\"rows\":[]}";
        if (!formDoc.isNull()) {
            fieldConfig = formDoc.g("FieldConfig");
            fieldConfig = fieldConfig.replace("\"fdList\"", "\"rows\"");
        }
        BeanCtx.print(fieldConfig);

        return "";
    }

}
