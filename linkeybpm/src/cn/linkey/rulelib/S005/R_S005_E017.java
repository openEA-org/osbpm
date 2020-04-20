package cn.linkey.rulelib.S005;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;

/**
 * @RuleName:个人信息
 * @author admin
 * @version: 8.0
 * @Created: 2014-05-28 14:44
 */
final public class R_S005_E017 implements LinkeyRule {

    @Override
    public String run(HashMap<String, Object> params) {
        // 获取事件运行参数
        Document formDoc = (Document) params.get("FormDoc"); // 表单配置文档
        Document doc = (Document) params.get("DataDoc"); // 数据主文档
        String eventName = (String) params.get("EventName");// 事件名称
        if (eventName.equals("onFormOpen")) {
            String readOnly = (String) params.get("ReadOnly"); // 1表示只读，0表示编辑
            return onFormOpen(doc, formDoc, readOnly);
        }
        else if (eventName.equals("onFormSave")) {
            return onFormSave(doc, formDoc);
        }
        return "1";
    }

    public String onFormOpen(Document doc, Document formDoc, String readOnly) {
        // 当表单打开时
        // System.out.println(doc.getDocUnid());
        String UserName = BeanCtx.getUserid();
        String sql = "select * from BPM_OrgUserDeptMap where Userid='" + UserName + "'";
        Document[] dc = Rdb.getAllDocumentsBySql(sql);
        if (dc.length > 0) {
            String FolderName = "";
            String othFolderName = "";
            for (Document deptdoc : dc) {
                if (deptdoc.g("MainDept").equals("1")) {
                    String Folderid = deptdoc.g("Deptid");
                    sql = "select FolderName From BPM_OrgDeptList where Deptid='" + Folderid + "'";
                    FolderName = Rdb.getValueBySql(sql);
                }
                else {
                    String othFolderid = deptdoc.g("Deptid");
                    sql = "select FolderName From BPM_OrgDeptList where Deptid='" + othFolderid + "'";
                    if (othFolderName == "") {
                        othFolderName = Rdb.getValueBySql(sql);
                    }
                    else {
                        othFolderName = othFolderName + "," + Rdb.getValueBySql(sql);
                    }
                }
            }
            doc.s("WF_Folderid", FolderName);
            doc.s("WF_OtherFolderid_show", othFolderName);
        }

        Document[] attDc = doc.getAttachmentsDoc();
        for (Document attDoc : attDc) {
            String filepath = attDoc.g("FilePath"); //附件所在目录
            String downloadfilename = attDoc.g("FileName"); //从sql表中找到文件信息
            String filenum = attDoc.g("WF_OrUnid");
            String filename = downloadfilename.substring(downloadfilename.lastIndexOf(".")); //得到文件扩展名称加上文件编号
            if (filename.toLowerCase().equals(".jpg") || filename.toLowerCase().equals(".gif") || filename.toLowerCase().equals(".png")) {
                String fullfilepath = filepath + filenum + filename; //文件所在硬盘的真实路径
                doc.s("imageurl", fullfilepath);
            }
        }
        if (readOnly.equals("1")) {
            return "1";
        } // 如果是阅读状态则可不执行
        if (doc.isNewDoc()) {
            // 可以对表单字段进行初始化如:doc.s("fdname",fdvalue),可以获取字段值 doc.g("fdname")

        }
        return "1"; // 成功必须返回1，否则表示退出并显示返回的字符串
    }

    public String onFormSave(Document doc, Document formDoc) {
        // 当表单存盘前

        return "1"; // 成功必须返回1，否则表示退出存盘
    }

}