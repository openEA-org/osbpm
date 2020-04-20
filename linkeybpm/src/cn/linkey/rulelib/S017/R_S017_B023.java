package cn.linkey.rulelib.S017;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.form.ApprovalForm;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;
import cn.linkey.wf.ProcessEngine;

/**
 * @RuleName:Engine_打开流程和处理单
 * @author admin
 * @version: 8.0
 * @Created: 2014-07-10 21:52
 */
final public class R_S017_B023 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        //params为运行本规则时所传入的参数
        //示例参数:{"Processid":"a8bb004c00173041060b5d502bada1435268","DocUnid":"351332d6027260453b088960719e75351c52","DocXml":""}
        String processid = (String) params.get("Processid");
        String docUnid = (String) params.get("DocUnid");
        String docXml = (String) params.get("DocXml");

        Document returnDoc = BeanCtx.getDocumentBean("");

        //0.看是否传入了流程id
        if (Tools.isBlank(processid)) {
            returnDoc.s("WF_SucessFlag", "0");
            returnDoc.s("WF_Msg", "Error:processid is null!");
            return returnDoc.toJson();
        }

        StringBuilder jsonStr = new StringBuilder();

        //初始化引擎
        ProcessEngine linkeywf = BeanCtx.getDefaultEngine();
        if (Tools.isBlank(docUnid)) {
            docUnid = Rdb.getNewUnid();
        } //如果没有传入文档unid则说明要启动一个新文档
        linkeywf.init(processid, docUnid, BeanCtx.getUserid(), ""); //初始化工作流引擎
        linkeywf.getDocument().appendFromXml(docXml); //把传入的xml字符串附加到主文档中去

        //输出当前文档的所有字段内容
        linkeywf.getDocument().copyAllItems(returnDoc);
        returnDoc.s("WF_SucessFlag", "1");
        returnDoc.s("WF_IsProcessAdmin", String.valueOf(linkeywf.isProcessOwner()));
        returnDoc.s("WF_LockStatus", linkeywf.getLockStatus());
        returnDoc.s("WF_CurrentNodeid", linkeywf.getCurrentNodeid());

        //输出当前节点组合好的处理单HTML代码
        if (Tools.isNotBlank(linkeywf.getCurrentNodeid())) {
            HashMap<String, Object> nodeParams = new HashMap<String, Object>();
            ApprovalForm approvalForm = (ApprovalForm) BeanCtx.getBean("ApprovalForm");
            returnDoc.s("ApprovalForm", approvalForm.getWsApprovalForm(nodeParams));
        }

        //转文档为json格式输出
        int i = 0;
        HashMap<String, String> allFieldItem = returnDoc.getAllItems();
        for (String fdName : allFieldItem.keySet()) {
            String fdValue = Tools.encodeJson(returnDoc.g(fdName)); //进行json编码
            if (i == 0) {
                jsonStr.append("{\"" + fdName + "\":\"" + fdValue + "\"");
                i = 1;
            }
            else {
                jsonStr.append(",\"" + fdName + "\":\"" + fdValue + "\"");
            }
        }

        //增加输出当前环节的配置信息
        if (linkeywf.getCurrentModNodeDoc() != null) {
            //自动获取Actionid
            int canNextNodeFlag = linkeywf.canSelectNodeAndUser(); //获得是否可以选择后继环节返回0表示可以
            String actionid = "";
            if (canNextNodeFlag == 0) {
                actionid = "EndUserTask";
            }
            else if (canNextNodeFlag == 1) {
                actionid = "GoToNextSerialUser";
            }
            else if (canNextNodeFlag == 2) {
                actionid = "BackToDeliver";
            }
            else if (canNextNodeFlag == 3) {
                actionid = "BackToReturnUser";
            }
            else if (canNextNodeFlag == 4) {
                actionid = "GoToNextParallelUser";
            }
            linkeywf.getCurrentModNodeDoc().s("WF_Actionid", actionid);//输出Actionid参数，调用WF_RunProcess服务时有用
            jsonStr.append(",\"WF_CurrentNodeConfig\":" + linkeywf.getCurrentModNodeDoc().toJson() + "}");
        }
        else {
            jsonStr.append(",\"WF_CurrentNodeConfig\":\"\"}");
        }

        return jsonStr.toString();
    }
}