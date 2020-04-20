package cn.linkey.ws.server;

import java.util.HashMap;
import java.util.LinkedHashSet;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.util.Tools;
import cn.linkey.wf.ProcessEngine;
import cn.linkey.wf.ProcessUtil;

@WebService
public class WF_OpenProcess {

    /**
     * @param docXml 传入的文档参数,格式为<Items><WFItem name="字段名">字段值</WFItem></Items>
     * @param processid 流程unid
     * @param docUnid 文档unid
     * @param userid 用户id
     * @param sysid 系统id
     * @param syspwd 系统密码
     * @return XML字符串格式与docXml格式一样
     */
    @WebMethod
    public String openProcess(@WebParam(name = "docXml") String docXml, @WebParam(name = "processid") String processid, @WebParam(name = "docUnid") String docUnid,
            @WebParam(name = "userid") String userid, @WebParam(name = "sysid") String sysid, @WebParam(name = "syspwd") String syspwd) {
        Document returnDoc = null;
        StringBuilder jsonStr = new StringBuilder();
        try {
            BeanCtx.init(userid, null, null); //环境初始化

            returnDoc = BeanCtx.getDocumentBean("");

            //0.看是否传入了流程id
            if (Tools.isBlank(processid)) {
                returnDoc.s("WF_SucessFlag", "0");
                returnDoc.s("WF_Msg", "Error:processid is null!");
                return returnDoc.toJson();
            }

            //1.检测业务系统和密码是否正确
            String sql = "select * from BPM_BusinessSystem where Systemid='" + sysid + "' and SystemPwd='" + syspwd + "'";
            if (!Rdb.hasRecord(sql)) {
                returnDoc.s("WF_SucessFlag", "0");
                returnDoc.s("WF_Msg", "Error:sysid or syspwd error!");
                return returnDoc.toJson();
            }

            //初始化引擎
            ProcessEngine linkeywf = new ProcessEngine();
            BeanCtx.setLinkeywf(linkeywf); //把工作流引擎对像设置为全局变量对像
            //如果没有传入文档unid则说明要启动一个新文档
            if (Tools.isBlank(docUnid)) {
                docUnid = Rdb.getNewUnid();
            }
            linkeywf.init(processid, docUnid, BeanCtx.getUserid(), ""); //初始化工作流引擎
            linkeywf.getDocument().appendFromXml(docXml); //把传入的xml字符串附加到主文档中去

            //输出当前文档的所有字段内容
            linkeywf.getDocument().copyAllItems(returnDoc);
            returnDoc.s("WF_SucessFlag", "1");
            returnDoc.s("WF_IsProcessAdmin", String.valueOf(linkeywf.isProcessOwner()));
            returnDoc.s("WF_LockStatus", linkeywf.getLockStatus());
            returnDoc.s("WF_ProcessName", linkeywf.getProcessName());
            returnDoc.s("WF_CurrentNodeid", linkeywf.getCurrentNodeid());

            //找到所有后继节点的配置信息
            int canNextNodeFlag = 0;
            StringBuilder nextNodeJson = new StringBuilder();
            if (Tools.isNotBlank(linkeywf.getCurrentNodeid())) {
                canNextNodeFlag = linkeywf.canSelectNodeAndUser(); //获得是否可以选择后继环节返回0表示可以
                if (canNextNodeFlag == 0) { //看是可以显示节点和人员选项
                    //这里要找到所有路由和节点输出节点的配置文档对像
                    LinkedHashSet<Document> nextNodeDc = new LinkedHashSet<Document>();
                    ProcessUtil.getNextNodeDoc(processid, linkeywf.getCurrentNodeid(), nextNodeDc);
                    for (Document doc : nextNodeDc) {
                        if (nextNodeJson.length() > 0) {
                            nextNodeJson.append(",");
                        }
                        nextNodeJson.append(doc.toString()); //转换为节点的xml配置信息输出
                    }
                }
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

            if (linkeywf.getCurrentModNodeDoc() != null) {
                linkeywf.getCurrentModNodeDoc().s("WF_Actionid", linkeywf.getCurrentActionid());
                jsonStr.append(",\"WF_CurrentNodeConfig\":[" + linkeywf.getCurrentModNodeDoc().toJson() + "]");
                jsonStr.append(",\"WF_NextNodeConfig\":[" + nextNodeJson.toString() + "]");

            }
            else {
                jsonStr.append(",\"WF_CurrentNodeConfig\":\"\"");
                jsonStr.append(",\"WF_NextNodeConfig\":\"\"");
            }

        }
        catch (Exception e) {
            BeanCtx.setRollback(true); //设置需要回滚
            e.printStackTrace();
            returnDoc.s("WF_SucessFlag", "0");
            returnDoc.s("WF_Msg", "System run exception!");
            return returnDoc.toJson();

        }
        finally {
            BeanCtx.close(); //这里会自动提交或回滚事务
        }

        //转为json输出
        jsonStr.append("}");
        return jsonStr.toString();
    }

}
