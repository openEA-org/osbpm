package cn.linkey.rulelib.S005;

import java.util.*;
import cn.linkey.dao.*;
import cn.linkey.util.*;
import cn.linkey.doc.*;
import cn.linkey.factory.*;
import cn.linkey.wf.*;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.org.LinkeyUser;
import java.io.File;  
import java.io.FileOutputStream;  
import java.io.IOException;  
import java.io.InputStream;  
import java.io.OutputStream;  
import java.util.List;  
  
import javax.servlet.ServletException;  
import javax.servlet.annotation.WebServlet;  
import javax.servlet.http.HttpServlet;  
import javax.servlet.http.HttpServletRequest;  
import javax.servlet.http.HttpServletResponse; 

  
import org.apache.commons.fileupload.FileItem;  
import org.apache.commons.fileupload.FileUploadException;  
import org.apache.commons.fileupload.disk.DiskFileItemFactory;  
import org.apache.commons.fileupload.servlet.ServletFileUpload; 
import java.awt.image.BufferedImage;
import java.awt.Image;
import javax.imageio.ImageIO;
import java.awt.image.AffineTransformOp;
import java.awt.geom.AffineTransform;
/**
 * @RuleName:头像上传
 * @author  designer
 * @version: 8.0
 * @Created: 2016-11-10 16:19:52
 */
