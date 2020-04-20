package cn.linkey.rulelib.S016;

import java.util.ArrayList;
import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;

/**
 * @RuleName:流程与岗位(table)
 * @author admin
 * @version: 8.0
 * @Created: 2014-10-28 16:58
 */
final public class R_S016_B017 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        //params为运行本规则时所传入的参数
        //String processid="9769ff3f01fc8049a6099fc042b7fb56";
        String processid = BeanCtx.g("processid");
        String processsql = "select nodename from bpm_modprocesslist where processid='" + processid + "'";
        String header = "<!DOCTYPE html><html><title>流程与规则</title><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">"
                + "<link rel=\"stylesheet\" type=\"text/css\" href=\"linkey/bpm/easyui/themes/gray/easyui.css\">"
                + "<link rel=\"stylesheet\" type=\"text/css\" href=\"linkey/bpm/easyui/themes/icon.css\">" + "<link rel=\"stylesheet\" type=\"text/css\" href=\"linkey/bpm/css/engine_openform.css\">";
        String html = "<body><table width=\"100%\"><tr><td align=\"center\" style=\"font-size:28px;font-family:\"宋体\"\">流程与规则</td></tr><tr><td align=\"left\" style=\"color:#0000FF;font-size:20px;font-family:\"宋体\"\">"
                + Rdb.getDocumentBySql(processsql).g("nodename") + "  :" + "<td></tr></table><table class=\"linkeytable\" data-sort=\"sortDisabled\"><tbody> <tr><td> 流程环节 ↓ /  规则名 →</td>";
        String lasthtml = "</tbody></table></body></html>";
        String rulesql = "select * from BPM_EngineEventConfig where processid='" + processid + "'";
        String rulenamesql = "";
        String nodenamesql = "select distinct nodename  from BPM_InsNodeList where Processid='" + processid + "'";
        String rulenumsql = "select nodename,Nodeid from BPM_ModSequenceFlowList where Processid='" + processid + "' union  (select nodename,Nodeid from BPM_ModEventList where processid='" + processid
                + "') union (select nodename,nodeid from BPM_ModGatewayList where processid='" + processid + "') union (select nodename,Nodeid from BPM_ModTaskList where Processid='" + processid
                + "')";
        String rolenumber = "";
        Document[] ruledoc = Rdb.getAllDocumentsBySql(rulesql);
        ArrayList<String> str = new ArrayList<String>();
        for (Document doc : ruledoc) {
            rulenamesql = "select RuleName from BPM_RuleList where rulenum='" + doc.g("rulenum") + "'";

            html = html + "<td align=\"center\">" + Rdb.getDocumentBySql(rulenamesql).g("rulename") + "</td>";
            str.add(doc.g("rulenum"));

        }
        html = html + "</tr><tr>";
        html = html + "<td>流程属性</td>";
        for (int i = 0; i < str.size(); i++) {
            String processrulesql = "select * from bpm_engineEventConfig where processid='" + processid + "' and rulenum='" + str.get(i) + "'";
            if (Rdb.getCountBySql(processrulesql) > 0) {
                html = html + "<td align=\"center\" style=\"color:#FF0000;background:#EDEDED\">√</td>";
            }
            else {
                html = html + "<td align=\"center\">×</td>";
            }
        }
        html = html + "</tr>";
        for (Document doc : Rdb.getAllDocumentsBySql(rulenumsql)) {

            String temphtml = "";
            //usersql="select distinct nodename,actionuserid  from BPM_InsNodeList where Processid='"+processid+"' and nodename='"+doc.g("nodename")+"'";
            if (doc.g("nodename").length() > 0) {
                html = html + "<td>" + doc.g("nodename") + " (" + doc.g("nodeid") + ")" + "</td>";
                rulenamesql = "select * from BPM_EngineEventConfig where nodeid='" + doc.g("nodeid") + "' and processid='" + processid + "'";
                for (int i = 0; i < str.size(); i++) {
                    boolean in = false;
                    for (Document doc1 : Rdb.getAllDocumentsBySql(rulenamesql)) {
                        //String rolenumbersql="select * from BPM_OrgRoleMembers where member='"+doc1.g("actionuserid")+"'";
                        // for(Document doc2:Rdb.getAllDocumentsBySql(rolenumbersql))
                        // {
                        if (doc1.g("rulenum").equals(str.get(i)) && !in) {
                            html = html + "<td align=\"center\" style=\"color:#FF0000;background:#EDEDED\">√</td>";
                            in = true;
                        }

                        // }

                    }
                    if (!in) {
                        html = html + "<td align=\"center\">×</td>";
                    }
                }
                html = html + "</tr>";
            }
        }
        BeanCtx.p(header + html + lasthtml);
        return "";
    }
}