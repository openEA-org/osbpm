package cn.linkey.rule;

import java.util.HashMap;

import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;

// 表单打开事件、表单存盘事件、流程打开事件、流程启动事件
// 事件源UNID,如表单的UNID号，流程的UNID号,事件编号的ID值如：FormOpen,ProcessOpen,FormSave
public class EventEngine {

    /**
     * 根据流程id节点id和事件类型运行事件规则
     * 
     * @param processid 流程id号
     * @param nodeid 节点id号
     * @param eventType 事件类型
     * @param params 运行时参数，如果没有参数不能传null值可以用另一个run()方法
     * @return 返回字符串
     */
    public String run(String processid, String nodeid, String eventid, HashMap<String, Object> params) throws Exception {
        RuleConfig ruleConfig = (RuleConfig) BeanCtx.getBean("RuleConfig");

        // 首先看事件是否有定义，如果不存在直接返回1表示成立
        Document[] dc = ruleConfig.getEventConfig(processid, eventid, nodeid);
        if (dc.length == 0) {
            return "1";
        }

        // 根据事件中定义执行的规则编号找到规则并运行,通过rulenum运行规则可以使用缓存功能，速度更快
        String result = "";
        for (Document eventConfigDoc : dc) {
            params.put("Processid", processid);
            params.put("Nodeid", nodeid);
            params.put("Eventid", eventid);
            params.put("EventParams", eventConfigDoc.g("Params")); //运行规则所需的参数
            result = BeanCtx.getExecuteEngine().run(eventConfigDoc.g("RuleNum"), params);
            if (result.equals("0")) {
                return "0";
            }
        }
        return result;
    }

    /**
     * 根据流程id节点id和事件类型运行事件规则
     * 
     * @param processid 流程id号
     * @param nodeid 节点id号
     * @param eventid 事件类型
     * @return 返回字符串
     */
    public String run(String processid, String nodeid, String eventid) throws Exception {
        return run(processid, nodeid, eventid, new HashMap<String, Object>());
    }

}
