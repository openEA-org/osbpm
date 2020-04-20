package cn.linkey.rulelib.S001;

import java.util.HashMap;

import cn.linkey.app.AppUtil;
import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.doc.Documents;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.DateUtil;
import cn.linkey.util.Tools;

/**
 * @RuleName:数据库表视图事件
 * @author admin
 * @version: 8.0
 * @Created: 2014-05-29 16:08
 */
final public class R_S001_E067 implements LinkeyRule {

    @Override
    public String run(HashMap<String, Object> params) {
        Document gridDoc = (Document) params.get("GridDoc"); //grid配置文档
        Document doc = (Document) params.get("DataDoc"); //数据主文档
        String eventName = (String) params.get("EventName");//事件名称
        if (eventName.equals("onGridOpen")) {
            return onGridOpen(gridDoc);
        }
        else if (eventName.equals("onDocDelete")) {
            return onDocDelete(doc, gridDoc);
        }
        else if (eventName.equals("onDocCopy")) {
            return onDocCopy(doc, gridDoc);
        }
        else if (eventName.equals("onBtnClick")) {
            return onBtnClick(doc, gridDoc);
        }
        return "1";
    }

    public String onGridOpen(Document gridDoc) {
        //如果不返回1则表示退出本视图并输出返回的字符串
        //通过gridDoc.s("WF_SearchBar","自定义操作条上的搜索框HTML代码");

        return "1";
    }

    public String onDocDelete(Document doc, Document gridDoc) {
        //如果不返回1则表示退出本次删除操作，并弹出返回的字符串为提示消息
        //从sql中删除数据库表结构,不允许删除BPM_开头的表
        String tableName = doc.g("TableName");
        String action = BeanCtx.g("Action", true);
        if (action.equals("0")) {
            //仅删除配置，不删除实例表
            return "1";
        }
        else if (action.equals("1")) {
            //仅删除实体表，不删除配置信息
            if (tableName.toLowerCase().startsWith("bpm_")) {
                return "不允许删除系统表!";
            }
            else if (Tools.isNotBlank(tableName)) {
                String sql = "DROP TABLE " + tableName;
                int i = Rdb.execSql(sql);
                return "成功删除实体表!";
            }
        }
        else if (action.equals("2")) {

            //先导出一次数据再删除，用来备用
            String sql = "select * from " + tableName;
            Document[] dc = Rdb.getAllDocumentsBySql(sql);
            String fileName = AppUtil.getPackagePath() + tableName + "(" + DateUtil.getDateNum() + ").xml";
            Documents.dc2Xmlfile(dc, fileName, true);

            //删除表数据
            sql = "delete from " + tableName;
            int i = Rdb.execSql(sql);
            if (i < 1) {
                return "数据删除失败!";
            }
            else {
                return "数据已成功清空!";
            }
        }
        return "0";
    }

    public String onDocCopy(Document doc, Document gridDoc) {
        //如果不返回1则表示退出本次拷贝操作，并弹出返回的字符串为提示消息

        return "1";
    }

    public String onBtnClick(Document doc, Document gridDoc) {
        //返回操作完成后的提示信息
        String tableName = doc.g("TableName");
        String action = BeanCtx.g("WF_Btnid", true); //获得按扭编号
        if (action.equals("CreatTable")) {

            //看表是否已经存在
            String dataSourceid = doc.g("DataSourceid");
            if (Tools.isBlank(dataSourceid)) {
                dataSourceid = "default";
            }
            if (!Rdb.isExistTable(dataSourceid, tableName)) {
                //建一张新表
                int i = Rdb.creatTable("default", tableName, doc.g("FieldConfig"));
                return "数据库表创建成功!";
            }
            else {
                //修改表结构
                return "实体表已存在,如果要修改请先删除实体表再创建表,如果要保留数据可先导出数据再导入!";
            }

        }
        else if (action.equals("OutData")) {
            String filePath = AppUtil.getPackagePath();
            String sql = "select * from " + tableName;
            Document[] dc = Rdb.getAllDocumentsBySql(sql);
            for (Document tdoc : dc) {
                tdoc.s("WF_OrTableName", tableName);
            }
            String fileName = filePath + "initdata/" + tableName + ".xml";
            fileName = fileName.replace("\\", "/");
//            BeanCtx.out("导出"+doc.g("TableName")+"数据到->"+fileName);
            if (!Documents.dc2Xmlfile(dc, fileName, true)) {
                return "数据导出失败!";
            }
            else {
                return "initdata/" + tableName + ".xml";
            }
        }
        return "";
    }

}