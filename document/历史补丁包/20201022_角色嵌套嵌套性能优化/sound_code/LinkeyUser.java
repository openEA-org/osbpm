package cn.linkey.org;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

import cn.linkey.dao.Rdb;
import cn.linkey.dao.RdbCache;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.util.Tools;
import cn.linkey.wf.NodeUser;

/**
 * 
 * 数据库对象类
 *
 * @author: alibao
 *
 * Modification History:
 * Date         Author          Version            Description
 *---------------------------------------------------------*
 * 2018年8月22日     alibao           v1.0.0               修改原因
 */
public class LinkeyUser {

    /**
     * 初始化用户的缓存信息，用户登录后将调用本函数把用户的个人文档加入到缓存中
     * 
     * @param userid 用户id
     */
    @SuppressWarnings("unchecked")
    public void initUserCache(String userid) {
        if (Tools.isBlank(userid)) {
            return;
        }
        HashMap<String, Object> userCacheObj = (HashMap<String, Object>) RdbCache.get("UserCacheStrategy", userid);
        if (userCacheObj == null) {
            String sql = "select * from BPM_OrgUserList where userid='" + userid + "'";
            Document userDoc = Rdb.getDocumentBySql("BPM_OrgUserList", sql);
            String lang = userDoc.g("LANG");
            Locale userLocale;
            if (Tools.isBlank(lang)) {
                userLocale = new Locale("zh", "CN");
            }
            else {
                String[] langArray = Tools.split(lang, ",");
                userLocale = new Locale(langArray[0], langArray[1]);
            }

            //缓存用户相关数据信息
            userCacheObj = new HashMap<String, Object>();
            userDoc.removeItem("Password");
            userDoc.removeItem("WF_LastModified");
            userDoc.removeItem("WF_DocCreated");
            userDoc.removeItem("WF_LastModified");
            userCacheObj.put("UserDoc", userDoc); //用户个人文档缓存
            userCacheObj.put("UserLocale", userLocale); //用户语言环境缓存
            userCacheObj.put("UserRoles", getRolesByUserid(userid)); //用户所拥有的角色缓存
            RdbCache.put("UserCacheStrategy", userid, userCacheObj); //加入用户缓存策略中

            //获得用户的主部门deptid缓存起来
            sql = "select Deptid from BPM_OrgUserDeptMap where Userid='" + userid + "' and MainDept='1' and OrgClass='" + BeanCtx.getOrgClass() + "'";
            String deptid = Rdb.getValueBySql(sql);
            userCacheObj.put("Deptid", deptid); //用户所在最小主部门的deptid

        }
    }

    /**
     * 切换用户的主部门
     * @param orgClass 组织架构标识
     * @param userid 用户id
     * @param deptid 要切换的部门的唯一id
     */
    public void changeCurrentDept(String orgClass, String userid, String deptid) {

        if (this.isDept(deptid)) {
            //如果部门存在才进行切换，如果不存在就不需要进行切换了

            //先全部取消
            String sql = "update BPM_OrgUserDeptMap set CurrentFlag='0' where userid='" + userid + "' and OrgClass='" + orgClass + "'";
            Rdb.execSql(sql);

            //再切换到兼职部门中去
            sql = "update BPM_OrgUserDeptMap set CurrentFlag='1' where Deptid='" + deptid + "' and OrgClass='" + orgClass + "'";
            Rdb.execSql(sql);

        }
    }

