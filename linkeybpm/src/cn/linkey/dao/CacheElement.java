package cn.linkey.dao;

import java.util.Date;

import cn.linkey.util.DateUtil;

/**
 * 本类为多例类，每一个缓存实例都持有本类
 * 
 * @author Administrator
 */
public class CacheElement {
    private String key;
    private Object value;
    private long hitCount = 0; //命中次数
    private Date lastAccessTime; //最后访问时间
    private int sortNum = 0; //进入序顺
    private Date createTime = DateUtil.getDate(); //缓存创建时间

    public CacheElement(String key, Object value) {
        this.key = key;
        this.value = value;
    }

    /**
     * 获得缓存的对像
     * 
     * @return 返回Object
     */
    public Object getObjectValue() {
        this.hitCount++;
        this.lastAccessTime = new Date(); //更新最后访问时间
        //BeanCtx.out(this.key+"命中次数为="+this.hitCount);
        //BeanCtx.out(this.lastAccessTime.toString());
        //BeanCtx.out("进入序顺为="+this.sortNum);
        return this.value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public long getHitCount() {
        return hitCount;
    }

    public void setHitCount(long hitCount) {
        this.hitCount = hitCount;
    }

    public Date getLastAccessTime() {
        return lastAccessTime;
    }

    public void setLastAccessTime(Date lastAccessTime) {
        this.lastAccessTime = lastAccessTime;
    }

    public int getSortNum() {
        return sortNum;
    }

    public void setSortNum(int sortNum) {
        this.sortNum = sortNum;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
