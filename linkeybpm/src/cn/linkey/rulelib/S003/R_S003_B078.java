package cn.linkey.rulelib.S003;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;
import cn.linkey.wf.InsNode;
import cn.linkey.wf.InsUser;
import cn.linkey.wf.ModNode;

/**
 * 本规则负责启动outProcess节点的任务
 * outProcess节点不具备自动推进功能,必须由人工手动推进 
 * 当Action运行到本规则时就算结束一次推进，因为遇到outProcess节点就表示启动的相应的用户任务,由用户去外部子流程中完成
 * 
 * @author Administrator
 */
public class R_S003_B078 implements LinkeyRule {
    @SuppressWarnings("unchecked")
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        String processid = BeanCtx.getLinkeywf().getProcessid();
        String runNodeid = (String) params.get("WF_RunNodeid"); //获得要运行的节点id
        HashMap<String, HashMap<String, String>> allUserDetpSet = (HashMap<String, HashMap<String, String>>) params.get("WF_NextUserDept"); //用户与部门id的组合字符串
        String startUserList = ""; //本环节要启动的用户列表

        //1.首先要启动本环节
        ((InsNode) BeanCtx.getBean("InsNode")).startNode(processid, BeanCtx.getLinkeywf().getDocUnid(), runNodeid);

        //2.获得本环节的所有需要启动的用户
        ModNode insModNode = (ModNode) BeanCtx.getBean("ModNode");
        Document modNodeDoc = insModNode.getNodeDoc(processid, runNodeid);
        String usePostOwnerFlag = modNodeDoc.g("UsePostOwner"); //默认使用环节中的配置值
        if (params.get("WF_UsePostOwner") != null) {
            usePostOwnerFlag = (String) params.get("WF_UsePostOwner"); //如果运行参数中指定了则使用运行时的指定值
        }

        HashMap<String, String> userDeptSet = new HashMap<String, String>();//用户与部门id的组合map

        //调试流程信息
        if (BeanCtx.getLinkeywf().isDebug()) {
            BeanCtx.out("运行outProcess节点=" + runNodeid + ",是否使用Post用户作为参与者=" + usePostOwnerFlag);
        }
        if (usePostOwnerFlag.equals("1")) {
            //1.使用上一环节提交的用户数据作为本环节的参与者
            //取post上来的参数作为参与者
            String engineAction = (String) params.get("WF_EngineAction");//由ProcessEngine.endParentSubProcessNode()
            HashMap<String, String> nextUserMap = new HashMap<String, String>();
            if (engineAction != null && engineAction.equalsIgnoreCase("BackToParentProcess")) {
                //子流程时由WF_SubNextUserList提交上来的数据
                startUserList = (String) params.get("WF_SubNextUserList"); //直接把WF_SubNextUserList提交的数据作为参与者
                userDeptSet = BeanCtx.getDeptidByMulStr(startUserList); //部门与用户的组合
                startUserList = BeanCtx.getUseridByMulStr(startUserList); //去掉#号后面的用户id
            }
            else {
                //不是子流程时由WF_NextUserList上提交上来的用户
                nextUserMap = (HashMap<String, String>) params.get("WF_NextUserList"); //当前上一环节提交的所有用户数据
                if (nextUserMap != null) {
                    startUserList = nextUserMap.get(runNodeid); //获得本环节的被提交的用户
                }
            }
            if (allUserDetpSet != null) {
                userDeptSet = (HashMap<String, String>) allUserDetpSet.get(runNodeid);//取当前环节提交上来的用户与部门的组合map对像
            }

            /*
             * 2.从驻留数据中去查看看是否有本环节的中间驻留的提交数据，如果有也需要加上，因为有可能其他用户也提交了但是被暂存在中间环节了 不管是那个环节提交的，只要是提交给本环节的数据，在本环节启动时都算是提交上来的用户，都要加入启动者中
             */
            String sql = "select WF_NextUserList,WF_NextUserDept from BPM_InsStayData where DocUnid='" + BeanCtx.getLinkeywf().getDocUnid() + "' and WF_NextNodeid='" + runNodeid + "'";
            Document doc = Rdb.getDocumentBySql(sql);
            String stayUserList = "", stayUserDept = "";
            if (!doc.isNull()) {
                stayUserList = doc.g("WF_NextUserList");
                stayUserDept = doc.g("WF_NextUserDept"); //格式{user1=dp01,user2=dp02}
            }

            //3.获得最终用户与部门id的组像关系的map对像, 追加驻留的数据项
            if (Tools.isNotBlank(stayUserDept)) {
                String tmpStr = stayUserDept.substring(1, stayUserDept.length() - 1);
                String[] tmpArray = Tools.split(tmpStr);
                for (String userid : tmpArray) {
                    String[] tmpUserArray = Tools.split(userid, "=");
                    userDeptSet.put(tmpUserArray[0], tmpUserArray[1]);
                }
            }

            //4.组合得到最终要启动的用户列表
            if (Tools.isBlank(startUserList)) {
                startUserList = stayUserList;
            }
            else {
                if (Tools.isNotBlank(stayUserList)) {
                    startUserList = startUserList + "," + stayUserList;
                }
            }
        }
        else {
            //使用环节中配置的PotentialOwner字段作为参与者
            startUserList = ((ModNode) BeanCtx.getBean("ModNode")).getNodePotentialOwner(processid, runNodeid);
            startUserList = Tools.join(BeanCtx.getLinkeyUser().parserNodeMembers(Tools.splitAsLinkedSet(startUserList)), ",");//分析用户字符串
            userDeptSet = BeanCtx.getDeptidByMulStr(startUserList); //部门与用户的组合
            startUserList = BeanCtx.getUseridByMulStr(startUserList); //去掉#号后面的用户id
        }

        if (BeanCtx.getLinkeywf().isDebug()) {
            BeanCtx.out("启动节点" + runNodeid + "用户startUserList=" + startUserList);
            BeanCtx.out("启动节点" + runNodeid + "指定用户所属部门userDeptSet=" + userDeptSet);
        }

        //3.启动本环节的用户任务
        if (Tools.isNotBlank(startUserList)) {
            ((InsUser) BeanCtx.getBean("InsUser")).startUser(processid, BeanCtx.getLinkeywf().getDocUnid(), runNodeid, startUserList, userDeptSet);
        }

        //4.删除提交到本环节的中间表的驻留数据
        String sql = "delete from BPM_InsStayData where DocUnid='" + BeanCtx.getLinkeywf().getDocUnid() + "' and WF_NextNodeid='" + runNodeid + "'";
        Rdb.execSql(sql);

        return "";
    }

}
