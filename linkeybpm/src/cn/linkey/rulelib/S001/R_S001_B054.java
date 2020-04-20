package cn.linkey.rulelib.S001;

import java.util.HashMap;

import cn.linkey.app.AppUtil;
import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * 删除应用
 * 
 * @author Administrator
 * 
 */
public class R_S001_B054 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        // params为运行本规则时所传入的参数
        String docunid = BeanCtx.g("wf_docunid", true);
        if (Tools.isBlank(docunid)) {
            BeanCtx.p("{\"Status\":\"error\",\"msg\":\"Application to delete failed!\"}");
            return "";
        }
        String sql = "select * from BPM_AppList where WF_OrUnid='" + docunid + "'";
        Document doc = Rdb.getDocumentBySql(sql);
        String appid = doc.g("WF_Appid");
        String appName = doc.g("AppName");
        AppUtil.removeApp(appid);

        BeanCtx.p("{\"Status\":\"ok\",\"msg\":\"" + appName + " deleted successfully!\"}");
        BeanCtx.userlog(docunid, "删除应用", "删除应用" + appName + "(" + appid + ")");
        return "";
    }

}
