package cn.linkey.rulelib.S003;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;
import cn.linkey.wf.ProcessEngine;

/**
 * 本规则完成暂存文档功能,如果是新文档存存为草稿
 * 
 * @author Administrator
 *
 */
public class R_S003_B031 implements LinkeyRule {

    @Override
    public String run(HashMap<String, Object> params) throws Exception {
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

        //准备暂存办理意见,一份文档只能存一个临时的办理意见
        String sql = "select * from BPM_TempRemarkList where Userid='" + BeanCtx.getUserid() + "' and DocUnid='" + docUnid + "'";
        Document doc = Rdb.getDocumentBySql(sql);
        doc.s("DocUnid", docUnid);
        doc.s("Userid", BeanCtx.getUserid());
        doc.s("Remark", BeanCtx.g("WF_Remark"));
        doc.save();

        if (Tools.isNotBlank(linkeywf.getCurrentNodeid())) {
            BeanCtx.getEventEngine().run(processid, linkeywf.getCurrentNodeid(), "FormBeforeTempSave", params); //表单暂存前
        }

        int i = document.saveToExtendTable("BPM_MainData", linkeywf.getExtendTable()); //返回sql运行结果

        if (i > 0) {
            BeanCtx.print("{\"msg\":\"" + BeanCtx.getMsg("Engine", "SaveDocOnly") + "\",\"Status\":\"ok\"}");
        }
        else {
            BeanCtx.print("{\"msg\":\"" + BeanCtx.getMsg("Engine", "Error_SaveMainDoc") + "\",\"Status\":\"Error\"}");
        }
        return "";
    }
}
