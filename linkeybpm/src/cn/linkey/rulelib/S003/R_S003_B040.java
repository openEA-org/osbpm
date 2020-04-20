package cn.linkey.rulelib.S003;

import java.util.*;
import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.org.LinkeyUser;
import cn.linkey.util.Tools;

/**
 * @RuleName:回退任意环节
 * @author admin
 * @version: 8.0
 * @Created: 2014-05-07 11:53
 */
final public class R_S003_B040 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) {
        //params为运行本规则时所传入的参数
        String action = BeanCtx.g("WF_Action");
        if (action.equals("GoToFirstNode")) {
            getGoToFirstNode();
        }
        else if (action.equals("ReturnToAnyNode")) {
            getReturnToAnyNode();
        }
        return "";
    }

    public void getReturnToAnyNode() {
        LinkeyUser linkeyUser = BeanCtx.getLinkeyUser();
        StringBuilder optionStr = new StringBuilder();
        String docUnid = BeanCtx.g("WF_DocUnid", true);
        String curNodeid = BeanCtx.g("WF_CurrentNodeid", true);
        String sql = "select NodeName,Nodeid from BPM_InsNodeList where DocUnid='" + docUnid + "' and NodeType='Task' order by StartTime";
        Document[] dc = Rdb.getAllDocumentsBySql(sql);
        optionStr.append("<option value=''>---请选择要回退的节点---</option>");
        LinkedHashMap<String, String> nodeMap = new LinkedHashMap<String, String>();
        for (Document doc : dc) {
            nodeMap.put(doc.g("Nodeid"), doc.g("NodeName"));
        }
        nodeMap.remove(curNodeid); //删除当前环节

        for (String nodeid : nodeMap.keySet()) {
            String nodeName = nodeMap.get(nodeid);
            LinkedHashSet<String> useridSet = Rdb.getValueLinkedSetBySql("select Userid from BPM_InsUserList where DocUnid='" + docUnid + "' and Nodeid='" + nodeid + "'");
            LinkedHashSet<String> userNameSet = new LinkedHashSet<String>();
            if (useridSet.size() > 0) {
                userNameSet = (LinkedHashSet<String>) linkeyUser.getCnName(useridSet);
            }
            optionStr.append("<option value='" + nodeid + "' userid='" + Tools.join(useridSet, ",") + "' userName='" + Tools.join(userNameSet, ",") + "'>" + nodeName + "</option>");
        }

        String htmlCode = "<div style='height:5px'></div><table class='linkeytable' >"
                + "<tr><td class='texttd' width=20%>选项</td><td width=*><input type=radio value='1' name=\"IsBackFlag\" id=\"IsBackFlag_1\" class='lschk' checked  ><label for=\"IsBackFlag_1\">需要用户再次提交审批</label><br><label for=\"IsBackFlag_2\"><input type=radio value='2' class='lschk' id=\"IsBackFlag_2\" name=\"IsBackFlag\" ><label for=\"IsBackFlag_2\">用户修改后直接提交给我</label></td></tr>"
                + "<tr><td class='texttd' nowrap >回退环节</td><td><select id='WF_ReturnNodeid' onChange='getReturnAnyNodeUser()'>" + optionStr.toString() + "</select></td></tr>"
                + "<tr><td class='texttd' >回退用户</td><td><input id='WF_ReturnUserid' style='display:none' ><span id='WF_ReturnUserid_show' class='fieldShow' ></span>"
                + "<a class=\"easyui-linkbutton\" plain=\"true\" data-options=\"iconCls:'icon-userbtn'\" onclick=\"seluser('WF_ReturnUserid');return false;\" ></a></td></tr>" + "</table>";
        BeanCtx.p(htmlCode);
    }

    public void getGoToFirstNode() {
        String htmlCode = "<div style=\"padding-left:30px:padding-top:10px;margin:10px;\">"
                + "<input type=radio value='1' name=\"IsBackFlag\" id=\"IsBackFlag_1\" checked class='lschk' ><label for=\"IsBackFlag_1\">需要用户再次提交审批</label><br>"
                + "<input type=radio value='2' name=\"IsBackFlag\" id=\"IsBackFlag_2\" class='lschk' ><label for=\"IsBackFlag_2\">用户修改后直接提交给我</label>" + "</div>";
        BeanCtx.p(htmlCode);
    }

}