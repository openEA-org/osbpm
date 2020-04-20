package cn.linkey.rulelib.S006;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;

/**
 * @RuleName:旧版本的用户与部门关系转换
 * @author admin
 * @version: 8.0
 * @Created: 2014-04-21 15:25
 */
final public class R_S006_B003 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) {
        //params为运行本规则时所传入的参数
        int i = 0;
        String sql = "select * from BPM.dbo.bpm_orguserlist"; //旧的用户表中找到所有用户
        Document[] dc = Rdb.getAllDocumentsBySql(sql);
        Document mapDoc = BeanCtx.getDocumentBean("BPM_OrgUserDeptMap");
        for (Document doc : dc) {
            String deptid = Rdb.getValueBySql("select Deptid from BPM_OrgDeptList where OrgClass='1' and Folderid='" + doc.g("FolderId") + "'"); //从新的部门中查找最新的编号
            mapDoc.s("WF_OrUnid", Rdb.getNewid("BPM_OrgUserDeptMap"));
            mapDoc.s("Userid", doc.g("ShortName"));
            mapDoc.s("Folderid", doc.g("FolderId"));
            mapDoc.s("CurrentFlag", "1");
            mapDoc.s("OrgClass", "1");
            mapDoc.s("Deptid", deptid);
            mapDoc.s("MainDept", "1");
            mapDoc.save();
            i++;
        }
        BeanCtx.p("共转换(" + i + ")个用户与部门的关系!");

        return "";
    }
}