package cn.linkey.dao;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.util.Tools;

/**
 * 数据缓存类 <br />
 * RdbCache中返回的HashSet, HashMap等对像必须要处理只读状态，否则在多线程操作时将出现问题
 * 
 * @author Administrator 本类为单例类
 */
public class RdbCache {

    private static int stopCache = 0; //1表示停用缓存，0表示启用缓存,2表示未设定

    //1.java Bean实例对像池缓存,减少对表BPM_BeanConfig的读取
    public static Object getBeanObject(String key) {
        return get(key);
    }

    public static void putBeanObject(String key, Object obj) {
        put(key, obj);
    }

    /**
     * 初始化缓存
     */
    private static void initStopCache() {
        if (stopCache == 2) {
            String stopCachePro = Tools.getProperty("StopCache");
            if (stopCachePro.equals("1")) {
                stopCache = 1; //停用缓存
            }
            else {
                stopCache = 0; //启用缓存
            }
        }
    }

    /**
     * 添加键值到缺省的cache对像中去
     * 
     * @param key 键
     * @param value 值
     */
    public synchronized static void put(String key, Object value) {
        CacheManager.getDefaultCacheStrategy().put(new CacheElement(key, value));
    }

    /**
     * 从缺省的cache对像删除缓存
     * 
     * @param key 键
     */
    public static void remove(String key) {
        CacheManager.getDefaultCacheStrategy().remove(key);
        removeAllServerCache("Default", key, "");//同步清缓存
    }

    /**
     * 删除指定缓存策略中的缓存
     * 
     * @param cacheName 策略名称
     * @param key 键
     */
    public static void remove(String cacheName, String key) {
        //BeanCtx.out("删除缓存="+cacheName+"="+key);
        CacheStrategy cacheStrategy = CacheManager.get(cacheName);
        if (cacheStrategy != null) {
            cacheStrategy.remove(key); //删除缓存
            removeAllServerCache(cacheName, key, "");//同步清缓存
        }
    }

    /**
     * 删除系统缓存中的指定key缓存的数据
     * 
     * @param configName
     * @param systemkey
     * @param configid
     */
    @SuppressWarnings("unchecked")
    public static void removeSystemCache(String cacheName, String key, String configid) {
        if (Tools.isBlank(key)) {
            key = "ALL";
        }
        HashMap<String, String> configmap = (HashMap<String, String>) RdbCache.getSystemCache(cacheName, key);
        if (configmap != null) {
            configmap.remove(configid);
            removeAllServerCache(cacheName, key, configid);
        }
    }

