package cn.linkey.rulelib.S005;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;

/**
 * @RuleName:获取用户UNID
 * @author admin
 * @version: 8.0
 * @Created: 2014-05-28 15:43
 */
final public class R_S005_E018 implements LinkeyRule {

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
        String Userid = BeanCtx.getUserid();
        String useSQL = "Select WF_OrUnid FROM BPM_OrgUserList where Userid='" + Userid + "'";
        String userUnid = Rdb.getValueBySql(useSQL);
        doc.s("unidvalue", userUnid);
        return "1"; //成功必须返回1，否则表示退出并显示返回的字符串
    }

}