package cn.linkey.rulelib.S001;

import java.util.HashMap;

import cn.linkey.dao.RdbCache;
import cn.linkey.doc.Document;
import cn.linkey.rule.LinkeyRule;

/**
 * @RuleName:应用级配置表单事件
 * @author admin
 * @version: 8.0
 * @Created: 2015-04-08 13:57
 */
final public class R_S001_E076 implements LinkeyRule {

    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        //获取事件运行参数
        Document formDoc = (Document) params.get("FormDoc"); //表单配置文档
        Document doc = (Document) params.get("DataDoc"); //数据主文档
        String eventName = (String) params.get("EventName");//事件名称
        if (eventName.equals("onFormOpen")) {
            String readOnly = (String) params.get("ReadOnly"); //1表示只读，0表示编辑
            return onFormOpen(doc, formDoc, readOnly);
        }
        else if (eventName.equals("onFormSave")) {
            return onFormSave(doc, formDoc);
        }
        return "1";
    }

    public String onFormOpen(Document doc, Document formDoc, String readOnly) throws Exception {
        //当表单打开时
        if (readOnly.equals("1")) {
            return "1";
        } //如果是阅读状态则可不执行
        if (doc.isNewDoc()) {
            //可以对表单字段进行初始化如:doc.s("fdname",fdvalue),可以获取字段值 doc.g("fdname")

        }
        return "1"; //成功必须返回1，否则表示退出并显示返回的字符串
    }

    public String onFormSave(Document doc, Document formDoc) throws Exception {
        //当表单存盘前
        //清除系统中的缓存
        String configid = doc.g("Configid");
        String appid = doc.g("WF_Appid");
        if (appid.indexOf(",") != -1) {
            appid = appid.substring(0, appid.indexOf(","));
        }
        configid = appid + "_" + configid;
        // 		BeanCtx.out(configid);
        RdbCache.removeSystemCache("BPM_AppSystemConfig", "", configid);

        return "1"; //成功必须返回1，否则表示退出存盘
    }

}