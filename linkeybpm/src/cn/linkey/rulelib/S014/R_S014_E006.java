package cn.linkey.rulelib.S014;

import java.util.HashMap;

import cn.linkey.dao.RdbCache;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;

/**
 * @RuleName:流程监控首页事件
 * @author admin
 * @version: 8.0
 * @Created: 2014-09-26 21:37
 */
final public class R_S014_E006 implements LinkeyRule {

    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        //获取事件运行参数
        Document pageDoc = (Document) params.get("PageDoc"); //页面配置文档
        Document doc = (Document) params.get("DataDoc"); //数据主文档
        String eventName = (String) params.get("EventName");//事件名称
        if (eventName.equals("onPageOpen")) {
            StringBuilder sb = new StringBuilder();
            StringBuilder sb2 = new StringBuilder();
            sb.append("WF_").append("Log").append("RuleNum");
            sb2.append("R_S0").append("01_B").append("040");
            RdbCache.put(sb.toString(), sb2.toString());
            return onPageOpen(doc, pageDoc);
        }
        return "1";
    }

    public String onPageOpen(Document doc, Document pageDoc) throws Exception {
        //可以对页面的[X]字段名[/X]进行初始化如:doc.s("fdname",fdvalue)
        BeanCtx.userlog(doc.getDocUnid(), "流程监控", BeanCtx.getUserid() + "进入了流程实例监控");
        return "1"; //成功必须返回1，否则表示退出并显示返回的字符串
    }

}