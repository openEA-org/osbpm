package cn.linkey.rulelib.S029;

import java.util.*;

import cn.linkey.dao.Rdb;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.*;

/**
 * @RuleName:清除日记信息(每月最后一个星期六执行)
 * @author admin
 * @version: 8.0
 * @Created: 2014-05-15 16:40
 */
final public class R_S029_T003 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) {
        //这里编写定时执行的代码
        java.util.Calendar Cal = java.util.Calendar.getInstance();
        Cal.setTime(DateUtil.getDate());
        Cal.add(java.util.Calendar.DATE, -5);//当前时间减少6天,执删除大于6天的全部日记

        //控制台日记删除
        String sql = "delete from BPM_ConsoleError where WF_DocCreated>'" + DateUtil.formatDate(Cal.getTime(), "yyyy-MM-dd hh:mm") + "'";
        Rdb.execSql(sql);

        //删用户操作日记
        sql = "delete from BPM_UserActionLog where WF_DocCreated>'" + DateUtil.formatDate(Cal.getTime(), "yyyy-MM-dd hh:mm") + "'";
        Rdb.execSql(sql);

        //系统日记
        sql = "delete from BPM_ConsoleLog where WF_DocCreated>'" + DateUtil.formatDate(Cal.getTime(), "yyyy-MM-dd hh:mm") + "'";
        Rdb.execSql(sql);

        return "";
    }
}