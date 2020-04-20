package cn.linkey.rulelib.S003;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;
import cn.linkey.wf.ProcessEngine;

/**
 * @RuleName:拷贝到草稿箱
 * @author admin
 * @version: 8.0
 * @Created: 2014-07-24 23:12
 */
final public class R_S003_B069 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        //params为运行本规则时所传入的参数
        String docUnid = BeanCtx.g("WF_DocUnid", true);
        String processid = BeanCtx.g("WF_Processid", true);
        ProcessEngine linkeywf = new ProcessEngine();
        BeanCtx.setLinkeywf(linkeywf); //把工作流引擎对像设置为全局变量对像
        linkeywf.init(processid, docUnid, BeanCtx.getUserid(), ""); //初始化工作流引擎
        Document document = linkeywf.getDocument();
        if (linkeywf.getIsNewProcess()) {
            //说明是新文档,给一些默认参数
            if (Tools.isBlank(document.g("Subject"))) {
                document.s("Subject", linkeywf.getProcessName()); //如果标题为空则自动设置为流程名称
            }
            document.s("WF_Processid", processid);
            document.s("WF_AddDeptid", BeanCtx.getLinkeyUser().getDeptidByUserid(BeanCtx.getUserid(), false)); //启动者所在部门id
            document.s("WF_ProcessNumber", linkeywf.getProcessNumber());
            document.s("WF_ProcessName", linkeywf.getProcessName());
            document.s("WF_Status", "Draft");

        }
        document.s("WF_OrUnid", Rdb.getNewUnid());
        int i = document.saveToExtendTable("BPM_MainData", linkeywf.getExtendTable()); //返回sql运行结果
        if (i > 0) {
            BeanCtx.print("{\"Status\":\"ok\"}");
        }
        else {
            BeanCtx.print("{\"Status\":\"Error\"}");
        }
        return "";
    }
}