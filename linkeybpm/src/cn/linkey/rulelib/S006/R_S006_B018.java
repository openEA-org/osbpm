package cn.linkey.rulelib.S006;

import java.util.HashMap;
import java.util.HashSet;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;

/**
 * @RuleName:用户角色明细
 * @author admin
 * @version: 8.0
 * @Created: 2014-07-16 09:54
 */
final public class R_S006_B018 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        //params为运行本规则时所传入的参数
        StringBuilder aclList = new StringBuilder();
        String userid = BeanCtx.g("Userid");

        //1.先找所属部门
        String sql = "select folderid,OrgClass,CurrentFlag,MainDept from BPM_OrgUserDeptMap where Userid='" + userid + "'";
        Document[] dc = Rdb.getAllDocumentsBySql(sql);
        for (Document doc : dc) {
            String folderid = doc.g("Folderid");
            String orgClass = doc.g("OrgClass");
            String currentFlag = "", mainDept = "";
            if (doc.g("CurrentFlag").equals("1")) {
                currentFlag = " <font color=red>-当前部门</font> ";
            }
            if (doc.g("MainDept").equals("1")) {
                mainDept = " <font color=red>-主部门</font> ";
            }

            String deptName = "部门:" + getFullDeptNameByFolderid(orgClass, folderid, true) + mainDept + currentFlag + "<br>";
            aclList.append(deptName);
        }

        //2.换角色或岗位
        sql = "select RoleNumber from BPM_OrgRoleMembers where Member='" + userid + "'";
        HashSet<String> roles = Rdb.getValueSetBySql(sql);
        for (String roleNum : roles) {
            //说明是角色
            sql = "select RoleName from BPM_OrgRoleList where RoleNumber='" + roleNum + "'";
            String roleName = Rdb.getValueBySql(sql);
            if (roleNum.startsWith("RG")) {
                roleName = "岗位:<a href='' onclick=\"OpenUrl('view?wf_num=V_S006_G010&RoleNumber=" + roleNum + "',300,400);return false;\">" + roleName + "(" + roleNum + ")</a><br>";
            }
            else {
                roleName = "角色:<a href='' onclick=\"OpenUrl('view?wf_num=V_S006_G007&RoleNumber=" + roleNum + "',300,400);return false;\">" + roleName + "(" + roleNum + ")</a><br>";
            }
            aclList.append(roleName);
        }

        BeanCtx.p("<div style='margin:10px 20px 20px 20px;line-height:20px'>");
        BeanCtx.p(aclList.toString());
        BeanCtx.p("</div>");

        return "";
    }

    /**
     * 获得部门的全称
     * 
     * @param folderid部门层级id
     * @param ctd 是否返回第一层公司名 true表示返回,false表示不返回
     * @return 返回如： 公司/财务部/会计科
     */
    public String getFullDeptNameByFolderid(String orgClass, String folderid, boolean ctd) {
        StringBuilder deptName = new StringBuilder();
        int i = 0;
        while ((folderid.length() >= 3 && ctd == true) || (folderid.length() > 3 && ctd == false)) {
            String sql = "select FolderName from BPM_OrgDeptList where OrgClass='" + orgClass + "' and Folderid='" + folderid + "'";
            if (i == 0) {
                deptName.append(Rdb.getValueBySql(sql));
                i = 1;
            }
            else {
                deptName.insert(0, Rdb.getValueBySql(sql) + "/");
            }
            folderid = folderid.substring(0, folderid.length() - 3);
        }
        return deptName.toString();
    }

}