package cn.linkey.sap;

import cn.linkey.factory.BeanCtx;
import com.sap.mw.jco.JCO;

public class SAPTest {
    public static void main(String[] args) {
        SAPConn sapconn = (SAPConn) BeanCtx.getBean("SAPConn"); //获得一个sap对像

        //1.链接并获得rfc function并且设置输入参数
        JCO.Function function = sapconn.getRFCFunction("RFC名称");
        function.getImportParameterList().setValue("参数名", "参数值"); //独立输入参数

        JCO.Table inTable = function.getTableParameterList().getTable("输入参数表名"); //通过二维表的输入参数
        inTable.appendRow();
        inTable.setRow(0);
        inTable.setValue("MAKTX", "FIELDNAME");//描述
        inTable.appendRow();
        inTable.setRow(1);
        inTable.setValue("FdName2", "FdValue2");

        //2.执行rfc函数
        sapconn.execute();

        //3.取输出参数
        JCO.Table outTable = function.getTableParameterList().getTable("输出二维表名称");
        outTable.writeHTML("d:/sapouttable.html"); //直接输出到html文件中

        //4.逐行输出outtable中的数据
        for (int i = 0; i < outTable.getNumRows(); i++) {
            outTable.setRow(i);
            String fdValue = outTable.getString("字段名称");
            BeanCtx.out("fdValue=" + fdValue);
        }

        sapconn.close(); //关闭sap链接放入链接池中

    }
}
