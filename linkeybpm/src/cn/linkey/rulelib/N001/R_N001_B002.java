package cn.linkey.rulelib.N001;

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
final public class R_N001_B002 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        //params为运行本规则时所传入的参数
        String treeid = (String) params.get("XTagValue");//获得导航树编号
        //		treeid="T_S001_001";
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
        String parentid = BeanCtx.g("id", true);
        StringBuilder jsonStr = new StringBuilder();
        if (Tools.isBlank(parentid)) {
            parentid = "root";
        }
        LinkeyUser linkeyUser = (LinkeyUser) BeanCtx.getBean("LinkeyUser");
        String sql = "select * from BPM_NavTreeNode where Treeid='" + treeid + "' and ParentFolderid='" + parentid + "' order by SortNum";
        Document[] dc = Rdb.getAllDocumentsBySql(sql);
        int i = 0;
        for (Document doc : dc) {
            if (linkeyUser.inRoles(BeanCtx.getUserid(), doc.g("Roles"))) {
                //说明有权查看此菜单
                jsonStr.append(getAllSubFolder(doc, true, treeid, i));
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
    public String getAllSubFolder(Document doc, boolean istitle, String treeid, int i) {
        StringBuilder jsonStr = new StringBuilder();
        String totalSql = doc.g("TotalSql");
        String totalNum = "";
        if (Tools.isNotBlank(totalSql)) {
            totalSql = Rdb.formatSql(totalSql, "");
            totalNum = Rdb.getValueBySql(totalSql);
        }
        String iconCls = doc.g("iconCls").replace("class", "doc").replace("menu", "tag");
        if (Tools.isBlank(iconCls)) {
            iconCls = "icon-paper-plane";
        }
        String folderName = doc.g("FolderName");
        if (folderName.startsWith("L_")) {
            folderName = BeanCtx.getLabel(folderName);//获得国际标签
        }
        folderName = folderName.replace("(#TotalNum)", "");
        if (Tools.isNotBlank(totalNum)) {
            folderName = folderName + "<span class=\"badge badge-roundless badge-warning\">" + totalNum + "</span>";
        }

        //是主菜单是加上title class 这样收起来后就只有图标而不会显示文字
        if (istitle) {
            folderName = "<span class=\"title\">" + folderName + "</span>";
        }

        //如果是第一个菜单则默认选中并打开
        if (i == 0 && istitle) {
            jsonStr.append("<li class=\"active open\">");
            folderName += "<span class=\"selected\"></span><span class=\"arrow open\"></span>";
        }
        else {
            jsonStr.append("<li>");
        }

        //	    StringBuilder jsonStr=new StringBuilder("{\"id\":\""+doc.g("Folderid")+"\",\"text\":\""+folderName+"\",\"iconCls\":\""+iconCls+"\",\"Treeid\":\""+doc.g("Treeid")+"\",\"ItemUrl\":\""+doc.g("ItemUrl")+"\",\"Itemid\":\""+doc.g("Itemid")+"\",\"OpenType\":\""+doc.g("OpenType")+"\",\"WF_OrUnid\":\""+doc.g("WF_OrUnid")+"\"");

        //添加主菜单的li
        String url = doc.g("ItemUrl");
        url += "&v=&tid=" + doc.g("Folderid");
        jsonStr.append("<a href=\"" + url + "\"><i class=\"" + iconCls + "\"></i>" + folderName + "</a>");

        //看此文件夹是否有子文件夹
        String sql = "select * from BPM_NavTreeNode where Treeid='" + treeid + "' and ParentFolderid='" + doc.g("Folderid") + "' order by SortNum";
        Document[] dc = Rdb.getAllDocumentsBySql(sql);
        if (dc.length > 0) {
            //说明有子文件夹
            LinkeyUser linkeyUser = (LinkeyUser) BeanCtx.getBean("LinkeyUser");
            jsonStr.append("<ul class=\"sub-menu\">"); //增加子菜单的ul
            for (Document subdoc : dc) {
                if (linkeyUser.inRoles(BeanCtx.getUserid(), subdoc.g("Roles"))) {
                    jsonStr.append(getAllSubFolder(subdoc, false, treeid, 3));
                }
            }
            jsonStr.append("</ul>");
        }

        //主菜单的li结束
        jsonStr.append("</li>");

        return jsonStr.toString();
    }

}