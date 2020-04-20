package cn.linkey.rulelib.S002;

import java.util.*;
import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.doc.Documents;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * @RuleName:流程列表视图事件
 * @author admin
 * @version: 8.0
 * @Created: 2014-05-04 19:56
 */
final public class R_S002_E002 implements LinkeyRule {

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
        String tableList = BeanCtx.getSystemConfig("ProcessModTableList");
        String[] tableArray = Tools.split(tableList);
        for (String tableName : tableArray) {
            String sql = "select * from " + tableName + " where Processid='" + doc.g("Processid") + "'";
            Document[] dc = Rdb.getAllDocumentsBySql(sql);
            if (dc.length > 0) {
                Documents.remove(dc);
            }
        }
        return "流程成功删除!";
    }

    public String onDocCopy(Document doc, Document gridDoc) {
        //如果不返回1则表示退出本次拷贝操作，并弹出返回的字符串为提示消息
        BeanCtx.setDocNotEncode();
        String tableList = BeanCtx.getSystemConfig("ProcessModTableList");
        String[] tableArray = Tools.split(tableList);
        String processid = Rdb.getNewUnid();
        for (String tableName : tableArray) {
            String sql = "select * from " + tableName + " where Processid='" + doc.g("Processid") + "'";
            Document[] dc = Rdb.getAllDocumentsBySql(sql);
            for (Document nodedoc : dc) {
                nodedoc.s("WF_OrUnid", Rdb.getNewid(""));
                nodedoc.s("Processid", processid);
                if (nodedoc.g("NodeType").equals("Process")) {
                    nodedoc.s("NodeName", nodedoc.g("NodeName") + "(copy)");
                }
                nodedoc.save();
            }
        }
        return "流程拷贝成功!";
    }

    public String onBtnClick(Document doc, Document gridDoc) {
        //返回成功的提示信息
        String action = BeanCtx.g("WF_Btnid", true);
        String msg = "";
        //BeanCtx.out(action);
        if (action.equals("publish")) {
            doc.s("Status", "1");
            msg = "流程成功发布";
        }
        else if (action.equals("stop")) {
            doc.s("Status", "0");
            msg = "流程成功停止";
        }
        doc.save();

        return msg;
    }

}