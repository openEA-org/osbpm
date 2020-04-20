package cn.linkey.rulelib.S001;

import java.util.HashMap;

import cn.linkey.app.AppUtil;
import cn.linkey.dao.Rdb;
import cn.linkey.dao.RdbCache;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * 本规则负责存盘设计元素的所有属性
 * 
 * @author Administrator 编号:R_S001_B002
 */
public class R_S001_B002 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) {
        String docunid = BeanCtx.g("wf_docunid", true);
        if (Tools.isBlank(docunid)) {
            docunid = BeanCtx.g("WF_DocUnid", true); //兼容大写
        }
        String sqlTableName = BeanCtx.g("WF_TableName");
        if ((docunid.indexOf(",") != -1 && Tools.isBlank(docunid)) || Tools.isBlank(sqlTableName)) {
            BeanCtx.print("{\"Status\":\"error\",\"msg\":\"R_S001_B002:Must post WF_DocUnid and WF_TableName parameters!\"}");
            return "";
        }

        Document eldoc = AppUtil.getDocByUnid(sqlTableName, docunid);
        if (eldoc.isNull()) {
            eldoc.s("WF_OrUnid", docunid);
        }
        String keyFdName = BeanCtx.g("WF_KeyFdName");
        String oldKeyValue = eldoc.g(keyFdName); // 旧的元素的唯一编号，在设计修改编号时必须记录下来，不然设计的事件不会跟随变动，造成事件丢失(条件：旧编号不可能为空)
        String newKeyValue = BeanCtx.g(keyFdName); // 新的元素的唯一编号
        if (Tools.isBlank(newKeyValue)) {
            newKeyValue = oldKeyValue;
        } // newKeyValue不一定会 post进来
        if (Tools.isBlank(keyFdName)) {
            BeanCtx.print("{\"Status\":\"error\",\"msg\":\"Must post WF_KeyFdName field in parameters!\"}");
            return "";
        }
        eldoc.appendFromRequest(BeanCtx.getRequest()); // 把post数据初始化到本文档中
        // 1.保存RuleCode代码,只有保存规则时才执行本操作
        if (sqlTableName.toLowerCase().equals("bpm_rulelist")) {
            String ruleCode = BeanCtx.getRequest().getParameter("RuleCode");
            if (!eldoc.g("RuleType").equals("7")) {
                //如果是javabean则不进行任何检测
                if (ruleCode != null) {
                    if (!AppUtil.checkRuleCode(ruleCode, eldoc.g("WF_Appid"), newKeyValue)) {
                        return "";
                    } // 检测包名类名是否符合要求
                }
                String classPath = "cn.linkey.rulelib." + eldoc.g("WF_Appid") + "." + eldoc.g("RuleNum");
                eldoc.s("ClassPath", classPath);
            }
        }
        BeanCtx.setDocNotEncode(); //not encode
        int i = eldoc.save(); // 保存规则文档且不编码
        if (i < 1) {
            BeanCtx.print("{\"Status\":\"error\",\"msg\":\"Save document error!\"}");
        }
        else {
            BeanCtx.print("{\"Status\":\"ok\",\"msg\":\"" + BeanCtx.getMsg("Common", "AppDocumentSaved") + "\"}");
        }

        //清除系统中的设计缓存
        RdbCache.remove("TempCache", newKeyValue);

        BeanCtx.userlog(docunid, "修改设计", "设计元素所在表(" + sqlTableName + ")编号为(" + newKeyValue + ")");
        addSysAppEditLog(newKeyValue, eldoc.g("WF_Appid"));
        return "";
    }

    /**
     * 增加对系统应用设计的修改日记
     * 
     * @param eleid
     */
    public void addSysAppEditLog(String eleid, String appid) {
        if (appid.startsWith("S") && !BeanCtx.getUserid().equals("admin")) {
            Document doc = BeanCtx.getDocumentBean("BPM_SysAppEditLog");
            doc.s("Elementid", eleid);
            doc.s("ElementName", Rdb.getValueBySql("select eleName from BPM_AllElementList where eleid='" + eleid + "'"));
            doc.save();
        }
    }
}
