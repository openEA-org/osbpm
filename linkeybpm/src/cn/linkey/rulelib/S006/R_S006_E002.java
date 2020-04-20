package cn.linkey.rulelib.S006;

import java.util.HashMap;
import java.util.HashSet;

import cn.linkey.app.AppUtil;
import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.form.FormDesigner;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * @RuleName:注册部门
 * @author admin
 * @version: 8.0
 * @Created: 2014-04-22 11:06
 */
final public class R_S006_E002 implements LinkeyRule {

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
        if (readOnly.equals("1")) {
            return "1";
        } //如果是阅读状态则可不执行
        if (doc.isNewDoc()) {
            //可以对表单字段进行初始化如:doc.s("fdname",fdvalue),可以获取字段值 doc.g("fdname")
            //doc.s("Deptid", "D"+doc.g("OrgClass")+"0");

            String sql = "select Deptid from BPM_OrgDeptList where OrgClass='" + doc.g("OrgClass") + "' order by Deptid desc";
            String newNum = AppUtil.getElNewNum(sql);
            if (Tools.isBlank(newNum)) {
                newNum = "DP" + doc.g("OrgClass") + "001";
            }
            doc.s("Deptid", newNum);
        }
        else {
            //编辑状态下部门唯一编号为只读
            FormDesigner formDesigner = (FormDesigner) BeanCtx.getBean("FormDesigner");
            formDesigner.setFdAttr(formDoc, "Deptid", "edittype", "compute");
        }

        return "1"; //成功必须返回1，否则表示退出并显示返回的字符串
    }

    public String onFormSave(Document doc, Document formDoc) {
        //当表单存盘前
        if (doc.isNewDoc()) {
            //注册新部门
            doc.s("Folderid", getNewFolder(doc.g("OrgClass"), doc.g("ParentFolderid"), "BPM_OrgDeptList"));
        }
        else {
            //修改部门
            Document deptDoc = Rdb.getDocumentBySql("select * from BPM_OrgDeptList where WF_OrUnid='" + doc.getDocUnid() + "'");
            String oldFolderid = deptDoc.g("Folderid");
            String oldParentFolderid = deptDoc.g("ParentFolderid");
            if (!oldParentFolderid.equals(doc.g("ParentFolderid"))) {
                //说明部门层级有移动需要重新计算文件夹id
                String newFolderid = getNewFolder(doc.g("OrgClass"), doc.g("ParentFolderid"), "BPM_OrgDeptList");
                doc.s("Folderid", newFolderid);
                updateDeptMap(doc.g("Deptid"), doc.g("Folderid")); //更新关系表
                moverAllSubFolder(doc.g("OrgClass"), oldFolderid, newFolderid); //移动本文件夹的子文件夹
            }
        }
        return "1"; //成功必须返回1，否则表示退出存盘
    }

    /**
     * 移动一个文件夹下面的所有子文件夹
     * 
     * @param appid 应用编号
     * @param treeid 树id
     * @param oldFolderid 旧文夹
     * @param newFolderid
     */
    public void moverAllSubFolder(String orgClass, String oldFolderid, String newFolderid) {
        String sql = "select * from BPM_OrgDeptList where OrgClass='" + orgClass + "' and ParentFolderid like '" + oldFolderid + "%'";
        Document[] dc = Rdb.getAllDocumentsBySql(sql);
        for (Document doc : dc) {
            //oldFolderid=001,newFolderid=003002
            String subParentFolderid = doc.g("ParentFolderid"); //001
            String subFolderid = doc.g("Folderid"); //001002,001002001,001002003
            String newSubFolderid = newFolderid + subFolderid.substring(oldFolderid.length()); //计算后:003002+002,003002+002001,003002+002003
            String newSubParentFolderid = newFolderid + subParentFolderid.substring(oldFolderid.length());//计算后:003003+""
            doc.s("ParentFolderid", newSubParentFolderid);
            doc.s("Folderid", newSubFolderid);
            doc.save();
            updateDeptMap(doc.g("Deptid"), doc.g("Folderid")); //更新关系表
        }
    }

    /**
     * 更新部门与用户，角色，角色成员之间的关系表
     */
    public void updateDeptMap(String deptid, String newFolderid) {
        //更新用户关系表
        String sql = "update BPM_OrgUserDeptMap set Folderid='" + newFolderid + "' where Deptid='" + deptid + "'";
        Rdb.execSql(sql);
    }

    /**
     * 获得一个新的文件夹编号
     * 
     * @param orgClass 架构标识
     * @param parentFolderid 上级文件夹编号
     * @param tableName 所在数据库表名，表中必须要有OrgClass,Folderid和ParentFolderid三个字段
     * @return
     */
    public String getNewFolder(String orgClass, String parentFolderid, String tableName) {
        String newSubFolderid = "";
        //获得文件夹下面的所有子文件夹的已有编号
        String sql = "select Folderid from " + tableName + " where OrgClass='" + orgClass + "' and ParentFolderid='" + parentFolderid + "'";
        //        BeanCtx.out("sql value="+Rdb.getValueBySql(sql));
        HashSet<String> allSubFolderSet = Rdb.getValueSetBySql(sql);
        //        BeanCtx.out("size="+allSubFolderSet.size()+" str="+allSubFolderSet.toString());
        if (allSubFolderSet.size() == 0) {
            //还没有子文件夹的情况下
            if (parentFolderid.equals("root")) {
                parentFolderid = "";
            }
            newSubFolderid = parentFolderid + "001";
        }
        else {
            //已经有子文件夹的情况下
            if (parentFolderid.equals("root")) {
                parentFolderid = "";
            }
            for (int i = 1; i < 100; i++) {
                String newNum = "00000" + i;
                newNum = newNum.substring(newNum.length() - 3); //每次取最后3位数的编号
                String newFolderid = parentFolderid + newNum;
                if (!allSubFolderSet.contains(newFolderid)) {
                    newSubFolderid = newFolderid; //如果在已有的文件夹中找不到则返回新文件夹id号
                    break;
                }
            }
        }

        return newSubFolderid;
    }

}