    /**
     * 工作流引擎将调用本函数进行环节参与者的用户解析 <br>
     * 解析任务环节中的参与者为具体的userid并去掉重复值和空值
     * @param potentialOwner 规则数据集合
     * @return 返回用户userid集合
     * @exception Exception 解析失败
     */
    public LinkedHashSet<String> parserNodeMembers(Set<String> potentialOwner) throws Exception {
        LinkedHashSet<String> userSet = new LinkedHashSet<String>();
        for (String userRule : potentialOwner) {
            int spos = userRule.indexOf(".");
            String ruleType = "", ruleValue = "";
            if (spos != -1) {
                ruleType = userRule.substring(0, spos + 1); //先获得.号前面的类型Rule. Role. Form. Node.
                ruleValue = userRule.substring(spos + 1, userRule.length()); //配置的值
            }
            if (Tools.isBlank(ruleType)) {
                userSet.addAll(this.parserUserid(userRule)); //这里面有可能是部门，角色，岗位，还不一定就是用户
            }
            else if (ruleType.equals("Rule.")) {
                //说明是规则，要进行计算
                userSet.addAll(((NodeUser) BeanCtx.getBean("NodeUser")).getNodeOwnerRuleUser(ruleValue)); //获得规则运算后的结果
            }
            else if (ruleType.equals("Role.")) {
                //说明是角色，进行角色用户解析Role.CurrentUser.RS003
                int epos = ruleValue.indexOf(".");
                if (epos != -1) {
                    String userid = ruleValue.substring(0, epos); //可能是 用户id,环节id,部门id
                    String roleNumber = ruleValue.substring(epos + 1);
                    if (userid.equals("CurrentUser")) {
                        //当前用户
                        userSet.addAll(getRoleMemberByUserid(BeanCtx.getUserid(), roleNumber));
                    }
                    else if (userid.equalsIgnoreCase("ProcessStarter")) {
                        //流程启动者
                        if (BeanCtx.getLinkeywf().getIsNewProcess()) {
                            //如果是新流程测获得当前用户的id
                            userid = BeanCtx.getUserid();
                        }
                        else {
                            //否则获得文档中的流程启动者
                            userid = BeanCtx.getLinkeywf().getDocument().g("WF_AddName"); //流程启动者
                        }
                        userSet.addAll(getRoleMemberByUserid(userid, roleNumber));
                    }
                    else if (userid.substring(0, 1).equals("T")) {
                        //说明是userTask节点
                        String sql = "select userid from BPM_InsUserList where DocUnid='" + BeanCtx.getLinkeywf().getDocUnid() + "' and processid='" + BeanCtx.getLinkeywf().getProcessid()
                                + "' and nodeid='" + userid + "'";
                        HashSet<String> insNodeUserSet = Rdb.getValueSetBySql(sql);
                        for (String nodeUserid : insNodeUserSet) {
                            userSet.addAll(getRoleMemberByUserid(nodeUserid, roleNumber)); //为每一个用户查找他所在部门的角色成员
                        }
                    }
                    else if (this.isDept(userid)) {
                        //说明是部门唯一id
                        userSet.addAll(getRoleMemberByDeptid(userid, roleNumber));
                    }
                }
            }
            else if (ruleType.equals("Form.")) {
                //说明是要解析文档字段
                String fdValue = BeanCtx.getLinkeywf().getDocument().g(ruleValue);
                userSet.addAll(Tools.splitAsLinkedSet(fdValue, ","));
            }
            else if (ruleType.equals("Node.")) {
                //说明是根据环节id获得参与者
                String sql = "select userid from BPM_InsUserList where DocUnid='" + BeanCtx.getLinkeywf().getDocUnid() + "' and processid='" + BeanCtx.getLinkeywf().getProcessid() + "' and nodeid='"
                        + ruleValue + "'";
                userSet.addAll(Rdb.getValueSetBySql(sql));
            }
            else {
                userSet.addAll(this.parserUserid(userRule)); //这里面有可能是部门，角色，岗位，还不一定就是用户
            }
        }
        userSet.remove("");
        return userSet;
    }

    /**
     * 根据用户id和角色编号逐级往上查找角色成员，直到找到为止，如果找不到返回空值
     * @param userid 用户id
     * @param roleNumber 角色编号
     * @return 角色成员的userid
     */
    public LinkedHashSet<String> getRoleMemberByUserid(String userid, String roleNumber) {
        String folderid = this.getDeptFolderidByUserid(userid, false); //获得用户所在的部门层级id
        return getRoleMemberByFolderid(folderid, roleNumber);
    }

    /**
     * 根据部门deptid和角色编号逐级往上查找角色成员，直到找到为止，如果找不到返回空值
     * @param deptid 部门唯一id
     * @param roleNumber 角色编号
     * @return 角色成员的userid集合
     */
    public LinkedHashSet<String> getRoleMemberByDeptid(String deptid, String roleNumber) {
        String folderid = this.getFolderidByDeptid(deptid);
        return getRoleMemberByFolderid(folderid, roleNumber);
    }

    /**
     * 根据部门folderid和角色编号逐级往上查找角色成员，直到找到为止，如果找不到返回空值
     * @param folderid 部门层级id
     * @param roleNumber 角色编号
     * @return 角色成员的userid集合
     */
    public LinkedHashSet<String> getRoleMemberByFolderid(String folderid, String roleNumber) {
        String orgClass = BeanCtx.getOrgClass();
        while (folderid.length() >= 3) {
            String sql = "select Member from BPM_OrgRoleMembers where  OrgClass='" + orgClass + "' and RoleNumber='" + roleNumber + "' and Folderid='" + folderid + "' order by SortNum";
            LinkedHashSet<String> member = Rdb.getValueLinkedSetBySql(sql, true);
            if (member.size() > 0) {
                return member; //说明找到了角色成员
            }
            else {
                //继续往上查找
                folderid = folderid.substring(0, folderid.length() - 3);
            }
        }
        return new LinkedHashSet<String>(); //没有找到返回空值
    }

    /**
     * 获得指定部门唯一编号下的所有人员列表
     * @param deptid 部门唯一编号
     * @param isSub 是否包含子部门
     * @return 返回userid集合
     */
    public LinkedHashSet<String> getAllUseridByDeptid(String deptid, boolean isSub) {
        Document doc = Rdb.getDocumentBySql("select Folderid,OrgClass from BPM_OrgDeptList where Deptid='" + deptid + "'");
        return getAllUseridByFolderid(doc.g("Folderid"), doc.g("OrgClass"), isSub);
    }

