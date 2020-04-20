package cn.linkey.rulelib.S029;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import cn.linkey.dao.*;
import cn.linkey.util.*;
import cn.linkey.factory.*;
import cn.linkey.rule.LinkeyRule;

/**
 * @RuleName:定时清缓存(每小时执行一次)
 * @author admin
 * @version: 8.0
 * @Created: 2014-06-06 17:26
 */
final public class R_S029_T004 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) {

        //1.首先获得所有缓存策略
        ConcurrentHashMap<String, CacheStrategy> allCacheStrategy = CacheManager.getAllCacheStrategy();

        //2.获得每种策略的配置参数
        for (String cacheName : allCacheStrategy.keySet()) {

            //获取一个缓存策略的配置参数
            CacheStrategy cacheStrategy = allCacheStrategy.get(cacheName); //获得具体的一个缓存策略
            int maxElementsInMemory = cacheStrategy.getMaxElementsInMemory(); //最大缓存数
            String memoryStoreEvictionPolicy = cacheStrategy.getMemoryStoreEvictionPolicy(); //缓存清理方式 FIFO（先进先出）LFU（较少使用）
            int timeToIdleSeconds = cacheStrategy.getTimeToIdleSeconds(); //允许闲置时间（单位：秒）
            int timeToLiveSeconds = cacheStrategy.getTimeToLiveSeconds(); //允许存活时间（单位：秒）

            //获取此策略下的所有缓存对像
            ConcurrentHashMap<String, CacheElement> allCacheElement = cacheStrategy.getCacheElement();

            //clearFIFO(allCacheElement,cacheStrategy);

            //1.首先看是否已超过最大缓存数,如果没有超过缓存数暂不执行清操作,存活时间和空闭时间也先不计算
            if (allCacheElement.size() > maxElementsInMemory) {

                //1.先清理超过闭置时间和存活时间的缓存
                if (timeToIdleSeconds != 0 || timeToLiveSeconds != 0) {
                    clearByTime(allCacheElement, timeToIdleSeconds, timeToLiveSeconds);
                }

                //2.如果还大于再清理最大缓存数超过的缓存
                if (allCacheElement.size() > maxElementsInMemory) {
                    if (memoryStoreEvictionPolicy.equals("FIFO")) {
                        //按先进先出进行清理，一次性清理最后面的1000个,默认缓存数是10000个
                        clearFIFO(allCacheElement, cacheStrategy);
                    }
                    else if (memoryStoreEvictionPolicy.equals("LFU")) {
                        //按较少使用进行清理，一次性清理最少使用的1000个,默认缓存数是10000个
                        clearLFU(allCacheElement, cacheStrategy);
                    }
                }
            }
        }

        return "";
    }

    /**
     * 按时间策略清理缓存
     */
    public void clearByTime(ConcurrentHashMap<String, CacheElement> allCacheElement, int timeToIdleSeconds, int timeToLiveSeconds) {
        for (String key : allCacheElement.keySet()) {
            CacheElement cacheElement = allCacheElement.get(key); //获得缓存对像
            Date lastDate = cacheElement.getLastAccessTime(); //最后访问时间
            Date endDate = DateUtil.getDate(); //现在时间
            Date createDate = cacheElement.getCreateTime();//创建时间

            //1.清除空闭时间超时的缓存
            if (timeToIdleSeconds != 0) {
                double idleSeconds = DateUtil.getDifTwoTime(endDate, lastDate, "S"); //当前时间减去最后访问时间得到空闭时间
                if (idleSeconds > timeToIdleSeconds) {
                    allCacheElement.remove(key);
                }
            }

            //2.清除生存时间超时的缓存
            if (timeToLiveSeconds != 0) {
                double liveSeconds = DateUtil.getDifTwoTime(endDate, createDate, "S"); //当前时间减去创建时间得到已生存的时间
                if (liveSeconds > timeToLiveSeconds) {
                    allCacheElement.remove(key);
                }
            }
        }
    }

    /**
     * 按先进先出模式清理缓存
     */
    public void clearFIFO(ConcurrentHashMap<String, CacheElement> allCacheElement, CacheStrategy cacheStrategy) {

        int maxElementsInMemory = cacheStrategy.getMaxElementsInMemory(); //最大缓存数
        int maxDelNum = allCacheElement.size() - maxElementsInMemory; //一次最大清理缓存数1000
        maxDelNum = maxDelNum + 1000;

        //获得所有元素的key和sortNum的haspmap
        Map<String, Integer> map = new HashMap<String, Integer>();
        for (String key : allCacheElement.keySet()) {
            CacheElement cacheElement = allCacheElement.get(key); //获得缓存对像
            map.put(cacheElement.getKey(), cacheElement.getSortNum());
        }

        //map开始按进入顺序排序
        ArrayList<Map.Entry<String, Integer>> list = new ArrayList<Map.Entry<String, Integer>>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return (o2.getValue() - o1.getValue());
            }
        });

        //开始删除
        //BeanCtx.out("list="+list);
        BeanCtx.out(cacheStrategy.getCahceName() + "缓存清除前大小=" + allCacheElement.size());
        for (int i = (list.size() - 1); i >= 0; i--) {
            maxDelNum--;
            if (maxDelNum > 0) {
                String key = list.get(i).getKey();
                allCacheElement.remove(key);
                //BeanCtx.out("删除缓存="+key);
            }
            else {
                break;
            }
        }
        BeanCtx.out(cacheStrategy.getCahceName() + "缓存清除后大小=" + allCacheElement.size());

    }

    /**
     * 按最少使用清理缓存
     */
    public void clearLFU(ConcurrentHashMap<String, CacheElement> allCacheElement, CacheStrategy cacheStrategy) {
        int maxElementsInMemory = cacheStrategy.getMaxElementsInMemory(); //最大缓存数
        int maxDelNum = allCacheElement.size() - maxElementsInMemory; //一次最大清理缓存数1000
        maxDelNum = maxDelNum + 1000;

        //获得所有元素的key和sortNum的haspmap
        Map<String, Integer> map = new HashMap<String, Integer>();
        for (String key : allCacheElement.keySet()) {
            CacheElement cacheElement = allCacheElement.get(key); //获得缓存对像
            long hitCount = cacheElement.getHitCount();
            Integer i = new Integer(String.valueOf(hitCount));
            map.put(cacheElement.getKey(), i); //把使用次数加入到hashmap中
        }

        //map开始按使用次数进行排序
        ArrayList<Map.Entry<String, Integer>> list = new ArrayList<Map.Entry<String, Integer>>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return (o2.getValue() - o1.getValue());
            }
        });

        //开始删除
        //BeanCtx.out("list="+list);
        BeanCtx.out(cacheStrategy.getCahceName() + "缓存清除前大小=" + allCacheElement.size());
        for (int i = (list.size() - 1); i >= 0; i--) {
            maxDelNum--;
            if (maxDelNum > 0) {
                String key = list.get(i).getKey();
                allCacheElement.remove(key);
                //BeanCtx.out("删除缓存="+key);
            }
            else {
                break;
            }
        }
        BeanCtx.out(cacheStrategy.getCahceName() + "缓存清除后大小=" + allCacheElement.size());
    }
}