package cn.linkey.rulelib.S016;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.rule.LinkeyRule;

/**
 * @RuleName:流程分类分析
 * @author admin
 * @version: 8.0
 * @Created: 2014-10-15 17:08
 */
final public class R_S016_E003 implements LinkeyRule {

    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        Document viewDoc = (Document) params.get("GridDoc"); //grid配置文档
        String eventName = (String) params.get("EventName");//事件名称
        if (eventName.equals("onViewOpen")) {
            return onViewOpen(viewDoc);
        }
        return "1";
    }

    public String onViewOpen(Document viewDoc) throws Exception {
        String sql = "select * from BPM_NavTreeNode where Treeid='T_S002_001'";
        Document[] dc = Rdb.getAllDocumentsBySql(sql);
        StringBuilder folderList = new StringBuilder();
        StringBuilder folderValue = new StringBuilder();
        folderList.append("[");
        folderValue.append("[");
        int i = 0;
        for (Document doc : dc) {
            String folderid = doc.g("Folderid");
            String num = getProcessNum(folderid);
            String folderName = doc.g("FolderName") + "(" + num + ")";
            if (i > 0) {
                folderList.append(",");
                folderValue.append(",");
            }
            folderList.append("'").append(folderName).append("'");
            folderValue.append("{value:").append(num).append(",name:'").append(folderName).append("'}");
            i++;
        }
        folderList.append("]");
        folderValue.append("]");

        viewDoc.s("FolderList", folderList.toString());
        viewDoc.s("FolderValue", folderValue.toString());

        return "1";
    }

    public String getProcessNum(String folderid) {
        String sql = "select count(*) from BPM_ModProcessList where Folderid='" + folderid + "'";
        return Rdb.getValueBySql(sql);
    }
}