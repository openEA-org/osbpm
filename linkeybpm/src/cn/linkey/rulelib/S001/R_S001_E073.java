package cn.linkey.rulelib.S001;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.FileUtils;

import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.DateUtil;

/**
 * @RuleName:查看控制台日记
 * @author admin
 * @version: 8.0
 * @Created: 2014-07-18 14:49
 */
final public class R_S001_E073 implements LinkeyRule {

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

        String filePath = BeanCtx.getWebAppsPath() + "log/" + DateUtil.getDateNum() + ".log";
        File file = new File(filePath);
        StringBuilder logStr = new StringBuilder();
        try {
            //			String logStr=FileUtils.readFileToString(file);
            List<String> contents = FileUtils.readLines(file);
            int start = 0, max = 2000, size = contents.size();
            if (size > max) {
                start = size - max;
            }
            for (String line : contents) {
                if (start < size) {
                    line = line.replace("	", "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
                    logStr.append("<br>").append(line);
                }
                start++;
            }
            //			logStr=logStr.replace("\n","<br>");
            doc.s("logStr", logStr);
        }
        catch (Exception e) {
            //e.printStackTrace();
            doc.s("logStr", "错误:未找到系统日记文件...");
        }

        return "1"; //成功必须返回1，否则表示退出并显示返回的字符串
    }

    public String onFormSave(Document doc, Document formDoc) throws Exception {
        //当表单存盘前
        //清空日记
        String filePath = BeanCtx.getWebAppsPath() + "log/" + DateUtil.getDateNum() + ".log";
        File file = new File(filePath);
        try {
            FileUtils.write(file, "");//清空日记
        }
        catch (Exception e) {
            e.printStackTrace();
            BeanCtx.p("错误:未找到系统日记文件...");
        }
        return "日记已成功清空!"; //成功必须返回1，否则表示退出存盘
    }

}