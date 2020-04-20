package cn.linkey.rulelib.S029;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

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
final public class R_S029_T010 implements LinkeyRule {
    private int deptu = 0, deptn = 0;
    private static final String baseUrl = "http://portal.ly-sky.com:88/LYGTC1.0/";
    private static final String deptItemsUrl = baseUrl + "departmentManager.departmentSearchListService.svc";//部门下的直属部门
    private static final String userItemsUrl = baseUrl + "user.userSearch.svc"; //用户接口
    private static final String roleItemsUrl = baseUrl + "role.searcheRole.svc"; //角色接口
    private static final String roleMemberUrl = baseUrl + "role.roleSearchUserService.svc"; //角色成员查询接口

    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        //params为运行本规则时所传入的参数
        this.deptn = 0;
        this.deptu = 0;
        //		this.syncDeptList("-1"); //先同步部门
        //		BeanCtx.out("共更新("+deptu+")个部门，共注册("+deptn+")个部门");
        //		this.syncUser();//再同步用户
        //		this.syncRole();//同步角色
        //		this.syncRoleMember();//同步角色成员

        return "";
    }

    /**
     * 从domino中同步部门数据到本系统中来
     * 
     * @return
     */
    public void syncDeptList(String pid) throws Exception {
        //获得domino的所有部门列表
        String url = deptItemsUrl + "?pid=" + pid;
        String jsonStr = Tools.httpGet(url, "");
        JSONObject jsonObject = JSONObject.parseObject(jsonStr);
        String rows = jsonObject.getString("rows");
        if (rows.equals("[]") || Tools.isBlank(rows)) {
            return;
        }
        JSONArray jsonArray = JSONArray.parseArray(rows);
        for (int i = 0; i < jsonArray.size(); i++) {
            jsonObject = jsonArray.getJSONObject(i);
            String deptName = jsonObject.getString("bmmc");
            String bmbh = jsonObject.getString("bmbh"); //0-1
            String bmdm = jsonObject.getString("bmdm"); //123456
            String sjbmbh = jsonObject.getString("sjbmbh"); //0,-1

            //开始注册,不存在注册，已存在更新
            String sql = "select * from BPM_OrgDeptList where bmdm='" + bmdm + "'";
            Document deptDoc = Rdb.getDocumentBySql(sql);
            if (deptDoc.isNull()) {
                String deptFlag = pid.equals("-1") ? "Company" : "Department";
                String parentFolderid = pid.equals("-1") ? "root" : getParentFolderid(sjbmbh); //获得上级文件夹的folderid
                String folderid = pid.equals("-1") ? "001" : getNewFolderid(parentFolderid); //计算当前文件夹的folderid
                deptDoc.s("ParentFolderid", parentFolderid);
                deptDoc.s("Folderid", folderid);
                deptDoc.s("Deptid", getNewDeptid());
                deptDoc.s("deptFlag", deptFlag);
                deptDoc.s("SortNumber", "1001");
                deptDoc.s("OrgClass", "1");
                deptDoc.s("FolderName", deptName);
                deptDoc.s("bmbh", bmbh);
                deptDoc.s("sjbmbh", sjbmbh);
                deptDoc.s("bmdm", bmdm);
                deptn++;
            }
            else {
                deptDoc.s("FolderName", deptName);
                deptu++;
            }
            deptDoc.save();
            syncDeptList(bmbh);//递归循环所有下级部门
        }
    }

    /**
     * 获得上级文件夹的folderid
     * 
     * @param sjbmbh
     * @return
     */
    public String getParentFolderid(String sjbmbh) {
        String sql = "select Folderid from BPM_OrgDeptList where bmbh='" + sjbmbh + "'";
        return Rdb.getValueBySql(sql);
    }

    /**
     * 获得一个新的文件夹编号
     * 
     * @param orgClass 架构标识
     * @param parentFolderid 上级文件夹编号
     * @param tableName 所在数据库表名，表中必须要有OrgClass,Folderid和ParentFolderid三个字段
     * @return
     */
    public String getNewFolderid(String parentFolderid) {
        String newSubFolderid = "";
        //获得文件夹下面的所有子文件夹的已有编号
        String sql = "select Folderid from BPM_OrgDeptList where OrgClass='1' and ParentFolderid='" + parentFolderid + "'";
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

    //获得一个新的部门唯一id
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
    public void syncUser() throws Exception {
        int u = 0, n = 0;
        String jsonStr = Tools.httpGet(userItemsUrl, "");
        JSONObject jsonObject = JSONObject.parseObject(jsonStr);
        String rows = jsonObject.getString("rows");
        JSONArray jsonArray = JSONArray.parseArray(rows);
        for (int i = 0; i < jsonArray.size(); i++) {
            jsonObject = jsonArray.getJSONObject(i);
            String userid = jsonObject.getString("userAccount");
            String folderid = Rdb.getValueBySql("select Folderid from BPM_OrgDeptList where bmbh='" + jsonObject.getString("userDept") + "'"); //部门编号
            if (Tools.isBlank(folderid)) {
                continue;
            } //如果找不到部门就跳过，不注册
            String deptName = jsonObject.getString("userDeptName");
            String sql = "select * from BPM_OrgUserList where Userid='" + userid + "'";
            Document jdoc = Rdb.getDocumentBySql(sql);
            if (!jdoc.isNull()) {
                //用户已存在更新一次
                u++;
                jdoc.s("CnName", jsonObject.getString("userName"));
                jdoc.s("PhoneNumber", jsonObject.getString("mobile"));
                jdoc.s("SortNumber", "1001");
                jdoc.s("InternetAddress", jsonObject.getString("email"));
                jdoc.s("WF_Folderid_show", deptName);//编辑用户显示时用
                creatUserAndDeptMap(userid, folderid);
            }
            else {
                //用户不存在注册一个新的
                n++;
                jdoc.s("Userid", userid);
                jdoc.s("CnName", jsonObject.getString("userName"));
                jdoc.s("Password", jsonObject.getString("password"));
                jdoc.s("PhoneNumber", jsonObject.getString("mobile"));
                jdoc.s("SortNumber", "1001");
                jdoc.s("InternetAddress", jsonObject.getString("email"));
                jdoc.s("WF_Folderid_show", deptName);//编辑用户显示时用
                jdoc.s("LANG", "zh,CN");
                jdoc.s("Status", "1");
                jdoc.s("WF_AddName", "admin");
                jdoc.s("WF_Folderid_show", deptName);//编辑用户显示时用
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
        //先看用户的影谢关系是否存在，如果存在就更新，不存在就生成一条
        String sql = "select WF_OrUnid from BPM_OrgUserDeptMap where Folderid='" + folderid + "' and Userid='" + userid + "'";
        Document mapDoc = Rdb.getDocumentBySql(sql);
        if (mapDoc.isNull()) {
            String deptid = Rdb.getValueBySql("select Deptid from BPM_OrgDeptList where OrgClass='1' and Folderid='" + folderid + "'"); //从新的部门中查找最新的编号
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
    public void syncRole() throws Exception {
        int u = 0, n = 0;
        String jsonStr = Tools.httpGet(roleItemsUrl, "");
        JSONObject jsonObject = JSONObject.parseObject(jsonStr);
        String rows = jsonObject.getString("rows");
        JSONArray jsonArray = JSONArray.parseArray(rows);
        for (int i = 0; i < jsonArray.size(); i++) {
            jsonObject = jsonArray.getJSONObject(i);
            String roleNumber = "RS" + jsonObject.getString("rn"); //由于roleid太长了，用rn来作为流程中心的角色id
            String roleName = jsonObject.getString("roleName");
            String sql = "select * from BPM_OrgRoleList where RoleNumber='" + roleNumber + "'";
            Document jdoc = Rdb.getDocumentBySql(sql);
            if (jdoc.isNull()) {
                //角色不存在注册一个新的
                jdoc.s("WF_OrUnid", jsonObject.getString("roleId"));
                jdoc.s("WF_Appid", "S029");
                jdoc.s("RoleFolderid", "001");
                jdoc.s("RoleType", "1");
                jdoc.s("RoleName", roleName);
                jdoc.s("RoleNumber", roleNumber);
                jdoc.s("Status", "1");
                jdoc.s("remark", jsonObject.getString("roleDesc"));
                jdoc.save();
                n++;
            }
            else {
                //更新角色
                jdoc.s("WF_Appid", "S029");
                jdoc.s("RoleFolderid", "001");
                jdoc.s("RoleType", "1");
                jdoc.s("RoleName", roleName);
                jdoc.s("Status", "1");
                jdoc.s("remark", jsonObject.getString("roleDesc"));
                jdoc.save();
                u++;
            }
        }
        BeanCtx.out("共更新(" + u + ")个角色,共同步注册(" + n + ")个角色");
    }

    /**
     * 同步角色成员
     */
    public void syncRoleMember() throws Exception {
        int n = 0, u = 0;
        String sql = "select * from BPM_OrgRoleList";
        LinkedHashSet<Document> dc = Rdb.getAllDocumentsSetBySql(sql);
        for (Document roleDoc : dc) {
            String roleid = roleDoc.g("WF_OrUnid");
            String roleNumber = roleDoc.g("RoleNumber");
            String url = roleMemberUrl + "?roleId=" + roleid;
            //			BeanCtx.out(url);
            String jsonStr = Tools.httpGet(url, "");
            JSONObject jsonObject = JSONObject.parseObject(jsonStr);
            String rows = jsonObject.getString("rows");
            //			BeanCtx.out("rows="+rows);
            JSONArray jsonArray = JSONArray.parseArray(rows);
            for (int i = 0; i < jsonArray.size(); i++) {
                jsonObject = jsonArray.getJSONObject(i);
                String userid = jsonObject.getString("userAccount");
                String userName = jsonObject.getString("userName");
                String userDept = jsonObject.getString("userDept");//0-1
                //从部门中取相关信息
                Document deptDoc = Rdb.getDocumentBySql("select * from BPM_OrgDeptList where bmbh='" + userDept + "'");
                String folderid = deptDoc.g("Folderid");
                String deptName = deptDoc.g("FolderName");
                String deptid = deptDoc.g("Deptid");
                //看是否已存在
                sql = "select * from BPM_OrgRoleMembers where RoleNumber='" + roleNumber + "' and Member='" + userid + "'";
                Document jdoc = Rdb.getDocumentBySql(sql);
                if (jdoc.isNull()) {
                    jdoc.s("RoleType", "1");
                    jdoc.s("RoleNumber", roleNumber);
                    jdoc.s("MemberName", userName);
                    jdoc.s("Member", userid);
                    jdoc.s("Member_show", userName); //显示时用
                    jdoc.s("OrgClass", "1");
                    jdoc.s("SortNum", "1001");
                    jdoc.s("Folderid", folderid);
                    jdoc.s("Deptid", deptid);
                    jdoc.s("Deptid_show", deptName);
                    jdoc.save();
                    n++;
                }
            }
        }
        BeanCtx.out("共同步注册(" + n + ")个角色成员");
    }

}