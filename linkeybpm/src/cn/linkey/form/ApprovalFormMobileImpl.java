package cn.linkey.form;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import cn.linkey.app.AppElement;
import cn.linkey.dao.*;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.org.LinkeyUser;
import cn.linkey.util.Tools;
import cn.linkey.wf.*;

/**
 * 流程处理单生成类 本类为单例类
 * 
 * 如果要修改本类来适用客户需求可以另建一个类，然后修改javabean的配置到修改后的类中即可
 * 
 * @author Administrator
 */
public class ApprovalFormMobileImpl implements ApprovalForm {
	
	private static final String UIType = "3"; //20181010 默认写死EasyUI

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
        String ApprovalAutoFlag = linkeywf.getCurrentModNodeDoc().g("ApprovalAutoFlag"); //是否自动生成处理单标记
        String approvalHtml = "";
        if (ApprovalAutoFlag.equals("1")) {
            approvalHtml = autoCreateEngineApprovalForm(processid, curNodeid); //自动生成流程处理单
        }
        else if (ApprovalAutoFlag.equals("2")) {
            approvalHtml = getCustomSubForm(processid, curNodeid); //使用指定的子表单
        }
        return approvalHtml;
    }

    @Override
    public String getWsApprovalForm(HashMap<String, Object> nodeParams) throws Exception {
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
        String ApprovalAutoFlag = linkeywf.getCurrentModNodeDoc().g("ApprovalAutoFlag"); //是否自动生成处理单标记
        String approvalHtml = "";
        if (ApprovalAutoFlag.equals("1")) {
            approvalHtml = autoCreateWsApprovalForm(processid, curNodeid); //自动生成流程处理单
        }
        else if (ApprovalAutoFlag.equals("2")) {
            approvalHtml = getCustomSubForm(processid, curNodeid); //使用指定的子表单
        }
        return approvalHtml;
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
        Document curModNodeDoc = BeanCtx.getLinkeywf().getCurrentModNodeDoc();
        StringBuilder formHtml = new StringBuilder();

        //1.生成处理单的信息头
        String styleStr = BeanCtx.getSystemConfig("HiddenApprovalForm").equals("1") ? "display:none" : ""; //在通用配置隐藏显示处理单
        String ApprovalMsg = linkeywf.getCurrentModNodeDoc().g("ApprovalMsg");
        if (Tools.isBlank(ApprovalMsg)) {
            ApprovalMsg = BeanCtx.getMsg("Engine", "ApprovalForm_TitleInfo", curModNodeDoc.g("NodeName"));
        }
        else if (ApprovalMsg.equals("NO")) {
            ApprovalMsg = "";
        }
        formHtml.append("\n<!-- ApprovalForm Begin-->\n<div id=\"ApprovalForm\" class=\"ApprovalForm\" " + styleStr + " >");
        formHtml.append("\n<div id=\"ApprovalMsg\" class=\"ApprovalMsg\"  >" + ApprovalMsg + "</div>");
        formHtml.append("\n<table width='99%' border=0 class='Approvaltable' id='ApprovalTable' >");

        int canNextNodeFlag = linkeywf.canSelectNodeAndUser(); //获得是否可以选择后继环节返回0表示可以
        //看是可以显示节点和人员选项
        if (canNextNodeFlag == 0) {
            //2.开始生成节点和人员选项
            StringBuilder nodeListStr = new StringBuilder();
            //节点选项的html
            StringBuilder userListStr = new StringBuilder(); //人员选项的html
            getNodeAndUserSelectHtml(processid, curNodeid, nodeListStr, userListStr); //调用人员和节点选择的HTML生成函数
            if (nodeListStr.length() > 0) {
                nodeListStr.append("</td></tr>");
            }
            formHtml.append(nodeListStr);
            formHtml.append(userListStr);

            //3.生成转他人处理选项
            getReassignmentHtml(formHtml);

            //4.生成传阅选项
            getCopyToHtml(formHtml);
        }

        //5.生成手机短信提示选项
        if (curModNodeDoc.g("CanSendSmsFlag").equals("1")) {
            String checkedStr = curModNodeDoc.g("SendSmsDefaultChecked").equals("1") ? "checked" : "";
            formHtml.append("\n<tr><td class='texttd' >" + BeanCtx.getMsg("Engine", "ApprovalForm_SendSmsInfo") + "</td>");
            formHtml.append("<td><input type=\"checkbox\" " + checkedStr + "  value=\"1\" id=\"WF_SelSendSms\" class=\"lschk\" >" + BeanCtx.getMsg("Common", "yes") + "</td></tr>");
        }
        //20180427 添加手写切换
        formHtml.append("\n<tr id='SwitchHand' ><td class='texttd' width='15%' >手写签名</td><td><input id=\"switchBox\" type=\"checkbox\" onchange=\"switchHand()\"></td></tr>");

        //6.生成填写意见的框
        getRemarkHtml(formHtml);

        //7.生成提交按扭
        formHtml.append("\n<tr id='ToolbarTr' ><td class='texttd' width='15%' >&nbsp;</td><td>");
        formHtml.append(getToolbar(canNextNodeFlag));
        formHtml.append("</td></tr>");

        //8.生成处理单的尾部HTML
        formHtml.append("\n</table>\n</div><!-- ApprovalForm End-->\n");

        return formHtml.toString();
    }

    /**
     * 自动生成处理单,WebService专用
     * 
     * @param processid
     * @param curNodeid
     * @return 返回HTML字符串
     */
    private String autoCreateWsApprovalForm(String processid, String curNodeid) throws Exception {
        ProcessEngine linkeywf = BeanCtx.getLinkeywf();
        Document curModNodeDoc = BeanCtx.getLinkeywf().getCurrentModNodeDoc();
        StringBuilder formHtml = new StringBuilder();

        //1.生成处理单的信息头
        formHtml.append("\n<table width='99%' border=0 class='Approvaltable' id='ApprovalTable' >");

        int canNextNodeFlag = linkeywf.canSelectNodeAndUser(); //获得是否可以选择后继环节返回0表示可以
        //看是可以显示节点和人员选项
        if (canNextNodeFlag == 0) {
            //2.开始生成节点和人员选项
            StringBuilder nodeListStr = new StringBuilder();
            //节点选项的html
            StringBuilder userListStr = new StringBuilder(); //人员选项的html
            getNodeAndUserSelectHtml(processid, curNodeid, nodeListStr, userListStr); //调用人员和节点选择的HTML生成函数
            if (nodeListStr.length() > 0) {
                nodeListStr.append("</td></tr>");
            }
            formHtml.append(nodeListStr);
            formHtml.append(userListStr);

            //3.生成转他人处理选项
            getReassignmentHtml(formHtml);

            //4.生成传阅选项
            getCopyToHtml(formHtml);

        }

        //5.生成手机短信提示选项
        if (curModNodeDoc.g("CanSendSmsFlag").equals("1")) {
            String checkedStr = curModNodeDoc.g("SendSmsDefaultChecked").equals("1") ? "checked" : "";
            formHtml.append("\n<tr><td class='texttd' >" + BeanCtx.getMsg("Engine", "ApprovalForm_SendSmsInfo") + "</td><td><input type=\"checkbox\" " + checkedStr
                    + " value=\"1\" id=\"WF_SelSendSms\" class=\"lschk\" >" + BeanCtx.getMsg("Common", "yes") + "</td></tr>");
        }

        //6.生成填写意见的框
        getRemarkHtml(formHtml);

        //7.生成提交按扭
        formHtml.append("\n<tr id='ToolbarTr' ><td class='texttd' width='15%' >&nbsp;</td><td>");
        formHtml.append(getToolbar(canNextNodeFlag));
        formHtml.append("</td></tr>");

        //8.生成处理单的尾部HTML
        formHtml.append("\n</table>");

        return formHtml.toString();
    }

    /**
     * 查找当前用户所处的节点并计算此节点后面的所有成立的路由线找到目的Task或endEvent节点，生成节点和人员的选项
     * 
     * @param processid 流程id
     * @param nodeid 当前用户所处节点id
     * @param nodeListStr 节点选择字符串对像
     * @param userListStr 用户选择字符串对像
     * @throws Exception
     */
    private void getNodeAndUserSelectHtml(String processid, String nodeid, StringBuilder nodeListStr, StringBuilder userListStr) throws Exception {
        ModNode insModNode = (ModNode) BeanCtx.getBean("ModNode");
        LinkeyUser linkeyUser = (LinkeyUser) BeanCtx.getBean("LinkeyUser");
        NodeUser insNodeUser = (NodeUser) BeanCtx.getBean("NodeUser");
        String docUnid = BeanCtx.getLinkeywf().getDocUnid();

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
                    if (nodeListStr.length() == 0) {
                        nodeListStr.append("\n<tr id=\"UserTr_Node0\" ><td width=\"15%\" class=\"texttd\" nowrap >" + BeanCtx.getMsg("Engine", "ApprovalForm_NodeInfo_Mobile") + "</td><td>");
                    }

                    //生成节点选项 1表示为始终网关，2表示唯一网关，3表示并行网关,4复杂条件网关
                    String gatewayType = insModNode.getGatewayType(processid, BeanCtx.getLinkeywf().getCurrentNodeid(), targetNodeid, "0"); //获得当前节点与目标节点之间网关节点的类型，如果没有就以最后的路由线中的为准
                    //说明两个节点之间没有网关节点以最后路由线中的类型为准
                    if (gatewayType.equals("0")) {
                        gatewayType = routerNodeDoc.g("GatewayType");
                    }
                    String nodeName = nextNodeDoc.g("NodeName"); //节点名称
                    String routerNodeName = routerNodeDoc.g("NodeName");//由路由替换的节点名称
                    if (Tools.isBlank(routerNodeName)) {
                        routerNodeName = nodeName;
                    }
                    String defaultSelected = routerNodeDoc.g("DefaultSelected"); //是否缺省选中

                    String inputType = gatewayType.equals("2") ? "radio" : "checkbox"; //只有唯一网关是radio模式
                    String disabledStr = (gatewayType.equals("1") || gatewayType.equals("4")) ? "disabled" : ""; //始终和复杂条件网关时不可选
                    String checked = (disabledStr.equals("disabled") || defaultSelected.equals("1")) ? "checked" : ""; //是否默认选中

                    nodeListStr.append("<input type=\"" + inputType + "\" name=\"WF_NextNodeSelect\" id=\"WF_NextNodeSelect_" + targetNodeid + "\"  onclick=\"ShowRouterUser(this)\" " + disabledStr
                            + " " + checked + " value=\"" + targetNodeid + "\" ><label for=\"WF_NextNodeSelect_" + targetNodeid + "\">" + routerNodeName + "</label>");

                    //生成人员选项
                    if (nextExtNodeType.equals("userTask") && nextNodeDoc.g("UsePostOwner").equals("1")) {
                        //如果目的节点是userTask类型且环节中选择是是接受提交的用户作为参与者时则生成节点选项和人员选项

                        //看路由线是否是回退线，如果是回退线则把流程实例用户作为选择用户
                        LinkedHashSet<String> nodeOwnerSet = new LinkedHashSet<String>();
                        if (routerNodeDoc.g("ReturnFlag").equals("1")) {
                            if (routerNodeDoc.g("BackToPrvUser").equals("1")) {
                                //直接回退给上一提交者
                                String sourceOrUnid = BeanCtx.getLinkeywf().getCurrentInsUserDoc().g("SourceOrUnid");
                                sql = "select Userid from BPM_InsUserList where WF_OrUnid='" + sourceOrUnid + "'";
                                String prvUserid = Rdb.getValueBySql(sql);
                                nodeOwnerSet.add(prvUserid);
                            }
                            else {
                                nodeOwnerSet = insNodeUser.getInsNodeUser(docUnid, targetNodeid); //获得已存在的实例用户作为参与者
                            }
                        }
                        else {
                            nodeOwnerSet = Tools.splitAsLinkedSet(nextNodeDoc.g("ApprovalFormOwner"), ","); //使用节点中配置的人员选择范围
                        }

                        //获得人员列表的HTML
                        LinkedHashSet<String> ownerSet = BeanCtx.getLinkeyUser().parserNodeMembers(nodeOwnerSet);
                        // String userSelected=(ownerSet.size()==1 || nextNodeDoc.g("OwnerSelectType").equals("2") || nextNodeDoc.g("OwnerSelectType").equals("3")) ? "selected" : ""; //是否默认选中
                        // for(String userid:ownerSet){
                        //     userOptionStr.append("<option value=\""+userid+"\" "+userSelected+" >"+linkeyUser.getCnNameAndDeptName(userid, false)+"</option>");
                        // }

                        //获得人员选择框的自定义属性
                        disabledStr = nextNodeDoc.g("OwnerSelectType").equals("2") ? "disabled" : ""; //是否全部参与,如果全部参与则不允许用户选择
                        String ownerMaxUserNum = Tools.isBlank(nextNodeDoc.g("OwnerMaxUserNum")) ? "0" : nextNodeDoc.g("OwnerMaxUserNum"); //最大用户参与人数
                        String ownerMinUserNum = Tools.isBlank(nextNodeDoc.g("OwnerMinUserNum")) ? "0" : nextNodeDoc.g("OwnerMinUserNum"); //最小用户参与人数
                        //是否允许任意选择他人
                        String selectUserBtn = nextNodeDoc.g("OwnerSelectFlag").equals("1")
                                ? "<a class=\"easyui-linkbutton\" plain=\"true\" data-options=\"iconCls:'icon-userbtn'\" onclick=\"seluser('WF_" + targetNodeid + "');return false;\" >添加</a>" : "";
                        String ownerLimitTimeStr = nextNodeDoc.g("OwnerLimitTimeFlag").equals("1") ? BeanCtx.getMsg("Engine", "ApprovalForm_OwnerLimitTime") + "<input name=\"WF_LimitTime_"
                                + targetNodeid + "\"  class='easyui-datetimebox' data-options='formatter:formatterDateTime' Nodeid='" + targetNodeid + "' value=''><br>" : ""; //允许上一环节指定完成时间
                        String ownerSelectInfo = Tools.isBlank(nextNodeDoc.g("OwnerSelectInfo")) ? nodeName : nextNodeDoc.g("OwnerSelectInfo"); //人员选择提示信息

                        //生成人员选择框的HTML代码
                        userListStr.append("\n<tr id=\"UserTr_" + targetNodeid + "\" style=\"display:none\"><td class=\"texttd\" >");
                        userListStr.append(ownerSelectInfo);
                        userListStr.append("</td><td>" + ownerLimitTimeStr);
                        userListStr.append("<input " + disabledStr + " name=\"WF_" + targetNodeid + "\" id=\"WF_" + targetNodeid + "\" style=\"display:none\" NodeName=\"" + nodeName
                                + "\" MaxUserNum=\"" + ownerMaxUserNum + "\" MinUserNum=\"" + ownerMinUserNum + "\" value=\"" + Tools.join(ownerSet, ",") + "\"  >");
                        userListStr.append("<span id='WF_" + targetNodeid + "_show' >");
                        for (String userid : ownerSet) {
                            String userName = linkeyUser.getCnName(userid);
                            if (Tools.isBlank(userName)) {
                                userName = userid;
                            }
                            userListStr.append("<a  id=\"U_" + targetNodeid + "_" + userid + "\" onclick=\"MobileDeleteNodeUser('" + targetNodeid + "','" + userid
                                    + "');return false;\" href='' class=\"fieldShow\" ><img src='linkey/bpm/images/icons/vwicn203.gif'>" + userName + "</a> ");
                        }
                        userListStr.append("</span>");
                        userListStr.append(selectUserBtn + "</td></tr>");

                    }

                }
                else {
                    //如果不是Task或事件则再往后查
                    getNodeAndUserSelectHtml(processid, targetNodeid, nodeListStr, userListStr);
                }
            }
        }
    }

    /**
     * 生成转他人处理的HTML代码
     * 
     * @return
     */
    private void getReassignmentHtml(StringBuilder formHtml) throws Exception {
        ModNode insModNode = (ModNode) BeanCtx.getBean("ModNode");
        LinkeyUser linkeyUser = (LinkeyUser) BeanCtx.getBean("LinkeyUser");
        if (!insModNode.getReassignmentFlag()) {
            return;
        } //不允许转交直接返回

        //可以转他人处理
        String userBtn = BeanCtx.getLinkeywf().getCurrentModNodeDoc().g("NoReassSelUserFlag").equals("1") ? ""
                : "<a class=\"easyui-linkbutton\" plain=\"true\" data-options=\"iconCls:'icon-userbtn'\" onclick=\"seluser('WF_OtherUserList');return false;\" >添加</a>"; //人员选择按扭
        String backOption = " <input name=\"WF_SendToOtherUserAndBack\" id=\"WF_SendToOtherUserAndBack\" type=\"checkbox\" Class=\"lschk\">"
                + BeanCtx.getMsg("Engine", "ApprovalForm_ReassignmentBackOption");
        HashSet<String> ownerSet = ((LinkeyUser) BeanCtx.getBean("LinkeyUser")).parserNodeMembers(Tools.splitAsLinkedSet(BeanCtx.getLinkeywf().getCurrentModNodeDoc().g("ReassignmentOwner"), ","));
        formHtml.append("\n<tr id=\"TrOtherUser\"><td class=\"texttd\" nowrap >");
        formHtml.append(BeanCtx.getMsg("Engine", "ApprovalForm_OtherUserInfo"));
        formHtml.append(
                "</td><td><input type=\"checkbox\" name=\"WF_SendToOtherUser\" id=\"WF_SendToOtherUser\" class=\"lschk\" onclick=\"ShowSendToOtherUser(this);\"><label for=\"WF_SendToOtherUser\">")
                .append(BeanCtx.getMsg("Common", "yes")).append("</label>");
        formHtml.append("<br><span id='SpanSendToOtherUser' style=\"display:none\"><input name=\"WF_OtherUserList\" id=\"WF_OtherUserList\" multiple=\"multiple\" style=\"display:none\" value=\""
                + Tools.join(ownerSet, ",") + "\" >");
        formHtml.append("<span id='WF_OtherUserList_show' >");
        for (String userid : ownerSet) {
            String userName = linkeyUser.getCnName(userid);
            if (Tools.isBlank(userName)) {
                userName = userid;
            }
            formHtml.append("<a  id=\"U_OtherUserList_").append(userid).append("\" onclick=\"MobileDeleteNodeUser('OtherUserList','").append(userid)
                    .append("');return false;\" href='' class=\"fieldShow\" ><img src='linkey/bpm/images/icons/vwicn203.gif'>").append(userName).append("</a> ");
        }
        formHtml.append("</span>");
        formHtml.append(userBtn).append(backOption).append("</span></td></tr>");
    }

    /**
     * 生成传阅的html代码
     * 
     * @return
     */
    private void getCopyToHtml(StringBuilder formHtml) throws Exception {
        Document curModNodeDoc = BeanCtx.getLinkeywf().getCurrentModNodeDoc();
        //禁止传阅
        if (curModNodeDoc.g("NoCopyToFlag").equals("1")) {
            return;
        }

        //允许传阅
        LinkeyUser linkeyUser = (LinkeyUser) BeanCtx.getBean("LinkeyUser");
        HashSet<String> copyToUserSet = ((LinkeyUser) BeanCtx.getBean("LinkeyUser")).parserNodeMembers(Tools.splitAsSet(curModNodeDoc.g("CopyToOwner"), ",")); //默认传阅用户
        String userBtn = BeanCtx.getLinkeywf().getCurrentModNodeDoc().g("NoCopyToSelectFlag").equals("1") ? ""
                : "<a class=\"easyui-linkbutton\" plain=\"true\" data-options=\"iconCls:'icon-userbtn'\" onclick=\"seluser('WF_SelCopyUser');return false;\" >添加</a>"; //人员选择按扭
        String useridList = Tools.join(copyToUserSet, ",");
        formHtml.append("\n<tr id=\"CopyTr\"><td class=\"texttd\" >" + BeanCtx.getMsg("Engine", "ApprovalForm_CopyToInfo")
                + "</td><td><input name='WF_SelCopyUser' id='WF_SelCopyUser' style='display:none' value=\"" + useridList + "\" >");
        if (Tools.isNotBlank(useridList)) {
            useridList = linkeyUser.getCnName(useridList); //转换为中文名称
        }
        formHtml.append("<span id='WF_SelCopyUser_show' class=\"fieldShow\" value=\"" + useridList + "\" >" + useridList + "</span>" + userBtn + "</td></tr>");
    }

    /**
     * 获得填写意见框
     * 
     * @param formHtml
     */
    private void getRemarkHtml(StringBuilder formHtml) {
        Document curModNodeDoc = BeanCtx.getLinkeywf().getCurrentModNodeDoc();
        if (curModNodeDoc.g("RemarkNullFlag").equals("3")) {
            return; //禁止填写办理意见
        }

        //显示意见填写框
        String remarkName = Tools.isBlank(curModNodeDoc.g("RemarkName")) ? BeanCtx.getMsg("Engine", "ApprovalForm_RemarkName") : curModNodeDoc.g("RemarkName");
        String isRemarkNull = curModNodeDoc.g("RemarkNullFlag");
        String sql = "select WF_MyRemark from BPM_UserProfile where Userid='" + BeanCtx.getUserid() + "'";
        String commonRemarkList = Rdb.getValueBySql(sql) + "\n" + BeanCtx.getSystemConfig("CommonRemarkList");
        String[] remarkSet = Tools.split(commonRemarkList, "\n");
        StringBuilder optionStr = new StringBuilder();
        for (String remark : remarkSet) {
            remark = remark.replace("\r", "");
            optionStr.append("<option value='" + remark + "'>" + remark + "</option>");
        }
      //20180502
        formHtml.append("\n<tr id='TrRemark' ><td class='texttd' id='TdRemark'>" + remarkName + "</td><td td id=\"WF_SelectRemark_td\" style=\"height:118px\">");
        formHtml.append(
                "<select id=\"WF_SelectRemark\"  style=\"width:98%\" onchange=\"SelectRemark(this)\"><option value=''>" + BeanCtx.getMsg("Engine", "ApprovalForm_SelectCommonRemark") + "</option>");
        formHtml.append(optionStr);

        //获得标准初始意见或者是暂存的办理意见
        String tempRemark = Rdb.getValueBySql("select Remark from BPM_TempRemarkList where Userid='" + BeanCtx.getUserid() + "' and DocUnid='" + BeanCtx.getLinkeywf().getDocUnid() + "'");
        if (Tools.isBlank(tempRemark)) {
            tempRemark = curModNodeDoc.g("DefaultRemark");
        }
        //20180502 
//        formHtml.append("</select><a class=\"easyui-linkbutton\" plain=\"true\" data-options=\"iconCls:'icon-edit'\" onclick=\"drawMsg();return false;\" >手写</a><br><textarea name=\"WF_TmpRemark\" id=\"WF_TmpRemark\" IsNull=\"" + isRemarkNull + "\" style=\"width:98%;height:65px;overflow:auto;\">" + tempRemark
//                + "</textarea></td></tr>");
        formHtml.append("</select><br><textarea name=\"WF_TmpRemark\" id=\"WF_TmpRemark\" IsNull=\"" + isRemarkNull + "\" style=\"width:98%;height:65px;overflow:auto;\">" + tempRemark
        		+ "</textarea></td>");
      //add 20180502  添加手写签名   
        formHtml.append("<td id=\"signature_TmpRemark\" style=\"display:none;height:151px;\"><canvas id=\"signName\" width=\"325\" height=\"154\">您的浏览器当前不支持canvas画布，请更换别的浏览器进行签名操作</canvas>&nbsp;&nbsp;<button id=\"clearCanvas\">重写</button></td></tr>");
        //formHtml.append("<td id=\"signature_TmpRemark\" style=\"display:none;\"><img id=\"newImage\" src=\"\" />&nbsp;&nbsp;<button id=\"clearCanvas\">重写</button>&nbsp;&nbsp;</td>");

    }

    /**
     * 根据环节操作按扭选项自动生成操作按扭条
     * 
     * @param canNextNodeFlag根据不同的参数显示不同的按扭可从linkeywf中自动获取此参数
     */
    @SuppressWarnings("unchecked")
    private String getToolbar(int canNextNodeFlag) throws Exception {

        StringBuilder formHtml = new StringBuilder();
        Document curModNodeDoc = BeanCtx.getLinkeywf().getCurrentModNodeDoc();
        String toolbarList = "";
        if (canNextNodeFlag == 1) {
            toolbarList = "BU1009,BU1022"; //提交下一串行用户
        }
        else if (canNextNodeFlag == 2) {
            toolbarList = "BU1006,BU1022"; //返回给转交者按扭
        }
        else if (canNextNodeFlag == 3) {
            toolbarList = "BU1007,BU1022"; //返回给回退者按扭
        }
        else if (canNextNodeFlag == 4) {
            toolbarList = "BU1010,BU1022"; //提交下一会签用户
        }
        else { //提交后继环节
            toolbarList = curModNodeDoc.g("ToolbarList");
            if (Tools.isBlank(toolbarList)) {
                if (BeanCtx.getLinkeywf().isFirstNode()) {
                    toolbarList = "BU1001,BU1002,BU1022"; //首环节不显示回退首环节按扭
                }
                else {
                    toolbarList = "BU1001,BU1002,BU1005,BU1022"; //其他环节默认显示4个按扭
                }
            }
        }
        List<String> toolbarSet = Tools.splitAsList(toolbarList);
        HashMap<String, Document> toolbarDocCache = (HashMap<String, Document>) RdbCache.getSystemCache("BPM_EngineButtonConfig", "ALL"); //获得所有缓存的按扭配置文档,如果取不到则在后面执行sql再取
        for (String toolbarid : toolbarSet) {
            String realToolbarid = toolbarid;
            String realToolbarName = "";
            int spos = toolbarid.indexOf("|");
            if (spos > 0) {
                //有自定义的按扭名称
                realToolbarid = toolbarid.substring(0, spos);
                realToolbarName = toolbarid.substring(spos + 1, toolbarid.length());
            }

            //从缓存中得到按扭的配置，如果没有得到则从数据库中查找
            Document doc = toolbarDocCache.get(realToolbarid + "_" + BeanCtx.getCountry()+ "_" + UIType); //从缓存中获得按扭文档对像
            if (doc == null) {
                String sql = "select Toolbarid,Country,ToolbarName,ToolbarHtml,HiddenRuleNum from BPM_EngineButtonConfig where Toolbarid='" + realToolbarid + "' and Country='" + BeanCtx.getCountry() + "' and UITYPE='" + UIType + "'";
                doc = Rdb.getDocumentBySql(sql); //如果缓存中不存在则直接从数据库中取
                if (doc.isNull()) {
                    BeanCtx.log("W", "警告:按扭(" + realToolbarid + ")未没有配置文档,请在流程处理单按扭配置中进行定义!");
                    continue;
                }
            }

            //如果没有在环节中自定义名称则使用配置文档中的值
            if (Tools.isBlank(realToolbarName)) {
                realToolbarName = doc.g("ToolbarName");
            }

            //看是否有权限显示此按扭
            String ruleNum = doc.g("HiddenRuleNum");
            if (Tools.isNotBlank(ruleNum)) {
                String result = BeanCtx.getExecuteEngine().run(ruleNum);
                if (result.equals("0")) {
                    continue; //说明规则中禁止显示此按扭,跳过
                }
            }

            //可以显示按扭
            formHtml.append(doc.g("ToolbarHtml").replace("{0}", realToolbarName));
        }
        return formHtml.toString();
    }

    /**
     * 获得自定义的处理子表单
     * 
     * @param processid
     * @param curNodeid
     * @return
     */
    private String getCustomSubForm(String processid, String curNodeid) throws Exception {

        StringBuilder formStr = new StringBuilder();
        Document curModNodeDoc = BeanCtx.getLinkeywf().getCurrentModNodeDoc();
        String cusFormNumber = curModNodeDoc.g("CusApprovalFormNum"); //the custom formnumber

        //获得js header 代码
        HtmlParser htmlParser = ((HtmlParser) BeanCtx.getBean("HtmlParser"));
        ModForm insModForm = (ModForm) BeanCtx.getBean("ModForm");
        Document formDoc = insModForm.getFormDoc(cusFormNumber); // 表单
        String jsHeader = formDoc.g("JsHeader");
        jsHeader = htmlParser.parserJsTagValue(jsHeader);/* 分析JS标签 */
        jsHeader = htmlParser.parserXTagValue(BeanCtx.getLinkeywf().getDocument(), jsHeader); /* 分析x标签 */
        formStr.append("<script>\n");
        formStr.append(jsHeader);
        formStr.append("</script>\n");

        //获得表单主体的formbody html
        AppElement appElement = (AppElement) BeanCtx.getBean("form");
        String formBodyHtml = appElement.getElementBody(cusFormNumber, false);
        if (formBodyHtml.indexOf("{BUTTON}") != -1) {
            //自动输出操作按扭
            formBodyHtml = formBodyHtml.replace("{BUTTON}", this.getToolbar(BeanCtx.getLinkeywf().canSelectNodeAndUser()));
        }

        if (formBodyHtml.indexOf("{MYREMARK}") != -1) {
            //自动输出常用处理意见
            String sql = "select WF_MyRemark from BPM_UserProfile where Userid='" + BeanCtx.getUserid() + "'";
            String commonRemarkList = Rdb.getValueBySql(sql) + "\n" + BeanCtx.getSystemConfig("CommonRemarkList");
            String[] remarkSet = Tools.split(commonRemarkList, "\n");
            StringBuilder optionStr = new StringBuilder();
            for (String remark : remarkSet) {
                remark = remark.replace("\r", "");
                optionStr.append("<option value='" + remark + "'>" + remark + "</option>");
            }
            StringBuilder remarkHtml = new StringBuilder();
            remarkHtml.append("<select id=\"WF_SelectRemark\" style=\"width:464px\" onchange=\"SelectRemark(this)\"><option>").append(BeanCtx.getMsg("Engine", "ApprovalForm_SelectCommonRemark"))
                    .append("</option>").append(optionStr).append("</select>");
            formBodyHtml = formBodyHtml.replace("{MYREMARK}", remarkHtml.toString());
        }

        formStr.append(formBodyHtml);//get the approval form body

        return formStr.toString();
    }
}
