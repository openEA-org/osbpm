package cn.linkey.rulelib.S016;

import java.text.DecimalFormat;
import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;

/**
 * @RuleName:每月办理数统计
 * @author admin
 * @version: 8.0
 * @Created: 2014-06-03 17:14
 */
final public class R_S016_B004 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) {
        //params为运行本规则时所传入的参数
        String sHtml = "";
        String sYear = BeanCtx.g("year");
        sHtml = getHtmlCode(sYear);
        BeanCtx.p(sHtml);
        return "";
    }

    public String getHtmlCode(String sYear) {
        String graph = "<graph  xAxisName='" + sYear + "年  月份' yAxisName='处理数' decimalPrecision='0' formatNumberScale='0'>";
        StringBuffer tempStr = new StringBuffer(graph);
        String color = null;
        for (int i = 1; i <= 12; i++) {
            if (i == 1)
                color = "AFD8F8";
            if (i == 2)
                color = "F6BD0F";
            if (i == 3)
                color = "8BBA00";
            if (i == 4)
                color = "FF8E46";
            if (i == 5)
                color = "008E8E";
            if (i == 6)
                color = "D64646";
            if (i == 7)
                color = "8E468E";
            if (i == 8)
                color = "588526";
            if (i == 9)
                color = "B3AA00";
            if (i == 10)
                color = "008ED6";
            if (i == 11)
                color = "9D080D";
            if (i == 12)
                color = "A186BE";
            String sMM = new DecimalFormat("00").format(i);
            String sDateTime = sYear + "-" + sMM;
            String sql = "select count(*) as Num from BPM_ArchivedData where WF_EndTime like '" + sDateTime + "%'";
            String NumValue = Rdb.getValueBySql(sql);
            tempStr.append("<set name='" + sMM + "月' value='" + NumValue + "' color='" + color + "' />");
        }
        tempStr.append("</graph>");
        String HtmlCode = "<html>" + "\n" + "<head>" + "\n" + "</head>" + "\n" + "<body text=\"#000000\">" + "\n" + "<title>图形统计</title>" + "\n"
                + "<link rel=\"stylesheet\" type=\"text/css\" href=\"linkey/bpm/css/app_openform.css\" />" + "\n"
                + "<script language=\"JavaScript\" src=\"linkey/bpm/charts/FusionCharts.js\"></script>" + "\n" + "<body style='border:none'>" + "\n" + "<center><b>所有流程每月处理量统计</b></center>" + "\n"
                + "<div id=\"chartdiv\" align=center ></div>" + "\n" + "<script>" + "\n" + "var chart = new FusionCharts(\"linkey/bpm/charts/FCF_Column2D.swf\", \"ChartId\", \"800\", \"500\");" + "\n"
                + "chart.setDataXML(\"" + tempStr.toString() + "\");" + "\n" + "chart.render(\"chartdiv\");" + "\n" + "</script>" + "\n" + "</body></html>" + "\n";
        return HtmlCode;
    }
}