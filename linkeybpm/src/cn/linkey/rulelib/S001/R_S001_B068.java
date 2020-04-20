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
 * @RuleName:设计元素列表JSON树(选择用)
 * @author admin
 * @version: 8.0
 * @Created: 2014-08-12 21:21
 */
public class R_S001_B068 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) {
        //		 BeanCtx.out(BeanCtx.getRequest().getParameterMap().toString());
        //		 BeanCtx.out("id="+BeanCtx.g("id"));

        String appid = BeanCtx.g("Appid");
        //		BeanCtx.out("appid="+appid);

        String parentid = BeanCtx.g("id", true);
        String folderType = BeanCtx.g("FolderType", true);
        String jsonStr = "", sql = "";
        Document[] dc;
        //		BeanCtx.out("folderType="+folderType);
        if (Tools.isBlank(parentid) || folderType.equals("APP")) {
            //说明是应用分类
            if (Tools.isBlank(parentid)) {
                parentid = "root";
            }
            sql = "select AppName as text,WF_Appid as id,WF_OrUnid,Icon as iconCls,Owner from BPM_AppList where ParentFolderid='" + parentid + "' order by SortNum";
            LinkedHashSet<Document> dcSet = Rdb.getAllDocumentsSetBySql(sql);
            if (dcSet.size() > 0) {
                //说明应用还有子应用
                for (Document doc : dcSet) {
                    doc.s("state", "closed");
                    doc.s("isLeaf", "false");
                    doc.s("FolderType", "APP");
                    doc.s("text", doc.g("text") + "(" + doc.g("id") + ")");
                    doc.s("Appid", doc.g("id"));
                }
                jsonStr = Documents.dc2json(dcSet, "");
                if (parentid.equals("root")) {
                    jsonStr = "[{\"id\":\"root\",\"text\":\"所有设计列表\",\"children\":" + jsonStr + "}]";
                }
            }
            else {
                //说明应用没有子应用了，读取所有根root插件列表
                sql = "select PluginName as text,Folderid as id,url,iconCls,TotalNumSql,Folderid from BPM_AppPluginConfig where ParentFolderid='root' and SelectFlag='1' and Status='1'  order by SortNum";
                //输出json字符串
                dc = Rdb.getAllDocumentsBySql(sql);
                for (Document doc : dc) {
                    doc.s("state", "closed"); // 还有子文件夹
                    doc.s("isLeaf", "false");
                    // 计算设计元素总数
                    String totalNumSql = doc.g("TotalNumSql");
                    if (Tools.isNotBlank(totalNumSql)) {
                        totalNumSql = totalNumSql.replace("{appid}", appid);
                        String totalNum = Rdb.getValueBySql(totalNumSql);
                        doc.s("text", doc.g("text") + "(" + totalNum + ")");
                    }
                    doc.s("Appid", appid);
                    doc.s("FolderType", "PLUGIN");
                    doc.removeItem("TotalNumSql"); // 删除不需要输出的字段
                    doc.removeItem("Folderid"); // 删除不需要输出的字段
                }
                jsonStr = Documents.dc2json(dc, "");
            }
        }
        else if (folderType.equals("PLUGIN")) {
            //说明是点击展开开发插件,需要看是否还有子插件
            sql = "select WF_OrUnid from BPM_AppPluginConfig where ParentFolderid='" + parentid + "'";
            if (Rdb.hasRecord(sql)) {
                //说明还有子插件
                sql = "select PluginName as text,Folderid as id,url,iconCls,TotalNumSql,Folderid from BPM_AppPluginConfig where ParentFolderid='" + parentid
                        + "' and SelectFlag='1' and Status='1'  order by SortNum";
                //输出json字符串
                dc = Rdb.getAllDocumentsBySql(sql);
                for (Document doc : dc) {
                    doc.s("state", "closed"); // 还有子文件夹
                    doc.s("isLeaf", "false");
                    // 计算设计元素总数
                    String totalNumSql = doc.g("TotalNumSql");
                    if (Tools.isNotBlank(totalNumSql)) {
                        totalNumSql = totalNumSql.replace("{appid}", appid);
                        String totalNum = Rdb.getValueBySql(totalNumSql);
                        doc.s("text", doc.g("text") + "(" + totalNum + ")");
                    }
                    doc.s("Appid", appid);
                    doc.s("FolderType", "PLUGIN");
                    doc.removeItem("TotalNumSql"); // 删除不需要输出的字段
                    doc.removeItem("Folderid"); // 删除不需要输出的字段
                }
                jsonStr = Documents.dc2json(dc, "");
            }
            else {
                //没有子插件了，读取设计元素列表
                //				BeanCtx.setDebug();
                sql = "select AllElementSQL from BPM_AppPluginConfig where Folderid='" + parentid + "'";
                sql = Rdb.getValueBySql(sql);
                if (Tools.isNotBlank(sql)) {
                    sql = sql.replace("{appid}", appid);
                    dc = Rdb.getAllDocumentsBySql(sql);
                    for (Document doc : dc) {
                        doc.s("text", doc.g("text") + "(" + doc.g("id") + ")");
                        doc.s("FolderType", "ELE"); //标识为设计元素
                    }
                    jsonStr = Documents.dc2json(dc, "");
                }
            }

        }
        //BeanCtx.out(jsonStr);
        if (Rdb.getDbType().equals("ORACLE")) {
            jsonStr = jsonStr.replace("\"TEXT\"", "\"text\"");
            jsonStr = jsonStr.replace("\"ID\"", "\"id\"");
            jsonStr = jsonStr.replace("\"ICONCLS\"", "\"iconCls\"");
            jsonStr = jsonStr.replace("\"URL\"", "\"url\"");
            jsonStr = jsonStr.replace("\"WF_ORUNID\"", "\"WF_OrUnid\"");
        }
        BeanCtx.p(jsonStr);

        return "";
    }
}