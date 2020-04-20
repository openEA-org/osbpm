package cn.linkey.rulelib.S001;

import java.util.HashMap;

import cn.linkey.app.AppUtil;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * @RuleName:定时规则视图事件
 * @author admin
 * @version: 8.0
 * @Created: 2014-05-13 15:54
 */
final public class R_S001_E061 implements LinkeyRule {

    @Override
    public String run(HashMap<String, Object> params) throws Exception {
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
        return "1";
    }

    public String onDocCopy(Document doc, Document gridDoc) {
        //如果不返回1则表示退出本次拷贝操作，并弹出返回的字符串为提示消息
        BeanCtx.setCtxData("WF_NoEncode", "1"); // 指定存盘时不进行编码操作

        String appid = doc.g("WF_Appid");
        String sql = "select RuleNum from BPM_RuleList where WF_Appid='" + appid + "' and RuleType='5' order by RuleNum desc";
        String newNum = AppUtil.getElNewNum(sql);
        if (Tools.isBlank(newNum)) {
            newNum = doc.g("RuleNum") + "(Copy)";
        }
        String ruleCode = doc.g("RuleCode");
        ruleCode = ruleCode.replace(doc.g("RuleNum"), newNum); //替换规则中的类名称
        doc.s("RuleCode", ruleCode);
        doc.s("RuleName", doc.g("RuleName") + "(copy)");
        doc.s("RuleNum", newNum);
        return "1";
    }

    public String onBtnClick(Document doc, Document gridDoc) throws Exception {
        //返回成功的提示信息
        String action = BeanCtx.g("WF_Btnid", true);
        if (action.equals("pauseJob")) {
            /*boolean r = BeanCtx.getSchedulerEngine().pauseJob(doc.g("RuleNum"), true);
            if (r) {
                return "(" + doc.g("RuleName") + ")已经暂停运行!";
            }
            else {
                return "(" + doc.g("RuleName") + ")暂停运行失败，请查看后台管理日记!";
            }*/ 
        	//20190410 修改返回信息
        	return BeanCtx.getSchedulerEngine().pauseJob(doc.g("RuleNum"), true);
        }
        else if (action.equals("startJob")) {
            return BeanCtx.getSchedulerEngine().startJob(doc.g("RuleNum"), true);
        }
        else if (action.equals("resumeJob")) {
            /*boolean r = BeanCtx.getSchedulerEngine().resumeJob(doc.g("RuleNum"), true);
            if (r) {
                return "(" + doc.g("RuleName") + ")已经恢复运行!";
            }
            else {
                return "(" + doc.g("RuleName") + ")恢复运行失败，请查看后台管理日记!";
            }*/
        	//20190410 修改返回信息
        	return BeanCtx.getSchedulerEngine().resumeJob(doc.g("RuleNum"), true);
        }
        else if (action.equals("deleteJob")) {
            boolean r = BeanCtx.getSchedulerEngine().deleteJob(doc.g("RuleNum"), true);
            if (r) {
                return "(" + doc.g("RuleName") + ")已经从作业务调度器中删除!";
            }
            else {
                return "(" + doc.g("RuleName") + ")删除失败，请查看后台管理日记!";
            }
        }
        else if (action.equals("getRuningJob")) {
            String r = BeanCtx.getSchedulerEngine().getRunningJob();
            return r;
        }
        return "";
    }
}