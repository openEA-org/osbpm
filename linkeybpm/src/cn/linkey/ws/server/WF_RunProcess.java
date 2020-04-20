package cn.linkey.ws.server;

import java.util.HashMap;
import java.util.HashSet;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.util.Tools;
import cn.linkey.wf.ProcessEngine;
import cn.linkey.wf.NodeUser;

@WebService
public class WF_RunProcess {

    /**
     * @param docXml 传入的文档参数,格式为<Items><WFItem name="字段名">字段值</WFItem></Items>
     * @param actionid 要执行的动作id
     * @param processid 流程unid
     * @param docUnid 文档unid,传空值表示启动一个新的流程
     * @param nextNodeid 要提交的节点
     * @param nextUserList 要提交的用户格式为:admin$Node1,user01$Node2,如果只有一个节点也可以直接admin,user01
     * @param userid 用户id
     * @param remark 意见
     * @param sysid 系统id
     * @param syspwd 系统密码
     * @return XML字符串格式与docXml格式一样
     */
    @WebMethod
    public String runProcess(@WebParam(name = "docXml") String docXml, @WebParam(name = "actionid") String actionid, @WebParam(name = "processid") String processid,
            @WebParam(name = "docUnid") String docUnid, @WebParam(name = "nextNodeid") String nextNodeid, @WebParam(name = "nextUserList") String nextUserList,
            @WebParam(name = "userid") String userid, @WebParam(name = "remark") String remark, @WebParam(name = "sysid") String sysid, @WebParam(name = "syspwd") String syspwd) {
        Document returnDoc = null;
        HashMap<String, Object> params = new HashMap<String, Object>();
        try {
            BeanCtx.init(userid, null, null); //环境初始化
            returnDoc = BeanCtx.getDocumentBean("");

            if (Tools.isBlank(processid)) {
                returnDoc.s("WF_SucessFlag", "0");
                returnDoc.s("WF_Msg", "Error:processid is null!");
                return returnDoc.toString();
            }

            //0.检测业务系统和密码是否正确
            String sql = "select * from BPM_BusinessSystem where Systemid='" + sysid + "' and SystemPwd='" + syspwd + "'";
            if (!Rdb.hasRecord(sql)) {
                returnDoc.s("WF_SucessFlag", "0");
                returnDoc.s("WF_Msg", "Error:sysid or syspwd error!");
                return returnDoc.toString();
            }

            //1.开启事务并初始化工作流引擎
            Rdb.setAutoCommit(false); //开启事务
            ProcessEngine linkeywf = new ProcessEngine();
            BeanCtx.setLinkeywf(linkeywf); //把工作流引擎对像设置为全局变量对像
            if (Tools.isBlank(docUnid)) {
                docUnid = Rdb.getNewUnid();
            } //如果没有传入文档unid则说明要启动一个新文档
            linkeywf.init(processid, docUnid, BeanCtx.getUserid(), ""); //初始化工作流引擎
            if (Tools.isNotBlank(docXml)) {
                linkeywf.getDocument().appendFromXml(docXml); //把传入的xml字符串附加到主文档中去
            }

            linkeywf.getDocument().s("WF_Systemid", sysid); //设置系统标识，表示此文档是由业务系统启动的

            //2.准备节点参数
            String nextNodeList = nextNodeid;
            HashSet<String> nextNodeSet = new HashSet<String>();
            if (Tools.isNotBlank(nextNodeList)) {
                nextNodeSet.addAll(Tools.splitAsSet(nextNodeList));
            }

            //3.准备用户参数
            NodeUser insNodeUser = (NodeUser) BeanCtx.getBean("NodeUser");
            HashMap<String, String> nextUserMap = new HashMap<String, String>();
            if (Tools.isNotBlank(nextUserList)) {
                if (Tools.isNotBlank(nextNodeList)) { //有节点有用户
                    if (nextNodeList.indexOf("$") != -1) {
                        for (String nodeid : nextNodeSet) {
                            nextUserMap.put(nodeid, insNodeUser.getNodeUser(nextUserList, nodeid)); //用户列表格式为:admin$Node1,lch$Node2
                        }
                    }
                    else {
                        for (String nodeid : nextNodeSet) {
                            nextUserMap.put(nodeid, nextUserList); //把所有用户提交到每一个环节中去
                        }
                    }
                }
                else {
                    nextUserMap.put("ALL", nextUserList); //没有节点只有用户设置为全部
                }
            }

            //4.准备传阅用户
            String copyUserList = linkeywf.getDocument().g("WF_CopyUserList");
            if (Tools.isNotBlank(copyUserList)) {
                nextUserMap.put("COPYUSER", copyUserList); //要抄送的用户
            }

            //5.准备运行工作流的map参数************************************开始
            //全部可用参数如下：
            //WF_Processid流程unid
            //WF_DocUnid文档unid
            //WF_Action动作id标识
            //WF_NextNodeid 提交下一环节类型为HashSet<String>
            //WF_NextUserList提交的下一环节参与者类型HashMap<String,String> key为 COPYUSER时表示传阅用户
            //WF_StopNodeType 停止推进的节点类型 String
            //WF_UsePostOwner强制使用提交的WF_NextUserList参数的用户作为参与者 String
            //WF_SendSms发送手机短信标记 String
            //WF_IsBackFlag回退后是否需要返回标记1表示再次提交，2表示直接返回 String
            //WF_ReassignmentFlag转交后是否需要返回标记1表示不需要，2表示需要返回 String
            //WF_AllRouterPath使用指定的路径集合进行运行 String
            //WF_RunActionid动作标识由WF_Action传入
            //WF_RunNodeid要运行的节点，一般由Action规则根据后续节点生成

            params.put("WF_NextNodeid", nextNodeSet); //要提交的节点类型为HashSet<String>
            params.put("WF_NextUserList", nextUserMap); //要提交的用户类型为HashMap<String,String>
            params.put("WF_Remark", remark); //加入意见

            //5.1设置发送手机短信标记
            String sendSms = linkeywf.getDocument().g("WF_SendSms");
            if (Tools.isNotBlank(sendSms)) {
                params.put("WF_SendSms", sendSms);//发送手机短信标记
            }

            //5.2设置回退后返回标记，如果为2表示回退后需要直接返回给回退者
            String isBackFlag = linkeywf.getDocument().g("WF_IsBackFlag");
            if (Tools.isNotBlank(isBackFlag)) {
                params.put("WF_IsBackFlag", isBackFlag);
            }

            //5.3设置转他人处理并返回标记
            String ReassignmentFlag = linkeywf.getDocument().g("WF_ReassignmentFlag"); //转交时是否需要转交者返回的标记1表示不需要2表示需要
            if (Tools.isNotBlank(ReassignmentFlag)) {
                params.put("WF_ReassignmentFlag", ReassignmentFlag);
            }

            //BeanCtx.out("Run本次Actionid=="+actionid);
            //BeanCtx.out("Run提交参数=="+params.toString());
            //************************************************************准备结束

            //6.提交工作流引擎运行
            String msg = linkeywf.run(actionid, params);

            //7.如果出错则数据需要回滚
            if (BeanCtx.isRollBack()) {
                //获得回滚后的提示信息
                if (Tools.isBlank(linkeywf.getRollbackMsg())) {
                    msg = BeanCtx.getMsg("Engine", "Error_EngineRun");
                }
                else {
                    msg += linkeywf.getRollbackMsg();
                }
                params.put("ErrorType", "RollBack");
                BeanCtx.getEventEngine().run(processid, "Process", "EngineRunError", params); //注册流程运行出错后的事件
                linkeywf.setRunStatus(false);
            }
            if (linkeywf.isRunStatus()) {
                //如果流程运行成功，则返回以下参数
                returnDoc.s("WF_Processid", linkeywf.getDocument().g("WF_Processid"));
                returnDoc.s("WF_DocUnid", linkeywf.getDocUnid());
                returnDoc.s("WF_ProcessName", linkeywf.getProcessName());
                returnDoc.s("WF_Author", linkeywf.getDocument().g("WF_Author"));
                returnDoc.s("WF_Author_CN", linkeywf.getDocument().g("WF_Author_CN"));
                returnDoc.s("WF_CurrentNodeid", linkeywf.getDocument().g("WF_CurrentNodeid"));
                returnDoc.s("WF_CurrentNodeName", linkeywf.getDocument().g("WF_CurrentNodeName"));
                returnDoc.s("WF_EndUser", linkeywf.getDocument().g("WF_EndUser"));
                returnDoc.s("WF_AddName", linkeywf.getDocument().g("WF_AddName"));
                returnDoc.s("WF_DocCreated", linkeywf.getDocument().g("WF_DocCreated"));
                returnDoc.s("Subject", linkeywf.getDocument().g("Subject"));
                returnDoc.s("WF_SucessFlag", "1");
            }
            else {
                returnDoc.s("WF_SucessFlag", "0");
            }
            returnDoc.s("WF_Msg", msg);

        }
        catch (Exception e) {
            BeanCtx.setRollback(true); //设置需要回滚
            try {
                params.put("ErrorType", "Exception");
                BeanCtx.getEventEngine().run(processid, "Process", "EngineRunError", params); //注册流程运行出错后的事件
            }
            catch (Exception re) {
                BeanCtx.log("E", "运行流程出错处理规则时出错!");
            }
            returnDoc.s("WF_SucessFlag", "0");
            returnDoc.s("WF_Msg", "System submit exception!");
            e.printStackTrace();
        }
        finally {
            BeanCtx.close(); //这里会自动提交或回滚事务
        }
        return returnDoc.toString();
    }

}
