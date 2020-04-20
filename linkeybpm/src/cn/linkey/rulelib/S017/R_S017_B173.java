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
 * @RuleName:删除流程实例的附件
 * @author  admin
 * @version: 1.0
 */
final public class R_S017_B173 implements LinkeyRule {
	@Override
	public String run(HashMap<String, Object> params) throws Exception  {
	    //params为运行本规则时所传入的参数
		 String docUnid = BeanCtx.g("docUnid",true);
        if (Tools.isBlank(docUnid)) {return RestUtil.formartResultJson("0", "docUnid不能为空");}
	    
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

            attachlog(doc.g("DocUnid"), doc.g("Processid"), "删除附件(" + fileName + ")");
            return RestUtil.formartResultJson("1", "附件成功删除");
        }
        else {
        	return RestUtil.formartResultJson("0", "附件不存在,删除失败");
        }
        

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