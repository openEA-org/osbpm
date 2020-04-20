package cn.linkey.rulelib.S024;

import java.util.*;
import cn.linkey.dao.*;
import cn.linkey.util.*;
import cn.linkey.doc.*;
import cn.linkey.factory.*;
import cn.linkey.rule.LinkeyRule;

/**
 * @RuleName:附件模版
 * @author admin
 * @version: 8.0
 * @Created: 2014-06-11 14:15
 */
final public class R_S024_E005 implements LinkeyRule {

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
        //获取附件模版路径
        String WordDocUrl = "";
        String Processid = BeanCtx.g("Processid", true);
        String DocUnid = BeanCtx.g("DocUnid", true);
        String FileName = Tools.decodeUrl(BeanCtx.g("FileName", true));
        String templateDocUnid = BeanCtx.g("TemplateDocUnid", true);

        String sql = "select * from BPM_AttachmentsList where DocUnid='" + DocUnid + "' and FileType='1' and FileName='" + FileName + "' and (DeleteFlag='0' or DeleteFlag is null)";
        Document fdoc = Rdb.getDocumentBySql(sql);
        if (!fdoc.isNull()) {
            FileName = fdoc.g("FileName");
            WordDocUrl = fdoc.g("FilePath") + fdoc.g("WF_OrUnid") + FileName.substring(FileName.indexOf("."));
        }
        else {
            //获取表单配置的附件模板
            sql = "select * from BPM_AttachmentsList where DocUnid='" + templateDocUnid + "' and FileType='0' and FileName='" + FileName + "' and (DeleteFlag='0' or DeleteFlag is null)";
            fdoc = Rdb.getDocumentBySql(sql);
            if (!fdoc.isNull()) {
                FileName = fdoc.g("FileName");
                WordDocUrl = fdoc.g("FilePath") + fdoc.g("WF_OrUnid") + FileName.substring(FileName.indexOf("."));
            }
        }
        doc.s("WordDocUrl", WordDocUrl);
        doc.s("FileName", FileName);

        if (BeanCtx.isMobile()) {
            //如果是移动终端则直接转向
            String url = "r?wf_num=R_S004_B004&filenum=" + fdoc.g("WF_OrUnid");
            BeanCtx.getResponse().sendRedirect(url);
            return "";
        }

        if (Tools.isNotBlank(Processid)) {
            //只有流程表单才检测是否有保存权限
            doc.s("btnSave", "true"); //默认可以保存文档
            String Nodeid = doc.g("Nodeid");
            if (!Nodeid.equals("")) {
                Document maindoc = new Document("BPM_AllDocument");
                maindoc.initByDocUnid(DocUnid);
                if (maindoc.g("WF_Status").equals("ARC")) {
                    doc.s("btnSave", "false"); //归档后不允许保存
                }
            }
            else {
                doc.s("btnSave", "false"); //归档后不允许保存
            }
        }

        return "1"; //成功必须返回1，否则表示退出并显示返回的字符串
    }

    public String onFormSave(Document doc, Document formDoc) {
        //当表单存盘前

        return "1"; //成功必须返回1，否则表示退出存盘
    }

}