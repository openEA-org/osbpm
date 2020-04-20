package cn.linkey.rulelib.S007;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.org.LinkeyUser;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * @RuleName:人员选择左则部门树
 * @author admin
 * @version: 8.0
 * @Created: 2014-04-20 23:11
 */
final public class R_S007_B001 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) {
        // params为运行本规则时所传入的参数
        String parentid = BeanCtx.g("id", true);
        String orgClass = BeanCtx.g("OrgClass", true);
        String defaultOrgClass = BeanCtx.getSystemConfig("DefaultOrgClass");
        String dtype = BeanCtx.g("dtype", true);
        StringBuilder jsonStr = new StringBuilder();
        String async = BeanCtx.g("async", true);
        if (Tools.isBlank(async)) {
            async = "true";
        }
        // BeanCtx.out("parentid="+parentid);
        int i = 0;
        if (Tools.isBlank(parentid)) {
            String sql = "select OrgName,OrgClass from BPM_Organization order by SortNum";
            Document[] dc = Rdb.getAllDocumentsBySql(sql);
            jsonStr.append("[");
            for (Document doc : dc) {
                if (i == 0) {
                    i = 1;
                }
                else {
                    jsonStr.append(",");
                }
                String state = "closed";
                if (doc.g("OrgClass").equals(defaultOrgClass)) {
                    state = "open";
                }
                jsonStr.append("{\"id\":\"" + doc.g("OrgClass") + "\",\"text\":\"" + doc.g("OrgName") + "\",\"OrgClass\":\"" + doc.g("OrgClass") + "\",\"state\":\"" + state
                        + "\",\"dtype\":\"orgclass\",\"children\":");
                jsonStr.append(getDeptJson("root", doc.g("OrgClass"), async));
                jsonStr.append("}");
            }
            jsonStr.append(getRolesTreeRoot()); // 获得角色和岗位树
            jsonStr.append("]");
        }
        else {
            if (dtype.equals("Role")) {
                // BeanCtx.out("parentid="+parentid);
                jsonStr = getSubRolesTree(parentid);// 显示角色的子节点
            }
            else {
                if (dtype.equals("orgclass")) {
                    parentid = "root";
                }
                jsonStr = getDeptJson(parentid, orgClass, async); // 显示部门的子节点
            }
        }
        BeanCtx.p(jsonStr.toString());
        return "";
    }

    /**
     * 获得角色和岗位选择树
     * 
     * @param parentid格式为Treeid#Parentid
     */
    private StringBuilder getRolesTreeRoot() {
        StringBuilder jsonStr = new StringBuilder();
        String sql = "select * from BPM_OrgRoleTree where ParentFolderid='root' order by SortNum";
        Document[] dc = Rdb.getAllDocumentsBySql(sql);
        for (Document doc : dc) {
            jsonStr.append(",");
            String id = doc.g("Treeid") + "#" + doc.g("Folderid");
            jsonStr.append("{\"id\":\"" + id + "\",\"text\":\"" + doc.g("FolderName") + "\",\"state\":\"closed\",\"dtype\":\"Role\"}");
        }
        return jsonStr;
    }

    /**
     * 获得角色和岗位选择树
     * 
     * @param parentid格式为Treeid#Parentid
     */
    private StringBuilder getSubRolesTree(String parentid) {
        String treeid = "";
        String[] tmpArray = Tools.split(parentid, "#");
        treeid = tmpArray[0];
        parentid = tmpArray[1];
        StringBuilder jsonStr = new StringBuilder();
        String sql = "select * from BPM_OrgRoleTree where Treeid='" + treeid + "' and ParentFolderid='" + parentid + "' order by SortNum";
        Document[] dc = Rdb.getAllDocumentsBySql(sql);
        for (Document doc : dc) {
            if (jsonStr.length() > 0) {
                jsonStr.append(",");
            }
            String id = doc.g("Treeid") + "#" + doc.g("Folderid");
            jsonStr.append("{\"id\":\"" + id + "\",\"text\":\"" + doc.g("FolderName") + "\",\"state\":\"closed\",\"dtype\":\"Role\"}");
        }
        jsonStr.insert(0, "[");

        // 获得本角色分类下的所有角色列表
        jsonStr.append(getRolesItem(parentid, treeid));
        jsonStr.append("]");
        return jsonStr;
    }

    /**
     * 获得角色分类下的所有角色列表
     * 
     * @param RoleFolderid 角色编号
     * @return
     */
    private String getRolesItem(String roleFolderid, String roleType) {
        StringBuilder jsonStr = new StringBuilder();
        String sql = "select * from BPM_OrgRoleList where RoleFolderid='" + roleFolderid + "' and RoleType='" + roleType + "'";
        Document[] dc = Rdb.getAllDocumentsBySql(sql);
        for (Document doc : dc) {
            if (jsonStr.length() > 0) {
                jsonStr.append(",");
            }
            jsonStr.append("{\"text\":\"" + doc.g("RoleName") + "\",\"id\":\"" + doc.g("RoleNumber") + "\",\"iconCls\":\"icon-usergreen\",\"dtype\":\"Role\",\"state\":\"open\"}");
        }
        return jsonStr.toString();
    }

    /**
     * 获得部门的Json
     */
    public StringBuilder getDeptJson(String parentid, String orgClass, String async) {
        // params为运行本规则时所传入的参数
        // 传false表示一次性全部加载，true表示异步加载
        StringBuilder jsonStr = new StringBuilder();
        String sql = "select * from BPM_OrgDeptList where OrgClass='" + orgClass + "' and ParentFolderid='" + parentid + "' order by SortNumber";
        Document[] dc = Rdb.getAllDocumentsBySql(sql);
        jsonStr.append("[");
        int i = 0;
        for (Document doc : dc) {
            if (i == 0) {
                i = 1;
            }
            else {
                jsonStr.append(",");
            }
            jsonStr.append(getAllSubFolder(doc, async, orgClass));
        }
        jsonStr.append("]");
        return jsonStr;
    }

    /**
     * 获得所有子部门的json
     */
    public StringBuilder getAllSubFolder(Document doc, String async, String orgClass) {
        String folderName = doc.g("FolderName");
        StringBuilder jsonStr = new StringBuilder("{\"id\":\"" + doc.g("Folderid") + "\",\"text\":\"" + folderName + "\",\"OrgClass\":\"" + orgClass + "\"");
        // 看此文件夹是否有子文件夹
        String sql = "select * from BPM_OrgDeptList where OrgClass='" + orgClass + "' and ParentFolderid='" + doc.g("Folderid") + "' order by SortNumber";
        // BeanCtx.out("sql="+sql);
        Document[] dc = Rdb.getAllDocumentsBySql(sql);
        if (dc.length > 0) {
            // 说明有子文件夹
            if (async.equals("false") || doc.g("Folderid").equals("001")) {
                // false表示一次性全部加载输出,进行递归调用
                LinkeyUser linkeyUser = (LinkeyUser) BeanCtx.getBean("LinkeyUser");
                String state = "closed";
                if (doc.g("Folderid").equals("001")) {
                    state = "open";
                }
                jsonStr.append(",\"state\":\"" + state + "\",\"children\":[");
                int i = 0;
                for (Document subdoc : dc) {
                    if (linkeyUser.inRoles(BeanCtx.getUserid(), subdoc.g("Roles"))) {
                        if (i == 0) {
                            i = 1;
                        }
                        else {
                            jsonStr.append(",");
                        }
                        jsonStr.append(getAllSubFolder(subdoc, async, orgClass));
                    }
                }
                jsonStr.append("]}");
            }
            else {
                // 表示异步加载输出
                jsonStr.append(",\"state\":\"closed\"}");
            }
        }
        else {
            // 没有子文件夹了
            jsonStr.append(",\"state\":\"open\"}");
        }

        return jsonStr;
    }

}