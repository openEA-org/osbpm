package cn.linkey.rulelib.S016;

import java.util.HashMap;

import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;

/**
 * @RuleName:BPM_AllDocument字段选择
 * @author admin
 * @version: 8.0
 * @Created: 2014-11-11 15:39
 */
final public class R_S016_B025 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        //params为运行本规则时所传入的参数
        BeanCtx.p(
                "[{\"name\":\"WF_OrUnid\",\"value\":\"WF_OrUnid\"},{\"name\":\"Subject\",\"value\":\"Subject\"},{\"name\":\"WF_Appid\",\"value\":\"WF_Appid\"},{\"name\":\"WF_ProcessName\",\"value\":\"WF_ProcessName\"},{\"name\":\"WF_AddName_CN\",\"value\":\"WF_AddName_CN\"},{\"name\":\"WF_AddName\",\"value\":\"WF_AddName\"},{\"name\":\"WF_Author\",\"value\":\"WF_Author\"},{\"name\":\"WF_Author_CN\",\"value\":\"WF_Author_CN\"},{\"name\":\"WF_CurrentNodeName\",\"value\":\"WF_CurrentNodeName\"},{\"name\":\"WF_Status\",\"value\":\"WF_Status\"},{\"name\":\"WF_DocCreated\",\"value\":\"WF_DocCreated\"}] ");

        return "";
    }
}