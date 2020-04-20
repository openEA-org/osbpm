package cn.linkey.rulelib.S008;

import java.util.HashMap;

import cn.linkey.dao.RdbCache;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * @RuleName:app解析所有嵌入设计 app.设计编号
 * @author admin
 * @version: 8.0
 * @Created: 2015-10-09 19:23
 */
final public class R_S008_B003 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        //params为运行本规则时所传入的参数
        String eleid = BeanCtx.g("eid", true);
        if (Tools.isBlank(eleid)) {
            eleid = (String) params.get("XTagValue");//获得指定的默认编号
        }
        params.put("eleid", eleid); //传入设计编号

        //获取解析器的配置缓存信息
        HashMap<String, String> configmap = (HashMap<String, String>) RdbCache.getSystemCache("BPM_XTagConfig", "ALL");

        //根据不同的设计元素调用不同的配置规则实现解析
        String ruleNum = "", configid = "", htmlCode = "";
        if (eleid.startsWith("P_")) {//页面
            configid = "page";
            ruleNum = configmap.get(configid);
        }
        else if (eleid.startsWith("F_")) {
            //表单
            configid = "form";
            ruleNum = configmap.get(configid);
        }
        else if (eleid.startsWith("V_")) {
            //视图
            configid = "view";
            ruleNum = configmap.get(configid);
        }
        else if (eleid.startsWith("T_")) {
            //导航树
            configid = "navtree";
            ruleNum = configmap.get(configid);
        }
        else if (eleid.startsWith("R_")) {
            //规则
            ruleNum = eleid;
        }
        if (Tools.isBlank(ruleNum)) {
            params.clear();
            return "错误:未找到相应的解析器!";
        }
        htmlCode = BeanCtx.getExecuteEngine().run(ruleNum, params);
        params.clear();
        return htmlCode;
    }
}