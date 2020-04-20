package cn.linkey.rulelib.S016;

import java.util.ArrayList;
import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;

/**
 * @RuleName:流程与部门(table)
 * @author admin
 * @version: 8.0
 * @Created: 2014-10-28 14:03
 */
final public class R_S016_B013 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        //params为运行本规则时所传入的参数
        //String processid="f217a2fe03e52048d10ba0b0950eec57";
        String processid = BeanCtx.g("processid");
        String processsql = "select nodename from bpm_modprocesslist where processid='" + processid + "'";
        String header = "<!DOCTYPE html><html><title>流程与部门</title><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">"
                + "<link rel=\"stylesheet\" type=\"text/css\" href=\"linkey/bpm/easyui/themes/gray/easyui.css\">"
                + "<link rel=\"stylesheet\" type=\"text/css\" href=\"linkey/bpm/easyui/themes/icon.css\">" 
                + "<link rel=\"stylesheet\" type=\"text/css\" href=\"linkey/bpm/css/engine_openform.css\">"
                
                // 20190107 样式修复
                + "<link rel=\"stylesheet\" type=\"text/css\" href=\"linkey/bpm/css/app_openform.css\">";
                // 20190107 END
        
        String html = "<body><table width=\"100%\"><tr><td align=\"center\" style=\"font-size:28px;font-family:\"宋体\"\">流程与部门</td></tr><tr><td align=\"left\" style=\"color:#0000FF;font-size:20px;font-family:\"宋体\"\">"
                + Rdb.getDocumentBySql(processsql).g("nodename") + "  :" + "<td></tr></table><table class=\"linkeytable\" data-sort=\"sortDisabled\"><tbody> <tr><td align=\"center\"> 流程环节 ↓</td>";
        String lasthtml = "</tbody></table></body></html>";
        String deptsql = "select FolderName from BPM_OrgDeptList where OrgClass='1'";
        String nodenamesql = "select distinct nodename,ActionUserid,nodeid  from BPM_InsNodeList where Nodeid like 'T%'  and Processid='" + processid + "'";
        String department = "";
        Document[] deptdoc = Rdb.getAllDocumentsBySql(nodenamesql);
        ArrayList<String> str = new ArrayList<String>();
        for (Document doc : deptdoc) {

            department = BeanCtx.getLinkeyUser().getDeptNameByUserid(doc.g("actionuserid"), false);
            if (str.indexOf(department) == -1) {
                html = html + "<td align=\"center\">" + department + "</td>";
                str.add(department);
            }
        }
        html = html + "</tr><tr>";
        for (Document doc : Rdb.getAllDocumentsBySql(nodenamesql)) {
            department = BeanCtx.getLinkeyUser().getDeptNameByUserid(doc.g("actionuserid"), false);
            html = html + "<td align=\"center\">" + doc.g("nodename") + "(" + doc.g("nodeid") + ")" + "</td>";
            for (int i = 0; i < str.size(); i++) {
                if (department.equals(str.get(i))) {
                    html = html + "<td align=\"center\" style=\"color:#FF0000;background:#EDEDED\">" + BeanCtx.getLinkeyUser().getCnName(doc.g("actionuserid")) + "</td>";
                }
                else
                    html = html + "<td align=\"center\">×</td>";
            }
            html = html + "</tr>";
        }
        BeanCtx.p(header + html + lasthtml);
        return "";
    }
}