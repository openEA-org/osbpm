package cn.linkey.rulelib.S017;

import java.io.File;
import java.util.*;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import cn.linkey.dao.*;
import cn.linkey.util.*;
import cn.linkey.doc.*;
import cn.linkey.factory.*;
import cn.linkey.wf.*;
import cn.linkey.rest.RestUtil;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.org.LinkeyUser;
/**
 * @RuleName:查询流程实例的附件
 * @author  admin
 * @version: 1.0
 */
final public class R_S017_B174 implements LinkeyRule {
	@Override
	public String run(HashMap<String, Object> params) throws Exception  {
	    //params为运行本规则时所传入的参数
		 String docUnid = BeanCtx.g("docUnid",true);//流程实例id
		 String fileFieldName = BeanCtx.g("fileFieldName",true); //附件上传控件的id
        if (Tools.isBlank(docUnid)) {return RestUtil.formartResultJson("0", "docUnid不能为空");}
	    
        String sql;
        if (Tools.isBlank(fileFieldName)) { //显示所有附件
            sql = "select * from BPM_AttachmentsList where DocUnid='" + docUnid + "' and FileType='0' and DeleteFlag='0'";
        }
        else { //显示指定序号的附件
            sql = "select * from BPM_AttachmentsList where DocUnid='" + docUnid + "' and FdName='" + fileFieldName + "' and FileType='0' and DeleteFlag='0'";
        }
        Document[] dc = Rdb.getAllDocumentsBySql("BPM_AttachmentsList", sql);
        for (Document doc : dc) {
            String fileName = doc.g("FileName");
            fileName = doc.g("FilePath") + doc.g("WF_OrUnid") + fileName.substring(fileName.lastIndexOf("."));
            doc.s("FilePath", fileName);
        }
        String jsonStr=Documents.dc2json(dc, "");
        return RestUtil.formartResultJson("1", "",jsonStr);

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
        String sql = "insert into BPM_AttachmentLog(WF_OrUnid,DocUnid,Userid,Processid,IP,Remark,WF_DocCreated) " + "values('" + Rdb.getNewUnid() + "','" + docUnid + "','" + BeanCtx.getUserName()
                + "(" + BeanCtx.getUserid() + ")" + "','" + processid + "','" + ip + "','" + remark + "','" + DateUtil.getNow() + "')";
        Rdb.execSql(sql);
    }
    
    
}