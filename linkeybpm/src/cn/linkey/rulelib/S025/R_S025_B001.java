package cn.linkey.rulelib.S025;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * @RuleName:删除分类
 * @author admin
 * @version: 8.0
 * @Created: 2014-08-13 11:27
 */
final public class R_S025_B001 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        //params为运行本规则时所传入的参数
        String jsonStr = "";
        String docUnid = BeanCtx.g("wf_docunid", true);
        if (Tools.isNotBlank(docUnid)) {
            String sql = "select * from App_TempTree where WF_OrUnid='" + docUnid + "'";
            Document folderDoc = Rdb.getDocumentBySql(sql);

            //看本文件夹下面是否有文档
            String folderid = folderDoc.g("Folderid");
            sql = "select WF_OrUnid from App_TempDocList where Folderid='" + folderid + "'";
            if (Rdb.hasRecord(sql)) {
                jsonStr = Tools.jmsg("error", "本分类下面还有文档,不能删除!");
            }
            else {
                folderDoc.remove(true);
                jsonStr = Tools.jmsg("ok", "成功删除!");
            }
        }
        else {
            jsonStr = Tools.jmsg("error", "参数错误!");
        }
        BeanCtx.p(jsonStr);
        return "";
    }
}