package cn.linkey.rulelib.S003;

import java.util.HashMap;
import java.util.HashSet;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.linkey.app.AppUtil;
import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * @RuleName:获得流程主文档中的动态字段JSON
 * @author admin
 * @version: 8.0
 * @Created: 2014-06-18 23:39
 */
final public class R_S003_B065 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        //params为运行本规则时所传入的参数
        String mainDocUnid = BeanCtx.g("wf_docunid", true);
        String gridNum = BeanCtx.g("GridNum", true);
        //BeanCtx.setDebug();

        //获得视图配置列的所有字段名称
        Document gridDoc = AppUtil.getDocByid("BPM_GridList", "GridNum", gridNum, true);
        String columnConfig = gridDoc.g("ColumnConfig");
        int spos = columnConfig.indexOf("[");
        if (spos == -1) {
            BeanCtx.p("{\"total\":1,\"rows\":[{}]}");
            return "";
        }
        columnConfig = columnConfig.substring(spos, columnConfig.lastIndexOf("]") + 1);
        JSONArray jsonArr = JSON.parseArray(columnConfig);
        HashSet<String> fdList = new HashSet<String>(); //所有动态字段列表
        for (int i = 0; i < jsonArr.size(); i++) {
            JSONObject colConfigItem = (JSONObject) jsonArr.get(i);
            String fdName = colConfigItem.getString("FdName");
            if (Tools.isNotBlank(fdName) && !fdName.equals("icon")) {
                fdList.add(fdName);
            }
        }

        //从主文档中找这些字段的所有值组成json输出
        int max = 0;
        StringBuilder jsonStr = new StringBuilder();
        String sql = "select * from BPM_AllDocument where WF_OrUnid='" + mainDocUnid + "'";
        Document doc = Rdb.getDocumentBySql(sql);
        if (!doc.isNull()) {
            for (int i = 1; i < 10; i++) {
                String itemStr = "";
                for (String fdName : fdList) {
                    fdName = fdName + "_" + i;
                    if (doc.hasItem(fdName)) {
                        if (Tools.isNotBlank(itemStr)) {
                            itemStr += ",";
                        }
                        itemStr += "\"" + fdName.substring(0, fdName.indexOf("_")) + "\":\"" + doc.g(fdName) + "\"";
                    }
                    else {
                        max = i - 1;
                        i = 10000; //说明没有字段了退出
                        break;
                    }
                }
                if (Tools.isNotBlank(itemStr)) {
                    if (jsonStr.length() > 0) {
                        jsonStr.append(",");
                    }
                    jsonStr.append("{").append(itemStr).append("}");
                }
            }
        }
        else {
            //说明主文档还不存在
            jsonStr.append("{\"total\":1,\"rows\":[{}]}");
        }
        jsonStr.insert(0, "{\"total\":" + max + ",\"rows\":[").append("]}");
        //BeanCtx.out(jsonStr.toString());
        BeanCtx.p(jsonStr.toString());
        return "";
    }
}