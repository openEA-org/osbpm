package cn.linkey.rulelib.S008;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.org.LinkeyUser;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * @RuleName:navtree导航树解析器
 * @author admin
 * @version: 8.0
 * @Created: 2015-10-09 11:37
 */
final public class R_S008_B002 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        //params为运行本规则时所传入的参数
        String treeid = (String) params.get("XTagValue");//获得导航树编号
        String eleid = (String) params.get("eleid");
        if (Tools.isNotBlank(eleid)) {
            treeid = eleid;
        } //以R_S008_B003中传入的优先
        if (Tools.isBlank(treeid)) {
            return "treeid does not exist!";
        }
        String htmlCode = getElementHtml(treeid);
        //	    BeanCtx.p(htmlCode);
        return htmlCode;
    }

    /**
     * 获得数据的字符串
     */
    public String getElementHtml(String treeid) {
        //params为运行本规则时所传入的参数
        String parentFolderid = "";
        String currentFolderid = BeanCtx.g("tid", true); //用户当前选中的文件夹id
        if (Tools.isNotBlank(currentFolderid)) {
            parentFolderid = currentFolderid.substring(0, 3);
        }
        StringBuilder jsonStr = new StringBuilder();
        LinkeyUser linkeyUser = (LinkeyUser) BeanCtx.getBean("LinkeyUser");
        String sql = "select * from BPM_NavTreeNode where Treeid='" + treeid + "' and ParentFolderid='root' order by SortNum";
        Document[] dc = Rdb.getAllDocumentsBySql(sql);
        int i = 0;
        for (Document doc : dc) {
            if (linkeyUser.inRoles(BeanCtx.getUserid(), doc.g("Roles"))) {
                //说明有权查看此菜单
                jsonStr.append(getAllSubFolder(doc, true, treeid, i, currentFolderid, parentFolderid));
                i++;
            }
        }
        return jsonStr.toString();
    }

    /**
     * 
     * @param doc 菜单文档对像
     * @param istitle 是否是主菜单，主菜单要加class=title
     * @param treeid 树的id
     * @param i 是第几个菜单，默认第0个菜单打开并选中
     * @return
     */
    public String getAllSubFolder(Document doc, boolean istitle, String treeid, int i, String currentFolderid, String parentFolderid) {
        StringBuilder jsonStr = new StringBuilder();
        String totalSql = doc.g("TotalSql");
        String totalNum = "";
        if (Tools.isNotBlank(totalSql)) {
            totalSql = Rdb.formatSql(totalSql, "");
            totalNum = Rdb.getValueBySql(totalSql);
        }
        String iconCls = doc.g("iconCls").replace("class", "doc").replace("menu", "tag");
        if (Tools.isBlank(iconCls)) {
            iconCls = "icon-folder";
        }
        String folderName = doc.g("FolderName").replace("(#TotalNum)", "");
        if (folderName.startsWith("L_")) {
            folderName = BeanCtx.getLabel(folderName);//获得国际标签
        }
        if (Tools.isNotBlank(totalNum)) {
            folderName = folderName + "<span class=\"badge badge-warning\">" + totalNum + "</span>";
        }

        //是主菜单是加上title class 这样收起来后就只有图标而不会显示文字
        if (istitle) {
            folderName = "<span class=\"title\">" + folderName + "</span>";
        }

        //获得所有子菜单的文档集合对像
        String sql = "select * from BPM_NavTreeNode where Treeid='" + treeid + "' and ParentFolderid='" + doc.g("Folderid") + "' order by SortNum";
        Document[] dc = Rdb.getAllDocumentsBySql(sql);

        //如果是第一个菜单则默认选中并打开
        if (Tools.isBlank(currentFolderid) && doc.g("DefaultSelected").equals("1")) {
            jsonStr.append("<li class=\"active\">");
            folderName += "<span class=\"selected\"></span>";
        }
        else {
            if (Tools.isNotBlank(parentFolderid) && doc.g("Folderid").equals(parentFolderid)) {
                //说明选中了本主文件夹，显示为选中状态
                jsonStr.append("<li class=\"active\">");
                folderName += "<span class=\"selected\"></span><span class=\"arrow open\"></span>";
            }
            else if (Tools.isNotBlank(currentFolderid) && doc.g("Folderid").equals(currentFolderid)) {
                //说明选中了本子文件夹
                jsonStr.append("<li class=\"active\">");
                folderName += "<span class=\"selected\"></span>";
                iconCls = "icon-tag";
            }
            else if (currentFolderid.startsWith(doc.g("Folderid"))) {
                jsonStr.append("<li class=\"active\">");
                folderName += "<span class=\"selected arrow open\"></span>";
            }
            else {
                //没有选中的文件夹
                jsonStr.append("<li>");
                if (istitle && dc.length > 0) {
                    folderName += "<span class=\"arrow\"></span>"; //是主菜单而且有子菜单的情况下才增加箭头
                }
            }
        }

        //添加主菜单的li
        String url = doc.g("ItemUrl");
        if (Tools.isNotBlank(url)) {
            url += "&tid=" + doc.g("Folderid");
        }
        else {
            url = "javascript:;";
        }
        String target = "";
        if (doc.g("OpenType").equals("New")) {
            target = "target=_blank";
        }
//        jsonStr.append("<a href=\"" + url + "\" " + target + " ><i class=\"" + iconCls + "\"></i>" + folderName + "</a>");
        if(url.equals("javascript:;")){
        	jsonStr.append("<a href=\"" + url + "\" ><i class=\"" + iconCls + "\"></i>" + folderName + "</a>");
        }else if(doc.g("OpenType").equals("New")){
        	jsonStr.append("<a href=\""+url+"\" "+target+" ><i class=\"" + iconCls + "\"></i>" + folderName + "</a>");
        }else{
        	jsonStr.append("<a href=\"#"+url+"\" onclick=\"loadContentPanel('"+url+"');\" ><i class=\"" + iconCls + "\"></i>" + folderName + "</a>");
        }
        
        //看此文件夹是否有子文件夹
        if (dc.length > 0) {
            //说明有子文件夹
            LinkeyUser linkeyUser = (LinkeyUser) BeanCtx.getBean("LinkeyUser");
            jsonStr.append("<ul id='submenu_" + doc.g("Folderid") + "' class=\"sub-menu\">"); //增加子菜单的ul
            for (Document subdoc : dc) {
                if (linkeyUser.inRoles(BeanCtx.getUserid(), subdoc.g("Roles"))) {
                    jsonStr.append(getAllSubFolder(subdoc, false, treeid, 3, currentFolderid, parentFolderid));
                }
            }
            jsonStr.append("</ul>");
        }

        //主菜单的li结束
        jsonStr.append("</li>");

        return jsonStr.toString();
    }

}