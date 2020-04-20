package cn.linkey.rulelib.S005;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.DateUtil;
import cn.linkey.util.Tools;

/**
 * @RuleName:定时请求获得在线用户和待办
 * @author admin
 * @version: 8.0
 * @Created: 2014-05-13 11:16
 */
final public class R_S005_B002 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) {
        //params为运行本规则时所传入的参数
        BeanCtx.p("{\"TodoNum\":\"" + getToDoNum() + "\",\"UserNum\":\"" + onlineUser() + "\",\"UserName\":\"" + BeanCtx.getUserName() + "\"}");
        return "";
    }

    /**
     * 获得待办数
     * 
     * @return
     */
    public String getToDoNum() {
        String appid = BeanCtx.g("WF_Appid", true);
        String sql = "";
        if (Tools.isBlank(appid)) {
            sql = "select count(*) as TotalNum from BPM_UserToDo where Userid='" + BeanCtx.getUserid() + "'";
        }
        else {
            sql = "select count(*) as TotalNum from BPM_UserToDo where Userid='" + BeanCtx.getUserid() + "' and WF_Appid='" + appid + "'";
        }
        return Rdb.getValueBySql(sql);
    }

    /**
     * 获得在线用户数
     * 
     * @return
     */
    public String onlineUser() {
        //看当前用户是否已在线，如果不在则加入一条记录
        String sql = "select WF_OrUnid from BPM_OnlineUser where Userid='" + BeanCtx.getUserid() + "'";
        if (!Rdb.hasRecord(sql)) {
            //创建一个在线用户的记录
            Document doc = BeanCtx.getDocumentBean("BPM_OnlineUser");
            doc.s("Userid", BeanCtx.getUserid());
            doc.s("UserName", BeanCtx.getUserName());
            doc.s("WF_ServerName", Tools.getProperty("WF_ServerName"));
            doc.s("WF_LoginIP", BeanCtx.getRequest().getRemoteAddr());
            doc.save();
        }
        else {
            //更新一次最后在线时间
            sql = "update BPM_OnlineUser set WF_LastModified='" + DateUtil.getNow() + "' where Userid='" + BeanCtx.getUserid() + "'";
            Rdb.execSql(sql);
        }
        sql = "select count(*) from BPM_OnlineUser";
        String onlineNum = Rdb.getValueBySql(sql);
        return onlineNum;
    }

}