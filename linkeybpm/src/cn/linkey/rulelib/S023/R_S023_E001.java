package cn.linkey.rulelib.S023;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;

/**
 * @RuleName:移动应用首页事件
 * @author admin
 * @version: 8.0
 * @Created: 2014-07-30 17:07
 */
final public class R_S023_E001 implements LinkeyRule {

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

        //我的待办数
        String sql = "select count(*) as TotalNum from BPM_UserToDo where Userid='" + BeanCtx.getUserid() + "'";
        doc.s("ToDoNum", Rdb.getValueBySql(sql));

        //我的待阅数
        sql = "select count(*) as TotalNum from BPM_MainData where ','+WF_CopyUser+',' like '%," + BeanCtx.getUserid() + ",%'";
        doc.s("ReadNum", Rdb.getValueBySql(sql));

        //我的已办
        sql = "select count(*) as TotalNum from BPM_MainData where ','+WF_EndUser+',' like '%," + BeanCtx.getUserid() + ",%'";
        doc.s("HadDoNum", Rdb.getValueBySql(sql));

        //我申请的
        sql = "select count(*) as TotalNum from BPM_AllDocument where WF_AddName='" + BeanCtx.getUserid() + "'";
        doc.s("MyReqNum", Rdb.getValueBySql(sql));

        //我委托的
        sql = "select count(*) as TotalNum from BPM_MainData where ','+WF_SourceEntrustUserid+',' like '%," + BeanCtx.getUserid() + ",%'";
        doc.s("EntrustNum", Rdb.getValueBySql(sql));

        doc.s("UserName", BeanCtx.getUserName());

        return "1"; //成功必须返回1，否则表示退出并显示返回的字符串
    }

}