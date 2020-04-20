package cn.linkey.ws.server;

import java.util.HashMap;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.RuleConfig;
import cn.linkey.util.Tools;

@WebService
public class WF_RunRule {

    /**
     * @param rulenum 要运行的规则
     * @param params 要传的JSON字符串，字符串会传入到规则的params参数中去
     * @param userid 登录用户的id
     * @param sysid 系统id
     * @param syspwd 系统密码
     * @return 返回规则运行结果的字符串
     */
    @WebMethod
    public String runRule(@WebParam(name = "rulenum") String rulenum, @WebParam(name = "params") String params, @WebParam(name = "userid") String userid, @WebParam(name = "sysid") String sysid,
            @WebParam(name = "syspwd") String syspwd) {
        userid = Rdb.formatArg(userid);
        sysid = Rdb.formatArg(sysid);
        syspwd = Rdb.formatArg(syspwd);
        rulenum = Rdb.formatArg(rulenum);
        String appid = Tools.getAppidFromElNum(rulenum);
        try {
            BeanCtx.init(userid, null, null);

            //0.检测业务系统和密码是否正确
            String sql = "select * from BPM_BusinessSystem where Systemid='" + sysid + "' and SystemPwd='" + syspwd + "'";
            if (!Rdb.hasRecord(sql)) {
                return "Error:sysid or syspwd error!";
            }

            //1.检测是否是S017中的规则，为了安全起见不允许运行其他应用的规则
            if (!appid.equals("S017")) {
                return "Error:不能运行非S017应用中的规则!";
            }

            if (Tools.isNotBlank(rulenum)) {

                //检测是否有运行此规则的权限
                Document ruleDoc = ((RuleConfig) BeanCtx.getBean("RuleConfig")).getRuleDoc(rulenum);
                String roles = ruleDoc.g("Roles");
                if (Tools.isNotBlank(roles)) {
                    if (!BeanCtx.getLinkeyUser().inRoles(userid, roles)) {
                        return "Error:" + userid + "没有权限运行此规则!";
                    }
                }

                //开始调用规则并把参数转换为hashmap对像传入到rule中去
                HashMap<String, Object> runParams = new HashMap<String, Object>();
                runParams.put("userid", userid); //传入用户id
                runParams.put("sysid", sysid);//传入系统id
                try {
                    if (Tools.isNotBlank(params)) {
                        com.alibaba.fastjson.JSONObject jsonobj = com.alibaba.fastjson.JSON.parseObject(params);
                        for (String fdName : jsonobj.keySet()) {
                            runParams.put(fdName, Rdb.formatArg(jsonobj.getString(fdName))); //防止sql注入
                        }
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                    return "参数必须是JSON格式的...";
                }
                return BeanCtx.getExecuteEngine().run(rulenum, runParams);

            }
            else {
                return "Error:rulenum不能为空值...";
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            return "规则运行错误!";
        }
        finally {
            BeanCtx.close();
        }
    }

}
