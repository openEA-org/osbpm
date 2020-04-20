package cn.linkey.rulelib.S026;

import java.util.HashMap;

import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * @RuleName:按字段保存模拟数据
 * @author admin
 * @version: 8.0
 * @Created: 2014-09-18 09:01
 */
final public class R_S026_B009 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        //params为运行本规则时所传入的参数
        String jsonData = BeanCtx.g("FieldData");
        Document doc = BeanCtx.getDocumentBean("BPM_SimFormList");
        doc.appendFromJsonStr(jsonData);
        doc.save();
        BeanCtx.p(Tools.jmsg("ok", "数据成功保存"));
        return "";
    }
}