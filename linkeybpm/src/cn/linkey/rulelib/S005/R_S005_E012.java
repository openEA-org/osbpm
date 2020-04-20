package cn.linkey.rulelib.S005;

import java.util.HashMap;
import java.util.LinkedHashSet;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.linkey.app.AppUtil;
import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.doc.Documents;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * @RuleName:从Excel导入到Grid
 * @author admin
 * @version: 8.0
 * @Created: 2014-05-09 21:21
 */
final public class R_S005_E012 implements LinkeyRule {

    @Override
    public String run(HashMap<String, Object> params) {
        //获取事件运行参数
        Document formDoc = (Document) params.get("FormDoc"); //表单配置文档
        Document doc = (Document) params.get("DataDoc"); //数据主文档
        String eventName = (String) params.get("EventName");//事件名称
        if (eventName.equals("onFormOpen")) {
            String readOnly = (String) params.get("ReadOnly"); //1表示只读，0表示编辑
            return onFormOpen(doc, formDoc, readOnly);
        }
        else if (eventName.equals("onFormSave")) {
            return onFormSave(doc, formDoc);
        }
        return "1";
    }

    public String onFormOpen(Document doc, Document formDoc, String readOnly) {
        //当表单打开时
        if (readOnly.equals("1")) {
            return "1";
        } //如果是阅读状态则可不执行
        if (doc.isNewDoc()) {
            //可以对表单字段进行初始化如:doc.s("fdname",fdvalue),可以获取字段值 doc.g("fdname")

        }
        return "1"; //成功必须返回1，否则表示退出并显示返回的字符串
    }

    public String onFormSave(Document doc, Document formDoc) {
        //当表单存盘前
        BeanCtx.setCtxData("WF_NoEncode", "1"); // 指定存盘时不进行编码操作
        LinkedHashSet<String> fileList = doc.getAttachmentsNameAndPath();
        //BeanCtx.out(fileList.toString());
        if (fileList.size() == 0) {
            return "Please upload a excel file!";
        }
        String mainDocUnid = BeanCtx.g("MainDocUnid", true); //主文档的docunid如果表中建了增加，如果没有建则不给此字段
        //BeanCtx.out("mainDocUnid="+mainDocUnid);
        String filePath = BeanCtx.getAppPath();
        int i = 0;
        for (String fileName : fileList) {
            String fullFilePath = filePath + fileName; //获得excel的文件名
            //获得grid的列名称
            String gridNum = doc.g("GridNum");
            Document gridDoc = AppUtil.getDocByid("BPM_GridList", "GridNum", gridNum, true);
            String sql = "select SqlTableName from BPM_DataSourceList where Dataid='" + gridDoc.g("DataSource") + "' and Status='1'";
            String sqlTableName = Rdb.getValueBySql(sql);
            String columnConfig = gridDoc.g("ColumnConfig");
            int spos = columnConfig.indexOf("[");
            columnConfig = columnConfig.substring(spos, columnConfig.lastIndexOf("]") + 1);
            JSONArray jsonArr = JSON.parseArray(columnConfig);
            StringBuilder colStr = new StringBuilder();
            for (int x = 0; x < jsonArr.size(); x++) {
                JSONObject colConfigItem = (JSONObject) jsonArr.get(x);
                String fdName = colConfigItem.getString("FdName");
                if (colStr.length() > 0) {
                    colStr.append(",");
                }
                colStr.append(fdName);
            }
            LinkedHashSet<Document> dc = Documents.excel2dc(fullFilePath, colStr.toString(), sqlTableName);
            for (Document rowdoc : dc) {
                if (Tools.isNotBlank(mainDocUnid)) {
                    rowdoc.s("MainDocUnid", mainDocUnid); //主文档的UNID
                }
                int s = rowdoc.save();
                if (s > 0) {
                    i++;
                }
            }
            //BeanCtx.out("dc="+Documents.dc2json(dc, "rows"));
        }
        //BeanCtx.out("删除所有上传的附件...");
        doc.removeAllAttachments(true); //删除上传的临时文件

        return "成功导入(" + i + ")条记录!";
    }

}