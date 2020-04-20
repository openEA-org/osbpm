package cn.linkey.rulelib.S006;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.rule.LinkeyRule;

/**
 * @RuleName:部门管理视图事件
 * @author admin
 * @version: 8.0
 * @Created: 2014-04-22 14:15
 */
final public class R_S006_E003 implements LinkeyRule {

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

        String deptid = doc.g("Deptid");
        String sql = "select WF_OrUnid from BPM_OrgUserDeptMap where Deptid='" + deptid + "' union select WF_OrUnid from BPM_OrgRoleMembers where Deptid='" + deptid + "'";
        if (Rdb.hasRecord(sql)) {
            return "本部门下还有用户或角色/岗位的成员配置记录，删除失败!";
        }
        return "1";
    }

    public String onDocCopy(Document doc, Document gridDoc) {
        //如果不返回1则表示退出本次拷贝操作，并弹出返回的字符串为提示消息

        return "1";
    }

    public String onBtnClick(Document doc, Document gridDoc) {
        //返回成功的提示信息

        return "";
    }

}