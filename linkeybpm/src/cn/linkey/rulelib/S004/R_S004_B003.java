package cn.linkey.rulelib.S004;

import java.io.File;
import java.util.HashMap;
import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.DateUtil;

/**
 * 本规则负责把附件标记为已删除的，并不在硬盘中删除真实的文件记录
 * 
 * @author Administrator
 *
 */
public class R_S004_B003 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) {
        String docUnid = BeanCtx.g("wf_docunid", true);

        /* 删除真正的硬盘文件 */
        String appPath = BeanCtx.getAppPath(); //附件所在目录
        String sql = "select * from BPM_AttachmentsList where WF_OrUnid='" + docUnid + "'";
        Document doc = Rdb.getDocumentBySql("BPM_AttachmentsList", sql);
        if (!doc.isNull()) {
            String fileName = doc.g("FileName");
            String extName = fileName.substring(fileName.lastIndexOf(".")); //文件扩展名称
            String diskFileName = docUnid + extName;
            String realfilename = appPath + doc.g("FilePath") + diskFileName; //得到文件扩展名称加上文件编号
            //BeanCtx.out("realfilename="+realfilename);
            File file = new File(realfilename);
            if (file.exists()) {
                file.delete();
            }
            sql = "delete from BPM_AttachmentsList where WF_OrUnid='" + docUnid + "'";
            Rdb.execSql(sql);

            //仅更新标记不删除真实的文件
            //		String usql="update BPM_AttachmentsList set DeleteFlag='1' where WF_OrUnid='"+docUnid+"'";
            //		Rdb.execSql(usql);

            BeanCtx.print("{\"msg\":\"Deleted\"}");
            attachlog(doc.g("DocUnid"), doc.g("Processid"), "删除附件(" + fileName + ")");

        }
        else {
            BeanCtx.print("{\"msg\":\"File delete error!\"}");
        }
        return "";
    }

    /**
     * 记录附件操作日记
     */
    public static void attachlog(String docUnid, String processid, String remark) {
        String ip = "";
        if (BeanCtx.getRequest() != null) {
            ip = BeanCtx.getRequest().getRemoteAddr();
        }
        remark = remark.replace("'", "''");
        String sql = "insert into BPM_AttachmentLog(WF_OrUnid,DocUnid,Userid,Processid,IP,Remark,WF_DocCreated) " + "values('" + Rdb.getNewUnid() + "','" + docUnid + "','" + BeanCtx.getUserid()
                + "','" + processid + "','" + ip + "','" + remark + "','" + DateUtil.getNow() + "')";
        Rdb.execSql(sql);
    }

}