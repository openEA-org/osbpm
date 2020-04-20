package cn.linkey.rulelib.S008;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * @RuleName:流程启动页
 * @author admin
 * @version: 8.0
 * @Created: 2014-05-09 10:11
 */
final public class R_S008_B004 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) {
        //params为运行本规则时所传入的参数
        String ProcessTrStr = "";
        String sql = "select * from BPM_NavTreeNode where Treeid='T_S002_001'";
        Document[] dc = Rdb.getAllDocumentsBySql(sql);
        StringBuilder folderStr = new StringBuilder();
        for (Document doc : dc) {
            ProcessTrStr = GetProcessList(doc.g("Folderid"));
            if (Tools.isNotBlank(ProcessTrStr)) {
                folderStr.append("<div class=\"portlet box grey-cascade\"><div class=\"portlet-title\">");
                folderStr.append("<div class=\"caption\"><i class=\"fa fa-table\"></i>").append(doc.g("FolderName")).append("</div>");
                folderStr.append("<div class=\"tools\"><a href=\"javascript:;\" class=\"collapse\"></a></div></div><div class=\"portlet-body\">");
                folderStr.append(ProcessTrStr);
                folderStr.append("</div></div>");
            }
        }
        if (Tools.isBlank(folderStr.toString())) {
            folderStr.append("未找到本应用的流程...");
        }

        //输出页面
        String htmlCode = "<div class=\"container-fluid\"><div class=\"row clearfix\"><div class=\"col-md-12 column\">".concat(folderStr.toString()).concat("</div></div></div>");
        BeanCtx.p(htmlCode);
        return "";

    }

    public String GetProcessList(String folderid) {
        //获得所有已发布的流程列表
        boolean hasProcess = false;
        String appid = BeanCtx.g("WF_Appid", true);
        String sql = "";
        if (Tools.isBlank(appid)) {
            sql = "select NodeName,Processid,icons from BPM_ModProcessList where Folderid='" + folderid + "' and Status='1'";
        }
        else {
            sql = "select NodeName,Processid,icons from BPM_ModProcessList where Folderid='" + folderid + "' and WF_Appid='" + appid + "' and Status='1'";
        }
        Document[] dc = Rdb.getAllDocumentsBySql(sql);
        int x = 0;
        StringBuilder listStr = new StringBuilder();
        listStr.append("<div class=\"row\">");
        for (Document doc : dc) {
            if (BeanCtx.getLinkeyUser().inRoles(BeanCtx.getUserid(), doc.g("ProcessStarter"))) {
                hasProcess = true;
                x = x + 1;
                String icon = doc.g("icons");
                if (Tools.isBlank(icon)) {
                    icon = "glyphicon glyphicon-user";
                }

                //获得本流程的待办总数
                String num_sql = "select count(*) As TotalNum from BPM_UserTodo where WF_Processid='" + doc.g("Processid") + "'and Userid='" + BeanCtx.getUserid() + "'";
                String count = Rdb.getValueBySql(num_sql);

                listStr.append("<div class=\"col-xs-2\"><div class=\"thumbnail\">");
                listStr.append("<div style=\"background-color:#DEDFDE;text-align:center;\" valign=\"middle\">");
                listStr.append("<span class=\"").append(icon).append("\" style=\"font-size:35px;color:#1BA1E2\"></span>");
                listStr.append("<a href='r?wf_num=V_S005_G015&Processid=").append(doc.g("Processid")).append("' ><span  style=\"background-color:#ef0303;position:absolute;\" class=\"badge\">")
                        .append(count).append("</span></a></div>");
                String url="r?wf_num=R_S008_B006&wf_processid="+doc.g("Processid");
                listStr.append("<div class=\"caption\" align=\"center\"><h4><a href=\"#"+url+"\" onclick=\"loadContentPanel('"+url+"');\" >")
                        .append(doc.g("NodeName")).append("</a></h4></div></div></div>");
            }
            if (x == 6) {
                listStr.append("</div><div class=\"row\">");
                x = 0;
            }
        }
        if (hasProcess == false) {
            return "";
        }
        else {
            return listStr.append("</div>").toString();
        }
    }

}