    /**
     * 获得指定部门层级编号下的所有人员列表
     * @param folderid 部门层级编号
     * @param orgClass 架构标识,空表示缺省架构
     * @param isSub 是否包含子部门
     * @return 返回userid集合
     */
    public LinkedHashSet<String> getAllUseridByFolderid(String folderid, String orgClass, boolean isSub) {
        if (Tools.isBlank(orgClass)) {
            orgClass = BeanCtx.getSystemConfig("DefaultOrgClass");
        }
        String sql = "";
        if (isSub) {
            sql = "select Userid from BPM_OrgUserDeptMap where Folderid like '" + folderid + "%' and OrgClass='" + orgClass + "'";
        }
        else {
            sql = "select Userid from BPM_OrgUserDeptMap where Folderid='" + folderid + "' and OrgClass='" + orgClass + "'";
        }
        return Rdb.getValueLinkedSetBySql(sql);
    }

    /**
     * 分析组织架构字符串并返回所有用户userid
     * @param orgStr 由userid,roleid,deptid组成的混合字符串
     * @return 返回userid
     */
    public LinkedHashSet<String> parserUserid(String orgStr) {
        LinkedHashSet<String> userset = new LinkedHashSet<String>();
        String[] userArray = Tools.split(orgStr);
        for (String userid : userArray) {
            if (userid.substring(0, 2).equals("RS") || userid.substring(0, 2).equals("RG")) {
                //看是否是角色
                userset.addAll(this.getRoleUserByRoleNumber(userid));
            }
            else if (this.isDept(userid)) {
                //说明是部门
                userset.addAll(getAllUseridByDeptid(userid, true));
            }
            else {
                //说明是用户
                userset.add(userid);
            }
        }
        return userset;
    }

    /**
     * 获得用户当前所在部门id，如果用户有兼职的情况下可以进行部门切换
     * @param userid 用户id
     * @param isdept true表示往上查找直到找到类型为部门时返回，false表示返回直属层级的depid
     * @return 返回字符串
     */
    public String getDeptFolderidByUserid(String userid, boolean isdept) {
        String sql = "select Folderid from BPM_OrgUserDeptMap where CurrentFlag='1' and OrgClass='" + BeanCtx.getOrgClass() + "' and Userid='" + userid + "'"; //从关系表中得到用户活动的Folderid
        String deptid = Rdb.getValueBySql(sql);
        if (Tools.isBlank(deptid)) {
            return "";
        }
        if (!isdept) {
            return deptid; //直接返回部门编号
        }
        else {
            //需要往上查找直到找到属性为部门的为止
            while (deptid.length() > 3) {
                sql = "select deptFlag from BPM_OrgDeptList where  OrgClass='" + BeanCtx.getOrgClass() + "' and Folderid='" + deptid + "'";
                if (Rdb.getValueBySql(sql).equals("Department")) {
                    return deptid;
                }
                else {
                    deptid = deptid.substring(0, deptid.length() - 3);
                }
            }
        }
        return "";
    }

    /**
     * 获得用户所在部门的唯一id
     * @param userid 用户id
     * @param isdept true表示往上查找直到找到类型为部门时返回，false表示返回最小部门的depid
     * @return 返回字符串
     */
    public String getDeptidByUserid(String userid, boolean isdept) {
        String sql = "select Folderid,Deptid from BPM_OrgUserDeptMap where CurrentFlag='1' and OrgClass='" + BeanCtx.getOrgClass() + "' and Userid='" + userid + "'"; //从关系表中得到用户活动的Folderid
        Document doc = Rdb.getDocumentBySql(sql);
        if (doc.isNull()) {
            return "";
        }
        String folderid = doc.g("Folderid");
        if (!isdept) {
            return doc.g("Deptid"); //直接返回部门deptid
        }
        else {
            //需要往上查找直到找到属性为部门的为止
            while (folderid.length() > 3) {
                sql = "select DeptFlag,Deptid from BPM_OrgDeptList where  OrgClass='" + BeanCtx.getOrgClass() + "' and Folderid='" + folderid + "'";
                Document pdoc = Rdb.getDocumentBySql(sql);
                if (pdoc.g("deptFlag").equalsIgnoreCase("Department")) {
                    return pdoc.g("Deptid");
                }
                else {
                    folderid = folderid.substring(0, folderid.length() - 3);
                }
            }
        }
        return "";
    }

    /**
     * 获得用户所在部门层级编号Folderid
     * @param userid 用户id
     * @param isdept true表示往上查找直到找到类型为部门时返回，false表示返回直属层级的Folderid
     * @return 返回字符串
     */
    public String getFolderidByUserid(String userid, boolean isdept) {
        String sql = "select * from BPM_OrgUserDeptMap where CurrentFlag='1' and OrgClass='" + BeanCtx.getOrgClass() + "' and Userid='" + userid + "'"; //从关系表中得到用户活动的Folderid
        Document doc = Rdb.getDocumentBySql(sql);
        if (doc.isNull()) {
            return "";
        }
        String folderid = doc.g("Folderid");
        if (!isdept) {
            return doc.g("Folderid"); //直接返回部门deptid
        }
        else {
            //需要往上查找直到找到属性为部门的为止
            while (folderid.length() > 3) {
                sql = "select * from BPM_OrgDeptList where  OrgClass='" + BeanCtx.getOrgClass() + "' and Folderid='" + folderid + "'";
                Document pdoc = Rdb.getDocumentBySql(sql);
                if (pdoc.g("deptFlag").equals("Department")) {
                    return pdoc.g("Folderid");
                }
                else {
                    folderid = folderid.substring(0, folderid.length() - 3);
                }
            }
        }
        return "";
    }

