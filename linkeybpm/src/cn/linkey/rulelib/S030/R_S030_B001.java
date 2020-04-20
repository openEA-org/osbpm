package cn.linkey.rulelib.S030;

import java.util.*;
import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.factory.IIIIILIIIIIIIII;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.DateUtil;
import cn.linkey.util.Tools;

/**
 * @RuleName:图形建模首页
 * @author admin
 * @version: 8.0
 * @Created: 2014-04-28 22:54
 */
final public class R_S030_B001 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) {
        //params为运行本规则时所传入的参数
        String action = BeanCtx.g("WF_Action");
        if (action.equals("save")) {
            save();
        }
        else {
            open();
        }
        return "";
    }

    /**
     * 保存流程图形
     */
    public void save() {
        String processid = BeanCtx.g("Processid", true);
        String xmlStr = BeanCtx.g("XmlStr");
        String sql = "select Processid from BPG_ModProcessList where Processid='" + processid + "'";
        if (Rdb.hasRecord(sql)) {
            sql = "select * from BPG_ModGraphicList where Processid='" + processid + "'";
            Document doc = Rdb.getDocumentBySql(sql);
            doc.s("GraphicBody", xmlStr);
            doc.s("Processid", processid);
            doc.save();
            BeanCtx.p(Tools.jmsg("ok", "save"));
        }
        else {
            BeanCtx.p(Tools.jmsg("error", "请在空白处点击键并在过程属性中指定流程的名称!"));
        }

    }

    /**
     * 打开流程
     */
    public void open() {
        String processid = BeanCtx.g("Processid");
        String processName = "", sql = "";
        if (Tools.isBlank(processid)) {
            processid = Rdb.getNewid("");
            processName = "新建流程";
        }
        else {
            sql = "select * from BPG_ModProcessList where Processid='" + processid + "'";
            Document doc = Rdb.getDocumentBySql(sql);
            if (doc.isNull()) {
                BeanCtx.p("流程(" + processid + ")不存在!");
                return;
            }
            else {
                processName = doc.g("NodeName");
            }
        }

        sql = "select * from BPM_SystemInfo";
        Document doc = Rdb.getDocumentBySql(sql);
        String ctdName = doc.g("CtdName");
        ctdName = IIIIILIIIIIIIII.L1IIIL1L1IIIIIIIIIILLLIIIIIIIIIIIIIIIIIIIIIILLL(ctdName);
        String startDate = IIIIILIIIIIIIII.L1IIIL1L1IIIIIIIIIILLLIIIIIIIIIIIIIIIIIIIIIILLL(doc.g("EndDate"));
        if (Tools.isBlank(startDate) || Tools.isBlank(ctdName)) {
            BeanCtx.p("<script>alert(\"系统已过期请联系开发商解决此问题!\");top.close();</script>");
            return;
        }
        if (DateUtil.lessTime(startDate + " 00:00", DateUtil.getNow())) {
            BeanCtx.p("<script>alert(\"系统已过期请联系开发商解决此问题!\");top.close();</script>");
        }
        else {
            sql = "select DefaultCode from BPM_DevDefaultCode where  CodeType='BPG_ProcessIndexFrame'";
            String htmlCode = Rdb.getValueBySql(sql);
            htmlCode = htmlCode.replace("{logoinfo}", Tools.decode(ctdName));
            htmlCode = htmlCode.replace("{UserName}", BeanCtx.getUserName());
            htmlCode = htmlCode.replace("{Date}", DateUtil.getNow("yyyy-MM-dd"));
            htmlCode = htmlCode.replace("{ProcessName}", processName);
            htmlCode = htmlCode.replace("{Processid}", processid);
            BeanCtx.p(htmlCode);
        }
    }

}