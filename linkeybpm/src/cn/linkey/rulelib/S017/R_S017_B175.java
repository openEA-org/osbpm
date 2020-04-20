package cn.linkey.rulelib.S017;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.*;

import javax.servlet.ServletOutputStream;

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
 * @RuleName:下载流程实例的附件
 * @author  admin
 * @version: 1.0
 */
final public class R_S017_B175 implements LinkeyRule {
	@Override
	public String run(HashMap<String, Object> params) throws Exception  {
	    //params为运行本规则时所传入的参数
		String enc = "utf-8";
		 String docUnid = BeanCtx.g("docUnid",true);
        if (Tools.isBlank(docUnid)) {return RestUtil.formartResultJson("0", "docUnid不能为空");}
	    
        try {
            String sql = "select FileName,FilePath from BPM_AttachmentsList where WF_OrUnid='" + docUnid + "'";
            Document fileDoc = Rdb.getDocumentBySql(sql);
            String folder = fileDoc.g("FilePath"); // 附件所在目录
            if (Tools.isBlank(folder)) {
                folder = "attachment/";
            }
            String filepath = BeanCtx.getAppPath() + folder; // 附件所在目录
            String downloadfilename = fileDoc.g("FileName"); // 从sql表中找到文件信息
            if (Tools.isBlank(downloadfilename)) {
                return RestUtil.formartResultJson("0","附件不存在!");
            }
            String filename = docUnid + downloadfilename.substring(downloadfilename.lastIndexOf(".")); // 得到文件扩展名称加上文件编号
            String fullfilepath = filepath + filename; // 文件所在硬盘的真实路径
            // BeanCtx.out(fullfilepath);

            /* 读取文件 */
            File file = new File(fullfilepath);
            /* 如果文件存在 */
            if (file.exists()) {
                BeanCtx.getResponse().reset();
                String extFileName = downloadfilename.substring(downloadfilename.lastIndexOf(".") + 1);
                downloadfilename = URLEncoder.encode(downloadfilename, enc);
                String contentType = "application/x-msdownload";
                if (extFileName.equals("jpg") || extFileName.equals("gif") || extFileName.equals("png") || extFileName.equals("txt")) {
                    // 不用提示下载
                }else {
                    // 直接提示下载
                    BeanCtx.getResponse().addHeader("Content-Disposition", "attachment; filename=\"" + downloadfilename + "\"");
                }
                BeanCtx.getResponse().setContentType(contentType);
                int fileLength = (int) file.length();
                BeanCtx.getResponse().setContentLength(fileLength);
                /* 如果文件长度大于0 */
                if (fileLength != 0) {
                    InputStream inStream = null;
                    ServletOutputStream servletOS = null;
                    try {
                        /* 创建输入流 */
                        inStream = new FileInputStream(file);
                        byte[] buf = new byte[4096];
                        /* 创建输出流 */
                        servletOS = BeanCtx.getResponse().getOutputStream();
                        int readLength;
                        while (((readLength = inStream.read(buf)) != -1)) {
                            servletOS.write(buf, 0, readLength);
                        }
                    }
                    catch (Exception e) {
                        return RestUtil.formartResultJson("0", "文件下载错误(" + Tools.decode(downloadfilename) + ")");
                    }
                    finally {
                        if (inStream != null) {
                            inStream.close();
                        }
                        if (servletOS != null) {
                            servletOS.flush();
                            servletOS.close();
                        }
                    }
                }
                attachlog(BeanCtx.g("wf_docunid", true), "", "查看附件(" + Tools.decode(downloadfilename) + ")");// 增加附件阅读记录
                return "";
            }
            else {
            	return RestUtil.formartResultJson("0", "找不到文件("+fullfilepath+")");
            }
        }
        catch (Exception e) {
            if (BeanCtx.isMobile()) {
                BeanCtx.log("D", "移动端下载附件有错误!");
            }
            else {
                BeanCtx.log(e, "E", "文件下载出错!");
            }
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
        String sql = "insert into BPM_AttachmentLog(WF_OrUnid,DocUnid,Userid,Processid,IP,Remark,WF_DocCreated) " + "values('" + Rdb.getNewUnid() + "','" + docUnid + "','" + BeanCtx.getUserName()
                + "(" + BeanCtx.getUserid() + ")" + "','" + processid + "','" + ip + "','" + remark + "','" + DateUtil.getNow() + "')";
        Rdb.execSql(sql);
    }
    
    
}