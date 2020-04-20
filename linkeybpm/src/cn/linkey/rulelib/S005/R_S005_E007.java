package cn.linkey.rulelib.S005;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.wf.InsUser;
import cn.linkey.wf.ModNode;

/**
 * @RuleName:Word正文
 * @author admin
 * @version: 8.0
 * @Created: 2014-06-03 09:21
 */
final public class R_S005_E007 implements LinkeyRule {

    @Override
    public String run(HashMap<String, Object> params) {
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

    public String onFormOpen(Document doc, Document formDoc, String readOnly) {
        //当表单打开时
        if (readOnly.equals("1")) {
            return "1";
        } //如果是阅读状态则可不执行
        if (doc.isNewDoc()) {
            //可以对表单字段进行初始化如:doc.s("fdname",fdvalue),可以获取字段值 doc.g("fdname")

        }
        //获取Word正文路径
        String WordDocUrl = "";
        String Processid = doc.g("Processid");
        String DocUnid = doc.g("DocUnid");
        String FileName = doc.g("FileName");
        try {
            FileName = new String(FileName.getBytes("ISO-8859-1"), "utf-8");
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String sql = "select * from BPM_AttachmentsList where DocUnid='" + DocUnid + "' and FileType='1' and FileName='" + FileName + "' and (DeleteFlag='0' or DeleteFlag is null)";
        Document fdoc = Rdb.getDocumentBySql(sql);
        if (fdoc.isNull()) {
            //BeanCtx.out(sql);
        }
        else {
            FileName = fdoc.g("FileName");
            WordDocUrl = fdoc.g("FilePath") + fdoc.g("WF_OrUnid") + FileName.substring(FileName.indexOf("."));
        }
        if (WordDocUrl.equals("")) {
            //获取流程设置的Word模版
            sql = "select WordTemplate from BPM_ModProcessList where Processid='" + Processid + "' and Nodeid='Process' and NodeType='Process'";
            Document pdoc = Rdb.getDocumentBySql(sql);
            if (pdoc.isNull()) {
                //BeanCtx.out(sql);
            }
            else {
                sql = "select * from BPM_AttachmentsList where DocUnid='" + pdoc.g("WordTemplate") + "'";
                fdoc = Rdb.getDocumentBySql(sql);
                if (fdoc.isNull()) {
                    //BeanCtx.out(sql);
                }
                else {
                    FileName = fdoc.g("FileName");
                    WordDocUrl = fdoc.g("FilePath") + fdoc.g("WF_OrUnid") + FileName.substring(FileName.indexOf("."));
                }
            }

        }
        doc.s("WordDocUrl", WordDocUrl);
        doc.s("wordurl", "form?wf_num=F_S005_A004");
        //检验权限
        String Nodeid = doc.g("Nodeid");
        if (!Nodeid.equals("")) {
            //只读
            Document insProcessDoc = new Document("BPM_AllDocument");
            insProcessDoc.initByDocUnid(DocUnid);
            if (insProcessDoc.isNewDoc()) {
                doc.s("WF_UserCurrentNodeid", Nodeid);
                doc.s("NewDocFlag", "1");
            }
            else if (!insProcessDoc.g("WF_Status").equals("ARC")) {
                InsUser insUser = new InsUser();
                Document insUserDoc = insUser.getInsUserDoc(Processid, Nodeid, DocUnid, BeanCtx.getUserid(), "Current");
                if (insUserDoc.isNull()) {
                    doc.s("WF_UserCurrentNodeid", "");
                }
                else {
                    doc.s("WF_UserCurrentNodeid", Nodeid);
                }
            }
            //按钮权限
            //获取环节属性
            ModNode modNode = new ModNode();
            Document modNodeDoc = modNode.getNodeDoc(Processid, Nodeid);
            doc.s("bSave", "true"); //保存文档
            doc.s("bSign", "true"); //手写批注
            if (!modNodeDoc.isNull()) {
                if (!modNodeDoc.g("CanNotEditWord").equals("")) {
                    doc.s("bSave", "false");
                    doc.s("bSign", "false");
                }
            }
        }

        return "1"; //成功必须返回1，否则表示退出并显示返回的字符串
    }

    public String onFormSave(Document doc, Document formDoc) {
        //当表单存盘前

        return "1"; //成功必须返回1，否则表示退出存盘
    }

}