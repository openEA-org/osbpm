package cn.linkey.rulelib.S006;

import java.io.IOException;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.Control;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import javax.naming.ldap.PagedResultsControl;
import javax.naming.ldap.PagedResultsResponseControl;
import java.util.*;
import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.util.Tools;
import cn.linkey.rule.LinkeyRule;

/**
 * @RuleName:AD同步
 * @author admin
 * @version: 8.0
 * @Created: 2014-06-30 16:42
 */
final public class R_S006_B016 implements LinkeyRule {

    private LdapContext ctx = null;
    private String root = "";
    private String LDAP_URL = "";
    private String adminName = "";
    private String adminPassword = "";
    private String searchBase = "";
    private String HTTPPassword = Tools.md5("pass");

    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        //params为运行本规则时所传入的参数
        root = BeanCtx.getSystemConfig("Ad.root");
        LDAP_URL = BeanCtx.getSystemConfig("Ad.LDAP_URL");
        adminName = BeanCtx.getSystemConfig("Ad.adminName");
        adminPassword = BeanCtx.getSystemConfig("Ad.adminPwd");
        searchBase = BeanCtx.getSystemConfig("Ad.searchBase");
        // root = "DC=oatest,DC=gzs,DC=com";
        // LDAP_URL = "ldap://172.20.3.77:389";
        // adminName = "oatest\\HRvistor";
        // adminPassword = "gzzq@123";
        // searchBase = "OU=gzjt,DC=oatest,DC=gzs,DC=com";

        connectLdap(); //链接ldap
        DeptSync(); //同步部门
        UserSync(); //同步用户
        closeLdap(); //关闭链接
        BeanCtx.p("AD同步结束");

