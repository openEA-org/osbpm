package cn.linkey.factory;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.dao.RdbCache;
import cn.linkey.doc.Document;

/**
 * 本类主要根据beanid返回具体的类路径 <br />
 * 本类为单例静态类
 * 
 * @author lch
 */
public class BeanConfig {

    @SuppressWarnings("unchecked")
    public static HashMap<String, String> getClassPath(String beanid) {
        // 这里可能有大小写的问题 classPath,singleton在oracle数据库中时为大写
        // 从缓存中查找类配置信息，如果没有就从sql表中去查找，找到后再加入缓存中
        HashMap<String, HashMap<String, String>> allJavaBeanCache = (HashMap<String, HashMap<String, String>>) RdbCache.getSystemCache("BPM_BeanConfig", "ALL");
        HashMap<String, String> beancfg = allJavaBeanCache.get(beanid);
        if (beancfg != null) {
            return beancfg;
        }
        else {
            beancfg = new HashMap<String, String>();
            String sql = "select classPath,singleton from BPM_BeanConfig where Beanid='" + beanid + "'";
            BeanCtx.log("D", "单独初始化Bean不在缓存中 Beanid=" + beanid);
            Document doc = Rdb.getDocumentBySql(sql);
            beancfg.put("classPath", doc.g("classPath"));
            beancfg.put("singleton", doc.g("singleton"));
            if (beancfg == null || beancfg.get("classPath") == null) {
                BeanCtx.log("E", "在BPM_BeanConfig配置表中没有找到(" + beanid + ")的配置信息...");
            }
            return beancfg;
        }
    }

}
