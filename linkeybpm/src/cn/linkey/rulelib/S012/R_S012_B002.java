package cn.linkey.rulelib.S012;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;

/**
 * @RuleName:MSSQL传数据到MySQL
 * @author admin
 * @version: 8.0
 * @Created: 2014-09-20 17:47
 */
final public class R_S012_B002 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        //params为运行本规则时所传入的参数
        String sql = "select * from BPM_TableConfig where TableType='1' and WF_Appid='S001'";
        Document[] dc = Rdb.getAllDocumentsBySql(sql);
        int i = 0;
        for (Document doc : dc) {
            int j = Rdb.creatTable("mysql", doc.g("TableName"), doc.g("FieldConfig"));
            if (j == -2) {
                break;
            }
            if (j >= 0) {
                i++;
            }
        }

        BeanCtx.p("共创建(" + i + ")个数据库表");

        return "";
    }
}