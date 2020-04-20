package cn.linkey.rulelib.S004;

import java.io.File;
import java.util.Date;
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
 * 
* Copyright © 2018 A Little Bao. All rights reserved.
* 
* @ClassName: R_S004_B005.java
* @Description: 本规则负责显示附件上传表单,使用第三方组件实现附件上传
*
* @version: v1.0.0
* @author: alibao
* @date: 2018年3月21日 下午2:30:40 
*
* Modification History:
* Date         Author          Version            Description
*---------------------------------------------------------*
* 2018年3月21日     alibao           v1.0.0               修改原因
 */
public class R_S004_B005 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) {
        if (BeanCtx.getRequest().getMethod().equals("GET")) {
            showUploadForm(); //get模式为显示上传表单
        }
        else {
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

            //以年和月作为附件存盘路径每个月存一个文件夹 如：attachment/201405/
            String appId = BeanCtx.g("appId", true);
            String attachmentFolder = BeanCtx.getAttachmentFolder() + File.separator + appId + File.separator + DateUtil.formatDate(new Date(), "yyyyMM") + File.separator;
            String path = BeanCtx.getAppPath() + attachmentFolder; //获取文件需要上传到的路径

            //如果path不存在创建一个path
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            factory.setRepository(new File(path)); //设置临时文件路径
            factory.setSizeThreshold(1000); //设置 缓存的大小
            ServletFileUpload upload = new ServletFileUpload(factory);
            upload.setFileSizeMax(1024 * 10240 * 2); //设置最大小传附件为10M

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
                    if (name.toLowerCase().equals("nodename")) {
                        doc.s(name, Tools.decode(item.getString())); //utf-8解码
                    }
                    else {
                        doc.s(name, item.getString());
                    }
                }
            }

            //2.再取附件内容存盘文档对像
            for (FileItem item : list) {
                if (!item.isFormField()) {
                    //对上传的文件进行处理
                    doc.s("WF_OrUnid", Rdb.getNewid("BPM_AttachmentsList"));
                    String value = item.getName(); //获取路径名  
                    String realfilename = value.substring(value.lastIndexOf("\\") + 1); //截取 上传文件的 字符串名字，作为真实名称存入sql表中
                    String extname = value.substring(value.lastIndexOf("."));
                    String filename = doc.g("WF_OrUnid") + extname; //以id为命名存入硬盘中，这样可以避免附件同名问题 
                    doc.s("FileName", realfilename);
                    doc.s("FilePath", attachmentFolder);
                    item.write(new File(path, filename)); //真正写到磁盘上  
                    doc.s("FileSize", String.valueOf(item.getSize() / 1024) + "k");
                    doc.save(); //针对每一个附件内容，保存附件文件信息到sql表中一条记录
                    attachlog(doc.g("DocUnid"), doc.g("Processid"), "上传附件(" + doc.g("FileName") + ")"); //添加日记
                }
            }

            BeanCtx.getResponse().sendRedirect("rule?wf_num=R_S004_B005&appId=" + appId + "&FdName=" + doc.g("FdName") + "&reload=1"); //加入重新显示附件列表标记位
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
     * GET方法时显示上传表单
     */
    public void showUploadForm() {
        String fileList = BeanCtx.getSystemConfig("UploadFileformat"); //允许上传的文件格式
        if (Tools.isBlank(fileList)) {
            fileList = "*";
        }
        //20180321添加，layui上传按钮样式
        String upLoadText = "<button type=\"button\" class=\"layui-btn\"><i class=\"layui-icon\"></i>上传文件</button>";
        String layuiJS = "<link rel=\"stylesheet\" type=\"text/css\" href=\"linkey/bpm/layui/css/layui.css\">\n<script type=\"text/javascript\" src=\"linkey/bpm/layui/layui.js\"></script>";

        
        String fdName = BeanCtx.g("FdName", true);
        StringBuffer htmlBf = new StringBuffer();
        htmlBf.append("<html>");
        htmlBf.append("  <head>");
        htmlBf.append("    <meta http-equiv=Content-Type content=\"text/html;charset=utf-8\">");
        htmlBf.append("    <title>Attachment upload</title>");
        htmlBf.append("    <style>");
        htmlBf.append("      #uploadImg { font-size:12px;  position:absolute;}");
        htmlBf.append("      #selectfile {    cursor: pointer;    position: absolute;    z-index: 100;    opacity: 0;    filter: alpha(opacity=0);    top: 5px;    width: 110px;    height: 28px;}");
        htmlBf.append("      #uploadtext{background-repeat:no-repeat;display:block;float:left;height:20px;margin-top:1px;position:relative;text-decoration:none;top:0pt; width:80px;cursor:pointer;}");
        htmlBf.append("    </style>");
        htmlBf.append(layuiJS); //20180321 添加layui头文件的引入
        htmlBf.append("    <script language=\"JavaScript\" type=\"text/javascript\">");
        htmlBf.append("      function upfile() {");
        htmlBf.append("        var fileList='" + fileList + "';");
        htmlBf.append("        var filepath=document.getElementById('selectfile').value;");
        htmlBf.append("        var spos=filepath.lastIndexOf(\".\");");
        htmlBf.append("        var extname=filepath.substring(spos,filepath.length);");
        htmlBf.append("        if(fileList.indexOf(extname+\",\")==-1 && fileList!=\"*\") {");
        htmlBf.append("          alert('" + BeanCtx.getMsg("Common", "NoUploadFile") + "');");
        htmlBf.append("          return false;");
        htmlBf.append("        }");
        htmlBf.append("        parent.mask();");
        htmlBf.append("        document.forms[0].submit();");
        htmlBf.append("      }");
        htmlBf.append("      function initParams() {");
        htmlBf.append("        try {");
        htmlBf.append("          parent.unmask();");
        htmlBf.append("        }");
        htmlBf.append("        catch(e) {}");
        htmlBf.append("        if(location.href.indexOf('reload') != -1) {");
        htmlBf.append("          parent.LoadAttachments('" + fdName + "');");
        htmlBf.append("        }");
        htmlBf.append("        try {");
        htmlBf.append("          document.getElementById('Processid').value = parent.document.getElementById('WF_Processid').value;");
        htmlBf.append("          document.getElementById('NodeName').value = encodeURIComponent(parent.document.getElementById('WF_CurrentNodeName').value);");
        htmlBf.append("        }");
        htmlBf.append("        catch(e) { }");
        htmlBf.append("        document.getElementById('DocUnid').value = parent.document.getElementById('WF_DocUnid').value;");
        htmlBf.append("      }");
        htmlBf.append("    </script>");
        htmlBf.append("  </head>");
        htmlBf.append("  <body style='border:none;margin:0;overflow-y: hidden;overflow-x: hidden' scroll='no' onload=\"initParams()\" >");
        htmlBf.append("    <form method=\"post\" action=\"rule?wf_num=R_S004_B005&appId=" + BeanCtx.g("appId", true) + "\" enctype=\"multipart/form-data\" accept-charset=\"utf-8\" name=\"linkeyform\">");
        htmlBf.append("      <span id=\"uploadImg\">");
        htmlBf.append("        <input id=\"selectfile\" multiple='multiple' onchange=\"upfile()\" type=\"file\" name=\"selectfile\"> ");
        htmlBf.append("        <a href=\"#\" id=\"uploadtext\"  hidefocus=\"true\">" + upLoadText + "</a>");
        htmlBf.append("      </span>");
        htmlBf.append("      <input type='hidden' name='DocUnid' id='DocUnid' >");
        htmlBf.append("      <input type='hidden' name='Processid' id='Processid' >");
        htmlBf.append("      <input type='hidden' name='NodeName' id='NodeName' >");
        htmlBf.append("      <input type='hidden' name='FdName' id='FdName' value='" + fdName + "'>");
        htmlBf.append("    </form>");
        htmlBf.append("  </body>");
        htmlBf.append("</html>");
        BeanCtx.print(htmlBf.toString());
    }
}