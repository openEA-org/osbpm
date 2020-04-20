package cn.linkey.rulelib.S004;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * 本规则负责显示附件列表
 * 
 * @author Administrator
 *
 */
public class R_S004_B001 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        String docUnid = BeanCtx.g("DocUnid", true);
        String readOnly = BeanCtx.g("ReadOnly", true); //应用表单是否处于只读状态
        String fdName = BeanCtx.g("FdName", true); //附件字段的id
        BeanCtx.p(getFileList(docUnid, fdName, readOnly.equals("true")));
        return "";
    }

    /**
     * 显示附件列表
     * 
     * @param docUnid 文档id
     * @param id 附件上传字段的id
     * @param action 是否为只读还是可编辑
     * @return
     */
    public String getFileList(String docUnid, String fdName, boolean readOnly) {
        StringBuilder fileList = new StringBuilder();
        String sql;
        if (Tools.isBlank(fdName)) { //显示所有附件
            sql = "select * from BPM_AttachmentsList where DocUnid='" + docUnid + "' and FileType='0' and DeleteFlag='0'";
        }
        else { //显示指定序号的附件
            sql = "select * from BPM_AttachmentsList where DocUnid='" + docUnid + "' and FdName='" + fdName + "' and FileType='0' and DeleteFlag='0'";
        }
        Document[] dc = Rdb.getAllDocumentsBySql("BPM_AttachmentsList", sql);
        int i = 0;
        String title;
        for (Document doc : dc) {
            i++;
            if (Tools.isBlank(doc.g("NodeName"))) {
                title = doc.g("WF_AddName_CN") + " upload at " + doc.g("WF_DocCreated");
            }
            else {
                title = BeanCtx.getMsg("Common", "AttachmentInfo", doc.g("NodeName"), doc.g("WF_AddName_CN"), doc.g("WF_DocCreated"));
            }
            fileList.append(i + ".<img src='linkey/bpm/images/icons/filelist.gif' > <a href='' title='" + title + "' onclick=\"OpenAttachment('" + doc.g("WF_OrUnid") + "');return false;\">" + doc.g("FileName") + "(" + doc.g("FileSize") + ")</a>");
            String delHtml = "";
            if (readOnly) {
                //显示时需要换行
                delHtml = "<br>";
            }
            else {
                //显示删除按扭
                delHtml = "<a href='' onclick=\"DelAttachment('" + doc.g("FileName") + "','" + doc.g("WF_OrUnid") + "','" + doc.g("FdName") + "');return false;\"><img src=\"linkey/bpm/images/icons/vwicn203.gif\" border='0' title='" + BeanCtx.getMsg("Common", "AttachmentDele") + "'></a><br>";
            }
            fileList.append(delHtml);
        }
        return fileList.toString();
    }
}