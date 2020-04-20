package cn.linkey.rulelib.S023;

import java.util.*;
import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.org.LinkeyUser;
import cn.linkey.util.Tools;

/**
 * @RuleName:树型展开的用户选择树(Mobile专用)
 * @author admin
 * @version: 8.0
 * @Created: 2014-04-20 23:11
 */
final public class R_S023_B012 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) {
        //params为运行本规则时所传入的参数
        String deptid = BeanCtx.g("id"); //这里得到的Deptid而不是folderid
        StringBuilder jsonStr = new StringBuilder();
        String async = BeanCtx.g("async");
        if (Tools.isBlank(async)) {
            async = "true";
        } //true表示异步加载,false表示同步加载
        int i = 0;
        if (Tools.isBlank(deptid)) {
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
                String state = doc.g("OrgClass").equals(BeanCtx.getOrgClass()) ? "open" : "closed";

                jsonStr.append("{\"id\":\"" + doc.g("OrgClass") + "\",\"text\":\"" + doc.g("OrgName") + "\",\"OrgClass\":\"" + doc.g("OrgClass") + "\",\"state\":\"" + state + "\",\"children\":");
                jsonStr.append(getDeptJson("root", "root", doc.g("OrgClass"), async));
                jsonStr.append("}");
            }
            jsonStr.append("]");
        }
        else {
            //获得部门列表
            //此处传入的parentid为 deptid而不是folderid，所以要进行转换
            String orgClass = BeanCtx.getLinkeyUser().getOrgClassByDeptid(deptid);//根据部门编号获得架构标识
            String parentid = BeanCtx.getLinkeyUser().getFolderidByDeptid(deptid); //转换为folderid
            jsonStr.append(getDeptJson(parentid, deptid, orgClass, async));
        }
        BeanCtx.p(jsonStr.toString());
        return "";
    }

    /**
     * 获得数据的字符串
     */
    public StringBuilder getDeptJson(String parentid, String deptid, String orgClass, String async) {
        //params为运行本规则时所传入的参数
        //传false表示一次性全部加载，true表示异步加载
        StringBuilder jsonStr = new StringBuilder();
        LinkeyUser linkeyUser = (LinkeyUser) BeanCtx.getBean("LinkeyUser");

        String sql = "select * from BPM_OrgDeptList where OrgClass='" + orgClass + "' and ParentFolderid='" + parentid + "' order by SortNumber";
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
            StringBuilder userItem = getUserItem(orgClass, deptid);
            if (i == 1 && userItem.length() > 0) {
                jsonStr.append(",");
            }
            jsonStr.append(userItem);
        }
        jsonStr.append("]");
        return jsonStr;
    }

    /**
     * 获得所有子部门的json
     */
    public StringBuilder getAllSubFolder(Document doc, String async, String orgClass) {
        String folderName = doc.g("FolderName");
        StringBuilder jsonStr = new StringBuilder("{\"id\":\"" + doc.g("Deptid") + "\",\"text\":\"" + folderName + "\",\"OrgClass\":\"" + orgClass + "\"");
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
                if (doc.g("Folderid").equals("001") && orgClass.equals(BeanCtx.getOrgClass())) {
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
    public StringBuilder getUserItem(String orgClass, String deptid) {
        StringBuilder jsonStr = new StringBuilder();
        String sql = "select userid,CnName as username from BPM_OrgUserWithAllDept where Deptid='" + deptid + "'";
        Document[] dc = Rdb.getAllDocumentsBySql(sql);
        for (Document doc : dc) {
            if (jsonStr.length() > 0) {
                jsonStr.append(",");
            }
            jsonStr.append("{\"id\":\"").append(doc.g("userid")).append("\",\"text\":\"<input type='checkbox' onclick=\\\"selectUser('" + doc.g("userid") + "','" + doc.g("username") + "',this)\\\">")
                    .append(doc.g("username")).append("\",\"iconCls\":\"icon-user\",\"isLeaf\":\"true\"}");
        }

        return jsonStr;
    }

}