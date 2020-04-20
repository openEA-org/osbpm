package cn.linkey.rulelib.S003;

import java.util.HashMap;

import cn.linkey.factory.BeanCtx;
import cn.linkey.form.ApprovalForm;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;
import cn.linkey.wf.ProcessEngine;

/**
 * 本规则主要生成流程处理单的底部区域操作条以及隐藏字段
 * 
 * @author Administrator
 *
 */
public class R_S003_B090 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        StringBuilder formBody = new StringBuilder(3000);
        /* FormBody结束div */
        formBody.append("</div><!-- FormBody End-->\n");

        /* 追加流程处理表单 */
        ApprovalForm approvalForm = (ApprovalForm) BeanCtx.getBean("ApprovalFormLayUI");
        formBody.append(approvalForm.getEngineApprovalForm(params));

        /* 增加底部操作条 */
        formBody.append("\n<!-- BottomForm Begin-->\n<div align=\"right\" class=\"ApprovalBottom\" id=\"BottomToolbar\" >");
        String configid = "EngineFormBottomBar";
        if (!BeanCtx.getCountry().equals("CN")) {
            configid = configid.concat("_").concat(BeanCtx.getCountry());
        }

        //读取html头文件，如果应用中配置有则以应用优先2015.4.8
        ProcessEngine linkeywf = BeanCtx.getLinkeywf();
        String configHtml = "";
        if (Tools.isNotBlank(linkeywf.getAppid())) {
            configHtml = BeanCtx.getAppConfig(linkeywf.getAppid(), configid);
        }
        if (Tools.isBlank(configHtml)) {
            configHtml = BeanCtx.getSystemConfig(configid); //如果为空则到系统通用配置中去找
        }
        formBody.append(configHtml);
        //读取结束

        if (linkeywf.isProcessOwner() && !linkeywf.getDocument().g("WF_Status").equals("ARC")) {
            formBody.append(" <a href='' onclick=\"WF_SystemTools();return false;\">流程监控</a>");
        }
        formBody.append("</div>\n<!-- BottomForm End-->");

        /* 增加隐藏字段 */
        formBody.append(getWorkflowHiddenField());
        formBody.append("</form>\n</body></html>");

        formBody.trimToSize();
        return formBody.toString();
    }

    public StringBuilder getWorkflowHiddenField() throws Exception {
        ProcessEngine linkeywf = BeanCtx.getLinkeywf();
        StringBuilder tempStr = new StringBuilder(1200);
        tempStr.append("\n\n<!-- Workflow Field Begin--><div style='display:none'>\n<div id='win'></div>\n");
        tempStr.append("<input name='WF_DocUnid' id='WF_DocUnid' value='" + linkeywf.getDocUnid() + "'>\n");
        tempStr.append("<input name='WF_Taskid' id='WF_Taskid' value='" + BeanCtx.g("wf_taskid", true) + "'><!--任务id可为空-->\n");
        tempStr.append("<input name='WF_Processid' id='WF_Processid' value='" + linkeywf.getProcessid() + "' >\n");
        tempStr.append("<input id='WF_ProcessNumber'  value='" + linkeywf.getProcessNumber() + "' >\n");
        tempStr.append("<input id='WF_UserName' value='" + BeanCtx.getUserid() + "' >\n");
        tempStr.append("<input id='WF_ProcessName' value='" + linkeywf.getProcessName() + "' >\n");
        tempStr.append("<input name='WF_NextNodeid' id='WF_NextNodeid' value=''><!--要提交的目的节点-->\n");
        tempStr.append("<input name='WF_NextUserList' id='WF_NextUserList' value='' ><!--要提交的目的用户-->\n");
        tempStr.append("<input name='WF_SubNextUserList' id='WF_SubNextUserList' value='' ><!--要提交的子流程的目标用户-->\n");
        tempStr.append("<input name='WF_Appid' id='WF_Appid' value='" + linkeywf.getAppid() + "' >\n");
        if (linkeywf.getDocument().g("WF_Status").equals("ARC")) {
            tempStr.append("<input id='WF_CurrentNodeid' value='' ><!-- 当前用户所处节点id,空表示无权-->\n");
            tempStr.append("<input id='WF_CurrentNodeName' value='' >\n");
            tempStr.append("<input id='WF_NewDocFlag' name='WF_NewDocFlag' value='false' ><!-- true表示新文档-->\n");
            tempStr.append("<input id='WF_IsFirstNodeFlag' value='false' ><!-- true表示是首环节-->\n");
        }
        else {
            tempStr.append("<input id='WF_CurrentNodeid' value='" + linkeywf.getCurrentNodeid() + "' ><!-- 当前用户所处节点id,空表示无权-->\n");
            tempStr.append("<input id='WF_CurrentNodeName' value='" + linkeywf.getCurrentNodeName() + "' >\n");
            tempStr.append("<input id='WF_NewDocFlag' name='WF_NewDocFlag' value='" + linkeywf.getIsNewProcess() + "' ><!-- true表示新文档-->\n");
            tempStr.append("<input id='WF_IsFirstNodeFlag' value='" + linkeywf.isFirstNode() + "' ><!-- true表示是首环节-->\n");
        }
        String nodeProperty = "";
        if (Tools.isNotBlank(linkeywf.getCurrentNodeid())) {
            nodeProperty = linkeywf.getCurrentModNodeDoc().g("NodeProperty");
        }
        tempStr.append("<input id='WF_NodeProperty' value='" + nodeProperty + "' ><!-- 在环节中标识的自定义节点参数-->\n");
        tempStr.append("<input id='WF_DocStatus' value='" + linkeywf.getDocument().g("WF_Status") + "' disabled >\n");
        tempStr.append("<input name='WF_Remark' id='WF_Remark' value=''>\n");
        tempStr.append("</div><!-- Workflow Field End-->\n");
        return tempStr;
    }

}
