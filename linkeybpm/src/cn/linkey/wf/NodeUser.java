package cn.linkey.wf;

import cn.linkey.dao.Rdb;
import cn.linkey.dao.RdbCache;
import cn.linkey.factory.BeanCtx;
import cn.linkey.util.Tools;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;

/**
 * 用户和节点操作的业务逻辑运行类
 * 
 * @author Administrator 本类为单实例类
 */
public class NodeUser {

    /**
     * 从用户和节点列表中的组合中分出指定节点的用户
     * 
     * @param userList 用户列表格式为:admin$Node1,lch$Node2, 有可能格式为admin#DP1002$Node1,lch#DP1001$Node2
     * @param nodeid 节点id号 传入*号表示所有节点
     * @return 返回用户列表多个用逗号分隔
     */
    public String getNodeUser(String userList, String nodeid) {
        String userid;
        String tmpNodeid;
        StringBuilder userStr = new StringBuilder();
        int i = 0;
        for (String userItem : Tools.split(userList)) {
            int spos = userItem.indexOf("$");
            if (spos != -1) {
                userid = userItem.substring(0, spos);
                tmpNodeid = userItem.substring(spos + 1, userItem.length());
            }
            else {
                userid = userItem;
                tmpNodeid = "";
            }

            //如果传入的用户为user01#DP1001这种格式时，要取前面的userid
            int epos = userid.indexOf("#");
            if (epos != -1) {
                userid = userid.substring(0, epos);
            }

            if (tmpNodeid.equals(nodeid) || Tools.isBlank(tmpNodeid) || nodeid.equals("*")) {
                if (i == 0) {
                    userStr.append(userid);
                    i = 1;
                }
                else {
                    userStr.append("," + userid);
                }
            }
        }
        return userStr.toString();
    }

    /**
     * 从WF_NextUserList中获得用户和部门的HashMap结构
     * 
     * @param userList格式为 user01#DP1002$T10003,user02#DP1003$NodeT0005
     * @param nodeid为要分析的节点id，指定节点id则只返回此nodeid的用户和部门map对像，如果要返回所有用户和部门的map则nodeid传入*号
     * @return 用户与部门的的hasmap对像 key为userid,value为deptid
     */
    public HashMap<String, String> getNodeUserAndDept(String userList, String nodeid) {
        HashMap<String, String> userDept = new HashMap<String, String>();
        String[] userArray = Tools.split(userList);
        for (String userItem : userArray) {
            String userid, deptid, tmpNodeid;
            int spos = userItem.indexOf("#");
            if (spos != -1) {
                //格式为user01#DP1002$T1003
                userid = userItem.substring(0, spos);
                int epos = userItem.indexOf("$");
                if (epos != -1) {
                    deptid = userItem.substring(spos + 1, epos);
                    tmpNodeid = userItem.substring(epos + 1);
                }
                else {
                    deptid = "";
                    tmpNodeid = "";
                }

            }
            else {
                //格式为user01$T10003
                spos = userItem.indexOf("$");
                if (spos != -1) {
                    userid = userItem.substring(0, spos);
                    deptid = "";
                    tmpNodeid = userItem.substring(spos + 1);
                }
                else {
                    userid = userItem;
                    deptid = "";
                    tmpNodeid = "";
                }
            }
            if (tmpNodeid.equals(nodeid) || nodeid.equals("*")) {
                userDept.put(userid, deptid);
            }
        }
        return userDept;
    }

    /**
     * 获得本次操作新增加的审批用户和完成的审批用户
     * 
     * @return
     */
    @SuppressWarnings("unchecked")
    public void getInOutUser(LinkedHashSet<String> newAuthor) {
        //计算新增加和结束的用户信息放入到文档变量中去
        LinkedHashSet<String> oldAuthor = Tools.splitAsLinkedSet(BeanCtx.getLinkeywf().getDocument().g("WF_Author"));
        //BeanCtx.out("旧的审批用户为"+oldAuthor);

        LinkedHashSet<String> tmpNewAuthor = (LinkedHashSet<String>) newAuthor.clone();
        //BeanCtx.out("当前审批用户为"+tmpNewAuthor);

        tmpNewAuthor.removeAll(oldAuthor); //新的用户集合去掉旧的用户集合，剩下的为这次新进入的用户
        BeanCtx.getLinkeywf().getDocument().s("WF_InNewUser", Tools.join(tmpNewAuthor, ","));
        //BeanCtx.out("新进入的用户列表为="+tmpNewAuthor);

        oldAuthor.removeAll(newAuthor);//旧的用户删除掉目前新的用户，剩下的为这次结束的用户
        BeanCtx.getLinkeywf().getDocument().s("WF_OutEndUser", Tools.join(oldAuthor, ","));
        //BeanCtx.out("结束的用户列表为="+oldAuthor);
    }

