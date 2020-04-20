package cn.linkey.rulelib.S001;

import java.util.*;
import cn.linkey.doc.*;
import cn.linkey.factory.*;
import cn.linkey.form.ModForm;
import cn.linkey.rule.LinkeyRule;

/**
 * @RuleName:根据表单编号获得表单字段JSON
 * @author admin
 * @version: 8.0
 * @Created: 2014-05-24 15:12
 */
final public class R_S001_B048 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) {
        //params为运行本规则时所传入的参数
        BeanCtx.getResponse().setContentType("text/json;charset=utf-8");

        String formNumber = BeanCtx.g("FormNumber"); // 表单的unid值
        Document formDoc = ((ModForm) BeanCtx.getBean("ModForm")).getFormDoc(formNumber);
        String fieldConfig = "[]";
        if (!formDoc.isNull()) {
            fieldConfig = formDoc.g("FieldConfig");
            int spos = fieldConfig.indexOf("[");
            int epos = fieldConfig.lastIndexOf("]");
            fieldConfig = fieldConfig.substring(spos, epos + 1);
        }
        BeanCtx.print(fieldConfig);

        return "";
    }
}