    /**
     * 获得用户所在部门的名称
     * @param userid 用户id
     * @param isdept true表示往上查找直到找到类型为部门时返回，false表示返回直属层级的部门名称
     * @return 字符串
     */
    public String getDeptNameByUserid(String userid, boolean isdept) {
        String deptid = getDeptFolderidByUserid(userid, isdept);
        return Rdb.getValueBySql("select FolderName from BPM_OrgDeptList where  OrgClass='" + BeanCtx.getOrgClass() + "' and Folderid='" + deptid + "'");
    }

    /**
     * 返回所有组织类型的中文名称可混合在一起如：userid，roleid,deptid用户id,角色id,部门id混合在一起传入 多个用逗号分隔传入
     * @param userList 多个用户用逗号分隔
     * @return 返回每个id的中文名称
     */
    public String getCnNameAllType(String userList) {
        LinkedHashSet<String> orgSet = new LinkedHashSet<String>();
        LinkedHashSet<String> userset = new LinkedHashSet<String>();
        String[] userArray = Tools.split(userList);
        for (String userid : userArray) {
            if (userid.substring(0, 2).equals("RS") || userid.substring(0, 2).equals("RG")) {
                //看是否是角色
                String sql = "select RoleName from BPM_OrgRoleList where RoleNumber='" + userid + "'";
                String roleName = Rdb.getValueBySql(sql);
                if (Tools.isBlank(roleName)) {
                    //说明是用户
                    userset.add(this.getCnName(userid));
                }
                else {
                    //说明是角色
                    orgSet.add(roleName);
                }
            }
            else if (this.isDept(userid)) {
                //说明是部门
                String sql = "select FolderName from BPM_OrgDeptList where Deptid='" + userid + "'";
                String deptName = Rdb.getValueBySql(sql);
                if (Tools.isBlank(deptName)) {
                    //说明是用户
                    userset.add(this.getCnName(userid));
                }
                else {
                    //说明是部门
                    orgSet.add(deptName);
                }
            }
            else {
                //说明是用户
                userset.add(this.getCnName(userid));
            }
        }
        return Tools.join(userset, ",");
    }

    /**
     * 获得用户中文名称一次可以传入多个用户
     * @param userid 多个用户使用逗号分隔
     * @return 返回中文名称字符串
     */
    public String getCnName(String userid) {
        if (Tools.isBlank(userid)) {
            BeanCtx.log("W", "LinkeyUser.getCnName()传入了空的userid变量...");
            return "";
        }
        String sql;
        if (userid.indexOf(",") != -1) {
            String[] userArray = Tools.split(userid, ",");
            StringBuilder sqlWhere = new StringBuilder();
            int i = 0;
            for (String userItem : userArray) {
                if (i == 0) {
                    sqlWhere.append("userid='" + userItem + "'");
                    i = 1;
                }
                else {
                    sqlWhere.append(" or userid='" + userItem + "'");
                }
            }
            sql = "select CnName from BPM_OrgUserList where " + sqlWhere.toString();
        }
        else {
            sql = "select CnName from BPM_OrgUserList where userid='" + userid + "'";
        }
        return Rdb.getValueBySql(sql);
    }

    /**
     * 获得用户中文名称一次可以传入一个LinkedHashSet集合对像
     * 
     * @param userid set集合对像
     * @return 返回中文名称的LinkedHashSet集合对像
     */
    public LinkedHashSet<String> getCnName(LinkedHashSet<String> userid) {
        String sql;
        StringBuilder sqlWhere = new StringBuilder();
        int i = 0;
        for (String userItem : userid) {
            if (i == 0) {
                sqlWhere.append("userid='" + userItem + "'");
                i = 1;
            }
            else {
                sqlWhere.append(" or userid='" + userItem + "'");
            }
        }
        if (i == 0) {
            return new LinkedHashSet<String>(); //未传入用户
        }
        else {
            sql = "select CnName from BPM_OrgUserList where " + sqlWhere.toString();
        }
        return Rdb.getValueLinkedSetBySql(sql);
    }

    /**
     * 根据部门唯一编号获得部门名称
     * 
     * @param deptid 部门唯一编号
     * @return 返回部门名称
     */
    public String getDeptNameByDeptid(String deptid) {
        String sql = "select FolderName from BPM_OrgDeptList where Deptid='" + deptid + "'";
        return Rdb.getValueBySql(sql);
    }

    /**
     * 根据部门唯一编号获得部门所在的架构标识
     * 
     * @param deptid 部门唯一编号
     * @return 返回部门名称
     */
    public String getOrgClassByDeptid(String deptid) {
        String sql = "select OrgClass from BPM_OrgDeptList where Deptid='" + deptid + "'";
        return Rdb.getValueBySql(sql);
    }

