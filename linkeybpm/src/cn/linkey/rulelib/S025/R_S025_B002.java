package cn.linkey.rulelib.S025;

import java.util.*;
import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.org.LinkeyUser;
import cn.linkey.util.Tools;

/**
 * @RuleName:获得导航树的JSON
 * @author admin
 * @version: 8.0
 * @Created: 2014-04-02 11:04
 */
final public class R_S025_B002 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) {
        //params为运行本规则时所传入的参数
        String parentid = BeanCtx.g("id", true);
        String appid = BeanCtx.g("WF_Appid", true);
        String treeid = BeanCtx.g("treeid", true);
        StringBuilder jsonStr = new StringBuilder();
        if (Tools.isBlank(parentid)) {
            parentid = "root";
        }
        LinkeyUser linkeyUser = (LinkeyUser) BeanCtx.getBean("LinkeyUser");
        String sql = "select * from BPM_NavTreeNode where Treeid='" + treeid + "' and ParentFolderid='" + parentid + "' order by SortNum";
        // 		BeanCtx.out(sql);
        Document[] dc = Rdb.getAllDocumentsBySql(sql);
        if (BeanCtx.g("ShowRoot").equals("0")) {
            jsonStr.append("["); //不输出根文件夹
        }
        else {
            jsonStr.append("[{\"id\":\"root\",\"text\":\"" + BeanCtx.getMsg("Designer", "NavTreeRootName") + "\",\"WF_OrUnid\":\"root\",\"children\":[");
        }
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
                jsonStr.append(getAllSubFolder(doc, appid, treeid));
            }
        }
        if (BeanCtx.g("ShowRoot").equals("0")) {
            jsonStr.append("]"); //不输出根文件夹
        }
        else {
            jsonStr.append("]}]");
        }
        // 		BeanCtx.out(jsonStr.toString());
        BeanCtx.p(jsonStr.toString());
        return "";
    }

    /**
     * 获得所有子菜单的json
     */
    public String getAllSubFolder(Document doc, String appid, String treeid) {
        String totalNum = "";
        if (!doc.g("Folderid").equals("001") && Tools.isBlank(doc.g("ItemUrl"))) {
            String totalSql = "select Count(*) as TotalNum from BPM_DevLog where Folderid='" + doc.g("Folderid") + "'";
            totalNum = Rdb.getValueBySql(totalSql);
        }
        else {
            if (Tools.isNotBlank(doc.g("TotalSql"))) {
                totalNum = Rdb.getValueBySql(doc.g("TotalSql"));
            }
        }
        String iconCls = doc.g("iconCls");
        String folderName = doc.g("FolderName");
        if (Tools.isNotBlank(totalNum)) {
            folderName = folderName + "(<font color='blue'>" + totalNum + "</font>)";
        }
        StringBuilder jsonStr = new StringBuilder("{\"id\":\"" + doc.g("Folderid") + "\",\"text\":\"" + folderName + "\",\"ItemUrl\":\"" + doc.g("ItemUrl") + "\",\"iconCls\":\"" + iconCls
                + "\",\"WF_OrUnid\":\"" + doc.g("WF_OrUnid") + "\"");
        //看此文件夹是否有子文件夹
        String sql = "select * from BPM_NavTreeNode where Treeid='" + treeid + "' and ParentFolderid='" + doc.g("Folderid") + "' order by SortNum";
        Document[] dc = Rdb.getAllDocumentsBySql(sql);
        if (dc.length > 0) {
            //说明有子文件夹
            LinkeyUser linkeyUser = (LinkeyUser) BeanCtx.getBean("LinkeyUser");
            jsonStr.append(",\"state\":\"closed\",\"children\":[");
            int i = 0;
            for (Document subdoc : dc) {
                if (linkeyUser.inRoles(BeanCtx.getUserid(), subdoc.g("Roles"))) {
                    if (i == 0) {
                        i = 1;
                    }
                    else {
                        jsonStr.append(",");
                    }
                    jsonStr.append(getAllSubFolder(subdoc, appid, treeid));
                }
            }
            jsonStr.append("]}");
        }
        else {
            //没有子文件夹了
            jsonStr.append(",\"state\":\"open\"}");
        }

        return jsonStr.toString();
    }
}