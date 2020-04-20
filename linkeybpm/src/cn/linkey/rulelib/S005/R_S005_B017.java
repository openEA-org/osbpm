package cn.linkey.rulelib.S005;

import java.util.HashMap;
import java.util.LinkedHashSet;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.doc.Documents;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * @RuleName:直接输出我的待办的JSON
 * @author admin
 * @version: 8.0
 * @Created: 2015-01-20 23:17
 */
final public class R_S005_B017 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        //params为运行本规则时所传入的参数
        final String tableName = "BPM_MainData";
        final String fieldList = "COUNT (1) OVER () AS WF_TOTALROWS,Subject,row_number() over( order by WF_DocCreated DESC) as RowNumber,WF_AddName_CN,WF_Author_CN,WF_CurrentNodeName,WF_DocCreated,WF_DocNumber,WF_ProcessName,WF_Processid,WF_OrUnid";

        String remoteSortDir = BeanCtx.g("order", true); // 运程排序方向
        String remoteSortFdName = BeanCtx.g("sort", true); // 远程排序字段
        String searchStr = BeanCtx.g("searchStr", true); //搜索字符串
        int pageSize = 25; // 每页显示数
        String pageNum = BeanCtx.g("page", true); // 当前页
        if (Tools.isBlank(pageNum)) {
            pageNum = "1";
        }

        //待办选择条件
        String sqlWhere = " where ','+WF_Author+',' like '," + BeanCtx.getUserid() + ",' and WF_Status='Current'";

        // 附加搜索条件
        if (Tools.isNotBlank(searchStr)) {
            sqlWhere += " and Subject+WF_DocNumber+WF_AddName+WF_Author+WF_CurrentNodeName like '%" + searchStr + "%'";
        }

        //增加排序字段
        String orderStr = "";
        if (Tools.isNotBlank(remoteSortFdName)) {
            // 远程排序字段和排序方式
            String orderFieldName = remoteSortFdName;
            String sqlDirection = remoteSortDir;
            // 组合排序方式
            if (Tools.isNotBlank(orderFieldName)) {
                if (orderFieldName.indexOf(",") == -1) { // 仅有一个字段排序
                    orderStr = " order by " + orderFieldName + " " + sqlDirection;
                }
                else { // 同时有多个字段排序
                    String[] fdArray = Tools.split(orderFieldName);
                    String[] dirArray = Tools.split(sqlDirection);
                    int i = 0;
                    for (String fdName : fdArray) {
                        String dire = "";
                        if (dirArray.length > i) {
                            dire = dirArray[i];
                        }
                        if (Tools.isBlank(orderStr)) {
                            orderStr = " order by " + fdName + " " + dire;
                        }
                        else {
                            orderStr += "," + fdName + " " + dire;
                        }
                        i++;
                    }
                }
                sqlWhere += orderStr;
            }
        }

        // 获得文档总数
        long ts = System.currentTimeMillis();
        String totalNum = "0";
        //String sql = "select count(1) as TotalNum from  "+tableName + sqlWhere;
        //String totalNum = Rdb.getValueBySql(sql);	

        //组合翻页的sql语句
        int beginRow = (Integer.valueOf(pageNum) - 1) * pageSize + 1;
        int endRow = Integer.valueOf(pageNum) * pageSize;
        String sql = "select * from (select " + fieldList + " from " + tableName + " as t  " + sqlWhere + ") a where RowNumber between " + beginRow + " and " + endRow;

        //获得本页的数据记录
        LinkedHashSet<Document> dc = Rdb.getAllDocumentsSetBySql(sql);
        for (Document doc : dc) {
            if (Tools.isNotBlank(doc.g("WF_TOTALROWS"))) {
                totalNum = doc.g("WF_TOTALROWS");
                break;
            }
        }

        //输出json
        StringBuilder jsonOutStr = new StringBuilder();
        jsonOutStr.append("{\"total\":" + totalNum + ",rows:");
        jsonOutStr.append(Documents.dc2json(dc, ""));
        jsonOutStr.append("}");

        BeanCtx.p(jsonOutStr);

        long te = System.currentTimeMillis();
        return "";
    }
}