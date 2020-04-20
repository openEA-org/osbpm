package cn.linkey.rulelib.S001;

import java.util.HashMap;

import cn.linkey.dao.CacheManager;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.*;
import cn.linkey.util.DateHoliday;
import cn.linkey.util.Tools;

/**
 * 清除缓存
 * 
 * @author Administrator
 * 
 */
public class R_S001_B018 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> nodeParams) {
        CacheManager.shutdown();
        ((DateHoliday) BeanCtx.getBean("DateHoliday")).clearStatus();
        BeanCtx.print(Tools.jmsg("ok", "成功清空(" + Tools.getServerIP() + ")系统缓存!"));
        return "";
    }
}