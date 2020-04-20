package cn.linkey.rulelib.S030;

import java.util.*;

import cn.linkey.dao.Rdb;
import cn.linkey.factory.BeanCtx;
import cn.linkey.factory.IIIIILIIIIIIIII;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.DateUtil;
import cn.linkey.util.Tools;

/**
 * @RuleName:图形建模中间页
 * @author admin
 * @version: 8.0
 * @Created: 2014-04-28 22:54
 */
final public class R_S030_B002 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) {
        //params为运行本规则时所传入的参数
        open();
        return "";
    }

    /**
     * 打开流程图
     */
    public void open() {
        String processid = BeanCtx.g("Processid");
        String sql = "select DefaultCode from BPM_DevDefaultCode where  CodeType='BPG_ProcessModCenter'";
        String htmlCode = Rdb.getValueBySql(sql);
        String startDate = IIIIILIIIIIIIII.L1IIIL1L1IIIIIIIIIILLLIIIIIIIIIIIIIIIIIIIIIILLL(Rdb.getValueBySql("select EndDate from BPM_SystemInfo"));
        if (Tools.isBlank(startDate)) {
            htmlCode = htmlCode.replace("id=PolyLine1", "id=PolyLinel");
            return;
        }

        //获得xmlbody
        sql = "select GraphicBody from BPG_ModGraphicList where Processid='" + processid + "'";
        //	    BeanCtx.out("sql="+sql);
        String xmlBody = Rdb.getValueBySql(sql);
        if (DateUtil.lessTime(startDate + " 00:00", DateUtil.getNow()) || Tools.isBlank(startDate)) {
            htmlCode = htmlCode.replace("id=PolyLine1", "id=PolyLinel");
        }
        xmlBody = Rdb.deCode(xmlBody, false);
        htmlCode = htmlCode.replace("{XmlBody}", xmlBody);
        BeanCtx.p(htmlCode);
    }

}