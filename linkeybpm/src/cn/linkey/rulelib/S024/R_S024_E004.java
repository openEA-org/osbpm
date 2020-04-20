package cn.linkey.rulelib.S024;

import java.util.*;
import cn.linkey.dao.*;
import cn.linkey.util.*;
import cn.linkey.wf.ModNode;
import cn.linkey.doc.*;
import cn.linkey.factory.*;
import cn.linkey.rule.LinkeyRule;

/**
 * @RuleName:Word正文编辑
 * @author admin
 * @version: 8.0
 * @Created: 2014-06-03 09:21
 */
final public class R_S024_E004 implements LinkeyRule {

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
        //获取Word正文路径
        String WordDocUrl = "";
        String Processid = BeanCtx.g("Processid", true);
        String DocUnid = BeanCtx.g("DocUnid", true);
        String Nodeid = BeanCtx.g("Nodeid", true);
        String FileName = BeanCtx.g("FileName", true);
        ModNode modNode = (ModNode) BeanCtx.getBean("ModNode");
        if (Processid.equals("undefined")) {
            return "错误:没有指定流程id或者错误的Processid参数!";
        }

        //1.首先获得Word正文的模板或已保存过的正文文件
        String sql = "select * from BPM_AttachmentsList where DocUnid='" + DocUnid + "' and FileType='1' and FileName='" + FileName + "' and (DeleteFlag='0' or DeleteFlag is null)";
        //		BeanCtx.out("sql="+sql);
        Document fdoc = Rdb.getDocumentBySql(sql);
        if (!fdoc.isNull()) {
            //word正文已存在
            FileName = fdoc.g("FileName"); //重新设定文件名称
            //			BeanCtx.out("word正文已存在="+FileName);
            WordDocUrl = fdoc.g("FilePath") + fdoc.g("WF_OrUnid") + FileName.substring(FileName.indexOf("."));
        }
        else {
            //获取流程设置的Word正文模版
            Document pdoc = modNode.getNodeDoc(Processid, "Process");
            if (Tools.isBlank(pdoc.g("WordTemplate"))) {
                WordDocUrl = "linkey/bpm/word/default.doc";
            }
            else {
                sql = "select * from BPM_AttachmentsList where DocUnid='" + pdoc.g("WordTemplate") + "'";
                fdoc = Rdb.getDocumentBySql(sql);
                WordDocUrl = fdoc.g("FilePath") + fdoc.g("WF_OrUnid") + FileName.substring(FileName.indexOf("."));
            }
        }
        
        //20190218 自定义WORD模板路径不对无法打开问题=======start
        WordDocUrl = WordDocUrl.replace("\\", "/");
        //===============================================end
        
        
//        		BeanCtx.out("WordDocUrl="+WordDocUrl);

        doc.s("WordDocUrl", WordDocUrl);
        doc.s("UserName", BeanCtx.getUserName());
        doc.s("FileName", FileName);

        if (BeanCtx.isMobile()) {
            //如果是移动终端则直接转向
            String url = "r?wf_num=R_S004_B004&filenum=" + fdoc.g("WF_OrUnid");
            BeanCtx.getResponse().sendRedirect(url);
            return "";
        }

        //2.获取当前环节所有属性
        Document modNodeDoc = modNode.getNodeDoc(Processid, Nodeid);
        if (!modNodeDoc.isNull()) {
            modNodeDoc.copyAllItems(doc); //把当前环节的所有配置全部拷贝给当前文档
            if (!modNodeDoc.g("CanNotEditWord").equals("1")) {
                doc.s("btnCanNotEditWord", "true"); //显示保存按扭
            }
            if (modNodeDoc.g("CanSignFlag").equals("1")) {
                doc.s("btnCanSignFlag", "true"); //允许电子盖章
            }
            if (modNodeDoc.g("CanHongTou").equals("1")) {
                doc.s("btnCanHongTou", "true"); //允许红头文件
            }
            if (modNodeDoc.g("CanSaveCopyDoc").equals("1")) {
                doc.s("btnSaveCopyDoc", "true"); //允许保存副本
            }
        }

        //3.获取是否已经保存过副本
        int spos = FileName.lastIndexOf(".");
        String copyFileName = FileName.substring(0, spos) + "(副本)" + FileName.substring(spos);

        sql = "select * from BPM_AttachmentsList where DocUnid='" + DocUnid + "' and FileType='1' and FileName='" + copyFileName + "' and (DeleteFlag='0' or DeleteFlag is null)";
        if (Rdb.hasRecord(sql)) {
            doc.s("HadSaveFb", "1");
        }

        return "1"; //成功必须返回1，否则表示退出并显示返回的字符串
    }

    public String onFormSave(Document doc, Document formDoc) {
        //当表单存盘前

        return "1"; //成功必须返回1，否则表示退出存盘
    }

}