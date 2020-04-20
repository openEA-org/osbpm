package cn.linkey.rulelib.S030;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.factory.IIIIILIIIIIIIII;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.DateUtil;
import cn.linkey.util.Tools;
import cn.linkey.wf.NodeUser;

/**
 * @RuleName:BPG查看流程图
 * @author admin
 * @version: 8.0
 * @Created: 2014-07-05 13:57
 */
final public class R_S030_B007 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) {
        // params为运行本规则时所传入的参数
        open();
        return "";
    }

    /**
     * 打开流程图
     */
    public void open() {
        NodeUser nodeUser = (NodeUser) BeanCtx.getBean("NodeUser");
        String processid = BeanCtx.g("Processid", true);
        String sql = "select DefaultCode from BPM_DevDefaultCode where  CodeType='BPG_ProcessShowCenter'";
        String htmlCode = Rdb.getValueBySql(sql);

        // 获得xmlbody
        sql = "select GraphicBody from BPG_ModGraphicList where Processid='" + processid + "'";
        String xmlBody = Rdb.getValueBySql(sql);
        xmlBody = Rdb.deCode(xmlBody, false);
        htmlCode = htmlCode.replace("{XmlBody}", xmlBody);

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
            htmlCode = htmlCode.replace("{logoinfo}", Tools.decode(ctdName));
        }

        BeanCtx.p(htmlCode);
    }
}