package cn.linkey.rulelib.S001;

import java.util.HashMap;

import org.quartz.Scheduler;

import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;

/**
 * @RuleName:作业调度管理事件
 * @author admin
 * @version: 8.0
 * @Created: 2014-05-13 17:14
 */
final public class R_S001_E062 implements LinkeyRule {

    @Override
    public String run(HashMap<String, Object> params) {
        //获取事件运行参数
        Document formDoc = (Document) params.get("FormDoc"); //表单配置文档
        Document doc = (Document) params.get("DataDoc"); //数据主文档
        String eventName = (String) params.get("EventName");//事件名称
        if (eventName.equals("onFormOpen")) {
            String readOnly = (String) params.get("ReadOnly"); //1表示只读，0表示编辑
            return onFormOpen(doc, formDoc, readOnly);
        }
        else if (eventName.equals("onFormSave")) {
            return onFormSave(doc, formDoc);
        }
        return "1";
    }

    public String onFormOpen(Document doc, Document formDoc, String readOnly) {
        //当表单打开时
        //如果是阅读状态则可不执行
        if (readOnly.equals("1")) {
            return "1";
        }
        if (doc.isNewDoc()) {
            //可以对表单字段进行初始化如:doc.s("fdname",fdvalue),可以获取字段值 doc.g("fdname")
        }
        try {
            Scheduler sched = BeanCtx.getSchedulerEngine().getSched();
            if (sched != null) {
                if (BeanCtx.getSchedulerEngine().getSched().isShutdown()) {
                    doc.s("Status", "<font color=blue>已停止</font>");
                }
                else {
                    doc.s("Status", "<font color=red>运行中</font>");
                }
            }
            else {
                doc.s("Status", "<font color=blue>未启动</font>");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        //成功必须返回1，否则表示退出并显示返回的字符串
        return "1";
    }

    public String onFormSave(Document doc, Document formDoc) {
        //当表单存盘前
        String msg = "";
        if (doc.g("WF_Action").equals("stop")) {
            boolean r = BeanCtx.getSchedulerEngine().shutdown(true);
            if (r) {
                msg = "当前服务器的调度器已成功停止，并已通知其他集群服务器!";
            }
            else {
                msg = "调度器停止失败，请查看后台日记!";
            }
        }
        else if (doc.g("WF_Action").equals("start")) {
            boolean r = BeanCtx.getSchedulerEngine().run();
            if (r) {
                msg = "当前服务器的调度器已成功启动，如果有集群服务器请查看其他服务器的运行情况！";
            }
            else {
                msg = "当前服务器调度器启动失败，请查看后日记!";
            }
        }
        //成功必须返回1，否则表示退出存盘
        return msg;
    }

}