        return "";
    }

    /**
     * 
     * 建立Ldap连接
     * 
     * @return LdapContext
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void connectLdap() {
        Hashtable HashEnv = new Hashtable();

        HashEnv.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        HashEnv.put(Context.PROVIDER_URL, LDAP_URL);
        HashEnv.put(Context.SECURITY_AUTHENTICATION, "simple");
        HashEnv.put(Context.SECURITY_PRINCIPAL, adminName);
        HashEnv.put(Context.SECURITY_CREDENTIALS, adminPassword);

        try {
            ctx = new InitialLdapContext(HashEnv, null);// 初始化上下文
            BeanCtx.log("W", "认证成功");
        }
        catch (NamingException e) {
            BeanCtx.log(e, "W", "认证失败");
        }
        catch (Exception e) {
            BeanCtx.log(e, "W", "认证出错");
        }
    }

    /**
     * 关闭Ldap连接
     */
    public void closeLdap() {
        if (ctx != null) {
            try {
                ctx.close();
            }
            catch (NamingException e) {
                System.out.println("NamingException in close()" + e);
            }
        }
    }

    /**
     * 通过name属性值查询OU显示名
     */
    @SuppressWarnings("rawtypes")
    public String getDeptDisplayName(String name) {
        String FolderName = "";
        SearchControls searchCtls = new SearchControls();
        searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        String searchFilter = "(&(objectClass=OrganizationalUnit)(name=" + name + "))";// 组织单元
        String returnedAtts[] = { "displayName", "description" };// 定制返回属性
        searchCtls.setReturningAttributes(returnedAtts);// 设置返回属性集

        try {
            NamingEnumeration answer = ctx.search(searchBase, searchFilter, searchCtls);

            while (answer != null && answer.hasMoreElements()) {
                // 遍历结果集
                SearchResult sr = (SearchResult) answer.next();
                Attributes Attrs = sr.getAttributes();
                if (Attrs != null) {
                    String displayName = "";
                    String description = "";
                    for (NamingEnumeration ne = Attrs.getAll(); ne.hasMore();) {
                        // 得到下一个属性
                        Attribute Attr = (Attribute) ne.next();
                        String AttributeID = Attr.getID().toString();

                        if (AttributeID.equals("displayName")) {
                            // 遍历读取属性值
                            for (NamingEnumeration e = Attr.getAll(); e.hasMore();) {
                                displayName = e.next().toString();
                            }
                        }
                        if (AttributeID.equals("description")) {
                            // 遍历读取属性值
                            for (NamingEnumeration e = Attr.getAll(); e.hasMore();) {
                                description = e.next().toString();
                            }
                        }
                        if (displayName.trim().length() < description.trim().length()) {
                            FolderName = description;
                        }
                        else {
                            FolderName = displayName;
                        }
                    }
                }
            }
        }
        catch (NamingException e) {
            e.printStackTrace();
        }
        return FolderName;
    }

    /**
     * 部门同步
     */
    @SuppressWarnings("rawtypes")
    public void DeptSync() {
        SearchControls searchCtls = new SearchControls();
        searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        String searchFilter = "objectClass=OrganizationalUnit";// 组织单元

        String returnedAtts[] = { "distinguishedName" };// 定制返回属性

        searchCtls.setReturningAttributes(returnedAtts);// 设置返回属性集

        int totalResults = 0;
        try {
            NamingEnumeration answer = ctx.search(searchBase, searchFilter, searchCtls);
            while (answer != null && answer.hasMoreElements()) {
                // 遍历结果集
                SearchResult sr = (SearchResult) answer.next();
                // 得到符合条件的属性集
                Attributes Attrs = sr.getAttributes();
                if (Attrs != null) {
                    String distinguishedName = "";

                    for (NamingEnumeration ne = Attrs.getAll(); ne.hasMore();) {
                        // 得到下一个属性
                        Attribute Attr = (Attribute) ne.next();
                        String AttributeID = Attr.getID().toString();

                        if (AttributeID.equals("distinguishedName")) {
                            // 遍历读取属性值
                            for (NamingEnumeration e = Attr.getAll(); e.hasMore();) {
                                distinguishedName = e.next().toString();
                            }
                        }
                    }
                    // System.out.println(distinguishedName);

                    String OUFull = distinguishedName.replaceAll("," + root, "");

                    OUFull = OUFull.replaceAll("OU=", "");

                    String[] OU = OUFull.split(",");

                    for (int i = 0; i < OU.length / 2; i++) {
                        String temp = OU[i];
                        temp = OU[i];
                        OU[i] = OU[OU.length - 1 - i];
                        OU[OU.length - 1 - i] = temp;
                    }

                    String ParentFolderId = "";
                    String FolderId = "";

                    try {
                        for (int i = 0; i < OU.length; i++) {
                            Document doc = Rdb.getDocumentBySql("select * from BPM_OrgDeptList where Deptid='" + OU[i] + "'"); //根据deptid查找部门文档是否已存在
                            if (doc.isNull()) {
                                if (i == 0) {
                                    FolderId = this.getNewFolder(BeanCtx.getOrgClass(), "root", "BPM_OrgDeptList");
                                    RegNewDept(FolderId, OU[i]);
                                    ParentFolderId = FolderId;
                                }
                                else {
                                    FolderId = this.getNewFolder(BeanCtx.getOrgClass(), ParentFolderId, "BPM_OrgDeptList");
                                    RegNewDept(FolderId, OU[i]);
                                    ParentFolderId = FolderId;
                                }
                            }
                            else {
                                ParentFolderId = doc.g("FolderId");
                            }
                        }
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                totalResults++;
                System.out.println("同步Dept第-" + totalResults + "-条数据");
            }
        }
        catch (NamingException e) {
            e.printStackTrace();
        }
    }

    /**
     * 用户同步
     */
    @SuppressWarnings("rawtypes")
    public void UserSync() {
        SearchControls searchCtls = new SearchControls();
        searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        String searchFilter = "(&(objectCategory=Person)(objectClass=User))";
        String returnedAtts[] = { "name", "displayName", "mail", "distinguishedName" };// 定制返回属性
        searchCtls.setReturningAttributes(returnedAtts);// 设置返回属性集

        int totalResults = 0;
        byte[] cookie = null;
        int pageSize = 980; // 每次获取多少条

        try {
            ctx.setRequestControls(new Control[] { new PagedResultsControl(pageSize, Control.CRITICAL) });
            do {
                NamingEnumeration answer = ctx.search(searchBase, searchFilter, searchCtls);
                while (answer != null && answer.hasMoreElements()) {
                    // 遍历结果集
                    SearchResult sr = (SearchResult) answer.next();
                    // 得到符合条件的属性集
                    Attributes Attrs = sr.getAttributes();
                    if (Attrs != null) {
                        String AttributeID = "";
                        String distinguishedName = "";
                        String ShortName = "";
                        String ChinaeseName = "";
                        String InternetAddress = "";
                        String FolderId = "";

                        for (NamingEnumeration ne = Attrs.getAll(); ne.hasMore();) {
                            // 得到下一个属性
                            Attribute Attr = (Attribute) ne.next();
                            AttributeID = Attr.getID().toString();

                            if (AttributeID.equals("name")) {
                                // 遍历读取属性值
                                for (NamingEnumeration e = Attr.getAll(); e.hasMore();) {
                                    ShortName = e.next().toString();
                                }
                            }
                            if (AttributeID.equals("displayName")) {
                                // 遍历读取属性值
                                for (NamingEnumeration e = Attr.getAll(); e.hasMore();) {
                                    ChinaeseName = e.next().toString();
                                }
                            }
                            if (AttributeID.equals("mail")) {
                                // 遍历读取属性值
                                for (NamingEnumeration e = Attr.getAll(); e.hasMore();) {
                                    InternetAddress = e.next().toString();
                                }
                            }
                            if (AttributeID.equals("distinguishedName")) {
                                // 遍历读取属性值
                                for (NamingEnumeration e = Attr.getAll(); e.hasMore();) {
                                    distinguishedName = e.next().toString();
                                }
                            }
                        }

                        distinguishedName = distinguishedName.substring(distinguishedName.indexOf("OU="));

                        String OUName = distinguishedName.substring(3, distinguishedName.indexOf(","));

                        try {
                            Document doc = BeanCtx.getLinkeyUser().getUserDoc(OUName);
                            if (doc.isNull()) {
                                System.out.println("doc is null");
                            }
                            else {
                                FolderId = BeanCtx.getLinkeyUser().getFolderidByUserid(OUName, false);
                            }

                            RegNewUser(ShortName, ChinaeseName, FolderId, InternetAddress, HTTPPassword);
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                    totalResults++;
                    System.out.println("同步User第-" + totalResults + "-条数据");
                }

                Control[] controls = ctx.getResponseControls();
                if (controls != null) {
                    for (int i = 0; i < controls.length; i++) {
                        if (controls[i] instanceof PagedResultsResponseControl) {
                            PagedResultsResponseControl prrc = (PagedResultsResponseControl) controls[i];
                            cookie = prrc.getCookie();
                        }
                        else {
                        }
                    }
                }

                ctx.setRequestControls(new Control[] { new PagedResultsControl(pageSize, cookie, Control.CRITICAL) });

            }
            while (cookie != null);
        }
        catch (NamingException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

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

    /**
     * 传入文件夹编号，自动计算出父文件夹的编号
     * 
     */
    public String GetParentFolderId(String FolderId) {
        if (FolderId.length() == 3) {
            return "root";
        }
        else {
            return FolderId.substring(0, FolderId.length() - 3);
        }
    }

    /**
     * 注册部门
     * 
     */
    public void RegNewDept(String FolderId, String DeptId) {

        String ParentFolderId = GetParentFolderId(FolderId);// 部门层次ID
        String FolderName = getDeptDisplayName(DeptId);
        Document NewDoc = BeanCtx.getDocumentBean("BPM_OrgDeptList");
        // 类型: Company,Department,Team
        if (FolderId.length() == 3) {
            NewDoc.s("deptFlag", "Company");
        }
        else {
            if (FolderId.length() > 9) {
                NewDoc.s("deptFlag", "Team");
            }
            else {
                NewDoc.s("deptFlag", "Department");
            }
        }
        NewDoc.s("DeptId", DeptId);
        NewDoc.s("FolderName", FolderName);
        NewDoc.s("FolderId", FolderId);
        NewDoc.s("ParentFolderId", ParentFolderId);
        NewDoc.s("SortNumber", "1101");
        NewDoc.s("OrgClass", BeanCtx.getOrgClass());
        NewDoc.save();
    }

    /**
     * 注册用户
     * 
     */
    public void RegNewUser(String ShortName, String ChinaeseName, String FolderId, String InternetAddress, String HTTPPassword) {
        Document NewDoc = BeanCtx.getDocumentBean("BPM_OrgUserList");
        if (NewDoc.isNull()) {
            // 用户不存在,进行注册操作
            NewDoc.s("PhoneNumber", "");
            NewDoc.s("Password", Tools.md5(HTTPPassword));
            NewDoc.s("IndexFlag", "");
            NewDoc.s("InternetAddress", InternetAddress);
            NewDoc.s("JobTitle", "职员");
            NewDoc.s("Userid", ShortName);
            NewDoc.s("CnName", "ChinaeseName");
            NewDoc.s("SortNumber", "1101");
            NewDoc.s("LANG", "zh,CN");
            NewDoc.save();

            //1.先删除当前用户与部门的
            String sql = "delete from BPM_OrgUserDeptMap where OrgClass='" + BeanCtx.getOrgClass() + "' and Userid='" + ShortName + "'";
            Rdb.execSql(sql);

            //2.存或更新用户与主部门的关系记录
            Document mapdoc = BeanCtx.getDocumentBean("BPM_OrgUserDeptMap");
            mapdoc.s("OrgClass", BeanCtx.getOrgClass());
            mapdoc.s("Userid", ShortName);
            mapdoc.s("Folderid", FolderId);
            mapdoc.s("Deptid", BeanCtx.getLinkeyUser().getDeptidByFolderid(FolderId, BeanCtx.getOrgClass()));
            mapdoc.s("CurrentFlag", "1");
            mapdoc.s("MainDept", "1"); //标识为主部门的关系记录
            mapdoc.save();

        }
    }

}