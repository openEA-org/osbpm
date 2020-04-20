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
 * @RuleName:上传流程实例的附件
 * @author  admin
 * @version: 1.0
 */
final public class R_S017_B172 implements LinkeyRule {
	@Override
	public String run(HashMap<String, Object> params) throws Exception  {
	    //params为运行本规则时所传入的参数
		 String docUnid = BeanCtx.g("docUnid",true);
		 String fdName=BeanCtx.g("fdName",true);
		 String processId=BeanCtx.g("processId",true);
		 String nodeName=BeanCtx.g("nodeName",true);
        if (Tools.isBlank(docUnid)) {return RestUtil.formartResultJson("0", "docUnid不能为空");}
	    
        String json=uploadFile(docUnid,fdName,processId,nodeName);
        
	    return json;

	}
	
	/**
	 * 上传附件
	 */
    public String uploadFile(String docUnid,String fdName,String processId,String nodeName) {
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
                   if(Tools.isBlank(doc.g("nodeName"))){
                	   doc.s("NodeName", nodeName);
                   }
                   if(Tools.isBlank(doc.g("Processid"))){
                	   doc.s("Processid", processId);
                   }
                    int i=doc.save(); //针对每一个附件内容，保存附件文件信息到sql表中一条记录
                    if(i>0){
                    	attachlog(doc.g("DocUnid"), doc.g("Processid"), "上传附件(" + doc.g("FileName") + ")"); //添加日记
                    }else{
                    	return RestUtil.formartResultJson("0", "附件上传失败");
                    }
                }
            }
            return RestUtil.formartResultJson("1", "附件上传成功");
        }
        catch (Exception e) {
            e.printStackTrace();
            return RestUtil.formartResultJson("0", "附件上传失败");
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