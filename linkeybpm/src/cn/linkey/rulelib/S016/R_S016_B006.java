package cn.linkey.rulelib.S016;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.ParseException;
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
final public class R_S016_B006 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) {
        //params为运行本规则时所传入的参数
        String Processid = BeanCtx.g("processid", true);
        String ProcessName = BeanCtx.g("processname", true);
        String StartTime = BeanCtx.g("starttime", true);
        String EndTime = BeanCtx.g("endtime", true);
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
            sHtmlCode = getHtmlCode(Processid, newProcessName, StartTime, EndTime);
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        BeanCtx.p(sHtmlCode);
        return "";
    }

    public String getHtmlCode(String ProcessId, String ProcessName, String StartTime, String EndTime) throws ParseException {
        /**
         * 获取HTMLCode代码
         */
        String sql = "select count(*) as Num from BPM_ArchivedData where WF_Processid='" + ProcessId + "'";
        //获取已归档的文档实例数AdNum。
        String AdNum = Rdb.getValueBySql(sql);
        int DataNum = Integer.parseInt(AdNum);

        /* 获取流程的节点数－－－－－－－－－－－Begin */
        sql = "select Nodeid,NodeName,ExceedTime From BPM_ModTaskList where Processid='" + ProcessId + "' order by Nodeid asc";
        Document[] dc = Rdb.getAllDocumentsBySql(sql);
        if (dc.length <= 0) {
            return "";
        }
        ArrayList<String> NodeidArray = new ArrayList<String>();
        ArrayList<String> NodeNameArray = new ArrayList<String>();
        ArrayList<String> ExceedTimeArray = new ArrayList<String>();
        for (Document doc : dc) {
            String Nodeid = doc.g("Nodeid");
            if (Nodeid != null || Nodeid != "") {
                NodeidArray.add(Nodeid);
                NodeNameArray.add(doc.g("NodeName"));
                ExceedTimeArray.add(doc.g("ExceedTime"));
            }
        }
        /* 获取流程的节点数－－－－－－－－－－－End */

        //SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        //DecimalFormat df = new DecimalFormat("#.00");
        StringBuffer category = new StringBuffer();
        StringBuffer setValue = new StringBuffer();
        StringBuffer ExceedTime = new StringBuffer();
        double MexMean = 20;
        String sMexMean = "20";
        String sWhere = " (convert(datetime,EndTime) between convert(datetime,'" + StartTime + "') and convert(datetime,'" + EndTime + "'))";
        for (int i = 0; i < NodeidArray.size(); i++) {
            //该节点共审批的文档数。
            int NodeNum = 0;
            //该节点审批文档数的总时间。
            long MinTotal = 0;
            //平均完成时间(小时)
            double dMean = 0.0;
            sql = "select StartTime,EndTime,DifTime From BPM_ArchivedRemarkList where Processid='" + ProcessId + "' and Nodeid='" + NodeidArray.get(i) + "' and " + sWhere;
            //			System.out.println(sql);
            Document[] NodeDc = Rdb.getAllDocumentsBySql(sql);
            for (Document doc : NodeDc) {
                NodeNum += 1;
                //String NodeStartTime=doc.g("StartTime");
                //String NodeEndTime=doc.g("EndTime");
                //获取时间差，表示该环节的处理用时。
                //long DifMin=getDifMinByDate(dateFormat.parse(NodeEndTime),dateFormat.parse(NodeStartTime));
                long DifMin = Integer.parseInt(doc.g("DifTime")); //直接使用DifTime获取处理时长。
                if (DifMin <= 0) {
                    DifMin = 1;
                }
                //累加为总使用时间。
                MinTotal += DifMin;
            }
            //节点列表
            category.append("<category name='" + NodeNameArray.get(i) + "' hoverText='" + NodeNameArray.get(i) + "/小时' />");
            if (NodeNum > 0) {
                //通过总用时，计算平均完成时间，单位分钟。
                dMean = (MinTotal / NodeNum);
                //System.out.println("平均分长:"+dMean);
                dMean = dMean / 60;
                //System.out.println("平均时长:"+dMean);
            }
            if (dMean > MexMean) {
                //计算最大值，用于图形展示时设置yAxisMaxValue值。
                MexMean = dMean;
            }
            int Et = Integer.parseInt(ExceedTimeArray.get(i)); //设定的时限
            if (Et > MexMean) {
                //计算最大值，用于图形展示时设置yAxisMaxValue值。
                MexMean = Et;
            }
            String sMean = new DecimalFormat("#.00").format(dMean); //格式化显示。
            sMexMean = new DecimalFormat("#0").format(MexMean); //格式化显示。
            //System.out.println("格式化:"+sMean);
            setValue.append("<set value='" + sMean + "' />");
            ExceedTime.append("<set value='" + Et + "' />");
        }
        //循环结束后，组合Charts图表的XML内容。
        StringBuffer HtmlCodeXml = new StringBuffer("<graph xAxisName='环节名称' yAxisName='平均完成时间'  hovercapbg='DEDEBE' hovercapborder='889E6D' " + "rotateNames='0' yAxisMaxValue='" + sMexMean
                + "' numdivlines='9' divLineColor='CCCCCC' divLineAlpha='80' decimalPrecision='0' " + "showAlternateHGridColor='1' AlternateHGridAlpha='30' AlternateHGridColor='CCCCCC' >");
        HtmlCodeXml.append("<categories font='宋体' fontSize='11' fontColor='000000'>" + category.toString() + "</categories>");
        HtmlCodeXml.append("<dataset seriesname='平均完成时间' color='FDC12E'>" + setValue.toString() + "</dataset>");
        HtmlCodeXml.append("<dataset seriesname='设定时限'  color='56B9F9'>" + ExceedTime.toString() + "</dataset>");
        HtmlCodeXml.append("</graph>");
        int height = NodeidArray.size() * 80;
        //组合HTML内容
        StringBuffer HtmlCode = new StringBuffer();
        HtmlCode.append(OutHtmlHead(ProcessName, HtmlCodeXml.toString(), height));
        //返回
        return HtmlCode.toString();
    }

    public String OutHtmlHead(String ProcessName, String CodeXml, int height) {
        /**
         * 输出HTML
         */
        String tmpStr = "<html><head><title>(" + ProcessName + ")每环节平均完成时间</title></head>" + "\n" + "<script language=\"JavaScript\" src=\"linkey/bpm/charts/FusionCharts.js\"></script>" + "\n"
                + "<body style=\"border:none\" >" + "\n" + "<center><b><font color=green size=\"2\">(" + ProcessName + ")每环节平均完成时间/小时</font></b></center>" + "\n"
                + "<div id=\"chartdiv\" align=center ></div>" + "\n" + "<script>" + "\n" + "var cWidth=document.body.clientWidth-100;" + "\n" + "var cHeight=\"" + height + "\";" + "\n"
                + "var chart = new FusionCharts(\"linkey/bpm/charts/FCF_MSBar2D.swf\", \"ChartId\", cWidth, cHeight);" + "\n" + "chart.setDataXML(\"" + CodeXml + "\");" + "\n"
                + "chart.render(\"chartdiv\");" + "\n" + "</script></body></html>";
        return tmpStr;
    }

    public static long getDifMinByDate(Date dts, Date dte) {
        long diff = dts.getTime() - dte.getTime();
        diff = diff / (1000 * 60);
        return diff;
    }
}