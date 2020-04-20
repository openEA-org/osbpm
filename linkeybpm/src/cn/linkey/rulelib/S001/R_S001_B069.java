package cn.linkey.rulelib.S001;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * @RuleName:检测配置表与实体表的差异字段
 * @author admin
 * @version: 8.0
 * @Created: 2014-12-16 09:21
 */
final public class R_S001_B069 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        //params为运行本规则时所传入的参数
        //格式化列数据如: doc.s("Subject",doc.g("Subject")+doc.g("UserName")); 可重新格式化Subject字段
        String sql = "select * from BPM_TableConfig where WF_OrUnid='" + BeanCtx.g("DocUnid", true) + "'";
        Document doc = Rdb.getDocumentBySql(sql);

        //看数据库表是否在sql中真实存在
        String dataSourceid = doc.g("DataSourceid");
        if (Tools.isBlank(dataSourceid)) {
            dataSourceid = "default";
        }
        if (!Rdb.isExistTable(dataSourceid, doc.g("TableName"))) {
            BeanCtx.p("数据库实体表未创建!");
            return "";
        }

        //获得配置表中的所有字段的配置集合
        HashSet<String> fieldConfigSet = new HashSet<String>();
        String fieldConfig = doc.g("FieldConfig"); //所有字段配置
        int spos = fieldConfig.indexOf("[");
        if (spos != -1) {
            fieldConfig = fieldConfig.substring(spos, fieldConfig.lastIndexOf("]") + 1);
            //BeanCtx.out("fieldConfig="+fieldConfig);
            com.alibaba.fastjson.JSONArray jsonArr = com.alibaba.fastjson.JSON.parseArray(fieldConfig);
            for (int i = 0; i < jsonArr.size(); i++) {
                com.alibaba.fastjson.JSONObject rowItem = (com.alibaba.fastjson.JSONObject) jsonArr.get(i);
                String fdName = rowItem.getString("FdName"); // 字段名称
                fieldConfigSet.add(fdName);
            }
        }
        String configFdStr = "," + Tools.join(fieldConfigSet, ",").toLowerCase() + ",";

        //sql实体表中的数据库表字段列表
        Set<String> sqlFieldListSet = Rdb.getTableColumnName(doc.g("TableName")).keySet();
        String sqlFdStr = "," + Tools.join(sqlFieldListSet, ",").toLowerCase() + ",";

        //先比较配置表中的所有字段是否在实体表中
        BeanCtx.out(sqlFdStr);
        for (String fdName : fieldConfigSet) {
            if (sqlFdStr.indexOf("," + fdName.toLowerCase() + ",") == -1) {
                BeanCtx.p("<font color=red>字段在实体表中不存在:" + fdName + "</font><br>");
            }
        }

        //再比较sql表中与配置表中的字段差异
        for (String fdName : sqlFieldListSet) {
            if (configFdStr.indexOf("," + fdName.toLowerCase() + ",") == -1) {
                BeanCtx.p("<font color=blue>字段在配置表中不存在:" + fdName + "</font><br>");
            }
        }

        return "";
    }
}