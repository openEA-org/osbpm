package cn.linkey.rulelib.S016;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;

/**
 * @RuleName:环节发生概率统计
 * @author admin
 * @version: 8.0
 * @Created: 2014-06-04 15:47
 */
final public class R_S016_B005 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) {
        //params为运行本规则时所传入的参数
        String Processid = BeanCtx.g("processid", true);
        String ProcessName = BeanCtx.g("processname", true);
        String newProcessName = null;
        try {
            //对中文字符进行转码，否则会出现乱码。
            newProcessName = new String(ProcessName.getBytes("iso8859-1"), "utf-8");
        }
        catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        String sHtmlCode = null;
        try {
            sHtmlCode = getHtmlCode(Processid, newProcessName);
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        BeanCtx.p(sHtmlCode);
        return "";
    }

    public String getHtmlCode(String ProcessId, String ProcessName) throws ParseException {
        /**
         * 获取HTMLCode代码
         */
        String sql = "select count(*) as Num from BPM_ArchivedData where WF_Processid='" + ProcessId + "'";
        //获取已归档的文档实例数AdNum。
        String AdNum = Rdb.getValueBySql(sql);
        int DataNum = Integer.parseInt(AdNum);

        /* 获取流程的节点数－－－－－－－－－－－Begin */
        sql = "select Nodeid,NodeName From BPM_ModTaskList where Processid='" + ProcessId + "' order by Nodeid asc";
        Document[] dc = Rdb.getAllDocumentsBySql(sql);
        if (dc.length <= 0) {
            return "";
        }
        ArrayList<String> NodeidArray = new ArrayList<String>();
        ArrayList<String> NodeNameArray = new ArrayList<String>();
        for (Document doc : dc) {
            String Nodeid = doc.g("Nodeid");
            if (Nodeid != null || Nodeid != "") {
                NodeidArray.add(Nodeid);
                NodeNameArray.add(doc.g("NodeName"));
            }
        }
        /* 获取流程的节点数－－－－－－－－－－－End */

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        StringBuffer tempStr = new StringBuffer();
        int ListNum = 0; //序号
        for (int i = 0; i < NodeidArray.size(); i++) {
            //该节点共审批的文档数。
            int NodeNum = 0;
            //该节点审批文档数的总时间。
            long MinTotal = 0;
            //平均完成时间(小时)
            long gValue = 0;
            //发生概率百分比
            String mean = "0";
            //序号，循环加1
            ListNum += 1;
            sql = "select StartTime,EndTime From BPM_ArchivedRemarkList where Processid='" + ProcessId + "' and Nodeid='" + NodeidArray.get(i) + "'";
            Document[] NodeDc = Rdb.getAllDocumentsBySql(sql);
            for (Document doc : NodeDc) {
                NodeNum += 1;
                String StartTime = doc.g("StartTime");
                //System.out.println("StartTime:"+StartTime);
                String EndTime = doc.g("EndTime");
                //System.out.println("EndTime:"+EndTime);
                //获取时间差，表示该环节的处理用时。
                long DifMin = getDifMinByDate(dateFormat.parse(EndTime), dateFormat.parse(StartTime));
                if (DifMin <= 0) {
                    DifMin = 1;
                }
                //累加为总用时。
                MinTotal += DifMin;
            }
            if (NodeNum > 0) {
                //通过总用时，计算平均完成时间，单位小时。
                gValue = (MinTotal / NodeNum) / 60;
            }
            if (DataNum > 0) {
                double dMean = (float) NodeNum / DataNum;
                mean = new DecimalFormat("#0").format(dMean * 100); //发生概率百分比
            }
            //循环追加TR行。
            tempStr.append("<tr>" + "<td>" + ListNum + "</td>" + "<td>" + NodeNameArray.get(i) + "</td>" + "<td>" + gValue + "</td>" + "<td>" + NodeNum + "</td>" + "<td style='color:red'>" + mean
                    + "%</td>" + "</tr>");
        }
        StringBuffer HtmlCode = new StringBuffer();
        //HTML头部
        HtmlCode.append(OutHtmlHead(ProcessName));
        //HTML 内容部分
        HtmlCode.append(tempStr.toString());
        //Html结束部分
        HtmlCode.append("<tr><td colspan=20 align=center >本流程累计办理(" + DataNum + ")个文档</td></tr></table></body></html>");
        //返回
        return HtmlCode.toString();
    }

    public String OutHtmlHead(String ProcessName) {
        /**
         * 输出HTML头部
         */
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日");
        String DayTime = dateFormat.format(new Date());
        String tmpStr = "<html><head>" + "\n" + "<title>" + ProcessName + "环节发生概率统计</title>" + "\n" + "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">" + "\n"
                + "<link rel=\"stylesheet\" type=\"text/css\" href=\"linkey/bpm/css/app_openform.css\" />" + "\n"
                + "<script type=\"text/javascript\" src=\"/linkey/bpm/jscode/sharefunction.js\"></script>" + "\n" + "</head><body>" + "\n" + "<center><b><span style=\"font-size:16px\">(" + ProcessName
                + ")环节发生概率统计</span></b></center>" + "\n" + "<table width=100% border=0 ><tr><td>报表生成者:" + BeanCtx.getUserName() + "，生成日期:" + DayTime + "</td></tr></table>" + "\n"
                + "<table width=100% border=1 cellpadding=2 cellspacing=0 class=linkeytable id=\"mytable\" >" + "\n" + "<tr bgcolor=#f4f4f4 >" + "\n" + "<td width=1% nowrap >序号</td>" + "\n"
                + "<td width=20% nowrap >环节名称</td>" + "\n" + "<td width=10% nowrap >平均完成时间(小时)</td>" + "\n" + "<td width=10% nowrap >节点文档数</td>" + "\n" + "<td width=10% nowrap >发生概率</td>" + "\n"
                + "</tr>";
        return tmpStr;
    }

    public static long getDifMinByDate(Date dts, Date dte) {
        long diff = dts.getTime() - dte.getTime();
        diff = diff / (1000 * 60);
        return diff;
    }
}