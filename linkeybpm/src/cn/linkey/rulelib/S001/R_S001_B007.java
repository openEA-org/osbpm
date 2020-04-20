package cn.linkey.rulelib.S001;

import java.util.HashMap;
import java.util.LinkedHashSet;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.doc.Documents;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * 获得所有应用列表
 * 
 * @author Administrator 访问编号:R_S001_B007
 */
public class R_S001_B007 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) {

        boolean isApp = false; //是否是显示的应用
        String parentid = BeanCtx.g("id", true);
        String jsonStr = "", sql = "", appid = "";
        Document[] dc;
        if (Tools.isBlank(parentid)) { // 读取第一层的应用列表
            isApp = true;
            parentid = "root";
            sql = "select AppName as text,WF_Appid as id,WF_OrUnid,Icon as iconCls,Owner from BPM_AppList where ParentFolderid='" + parentid + "' and " + Rdb.getInReaderSql("Owner")
                    + " order by SortNum";
            LinkedHashSet<Document> dcSet = Rdb.getAllDocumentsSetBySql(sql);
            for (Document doc : dcSet) {
                doc.s("state", "closed");
                doc.s("isLeaf", "false");
                doc.s("text", doc.g("text") + "(" + doc.g("id") + ")");
                doc.s("id", "APP_" + doc.g("id"));
            }
            jsonStr = "[{\"id\":\"root\",\"text\":\"应用列表\",\"children\":" + Documents.dc2json(dcSet, "") + "}]";
        }
        else { // 读取第二层的插件列表
            if (parentid.indexOf("APP_") != -1) {
                appid = parentid.substring(4);
                //判断应用有没有子应用
                sql = "select AppName as text,WF_Appid as id,WF_OrUnid,Icon as iconCls   from BPM_AppList where ParentFolderid='" + appid + "' and " + Rdb.getInReaderSql("Owner")
                        + " order by WF_Appid";
                //BeanCtx.out("sql="+sql);
                if (!Rdb.hasRecord(sql)) {
                    //没有子应用读取第二级菜单
                    sql = "select PluginName as text,Folderid as id,url,iconCls,TotalNumSql,Folderid from BPM_AppPluginConfig where ParentFolderid='root' and Status='1'  order by SortNum";
                }
                else {
                    isApp = true;
                }
            }
            else {
                //读取第三层的菜单
                int spos = parentid.indexOf("_");
                appid = parentid.substring(0, spos);
                parentid = parentid.substring(spos + 1);
                sql = "select PluginName as text,Folderid as id,url,iconCls,TotalNumSql,Folderid from BPM_AppPluginConfig where ParentFolderid='" + parentid + "' and Status='1'  order by SortNum";
            }

            //输出json字符串
            dc = Rdb.getAllDocumentsBySql(sql);
            for (Document doc : dc) {
                //判断还有没有子文件夹
                sql = "select WF_OrUnid from BPM_AppPluginConfig where ParentFolderid='" + doc.g("Folderid") + "' and Status='1'";
                if (Rdb.hasRecord(sql) || !doc.hasItem("url")) {
                    doc.s("state", "closed"); // 还有子文件夹
                    doc.s("isLeaf", "false");
                }
                else {
                    doc.s("state", "open"); // 没有子文件夹
                    doc.s("isLeaf", "true");
                }

                //如果是应用时需要修改一下text和id
                if (isApp) {
                    doc.s("text", doc.g("text") + "(" + doc.g("id") + ")");
                    doc.s("id", "APP_" + doc.g("id"));
                }
                else {
                    //如果是插件则需要加为应用编号
                    doc.s("id", appid + "_" + doc.g("id"));
                }
                // 计算设计元素总数
                String totalNumSql = doc.g("TotalNumSql");
                if (Tools.isNotBlank(totalNumSql)) {
                    totalNumSql = totalNumSql.replace("{appid}", appid);
                    String totalNum = Rdb.getValueBySql(totalNumSql);
                    doc.s("text", doc.g("text") + "(" + totalNum + ")");
                }
                doc.s("url", doc.g("url").replace("{appid}", appid)); // 替换url中的appid参数
                doc.removeItem("TotalNumSql"); // 删除不需要输出的字段
                doc.removeItem("Folderid"); // 删除不需要输出的字段
            }
            jsonStr = Documents.dc2json(dc, "", true);
        }
        if (Rdb.getDbType().equals("ORACLE")) {
            jsonStr = jsonStr.replace("\"TEXT\"", "\"text\"");
            jsonStr = jsonStr.replace("\"ID\"", "\"id\"");
            jsonStr = jsonStr.replace("\"ICONCLS\"", "\"iconCls\"");
            jsonStr = jsonStr.replace("\"URL\"", "\"url\"");
            jsonStr = jsonStr.replace("\"WF_ORUNID\"", "\"WF_OrUnid\"");
        }
        BeanCtx.print(jsonStr); // 输出json字符串

        return "";
    }
}
