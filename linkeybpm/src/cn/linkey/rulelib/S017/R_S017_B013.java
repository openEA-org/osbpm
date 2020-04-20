package cn.linkey.rulelib.S017;

import java.util.HashMap;
import java.util.LinkedHashSet;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.doc.Documents;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * @RuleName:MOD_获得流程清单服务
 * @author admin
 * @version: 8.0
 * @Created: 2014-07-01 22:40
 */
final public class R_S017_B013 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        //params为运行本规则时所传入的参数
        //示例参数: {"Folderid":"001002"}

        String folderid = (String) params.get("Folderid");
        String sql = "";
        if (Tools.isBlank(folderid) || folderid.equals("*")) {
            sql = "select WF_Version,Processid,NodeName as ProcessName,ProcessNumber,ProcessStarter from BPM_ModProcessList where Status='1'";
        }
        else {
            sql = "select WF_Version,Processid,NodeName as ProcessName,ProcessNumber,ProcessStarter from BPM_ModProcessList where Folderid='" + Rdb.formatArg(folderid) + "' and Status='1'";
        }

        int total = 0;
        LinkedHashSet<Document> dc = Rdb.getAllDocumentsSetBySql(sql);
        LinkedHashSet<Document> newdc = new LinkedHashSet<Document>();

        //需要把没有启动权限的流程去掉
        for (Document doc : dc) {
            if (BeanCtx.getLinkeyUser().inRoles(BeanCtx.getUserid(), doc.g("ProcessStarter"))) {
                newdc.add(doc);
                total++;
            }
        }

        String jsonStr = Documents.dc2json(newdc, "");

        jsonStr = "{\"total\":" + total + ",\"rows\":" + jsonStr + "}";
        return jsonStr;
    }
}