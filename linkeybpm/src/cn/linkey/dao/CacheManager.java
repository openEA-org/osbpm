package cn.linkey.dao;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 本类为单例类，缓存管理器
 * 
 * @author Administrator
 */
public class CacheManager {
    private static ConcurrentHashMap<String, CacheStrategy> cacheStrategy = new ConcurrentHashMap<String, CacheStrategy>();
    static {
        //1.默认建一个缺省的缓存策略
        add("Default", new CacheStrategy()); //永久生存模式

        //2.创建一个用户缓存策略
        CacheStrategy userCacheStrategy = new CacheStrategy();
        userCacheStrategy.setCahceName("UserCacheStrategy");
        userCacheStrategy.setTimeToIdleSeconds(8 * 60); //最大空闲时间8分钟
        add("UserCacheStrategy", userCacheStrategy);

        //3.创建一个临时缓存策略
        CacheStrategy tempCacheStrategy = new CacheStrategy();
        tempCacheStrategy.setCahceName("TempCache");
        tempCacheStrategy.setTimeToIdleSeconds(60 * 60); //最大空闲时间60分钟
        add("TempCache", tempCacheStrategy);

        //4.创建一个国际标签缓存策略
        CacheStrategy labelCacheStrategy = new CacheStrategy();
        labelCacheStrategy.setCahceName("LabelCache");
        labelCacheStrategy.setTimeToIdleSeconds(60 * 60 * 24); //最大空闲24小时
        add("LabelCache", labelCacheStrategy);

    }

    /**
     * 获得缺省的缓存策略
     * 
     * @param key
     * @return
     */
    public static CacheStrategy getDefaultCacheStrategy() {
        return cacheStrategy.get("Default");
    }

    /**
     * 获得一个已存在的缓存策略
     * 
     * @param key
     * @return
     */
    public static CacheStrategy get(String key) {
        return cacheStrategy.get(key);
    }

    /**
     * 增加一个缓存策略,用new CacheStrategy()方法创建一个缓存策略并加入到缓存管理器中来
     * 
     * @param key 策略名称
     * @return 策略对像
     */
    public static void add(String key, CacheStrategy value) {
        cacheStrategy.put(key, value);
    }

    /**
     * 获得所有缓存的大小
     * 
     * @return
     */
    public static int getSize() {
        return cacheStrategy.size();
    }

    /**
     * 返回所有缓存策略的key name
     * 
     * @return
     */
    public static Set<String> getCacheNames() {
        return cacheStrategy.keySet();
    }

    /**
     * 删除一个缓存策略以及他缓存的所有对像
     * 
     * @param key
     */
    public static void remove(String key) {
        cacheStrategy.get(key).clear(); //删除缓存策略中的所有缓存元素
        cacheStrategy.remove(key); //从缓存策略中删除策略
    }

    /**
     * 关闭缓存
     */
    public static void shutdown() {
        for (String key : cacheStrategy.keySet()) {
            cacheStrategy.get(key).clear();
        }
    }

    public static ConcurrentHashMap<String, CacheStrategy> getAllCacheStrategy() {
        return cacheStrategy;
    }

    public static void setCacheStrategy(ConcurrentHashMap<String, CacheStrategy> cacheStrategy) {
        CacheManager.cacheStrategy = cacheStrategy;
    }

}
