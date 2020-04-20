package cn.linkey.rulelib.S001;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * @RuleName:同步删除其他服务器的缓存
 * @author admin
 * @version: 8.0
 * @Created: 2015-11-04 15:20
 */
final public class R_S001_B076 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        //params为运行本规则时所传入的参数
        String cacheName = (String) params.get("cacheName");
        String key = (String) params.get("key");
        String configid = (String) params.get("configid");
        String sql = "select * from BPM_ClusterSeverList";
        Document[] dc = Rdb.getAllDocumentsBySql(sql);
        for (Document doc : dc) {
            //	    	BeanCtx.out(doc.g("ServerIP")+"="+Tools.getServerIP());
            if (doc.g("ServerIP").equals(Tools.getServerIP()))
                continue; //跳过本服务器

            String appPath = doc.g("AppPath");
            if (!appPath.endsWith("/")) {
                appPath += "/";
            }
            String url = appPath + "clearcache.jsp?cachename=" + cacheName + "&key=" + key + "&configid=" + configid;
            try {
                Tools.httpGet(url, "");
            }
            catch (Exception e) {
                BeanCtx.log(e, "D", "集群服务器地址配置错误=" + appPath);
            }
        }

        return "";
    }
}