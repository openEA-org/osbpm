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
final public class R_S016_B016 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        //params为运行本规则时所传入的参数
        //String processid="a8bb004c00173041060b5d502bada1435268";
        String processid = BeanCtx.g("processid");
        String processsql = "select nodename from bpm_modprocesslist where processid='" + processid + "'";
        String header = "<!DOCTYPE html><html><title>流程与人员</title><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">"
                + "<link rel=\"stylesheet\" type=\"text/css\" href=\"linkey/bpm/easyui/themes/gray/easyui.css\">"
                + "<link rel=\"stylesheet\" type=\"text/css\" href=\"linkey/bpm/easyui/themes/icon.css\">" 
                + "<link rel=\"stylesheet\" type=\"text/css\" href=\"linkey/bpm/css/engine_openform.css\">"
                
				// 20190107 样式修复
                + "<link rel=\"stylesheet\" type=\"text/css\" href=\"linkey/bpm/css/app_openform.css\">";
                // 20190107 END
        
        String html = "<body><table width=\"100%\"><tr><td align=\"center\" style=\"font-size:28px;font-family:\"宋体\"\">流程与人员</td></tr><tr><td align=\"left\" style=\"color:#0000FF;font-size:20px;font-family:\"宋体\"\">"
                + Rdb.getDocumentBySql(processsql).g("nodename") + "  :" + "<td></tr></table><table class=\"linkeytable\" data-sort=\"sortDisabled\"><tbody> <tr><td align=\"center\"> 流程环节 ↓</td>";
        String lasthtml = "</tbody></table></body></html>";
        String rolesql = "select distinct ActionUserid from BPM_InsNodeList where processid='" + processid + "'";
        String nodenamesql = "select distinct nodename,nodeid  from BPM_InsNodeList where (Nodeid like '%T%' or Nodeid like '%p%') and Processid='" + processid + "'";
        String usersql = "select distinct nodename,actionuserid  from BPM_InsNodeList where Processid='" + processid + "'";
        String rolenumber = "";
        Document[] roledoc = Rdb.getAllDocumentsBySql(rolesql);
        ArrayList<String> str = new ArrayList<String>();
        for (Document doc : roledoc) {

            html = html + "<td align=\"center\">" + BeanCtx.getLinkeyUser().getCnName(doc.g("actionuserid")) + "</td>";
            str.add(doc.g("actionuserid"));
        }
        html = html + "</tr><tr>";
        for (Document doc : Rdb.getAllDocumentsBySql(nodenamesql)) {

            String temphtml = "";
            usersql = "select distinct nodename,actionuserid  from BPM_InsNodeList where Processid='" + processid + "' and nodename='" + doc.g("nodename") + "'";
            html = html + "<td align=\"center\">" + doc.g("nodename") + "(" + doc.g("nodeid") + ")" + "</td>";

            for (int i = 0; i < str.size(); i++) {
                boolean in = false;
                for (Document doc1 : Rdb.getAllDocumentsBySql(usersql)) {
                    //String rolenumbersql="select * from BPM_OrgRoleMembers where member='"+doc1.g("actionuserid")+"'";
                    // for(Document doc2:Rdb.getAllDocumentsBySql(rolenumbersql))
                    // {
                    if (doc1.g("actionuserid").equals(str.get(i)) && !in) {
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
        BeanCtx.p(header + html + lasthtml);
        return "";
    }
}