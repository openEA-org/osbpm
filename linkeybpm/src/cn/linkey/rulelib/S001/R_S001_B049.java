package cn.linkey.rulelib.S001;

import java.util.*;

import cn.linkey.dao.*;
import cn.linkey.util.*;
import cn.linkey.doc.*;
import cn.linkey.factory.*;
import cn.linkey.rule.LinkeyRule;

/**
 * @RuleName:导出所有SQL数据
 * @author admin
 * @version: 8.0
 * @Created: 2014-05-26 15:17
 */
final public class R_S001_B049 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) {
        //params为运行本规则时所传入的参数

        String filePath = BeanCtx.getAppPath();
        String tableList = BeanCtx.getSystemConfig("SysTableForInitData");
        HashSet<String> tableSet = Tools.splitAsLinkedSet(tableList);
        int i = 0;
        for (String tableName : tableSet) {
            String sql = "select * from " + tableName;
            Document[] dc = Rdb.getAllDocumentsBySql(sql);
            for (Document doc : dc) {
                doc.s("WF_OrTableName", tableName);
            }
            String fileName = filePath + "outfile/initdata/" + tableName + ".xml";
            BeanCtx.out("导出" + tableName + "到->" + fileName);
            if (!Documents.dc2Xmlfile(dc, fileName, true)) {
                break;
            }
            i++;
        }
        BeanCtx.p("共导出(" + i + ")个表的数据到硬盘中...");

        return "";
    }
}