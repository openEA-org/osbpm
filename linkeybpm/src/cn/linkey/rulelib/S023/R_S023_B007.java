package cn.linkey.rulelib.S023;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * @RuleName:移动终端流程启动页
 * @author admin
 * @version: 8.0
 * @Created: 2014-05-09 10:11
 */
final public class R_S023_B007 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) {
        // params为运行本规则时所传入的参数
        String ProcessTrStr = "";
        String sql = "select * from BPM_NavTreeNode where Treeid='T_S002_001'";
        Document[] dc = Rdb.getAllDocumentsBySql(sql);
        String folderStr = "";
        for (Document doc : dc) {
            ProcessTrStr = GetProcessList(doc.g("Folderid"));
            if (Tools.isNotBlank(ProcessTrStr)) {
                folderStr += "<div class=\"tabbable\" id=\"tabs-707060\" style=\"text-align: center;background-color:#f4f4f4;padding: 7px;\"><a href=\"#panel-939343\" data-toggle=\"tab\" style=\"color:#323232;\">" + doc.g("FolderName")
                        + "</a></div><div style=\"padding:4px\"></div>";
                folderStr += ProcessTrStr;
            }
        }
        if (Tools.isBlank(folderStr)) {
            folderStr = "未找到本应用的流程...";
        }

        // 输出页面
        BeanCtx.p("<!DOCTYPE html><html><head><title>流程启动</title>");
        BeanCtx.p(BeanCtx.getSystemConfig("BootstrapPageHeader"));
        BeanCtx.p("<style>.thumbnail2{min-width:80px}</style>");
        BeanCtx.p("<script type=\"text/javascript\" src=\"linkey/bpm/jscode/sharefunction.js\"></script>");
        BeanCtx.p("<body style=\"margin:0px 0px 0px 0px\"><div style='padding:5px'></div><div class=\"container-fluid\"><div class=\"row clearfix\"><div class=\"col-md-12 column\">");
        BeanCtx.p(folderStr);
        BeanCtx.p("</div></div></div>");
        BeanCtx.p("</body></html>");
        return "";
    }

    public String GetProcessList(String folderid) {
        // 获得所有已发布的流程列表
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
        String listStr = "<div class=\"row\">";
        for (Document doc : dc) {
            if (BeanCtx.getLinkeyUser().inRoles(BeanCtx.getUserid(), doc.g("ProcessStarter"))) {
                hasProcess = true;
                x = x + 1;
                String icon = doc.g("icons");
                if (Tools.isBlank(icon)) {
                    icon = "glyphicon glyphicon-user";
                }
                listStr += "<div class=\"col-xs-4\"><a href=\"rule?wf_num=R_S003_B036&wf_processid=" + doc.g("Processid")
                        + "&mobile=1\" ><div class=\"thumbnail\" style=\"border:none;\">" + "<div style=\"text-align:center;\" valign=\"middle\">" + "<img border=\"0\" src=\"linkey/bpm/images/process/picon/1.png\" align=\"absMiddle\" style=\"margin-bottom: 0px;width: 60px;\"/></div>" + "<div class=\"caption\" align=\"center\" style=\"padding:0;font-size: 13px;\">" + doc.g("NodeName") + "</div></div></a></div>";
            }
            if (x == 6) {
                listStr += "</div><div class=\"row\">";
                x = 0;
            }
        }
        if (hasProcess == false) {
            return "";
        }
        else {
            return listStr + "</div>";
        }
    }

}