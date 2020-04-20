package cn.linkey.ws.server;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

@WebService
public class WF_CallBack {

    /**
     * 
     * @param params 输入参数json字符串
     * @param userid 当前用户userid
     * @param syspwd 系统密码
     * @return 返回json字符串或xml字符串
     */
    @WebMethod
    public String callback(@WebParam(name = "params") String params, @WebParam(name = "userid") String userid, @WebParam(name = "syspwd") String syspwd) {

        return "{\"WF_SuccessFlag\":\"1\",\"Msg\":\"ok\"}";
    }

}
