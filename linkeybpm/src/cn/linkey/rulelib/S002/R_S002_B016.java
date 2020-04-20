package cn.linkey.rulelib.S002;

import java.util.*;
import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * @RuleName:流程选择树
 * @author admin
 * @version: 8.0
 * @Created: 2014-04-20 23:11
 */
final public class R_S002_B016 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) {
        //params为运行本规则时所传入的参数
        String treeid = "T_S002_001";
        String parentid = BeanCtx.g("id");
        String async = BeanCtx.g("async");
        if (Tools.isBlank(async)) {
            async = "false";
        }
        if (Tools.isBlank(parentid)) {
            parentid = "root";
        }
        String jsonStr = getFolderJson(parentid, async, treeid);
        //		 BeanCtx.out(jsonStr.toString());
        BeanCtx.p(jsonStr.toString());
        return "";
    }

    /**
     * 获得数据的字符串
     */
    public String getFolderJson(String parentid, String async, String treeid) {
        //params为运行本规则时所传入的参数
        //传false表示一次性全部加载，true表示异步加载
        StringBuilder jsonStr = new StringBuilder();
        String sql = "select * from BPM_NavTreeNode where Treeid='" + treeid + "' and ParentFolderid='" + parentid + "' order by SortNum";
        Document[] dc = Rdb.getAllDocumentsBySql(sql);
        for (Document doc : dc) {
            if (jsonStr.length() > 0) {
                jsonStr.append(",");
            }
            jsonStr.append(getAllSubFolder(doc, async, treeid));
        }
        if (jsonStr.length() > 0) {
            jsonStr.append(",{\"id\":\"\",\"text\":\"-无-\"}]");
        }
        jsonStr.insert(0, "[");

        return jsonStr.toString();
    }

    /**
     * 获得所有子分类的json
     */
    public StringBuilder getAllSubFolder(Document doc, String async, String treeid) {
        String folderName = doc.g("FolderName");
        folderName += "(" + getProcessNum(doc.g("Folderid")) + ")";
        StringBuilder jsonStr = new StringBuilder("{\"id\":\"" + doc.g("Folderid") + "\",\"text\":\"" + folderName + "\"");
        //看此文件夹是否有子文件夹
        String sql = "select * from BPM_NavTreeNode where Treeid='" + treeid + "' and ParentFolderid='" + doc.g("Folderid") + "' order by SortNum";
        //BeanCtx.out("sql="+sql);
        Document[] dc = Rdb.getAllDocumentsBySql(sql);
        if (dc.length > 0) {
            //说明有子文件夹
            if (async.equals("false") || doc.g("Folderid").equals("001")) {
                //false表示一次性全部加载输出,进行递归调用
                String state = "closed";
                if (doc.g("Folderid").equals("001")) {
                    state = "open";
                }
                jsonStr.append(",\"state\":\"" + state + "\",\"children\":[");
                int i = 0;
                for (Document subdoc : dc) {
                    if (i == 0) {
                        i = 1;
                    }
                    else {
                        jsonStr.append(",");
                    }
                    jsonStr.append(getAllSubFolder(subdoc, async, treeid));
                }

                String roleList = getAllDocList(doc.g("Folderid"));
                if (Tools.isNotBlank(roleList)) {
                    jsonStr.append("," + roleList); //增加角色成员
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

            //增加角色成员
            jsonStr.append(",\"children\":[");
            jsonStr.append(getAllDocList(doc.g("Folderid")));
            jsonStr.append("]");
            //增加结束

            jsonStr.append(",\"state\":\"closed\"}");
        }

        return jsonStr;
    }

    /**
     * 获得分类下有几个流程数
     * 
     * @return
     */
    public String getProcessNum(String folderid) {
        String sql = "select Count(*) as TotalNum from BPM_ModProcessList where Folderid like '" + folderid + "%'";
        return Rdb.getValueBySql(sql);
    }

    /**
     * 获得分类下的所有流程
     * 
     * @param folderid分类编号
     * @return
     */
    public String getAllDocList(String folderid) {
        StringBuilder jsonStr = new StringBuilder();
        String sql = "select * from BPM_ModProcessList where Folderid='" + folderid + "'";
        Document[] dc = Rdb.getAllDocumentsBySql(sql);
        int i = 0;
        for (Document doc : dc) {
            if (i == 0) {
                i = 1;
            }
            else {
                jsonStr.append(",");
            }
            jsonStr.append("{\"text\":\"" + doc.g("NodeName") + "\",\"id\":\"" + doc.g("Processid") + "\",\"iconCls\":\"icon-flow\",\"state\":\"open\"}");
        }
        return jsonStr.toString();
    }

}