package cn.linkey.rulelib.S001;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.rule.LinkeyRule;

/**
 * @RuleName:所有设计元素视图事件
 * @author admin
 * @version: 8.0
 * @Created: 2014-06-03 15:46
 */
final public class R_S001_E069 implements LinkeyRule {

    @Override
    public String run(HashMap<String, Object> params) {
        Document gridDoc = (Document) params.get("GridDoc"); //grid配置文档
        Document doc = (Document) params.get("DataDoc"); //数据主文档
        String eventName = (String) params.get("EventName");//事件名称
        if (eventName.equals("onGridOpen")) {
            return onGridOpen(gridDoc);
        }
        else if (eventName.equals("onDocDelete")) {
            return onDocDelete(doc, gridDoc);
        }
        else if (eventName.equals("onDocCopy")) {
            return onDocCopy(doc, gridDoc);
        }
        else if (eventName.equals("onBtnClick")) {
            return onBtnClick(doc, gridDoc);
        }
        return "1";
    }

    public String onGridOpen(Document gridDoc) {
        //如果不返回1则表示退出本视图并输出返回的字符串
        //通过gridDoc.s("WF_SearchBar","自定义操作条上的搜索框HTML代码");

        return "1";
    }

    public String onDocDelete(Document doc, Document gridDoc) {
        //如果不返回1则表示退出本次删除操作，并弹出返回的字符串为提示消息
        String tableName = doc.g("TableName");
        String sql = "select * from " + tableName + " where WF_OrUnid='" + doc.g("WF_OrUnid") + "'";
        Document edoc = Rdb.getDocumentBySql(sql);
        edoc.remove(true);
        return "设计成功删除!";
    }

    public String onDocCopy(Document doc, Document gridDoc) {
        //如果不返回1则表示退出本次拷贝操作，并弹出返回的字符串为提示消息

        return "1";
    }

    public String onBtnClick(Document doc, Document gridDoc) {
        //返回操作完成后的提示信息
        //String action=BeanCtx.g("WF_Btnid"); 获得按扭编号

        return "";
    }

}