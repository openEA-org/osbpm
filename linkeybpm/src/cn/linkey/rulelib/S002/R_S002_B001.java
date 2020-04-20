package cn.linkey.rulelib.S002;

import java.io.IOException;
import java.util.HashMap;

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
final public class R_S002_B001 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws IOException {
        //params为运行本规则时所传入的参数
        String action = BeanCtx.g("WF_Action");
        String processid = BeanCtx.g("Processid", true);
        String wf_OrUnid = BeanCtx.g("WF_Appid");
        
        //20180810 新版流程设计器跳转
        String FlowType = BeanCtx.g("FlowType");
        if(Tools.isBlank(FlowType)){
        	String sql1 = "select FlowType from BPM_MODGRAPHICLIST where PROCESSID='" + processid + "'";
        	FlowType = Rdb.getValueBySql(sql1);
        }
        //添加默认应用 //20181009 添加Tools.isNotBlank(processid) 不为空时不默认添加默认应用
        if(Tools.isBlank(wf_OrUnid)){
        	//wf_OrUnid = "S029"; 
        	if(Tools.isNotBlank(processid)){
        		String sql2 = "select WF_Appid from bpm_modprocesslist where Processid = '" + processid + "'";
        		wf_OrUnid = Rdb.getValueBySql(sql2);
        		if(Tools.isBlank(wf_OrUnid)){
        			wf_OrUnid = "S029";
        		}
        	}else{
        		wf_OrUnid = "S029";
        	}
        }
        if("2".equals(FlowType)){
        	BeanCtx.getResponse().sendRedirect("r?wf_num=P_S002_003&WF_Appid=" + wf_OrUnid + "&Processid="+processid); 
        	return "";
        }
        
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
        String sql = "select Processid from BPM_ModProcessList where Processid='" + processid + "'";
        if (Rdb.hasRecord(sql)) {
            sql = "select * from BPM_ModGraphicList where Processid='" + processid + "'";
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
        String processid = BeanCtx.g("Processid", true);
        String processName = "", sql = "";
        if (Tools.isBlank(processid)) {
            processid = Rdb.getNewid("");
            processName = "新建流程";
        }
        else {
            sql = "select * from BPM_ModProcessList where Processid='" + processid + "'";
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
            sql = "select DefaultCode from BPM_DevDefaultCode where  CodeType='ProcessIndexFrame'";
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