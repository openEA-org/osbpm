package cn.linkey.rulelib.S003;

import java.util.HashMap;
import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.form.ModForm;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.DateUtil;
import cn.linkey.util.Tools;
import cn.linkey.wf.ProcessEngine;

/**
 * 打开归档文件
 * 
 * @author Administrator
 *
 */
public class R_S003_B062 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> nodeParams) throws Exception {
        String docUnid = BeanCtx.g("wf_docunid", true); //文档id
        //兼容小写
        if (Tools.isBlank(docUnid)) {
            docUnid = BeanCtx.g("wf_docunid", true);
        }

        //从视图中得到文档对像
        String sql = "select * from BPM_AllDocument where WF_OrUnid='" + docUnid + "'";
        Document doc = Rdb.getDocumentBySql(sql);
        String processid = doc.g("WF_Processid");
        if (!Rdb.hasRecord("select WF_OrUnid from BPM_ModProcessList where Processid='" + processid + "'")) {
            BeanCtx.showErrorMsg("错误:流程id(" + processid + ")不存在!");
            return "";
        }

        //初始化工作流引擎
        ProcessEngine linkeywf = new ProcessEngine();
        BeanCtx.setLinkeywf(linkeywf);
        linkeywf.init(processid, docUnid, BeanCtx.getUserid(), "");

        //6.检查流程有没有指定业务扩展表，如果有指定需要合并业务扩展表数据2015.6.3增加
        if (!linkeywf.getExtendTable().equalsIgnoreCase("xmldata")) {
            sql = "select * from " + linkeywf.getExtendTable() + " where WF_OrUnid='" + docUnid + "'";
            Document extDoc = Rdb.getDocumentBySql(linkeywf.getExtendTable(), sql);
            extDoc.copyAllItems(doc);
            doc.s("WF_OrUnid", docUnid);
        }

        //重新设置工作流引擎的一些初始化参数
        linkeywf.setDocument(doc);
        linkeywf.setReadOnly(true);
        if (Tools.isNotBlank(doc.g("WF_ArcFormNumber"))) {
            ModForm modForm = (ModForm) BeanCtx.getBean("ModForm");
            Document formDoc = modForm.getFormDoc(doc.g("WF_ArcFormNumber"));
            linkeywf.setFormDoc(formDoc);//归档后更换显示表单
        }
        linkeywf.setNewProcess(false);
        linkeywf.setFirstNode(false);
        String htmlBody = linkeywf.open();

        BeanCtx.print(htmlBody);

        //增加阅读记录
        addProcessReadLog(docUnid, processid, "阅读");

        return "";
    }

    /**
     * 记录文件阅读记录
     */
    public static void addProcessReadLog(String docUnid, String processid, String remark) {
        if (BeanCtx.getSystemConfig("ProcessDocReadLog").equals("1")) {
            String ip = "";
            if (BeanCtx.getRequest() != null) {
                ip = BeanCtx.getRequest().getRemoteAddr();
            }
            remark = remark.replace("'", "''");
            String sql = "insert into BPM_AttachmentLog(WF_OrUnid,DocUnid,Userid,Processid,IP,Remark,WF_DocCreated) " + "values('" + Rdb.getNewUnid() + "','" + docUnid + "','" + BeanCtx.getUserName()
                    + "(" + BeanCtx.getUserid() + ")" + "','" + processid + "','" + ip + "','" + remark + "','" + DateUtil.getNow() + "')";
            Rdb.execSql(sql);
        }
    }

}
