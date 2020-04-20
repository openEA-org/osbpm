package cn.linkey.rulelib.S016;

import java.util.HashMap;

import cn.linkey.doc.Document;
import cn.linkey.rule.LinkeyRule;

/**
 * @RuleName:标准饼图
 * @author admin
 * @version: 8.0
 * @Created: 2014-10-17 11:36
 */
final public class R_S016_E009 implements LinkeyRule {

    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        Document viewDoc = (Document) params.get("GridDoc"); //grid配置文档
        String eventName = (String) params.get("EventName");//事件名称
        String processid = "ss";
        if (eventName.equals("onViewOpen")) {
            return onViewOpen(viewDoc, processid);
        }
        return "1";
    }

    public String onViewOpen(Document viewDoc, String processid) throws Exception {
        //如果不返回1则表示退出本视图并输出返回的字符串
        //图表JsHeader中用[X]字段名[/X]标识,viewDoc.s("字段名","10,20,30")来动态输出计算好的数据,
        viewDoc.s("processname", "ss");
        return "1";
    }

}