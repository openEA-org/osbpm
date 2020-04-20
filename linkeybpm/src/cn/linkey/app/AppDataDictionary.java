package cn.linkey.app;

import java.util.HashSet;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.util.Tools;

/**
 * @RuleName:获得数据字典的JSON
 * @author admin
 * @version: 8.0
 * @Created: 2014-04-02 11:04
 */
final public class AppDataDictionary implements AppElement {

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
    public String getElementHtml(String dataid) {
        //params为运行本规则时所传入的参数
        String sql = "select * from BPM_DataDicValueList where Dataid='" + dataid + "' order by SortNum DESC";
        StringBuilder jsonStr = new StringBuilder();
        Document[] dc = Rdb.getAllDocumentsBySql(sql);
        jsonStr.append("[");
        int i = 0, totalNum = 0;
        for (Document doc : dc) {
            if (i == 0) {
                i = 1;
            }
            else {
                jsonStr.append(",");
            }
            HashSet<String> fdList = new HashSet<String>();
            fdList.addAll(doc.getAllItemsName());
            for (String fdName : fdList) {
                if (fdName.startsWith("WF_")) {
                    doc.removeItem(fdName);
                }
            }
            doc.removeItem("Dataid");
            jsonStr.append(doc.toJson(true));
            totalNum++;
        }
        jsonStr.append("]");

        //格式化json字符串
        sql = "select FormatJson from BPM_DataDicConfig where Dataid='" + dataid + "'";
        String formatJson = Rdb.getValueBySql(sql);
        if (Tools.isNotBlank(formatJson)) {
            formatJson = formatJson.replace("#Total", String.valueOf(totalNum));
            formatJson = formatJson.replace("#JsonData", jsonStr.toString());
        }
        else {
            formatJson = jsonStr.toString();
        }
        return formatJson;
    }

}