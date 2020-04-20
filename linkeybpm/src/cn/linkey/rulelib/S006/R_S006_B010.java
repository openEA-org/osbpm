package cn.linkey.rulelib.S006;

import java.util.*;
import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.org.LinkeyUser;
import cn.linkey.util.Tools;

/**
 * @RuleName:矩阵人员配置部门树
 * @author admin
 * @version: 8.0
 * @Created: 2014-04-20 23:11
 */
final public class R_S006_B010 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) {
        //params为运行本规则时所传入的参数
        String parentid = BeanCtx.g("id");
        String orgClass = BeanCtx.g("OrgClass");
        String defaultOrgClass = BeanCtx.getSystemConfig("DefaultOrgClass"); //缺省架构不显示
        String dtype = BeanCtx.g("dtype");
        StringBuilder jsonStr = new StringBuilder();
        String async = BeanCtx.g("async");
        if (Tools.isBlank(async)) {
            async = "true";
        }
        int i = 0;
        if (Tools.isBlank(parentid)) {
            String sql = "select OrgName,OrgClass from BPM_Organization where OrgClass<>'" + defaultOrgClass + "' order by SortNum";
            Document[] dc = Rdb.getAllDocumentsBySql(sql);
            jsonStr.append("[");
            for (Document doc : dc) {
                if (i == 0) {
                    i = 1;
                }
                else {
                    jsonStr.append(",");
                }
                jsonStr.append("{\"id\":\"" + doc.g("OrgClass") + "\",\"text\":\"" + doc.g("OrgName") + "\",\"OrgClass\":\"" + doc.g("OrgClass")
                        + "\",\"state\":\"open\",\"dtype\":\"orgclass\",\"children\":");
                jsonStr.append(getDeptJson("root", doc.g("OrgClass"), async));
                jsonStr.append("}");
            }
            jsonStr.append("]");
        }
        else {
            if (dtype.equals("orgclass")) {
                parentid = "root";
            }
            jsonStr.append(getDeptJson(parentid, orgClass, async));
        }
        BeanCtx.p(jsonStr.toString());
        return "";
    }

    /**
     * 获得数据的字符串
     */
    public StringBuilder getDeptJson(String parentid, String orgClass, String async) {
        //params为运行本规则时所传入的参数
        //传false表示一次性全部加载，true表示异步加载
        StringBuilder jsonStr = new StringBuilder();
        LinkeyUser linkeyUser = (LinkeyUser) BeanCtx.getBean("LinkeyUser");
        String sql = "select * from BPM_OrgDeptList where OrgClass='" + orgClass + "' and ParentFolderid='" + parentid + "' order by SortNumber";
        //		BeanCtx.out("main sql="+sql);
        Document[] dc = Rdb.getAllDocumentsBySql(sql);
        jsonStr.append("[");
        int i = 0;
        for (Document doc : dc) {
            if (linkeyUser.inRoles(BeanCtx.getUserid(), doc.g("Roles"))) {
                //说明有权查看此菜单
                if (i == 0) {
                    i = 1;
                }
                else {
                    jsonStr.append(",");
                }
                jsonStr.append(getAllSubFolder(doc, async, orgClass));
            }
        }

        if (parentid.length() > 3 && !parentid.equals("root")) {
            //第一层不显示用户角色子菜单
            if (i == 1) {
                jsonStr.append(",");
            }
            ;
            jsonStr.append(getUserItem(orgClass, parentid));
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
        //看此文件夹是否有子文件夹
        String sql = "select * from BPM_OrgDeptList where OrgClass='" + orgClass + "' and ParentFolderid='" + doc.g("Folderid") + "' order by SortNumber";
        //	    BeanCtx.out("sql="+sql);
        Document[] dc = Rdb.getAllDocumentsBySql(sql);
        if (dc.length > 0) {
            //说明有子文件夹
            if (async.equals("false") || doc.g("Folderid").equals("001")) {
                //false表示一次性全部加载输出,进行递归调用
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
                //表示异步加载输出
                jsonStr.append(",\"state\":\"closed\"}");
            }
        }
        else {
            //没有子文件夹了
            jsonStr.append(",\"state\":\"closed\"}");
        }
        return jsonStr;
    }

    /**
     * 获得默认的人员，角色json
     * 
     * @param orgClass
     * @param folderid
     * @return
     */
    public StringBuilder getUserItem(String orgClass, String folderid) {
        StringBuilder jsonStr = new StringBuilder();
        String sql = "select count(*) as totalNum from BPM_OrgUserDeptMap where OrgClass='" + orgClass + "' and Folderid='" + folderid + "'";
        String totalNum = Rdb.getValueBySql(sql);
        jsonStr.append("{\"id\":\"" + folderid + "_user\",\"text\":\"人员(" + totalNum + ")\",\"OrgClass\":\"" + orgClass + "\",\"iconCls\":\"icon-user\",\"isLeaf\":\"true\"}");

        sql = "select count(*) as totalNum from BPM_OrgRoleMembers where RoleType='1' and OrgClass='" + orgClass + "' and Folderid='" + folderid + "'";
        totalNum = Rdb.getValueBySql(sql);
        jsonStr.append(",{\"id\":\"" + folderid + "_role\",\"text\":\"角色(" + totalNum + ")\",\"OrgClass\":\"" + orgClass + "\",\"iconCls\":\"icon-usergreen\",\"isLeaf\":\"true\"}");

        sql = "select count(*) as totalNum from BPM_OrgRoleMembers where RoleType='2' and OrgClass='" + orgClass + "' and Folderid='" + folderid + "'";
        totalNum = Rdb.getValueBySql(sql);
        jsonStr.append(",{\"id\":\"" + folderid + "_job\",\"text\":\"岗位(" + totalNum + ")\",\"OrgClass\":\"" + orgClass + "\",\"iconCls\":\"icon-userjob\",\"isLeaf\":\"true\"}");
        return jsonStr;
    }

}