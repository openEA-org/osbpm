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
import cn.linkey.rest.RestUtil;
import cn.linkey.rule.LinkeyRule;

/**
 * @RuleName:更新当前登录用户的签名信息
 * @author  admin
 * @version: 1.0
 */
final public class R_S017_B178 implements LinkeyRule {
	@Override
	public String run(HashMap<String, Object> params) throws Exception  {
	    //params为运行本规则时所传入的参数
		String docUnid=BeanCtx.g("docUnid"); //如果存在则更新，不存在则创建
		String userId=BeanCtx.getUserid();
		String itemid="StampSign";
		String remark=BeanCtx.g("remark"); //备注
        String sql = "select * from BPM_Template where WF_OrUnid='"+docUnid+"'";
       Document doc=Rdb.getDocumentBySql(sql);
       if(doc.isNull()){
    	   //签名信息不存在
    	   docUnid= Rdb.getNewUnid();
    	   doc.s("WF_OrUnid",docUnid);
    	   doc.s("Readers", userId);
    	   doc.s("Readers_show", BeanCtx.getUserName());
    	   doc.s("Subject", userId);
    	   doc.s("ItemId", itemid);
    	   doc.s("Remark", remark);
       }else{
    	   //已存在更新
    	   doc.s("Readers", userId);
    	   doc.s("Readers_show", BeanCtx.getUserName());
    	   doc.s("Subject", userId);
    	   doc.s("ItemId", itemid);
    	   doc.s("Remark", remark);
    	   doc.removeAllAttachments(true); //先删除已有签名
       }
       doc.save();
       uploadFile(docUnid,"file1","","");
       
       return RestUtil.formartResultJson("1", "签名更新成功");
       
	}
    
	/**
	 * 上传附件
	 */
    public void uploadFile(String docUnid,String fdName,String processId,String nodeName) {
        try {
            BeanCtx.getRequest().setCharacterEncoding("utf-8"); //设置编码
            DiskFileItemFactory factory = new DiskFileItemFactory();//获得磁盘文件条目工厂  

            //以年和月作为附件存盘路径每个月存一个文件夹 如：attachment/201405/
            java.text.DateFormat insDateFormat = new java.text.SimpleDateFormat("yyyyMM");
            String attachmentFolder = BeanCtx.getAttachmentFolder() + "/" + (String) insDateFormat.format(new java.util.Date()) + "/";
            String path = BeanCtx.getAppPath() + attachmentFolder; //获取文件需要上传到的路径

            //如果path不存在创建一个path
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            factory.setRepository(new File(path)); //设置临时文件路径
            factory.setSizeThreshold(1000); //设置 缓存的大小
            ServletFileUpload upload = new ServletFileUpload(factory);
            upload.setFileSizeMax(1024 * 10240); //设置最大小传附件为10M

            //可以上传多个文件  
            List<FileItem> list = (List<FileItem>) upload.parseRequest(BeanCtx.getRequest());

            Document doc = BeanCtx.getDocumentBean("BPM_AttachmentsList"); //创建一个附件文档对像
            doc.s("FileType", "0"); //附件类型
            doc.s("DeleteFlag", "0"); //删除标记

            //1.先取普通字段组成文件基本变量
            for (FileItem item : list) {
                String name = item.getFieldName(); //获取表单的属性名字  
                if (item.isFormField()) {
                    //如果获取的 表单信息是普通的 文本 信息  
                    if (name.toLowerCase().equalsIgnoreCase("nodename")) {
                        doc.s(name, Tools.decode(item.getString())); //utf-8解码
                    }
                    else {
                        doc.s(name, item.getString());
                    }
                }
            }

            //2.再取附件内容存盘文档对像
            for (FileItem item : list) {
//            	BeanCtx.out(item);
                if (!item.isFormField()) {
                    //对上传的文件进行处理
                    doc.s("WF_OrUnid", Rdb.getNewid("BPM_AttachmentsList"));
                    String value = item.getName(); //获取路径名  
                    String realfilename = value.substring(value.lastIndexOf("\\") + 1); //截取 上传文件的 字符串名字，作为真实名称存入sql表中
                    final int lastIndexOf = value.lastIndexOf(".");
                    String extname = lastIndexOf >= 0 ? value.substring(lastIndexOf) : "";
                    String filename = doc.g("WF_OrUnid") + extname; //以id为命名存入硬盘中，这样可以避免附件同名问题 
                    doc.s("FileName", realfilename);
                    doc.s("FilePath", attachmentFolder);
                    item.write(new File(path, filename)); //真正写到磁盘上  
                    doc.s("FileSize", String.valueOf(item.getSize() / 1024) + "k");
                   if(Tools.isBlank(doc.g("DocUnid"))){
                	   doc.s("DocUnid", docUnid);
                   }
                   if(Tools.isBlank(doc.g("FdName"))){
                	   doc.s("FdName", fdName);
                   }
                    int i=doc.save(); //针对每一个附件内容，保存附件文件信息到sql表中一条记录
                }
            }
//           BeanCtx.out("附件上传成功");
        }
        catch (Exception e) {
            e.printStackTrace();
//            BeanCtx.out("附件上传失败");
        }
    }
    
}