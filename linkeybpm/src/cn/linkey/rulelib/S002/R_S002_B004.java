package cn.linkey.rulelib.S002;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;

/**
 * 检测流程编号有没有重复的编号
 * 
 * @author Administrator
 */
public class R_S002_B004 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) {
        BeanCtx.getResponse().setContentType("text/json;charset=utf-8");
        String eldocunid = BeanCtx.g("WF_OrUnid"); // 元素unid
        String elid = BeanCtx.g("WF_Elid"); // 元素id
        String eltable = BeanCtx.g("WF_TableName"); // 元素所在表
        String elcolName = BeanCtx.g("WF_TableColName"); // id所在sql表中的字段名称
        String sql = "select WF_OrUnid from " + eltable + " where " + elcolName + "='" + elid + "' and WF_OrUnid<>'" + eldocunid + "'";
        if (Rdb.hasRecord(sql)) {
            BeanCtx.print("{\"Status\":false}"); // 验证不通过
        }
        else {
            BeanCtx.print("{\"Status\":true}"); // 验证通过
        }
        return "";
    }
}