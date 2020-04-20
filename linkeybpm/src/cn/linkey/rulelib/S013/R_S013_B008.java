package cn.linkey.rulelib.S013;

import java.util.HashMap;
import java.util.LinkedHashSet;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.doc.Documents;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * @RuleName:模版选择
 * @author admin
 * @version: 8.0
 * @Created: 2014-05-30 16:03
 */
final public class R_S013_B008 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) {

        // params为运行本规则时所传入的参数
        // 二级分类
        String Itemid = BeanCtx.g("Itemid", true);
        String sql = "";
        if (Tools.isBlank(Itemid)) {
            sql = "select FolderName as text from BPM_Template group by FolderName";
        }
        else {
            sql = "select FolderName as text from BPM_Template where Itemid='" + Itemid + "' group by FolderName";
        }
        LinkedHashSet<Document> dc = Rdb.getAllDocumentsSetBySql(sql);
        // 加子数据
        for (Document doc : dc) {
            String children = getAllSubFolder(Itemid, doc.g("text"));
            // BeanCtx.out(children);
            doc.s("children", children);
        }

        // BeanCtx.print(Documents.dc2json(dc, "")); // 输出json字符串
        Document nodoc = BeanCtx.getDocumentBean("");
        nodoc.s("text", "-无-");
        nodoc.s("id", "");
        dc.add(nodoc);

        String jsonStr = Documents.dc2json(dc, "", true);
        jsonStr = jsonStr.replaceAll("'", "\"");
        // jsonStr=jsonStr.replaceAll("\"", "\\\\\"");
        // children:[]
        jsonStr = jsonStr.replaceAll("\"\\[", "[");
        jsonStr = jsonStr.replaceAll("]\\\"", "]");
        BeanCtx.print(jsonStr);

        return "";
    }

    public String getAllSubFolder(String Itemid, String FolderName) {
        // StringBuilder jsonStr=new StringBuilder();
        String jsonStr = "";
        // String sql="select WF_OrUnid as id,Subject as text from BPM_Template where Itemid='"+Itemid+"' and FolderName='"+FolderName+"'";
        String sql = "";
        if (Tools.isBlank(Itemid)) {
            sql = "select WF_OrUnid as id, Subject as text from BPM_Template where FolderName='" + FolderName + "'";
        }
        else {
            sql = "select WF_OrUnid as id, Subject as text from BPM_Template where Itemid='" + Itemid + "' and FolderName='" + FolderName + "'";
        }
        // BeanCtx.out(sql);
        Document[] dc = Rdb.getAllDocumentsBySql(sql);
        jsonStr = Documents.dc2json(dc, "", true);
        jsonStr = jsonStr.replaceAll("\"", "'");
        return jsonStr;
    }

}