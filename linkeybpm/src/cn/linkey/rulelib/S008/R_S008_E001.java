package cn.linkey.rulelib.S008;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;

/**
 * @RuleName:首页顶部区域事件
 * @author admin
 * @version: 8.0
 * @Created: 2015-10-11 14:19
 */
final public class R_S008_E001 implements LinkeyRule {

    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        //获取事件运行参数
        Document pageDoc = (Document) params.get("PageDoc"); //页面配置文档
        Document doc = (Document) params.get("DataDoc"); //数据主文档
        String eventName = (String) params.get("EventName");//事件名称
        if (eventName.equals("onPageOpen")) {
            return onPageOpen(doc, pageDoc);
        }
        return "1";
    }

    public String onPageOpen(Document doc, Document pageDoc) throws Exception {
        //可以对页面的[X]字段名[/X]进行初始化如:doc.s("fdname",fdvalue)
        String sql = "select count(*) as TotalNum from BPM_UserToDo where Userid='" + BeanCtx.getUserid() + "'";
        String todonum = Rdb.getValueBySql(sql);
        doc.s("NewToDoNum", todonum);
        doc.s("UserName", BeanCtx.getUserName());

        sql = "select count(1) from APP_A001_ReceiveList where ReceiveUserid='" + BeanCtx.getUserid() + "' and ReadFlag='0'";
        String smsnum = Rdb.getValueBySql(sql);
        doc.s("smsnum", smsnum);

        return "1"; //成功必须返回1，否则表示退出并显示返回的字符串
    }

}