package cn.linkey.rulelib.S029;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.DateUtil;

/**
 * @RuleName:维护在线用户(每5分钟运行一次)
 * @author admin
 * @version: 8.0
 * @Created: 2014-05-13 15:15
 */
final public class R_S029_T002 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) {
        //这里编写定时执行的代码
        java.util.Calendar Cal = java.util.Calendar.getInstance();
        Cal.setTime(DateUtil.getDate());
        Cal.add(java.util.Calendar.MINUTE, -5);//当前时间减少5分钟
        //BeanCtx.out(DateUtil.formatDate(Cal.getTime(),"yyyy-MM-dd hh:mm"));
        //把5分钟还没有更新的用户删除掉
        String sql = "delete from BPM_OnlineUser where WF_LastModified < '" + DateUtil.formatDate(Cal.getTime(), "yyyy-MM-dd hh:mm") + "'";
        Rdb.execSql(sql);

        return "";
    }
}