package cn.linkey.rulelib.S015;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * @RuleName:显示方法的详细帮助信息
 * @author admin
 * @version: 8.0
 * @Created: 2014-05-21 14:01
 */
final public class R_S015_B001 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) {
        // params为运行本规则时所传入的参数
        String folderid = BeanCtx.g("Folderid");
        String sql = "select * from BPM_SystemHelp where Folderid='" + folderid + "'";
        Document doc = Rdb.getDocumentBySql(sql);
        String methodRemark = doc.g("MethodRemark");
        methodRemark = methodRemark.replace("\n", "<br>");
        methodRemark = methodRemark.replace("\r", "<br>");
        if (Tools.isBlank(methodRemark)) {
            methodRemark = "-无-";
        }

        String methodName = doc.g("MethodName");
        if (Tools.isBlank(methodName)) {
            methodName = "-无-";
        }

        String htmlStr = "<style>.linkeytable{border-collapse:collapse;border: 1px solid #e8e8e8;width:100%;font-size:9pt;}"
                + ".linkeytable td{border-collapse:collapse;border: 1px solid #e8e8e8;padding:4px;font-size:9pt;}"
                + ".linkeytable td.texttd{text-align:right;background:#f9f9f9;font-size:9pt;}</style>"
                + "<div style='height:2px'></div><table style='' width='100%' cellpadding=4 cellspacing=0 class='linkeytable' ><tr>"
                + "<td nowrap class='texttd' width='15%'>方法名称</td><td style='color:blue;'>" + methodName + "</td></tr>" + "<tr><td class='texttd'>方法说明</td><td>" + methodRemark + "</td></tr>"
                + "</table>";
        BeanCtx.p(htmlStr);

        return "";
    }
}