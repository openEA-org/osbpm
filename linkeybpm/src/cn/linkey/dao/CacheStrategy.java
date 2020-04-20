package cn.linkey.dao;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 本类为多例类，缓存对像和策略，一个缓存对像中可以缓存很多个CacheElement 应该为每一种缓存策略创建一个本类对像
 * 
 * @author Administrator
 */
public class CacheStrategy {
    private String cahceName = "Default"; //策略名称,Default为默认策略
    private int maxElementsInMemory = 10000; //最大缓存元素个数
    private boolean eternal = false;//是否永久有效
    private int timeToIdleSeconds = 0; //设置对象在失效前的允许闲置时间（单位：秒）。仅当eternal=false对象不是永久有效时使用，可选属性，默认值是0，也就是可闲置时间无穷大。
    private int timeToLiveSeconds = 0; //设置对象在失效前允许存活时间（单位：秒）。最大时间介于创建时间和失效时间之间。仅当eternal=false对象不是永久有效时使用，默认是0.，也就是对象存活时间无穷大。
    private boolean overflowToDisk = false;
    private String diskPath = "/"; //写入硬盘的路径
    private String memoryStoreEvictionPolicy = "LFU"; //当达到maxElementsInMemory限制时，Ehcache将会根据指定的策略去清理内存。你可以设置为FIFO（先进先出）或是LFU（较少使用）。
    private ConcurrentHashMap<String, CacheElement> cacheElement = new ConcurrentHashMap<String, CacheElement>(200);
    private int inSortNum = 0;

    /**
     * 获得缓存对像
     * 
     * @param key 缓存对像的key值
     * @return
     */
    public CacheElement get(String key) {
        return cacheElement.get(key);
    }

    /**
     * 追加一个缓存对像
     * 
     * @param key 键
     * @param value 值cacheElement类型
     */
    public void put(CacheElement value) {
        //if(cacheElement.size()<maxElementsInMemory){
        value.setSortNum(inSortNum++); //设定进入次序，用来维护先进先出的关系
        cacheElement.put(value.getKey(), value);
        //}else{
        //	BeanCtx.log("D", "警告: 缓存"+this.cahceName+"已超过最大设定的缓存数("+maxElementsInMemory+")!");
        //}
    }

    /**
     * 删除一个缓存对像
     * 
     * @param key 键名称
     */
    public void remove(String key) {
        cacheElement.remove(key);
    }

    /**
     * 清除缓存
     */
    public void clear() {
        cacheElement.clear();
    }

    /**
     * 获得缓存数量
     * 
     * @return
     */
    public int getSize() {
        return cacheElement.size();
    }

    public String getCahceName() {
        return cahceName;
    }

    public void setCahceName(String cahceName) {
        this.cahceName = cahceName;
    }

    public int getMaxElementsInMemory() {
        return maxElementsInMemory;
    }

    public void setMaxElementsInMemory(int maxElementsInMemory) {
        this.maxElementsInMemory = maxElementsInMemory;
    }

    public boolean isEternal() {
        return eternal;
    }

    public void setEternal(boolean eternal) {
        this.eternal = eternal;
    }

    public int getTimeToIdleSeconds() {
        return timeToIdleSeconds;
    }

    public void setTimeToIdleSeconds(int timeToIdleSeconds) {
        this.timeToIdleSeconds = timeToIdleSeconds;
    }

    public int getTimeToLiveSeconds() {
        return timeToLiveSeconds;
    }

    public void setTimeToLiveSeconds(int timeToLiveSeconds) {
        this.timeToLiveSeconds = timeToLiveSeconds;
    }

    public boolean isOverflowToDisk() {
        return overflowToDisk;
    }

    public void setOverflowToDisk(boolean overflowToDisk) {
        this.overflowToDisk = overflowToDisk;
    }

    public String getDiskPath() {
        return diskPath;
    }

    public void setDiskPath(String diskPath) {
        this.diskPath = diskPath;
    }

    public String getMemoryStoreEvictionPolicy() {
        return memoryStoreEvictionPolicy;
    }

    public void setMemoryStoreEvictionPolicy(String memoryStoreEvictionPolicy) {
        this.memoryStoreEvictionPolicy = memoryStoreEvictionPolicy;
    }

    public ConcurrentHashMap<String, CacheElement> getCacheElement() {
        return cacheElement;
    }

    public void setCacheElement(ConcurrentHashMap<String, CacheElement> cacheElement) {
        this.cacheElement = cacheElement;
    }

    public long getInSortNum() {
        return inSortNum;
    }

    public void setInSortNum(int inSortNum) {
        this.inSortNum = inSortNum;
    }
}
