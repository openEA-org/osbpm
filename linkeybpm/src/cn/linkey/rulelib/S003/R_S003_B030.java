package cn.linkey.rulelib.S003;

import java.util.HashMap;
import java.util.Map;

import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.form.HtmlParser;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * 本规则负责分析A标签
 * 
 * @author Administrator
 *
 */
public class R_S003_B030 implements LinkeyRule {

    @Override
    @SuppressWarnings("unchecked")
    public String run(HashMap<String, Object> params) {
        String mStr = (String) params.get("HtmlStr"); //传的的input标签的内容
        Document doc = (Document) params.get("DataDocument"); //数据主文档对像，包含所有字段数据
        String readOnly = (String) params.get("ReadOnly"); //1表示只读状态，0表示编辑状态
        String fdName = (String) params.get("FieldName"); //字段名称
        Map<String, String> fieldConfigMap = (Map<String, String>) params.get("FieldConfig"); //字段的配置属性

        if (readOnly.equals("1")) {
            return readDoc(doc, mStr, fdName, fieldConfigMap);
        }
        else {
            return editDoc(doc, mStr, fdName, fieldConfigMap);
        }
    }

    /**
     * 只读状态下解析
     * 
     * @param doc 数据文档对像
     * @param mStr HTML标签
     * @return 返回只读模式的HTML标签
     */
    public String readDoc(Document doc, String mStr, String fdName, Map<String, String> fieldConfigMap) {
        if (fieldConfigMap != null) { //说明有字段属性配置，需要设置字段的样式
            //1.首先判断是否阅读时隐藏如果是，则首先返回不用判断其他类型了
            String attrValue = fieldConfigMap.get("hiddentype");
            if (attrValue != null) {
                if (attrValue.indexOf("READ") != -1) {
                    return "";
                }
            }
            
            //20180915 add by alibao
        	HtmlParser htmlParser = (HtmlParser) BeanCtx.getBean("HtmlParser");
            String styleStr = htmlParser.getAttributeValue(mStr, "style").toLowerCase();
            String displayStr = HtmlParser.getStyleCSS(styleStr, "display");
            if(Tools.isNotBlank(displayStr) && displayStr.contains("none")){ //如果有表单字段直接隐藏的话，直接返回空
            	return "";
            }
            
            //说明在阅读模式下不需要隐藏，转入编辑函数中去显示按扭代码
            fieldConfigMap.remove("hiddentype");
        }
        

        return editDoc(doc, mStr, fdName, fieldConfigMap);
    }

    /**
     * 编辑状态下解析a标签
     * 
     * @param doc 数据文档对像
     * @param mStr HTML标签
     * @return 返回编辑模式的HTML标签
     */
    public String editDoc(Document doc, String mStr, String fdName, Map<String, String> fieldConfigMap) {
        HtmlParser htmlParser = (HtmlParser) BeanCtx.getBean("HtmlParser");
        if (fieldConfigMap != null) { //说明有字段属性配置，需要设置字段的样式
            //1.首先判断是否编辑时隐藏如果是，则首先返回不用判断其他类型了
            String attrValue = fieldConfigMap.get("hiddentype");
            if (attrValue != null) {
                if (doc.isNewDoc()) {
                    if (attrValue.indexOf("NEW") != -1) {
                        return "";
                    }
                }
                else if (attrValue.indexOf("EDIT") != -1) {
                    return "";
                }
            }

            String dataoptions = htmlParser.getAttributeValue(mStr, "data-options");

            //3.设置图标icons
            attrValue = fieldConfigMap.get("icons");
            if (attrValue != null) {
                dataoptions = htmlParser.setDataOptions(dataoptions, "iconCls", attrValue, true);
            }

            //3.设置plain
            attrValue = fieldConfigMap.get("plain");
            if (attrValue != null) {
                dataoptions = htmlParser.setDataOptions(dataoptions, "plain", attrValue, false);
            }

            //4.jsevent事件
            attrValue = fieldConfigMap.get("jsevent");
            if (attrValue != null) {
                String jsfun = fieldConfigMap.get("jsfun");
                if (jsfun != null) {
                    mStr = htmlParser.setAttributeValue(mStr, attrValue, jsfun);
                }
            }

            mStr = mStr.replace("{BUTTON}", ""); //去掉按扭文字

            //4.设置data-options属性
            if (Tools.isNotBlank(dataoptions)) {
                mStr = htmlParser.setAttributeValue(mStr, "data-options", dataoptions);
            }

        }
        return mStr;
    }

}
