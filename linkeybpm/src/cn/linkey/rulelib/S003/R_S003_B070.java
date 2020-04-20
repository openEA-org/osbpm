package cn.linkey.rulelib.S003;

import java.util.HashMap;

import cn.linkey.factory.BeanCtx;
import cn.linkey.form.ApprovalForm;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;
import cn.linkey.wf.ProcessEngine;

/**
 * Mobile 本规则主要生成流程处理单的底部区域操作条以及隐藏字段
 * 
 * @author Administrator 移动终端专用
 */
public class R_S003_B070 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        StringBuilder formBody = new StringBuilder(3000);

        /* FormBody结束div */
        formBody.append("</div><!-- FormBody End-->\n");

        /* 追加流程处理表单 */
        ApprovalForm approvalForm = (ApprovalForm) BeanCtx.getBean("ApprovalFormMobile"); //获取移动端的生成类
        formBody.append(approvalForm.getEngineApprovalForm(params));

        /* 增加隐藏字段 */
        formBody.append(getWorkflowHiddenField());
        formBody.append("<br><br></form>\n</body></html>");

        formBody.trimToSize();
        return formBody.toString();
    }

    public StringBuilder getWorkflowHiddenField() throws Exception {
        ProcessEngine linkeywf = BeanCtx.getLinkeywf();
        StringBuilder tempStr = new StringBuilder(1200);
        tempStr.append("\n\n<!-- Workflow Field Begin--><div style='display:none'>\n<div id='win'></div>\n");
        tempStr.append("<input name='WF_DocUnid' id='WF_DocUnid' value='" + linkeywf.getDocUnid() + "'>\n");
        tempStr.append("<input name='WF_Processid' id='WF_Processid' value='" + linkeywf.getProcessid() + "' >\n");
        tempStr.append("<input id='WF_ProcessNumber'  value='" + linkeywf.getProcessNumber() + "' >\n");
        tempStr.append("<input id='WF_UserName' value='" + BeanCtx.getUserid() + "' >\n");
        tempStr.append("<input id='WF_ProcessName' value='" + linkeywf.getProcessName() + "' >\n");
        tempStr.append("<input id='WF_CurrentNodeid' value='" + linkeywf.getCurrentNodeid() + "' ><!-- 当前用户所处节点id,空表示无权-->\n");
        tempStr.append("<input id='WF_CurrentNodeName' value='" + linkeywf.getCurrentNodeName() + "' >\n");
        tempStr.append("<input name='WF_NextNodeid' id='WF_NextNodeid' value=''><!--要提交的目的节点-->\n");
        tempStr.append("<input name='WF_NextUserList' id='WF_NextUserList' value='' ><!--要提交的目的用户-->\n");
        tempStr.append("<input name='WF_Appid' id='WF_Appid' value='" + linkeywf.getAppid() + "' >\n");
        tempStr.append("<input id='WF_NewDocFlag' name='WF_NewDocFlag' value='" + linkeywf.getIsNewProcess() + "' ><!-- true表示新文档-->\n");
        tempStr.append("<input id='WF_IsFirstNodeFlag' value='" + linkeywf.isFirstNode() + "' ><!-- true表示是首环节-->\n");
        String nodeProperty = "";
        if (Tools.isNotBlank(linkeywf.getCurrentNodeid())) {
            nodeProperty = linkeywf.getCurrentModNodeDoc().g("NodeProperty");
        }
        tempStr.append("<input id='WF_NodeProperty' value='" + nodeProperty + "' ><!-- 在环节中标识的自定义节点参数-->\n");
        tempStr.append("<input id='WF_DocStatus' value='" + linkeywf.getDocument().g("WF_Status") + "' disabled >\n");
        tempStr.append("<input name='WF_Remark' id='WF_Remark' value=''>\n");
        tempStr.append("</div><!-- Workflow Field End-->\n");
        //BeanCtx.p("tempStr="+tempStr.length());
        return tempStr;
    }

}
