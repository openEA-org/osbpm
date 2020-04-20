package cn.linkey.rulelib.S001;

import java.util.HashMap;
import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * 搜索所有设计元素并替换关键字
 * 
 * @author Administrator
 *
 */
public class R_S001_B024 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) {
        BeanCtx.setCtxData("WF_NoEncode", "1"); // 指定存盘时不进行编码操作
        BeanCtx.getResponse().setContentType("text/json;charset=utf-8");
        String keyword = Tools.decodeUrl(BeanCtx.g("keyword", true));
        String newkeyword = Tools.decodeUrl(BeanCtx.g("newkeyword", true));
        String tableList = BeanCtx.g("tableList", true);
        String qryappid = BeanCtx.g("WF_Appid", true);

        String numStr = "";
        int i = 0;
        String[] tableArray = Tools.split(tableList);
        for (String tableName : tableArray) {
            //针对每一张表要进行全文搜索，转成json对像后进行比较
            String sql = "select * from " + tableName;
            if (Tools.isNotBlank(qryappid)) {
                sql = sql + " where WF_Appid='" + qryappid + "'";
            }
            Document[] dc = Rdb.getAllDocumentsBySql(sql);
            for (Document doc : dc) {
                String jsonStr = doc.toJson();
                if (jsonStr.indexOf(keyword) != -1) {
                    int j = doc.removeToTrash(); //在替换之前移动一份到回收站中，用来恢复
                    if (j > 0) {
                        jsonStr = jsonStr.replace(keyword, newkeyword);
                        doc.appendFromJsonStr(jsonStr);
                        doc.save();
                        numStr += doc.g("FormNumber") + doc.g("RuleNum") + doc.g("GridNum") + doc.g("Dataid") + " ";
                        i++;
                    }
                }
            }
        }
        BeanCtx.p("{\"Status\":\"ok\",\"msg\":\"" + BeanCtx.getMsg("Designer", "SearchReplaceResultMsg", i, numStr) + "\"}");
        return "";
    }

}
