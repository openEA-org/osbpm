package cn.linkey.rulelib.S014;

import java.util.HashMap;

import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;
import cn.linkey.wf.ModNode;

/**
 * @RuleName:流程实例监控页面事件
 * @author admin
 * @version: 8.0
 * @Created: 2014-05-15 21:48
 */
final public class R_S014_E005 implements LinkeyRule {

    @Override
    public String run(HashMap<String, Object> params) {
        //获取事件运行参数
        Document pageDoc = (Document) params.get("PageDoc"); //页面配置文档
        Document doc = (Document) params.get("DataDoc"); //数据主文档
        String eventName = (String) params.get("EventName");//事件名称
        if (eventName.equals("onPageOpen")) {
            return onPageOpen(doc, pageDoc);
        }
        return "1";
    }

    public String onPageOpen(Document doc, Document pageDoc) {
        //可以对页面的[X]字段名[/X]进行初始化如:doc.s("fdname",fdvalue)
        //看当前用户是否是流程管理员
        String processid = doc.g("Processid");
        String docUind = doc.g("DocUnid");
        ModNode modNode = (ModNode) BeanCtx.getBean("ModNode");
        Document pdoc = modNode.getNodeDoc(processid, "Process");
        String processOwner = pdoc.g("ProcessOwner");
        //BeanCtx.out("processOwner="+processOwner);
        //BeanCtx.out(BeanCtx.getUserRoles(BeanCtx.getUserid()));
        if (BeanCtx.getUserRoles(BeanCtx.getUserid()).contains("RS005")) {
            return "1";
        }
        if (Tools.isBlank(processOwner) || !BeanCtx.getLinkeyUser().inRoles(BeanCtx.getUserid(), processOwner)) {
            //说明不是管理员
            return "您不是本流程的管理员，无权对本流程进行监控";
        }
        return "1"; //成功必须返回1，否则表示退出并显示返回的字符串
    }

}