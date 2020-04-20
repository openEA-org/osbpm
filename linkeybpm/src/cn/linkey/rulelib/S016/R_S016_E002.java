package cn.linkey.rulelib.S016;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.rule.LinkeyRule;

/**
 * @RuleName:流程上线比例分析
 * @author admin
 * @version: 8.0
 * @Created: 2014-10-15 16:25
 */
final public class R_S016_E002 implements LinkeyRule {

    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        Document viewDoc = (Document) params.get("GridDoc"); //grid配置文档
        String eventName = (String) params.get("EventName");//事件名称
        if (eventName.equals("onViewOpen")) {
            return onViewOpen(viewDoc);
        }
        return "1";
    }

    public String onViewOpen(Document viewDoc) throws Exception {
        //如果不返回1则表示退出本视图并输出返回的字符串
        //图表JsHeader中用[X]字段名[/X]标识,viewDoc.s("字段名","10,20,30")来动态输出计算好的数据,

        //计算已发布的流程数
        String sql = "select count(*) as TotalNum from BPM_ModProcessList where Status='1'";
        String totalNum = Rdb.getValueBySql(sql);
        viewDoc.s("PublishNum", totalNum);

        sql = "select count(*) as TotalNum from BPM_ModProcessList where Status='0'";
        totalNum = Rdb.getValueBySql(sql);
        viewDoc.s("NoPublishNum", totalNum);

        //计算未发布的流程数

        return "1";
    }

}