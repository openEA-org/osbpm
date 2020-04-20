package cn.linkey.form;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.util.Tools;

/**
 * 表单设计类负责生成表单设计的控件生成
 * 
 * @author Administrator 本类为单例类
 */
public class FormDesigner {

    /**
     * 获得表单中字段的属性值
     */
    @SuppressWarnings("unchecked")
    public String getFdAttr(Document formDoc, String fdName, String attrName) {
        String fieldConfigJson = formDoc.g("FieldConfig");
        int spos = fieldConfigJson.indexOf("[");
        if (spos != -1) {
            fieldConfigJson = fieldConfigJson.substring(spos, fieldConfigJson.lastIndexOf("]") + 1);
            if (Tools.isNotBlank(fieldConfigJson)) {
                JSONArray jsonArr = JSON.parseArray(fieldConfigJson);
                for (int i = 0; i < jsonArr.size(); i++) {
                    Map<String, String> fieldItemMap = (Map<String, String>) JSON.parse(jsonArr.getString(i));
                    if (fieldItemMap.get("name").equals(fdName)) { //找到字段
                        return fieldItemMap.get(attrName); //返回字段的属性值
                    }
                }
            }
        }
        return "";
    }

    /**
     * 设置表单中字段的属性值
     */
    @SuppressWarnings("unchecked")
    public void setFdAttr(Document formDoc, String fdName, String attrName, String attrValue) {
        String fieldConfigJson = formDoc.g("FieldConfig");
        int spos = fieldConfigJson.indexOf("[");
        if (spos != -1) {
            fieldConfigJson = fieldConfigJson.substring(spos, fieldConfigJson.lastIndexOf("]") + 1);
            if (Tools.isNotBlank(fieldConfigJson)) {
                JSONArray jsonArr = JSON.parseArray(fieldConfigJson);
                for (int i = 0; i < jsonArr.size(); i++) {
                    Map<String, String> fieldItemMap = (Map<String, String>) JSON.parse(jsonArr.getString(i));
                    if (fieldItemMap.get("name").equals(fdName)) { //找到字段
                        fieldItemMap.put(attrName, attrValue);
                        jsonArr.set(i, fieldItemMap);
                        String newJsonStr = "{\"fdList\":" + jsonArr.toJSONString() + "}"; //获得新的json字符串
                        formDoc.s("FieldConfig", newJsonStr); //重新设定
                        return;
                    }
                }

                //如果上面没的找到字段的配置值则追加一个配置值
                jsonArr.add("{\"name\":\"" + fdName + "\",\"" + attrName + "\":\"" + attrValue + "\"}");
                String newJsonStr = "{\"fdList\":" + jsonArr.toJSONString() + "}"; //获得新的json字符串
                formDoc.s("FieldConfig", newJsonStr); //重新设定

            }
        }
    }

    /**
     * 获得表单所有控件的属性配置json
     * @param uiType 
     * @return
     */
    public String getFormAttributeJson() {

        //1.首先获得所有属性的配置文档
        String sql = "select Attriid,AttriConfig from BPM_FormControlAttribute";
        HashMap<String, String> attriMap = Rdb.getMapDataBySql(sql);

        //2.根据每个控件选择的属性生成js文件
        StringBuilder jsonStr = new StringBuilder();
        jsonStr.append("var propertyConfig={\"typeList\":[");
        sql = "select AttriConfig,AttriTypeName from BPM_FormControlList where Status='1'";
        Document[] dc = Rdb.getAllDocumentsBySql(sql);
        int i = 0;
        for (Document doc : dc) {
            String attriConfig = doc.g("AttriConfig");
            String attriTypeName = doc.g("AttriTypeName");
            String[] attriArray = Tools.split(attriConfig);
            if (i == 0) {
                i = 1;
            }
            else {
                jsonStr.append(",");
            }
            jsonStr.append("\n{\"typeName\":\"" + attriTypeName + "\",\"rows\":[");
            int j = 0;
            for (String attrid : attriArray) {
                if (j == 0) {
                    j = 1;
                }
                else {
                    jsonStr.append(",");
                }
                String configStr = attriMap.get(attrid);
                jsonStr.append(configStr);
            }
            jsonStr.append("]}");
        }
        jsonStr.append("]}");
        return jsonStr.toString();
    }

    /**
     * 获得表单控件的html代码
     * 
     * @return
     */
    public String getFormControlHtmlCode(String controlType, String UItype) {
    	//20180202 添加对UI类型判断选择
        String sql = "select HtmlCode from BPM_FormControlList where ControlType='" + controlType + "' and UIType = '"+ UItype +"'and Status='1' order by SortNum";
        Document[] dc = Rdb.getAllDocumentsBySql(sql);
        StringBuilder HtmlCode = new StringBuilder(3000);
        for (Document doc : dc) {
            HtmlCode.append(doc.g("HtmlCode"));
            HtmlCode.append("<br>\n");
        }
        HtmlCode.trimToSize();
        return HtmlCode.toString();
    }

    /**
     * 获得表单控件的Js代码
     * @param uiType 
     * @return
     * 20180206 添加UI类型的参数 uiType
     */
    public String getFormControlJsCode(String uiType) {
        String sql = "select JsCode from BPM_FormControlList where UIType = '" + uiType + "' and Status='1' order by SortNum";
        Document[] dc = Rdb.getAllDocumentsBySql(sql);
        StringBuilder JsCode = new StringBuilder(3000);
        for (Document doc : dc) {
            String jsCode = doc.g("JsCode");
            JsCode.append(jsCode);
            JsCode.append("\n");
        }
        JsCode.trimToSize();
        return JsCode.toString();
    }

}
