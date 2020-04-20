package cn.linkey.rulelib.S017;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rest.RestUtil;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.DateUtil;
import cn.linkey.util.MessageUtil;
import cn.linkey.util.Tools;

/**
 * @RuleName:申请人催办
 * @author admin
 * @version: 1.0
 */
final public class R_S017_B167 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) {
        //params为运行本规则时所传入的参数
        String docUnid = BeanCtx.g("docUnid");
        String msg = BeanCtx.g("msg");
        if (Tools.isBlank(docUnid)) {
            BeanCtx.p(RestUtil.formartResultJson("0", "docUnid不能为空"));
            return "";
        }
		String sql="select * from bpm_maindata where Wf_orUnid='"+docUnid+"'";
		Document doc=Rdb.getDocumentBySql(sql);
        if (doc.isNull()) {
            BeanCtx.p(RestUtil.formartResultJson("1", "要催办的文档不存在!"));
        }else {
        	String author=doc.g("WF_Author"); //当前审批的用户id,发送邮件催办
            Document mailDoc = BeanCtx.getDocumentBean("BPM_MailBox");
            mailDoc.s("WF_Appid", doc.g("Wf_AppId"));
            mailDoc.s("MailTitle","流程["+doc.g("Subject")+"]请尽快办理!");
            mailDoc.s("MailBody", msg+" <a href='"+getDocLink(docUnid)+"' target=_blank >点击打开</a>");
            mailDoc.s("SendTo", author);
            mailDoc.s("CopyTo", "");
            mailDoc.s("Userid", BeanCtx.getUserid());
            mailDoc.save();
            BeanCtx.p(RestUtil.formartResultJson("1", "已成功发送催办邮件给("+doc.g("WF_Author_CN")+")!"));
        }

        return "";
    }
    
    public String getDocLink(String docUnid) {
        String url = BeanCtx.getSystemConfig("HttpServerUrl") + "/rule?wf_num=R_S003_B052&wf_docunid=" + docUnid;
        return url;
    }
    
}