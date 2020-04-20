package cn.linkey.rulelib.S017;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.doc.Documents;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;

/**
 * @RuleName:ORG_根据用户中文名搜索用户
 * @author admin
 * @version: 8.0
 * @Created: 2014-07-09 09:53
 */
final public class R_S017_B021 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        //params为运行本规则时所传入的参数
        //{"keyword":"刘"}
        String userid = BeanCtx.getUserid();
        String keyword = (String) params.get("keyword");
        String sql = "select Deptid,Folderid,Userid,CnName,FolderName as DeptName,JobTitle,SortNumber,IndexFlag from BPM_OrgUserWithMainDept where CnName like '%" + keyword + "%'";
        int total = Rdb.getCountBySql(sql);

        Document[] dc = Rdb.getAllDocumentsBySql(sql);
        String jsonStr = Documents.dc2json(dc, "");

        jsonStr = "{\"total\":" + total + ",\"rows\":" + jsonStr + "}";
        return jsonStr;

    }
}