final public class R_S005_B020 implements LinkeyRule {
	@Override
	public String run(HashMap<String, Object> params) throws Exception  {
	    //params为运行本规则时所传入的参数
	    //页面点击刷新
	    StringBuffer tmp = new StringBuffer();
        tmp.append("<a href=\"#\" onclick=\"check(").append(")\">").append("点击刷新").append("</a>");
        tmp.append("<script language="+"javascript"+"> ");
        tmp.append("function check(){ window.parent.parent.location.reload();}");
        tmp.append("</script>");
        
	   //存储新的头像，先删除旧的图片  

	   deleteFile(BeanCtx.getAppPath()+"\\attachment\\headpic\\png\\"+BeanCtx.getUserid()+".png");
	   deleteFile(BeanCtx.getAppPath()+"\\attachment\\headpic\\jpg\\"+BeanCtx.getUserid()+".jpg");
	   deleteFile(BeanCtx.getAppPath()+"\\attachment\\headpic\\gif\\"+BeanCtx.getUserid()+".gif");
	   deleteFile(BeanCtx.getAppPath()+"\\attachment\\headpic\\jpeg\\"+BeanCtx.getUserid()+".jpeg");
	  
	    String Geshi="";
        BeanCtx.getRequest().setCharacterEncoding("utf-8");  
        BeanCtx.getResponse().setCharacterEncoding("utf-8");  
        //1、创建一个DiskFileItemFactory工厂  
        DiskFileItemFactory factory = new DiskFileItemFactory();  
        //2、创建一个文件上传解析器  
        ServletFileUpload upload = new ServletFileUpload(factory);  
        //解决上传文件名的中文乱码  
        upload.setHeaderEncoding("UTF-8");   
        factory.setSizeThreshold(1024 * 500);//设置内存的临界值为500K  
        File linshi = new File(BeanCtx.getAppPath()+"\\attachment\\headpic\\tmp");//当超过500K的时候，存到一个临时文件夹中  
        factory.setRepository(linshi);  
        upload.setSizeMax(1024 * 1024 * 5);//设置上传的文件总的大小不能超过5M  
        
       
        
        try {  
            // 1. 得到 FileItem 的集合 items  
            List<FileItem> items = (List<FileItem>)upload.parseRequest(BeanCtx.getRequest());  
     
          //  List<FileItem> /* FileItem */items = upload.parseRequest(BeanCtx.getRequest());  
          // 2. 遍历 items:  
            for (FileItem item : items) {  
                
                // 若是一个一般的表单域, 打印信息  
                if (item.isFormField()) {  
                    String name = item.getFieldName();
                    String value = item.getString("utf-8");  
  
              //    System.out.println(name + ": " + value);  
                      
                      
                }  
                // 若是文件域则把文件保存到 e:\\files 目录下.  
                else {  
                    String fileName = item.getName();  
                    
                    int a=fileName.lastIndexOf(".");
                    fileName=fileName.substring(a+1,fileName.length());
                    Geshi=fileName;
                    fileName=BeanCtx.getUserid()+"."+fileName;
                 //   BeanCtx.out(fileName);
                    long sizeInBytes = item.getSize();  
                   
                    InputStream in = item.getInputStream();  
                    byte[] buffer = new byte[1024];  
                    int len = 0;
                    
                    String photoMkdirs = "";
                    if(Geshi.equals("jpg")||Geshi.equals("JPG")){
                        photoMkdirs = BeanCtx.getAppPath()+"\\attachment\\headpic\\jpg\\";
                        fileName = photoMkdirs + fileName;//文件最终上传的位置
                    }else if(Geshi.equals("png")||Geshi.equals("PNG")){
                        photoMkdirs = BeanCtx.getAppPath()+"\\attachment\\headpic\\png\\";
                        fileName = photoMkdirs + fileName;//文件最终上传的位置  
                    }else if(Geshi.equals("gif")||Geshi.equals("GIF")){
                        photoMkdirs = BeanCtx.getAppPath()+"\\attachment\\headpic\\gif\\";
                        fileName = photoMkdirs + fileName;//文件最终上传的位置 
                    }else if(Geshi.equals("jpeg")||Geshi.equals("JPEG")){
                        photoMkdirs = BeanCtx.getAppPath()+"\\attachment\\headpic\\jpeg\\";
                        fileName = photoMkdirs + fileName;//文件最终上传的位置
                    }
                    
                    //判断是否存在该目录，若不存在创建目录
                    if (photoMkdirs != null && !photoMkdirs.trim().isEmpty()) {
                        File mkdirsFile = new File(photoMkdirs);
            			if (!mkdirsFile.exists()) {
            				mkdirsFile.mkdirs();
            			}
                    }
                    
                    OutputStream out = new FileOutputStream(fileName);
                    while ((len = in.read(buffer)) != -1) {  
                        out.write(buffer, 0, len);  
                    }  
          
                    out.close();  
                    in.close();  
                    zoomImage(fileName,fileName,45,45);
                  
                    BeanCtx.p("更换成功，请重新刷新页面"+tmp);
               }  
            }  
  
        } catch (FileUploadException e) {  
            BeanCtx.p("更换失败，请重新刷新页面");
            e.printStackTrace();  
        }  
	    
	    return "";
	}

	
	
	
	
//按比例缩小照片
// src 相片位置，dest修改后储存位置， w修改后的宽， h修改后的高
 public static void zoomImage(String src,String dest,int w,int h) throws Exception {
        
        double wr=0,hr=0;
        File srcFile = new File(src);
        File destFile = new File(dest);
 try {
			 BufferedImage bufImg = ImageIO.read(srcFile); //读取图片
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        BufferedImage bufImg = ImageIO.read(srcFile); //读取图片
        Image Itemp = bufImg.getScaledInstance(w, h, bufImg.SCALE_SMOOTH);//设置缩放目标图片模板
        
        wr=w*1.0/bufImg.getWidth();     //获取缩放比例
        hr=h*1.0 / bufImg.getHeight();

        AffineTransformOp ato = new AffineTransformOp(AffineTransform.getScaleInstance(wr, hr), null);
        Itemp = ato.filter(bufImg, null);
        try {
            ImageIO.write((BufferedImage) Itemp,dest.substring(dest.lastIndexOf(".")+1), destFile); //写入缩减后的图片
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
	
	
//实现删除图片功能
public boolean deleteFile(String sPath) {  
  Boolean  flag = false;  
  File  file = new File(sPath);  
    // 路径为文件且不为空则进行删除  
   if (file.isFile() && file.exists()) {  
        file.delete();  
        flag = true;  
    }  
    return flag;  
}  
}