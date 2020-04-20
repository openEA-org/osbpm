package cn.linkey.rulelib.S016;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.rule.LinkeyRule;

/**
 * @RuleName:待办数量前10用户
 * @author admin
 * @version: 8.0
 * @Created: 2014-10-22 09:34
 */
final public class R_S016_E030 implements LinkeyRule {

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
        String sql = "select distinct WF_AddName,COUNT(wf_addname) as num from BPM_MainData group by WF_AddName order by num desc";
        String namesql = "";
        String nodename = "[";
        String value = "[";
        int i = 0;
        Document[] document = Rdb.getAllDocumentsBySql(sql);
        for (Document doc : document) {
            namesql = "select cnname from BPM_OrgUserList where Userid='" + doc.g("WF_AddName") + "'";
            if (Rdb.getDocumentBySql(namesql).g("cnname").length() > 0 && i < 10) {
                nodename = nodename + "'" + Rdb.getDocumentBySql(namesql).g("cnname") + "',";
                value = value + "{value:" + doc.g("num") + ",name:'" + Rdb.getDocumentBySql(namesql).g("cnname") + "'},";
                i++;
            }
        }
        if (nodename.length() > 1) {
            nodename = nodename.substring(0, nodename.length() - 1);
            value = value.substring(0, value.length() - 1);
        }
        nodename = nodename + "]";
        value = value + "]";
        viewDoc.s("nodename", nodename);
        viewDoc.s("value", value);
        return "1";
    }

}