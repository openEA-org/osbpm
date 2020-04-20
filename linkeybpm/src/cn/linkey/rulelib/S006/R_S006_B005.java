package cn.linkey.rulelib.S006;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;

/**
 * 检测设计元素有没有重复的编号
 * 
 * @author Administrator 编号:R_S006_B005
 */
public class R_S006_B005 implements LinkeyRule {
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