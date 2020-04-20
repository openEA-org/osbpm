package cn.linkey.rulelib.S001;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import cn.linkey.dao.CacheElement;
import cn.linkey.dao.CacheManager;
import cn.linkey.dao.CacheStrategy;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;

/**
 * @RuleName:缓存监控
 * @author admin
 * @version: 8.0
 * @Created: 2014-04-16 10:20
 */
final public class R_S001_B039 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) {
        //params为运行本规则时所传入的参数
        StringBuilder jsonStr = new StringBuilder();
        int totalNum = 0;

        ConcurrentHashMap<String, CacheStrategy> allCache = CacheManager.getAllCacheStrategy(); //所有缓存策略及对像
        for (String cacheName : allCache.keySet()) {
            CacheStrategy cache = allCache.get(cacheName); //具体的一个缓存策略
            ConcurrentHashMap<String, CacheElement> allElement = cache.getCacheElement(); //一个缓存策略下的所有缓存元素
            for (String key : allElement.keySet()) {
                totalNum++;
                if (jsonStr.length() > 0) {
                    jsonStr.append(",");
                }
                CacheElement element = allElement.get(key);
                String lastAccessTime = "-";
                if (element.getLastAccessTime() != null) {
                    lastAccessTime = element.getLastAccessTime().toLocaleString();
                }
                jsonStr.append("{\"CacheName\":\"" + cacheName + "\",\"key\":\"" + element.getKey() + "\",\"HitCount\":\"" + element.getHitCount() + "\",\"lastAccessTime\":\"" + lastAccessTime
                        + "\",\"CreatTime\":\"" + element.getCreateTime().toLocaleString() + "\"}");
            }
        }
        jsonStr.insert(0, "{\"total\":" + totalNum + ",\"rows\":[");
        jsonStr.append("]}");
        BeanCtx.p(jsonStr.toString());
        return "";
    }
}