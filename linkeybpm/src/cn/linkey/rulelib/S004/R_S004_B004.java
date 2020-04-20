package cn.linkey.rulelib.S004;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.HashMap;

import javax.servlet.ServletOutputStream;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.DateUtil;
import cn.linkey.util.Tools;

/**
 * 本规则负责下载附件文件
 * 
 * @author Administrator
 *
 */
public class R_S004_B004 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) {
        String enc = "utf-8";
        String filenum = BeanCtx.g("filenum", true); // 要下载的文件编号
        try {
            String sql = "select FileName,FilePath from BPM_AttachmentsList where WF_OrUnid='" + filenum + "'";
            Document fileDoc = Rdb.getDocumentBySql(sql);
            String folder = fileDoc.g("FilePath"); // 附件所在目录
            if (Tools.isBlank(folder)) {
                folder = "attachment/";
            }
            String filepath = BeanCtx.getAppPath() + folder; // 附件所在目录
            String downloadfilename = fileDoc.g("FileName"); // 从sql表中找到文件信息
            if (Tools.isBlank(downloadfilename)) {
                BeanCtx.print("Error:can't find the file " + filenum);
                return "";
            }
            String filename = filenum + downloadfilename.substring(downloadfilename.lastIndexOf(".")); // 得到文件扩展名称加上文件编号
            String fullfilepath = filepath + filename; // 文件所在硬盘的真实路径
            // BeanCtx.out(fullfilepath);

            /* 读取文件 */
            File file = new File(fullfilepath);
            /* 如果文件存在 */
            if (file.exists()) {
                BeanCtx.getResponse().reset();
                String extFileName = downloadfilename.substring(downloadfilename.lastIndexOf(".") + 1);
                downloadfilename = URLEncoder.encode(downloadfilename, enc);
                String contentType = "applicatoin/octet-stream";
                if (extFileName.equals("jpg") || extFileName.equals("gif") || extFileName.equals("png") || extFileName.equals("txt")) {
                    // 不用提示下载
                }
                else {
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
                        BeanCtx.log(e, "E", "文件下载错误(" + Tools.decode(downloadfilename) + ")");
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
            }
            else {
                BeanCtx.print("Error: can't find the file " + filename);
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