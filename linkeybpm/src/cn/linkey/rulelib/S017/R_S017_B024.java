package cn.linkey.rulelib.S017;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;

/**
 * @RuleName:通过规则统一调用回调服务
 * @author admin
 * @version: 8.0
 * @Created: 2014-07-11 17:17
 */
final public class R_S017_B024 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        //params为运行本规则时所传入的参数
        String url = "http://localhost:8080/bpm/WF_CallBackPort?wsdl";
        String targetNamespace = "http://server.ws.linkey.cn/";
        String arg = ""; //这里可以传json等字符串
        String syspwd = "";
        String format = "json";

        Document doc = Rdb.getDocumentByWsdl(url, targetNamespace, arg, syspwd, format);
        BeanCtx.p(doc.g("Msg"));

        return "";
    }
}