package cn.linkey.rulelib.S001;

import java.io.File;
import java.util.HashMap;

import org.apache.commons.io.FileUtils;

import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * @RuleName:应用上传附件
 * @author admin
 * @version: 8.0
 * @Created: 2015-10-19 13:58
 */
final public class R_S001_E082 implements LinkeyRule {

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
        Document[] dc = doc.getAttachmentsDoc();
        if (dc.length == 0) {
            return "请上传一个文件!";
        }

        //1.获得当前应用存附件的目录
        // 202001 调整附件上传地址配置顺序，先数据库，没有则从配置文件，再没有则使用默认地址
        // SysAppAttachmentPath配置支持绝对地址，也支持相对tomcat目录地址 ，eg: linkey/bpm/appfile/
        String configid = "SysAppAttachmentPath";
        String appPath = BeanCtx.getSystemConfig(configid); 
        if (Tools.isBlank(appPath)) {
            appPath = Tools.getProperty(configid);
        }else{
        	if(appPath.indexOf(":") == -1 || "/".equals(appPath.substring(0, 1))){ // 判断是否是绝对路径(window和linux)
        		appPath = BeanCtx.getWebAppsPath() + appPath;
        	}
        }
        if(Tools.isBlank(appPath)){
        	appPath = BeanCtx.getWebAppsPath() + "linkey/bpm/appfile/";
        }
        
        
        String appid = doc.g("WF_Appid");
        appPath += appid;

        //得到刚上传的文件移动到应用目录中去
        String filePath = BeanCtx.getAppPath();
        for (Document fdoc : dc) {
            String fileName = fdoc.g("FileName");
            fileName = fdoc.g("FilePath") + fdoc.g("WF_OrUnid") + fileName.substring(fileName.lastIndexOf("."));
            String fullFilePath = filePath + fileName;
            File srcFile = FileUtils.getFile(fullFilePath); //当前文件
            //			BeanCtx.out("srcFile="+fullFilePath);
            //			BeanCtx.out("destFile="+appPath+"/"+fileName);
            File destFile = FileUtils.getFile(appPath + "/" + fdoc.g("FileName")); //目标文件
            FileUtils.copyFile(srcFile, destFile); //拷贝过去
            srcFile.delete();
        }

        return "文件上传成功!"; //成功必须返回1，否则表示退出存盘
    }

}