    /**
     * 根据实例文档获得审批用户
     * 
     * @param docUnid
     * @return 返回去掉重复值和空值的用户集合
     */
    public LinkedHashSet<String> getAuthorUser(String docUnid) {
        String sql = "select Nodeid from BPM_InsNodeList where DocUnid='" + docUnid + "' and Status='Current'";
        HashSet<String> nodeSet = Rdb.getValueSetBySql(sql);
        nodeSet.remove("Process"); //过程节点不计算在内
        if (nodeSet.size() == 0) {
            return new LinkedHashSet<String>();
        } //没有找到活动的环节
        StringBuilder sqlWhere = new StringBuilder();
        int i = 0;
        for (String nodeid : nodeSet) {
            if (i == 0) {
                sqlWhere.append("Nodeid='" + nodeid + "'");
                i = 1;
            }
            else {
                sqlWhere.append(" or Nodeid='" + nodeid + "'");
            }
        }
        sql = "select userid from BPM_InsUserList where docUnid='" + docUnid + "' and Status='Current' and (" + sqlWhere.toString() + ")";
        return Rdb.getValueLinkedSetBySql(sql);
    }

    /**
     * 根据实例文档获得所有当前有权阅读的用户
     * 
     * @param docUnid
     * @return 返回去掉重复值和空值的用户集合
     */
    public HashSet<String> getAllReaderUser(String docUnid) {
        //所有提交后的用户都认为是可以阅读的用户，不管此用户有没有启动
        //String sql="select userid from BPM_InsUserList where docUnid='"+docUnid+"' and (Status='End' or Status='Copy' or Status='Read' or Status='Current' or Status='Pause')";
        String sql = "select userid from BPM_InsUserList where docUnid='" + docUnid + "' union all select userid from BPM_InsCopyUserList where docUnid='" + docUnid + "'";
        return Rdb.getValueSetBySql(sql);
    }

    /**
     * 获得指定文档指定节点的已经存在的实例用户
     * 
     * @param docUnid 文档unid
     * @param Nodeid 节点id
     * @return userid集合
     */
    public LinkedHashSet<String> getInsNodeUser(String docUnid, String Nodeid) {
        String sql = "select Userid from BPM_InsUserList where DocUnid='" + docUnid + "' and Nodeid='" + Nodeid + "'";
        return Rdb.getValueLinkedSetBySql(sql);
    }

    /**
     * 根据实例文档获得所有已审批结束的用户
     * 
     * @param docUnid
     * @return 返回去掉重复值和空值的用户集合
     */
    public HashSet<String> getEndUser(String docUnid) {
        String sql = "select userid from BPM_InsUserList where docUnid='" + docUnid + "' and Status='End'";
        return Rdb.getValueSetBySql(sql);
    }

    /**
     * 根据实例文档获得所有待阅用户
     * 
     * @param docUnid
     * @return 返回去掉重复值和空值的用户集合
     */
    public HashSet<String> getCopyUser(String docUnid) {
        String sql = "select userid from BPM_InsCopyUserList where docUnid='" + docUnid + "' and Status='Current'";
        return Rdb.getValueSetBySql(sql);
    }

    /**
     * 根据实例文档获得所有已阅用户
     * 
     * @param docUnid
     * @return 返回去掉重复值和空值的用户集合
     */
    public HashSet<String> getEndCopyUser(String docUnid) {
        String sql = "select userid from BPM_InsCopyUserList where docUnid='" + docUnid + "' and Status='End'";
        return Rdb.getValueSetBySql(sql);
    }

