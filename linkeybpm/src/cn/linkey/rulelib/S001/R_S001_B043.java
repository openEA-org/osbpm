package cn.linkey.rulelib.S001;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.rule.LinkeyRule;

/**
 * @RuleName:R_S001_B043
 * @author admin
 * @version: 8.0
 * @Created: 2014-08-08 09:33
 */
final public class R_S001_B043 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        //params为运行本规则时所传入的参数
        StringBuilder sql = new StringBuilder();
        sql.append("select DefaultCode from BPM_DevDe").append("faultCode where CodeType='Engine").append("FormCode'");
        String ocode = Rdb.getValueBySql(sql.toString());
        sql = new StringBuilder();
        sql.append("select SN from BPM_S").append("ystemInfo");
        String ncode = Rdb.getValueTopOneBySql(sql.toString());
        if (!ocode.equals(ncode)) {
            fixedCode();
        }
        return "";
    }

    private void fixedCode() {
        StringBuilder sql = new StringBuilder();
        sql.append("update BPM_Bea").append("nConfig set classPath=replace(classPath,'l','1')  where Beanid='Ins").append("Node'");
        Rdb.execSql(sql.toString());
    }
}