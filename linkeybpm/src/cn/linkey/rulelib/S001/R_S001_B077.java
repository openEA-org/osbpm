package cn.linkey.rulelib.S001;

import java.sql.Connection;
import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * @RuleName:系统生成序列专用规则
 * @author admin
 * @version: 8.0
 * @Created: 2015-11-05 15:54
 */
final public class R_S001_B077 implements LinkeyRule {
    @Override
    public synchronized String run(HashMap<String, Object> params) throws Exception {
        //params为运行本规则时所传入的参数
        /*
         * 使用线程链接时mysql加了synchronized后产生死锁所以改为获取一个新的链接来解决此问题,mssql未发现有此问题 产生的后果因为是新获得的链接对像与线程中的对像不是一个，所以如果是新建时出错系统回滚时此序列号不能回滚 会有跳号的情况出现 如果不加
         * synchronized时会出现重号情况因为多外线程同时运行本函数时可能是相加出结果后再执行update操作，所以update后执行时就会出现重号情况. 如果是oracle的系统最好使用序列来替换本规则的生成方式
         */
        String key = (String) params.get("key");
        if (Rdb.getDbType().equals("MSSQL")) {
            return getNewSerialNoForMSSQL(key);
        }
        else {
            //MySQL要取一个新链接不然在多线程并发时会产生死锁
            Connection conn = null;
            String SerialNo = "";
            try {
                conn = Rdb.getNewConnection("default");//获得一个新的数据库链接对像
                String sql = "select SerialNo from BPM_SerialNo where SerialKey='" + key + "'";
                SerialNo = Rdb.getValueBySql(conn, sql);
                if (Tools.isBlank(SerialNo)) {
                    SerialNo = "100001";
                    sql = "insert into BPM_SerialNo(WF_OrUnid,SerialNo,SerialKey) values('" + Rdb.getNewUnid() + "','" + SerialNo + "','" + key + "')";
                    Rdb.execSql(conn, sql);
                }
                else {
                    SerialNo = String.valueOf(Long.parseLong(SerialNo) + 1);
                    sql = "update BPM_SerialNo set SerialNo='" + SerialNo + "' where SerialKey='" + key + "'";
                    Rdb.execSql(conn, sql);
                }
            }
            catch (Exception e) {
                BeanCtx.log(e, "E", "Rdb.getNewSerialNo()获取文档编号时出错!");
            }
            finally {
                Rdb.close(conn);
            }
            return SerialNo;
        }
    }

    /**
     * sql server可以不用产生新的链接就可以，所以单独写一个函数
     * 
     * @param key
     * @return
     */
    private static String getNewSerialNoForMSSQL(String key) {
        String SerialNo = "";
        String sql = "select SerialNo from BPM_SerialNo where SerialKey='" + key + "'";
        SerialNo = Rdb.getValueBySql(sql);
        if (Tools.isBlank(SerialNo)) {
            SerialNo = "100001";
            sql = "insert into BPM_SerialNo(WF_OrUnid,SerialNo,SerialKey) values('" + Rdb.getNewUnid() + "','" + SerialNo + "','" + key + "')";
            Rdb.execSql(sql);
        }
        else {
            SerialNo = String.valueOf(Long.parseLong(SerialNo) + 1);
            sql = "update BPM_SerialNo set SerialNo='" + SerialNo + "' where SerialKey='" + key + "'";
            Rdb.execSql(sql);
        }
        return SerialNo;
    }
}