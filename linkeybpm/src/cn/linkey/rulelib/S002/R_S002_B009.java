package cn.linkey.rulelib.S002;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;
import cn.linkey.wf.ModNode;
import cn.linkey.form.ModForm;

/**
 * 获得表单的所有字段属性
 * 
 * @author Administrator
 */
public class R_S002_B009 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) {
        BeanCtx.getResponse().setContentType("text/json;charset=utf-8");

        ModNode modNode = (ModNode) BeanCtx.getBean("ModNode");
        String processid = BeanCtx.g("Processid");
        String nodeType = BeanCtx.g("NodeType");
        String nodeid = BeanCtx.g("Nodeid");

        //获得节点文档是否有选择更换主表单
        String nodeTableName = modNode.getNodeTableName(processid, nodeid); //节点所在数据库表
        String sql = "select * from " + nodeTableName + " where Processid='" + processid + "' and Nodeid='" + nodeid + "'";
        Document nodeDoc = Rdb.getDocumentBySql(sql);
        String formNumber = "";
        if (Tools.isNotBlank(nodeDoc.g("FormNumber"))) {
            //说明有更换主表单
            formNumber = nodeDoc.g("FormNumber");
        }
        else {
            sql = "select FormNumber from BPM_ModProcessList where Processid='" + processid + "' and Nodeid='Process'";
            formNumber = Rdb.getValueBySql(sql);
        }

        //获得主表单的字段列表
        //	    BeanCtx.out("formNumber="+formNumber);
        Document formDoc = ((ModForm) BeanCtx.getBean("ModForm")).getFormDoc(formNumber);
        if (formDoc.isNull()) {
            return "{\"rows\":[{}]}";
        }
        //1.当前环节的所有字段配置信息
        HashMap<String, Map<String, String>> curFieldConfigMap = new HashMap<String, Map<String, String>>(); //当前节点配置信息
        String curNodeFieldConfig = "[" + nodeDoc.g("FieldConfig") + "]";
        //BeanCtx.out("R_S002_B009当前环节的所有配置="+curNodeFieldConfig);
        if (Tools.isNotBlank(curNodeFieldConfig)) {
            JSONArray curFormJsonArr = JSON.parseArray(curNodeFieldConfig);
            for (int i = 0; i < curFormJsonArr.size(); i++) {
                Map<String, String> fieldItemMap = (Map<String, String>) JSON.parse(curFormJsonArr.getString(i));
                curFieldConfigMap.put(fieldItemMap.get("name"), fieldItemMap);
            }
        }

        //2.获得主表单的配置字段信息
        HashMap<String, Map<String, String>> mainFormAllFieldConfigMap = new HashMap<String, Map<String, String>>(); //所有字段的配置信息map对像
        String mainFormFieldConfig = formDoc.g("FieldConfig");
        int spos = mainFormFieldConfig.indexOf("[");
        if (spos != -1) {
            mainFormFieldConfig = mainFormFieldConfig.substring(spos, mainFormFieldConfig.lastIndexOf("]") + 1);
        }
        JSONArray mainFormJsonArr = JSON.parseArray(mainFormFieldConfig);
        for (int i = 0; i < mainFormJsonArr.size(); i++) {
            Map<String, String> mainFieldConfigMap = (Map<String, String>) JSON.parse(mainFormJsonArr.getString(i));
            String fieldName = mainFieldConfigMap.get("name"); //主表单字段名称
            Map<String, String> curFieldJsonObject = curFieldConfigMap.get(fieldName); //当前节点的字段配置信息
            if (curFieldJsonObject != null) {
                //说明在当前环节中有配置字段信息
                for (String attrName : curFieldJsonObject.keySet()) {
                    if (!attrName.equals("name")) {
                        //BeanCtx.out("替换主表单字段("+fieldName+")属性attrName="+attrName+"="+curFieldJsonObject.get(attrName));
                        mainFieldConfigMap.put(attrName, curFieldJsonObject.get(attrName)); //追加或替换主表单中已有的字段属性
                    }
                }
            }
            curFieldConfigMap.remove(fieldName); //从当前节点的配置信息中删除此字段，如果后面还有的话就得追加到配置中去了
            //BeanCtx.out("加入字段("+fieldName+")属性到主表单字段属性中="+mainFieldConfigMap);
            mainFormAllFieldConfigMap.put(fieldName, mainFieldConfigMap);
        }
        //BeanCtx.out("替换后的主表单字段属性="+mainFormAllFieldConfigMap);

        //追加当前节点中多出来的字段到主配置中去
        for (String fdName : curFieldConfigMap.keySet()) {
            mainFormAllFieldConfigMap.put(fdName, curFieldConfigMap.get(fdName));
        }

        //BeanCtx.out("追加后的主表单字段属性="+mainFormAllFieldConfigMap);

        //所有字段的配置信息
        StringBuilder jsonStr = new StringBuilder();
        for (String fdName : mainFormAllFieldConfigMap.keySet()) {
            Map<String, String> fieldConfigMap = mainFormAllFieldConfigMap.get(fdName);
            if (jsonStr.length() > 0) {
                jsonStr.append(",");
            }
            String nodeFdAcl = fieldConfigMap.get("NodeFdAcl") == null ? "" : fieldConfigMap.get("NodeFdAcl");
            String remark = fieldConfigMap.get("remark") == null ? "" : fieldConfigMap.get("remark");
            String nodeRule = fieldConfigMap.get("NodeRule") == null ? "" : fieldConfigMap.get("NodeRule");
            jsonStr.append("{\"name\":\"" + fdName + "\",\"NodeFdAcl\":\"" + nodeFdAcl + "\",\"NodeRule\":\"" + nodeRule + "\",\"remark\":\"" + remark + "\"}");
        }
        jsonStr.insert(0, "{\"rows\":[");
        jsonStr.append("]}");

        //BeanCtx.out("R_S002_B009="+jsonStr.toString());
        BeanCtx.print(jsonStr.toString());

        return "";
    }

}