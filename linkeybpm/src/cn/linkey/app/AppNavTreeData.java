package cn.linkey.app;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.org.LinkeyUser;
import cn.linkey.util.Tools;

/**
 * @RuleName:获得导航树的JSON
 * @author admin
 * @version: 8.0
 * @Created: 2014-04-02 11:04
 */
public class AppNavTreeData implements AppElement {
	
	private String UIType = "3"; //默认EasyUI 类型

    @Override
    public void run(String wf_num) {
        //params为运行本规则时所传入的参数
        BeanCtx.print(getElementHtml(wf_num));
    }

    /**
     * 获得数据体
     */
    public String getElementBody(String treeid, boolean readOnly) {
        return getElementHtml(treeid);
    }

    /**
     * 获得数据的字符串
     */
    public String getElementHtml(String treeid) {
        //params为运行本规则时所传入的参数
//        String sql = "select Async from BPM_NavTreeList where Treeid='" + treeid + "'";
//        String async = Rdb.getValueBySql(sql);
    	
    	//20180313 根据UI类型兼容layui树的数据源，"text"==>"name"
    	String sql = "select Async,UIType from BPM_NavTreeList where Treeid='" + treeid + "'";
    	Document treeDoc = Rdb.getDocumentBySql(sql);
    	UIType = treeDoc.g("UIType");
        String async = treeDoc.g("Async");
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
        
      //20180313 根据UI类型兼容layui树的数据源，"text"==>"name"
        String jsonStrs = jsonStr.toString();
        if("1".equals(UIType)) {
        	jsonStrs = jsonStrs.replaceAll("\"text\":", "\"name\":");
        	//jsonStrs = jsonStrs.replaceAll("\"ItemUrl\":", "\"href\":");
        }
        return jsonStrs;
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
        if (folderName.startsWith("L_")) {
            folderName = BeanCtx.getLabel(folderName);//获得国际标签
        }
        if (Tools.isNotBlank(totalNum)) {
            if (folderName.indexOf("#TotalNum") != -1) {
                folderName = folderName.replace("#TotalNum", totalNum);
            }
            else {
                folderName = folderName + "(<span style='color:blue'>" + totalNum + "</span>)";
            }
        }
        //	    int spos=folderName.indexOf("<");
        //	    if(spos!=-1){
        //	    	folderName=folderName.substring(0,spos);
        //	    	folderName+="<span class='badge'>"+totalNum+"</span>";
        //	    }else if(Tools.isNotBlank(totalNum)) {
        //	    	folderName+="<span class='badge'>"+totalNum+"</span>";
        //	    }
        //	    BeanCtx.out(folderName);
        
        StringBuilder jsonStr = null;
        //20180411 
        if("1".equals(UIType) && "New".equals(doc.g("OpenType"))){
        	jsonStr = new StringBuilder("{\"id\":\"" + doc.g("Folderid") + "\",\"text\":\"" + folderName + "\",\"iconCls\":\"" + iconCls + "\",\"Treeid\":\"" + doc.g("Treeid")
            + "\",\"href\":\"" + doc.g("ItemUrl") + "\",\"Itemid\":\"" + doc.g("Itemid") + "\",\"OpenType\":\"" + doc.g("OpenType") + "\",\"WF_OrUnid\":\"" + doc.g("WF_OrUnid") + "\"");

        }else{
        	jsonStr = new StringBuilder("{\"id\":\"" + doc.g("Folderid") + "\",\"text\":\"" + folderName + "\",\"iconCls\":\"" + iconCls + "\",\"Treeid\":\"" + doc.g("Treeid")
            + "\",\"ItemUrl\":\"" + doc.g("ItemUrl") + "\",\"Itemid\":\"" + doc.g("Itemid") + "\",\"OpenType\":\"" + doc.g("OpenType") + "\",\"WF_OrUnid\":\"" + doc.g("WF_OrUnid") + "\"");
        }
//        StringBuilder jsonStr = new StringBuilder("{\"id\":\"" + doc.g("Folderid") + "\",\"text\":\"" + folderName + "\",\"iconCls\":\"" + iconCls + "\",\"Treeid\":\"" + doc.g("Treeid")
//                + "\",\"ItemUrl\":\"" + doc.g("ItemUrl") + "\",\"Itemid\":\"" + doc.g("Itemid") + "\",\"OpenType\":\"" + doc.g("OpenType") + "\",\"WF_OrUnid\":\"" + doc.g("WF_OrUnid") + "\"");
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