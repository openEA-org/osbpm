package cn.linkey.rulelib.S006;

import java.util.HashMap;
import java.util.LinkedHashSet;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * @RuleName:Domino_同步部门/用户/角色数据
 * @author admin
 * @version: 8.0
 * @Created: 2015-12-07 10:24
 */
final public class R_S006_B019 implements LinkeyRule {
    private static final String domino_DeptTableName = "DominoBPM.dbo.BPM_OrgDeptList"; // 部门表名称
    private static final String domino_UserTableName = "DominoBPM.dbo.BPM_OrgUserList"; // 用户表名称
    private static final String domino_RolesTableName = "DominoBPM.dbo.BPM_OrgRoleList"; // 角色表名称
    private static final String domino_RolesMemberTableName = "DominoBPM.dbo.BPM_OrgRoleMembers"; // 角色成员表名称

    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        // params为运行本规则时所传入的参数
        this.syncDeptList(); // 先同步部门
        this.syncUser();// 再同步用户
        this.syncRole();// 同步角色
        this.syncRoleMember();// 同步角色成员
        return "";
    }

    /**
     * 从domino中同步部门数据到本系统中来
     * 
     * @return
     */
    public void syncDeptList() {
        // 获得domino的所有部门列表
        int n = 0, u = 0;
        String sql = "select * from " + domino_DeptTableName;
        LinkedHashSet<Document> dc = Rdb.getAllDocumentsSetBySql(sql);
        for (Document ddoc : dc) {
            String folderid = ddoc.g("Folderid"); // 层级id
            sql = "select * from BPM_OrgDeptList where Folderid='" + folderid + "'";
            Document jdoc = Rdb.getDocumentBySql(sql);
            if (!jdoc.isNull()) {
                // 部门已存在，更新一次
                jdoc.s("FolderName", ddoc.g("FolderName"));
                jdoc.s("deptFlag", ddoc.g("deptFlag"));
                jdoc.s("SortNumber", "SortNumber");
                u++;
            }
            else {
                // 部门不存在，注册一个新的
                jdoc.s("OrgClass", "1");
                jdoc.s("FolderName", ddoc.g("FolderName"));
                jdoc.s("ParentFolderid", ddoc.g("ParentFolderid"));
                jdoc.s("Folderid", ddoc.g("Folderid"));
                jdoc.s("Deptid", getNewDeptid());
                jdoc.s("deptFlag", ddoc.g("deptFlag"));
                jdoc.s("SortNumber", "SortNumber");
                n++;
            }
            jdoc.save();
        }
        BeanCtx.out("共更新(" + u + ")个部门，共注册(" + n + ")个部门");
    }

    // 获得一个新的部门唯一id
    public String getNewDeptid() {
        String sql = "select Deptid from BPM_OrgDeptList order by Deptid DESC";
        String deptid = Rdb.getValueTopOneBySql(sql);
        deptid = deptid.replace("DP", "");
        if (Tools.isBlank(deptid)) {
            deptid = "101";
        }
        String newdeptid = "DP" + (Integer.parseInt(deptid) + 1);
        return newdeptid;
    }

    /*
     * 同步用户数据
     */
    public void syncUser() {
        int u = 0, n = 0;
        String sql = "select * from " + domino_UserTableName;
        LinkedHashSet<Document> dc = Rdb.getAllDocumentsSetBySql(sql);
        for (Document ddoc : dc) {
            String userid = ddoc.g("ShortName");
            String folderid = ddoc.g("Folderid"); // 部门编号
            String deptName = BeanCtx.getLinkeyUser().getDeptNameByFolderid(folderid, "1");// 部门名称
            sql = "select * from BPM_OrgUserList where Userid='" + userid + "'";
            Document jdoc = Rdb.getDocumentBySql(sql);
            if (!jdoc.isNull()) {
                // 用户已存在更新一次
                u++;
                jdoc.s("CnName", ddoc.g("ChinaeseName"));
                jdoc.s("JobTitle", ddoc.g("JobTitle"));
                jdoc.s("secretary", ddoc.g("secretary"));
                jdoc.s("PhoneNumber", ddoc.g("CellPhoneNumber"));
                jdoc.s("QQ", ddoc.g("QQ"));
                jdoc.s("IndexFlag", ddoc.g("IndexFlag"));
                jdoc.s("SortNumber", ddoc.g("SortNumber"));
                jdoc.s("InternetAddress", ddoc.g("InternetAddress"));
                jdoc.s("WF_Folderid_show", deptName);// 编辑用户显示时用
                creatUserAndDeptMap(userid, folderid);
            }
            else {
                // 用户不存在注册一个新的
                n++;
                jdoc.s("Userid", userid);
                jdoc.s("CnName", ddoc.g("ChinaeseName"));
                jdoc.s("Password", "1a1dc91c907325c69271ddf0c944bc72");// pass
                jdoc.s("JobTitle", ddoc.g("JobTitle"));
                jdoc.s("secretary", ddoc.g("secretary"));
                jdoc.s("PhoneNumber", ddoc.g("CellPhoneNumber"));
                jdoc.s("QQ", ddoc.g("QQ"));
                jdoc.s("IndexFlag", ddoc.g("IndexFlag"));
                jdoc.s("SortNumber", ddoc.g("SortNumber"));
                jdoc.s("InternetAddress", ddoc.g("InternetAddress"));
                jdoc.s("LANG", "zh,CN");
                jdoc.s("Status", "1");
                jdoc.s("WF_AddName", "admin");
                jdoc.s("WF_Folderid_show", deptName);// 编辑用户显示时用
                creatUserAndDeptMap(userid, folderid);
            }
            jdoc.save();
        }
        BeanCtx.out("共更新(" + u + ")个用户，共注册(" + n + ")个用户");
    }

    /**
     * 创建用户和部门的映谢关系记录
     */
    public void creatUserAndDeptMap(String userid, String folderid) {
        // 先看用户的影谢关系是否存在，如果存在就更新，不存在就生成一条
        String sql = "select WF_OrUnid from BPM_OrgUserDeptMap where Folderid='" + folderid + "' and Userid='" + userid + "'";
        Document mapDoc = Rdb.getDocumentBySql(sql);
        if (mapDoc.isNull()) {
            String deptid = Rdb.getValueBySql("select Deptid from BPM_OrgDeptList where OrgClass='1' and Folderid='" + folderid + "'"); // 从新的部门中查找最新的编号
            mapDoc.s("Userid", userid);
            mapDoc.s("Folderid", folderid);
            mapDoc.s("CurrentFlag", "1");
            mapDoc.s("OrgClass", "1");
            mapDoc.s("Deptid", deptid);
            mapDoc.s("MainDept", "1");
            mapDoc.save();
        }
    }

    /**
     * 同步角色数据
     */
    public void syncRole() {
        int u = 0, n = 0;
        String sql = "select * from " + domino_RolesTableName;
        LinkedHashSet<Document> dc = Rdb.getAllDocumentsSetBySql(sql);
        for (Document ddoc : dc) {
            String roleNumber = ddoc.g("RoleNumber");
            sql = "select * from BPM_OrgRoleList where RoleNumber='RS" + roleNumber + "'";
            Document jdoc = Rdb.getDocumentBySql(sql);
            if (jdoc.isNull()) {
                // 角色不存在注册一个新的
                jdoc.s("WF_Appid", "S029");
                jdoc.s("RoleFolderid", "001");
                jdoc.s("RoleType", "1");
                jdoc.s("RoleName", ddoc.g("Subject"));
                jdoc.s("RoleNumber", "RS" + ddoc.g("RoleNumber"));
                jdoc.s("Status", "1");
                jdoc.save();
                n++;
            }
            else {
                // 更新角色
                jdoc.s("WF_Appid", "S029");
                jdoc.s("RoleFolderid", "001");
                jdoc.s("RoleType", "1");
                jdoc.s("RoleName", ddoc.g("Subject"));
                jdoc.s("Status", "1");
                jdoc.save();
                u++;
            }
        }
        BeanCtx.out("共更新(" + u + ")个角色,共同步注册(" + n + ")个角色");
    }

    /**
     * 同步角色成员
     */
    public void syncRoleMember() {
        int n = 0, u = 0;
        String sql = "select * from " + domino_RolesMemberTableName;
        LinkedHashSet<Document> dc = Rdb.getAllDocumentsSetBySql(sql);
        for (Document ddoc : dc) {
            String roleNumber = "RS" + ddoc.g("RolesUNID");
            String folderid = ddoc.g("Folderid");
            String deptid = BeanCtx.getLinkeyUser().getDeptidByFolderid(folderid, "1");// 部门的唯一id
            String member = ddoc.g("Member");
            String memberName = ddoc.g("Subject");
            if (Tools.isBlank(member)) {
                continue;
            }
            String[] memberArray = Tools.split(member);
            for (String userid : memberArray) {
                sql = "select * from BPM_OrgRoleMembers where RoleNumber='" + roleNumber + "' and Folderid='" + folderid + "'";
                Document jdoc = Rdb.getDocumentBySql(sql);
                if (jdoc.isNull()) {
                    // 不存在创建一个
                    jdoc.s("RoleType", "1");
                    jdoc.s("RoleNumber", roleNumber);
                    jdoc.s("MemberName", memberName);
                    jdoc.s("Member", userid);
                    jdoc.s("Member_show", memberName); // 显示时用
                    jdoc.s("OrgClass", "1");
                    jdoc.s("SortNum", "1001");
                    jdoc.s("Folderid", folderid);
                    jdoc.s("Deptid", deptid);
                    jdoc.s("Deptid_show", BeanCtx.getLinkeyUser().getDeptNameByDeptid(deptid));
                    jdoc.save();
                    n++;
                }
                else {
                    jdoc.s("MemberName", ddoc.g("Subject"));
                    u++;
                }
                jdoc.save();
            }
        }
        BeanCtx.out("共更新(" + u + ")个角色成员,共同步注册(" + n + ")个角色成员");
    }

}