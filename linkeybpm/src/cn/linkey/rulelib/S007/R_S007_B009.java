package cn.linkey.rulelib.S007;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;
import cn.linkey.wf.ModNode;
import cn.linkey.form.ModForm;

/**
 * 获得表单的所有字段列表json
 * 
 * @author Administrator
 */
public class R_S007_B009 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) {
        BeanCtx.getResponse().setContentType("text/json;charset=utf-8");

        ModNode modNode = (ModNode) BeanCtx.getBean("ModNode");
        String processid = BeanCtx.g("Processid", true);
        String nodeType = BeanCtx.g("NodeType", true);
        String nodeid = BeanCtx.g("Nodeid", true);

        //获得节点文档是否有选择更换主表单
        String nodeTableName = modNode.getNodeTableName(processid, nodeid); //节点所在数据库表
        String sql = "select * from " + nodeTableName + " where Processid='" + processid + "' and Nodeid='" + nodeid + "'";
        Document nodeDoc = Rdb.getDocumentBySql(sql);
        String formNumber = "";
        if (Tools.isNotBlank(nodeDoc.g("FormNumber"))) {
            //说明有更换主表单
            formNumber = nodeDoc.g("FormNumber");
        }
        else {
            sql = "select FormNumber from BPM_ModProcessList where Processid='" + processid + "' and Nodeid='Process'";
            formNumber = Rdb.getValueBySql(sql);
        }

        //	    BeanCtx.out("formNumber="+formNumber);
        String docunid = BeanCtx.g("wf_docunid", true); // 表单的unid值
        Document formDoc = ((ModForm) BeanCtx.getBean("ModForm")).getFormDoc(formNumber);
        String fieldConfig = "{\"rows\":[]}";
        if (!formDoc.isNull()) {
            fieldConfig = formDoc.g("FieldConfig");
            int spos = fieldConfig.indexOf("[");
            int epos = fieldConfig.lastIndexOf("]");
            fieldConfig = fieldConfig.substring(spos + 1, epos);
        }
        BeanCtx.print(fieldConfig);

        return "";
    }

}