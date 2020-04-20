package cn.linkey.rulelib.S017;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;

/**
 * @RuleName:ORG_获取用户个人信息
 * @author admin
 * @version: 8.0
 * @Created: 2014-07-09 09:38
 */
final public class R_S017_B018 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        //params为运行本规则时所传入的参数
        //无需传入任何params参数
        String userid = BeanCtx.getUserid();
        String sql = "select FolderName as DeptName,Folderid,Deptid,Userid,CnName,JobTitle,secretary,PhoneNumber,QQ,IndexFlag,SortNumber,InternetAddress";
        sql += " from BPM_OrgUserWithMainDept where Userid='" + userid + "'";
        Document doc = Rdb.getDocumentBySql(sql);
        return doc.toJson();
    }
}