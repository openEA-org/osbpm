package cn.linkey.rulelib.S005;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * @RuleName:流程启动页
 * @author admin
 * @version: 8.0
 * @Created: 2014-05-09 10:11
 */
final public class R_S005_B005 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) {
        //params为运行本规则时所传入的参数
        String ProcessTrStr = "";
        String[] imageTitle = new String[5];
        imageTitle[0] = "BlueTitle.jpg";
        imageTitle[1] = "YellowTitle.jpg";
        imageTitle[2] = "RedTitle.jpg";
        imageTitle[3] = "GreenTitle.jpg";
        imageTitle[4] = "PurpleTitle.jpg";
        String[] imageArray = new String[5];
        imageArray[0] = "border:1px solid #98D0E0;background:#F7FAFD url(linkey/bpm/images/process/processtype/BlueBackground.jpg) repeat-x top;";
        imageArray[1] = "border:1px solid #D1BD94;background:#FEFDF9 url(linkey/bpm/images/process/processtype/YellowBackground.jpg) repeat-x top;";
        imageArray[2] = "border:1px solid #DCAEAB;background:#FEFBFB url(linkey/bpm/images/process/processtype/RedBackground.jpg) repeat-x top;";
        imageArray[3] = "border:1px solid #A8CC9B;background:#FBFEFB url(linkey/bpm/images/process/processtype/GreenBackground.jpg) repeat-x top;";
        imageArray[4] = "border:1px solid #BDB0DB;background:#F7FAFC url(linkey/bpm/images/process/processtype/PurpleBackground.jpg) repeat-x top;";

        String sql = "select * from BPM_NavTreeNode where Treeid='T_S002_001'";
        Document[] dc = Rdb.getAllDocumentsBySql(sql);
        int i = 0;
        String imageBg = "", folderStr = "";
        for (Document doc : dc) {
            if (i > 4) {
                i = 0;
            }
            imageBg = imageArray[i];
            ProcessTrStr = GetProcessList(doc.g("Folderid"));
            if (Tools.isNotBlank(ProcessTrStr)) {
                folderStr += "<table width=142px height=\"24px\" border=0 cellpadding=0 cellspacing=0 ><tr>"
                        + "<td width=20px ></td><td align=center background=\"linkey/bpm/images/process/processtype/" + imageTitle[i] + "\" style=\"background-repeat:no-repeat;\" >"
                        + doc.g("FolderName") + "</td>" + "</tr></table>" + "<table width=98% border=0 cellpadding=0 cellspacing=0 style='" + imageArray[i] + "'  height=80px >"
                        + "<tr><td style='padding-left:10px;' >" + ProcessTrStr + "<br><br></td></tr><table><br>";
                i++;
            }
        }
        getHtmlHead();
        BeanCtx.p(folderStr);
        return "";
    }

    public String GetProcessList(String folderid) {
        //获得所有已发布的流程列表
        String sql = "select NodeName,Processid from BPM_ModProcessList where Folderid='" + folderid + "' and Status='1'";
        Document[] dc = Rdb.getAllDocumentsBySql(sql);
        int x = 0;
        String listStr = "";
        for (Document doc : dc) {
            if (BeanCtx.getLinkeyUser().inRoles(BeanCtx.getUserid(), doc.g("ProcessStarter"))) {
                String iconsStr = doc.g("iconsStr");
                if (Tools.isBlank(iconsStr)) {
                    iconsStr = "linkey/bpm/images/process/picon/category.png";
                }
                listStr += "<table align=left width=120 height=60px ><tr><td align=center nowrap >" + "<a href='' onclick=\"OpenUrl('rule?wf_num=R_S003_B036&wf_processid=" + doc.g("Processid")
                        + "');return false;\" >" + "<img src=\"" + iconsStr + "\" align='absmiddle' border=0><br>" + doc.g("NodeName") + "</a>" + "</td></tr></table>";
                x = x + 1;
            }
            if (x > 7) {
                listStr += "<br><br><br><br>";
                x = 0;
            }
        }
        return listStr;
    }

    public void getHtmlHead() {
        String htmlStr = "<title>流程服务分类</title><head><link rel='stylesheet' type='text/css' href='linkey/bpm/css/app_openform.css' />"
                + "<script type=\"text/javascript\" src=\"linkey/bpm/easyui/jquery.min.js\"></script>" + "<script type='text/javascript' src='linkey/bpm/jscode/sharefunction.js'></script></head>";
        BeanCtx.p(htmlStr);
    }

}