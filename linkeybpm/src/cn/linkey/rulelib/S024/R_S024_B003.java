package cn.linkey.rulelib.S024;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.DateUtil;
import cn.linkey.util.Tools;

/**
 * 本规则负责显示附件上传表单,使用第三方组件实现附件上传
 * 
 * @author Administrator
 *
 */
public class R_S024_B003 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) {
        if (BeanCtx.getRequest().getMethod().equals("POST")) {
            uploadFile(); //post模式为上传附件
        }
        return "";
    }

    /**
     * POST是上传文件
     */
    public void uploadFile() {
        try {
            BeanCtx.getRequest().setCharacterEncoding("utf-8"); //设置编码
            DiskFileItemFactory factory = new DiskFileItemFactory();//获得磁盘文件条目工厂  
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
            doc.s("FileType", "1"); //附件类型
            doc.s("DeleteFlag", "0"); //删除标记

            //1.先取普通字段组成文档基本变量
            for (FileItem item : list) {
                String name = item.getFieldName(); //获取表单的属性名字  
                if (item.isFormField()) {
                    //如果获取的 表单信息是普通的 文本 信息  
                    doc.s(name, Tools.decodeUrl(item.getString()));
                }
            }

            //2.再取附件内容存盘文档对像
            for (FileItem item : list) {
                if (!item.isFormField()) {
                    //对上传的文件进行处理
                    doc.s("WF_OrUnid", Rdb.getNewid("BPM_AttachmentsList"));
                    String fileName = item.getName(); //获取文件名
                    //                    BeanCtx.out("附件名-->"+value);
                    delWordDocByFileName(doc.g("DocUnid"), fileName); //删除已存在的正文
                    String realfilename = fileName.substring(fileName.lastIndexOf("\\") + 1); //截取 上传文件的 字符串名字，作为真实名称存入sql表中
                    String extname = fileName.substring(fileName.lastIndexOf("."));
                    String filename = doc.g("WF_OrUnid") + extname; //以id为命名存入硬盘中，这样可以避免附件同名问题 
                    doc.s("FileName", realfilename);
                    doc.s("FilePath", attachmentFolder);
                    item.write(new File(path, filename)); //真正写到磁盘上  
                    doc.s("FileSize", String.valueOf(item.getSize() / 1024) + "k");
                    doc.save(); //针对每一个附件内容，保存附件文件信息到sql表中一条记录
                    attachlog(doc.g("DocUnid"), doc.g("Processid"), "保存正文(" + doc.g("FileName") + ")"); //添加日记
                }
            }

            //BeanCtx.getResponse().sendRedirect("rule?wf_num=R_S004_B002&indexnum="+doc.g("indexnum")+"&reload=1"); //加入重新显示附件列表标记位
        }
        catch (Exception e) {
            e.printStackTrace();
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
        String sql = "insert into BPM_AttachmentLog(WF_OrUnid,DocUnid,Userid,Processid,IP,Remark,WF_DocCreated) " + "values('" + Rdb.getNewUnid() + "','" + docUnid + "','" + BeanCtx.getUserid()
                + "','" + processid + "','" + ip + "','" + remark + "','" + DateUtil.getNow() + "')";
        Rdb.execSql(sql);
    }

    /**
     * 删除已存在的正文Word文件
     */
    public static void delWordDocByFileName(String docUnid, String fileName) {
        String sql = "select * from BPM_AttachmentsList where DocUnid='" + docUnid + "' and FileType='1' and FileName='" + fileName + "'";
        Document doc = Rdb.getDocumentBySql("BPM_AttachmentsList", sql);
        if (!doc.isNull()) {
            String filepath = BeanCtx.getRequest().getServletContext().getRealPath(doc.g("FilePath")); //附件所在目录
            String extName = fileName.substring(fileName.lastIndexOf(".")); //文件扩展名称
            String realfilename = filepath + "/" + doc.g("WF_OrUnid") + extName; //得到文件扩展名称加上文件编号
            File file = new File(realfilename);
            if (file.exists()) {
                file.delete();
            }
            sql = "delete from BPM_AttachmentsList where WF_OrUnid='" + doc.g("WF_OrUnid") + "'";
            Rdb.execSql(sql);
        }
    }
}