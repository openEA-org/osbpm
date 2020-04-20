package cn.linkey.rulelib.S005;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.org.LinkeyUser;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * @RuleName:获得流程分类树JSON
 * @author admin
 * @version: 8.0
 * @Created: 2014-04-02 11:04
 */
final public class R_S005_B015 implements LinkeyRule {

    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        //params为运行本规则时所传入的参数
        BeanCtx.print(getJson());
        return "";
    }

    /**
     * 获得数据的字符串
     */
    public String getJson() {
        //params为运行本规则时所传入的参数
        String treeid = "T_S002_001";
        String sql = "select Async from BPM_NavTreeList where Treeid='" + treeid + "'";
        String async = Rdb.getValueBySql(sql);
        if (async.equals("1")) {
            async = "false";
        }
        else {
            async = "true";
        } //传false表示一次性全部加载，true表示异步加载
        String parentid = BeanCtx.g("id", true);
        StringBuilder jsonStr = new StringBuilder();
        if (Tools.isBlank(parentid)) {
            parentid = "root";
        }
        LinkeyUser linkeyUser = (LinkeyUser) BeanCtx.getBean("LinkeyUser");
        sql = "select * from BPM_NavTreeNode where Treeid='" + treeid + "' and ParentFolderid='" + parentid + "' order by SortNum";
        // 		BeanCtx.out(sql);
        Document[] dc = Rdb.getAllDocumentsBySql(sql);
        //jsonStr.append("[{\"id\":\"root\",\"text\":\""+BeanCtx.getMsg("Designer","NavTreeRootName")+"\",\"WF_OrUnid\":\"root\",\"children\":[");
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
                jsonStr.append(getAllSubFolder(doc, async, treeid));
            }
        }
        //jsonStr.append("]}]");
        jsonStr.append("]");
        // 		BeanCtx.out(jsonStr.toString());
        return jsonStr.toString();
    }

    /**
     * 获得所有子菜单的json
     */
    public String getAllSubFolder(Document doc, String async, String treeid) {
        String totalSql = doc.g("TotalSql");
        String totalNum = "";
        if (Tools.isNotBlank(totalSql)) {
            totalSql = Rdb.formatSql(totalSql, "");
            totalNum = Rdb.getValueBySql(totalSql);
        }
        String iconCls = doc.g("iconCls");
        String folderName = doc.g("FolderName");
        if (Tools.isNotBlank(totalNum)) {
            if (folderName.indexOf("#TotalNum") != -1) {
                folderName = folderName.replace("#TotalNum", totalNum);
            }
            else {
                folderName = folderName + "(<span style='color:blue'>" + totalNum + "</span>)";
            }
        }
        StringBuilder jsonStr = new StringBuilder("{\"id\":\"" + doc.g("Folderid") + "\",\"text\":\"" + folderName + "\",\"iconCls\":\"" + iconCls + "\",\"Treeid\":\"" + doc.g("Treeid")
                + "\",\"ItemUrl\":\"" + doc.g("ItemUrl") + "\",\"Itemid\":\"" + doc.g("Itemid") + "\",\"OpenType\":\"" + doc.g("OpenType") + "\",\"WF_OrUnid\":\"" + doc.g("WF_OrUnid") + "\"");
        //看此文件夹是否有子文件夹
        String sql = "select * from BPM_NavTreeNode where Treeid='" + treeid + "' and ParentFolderid='" + doc.g("Folderid") + "' order by SortNum";
        Document[] dc = Rdb.getAllDocumentsBySql(sql);
        if (dc.length > 0) {
            //说明有子文件夹
            if (async.equals("false")) {
                //false表示一次性全部加载输出,进行递归调用
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
                        jsonStr.append(getAllSubFolder(subdoc, async, treeid));
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
            jsonStr.append(",\"state\":\"open\"}");
        }

        return jsonStr.toString();
    }
}