    /**
     * 根据实例文档获得所有已阅用户
     * 
     * @param docUnid
     * @return 返回去掉重复值和空值的用户集合
     */
    public HashSet<String> getReadUser(String docUnid) {
        String sql = "select userid from BPM_InsUserList where docUnid='" + docUnid + "' and Status='Read'";
        return Rdb.getValueSetBySql(sql);
    }

    /**
     * 获得当前文档正在活动的节点名称列表
     * 
     * @param docUnid
     * @return 多个用逗号分隔
     */
    public String getCurrentNodeName(String docUnid) {
        String sql = "select NodeName from BPM_InsNodeList where docUnid='" + docUnid + "' and Status='Current' and NodeType<>'Process'";
        return Rdb.getValueBySql(sql);
    }

    /**
     * 获得当前文档正在活动的节点列表
     * 
     * @param docUnid
     * @return 多个用逗号分隔
     */
    public String getCurrentNodeid(String docUnid) {
        String sql = "select Nodeid from BPM_InsNodeList where docUnid='" + docUnid + "' and Status='Current'  and NodeType<>'Process'";
        return Rdb.getValueBySql(sql);
    }

    /**
     * 获得当前文档已结束的节点列表
     * 
     * @param docUnid
     * @return 多个用逗号分隔
     */
    public String getEndNodeid(String docUnid) {
        String sql = "select Nodeid from BPM_InsNodeList where docUnid='" + docUnid + "' and Status='End'  and NodeType<>'Process'";
        return Rdb.getValueBySql(sql);
    }

    /**
     * 获得活动参与者规则所运行的结果
     * 
     * @param ruleid 活动参与者的规则编号
     */
    @SuppressWarnings("unchecked")
    public LinkedHashSet<String> getNodeOwnerRuleUser(String ruleid) throws Exception {
        if (ruleid.equals("PAR10001")) {
            //如果执行流程启动者则直接返回，不用运算规则，提升性能
            String userid = BeanCtx.getLinkeywf().getDocument().g("WF_AddName");
            LinkedHashSet<String> userSet = new LinkedHashSet<String>();
            userSet.add(userid);
            return userSet;
        }
        HashMap<String, String> configCache = (HashMap<String, String>) RdbCache.getSystemCache("BPM_EngineNodeOwnerConfig", "");
        String ruleNum = configCache.get(ruleid);
        if (ruleNum == null) {
            //直接从数据库中取
            ruleNum = Rdb.getValueBySql("select RuleNum from BPM_EngineNodeOwnerConfig where OwnerRuleid='" + ruleid + "'");
        }
        String userList = "";
        if (Tools.isNotBlank(ruleNum)) {
            userList = BeanCtx.getExecuteEngine().run(ruleNum); //执行规则并得到返回值
        }
        return Tools.splitAsLinkedSet(userList, ",");
    }

    /**
     * 分析字符串中的{Node.Nodeid}
     * 
     * @param str 需要分析的字符串
     * @param docUnid 主文档的unid
     * @return 返回分析好的字符串
     */
    public String parserStrForNodeUser(String str, String docUnid) {
        if (Tools.isBlank(str)) {
            return "";
        }
        String startCode = "{";
        String endCode = "}";
        int spos = str.indexOf(startCode);
        if (spos == -1) {
            return str;
        } // 没有{符号直接返回
        StringBuilder newHtmlStr = new StringBuilder(str.length());
        while (spos != -1) {
            int epos = str.indexOf(endCode);
            String fdName = str.substring(spos + 1, epos);
            if (fdName.startsWith("Node.")) {
                String lStr = str.substring(0, spos);
                str = str.substring(epos + 1, str.length());
                String nodeid = fdName.substring(fdName.indexOf(".") + 1);
                String fdValue = Tools.join(getInsNodeUser(docUnid, nodeid), ",");//获得环节的实例用户
                newHtmlStr.append(lStr);
                newHtmlStr.append(fdValue);
            }
            else {
                //如果不是Nodeid.开头的就不分析
                String lStr = str.substring(0, epos + 1);
                str = str.substring(epos + 1, str.length());
                newHtmlStr.append(lStr);
            }
            spos = str.indexOf(startCode);
        }
        newHtmlStr.append(str);
        return newHtmlStr.toString();
    }

}