    /**
     * 根据部门唯一编号获得部门名称
     * 
     * @param deptidSet 部门唯一编号的set集合
     * @return 返回部门名称
     */
    public Set<String> getDeptNameByDeptid(Set<String> deptidSet) {
        if (deptidSet.size() == 0) {
            return new HashSet<String>();
        }
        StringBuilder sqlWhere = new StringBuilder();
        int i = 0;
        for (String folderid : deptidSet) {
            if (i == 0) {
                sqlWhere.append("Deptid='" + folderid + "'");
                i = 1;
            }
            else {
                sqlWhere.append(" or Deptid='" + folderid + "'");
            }
        }
        String sql = "select FolderName from BPM_OrgDeptList where " + sqlWhere.toString();
        return Rdb.getValueSetBySql(sql);
    }

    /**
     * 根据部门层级编号获得部门名称
     * 
     * @param orgClass 架构标识 传入空值表示使用缺省架构
     * @param folderid 部门编号
     * @return 返回部门名称
     */
    public String getDeptNameByFolderid(String folderid, String orgClass) {
        if (Tools.isBlank(orgClass)) {
            orgClass = BeanCtx.getOrgClass();
        }
        String sql = "select FolderName from BPM_OrgDeptList where Folderid='" + folderid + "' and OrgClass='" + orgClass + "'";
        return Rdb.getValueBySql(sql);
    }

    /**
     * 根据部门层级编号获得部门名称
     * 
     * @param orgClass 架构标识 传入空值表示使用缺省架构
     * @param folderSet folderid部门编号的set集合
     * @return 返回集合部门名称
     */
    public Set<String> getDeptNameByFolderid(Set<String> folderSet, String orgClass) {
        if (folderSet.size() == 0) {
            return new HashSet<String>();
        }
        if (Tools.isBlank(orgClass)) {
            orgClass = BeanCtx.getOrgClass();
        }
        StringBuilder sqlWhere = new StringBuilder();
        int i = 0;
        for (String folderid : folderSet) {
            if (i == 0) {
                sqlWhere.append("Folderid='" + folderid + "'");
                i = 1;
            }
            else {
                sqlWhere.append(" or Folderid='" + folderid + "'");
            }
        }
        String sql = "select FolderName from BPM_OrgDeptList where " + sqlWhere.toString() + " and OrgClass='" + orgClass + "'";
        return Rdb.getValueSetBySql(sql);
    }

    /**
     * 根据部门层级编号获得部门唯一编号
     * 
     * @param orgClass 架构标识 传入空值表示使用缺省架构
     * @param folderid 部门层级编号
     * @return 字符串
     */
    public String getDeptidByFolderid(String folderid, String orgClass) {
        if (Tools.isBlank(orgClass)) {
            orgClass = BeanCtx.getOrgClass();
        }
        String sql = "select Deptid from BPM_OrgDeptList where Folderid='" + folderid + "' and OrgClass='" + orgClass + "'";
        return Rdb.getValueBySql(sql);
    }

    /**
     * 根据部门唯一编号获得部门层级编号
     * 
     * @param deptid 部门唯一编号
     * @return 部门层级编号
     */
    public String getFolderidByDeptid(String deptid) {
        String sql = "select Folderid from BPM_OrgDeptList where Deptid='" + deptid + "'";
        return Rdb.getValueBySql(sql);
    }

    /**
     * 获得用户所在部门的全称
     * 
     * @param userid 用户id
     * @param ctd 是否返回第一层公司名 true表示返回,false表示不返回
     * @return 返回如： 公司/财务部/会计科
     */
    public String getFullDeptNameByUserid(String userid, boolean ctd) {
        String deptid = getDeptFolderidByUserid(userid, false);
        StringBuilder deptName = new StringBuilder();
        int i = 0;
        while ((deptid.length() >= 3 && ctd == true) || (deptid.length() > 3 && ctd == false)) {
            String sql = "select FolderName from BPM_OrgDeptList where OrgClass='" + BeanCtx.getOrgClass() + "' and Folderid='" + deptid + "'";
            if (i == 0) {
                deptName.append(Rdb.getValueBySql(sql));
                i = 1;
            }
            else {
                deptName.insert(0, Rdb.getValueBySql(sql) + "/");
            }
            deptid = deptid.substring(0, deptid.length() - 3);
        }
        return deptName.toString();
    }

    /**
     * 根据部门唯一编号获得部门的全称
     * 
     * @param deptid 部门id
     * @param ctd 是否返回第一层公司名 true表示返回,false表示不返回
     * @return 返回如： 公司/财务部/会计科
     */
    public String getFullDeptNameByDeptid(String deptid, boolean ctd) {
        return getFullDeptNameByFolderid(getFolderidByDeptid(deptid), ctd);
    }

