package cn.linkey.org;

import java.util.HashSet;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.util.Tools;

/**
 * 部门数据操作类主要提供部门注册，部门修改等功能 实例化时必须调用init()方法
 * 
 * @author Administrator
 */
public class DeptModel {

    private String orgClass;
    private String folderName;
    private String parentFolderid;
    private String folderid;
    private String deptid; //必须设置此关键字才能操作部门数据
    private String deptFlag;
    private String roles;
    private String xmlData;

    //private DeptModel(){}

    /**
     * 必须指定orgClass和deptid部门唯一id才能获得本类的实例
     * 
     * @param deptid
     */
    public void init(String orgClass, String deptid) {
        this.orgClass = orgClass;
        this.deptid = deptid;
    }

    /**
     * @param parentFolderid 上级部门的folderid
     * @return 返回0表示注册失败,1表示注册成功
     */
    public int reg(String parentFolderid) {
        this.parentFolderid = parentFolderid;
        this.folderid = getNewFolderid(orgClass, parentFolderid, "BPM_OrgDeptList");
        return save();
    }

    /**
     * 移动部门
     * 
     * @param newParentFolderid 新的上级部门层级id
     * @return 返回非负数表示修改成功
     */
    public int move(String newParentFolderid) {
        //修改部门
        Document deptDoc = this.getDeptDoc();
        this.parentFolderid = newParentFolderid;
        String oldParentFolderid = deptDoc.g("ParentFolderid");
        String oldFolderid = deptDoc.g("Folderid");
        if (!oldParentFolderid.equals(parentFolderid)) {
            //说明部门层级有移动需要重新计算文件夹id
            String newFolderid = getNewFolderid(orgClass, newParentFolderid, "BPM_OrgDeptList");
            this.folderid = newFolderid;
            updateDeptMap(deptid, newFolderid); //更新关系表
            moverAllSubFolder(orgClass, oldFolderid, newFolderid); //移动本文件夹的子文件夹
        }
        return save();
    }

    public Document getDeptDoc() {
        return Rdb.getDocumentBySql("select * from BPM_OrgDeptList where Deptid='" + deptid + "'");
    }

    /**
     * 移动一个文件夹下面的所有子文件夹
     * 
     * @param appid 应用编号
     * @param treeid 树id
     * @param oldFolderid 旧文夹
     * @param newFolderid
     */
    private void moverAllSubFolder(String orgClass, String oldFolderid, String newFolderid) {
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
    private void updateDeptMap(String deptid, String newFolderid) {
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
    public String getNewFolderid(String orgClass, String parentFolderid, String tableName) {
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

    /**
     * 保存用户文档
     * 
     * @return
     */
    public int save() {
        //开始创建用户文档对像
        Document userdoc = this.getDeptDoc();
        userdoc.s("deptid", deptid);
        if (Tools.isNotBlank(orgClass)) {
            userdoc.s("orgClass", orgClass);
        }
        if (Tools.isNotBlank(folderName)) {
            userdoc.s("folderName", folderName);
        }
        if (Tools.isNotBlank(parentFolderid)) {
            userdoc.s("parentFolderid", parentFolderid);
        }
        if (Tools.isNotBlank(folderid)) {
            userdoc.s("folderid", folderid);
        }
        if (Tools.isNotBlank(deptFlag)) {
            userdoc.s("deptFlag", deptFlag);
        }
        if (Tools.isNotBlank(roles)) {
            userdoc.s("roles", roles);
        }
        if (Tools.isNotBlank(xmlData)) {
            userdoc.appendFromXml(this.getXmlData());
        }
        return userdoc.save();
    }

    /**
     * 删除部门
     * 
     * @return 返回负数表示删除失败,返回非负数表示删除成功
     */
    public int delete() {

        //检测部门下面是否还有人员
        String sql = "select WF_OrUnid from BPM_OrgUserDeptMap where Deptid='" + deptid + "' union select WF_OrUnid from BPM_OrgRoleMembers where Deptid='" + deptid + "'";
        if (Rdb.hasRecord(sql)) {
            return -2;
        }

        //开始删除部门
        return this.getDeptDoc().remove(true);

    }

    public String getOrgClass() {
        return orgClass;
    }

    public void setOrgClass(String orgClass) {
        this.orgClass = orgClass;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public String getParentFolderid() {
        return parentFolderid;
    }

    public void setParentFolderid(String parentFolderid) {
        this.parentFolderid = parentFolderid;
    }

    public String getFolderid() {
        return folderid;
    }

    public void setFolderid(String folderid) {
        this.folderid = folderid;
    }

    public String getDeptid() {
        return deptid;
    }

    public void setDeptid(String deptid) {
        this.deptid = deptid;
    }

    public String getDeptFlag() {
        return deptFlag;
    }

    public void setDeptFlag(String deptFlag) {
        this.deptFlag = deptFlag;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    public String getXmlData() {
        return xmlData;
    }

    public void setXmlData(String xmlData) {
        this.xmlData = xmlData;
    }

}
