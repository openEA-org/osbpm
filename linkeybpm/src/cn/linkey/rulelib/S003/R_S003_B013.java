package cn.linkey.rulelib.S003;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.rule.LinkeyRule;

/**
 * 本规则存盘sn序列号
 * 
 * @author Administrator
 *
 */
public class R_S003_B013 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) {
        String a1 = "BPM_", b2 = "Syst", a3 = "emI", a4 = "nfo";
        String sql = "select * from " + a1 + b2 + a3 + a4;
        Document doc = Rdb.getDocumentBySql(sql);
        if (!doc.isNull()) {
            sql = "select * from BPM_DevDefaultCode where CodeType='EngineFormCode'";
            Document sdoc = Rdb.getDocumentBySql(sql);
            sdoc.s("WF_Appid", "S001");
            sdoc.s("Country", "CN");
            sdoc.s("CodeType", "EngineFormCode");
            sdoc.s("DefaultCode", doc.g("SN"));
            sdoc.s("WF_Version", "8.0");
            sdoc.save();
        }
        return "";
    }
}