    /**
     * 获得部门的全称
     * 
     * @param folderid 部门层级id
     * @param ctd 是否返回第一层公司名 true表示返回,false表示不返回
     * @return 返回如： 公司/财务部/会计科
     */
    public String getFullDeptNameByFolderid(String folderid, boolean ctd) {
        StringBuilder deptName = new StringBuilder();
        int i = 0;
        while ((folderid.length() >= 3 && ctd == true) || (folderid.length() > 3 && ctd == false)) {
            String sql = "select FolderName from BPM_OrgDeptList where OrgClass='" + BeanCtx.getOrgClass() + "' and Folderid='" + folderid + "'";
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

    /**
     * 获得用户的中文名和所在部门
     * 
     * @param userid 用户id
     * @param ctd 是否返回第一层公司名称true表示返回，false表示不返回
     * @return 返回如：张三/综合管理部/办公室
     */
    public String getCnNameAndDeptName(String userid, boolean ctd) {
        String[] userArray = Tools.split(userid, ",");
        StringBuilder userDeptList = new StringBuilder();
        for (String userItem : userArray) {
            String userCnName = getCnName(userItem);
            String deptName = getFullDeptNameByUserid(userItem, ctd);
            if (userDeptList.length() > 0) {
                userDeptList.append(",");
            }
            userDeptList.append(userCnName + "/" + deptName);
        }
        return userDeptList.toString();
    }

    /**
     * 获得用户文档对像
     * 
     * @param userid 用户id
     * @return 返回用户文档对像
     */
    @SuppressWarnings("unchecked")
    public Document getUserDoc(String userid) {
        Document userdoc = null;
        HashMap<String, Object> userCacheObj = (HashMap<String, Object>) RdbCache.get("UserCacheStrategy", userid);
        if (userCacheObj != null) {
            userdoc = (Document) userCacheObj.get("UserDoc");
        }
        else {
            String sql = "select * from BPM_OrgUserList where userid='" + userid + "'";
            userdoc = Rdb.getDocumentBySql("BPM_OrgUserList", sql);
            userdoc.removeItem("Password");
            userdoc.removeItem("WF_LastModified");
            userdoc.removeItem("WF_DocCreated");
            userdoc.removeItem("WF_LastModified");
        }
        return userdoc;
    }

    /**
     * 获得部门文档对像
     * 
     * @param deptid 部门id
     * @return 返回部门文档对像
     */
    public Document getDeptDoc(String deptid) {
        String sql = "select * from BPM_OrgDeptList where  Deptid='" + deptid + "'";
        return Rdb.getDocumentBySql("BPM_OrgDeptList", sql);
    }

    /**
     * 根据部门层级编号和架构标识获得部门文档对像
     * 
     * @param folderid 部门层级编号
     * @param orgClass 所属架构标识,传null表示使用默认架构标识
     * @return 返回部门文档对像
     */
    public Document getDeptDoc(String folderid, String orgClass) {
        if (orgClass == null) {
            orgClass = BeanCtx.getOrgClass();
        }
        String sql = "select * from BPM_OrgDeptList where OrgClass='" + orgClass + "' and Folderid='" + folderid + "'";
        return Rdb.getDocumentBySql("BPM_OrgDeptList", sql);
    }

    /**
     * 根据用户id获得所有部门的成员
     * 
     * @param userid 用户id
     * @param isdept true表示返回部内所有人员列表，false表示只返回直属层级的用户列表
     * @return 字符串
     */
    public String getAllDeptMemberByUserid(String userid, boolean isdept) {
        return getAllDeptMemberByFolderid(getDeptFolderidByUserid(userid, isdept), isdept);
    }

    /**
     * 根据部门层级id获得所有部门的成员
     * 
     * @param folderid 部门对应文件夹id
     * @param isdept true表示返回部内所有人员列表，false表示只返回直属层级的用户列表
     * @return 字符串
     */
    public String getAllDeptMemberByFolderid(String folderid, boolean isdept) {
        String sql;
        if (isdept) {
            sql = "select Userid from BPM_OrgUserDeptMap where OrgClass='" + BeanCtx.getOrgClass() + "' and Folderid like '" + folderid + "%'";
        }
        else {
            sql = "select Userid from BPM_OrgUserDeptMap where OrgClass='" + BeanCtx.getOrgClass() + "' and Folderid='" + folderid + "'";
        }
        return Rdb.getValueBySql(sql);
    }

    /**
     * 判断用户是否在指定的职位中
     * 
     * @param jobList 职位列表 经理,科长,部门
     * @param userid 用户id
     * @return 返回true表示成立，false表示不存在
     */
    public boolean inJobTitle(String jobList, String userid) {
        String jobTitle = Rdb.getValueBySql("select JobTitle from BPM_OrgUserList where userid='" + userid + "'");
        return Tools.inArray(Tools.split(jobList, ","), jobTitle);
    }

    /**
     * 根据角色编号获得角色成员
     * 
     * @param roleNumber 角色编号
     * @return 去掉重复值和空值的
     */
    public HashSet<String> getRoleUserByRoleNumber(String roleNumber) {
        String sql = "select Member from BPM_OrgRoleMembers where RoleNumber='" + roleNumber + "'";
        HashSet<String> userMember = Rdb.getValueSetBySql(sql, true);
        HashSet<String> allUserMember = new HashSet<String>();
        allUserMember.addAll(userMember);
        for (String userid : userMember) {
            if (userid.startsWith("RS") || userid.startsWith("RG")) {
                //看成员里面是否有子角角色
                allUserMember.remove(userid);
                allUserMember.addAll(getRoleUserByRoleNumber(userid));
            }
        }
        return allUserMember;
    }

    /**
     * 根据部门唯一编号返回指定角色下的所属此部门编号的角色成员
     * 
     * @param roleNumber 角色编号
     * @param deptid 部门id
     * @return 返回ArrayList
     */
    public LinkedHashSet<String> getRoleUserByDeptid(String roleNumber, String deptid) {
        String sql = "select Member from BPM_OrgRoleMembers where RoleNumber='" + roleNumber + "' and Deptid='" + deptid + "'";
        return Rdb.getValueLinkedSetBySql(sql, true);
    }

    /**
     * 获得用户的语言环境配置值
     * 
     * @param userid 用户id
     * @return 用户的语言环境配置值
     */
    public Locale getUserLocale(String userid) {
        String sql = "select LANG from BPM_OrgUserList where Userid='" + userid + "'";
        String lang = Rdb.getValueBySql(sql);
        if (Tools.isBlank(lang)) {
            return new Locale("zh", "CN");
        }
        else {
            String[] langArray = Tools.split(lang, ",");
            return new Locale(langArray[0], langArray[1]);
        }
    }

	/**
	 * 获得指定用户所拥用的所有角色列表，允许嵌套角色,角色编号必须要以RS开头才能支持嵌套角色 还需要获取用户所有所属的部门编号作为群组权限使用
	 * 
	 * @param userid 用户id
	 * @return 返回角色编号的set集合
	 */
	public HashSet<String> getRolesByUserid(String userid) {
		String sql = "select RoleNumber from BPM_OrgRoleMembers where Member='" + userid + "'"; //获得角色成员中有此用户的所有角色列表
		HashSet<String> roleSet = Rdb.getValueSetBySql(sql);
		HashSet<String> userRoles = new HashSet<String>();
		for (String roleNum : roleSet) {
			userRoles.add(roleNum);

			// 202010 add by alibao 支持角色、岗位(群组)互相嵌套==============start
			/*if (roleNum.startsWith("RS")) {
			    userRoles.addAll(getParentRoles(roleNum)); //获得此角色的所有上级角色
			}*/
			HashSet<String> subSet = getParentRoles(roleNum);
			if (!subSet.isEmpty()) {
				userRoles.addAll(subSet);
			}
			//================================================================end

		}
		userRoles.addAll(getAllDeptidByUserid(userid)); //追加此用户所属部门的deptid作为群组权限
		return userRoles;
	}
	

	/**
	 * 获得指定角色的所有上级角色如A角色的成员中包括B角色，则返回A
	 * 
	 * @param roleNumber 角色编号
	 * @return 返回对应角色
	 */
	public HashSet<String> getParentRoles(String roleNumber) {

		//202010 Oracle 优化处理，将递归放到数据库中遍历=====================start
		//select * from (select * from BPM_OrgRoleMembers) start with MEMBER='xieliang 14734' connect by prior RoleNumber=MEMBER
		if ("ORACLE".equalsIgnoreCase(Rdb.getDbType())) {
			String sql2 = "select RoleNumber from (select * from BPM_OrgRoleMembers) start with MEMBER='" + roleNumber
					+ "' connect by prior RoleNumber=MEMBER";
			return Rdb.getValueSetBySql(sql2);
		}
		
		// with temp (MEMBER,RoleNumber,MEMBERNAME) as (select MEMBER,RoleNumber,MEMBERNAME from BPM_OrgRoleMembers where MEMBER='admin' union all  select a.MEMBER, a.RoleNumber,a.MEMBERNAME from BPM_OrgRoleMembers a inner join temp on a.[MEMBER] = temp.[RoleNumber]) select * from temp
		if ("MSSQL".equalsIgnoreCase(Rdb.getDbType())) {
			String sql3 = "with temp (MEMBER,RoleNumber,MEMBERNAME) as (select MEMBER,RoleNumber,MEMBERNAME from BPM_OrgRoleMembers where MEMBER='" + roleNumber + "' union all  select a.MEMBER, a.RoleNumber,a.MEMBERNAME from BPM_OrgRoleMembers a inner join temp on a.[MEMBER] = temp.[RoleNumber]) select RoleNumber from temp";
			return Rdb.getValueSetBySql(sql3);
		}

		//==================================================================end

		String sql = "select RoleNumber from BPM_OrgRoleMembers where Member='" + roleNumber + "'"; //获得角色成员中有此角色的所有角色列表
		HashSet<String> roleSet = Rdb.getValueSetBySql(sql);
		HashSet<String> subRoles = new HashSet<String>();
		for (String roleNum : roleSet) {
			subRoles.add(roleNum);

			// 202010 add by alibao 支持角色、岗位(群组)互相嵌套==============start
			/* if (roleNum.startsWith("RS")) {
			    subRoles.addAll(getParentRoles(roleNum));//递归调用
			}*/
			HashSet<String> subSet = getParentRoles(roleNum);
			if (!subSet.isEmpty()) {
				subRoles.addAll(subSet);//递归调用
			}
			//================================================================end
		}
		return subRoles;
	}

    /**
     * 获得此用户所具有的所有含上级部门的唯一编号
     * 
     * @param userid 用户编号
     * @return 此用户所具有的所有含上级部门的唯一编号
     */
    public HashSet<String> getAllDeptidByUserid(String userid) {
        HashSet<String> deptid = new HashSet<String>();
        String sql = "select OrgClass,Folderid from BPM_OrgUserDeptMap where Userid='" + userid + "'";
        Document[] dc = Rdb.getAllDocumentsBySql(sql);
        for (Document doc : dc) {
            deptid.addAll(getAllDeptidByFolderid(doc.g("OrgClass"), doc.g("Folderid")));
        }
        return deptid;
    }

    /**
     * 获得部门以及上级部门的所有deptid
     * 
     * @param orgClass 架构标识
     * @param folderid 层级编号
     * @return 返回deptid集合
     */
    public HashSet<String> getAllDeptidByFolderid(String orgClass, String folderid) {
        HashSet<String> deptid = new HashSet<String>();
        while (folderid.length() >= 3) {
            String sql = "select Deptid from BPM_OrgDeptList where OrgClass='" + orgClass + "' and Folderid='" + folderid + "'";
            deptid.add(Rdb.getValueBySql(sql));
            folderid = folderid.substring(0, folderid.length() - 3);
        }
        return deptid;
    }

    /**
     * 获得用户的邮件地址
     * 
     * @param userid 多个用逗号分隔
     * @return 返回邮件地址字符串，没有邮件地址的用户不返回
     */
    public String getMailAddress(String userid) {
        String sql = "";
        if (userid.indexOf(",") != -1) {
            String[] userArray = Tools.split(userid, ",");
            StringBuilder sqlWhere = new StringBuilder();
            int i = 0;
            for (String userItem : userArray) {
                if (i == 0) {
                    sqlWhere.append("userid='" + userItem + "'");
                    i = 1;
                }
                else {
                    sqlWhere.append(" or userid='" + userItem + "'");
                }
            }
            sql = "select InternetAddress from BPM_OrgUserList where (" + sqlWhere.toString() + ") and InternetAddress<>''";
        }
        else {
            sql = "select InternetAddress from BPM_OrgUserList where userid='" + userid + "' and InternetAddress<>''";
        }
        return Rdb.getValueBySql(sql);
    }

    /**
     * 判断用户是否具有指定的角色权限，注意如果roles为空值时则表示inRoles是成立的
     * 
     * @param userid 用户id
     * @param roles 角色列表，多个用逗号他隔
     * @return 如果有返回ture,没有返回false
     */
    public boolean inRoles(String userid, String roles) {

        //如果是空角色则默认为成立
        if (Tools.isBlank(roles)) {
            return true;
        }

        //如果角色中包含用户id则返回true
        if (("," + roles + ",").indexOf("," + userid + ",") != -1) {
            return true;
        }

        //如果是超级管理员也返回true
        if (BeanCtx.getLinkeyUser().isSystemAdmin() == true) {
            return true;
        }

        //最后判断两个角色之间是否有交集，有交集则返回true
        HashSet<String> sourceRolesSet = Tools.splitAsSet(roles);
        HashSet<String> targetRoleSet = BeanCtx.getUserRoles(userid); //获得此用户所具有的全部角色列表,从BeanCtx中获取具有缓存功能
        sourceRolesSet.retainAll(targetRoleSet); //取两个角色列表的交集,如果有说明成立，没有说明没有权限
        if (sourceRolesSet.size() == 0) {
            return false;
        }
        else {
            return true;
        }

    }

    /**
     * 看用户id是否是部门类型
     * 
     * @param deptid 部门唯一id
     * 
     * @return true 部门类型；false 不是部门类型；
     */
    public boolean isDept(String deptid) {
        String sql = "select WF_OrUnid from BPM_OrgDeptList where Deptid='" + deptid + "'";
        return Rdb.hasRecord(sql);
    }

    /**
     * 当前用户是否系统超级管理员
     * 
     * @return true 系统超级管理员；false 不是系统超级管理员
     */
    public boolean isSystemAdmin() {
        String userid = BeanCtx.getUserid();
        String sql = "select WF_OrUnid from BPM_OrgRoleMembers where RoleNumber='RS002' and Member='" + userid + "'";
        return Rdb.hasRecord(sql);
    }
    
    /**
     * 当前用户是否系统设计员
     * 
     * @return true 是系统设计员，false 不是系统设计员；
     */
    public boolean isDesigner() {
        String userid = BeanCtx.getUserid();
        String sql = "select WF_OrUnid from BPM_OrgRoleMembers where RoleNumber='RS001' and Member='" + userid + "'";
        return Rdb.hasRecord(sql);
    }

    /**
     * 当前用户是否是应用管理员
     * @param appid 应用id
     * @return 返回true 是应用管理员，false 不是应用管理员
     */
    public boolean isAppAdmin(String appid) {
        String sql = "select Owner from BPM_AppList where WF_Appid='" + appid + "'";
        String appOwner = Rdb.getValueBySql(sql);
        boolean r = BeanCtx.getLinkeyUser().inRoles(BeanCtx.getUserid(), appOwner);//应用管理员中支持角色
        return r;
    }

}
