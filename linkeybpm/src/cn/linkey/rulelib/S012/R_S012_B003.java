package cn.linkey.rulelib.S012;

import java.sql.Connection;
import java.util.HashMap;
import java.util.HashSet;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;

/**
 * @RuleName:传送数据到MySql中去
 * @author admin
 * @version: 8.0
 * @Created: 2014-09-20 19:38
 */
final public class R_S012_B003 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        //params为运行本规则时所传入的参数
        Connection conn = null;
        BeanCtx.setDocNotEncode(); //不进行编码
        try {
            int m = 0;
            conn = Rdb.getNewConnection("mysql");
            String sql = "select TableName from BPM_TableConfig where TableType='1' and WF_Appid='S001' and TableName like 'BPM_%'";
            HashSet<String> tableSet = Rdb.getValueSetBySql(sql);
            for (String tableName : tableSet) {
                m++;
                int i = 0, j = 0, e = 0;
                Rdb.execSql(conn, "delete from " + tableName); //先清除所有数据

                BeanCtx.p(m + ".准备传送数据到(" + tableName + ")中.....");
                Document[] dc = Rdb.getAllDocumentsBySql("select * from " + tableName);
                for (Document doc : dc) {
                    int r = doc.save(conn, tableName);
                    if (r > 0) {
                        i++;
                    }
                    else {
                        j++;
                        return "";
                    }
                }

                BeanCtx.p("共成功传送(" + i + ")条数据,(<font color=red>" + j + "</font>)条数据失败<br>");
            }
        }
        catch (Exception e) {
            BeanCtx.p("出错了<br>");
        }
        finally {
            Rdb.close(conn);
        }
        BeanCtx.p("全部传送完成");

        return "";
    }
}