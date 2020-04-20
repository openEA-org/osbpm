package cn.linkey.rulelib.S016;

import java.util.ArrayList;
import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;

/**
 * 
 * 流程与表单
 *
 * @author: alibao
 *
 * Modification History:
 * Date         Author          Version            Description
 *---------------------------------------------------------*
 * 2018年9月7日     alibao           v1.0.0               修改原因
 */
final public class R_S016_B018 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        //params为运行本规则时所传入的参数
        //String processid="9769ff3f01fc8049a6099fc042b7fb56";
        String processid = BeanCtx.g("processid");

        // String processsql="select nodename from bpm_modprocesslist where processid='"+processid+"'";
        String header = "<!DOCTYPE html><html><title>流程与表单</title><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">"
        		//20180907 修改，主题样式 @alibao
                + "<link rel=\"stylesheet\" id=\"bpm_theme\" type=\"text/css\" href=\"linkey/bpm/easyui/newtheme/green/devclient.css\">"
                //+ "<link rel=\"stylesheet\" type=\"text/css\" href=\"linkey/bpm/easyui/themes/gray/easyui.css\">"
                + "<link rel=\"stylesheet\" type=\"text/css\" href=\"linkey/bpm/easyui/themes/icon.css\">" + "<link rel=\"stylesheet\" type=\"text/css\" href=\"linkey/bpm/css/engine_openform.css\">";
        String html = "<body><table width=\"100%\"><tr><td align=\"center\" style=\"font-size:28px;font-family:\"宋体\"\">流程与表单</td></tr><tr><td align=\"left\" style=\"color:#0000FF;font-size:20px;font-family:\"宋体\"\">"
                + "</td></tr></table><table class=\"linkeytable\" data-sort=\"sortDisabled\"><tbody> <tr><td align=\"center\"> 流程名 ↓ </td><td align=\"center\">引用的主表单</td><td  align=\"center\">引用的移动端表单</td><td  align=\"center\">更换的主表单</td><td  align=\"center\">引用的子表单</td>";
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

            String formhtml = "";
            String subformhtml = "";
            boolean hasformnumber = false;
            boolean hassubform = false;
            String formname = "select formname from bpm_formlist where formnumber='";
            String tasksql = "select * from BPM_ModTaskList where processid='" + doc.g("processid") + "'";
            html = html + "<td align=\"center\">" + doc.g("nodename") + "</td>";
            if (Rdb.getCountBySql(formname + doc.g("formnumber") + "'") > 0) {
                html = html + "<td align=\"center\" style=\"color:#FF0000;background:#EDEDED\">" + Rdb.getDocumentBySql(formname + doc.g("formnumber") + "'").g("formname") + "</td>";
            }
            else
                html = html + "<td align=\"center\">×</td>";
            if (Rdb.getCountBySql(formname + doc.g("formnumberformobile") + "'") > 0) {
                html = html + "<td align=\"center\" style=\"color:#FF0000;background:#EDEDED\">" + Rdb.getDocumentBySql(formname + doc.g("formnumber") + "'").g("formname") + "</td>";
            }
            else
                html = html + "<td align=\"center\">×</td>";
            for (Document doc1 : Rdb.getAllDocumentsBySql(tasksql)) {
                if (doc1.g("formnumber").length() > 0) {
                    formhtml = formhtml + Rdb.getDocumentBySql(formname + doc1.g("formnumber") + "'").g("formname") + "、";
                    hasformnumber = true;
                }
                if (doc1.g("subformnumberload").length() > 0) {
                    subformhtml = subformhtml + Rdb.getDocumentBySql(formname + doc1.g("subformnumberload") + "'").g("formname") + "、";
                    hassubform = true;
                }
            }
            if (hasformnumber) {
                formhtml = formhtml.substring(0, formhtml.length() - 1);
                html = html + "<td align=\"center\" style=\"color:#FF0000;background:#EDEDED\">" + formhtml + "</td>";
            }
            else
                html = html + "<td align=\"center\">×</td>";
            if (hassubform) {
                subformhtml = subformhtml.substring(0, subformhtml.length() - 1);
                //20180907 修改，主题样式 @alibao
                html = html + "<td align=\"center\" style=\"color:#FF0000;\">" + subformhtml + "</td>";
                //html = html + "<td align=\"center\" style=\"color:#FF0000;background:#EDEDED\">" + subformhtml + "</td>";
            }
            else
                html = html + "<td align=\"center\">×</td>";
            html = html + "</tr>";
        }

        BeanCtx.p(header + html + lasthtml);
        return "";
    }
}