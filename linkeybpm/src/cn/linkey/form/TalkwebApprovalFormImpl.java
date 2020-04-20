package cn.linkey.form;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.util.Tools;
import cn.linkey.wf.ModNode;
import cn.linkey.wf.ProcessEngine;

/**
 * 流程处理单生成类 本类为单例类
 * 
 * 如果要修改本类来适用客户需求可以另建一个类，然后修改javabean的配置到修改后的类中即可
 * 
 * @author Administrator
 */
public class TalkwebApprovalFormImpl implements ApprovalForm {

    /*
     * (non-Javadoc) 生成流程处理单的方法
     */
    @Override
    public String getEngineApprovalForm(HashMap<String, Object> nodeParams) throws Exception {
        ProcessEngine linkeywf = BeanCtx.getLinkeywf();
        //如果是只读直接返回空
        if (linkeywf.isReadOnly()) {
            return "";
        }
        //返回被锁定的消息提示
        if (Tools.isNotBlank(linkeywf.getLockStatus())) {
            return BeanCtx.getMsg("Engine", "DocumentLockedMsg", linkeywf.getLockStatus());
        }

        //开始生成流程处理单
        String processid = linkeywf.getProcessid();
        String curNodeid = linkeywf.getCurrentNodeid();
        String approvalHtml = autoCreateEngineApprovalForm(processid, curNodeid); //自动生成流程处理单

        return approvalHtml;
    }

    /*
     * 不用实现此方法
     */
    @Override
    public String getWsApprovalForm(HashMap<String, Object> nodeParams) throws Exception {
        return "";
    }

    /**
     * 自动生成流程处理单的HTML代码
     * 
     * @param processid
     * @param curNodeid
     * @return 返回HTML字符串
     */
    private String autoCreateEngineApprovalForm(String processid, String curNodeid) throws Exception {
        ProcessEngine linkeywf = BeanCtx.getLinkeywf();
        StringBuilder formHtml = new StringBuilder();

        //1.生成填写意见的框
        getRemarkHtml(formHtml);

        //2生成人员选择界面
        formHtml.append("\n<br><table  border=0 class='linkeytable' ><tr><td colspan=5>流程处理</td></tr><tr>");
        int canNextNodeFlag = linkeywf.canSelectNodeAndUser(); //获得是否可以选择后继环节返回0表示可以
        if (canNextNodeFlag == 0) { //看是可以显示节点和人员选项
            //2.开始生成节点和人员选项
            StringBuilder nodeListStr = new StringBuilder();
            //节点选项的html
            nodeListStr.append("<td width='30%'>处理步骤<br><select id='WF_SelNextNodeList' >");
            getNodeSelectHtml(processid, curNodeid, nodeListStr); //调用人员和节点选择的HTML生成函数
            formHtml.append(nodeListStr).append("</select></td>");
        }

        //7.用户选择框
        formHtml.append("<td width='25%' >选择用户</td>");
        formHtml.append("<td width='5%' > 添加 <br> 删除 <br> 上移 <br>下移 </td>");
        formHtml.append("<td  width='25%' >用户列表</td>");
        formHtml.append("<td width='15%'>");
        formHtml.append(getToolbar(canNextNodeFlag));
        formHtml.append("</td></tr></table>");

        return formHtml.toString();
    }

    /**
     * 查找当前用户所处的节点并计算此节点后面的所有成立的路由线找到目的Task或endEvent节点，生成节点和人员的选项
     * 
     * @param processid 流程id
     * @param nodeid 当前用户所处节点id
     * @throws Exception
     */
    private void getNodeSelectHtml(String processid, String nodeid, StringBuilder nodeListStr) throws Exception {
        ModNode insModNode = (ModNode) BeanCtx.getBean("ModNode");

        //获得此节点出去的所有路由线段的配置信息
        String sql = "select Nodeid,TargetNode from BPM_ModSequenceFlowList where Processid='" + processid + "' and SourceNode='" + nodeid + "'";
        HashMap<String, String> sequenceFlowMap = Rdb.getMapDataBySql(sql);
        for (String sequenceFlowNodeid : sequenceFlowMap.keySet()) {
            //循环所有路由线生成节点和人员选项
            Document routerNodeDoc = insModNode.getNodeDoc(processid, sequenceFlowNodeid); //路由线文档对像
            if (insModNode.checkSequenceFlow(routerNodeDoc)) {//如果路由条件成立
                String targetNodeid = sequenceFlowMap.get(sequenceFlowNodeid); //找到路由的目标节点
                Document nextNodeDoc = insModNode.getNodeDoc(processid, targetNodeid);//目标节点文档对像
                String nodeType = nextNodeDoc.g("NodeType");//基本属性
                String nextExtNodeType = nextNodeDoc.g("ExtNodeType"); //扩展属性
                if (nodeType.equals("Task") || nextExtNodeType.equals("endEvent") || nodeType.equals("SubProcess")) {

                    //生成节点选项 1表示为始终网关，2表示唯一网关，3表示并行网关,4复杂条件网关
                    String gatewayType = insModNode.getGatewayType(processid, BeanCtx.getLinkeywf().getCurrentNodeid(), targetNodeid, "0"); //获得当前节点与目标节点之间网关节点的类型，如果没有就以最后的路由线中的为准
                    if (gatewayType.equals("0")) {
                        gatewayType = routerNodeDoc.g("GatewayType");
                    } //说明两个节点之间没有网关节点以最后路由线中的类型为准
                    String nodeName = nextNodeDoc.g("NodeName"); //节点名称
                    String routerNodeName = routerNodeDoc.g("NodeName");//由路由替换的节点名称
                    if (Tools.isBlank(routerNodeName)) {
                        routerNodeName = nodeName;
                    }
                    String defaultSelected = routerNodeDoc.g("DefaultSelected"); //是否缺省选中
                    String checked = defaultSelected.equals("1") ? "selected" : ""; //是否默认选中

                    nodeListStr.append("<option onclick=\"ShowRouterUser()\" ");
                    nodeListStr.append(checked);
                    nodeListStr.append(" value=\"").append(targetNodeid);
                    nodeListStr.append("#" + gatewayType + "\" >" + routerNodeName + "</option>");

                }
                else {
                    //如果不是Task或事件则再往后查
                    getNodeSelectHtml(processid, targetNodeid, nodeListStr);
                }
            }
        }
    }

