package cn.linkey.rulelib.S013;

import java.util.HashMap;
import java.util.LinkedHashSet;

import cn.linkey.doc.Document;
import cn.linkey.doc.Documents;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.DateUtil;

/**
 * @RuleName:选择年份
 * @author admin
 * @version: 8.0
 * @Created: 2014-06-09 16:49
 */
final public class R_S013_B005 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) {
        //params为运行本规则时所传入的参数

        LinkedHashSet<Document> dc = new LinkedHashSet<Document>();
        Document doc;

        String sYear = DateUtil.getNow("yyyy");
        int iYear = Integer.parseInt(sYear);
        for (int i = iYear - 5; i < iYear + 5; i++) {
            doc = new Document("");
            doc.s("id", Integer.toString(i));
            doc.s("text", Integer.toString(i));
            dc.add(doc);
        }

        /*
         * Document[] dc = new Document[10]; Document doc; String sYear = DateUtil.getNow("yyyy"); int iYear = Integer.parseInt(sYear); for(int i = 0; i < 10; i++){ iYear++; doc = new Document("");
         * doc.s("id", Integer.toString(iYear)); doc.s("text", Integer.toString(iYear)); dc[i] = doc; }
         */

        BeanCtx.print(Documents.dc2json(dc, "")); // 输出json字符串

        return "";
    }
}