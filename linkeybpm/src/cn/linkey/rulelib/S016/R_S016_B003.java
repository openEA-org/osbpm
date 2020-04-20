package cn.linkey.rulelib.S016;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;

/**
 * @RuleName:流程运行天数分布图
 * @author admin
 * @version: 8.0
 * @Created: 2014-06-03 15:31
 */
final public class R_S016_B003 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) {
        //params为运行本规则时所传入的参数
        String sHtml = "";
        try {
            sHtml = getHtmlCode();
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        BeanCtx.p(sHtml);
        return "";
    }

    public String getHtmlCode() throws ParseException {
        int day3 = 0, day6 = 0, day10 = 0, day15 = 0, day20 = 0, day30 = 0, day60 = 0, dayMax = 0;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String sql = "select WF_DocCreated From BPM_MainData";
        Document[] dc = Rdb.getAllDocumentsBySql(sql);
        for (Document doc : dc) {
            String sDateTime = doc.g("WF_DocCreated");
            if (sDateTime != "" || sDateTime != null) {
                long dMin = getDifMinByDate(new Date(), dateFormat.parse(sDateTime));
                int DayNum = (int) dMin / 60 / 24;
                if (DayNum < 4) { //1-3天的
                    day3 += 1;
                }
                else if (DayNum < 7) { //4-6天的
                    day6 += 1;
                }
                else if (DayNum < 10) { //7-10天的
                    day10 += 1;
                }
                else if (DayNum < 16) { //10-15天的
                    day15 += 1;
                }
                else if (DayNum < 21) { //15-20天的
                    day20 += 1;
                }
                else if (DayNum < 31) { //21-30
                    day30 += 1;
                }
                else if (DayNum < 60) { //31-60天的
                    day60 += 1;
                }
                else {
                    dayMax += 1;
                }
            }
        }
        String tempStr = "<graph showNames='1' decimalPrecision='0' formatNumberScale='0'>" + "<set name='1-3天未处理文档数' value='" + day3 + "' />" + "<set name='4-6天未处理文档数' value='" + day6 + "' />"
                + "<set name='7-10天未处理文档数' value='" + day10 + "' />" + "<set name='10-15天未处理文档数' value='" + day15 + "' />" + "<set name='15-20天未处理文档数' value='" + day20 + "' />"
                + "<set name='21-30天未处理文档数' value='" + day30 + "' />" + "<set name='31-60天未处理文档数' value='" + day60 + "' />" + "<set name='大于60天未处理文档数' value='" + dayMax + "' />" + "</graph>";

        String HtmlCode = "<!DOCTYPE html><html>" + "\n" + "<head>" + "\n" + "</head>" + "\n" + "<body text=\"#000000\">" + "\n" + "<title>图形统计</title>" + "\n"
                + "<link rel=\"stylesheet\" type=\"text/css\" href=\"linkey/bpm/css/app_openform.css\" />" + "\n"
                + "<script language=\"JavaScript\" src=\"linkey/bpm/charts/FusionCharts.js\"></script>" + "\n" + "<body style='border:none'>" + "\n" + "<center><b>流程运行天数分布图</b></center>" + "\n"
                + "<div id=\"chartdiv\" align=center ></div>" + "\n" + "<script>" + "\n" + "//alert('123');" + "\n"
                + "var chart = new FusionCharts(\"linkey/bpm/charts/FCF_Doughnut2D.swf\", \"ChartId\", \"800\", \"500\");" + "\n" + "chart.setDataXML(\"" + tempStr + "\");" + "\n"
                + "chart.render(\"chartdiv\");" + "\n" + "</script>" + "\n" + "</body></html>" + "\n";
        return HtmlCode;
    }

    public static long getDifMinByDate(Date dts, Date dte) {
        long diff = dts.getTime() - dte.getTime();
        diff = diff / (1000 * 60);
        return diff;
    }
}