    /**
     * 获得填写意见框
     * 
     * @param formHtml
     */
    private void getRemarkHtml(StringBuilder formHtml) {
        Document curModNodeDoc = BeanCtx.getLinkeywf().getCurrentModNodeDoc();
        if (curModNodeDoc.g("RemarkNullFlag").equals("3"))
            return; //禁止填写办理意见

        //显示意见填写框
        String isRemarkNull = curModNodeDoc.g("RemarkNullFlag");
        String sql = "select WF_MyRemark from BPM_UserProfile where Userid='" + BeanCtx.getUserid() + "'";
        String commonRemarkList = Rdb.getValueBySql(sql) + "\n" + BeanCtx.getSystemConfig("CommonRemarkList");
        String[] remarkSet = Tools.split(commonRemarkList, "\n");
        StringBuilder optionStr = new StringBuilder();
        for (String remark : remarkSet) {
            remark = remark.replace("\r", "");
            optionStr.append("<option value='" + remark + "'>" + remark + "</option>");
        }
        formHtml.append("\n<table class='linkeytable'><tr><td colspan=4>审批意见</td></tr>");
        formHtml.append("\n<tr><td>同意,不同意,等</td>");
        formHtml.append("\n<td>");
        formHtml.append("<select id=\"WF_SelectRemark\" onchange=\"SelectRemark(this)\"><option>" + BeanCtx.getMsg("Engine", "ApprovalForm_SelectCommonRemark") + "</option>");
        formHtml.append(optionStr);
        formHtml.append("</select><br><input type='button' value='常用意见'></td>");

        //获得标准初始意见或者是暂存的办理意见
        String tempRemark = Rdb.getValueBySql("select Remark from BPM_TempRemarkList where Userid='" + BeanCtx.getUserid() + "' and DocUnid='" + BeanCtx.getLinkeywf().getDocUnid() + "'");
        if (Tools.isBlank(tempRemark)) {
            tempRemark = curModNodeDoc.g("DefaultRemark");
        }
        formHtml.append("\n<td>");
        formHtml.append("<textarea name=\"WF_TmpRemark\" id=\"WF_TmpRemark\" IsNull=\"" + isRemarkNull + "\" style=\"width:460px;height:65px;overflow:auto;\">" + tempRemark + "</textarea>");
        formHtml.append("<br>");
        formHtml.append(
                "<img src='linkey/bpm/images/icons/vwicn202.gif' onclick='AddToMyRemark();' style=\"cursor:pointer;\" title='" + BeanCtx.getMsg("Engine", "ApprovalForm_AddRemarkTitle") + "'>");
        formHtml.append("\n</td><td><input type='button' value='意见暂存'></td></tr></table>");
    }

    /**
     * 根据环节操作按扭选项自动生成操作按扭条
     * 
     * @param canNextNodeFlag根据不同的参数显示不同的按扭可从linkeywf中自动获取此参数
     */
    private String getToolbar(int canNextNodeFlag) throws Exception {
        String buttonHtml = "";
        if (canNextNodeFlag == 1) {
            //提交下一串行用户
        }
        else if (canNextNodeFlag == 2) {
            //返回给转交者按扭
        }
        else if (canNextNodeFlag == 3) {
            //返回给回退者按扭
        }
        else if (canNextNodeFlag == 4) {
            //提交下一会签用户
            buttonHtml = "<input type='button' value='提交会签' onclick=\"GoToNextParallelUser('Y')\" >";
        }
        else if (canNextNodeFlag == 5) {
            //提交下一会签用户，显示同意和不同意两个按扭
        }
        else { //提交后继环节
            buttonHtml = "<input type='button' value='流程处理' onclick=\"GoToNextNode();\" >";
        }
        return buttonHtml;
    }

}
