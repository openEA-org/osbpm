package cn.linkey.rulelib.S016;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;

/**
 * @RuleName:所有超时未处理的文档
 * @author admin
 * @version: 8.0
 * @Created: 2014-06-03 13:52
 */
final public class R_S016_B002 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) {
        //params为运行本规则时所传入的参数
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日");
        String DayTime = dateFormat.format(new Date());
        String UserName = BeanCtx.getUserName();

        String HtmlCode = "<html>" + "<head>" + "</head>" + "<body text=\"#000000\">" + "<html><head><title>超时未审批文档列表</title>"
                + "<link rel=\"stylesheet\" type=\"text/css\" href=\"linkey/bpm/css/app_openform.css\" />" + "<script type=\"text/javascript\" src=\"linkey/bpm/easyui/jquery.min.js\"></script>"
                + "<script type=\"text/javascript\" src=\"linkey/bpm/jscode/sharefunction.js\"></script>" + "<body style='border:none'>"
                + "<center><b><span style=\"font-size:16px\">所有超时未处理的文档</span></b></center>" + "<table width=100% border=0 ><tr><td>报表生成者:" + UserName + "???生成日期:" + DayTime + "</td></table>"
                + "<table width=100% border=1 cellpadding=2 cellspacing=0 id=\"mytable\">" + "<tr bgcolor=#f4f4f4 >" + "<td width=1% nowrap >序号</td>" + "<td width=3% nowrap >单号</td>"
                + "<td width=20% nowrap >标题</td>" + "<td width=5% nowrap >申请人</td>" + "<td width=5% nowrap >申请时间</td>" + "<td width=5% nowrap >当前状态</td>" + "<td width=5% nowrap >当前审批人</td>"
                + "<td width=5% nowrap >流程</td>" + "<td width=3% nowrap >最后期限</td>" + "<tr>" + "<tr bgcolor='#f4f4f4'><td colspan=30 align=center >共统计到(0)条文档</td></tr></table></body>" + "</body>"
                + "</html>";
        BeanCtx.p(HtmlCode);
        return "123123";
    }

}