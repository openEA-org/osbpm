package cn.linkey.rulelib.S014;

import java.util.*;
import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.DateUtil;
import cn.linkey.util.Tools;
import cn.linkey.wf.*;

/**
 * @RuleName:可审批用户管理表单事件
 * @author admin
 * @version: 8.0
 * @Created: 2014-05-15 12:30
 */
final public class R_S014_E004 implements LinkeyRule {

    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        //获取事件运行参数
        Document formDoc = (Document) params.get("FormDoc"); //表单配置文档
        Document doc = (Document) params.get("DataDoc"); //数据主文档
        String eventName = (String) params.get("EventName");//事件名称
        if (eventName.equals("onFormOpen")) {
            String readOnly = (String) params.get("ReadOnly"); //1表示只读，0表示编辑
            return onFormOpen(doc, formDoc, readOnly);
        }
        else if (eventName.equals("onFormSave")) {
            return onFormSave(doc, formDoc);
        }
        return "1";
    }

    public String onFormOpen(Document doc, Document formDoc, String readOnly) {
        //当表单打开时
        String processid = doc.g("WF_Processid");
        String docUnid = doc.getDocUnid();
        String nodeid = doc.g("Nodeid");
        ModNode insModNode = (ModNode) BeanCtx.getBean("ModNode");
        Document nodeDoc = insModNode.getNodeDoc(processid, nodeid);
        if (!nodeDoc.isNull()) {
            doc.s("CurrentNodeName", nodeDoc.g("NodeName"));
            //获得此环节所有活动的用户
            String sql = "select Userid from BPM_InsUserList where DocUnid='" + docUnid + "' and Nodeid='" + nodeid + "' and Status='Current'";
            Document insUserdoc = Rdb.getDocumentBySql(sql);
            if (!insUserdoc.isNull()) {
                String userid = insUserdoc.g("Userid");
                String userName = BeanCtx.getLinkeyUser().getCnName(userid);
                doc.s("NodeAuthor", userName);
            }
        }

        return "1"; //成功必须返回1，否则表示退出并显示返回的字符串
    }

    public String onFormSave(Document doc, Document formDoc) throws Exception {
        //当表单存盘前
        String processid = doc.g("WF_Processid");
        String docUnid = doc.getDocUnid();
        String nodeid = doc.g("Nodeid");
        String nodeAction = doc.g("NodeAction"); //1表示修改，2表示追加
        String editUserStr = doc.g("Userid");//要修改的用户
        String backFlag = "0";

        //支持兼职指定，在人员选择中指定用户的部门id
        HashMap<String, String> deptSet = BeanCtx.getDeptidByMulStr(editUserStr);
        editUserStr = BeanCtx.getUseridByMulStr(editUserStr);

        ProcessEngine linkeywf = new ProcessEngine();
        BeanCtx.setLinkeywf(linkeywf); //把工作流引擎对像设置为全局变量对像
        linkeywf.init(processid, docUnid, BeanCtx.getUserid(), ""); //初始化工作流引擎
        InsUser insUser = (InsUser) BeanCtx.getBean("InsUser");
        String startUser = "", endUser = "";

        //获得已经活动的用户
        LinkedHashSet<String> endUserSet = new LinkedHashSet<String>();
        LinkedHashSet<String> curUserSet = new LinkedHashSet<String>();
        String sql = "select userid from BPM_InsUserList where DocUnid='" + docUnid + "' and Nodeid='" + nodeid + "' and Status='Current'";
        curUserSet = Rdb.getValueLinkedSetBySql(sql, false); //当前已经活动的用户
        HashSet<String> editUserSet = Tools.splitAsSet(editUserStr); //需要修改的用户,去掉重复值
        if (nodeAction.equals("1")) {
            //表示修改
            //获得所有需要结束的用户列表
            for (String userid : curUserSet) {
                if (!editUserSet.contains(userid)) {
                    endUserSet.add(userid);
                }
            }
            endUser = Tools.join(endUserSet, ",");

            //获得所有新加入的用户列表
            for (String userid : editUserSet) {
                if (curUserSet.contains(userid)) {
                    editUserSet.remove(userid); //如果用户在当前curUserSet中存在则从editUserSet中删除掉
                }
            }
            startUser = Tools.join(editUserSet, ",");

        }
        else if (nodeAction.equals("2")) {
            //表示追加
            editUserSet.removeAll(curUserSet);
            startUser = Tools.join(editUserSet, ",");
        }

        MessageImpl message = (MessageImpl) BeanCtx.getBean("Message");

        //需要结束的用户
        String endUserName = "";
        if (Tools.isNotBlank(endUser)) {
            String[] userArray = Tools.split(endUser, ",");
            for (String userid : userArray) {
                Document insUserDoc;
                if (userid.equals(BeanCtx.getUserid()) && nodeid.equals(BeanCtx.getLinkeywf().getCurrentNodeid())) {
                    insUserDoc = BeanCtx.getLinkeywf().getCurrentInsUserDoc(); //从缓存中拿当前用户的实例文档
                }
                else {
                    insUserDoc = insUser.getInsUserDoc(processid, nodeid, docUnid, userid, "Current"); //从sql表中拿
                }
                backFlag = insUserDoc.g("IsBackFlag");
                break;
            }
            insUser.endUser(processid, docUnid, nodeid, endUser);
            message.cancelToDo(docUnid, endUser);//取消待办
            endUserName = BeanCtx.getLinkeyUser().getCnName(endUser);
        }

        //启动需要启动的用户
        String startUserName = "";
        if (Tools.isNotBlank(startUser)) {
            insUser.startUser(processid, docUnid, nodeid, startUser, deptSet, backFlag);
            message.sendToDo("", startUser, true); //发送待办消息
            startUserName = BeanCtx.getLinkeyUser().getCnName(startUser);
        }

        linkeywf.getDocument().removeItem("Nodeid");
        linkeywf.getDocument().removeItem("NodeAction");
        linkeywf.saveDocument();

        //      BeanCtx.userlog(docUnid, "流程监控调整审批用户", "调整("+nodeid+")启动的用户为("+startUserName+"),结束的用户为("+endUserName+")!");

        //增加节点修改日记
        Document nodeDoc = ((ModNode) BeanCtx.getBean("ModNode")).getNodeDoc(processid, nodeid);
        String nodeName = nodeDoc.g("NodeName");
        String remark = "在流程监控中调整节点(" + nodeName + ")的处理用户";
        if (Tools.isNotBlank(startUserName)) {
            remark += "启动用户(" + startUserName + ")";
        }
        if (Tools.isNotBlank(endUserName)) {
            remark += "结束用户(" + endUserName + ")";
        }
        addProcessReadLog(docUnid, processid, remark);

        return "成功启动的用户为(" + startUserName + "),结束的用户为(" + endUserName + ")!"; //成功必须返回1，否则表示退出存盘
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