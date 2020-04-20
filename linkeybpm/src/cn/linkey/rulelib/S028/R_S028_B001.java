package cn.linkey.rulelib.S028;

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
final public class R_S028_B001 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        //params为运行本规则时所传入的参数
        String jsonStr = "";
        String docUnid = BeanCtx.g("wf_docunid", true);
        if (Tools.isNotBlank(docUnid)) {
            String sql = "select * from BPM_DataDicConfig where WF_OrUnid='" + docUnid + "'";
            Document folderDoc = Rdb.getDocumentBySql(sql);

            //看本文件夹下面是否有文档
            String dataid = folderDoc.g("Dataid");
            sql = "select WF_OrUnid from BPM_DataDicValueList where Dataid='" + dataid + "'";
            if (Rdb.hasRecord(sql)) {
                jsonStr = Tools.jmsg("error", "本数据字典下面还有配置值,不能删除!");
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