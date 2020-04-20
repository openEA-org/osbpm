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
 * 流程与规则(table)V2
 *
 * @author: alibao
 *
 * Modification History:
 * Date         Author          Version            Description
 *---------------------------------------------------------*
 * 2018年9月7日     alibao           v1.0.0              调整样式
 */
final public class R_S016_B020 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        //params为运行本规则时所传入的参数
        //String processid=BeanCtx.g("processid");
        // String processsql="select nodename from bpm_modprocesslist where processid='"+processid+"'";
    	String header = "<!DOCTYPE html><html><title>流程与规则</title><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">"
        		//20180907 修改，主题样式 @alibao
                + "<link rel=\"stylesheet\" id=\"bpm_theme\" type=\"text/css\" href=\"linkey/bpm/easyui/newtheme/green/devclient.css\">"
                //+ "<link rel=\"stylesheet\" type=\"text/css\" href=\"linkey/bpm/easyui/themes/gray/easyui.css\">"
                + "<link rel=\"stylesheet\" type=\"text/css\" href=\"linkey/bpm/easyui/themes/icon.css\">" + "<link rel=\"stylesheet\" type=\"text/css\" href=\"linkey/bpm/css/engine_openform.css\">";
        String html = "<body><table width=\"100%\"><tr><td align=\"center\" style=\"font-size:28px;font-family:\"宋体\"\">流程与规则</td></tr><tr><td align=\"left\" style=\"color:#0000FF;font-size:20px;font-family:\"宋体\"\">"
                + "</td></tr></table><table class=\"linkeytable\" data-sort=\"sortDisabled\"><tbody> <tr><td align=\"center\"> 流程名 ↓ </td><td align=\"center\">引用的规则</td>";
        String lasthtml = "</tbody></table></body></html>";
        String processsql = "select * from bpm_modprocesslist ";

        //String nodenamesql="select distinct nodename  from BPM_InsNodeList where Processid='"+processid+"'";
        // String rulenumsql="select nodename,Nodeid from BPM_ModSequenceFlowList where Processid='"+processid+"' union  (select nodename,Nodeid from BPM_ModEventList where processid='"+processid+"') union (select nodename,nodeid from BPM_ModGatewayList where processid='"+processid+"') union (select nodename,Nodeid from BPM_ModTaskList where Processid='"+processid+"')";
        String rolenumber = "";
        ArrayList<String> str = new ArrayList<String>();
        html = html + "</tr><tr>";
        for (Document doc : Rdb.getAllDocumentsBySql(processsql)) {

            String rulehtml = "";
            String subformhtml = "";
            boolean hasrulenumber = false;
            boolean hassubform = false;
            String rulesql = "select distinct Processid,RuleNum from bpm_engineEventConfig where processid='" + doc.g("processid") + "'";
            //String tasksql="select * from BPM_ModTaskList where processid='"+doc.g("processid")+"'";
            String rulenamesql = "";
            html = html + "<td align=\"center\">" + doc.g("nodename") + "</td>";

            for (Document doc1 : Rdb.getAllDocumentsBySql(rulesql)) {
                rulenamesql = "select rulename,rulenum from bpm_rulelist where rulenum='" + doc1.g("rulenum") + "'";
                if (!Tools.isBlank(Rdb.getDocumentBySql(rulenamesql).g("rulename"))) {
                    rulehtml = rulehtml + Rdb.getDocumentBySql(rulenamesql).g("rulename") + "(" + Rdb.getDocumentBySql(rulenamesql).g("rulenum") + ")" + "、";
                    hasrulenumber = true;
                }

            }
            if (hasrulenumber) {
                rulehtml = rulehtml.substring(0, rulehtml.length() - 1);
                //20180907 修改，主题样式 @alibao
                html = html + "<td align=\"center\" style=\"color:#FF0000;\">" + rulehtml + "</td>";
                //html = html + "<td align=\"center\" style=\"color:#FF0000;background:#EDEDED\">" + rulehtml + "</td>";
            }
            else
                html = html + "<td align=\"center\">×</td>";

            html = html + "</tr>";
        }

        BeanCtx.p(header + html + lasthtml);
        return "";
    }
}