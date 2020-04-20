package cn.linkey.rulelib.S001;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

import javax.servlet.ServletOutputStream;

import cn.linkey.app.AppUtil;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * download outfile 本规则只能下载打包目录下的文件
 * 
 * @author Administrator
 * 
 */
public class R_S001_B020 implements LinkeyRule {

    @Override
    public String run(HashMap<String, Object> params) {
        String fileName = BeanCtx.g("filename", true);
        
        //20180410 修改解码方法，兼容Oracle数据库，使用Tools.encode(fileName)进行解码
        try {
			if(fileName.equals(new String(fileName.getBytes("ISO-8859-1"), "ISO-8859-1")))
			{
				fileName = Tools.decodeUrl(fileName);
			}else{
				fileName = Tools.decode(fileName);
			}
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
        
        String downloadfileName = fileName;
        int spos = fileName.lastIndexOf("/");
        if (spos != -1) {
            downloadfileName = fileName.substring(spos + 1);
        }
        String filepath = AppUtil.getPackagePath(); // 导出文件包所在目录
        String fullfilepath = filepath + fileName;

        // 开始下载打包好的文件
        String contentType = "application/x-msdownload";
        String enc = "utf-8";
        try {
            /* 读取文件 */
            File file = new File(fullfilepath);
            /* 如果文件存在 */
            if (file.exists()) {
                downloadfileName = URLEncoder.encode(downloadfileName, enc);
                BeanCtx.getResponse().reset();
                BeanCtx.getResponse().setContentType(contentType);
                BeanCtx.getResponse().addHeader("Content-Disposition", "attachment; filename=\"" + downloadfileName + "\"");
                int fileLength = (int) file.length();
                BeanCtx.getResponse().setContentLength(fileLength);
                /* 如果文件长度大于0 */
                if (fileLength != 0) {
                    /* 创建输入流 */
                    InputStream inStream = new FileInputStream(file);
                    byte[] buf = new byte[4096];
                    /* 创建输出流 */
                    ServletOutputStream servletOS = BeanCtx.getResponse().getOutputStream();
                    int readLength;
                    while (((readLength = inStream.read(buf)) != -1)) {
                        servletOS.write(buf, 0, readLength);
                    }
                    inStream.close();
                    servletOS.flush();
                    servletOS.close();
                }
            }
            else {
                BeanCtx.print("Error: can't find the file " + fileName);
            }
        }
        catch (Exception e) {
            BeanCtx.log(e, "E", "文件下载出错!");
            BeanCtx.print("File download error " + fileName);
        }
        return "";
    }
}
