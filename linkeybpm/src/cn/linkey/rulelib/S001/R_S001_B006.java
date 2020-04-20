package cn.linkey.rulelib.S001;

import java.util.HashMap;

import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.form.ModForm;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * 从后台删除表单字段的配置信息
 * 
 * @author Administrator
 * 
 */
public class R_S001_B006 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) {
        BeanCtx.getResponse().setContentType("text/json;charset=utf-8");
        BeanCtx.setCtxData("WF_NoEncode", "1"); // 指定存盘时不进行编码操作

        String docunid = BeanCtx.g("wf_docunid", true); // 表单的unid值
        String fdname = BeanCtx.g("fdname"); // 要删除的字段名称
        String r = "0";
        Document formDoc = ((ModForm) BeanCtx.getBean("ModForm")).getFormDocByDocUnid(docunid);
        if (!formDoc.isNull()) {
            // 获得json字符串，并转换成为json对像，找到要删除的字段，把json item删除掉然后再更新sql记录
            String fieldConfig = formDoc.g("FieldConfig");
            JSONObject jsonObj = JSON.parseObject(fieldConfig);
            JSONArray jsonArr = jsonObj.getJSONArray("fdList");
            for (int i = 0; i < jsonArr.size(); i++) {
                JSONObject jsonItem = (JSONObject) jsonArr.get(i);
                if (jsonItem.get("name").equals(fdname)) {
                    jsonArr.remove(i);
                    r = "1";
                }
            }

            // 准备更新json数据
            if (r.equals("1")) {
                formDoc.s("FieldConfig", jsonObj.toString());
                formDoc.save();
            }
        }
        BeanCtx.print(r); // 输出1表示删除了一条记录
        return "";
    }

}
