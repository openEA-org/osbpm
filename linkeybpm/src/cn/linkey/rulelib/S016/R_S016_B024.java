package cn.linkey.rulelib.S016;

import java.util.ArrayList;
import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * 
 * 流程所有者分析
 *
 * @author: alibao
 *
 * Modification History:
 * Date         Author          Version            Description
 *---------------------------------------------------------*
 * 2014-11-10 09:46
 * 2018年9月7日     alibao           v1.0.0           修改样式
 */
final public class R_S016_B024 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        //params为运行本规则时所传入的参数
        String processid = BeanCtx.g("processid");

        // String processsql="select nodename from bpm_modprocesslist where processid='"+processid+"'";
        String header = "<!DOCTYPE html><html><title>流程所有者分析</title><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">"
        		//20180725 修改，主题样式 @alibao
                + "<link rel=\"stylesheet\" id=\"bpm_theme\" type=\"text/css\" href=\"linkey/bpm/easyui/newtheme/green/devclient.css\">"
                //+ "<link rel=\"stylesheet\" type=\"text/css\" href=\"linkey/bpm/easyui/themes/gray/easyui.css\">"
                
                + "<link rel=\"stylesheet\" type=\"text/css\" href=\"linkey/bpm/easyui/themes/icon.css\">" + "<link rel=\"stylesheet\" type=\"text/css\" href=\"linkey/bpm/css/engine_openform.css\">";
        String html = "<body><table width=\"100%\"><tr><td align=\"center\" style=\"font-size:28px;font-family:\"宋体\"\">流程所有者</td></tr><tr><td align=\"left\" style=\"color:#0000FF;font-size:20px;font-family:\"宋体\"\">"
                + "</td></tr></table><table class=\"linkeytable\" data-sort=\"sortDisabled\"><tbody> <tr><td align=\"center\"> 流程名 ↓ </td><td align=\"center\">流程所有者</td>";
        String lasthtml = "</tbody></table></body></html>";
        String processsql = "select * from bpm_modprocesslist ";
        String rulenamesql = "";
        String nodenamesql = "select distinct nodename  from BPM_InsNodeList where Processid='" + processid + "'";
        String rulenumsql = "select nodename,Nodeid from BPM_ModSequenceFlowList where Processid='" + processid + "' union  (select nodename,Nodeid from BPM_ModEventList where processid='" + processid
                + "') union (select nodename,nodeid from BPM_ModGatewayList where processid='" + processid + "') union (select nodename,Nodeid from BPM_ModTaskList where Processid='" + processid
                + "')";
        String rolenumber = "";
        ArrayList<String> str = new ArrayList<String>();
        html = html + "</tr><tr>";
        for (Document doc : Rdb.getAllDocumentsBySql(processsql)) {

            String namehtml = "";
            String subformhtml = "";
            boolean hasformnumber = false;
            boolean hassubform = false;
            String formname = "select formname from bpm_formlist where formnumber='";
            String tasksql = "select * from BPM_ModTaskList where processid='" + doc.g("processid") + "'";
            html = html + "<td align=\"center\">" + doc.g("nodename") + "</td>";
            String owner = doc.g("Processowner");
            if (!Tools.isBlank(owner)) {
                String[] ownerstring = Tools.split(owner, ",");
                for (String subname : ownerstring) {
                    if (subname.indexOf("RS") != -1 || subname.indexOf("GS") != -1) {
                        String namesql = "select * from BPM_OrgRoleMembers where RoleNumber='" + subname + "'";
                        for (Document doc1 : Rdb.getAllDocumentsBySql(namesql)) {
                            namehtml = namehtml + doc1.g("membername") + ",";
                        }
                    }
                    else {
                        String namesql = "select * from bpm_orguserlist where userid='" + subname + "'";
                        namehtml = namehtml + Rdb.getDocumentBySql(namesql).g("cnName") + ",";
                    }
                }
                namehtml = namehtml.substring(0, namehtml.length() - 1);
                //20180725 修改，主题样式 @alibao
                html = html + "<td align=\"center\" style=\"color:#FF0000;\">" + namehtml + "</td>";
                //html = html + "<td align=\"center\" style=\"color:#FF0000;background:#EDEDED\">" + namehtml + "</td>";
            }
            else
                html = html + "<td align=\"center\" >" + "无" + "</td>";

            html = html + "</tr>";
        }

        BeanCtx.p(header + html + lasthtml);
        return "";
    }
}