    /**
     * 同时同步清除其他所有服务器的缓存数据
     * 
     * @param cacheName
     * @param key
     * @param configid
     */
    public static void removeAllServerCache(String cacheName, String key, String configid) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("cacheName", cacheName);
        params.put("key", key);
        params.put("configid", configid);
        try {
            BeanCtx.getExecuteEngine().run("R_S001_B076", params);
        }
        catch (Exception e) {
            BeanCtx.log(e, "W", "系统缓存清除时出错.");
        }
    }

    /**
     * 系统缓存的统一put方法
     * 
     * @param cacheName
     * @param key
     * @param value
     */
    @SuppressWarnings("unchecked")
    public synchronized static void putSystemCache(String cacheName, String key, String value) {
        HashMap<String, String> configmap = (HashMap<String, String>) RdbCache.getSystemCache(cacheName, key);
        if (configmap != null) {
            configmap.put(key, value);
        }
    }

    /**
     * 系统默认了三个缓存策略名称 <br />
     * 1.Default(永久有效) <br />
     * 2.UserCacheStrategy(5分钟后清除) <br />
     * 3.TempCache(30分钟不使用清除) <br />
     * 添加键值到指定的cache策略对像中去, 如果缓存策略不存在可以用 CacheStrategy cacheStrategy=new CacheStrategy(); //创建一个新的缓存策略 cacheStrategy.setCahceName("CacheStrategy2"); cacheStrategy.setTimeToIdleSeconds(300);
     * //最大空闲时间5分钟 CacheManager.add("CacheStrategy2", cacheStrategy); 使用方法：RdbCache.put("CacheStrategy2","key1","加入缓存中");
     * 
     * @param cacheName 缓存配置名称
     * @param key 键
     * @param value 值
     */
    public synchronized static void put(String cacheName, String key, Object value) {
        CacheManager.get(cacheName).put(new CacheElement(key, value));
    }

    /**
     * 根据 key获得指定缓存策略对像中key所缓存的值
     * 
     * @param cacheName 缓存策略的配置名称
     * @param key 键
     * @return 绘存的Object值
     */
    public static Object get(String cacheName, String key) {
        if (stopCache == 2) {
            initStopCache();
        }
        if (stopCache == 1) {
            return null;
        } //停缓存
        CacheElement element = CacheManager.get(cacheName).get(key);
        if (element == null) {
            return null;
        }
        Object obj = element.getObjectValue();
        return obj;
    }

    /**
     * 根据 key获得指定缺省缓存对像中的缓存值
     * 
     * @param cache 缓存对像
     * @param key 键
     * @return 绘存的Object值
     */
    public static Object get(String key) {
        if (stopCache == 2) {
            initStopCache();
        }
        //BeanCtx.out("stopcache="+stopCache);
        if (stopCache == 1) {
            return null;
        } //停缓存
        CacheElement element = CacheManager.getDefaultCacheStrategy().get(key);
        if (element == null) {
            return null;
        }
        Object obj = element.getObjectValue();
        return obj;
    }

    /**
     * 统一的bpm系统级别的缓存方法,本函数只负责根据各方法返回所有缓存数据，如果在缓存中没有的数据则由各调用方法自行负责如何处理
     * 
     * @param sysCacheName 缓存标识，一般以sql表命名
     * @param key 要获取的key
     * @return 返回object对像
     */
    public static Object getSystemCache(String sysCacheName, String key) {
        if (Tools.isBlank(key)) {
            key = "ALL";
        }
        Object obj = get(sysCacheName + "_" + key);
        //		if(sysCacheName.equals("BPM_EngineFormPluginConfig") || sysCacheName.equals("BPM_HtmlTagConfig")){obj=null;}
        if (obj == null) {
            if (sysCacheName.equals("BPM_EngineActionConfig")) {
                obj = getEngineActionConfig(key); //获得引擎动作配置表
            }
            else if (sysCacheName.equals("BPM_BeanConfig")) {
                obj = getBeanConfig(); //获得所有javabean的配置
            }
            else if (sysCacheName.equals("BPM_SystemConfig")) {
                obj = getSystemConfig(); //获得系统配置缓存
            }
            else if (sysCacheName.equals("BPM_AppSystemConfig")) {
                obj = getAppSystemConfig(); //获得系统应用配置缓存
            }
            else if (sysCacheName.equals("BPM_EngineFormPluginConfig")) {
                obj = getAllEngineFormPluginConfig();//引擎表单解析插件
            }
            else if (sysCacheName.equals("BPM_EngineButtonConfig")) {
                obj = getAllToolbarDoc(); //引擎处理单所有按扭的文档配置缓存
            }
            else if (sysCacheName.equals("BPM_HtmlTagConfig")) {
                obj = getAllHtmlTagConfig(); //获得所有html标签的缓存
            }
            else if (sysCacheName.equals("BPM_RuleList")) {
                obj = getAllRuleDoc(); //获得所有规则缓存文档
            }
            else if (sysCacheName.equals("BPM_EngineNodeOwnerConfig")) {
                obj = getAllNodeOwnerRule(); //获得所有活动参与者的规则配置
            }
            else if (sysCacheName.equals("BPM_ElementExtendsConfig")) {
                obj = getAllElementExtendsConfig(); //获得所有设计继承配置表
            }
            else if (sysCacheName.equals("BPM_JARRULELIST")) {
                obj = getAllRuleNumInJar();//获得所有jar包中的规则class文件的清单
            }
            else if (sysCacheName.equals("BPM_FormSelectorConfig")) {
                obj = getAllSelector(); //获得所有表单选择器
            }
            else if (sysCacheName.equalsIgnoreCase("BPM_XTagConfig")) {
                obj = getAllXTagConfig(); //获得页面XTag解析器
            }
            else if (sysCacheName.equalsIgnoreCase("BPM_UIEngineConfig")) {
                obj = getAllUIEngineConfig();//获得UI解析引擎
            }
            put(sysCacheName + "_" + key, obj); //加入缓存中
        }
        return obj;
    }

    /**
     * 返回所有UI引擎的配置数据
     * 
     * @return
     */
    private static HashMap<String, String> getAllUIEngineConfig() {
        HashMap<String, String> configCache = new HashMap<String, String>();
        if (Rdb.isExistTable(Tools.getProperty("DefaultDataSourceid"), "BPM_UIEngineConfig")) {
            String sql = "select WF_Appid,Configid,RuleNum from BPM_UIEngineConfig";
            HashSet<Document> dc = Rdb.getAllDocumentsSetBySql("BPM_UIEngineConfig", sql);
            for (Document doc : dc) {
                configCache.put(doc.g("Configid"), doc.g("RuleNum"));
            }
        }
        return configCache;
    }

    /**
     * 获取所有jar包中的规则清单并缓存起来
     * 
     * @return 返回对像map
     */
    private static HashMap<String, String> getAllRuleNumInJar() {
        HashMap<String, String> configCache = new HashMap<String, String>();
        String bpmjarName = BeanCtx.getSystemConfig("SystemJarName");
        if (Tools.isBlank(bpmjarName)) {
            bpmjarName = "linkeybpm.jar";
        }
        String libJarPath = BeanCtx.getWebAppsPath() + "WEB-INF/lib/" + bpmjarName;
        File file = new File(libJarPath);
        if (!file.exists()) {
            return configCache;
        }

        //如果jar文件存在则读取所有jar包中的rulenum缓存起来
        JarFile jarFile = null;
        try {
            jarFile = new JarFile(libJarPath);
            Enumeration<JarEntry> entrys = jarFile.entries();
            while (entrys.hasMoreElements()) {
                JarEntry jarEntry = entrys.nextElement();
                String classPath = jarEntry.getName();
                if (classPath.startsWith("cn/linkey/rulelib")) {
                    int spos = classPath.lastIndexOf("/") + 1;
                    int epos = classPath.indexOf(".class");
                    String className = classPath.substring(spos, epos);
                    classPath = classPath.substring(0, epos).replace("/", ".");
                    configCache.put(className, classPath);
                }
            }
        }
        catch (Exception e) {
            BeanCtx.log(e, "E", "未找到系统的jar包文件请确认jar包是否存在，路径为:" + libJarPath);
        }
        finally {
            if (jarFile != null) {
                try {
                    jarFile.close();
                }
                catch (IOException e) {
                }
            }
        }
        return configCache;
    }

    /**
     * 获得所有XTag标签的配置数据
     * 
     * @return
     */
    private static HashMap<String, String> getAllXTagConfig() {
        HashMap<String, String> configCache = new HashMap<String, String>();
        if (Rdb.isExistTable(Tools.getProperty("DefaultDataSourceid"), "BPM_XTagConfig")) {
            String sql = "select WF_Appid,Configid,RuleNum from BPM_XTagConfig";
            HashSet<Document> dc = Rdb.getAllDocumentsSetBySql("BPM_XTagConfig", sql);
            for (Document doc : dc) {
                configCache.put(doc.g("Configid"), doc.g("RuleNum"));
            }
        }
        return configCache;
    }

    /**
     * 获取所有继承设计的配置表参数
     * 
     * @return 返回对像map由设计id+应用id组成，可以实现不同应用配置不同的继承规则
     */
    private static HashMap<String, String> getAllElementExtendsConfig() {
        HashMap<String, String> configCache = new HashMap<String, String>();
        if (Rdb.isExistTable(Tools.getProperty("DefaultDataSourceid"), "BPM_ElementExtendsConfig")) {
            String sql = "select SourceEleNum,TargetEleNum,WF_Appid from BPM_ElementExtendsConfig where Status='Y'";
            HashSet<Document> dc = Rdb.getAllDocumentsSetBySql("BPM_ElementExtendsConfig", sql);
            for (Document doc : dc) {
                configCache.put(doc.g("SourceEleNum") + "_" + doc.g("WF_Appid"), doc.g("TargetEleNum")); //增加国家字符串用来支持多语言的按扭
            }
        }
        return configCache;
    }

    /**
     * 根据配置表获得设计元素在配置表中的继承的设计元素的编号
     * 
     * @param eleNum
     * @return
     */
    @SuppressWarnings("unchecked")
    public static String getElementExtendsNum(String eleNum) {

        //如果系统配置中直接禁止扩展则直接返回原id编号
        if (BeanCtx.getSystemConfig("StopExtendsRule").equals("1")) {
            return eleNum;
        }

        //允许扩展的情况下
        String appid = "";
        if (BeanCtx.getLinkeywf() != null) {
            appid = BeanCtx.getLinkeywf().getAppid();
        }
        else {
            appid = Tools.getAppidFromElNum(eleNum);
        }

        //设计继承配置表缓存数据
        HashMap<String, String> elementExtendsConfigCache = (HashMap<String, String>) getSystemCache("BPM_ElementExtendsConfig", "ALL");

        //首先按应用查找，找不到再找全局的配置
        String extendsRuleNum = elementExtendsConfigCache.get(eleNum + "_" + appid);
        if (extendsRuleNum != null) {
            //说明这个规则有继承配置
            eleNum = extendsRuleNum;//使用继承的规则编号
        }
        else {
            //找全局的,所有应用都生效
            extendsRuleNum = elementExtendsConfigCache.get(eleNum + "_*");
            if (extendsRuleNum != null) {
                eleNum = extendsRuleNum;
            }
        }
        return eleNum;
    }

    /**
     * 根据规则编号获得文档对像
     * 
     * @param ruleNum 规则编号
     * @return 返回规则文档对像map
     */
    private static HashMap<String, LinkedHashSet<String>> getAllEngineFormPluginConfig() {
        HashMap<String, LinkedHashSet<String>> pluginCache = new HashMap<String, LinkedHashSet<String>>();

        //获得所有pc端的表单插件
        String sql = "select RuleNum,IsMobile from BPM_EngineFormPluginConfig where IsMobile='0' and PlusinType='1' and Status='1' and  WF_CacheFlag='1' order by SortNum";
        pluginCache.put("PC", Rdb.getValueLinkedSetBySql(sql));

        //获得所有移动端的插件
        sql = "select RuleNum,IsMobile from BPM_EngineFormPluginConfig where IsMobile='1' and PlusinType='1' and Status='1' and  WF_CacheFlag='1' order by SortNum";
        pluginCache.put("MOBILE", Rdb.getValueLinkedSetBySql(sql));
        
        //获得layui所有的插件
        sql = "select RuleNum,IsMobile from BPM_EngineFormPluginConfig where IsMobile='3' and PlusinType='1' and Status='1' and  WF_CacheFlag='1' order by SortNum";
        pluginCache.put("LayUI", Rdb.getValueLinkedSetBySql(sql));

        return pluginCache;
    }

    /**
     * 获得所有活动参与者的缓存对像
     * 
     * @return
     */
    private static HashMap<String, String> getAllNodeOwnerRule() {
        String sql = "select OwnerRuleid,RuleNum from BPM_EngineNodeOwnerConfig where WF_CacheFlag='1' and Status='1'";
        return Rdb.getMapDataBySql(sql);
    }

    /**
     * 获得所有表单选择器的缓存对像
     * 
     * @return
     */
    private static HashMap<String, String> getAllSelector() {
        String sql = "select Selectorid,SelectorCode from BPM_FormSelectorConfig where WF_CacheFlag='1'";
        return Rdb.getMapDataBySql(sql);
    }

    /**
     * 根据规则编号获得文档对像
     * 
     * @param ruleNum 规则编号
     * @return 返回规则文档对像map
     */
    private static HashMap<String, Document> getAllRuleDoc() {
        HashMap<String, Document> ruleDocCache = new HashMap<String, Document>();
        String sql = "select * from BPM_RuleList where WF_CacheFlag='1'";
        HashSet<Document> dc = Rdb.getAllDocumentsSetBySql("BPM_RuleList", sql);
        for (Document doc : dc) {
            ruleDocCache.put(doc.g("RuleNum"), doc);
        }
        return ruleDocCache;
    }

    /**
     * 获得所有html标签解析配置
     * 
     * @return
     */
    private static HashMap<String, HashMap<String, HashMap<String, String>>> getAllHtmlTagConfig() {

        HashMap<String, HashMap<String, HashMap<String, String>>> returnMap = new HashMap<String, HashMap<String, HashMap<String, String>>>();

        //缓存PC端的标签插件
        HashMap<String, HashMap<String, String>> htmlTagConfig = new HashMap<String, HashMap<String, String>>();
        String sql = "select TagName,IsPair,RuleNum from BPM_HtmlTagConfig where Status='1' and IsMobile='0'";
        HashSet<Document> dc = Rdb.getAllDocumentsSetBySql("BPM_HtmlTagConfig", sql);
        for (Document doc : dc) {
            HashMap<String, String> configMap = new HashMap<String, String>();
            configMap.put("TagName", doc.g("TagName"));
            configMap.put("IsPair", doc.g("IsPair"));
            configMap.put("RuleNum", doc.g("RuleNum"));
            htmlTagConfig.put(doc.g("TagName"), configMap);
        }
        returnMap.put("PC", htmlTagConfig);

        //缓存Mobile端的标签插件
        HashMap<String, HashMap<String, String>> htmlTagConfigMobile = new HashMap<String, HashMap<String, String>>();
        sql = "select TagName,IsPair,RuleNum from BPM_HtmlTagConfig where Status='1' and IsMobile='1'";
        dc = Rdb.getAllDocumentsSetBySql("BPM_HtmlTagConfig", sql);
        for (Document doc : dc) {
            HashMap<String, String> configMap = new HashMap<String, String>();
            configMap.put("TagName", doc.g("TagName"));
            configMap.put("IsPair", doc.g("IsPair"));
            configMap.put("RuleNum", doc.g("RuleNum"));
            htmlTagConfigMobile.put(doc.g("TagName"), configMap);
        }
        returnMap.put("MOBILE", htmlTagConfigMobile);
        
        //20180131
        HashMap<String, HashMap<String, String>> htmlTagConfigLayUI = new HashMap<String, HashMap<String, String>>();
        sql = "select TagName,IsPair,RuleNum from BPM_HtmlTagConfig where Status='1' and IsMobile='3'";
        dc = Rdb.getAllDocumentsSetBySql("BPM_HtmlTagConfig", sql);
        for (Document doc : dc) {
            HashMap<String, String> configMap = new HashMap<String, String>();
            configMap.put("TagName", doc.g("TagName"));
            configMap.put("IsPair", doc.g("IsPair"));
            configMap.put("RuleNum", doc.g("RuleNum"));
            htmlTagConfigLayUI.put(doc.g("TagName"), configMap);
        }
        returnMap.put("LayUI", htmlTagConfigLayUI);
        //返回PC和Mobile端的缓存
        return returnMap;
    }

    /**
     * 引擎处理单按扭的所有文档对像
     * 
     * @return
     */
    private static HashMap<String, Document> getAllToolbarDoc() {
        HashMap<String, Document> engineToolbarCache = new HashMap<String, Document>();
        String sql = "select Toolbarid,Country,ToolbarName,ToolbarHtml,HiddenRuleNum from BPM_EngineButtonConfig where WF_CacheFlag='1'";
        HashSet<Document> dc = Rdb.getAllDocumentsSetBySql("BPM_EngineButtonConfig", sql);
        for (Document doc : dc) {
            engineToolbarCache.put(doc.g("Toolbarid") + "_" + doc.g("Country"), doc); //增加国家字符串用来支持多语言的按扭
        }
        return engineToolbarCache;
    }

    /**
     * 获得应用级别的配置数据库表的缓存
     * 
     * @return
     */
    private static HashMap<String, String> getAppSystemConfig() {
        HashMap<String, String> map = new HashMap<String, String>();
        String sql = "select WF_Appid,Configid,ConfigValue from BPM_AppSystemConfig where WF_CacheFlag='1'";
        Document[] dc = Rdb.getAllDocumentsBySql(sql);
        for (Document doc : dc) {
            map.put(doc.g("WF_Appid") + "_" + doc.g("Configid"), doc.g("ConfigValue"));
        }
        return map;
    }

    /**
     * 获得系统通用配置数据库表的缓存
     * 
     * @return
     */
    private static HashMap<String, String> getSystemConfig() {
        String sql = "select Configid,ConfigValue from BPM_SystemConfig where WF_CacheFlag='1'";
        return Rdb.getMapDataBySql(sql);
    }

    /**
     * 流程引擎动作和规则关系的配置缓存
     * 
     * @param actionid
     * @return
     */
    private static String getEngineActionConfig(String actionid) {
        String sql = "select RunRuleNum from BPM_EngineActionConfig where Actionid='" + actionid + "'";
        String ruleNum = Rdb.getValueBySql(sql);
        if (Tools.isBlank(ruleNum)) {
            BeanCtx.log("E", "根据动作" + actionid + "获得规则编号时出错，未找到对应的规则，请在BPM_EngineActionConfig表中进行配置！");
        }
        return ruleNum;
    }

    /**
     * 获得所有Java Bean的配置对像
     * 
     * @return 返回hashmap对像
     */
    private static HashMap<String, HashMap<String, String>> getBeanConfig() {
        HashMap<String, HashMap<String, String>> beanConfigCache = new HashMap<String, HashMap<String, String>>();
        String sql = "select * from BPM_BeanConfig";
        LinkedHashSet<Document> dc = Rdb.getAllDocumentsSetBySql("BPM_BeanConfig", sql);
        for (Document doc : dc) {
            HashMap<String, String> cfgmap = new HashMap<String, String>();
            cfgmap.put("classPath", doc.g("classPath"));
            cfgmap.put("singleton", doc.g("singleton"));
            beanConfigCache.put(doc.g("Beanid"), cfgmap);
        }
        return beanConfigCache;
    }

    public static int getStopCache() {
        return stopCache;
    }

    public static void setStopCache(int stopCache) {
        RdbCache.stopCache = stopCache;
    }

    /**
     * 清除全部缓存
     */
    public static void clear() {
        CacheManager.shutdown();
        removeAllServerCache("clear", "", "");
    }

}
