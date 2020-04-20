package cn.linkey.rulelib.S002;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.doc.Documents;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * @RuleName:流程相关设计打包导出
 * @author admin
 * @version: 8.0
 * @Created: 2015-03-27 11:44
 */
final public class R_S002_B023 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        //params为运行本规则时所传入的参数
        String processid = BeanCtx.g("Processid");
        if (Tools.isBlank(processid)) {
            BeanCtx.p(Tools.jmsg("ok", "Processid 不能为空!"));
            return "";
        }

        LinkedHashSet<Document> dc = new LinkedHashSet<Document>();
        getAllConfigByProcessid(processid, dc);
        BeanCtx.p(Documents.dc2json(dc, "rows"));

        return "";
    }

    /**
     * 根据流程id获得与此流程相关的所有设计元素
     * 
     * @param processid
     * @param dc
     */
    public void getAllConfigByProcessid(String processid, LinkedHashSet<Document> dc) {
        //1.先获得流程模型数据
        String sql = "select NodeName,Nodeid,WF_OrUnid,WF_DocCreated,WF_AddName,FormNumber,FormNumberForMobile,XmlData,WF_LastModified from BPM_ModProcessList where Processid='" + processid + "'";
        Document processDoc = Rdb.getDocumentBySql(sql);
        processDoc.s("ElName", "<b>" + processDoc.g("NodeName") + "</b>");
        processDoc.s("ElNumber", processDoc.g("Nodeid"));
        processDoc.s("ElType", "<b>主流程</b>");
        dc.add(processDoc);

        //2.获得流程的主表单
        getFormDocByFormNumber(processDoc.g("FormNumber"), dc);
        getFormDocByFormNumber(processDoc.g("FormNumberForMobile"), dc);
        getFormDocByFormNumber(processDoc.g("printForm"), dc);

        //1.1获得子流程节点中的动态规则
        sql = "select * from BPM_ModSubProcessList where Processid='" + processid + "'";
        Document[] subDc = Rdb.getAllDocumentsBySql(sql);
        for (Document doc : subDc) {
            String startProcessRuleNum = doc.g("StartProcessRuleNum");//启动规则
            String subRuleNum = doc.g("SubRuleNum"); //数据拷贝规则
            getRuleDocByRuleNum(startProcessRuleNum, dc);
            getRuleDocByRuleNum(subRuleNum, dc);
        }

        //1.2获得路由线中的规则
        sql = "select * from BPM_ModSequenceFlowList where Processid='" + processid + "'";
        subDc = Rdb.getAllDocumentsBySql(sql);
        for (Document doc : subDc) {
            String conditionRuleNum = doc.g("ConditionRuleNum");
            getRuleDocByRuleNum(conditionRuleNum, dc);
        }

        //1.3获得结束事件中的规则
        sql = "select * from BPM_ModEventList where Processid='" + processid + "'";
        subDc = Rdb.getAllDocumentsBySql(sql);
        for (Document doc : subDc) {
            getRuleDocByRuleNum(doc.g("SubRuleNum"), dc); //结束节点中的返回规则
        }

        //1.4获得userTask节点中更换的表单，子表单，处理单等
        sql = "select * from BPM_ModTaskList where Processid='" + processid + "'";
        subDc = Rdb.getAllDocumentsBySql(sql);
        for (Document doc : subDc) {
            getFormDocByFormNumber(doc.g("FormNumber"), dc); //更换的主表单
            getFormDocByFormNumber(doc.g("FormNumberForMobile"), dc); //更换的手机主表单
            getFormDocByFormNumber(doc.g("SubFormNumberLoad"), dc); //子表单
            getFormDocByFormNumber(doc.g("CusApprovalFormNum"), dc); //处理单

            //人员参与者规则
            String potentialOwner = doc.g("PotentialOwner") + "," + doc.g("ApprovalFormOwner");
            //	    	BeanCtx.out("potentialOwner="+potentialOwner);
            String[] userArray = Tools.split(potentialOwner, ",");
            for (String userid : userArray) {
                //	    		BeanCtx.out("userid="+userid);
                if (userid.startsWith("Rule.")) {
                    String OwnerRuleid = userid.substring(userid.indexOf(".") + 1);//这个编号是配置编号不是真实的规则编号
                    String ruleNum = Rdb.getValueBySql("select RuleNum from BPM_EngineNodeOwnerConfig where OwnerRuleid='" + OwnerRuleid + "'");
                    //	    			BeanCtx.out("ruleNum="+ruleNum);
                    getRuleDocByRuleNum(ruleNum, dc);
                }
            }

        }

        //4.获得流程和节点的所有事件
        sql = "select RuleNum from BPM_EngineEventConfig where Processid='" + processid + "'";
        HashSet<String> ruleList = Rdb.getValueSetBySql(sql);
        for (String ruleNum : ruleList) {
            getRuleDocByRuleNum(ruleNum, dc);
        }

    }

    /**
     * 根据表单编号获取规则配置信息
     * 
     * @param formNumber
     * @param dc
     */
    public void getFormDocByFormNumber(String formNumber, LinkedHashSet<Document> dc) {
        if (Tools.isBlank(formNumber)) {
            return;
        }
        String sql = "select WF_OrUnid,FormName,FormNumber,WF_AddName,WF_DocCreated,EventRuleNum,WF_LastModified from BPM_FormList where FormNumber='" + formNumber + "'";
        Document formDoc = Rdb.getDocumentBySql(sql);
        if (!formDoc.isNull()) {
            formDoc.s("ElName", formDoc.g("FormName"));
            formDoc.s("ElNumber", formDoc.g("FormNumber"));
            formDoc.s("ElType", "关联表单");
            dc.add(formDoc);
            getRuleDocByRuleNum(formDoc.g("EventRuleNum"), dc); //表单事件
        }
    }

    /**
     * 根据规则编号获取规则配置信息
     * 
     * @param ruleNum
     */
    public void getRuleDocByRuleNum(String ruleNum, LinkedHashSet<Document> dc) {
        if (Tools.isBlank(ruleNum)) {
            return;
        }

        String sql = "select WF_OrUnid,RuleName,WF_AddName,WF_DocCreated,RuleNum,WF_LastModified from BPM_RuleList where RuleNum='" + ruleNum + "'";
        Document ruleDoc = Rdb.getDocumentBySql(sql);
        if (!ruleDoc.isNull()) {
            ruleDoc.s("ElName", ruleDoc.g("RuleName"));
            ruleDoc.s("ElNumber", ruleDoc.g("RuleNum"));
            ruleDoc.s("ElType", "关联规则");
            dc.add(ruleDoc);
        }
    }

}