package cn.linkey.rulelib.S006;

import java.util.*;

import cn.linkey.dao.Rdb;
import cn.linkey.dao.RdbCache;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * @RuleName:注册用户
 * @author admin
 * @version: 8.0
 * @Created: 2014-04-23 08:40
 */
final public class R_S006_E006 implements LinkeyRule {

    @Override
    public String run(HashMap<String, Object> params) {
        //获取事件运行参数
        Document formDoc = (Document) params.get("FormDoc"); //表单配置文档
        Document doc = (Document) params.get("DataDoc"); //数据主文档
        String eventName = (String) params.get("EventName");//事件名称
        if (eventName.equals("onFormOpen")) {
            String readOnly = (String) params.get("ReadOnly"); //1表示只读，0表示编辑
            return onFormOpen(doc, formDoc, readOnly);
        }
        else if (eventName.equals("onFormSave")) {
            return onFormSave(doc, formDoc);
        }
        return "1";
    }

    public String onFormOpen(Document doc, Document formDoc, String readOnly) {
        //当表单打开时
        //if(readOnly.equals("1")){return "1";} //如果是阅读状态则可不执行
        if (doc.isNewDoc()) {
            //可以对表单字段进行初始化如:doc.s("fdname",fdvalue),可以获取字段值 doc.g("fdname")
        }
        else {
            //获得部门信息
            String otherFolderid = "";
            String otherFolderName = "";
            String vmOtherFolderName = "";//矩阵部门名称列表
            String sql = "select * from BPM_OrgUserDeptMap where Userid='" + doc.g("Userid") + "'";
            Document[] dc = Rdb.getAllDocumentsBySql(sql);
            for (Document mapdoc : dc) {
                if (mapdoc.g("MainDept").equals("1")) {
                    doc.s("WF_Folderid", mapdoc.g("Folderid"));
                    doc.s("OrgClass", mapdoc.g("OrgClass"));
                }
                else {
                    if (mapdoc.g("OrgClass").equals(BeanCtx.getSystemConfig("DefaultOrgClass"))) {
                        //说明是缺省架构的兼职部门
                        if (Tools.isBlank(otherFolderid)) {
                            otherFolderid = mapdoc.g("Folderid");
                            otherFolderName = BeanCtx.getLinkeyUser().getDeptNameByFolderid(mapdoc.g("Folderid"), mapdoc.g("OrgClass"));
                        }
                        else {
                            otherFolderid += "," + mapdoc.g("Folderid");
                            otherFolderName = otherFolderName + "," + BeanCtx.getLinkeyUser().getDeptNameByFolderid(mapdoc.g("Folderid"), mapdoc.g("OrgClass"));
                        }
                    }
                    else {
                        //说明是矩阵部门
                        if (Tools.isBlank(vmOtherFolderName)) {
                            vmOtherFolderName = BeanCtx.getLinkeyUser().getDeptNameByFolderid(mapdoc.g("Folderid"), mapdoc.g("OrgClass"));
                        }
                        else {
                            vmOtherFolderName = vmOtherFolderName + "," + BeanCtx.getLinkeyUser().getDeptNameByFolderid(mapdoc.g("Folderid"), mapdoc.g("OrgClass"));
                        }
                    }
                }
            }
            doc.s("WF_OtherFolderid", otherFolderid);
            doc.s("WF_OtherFolderid_show", otherFolderName);
            doc.s("VMDeptName", vmOtherFolderName);
            doc.s("Password", "*");

        }
        return "1"; //成功必须返回1，否则表示退出并显示返回的字符串
    }

    public String onFormSave(Document doc, Document formDoc) {
        //当表单存盘前
        //BeanCtx.setDebug();

        if (doc.g("Userid").indexOf("'") != -1 || doc.g("Userid").indexOf("\"") != -1 || doc.g("Userid").indexOf("&") != -1) {
            return "用户id中包含非法字符!";
        }

        String orgClass = BeanCtx.getSystemConfig("DefaultOrgClass"); //缺省行政架构
        String folderid = doc.g("WF_Folderid");
        String sql = "select Deptid from BPM_OrgDeptList where OrgClass='" + orgClass + "' and Folderid='" + folderid + "'";
        String deptid = Rdb.getValueBySql(sql);

        //1.先删除当前用户的所有关系记录
        sql = "delete from BPM_OrgUserDeptMap where OrgClass='" + orgClass + "' and Userid='" + doc.g("Userid") + "'";
        Rdb.execSql(sql);

        //2.存或更新用户与主部门的关系记录
        Document mapdoc = BeanCtx.getDocumentBean("BPM_OrgUserDeptMap");
        mapdoc.s("OrgClass", orgClass);
        mapdoc.s("Userid", doc.g("Userid"));
        mapdoc.s("Folderid", folderid);
        mapdoc.s("Deptid", deptid);
        mapdoc.s("CurrentFlag", "1");
        mapdoc.s("MainDept", "1"); //标识为主部门的关系记录
        mapdoc.save();

        //3.如果有选择兼职部门的情况下，存用户与兼职部门的关系记录
        String otherFolderidList = doc.g("WF_OtherFolderid");
        if (Tools.isNotBlank(otherFolderidList)) {
            String[] folderArray = Tools.split(otherFolderidList, ",");
            for (String otherFolderid : folderArray) {
                sql = "select Deptid from BPM_OrgDeptList where OrgClass='" + orgClass + "' and Folderid='" + otherFolderid + "'";
                deptid = Rdb.getValueBySql(sql);
                mapdoc = BeanCtx.getDocumentBean("BPM_OrgUserDeptMap");
                mapdoc.s("OrgClass", orgClass);
                mapdoc.s("Userid", doc.g("Userid"));
                mapdoc.s("Folderid", otherFolderid);
                mapdoc.s("Deptid", deptid);
                mapdoc.s("CurrentFlag", "0");
                mapdoc.s("MainDept", "0"); //标识为兼职部门
                mapdoc.save();
            }
        }

        //把密码加密成md5格式的
        if (!doc.g("Password").equals("*")) {
            doc.s("Password", Tools.md5(doc.g("Password")));
        }
        else {
            doc.removeItem("Password");
        }

        //清除用户的缓存
        RdbCache.remove("UserCacheStrategy", doc.g("Userid"));

        return "1"; //成功必须返回1，否则表示退出存盘
    }

}