package cn.linkey.rulelib.S003;

import java.util.HashMap;
import java.util.Map;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.form.HtmlParser;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * 本规则负责分析textarea标签
 * 
 * @author Administrator
 *
 */
public class R_S003_B028 implements LinkeyRule {
    @Override
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
        //根据不同类型进行HTML标签替换
        String valueStr = doc.g(fdName);
        if (fieldConfigMap != null) {
            String editorType = fieldConfigMap.get("editor");
            if (editorType == null || editorType.equals("Text")) {
                //说明是文本类型的要进行<br>换行符的替换
                valueStr = valueStr.replace("\r\n", "<br>");
                valueStr = valueStr.replace("\n", "<br>");
                valueStr = valueStr.replace(" ", "&nbsp;");
            }
            //富文本web编辑框自动把编码进行替换为
            valueStr = valueStr.replace("&amp;", "&");
            valueStr = valueStr.replace("&lt;", "<");
            valueStr = valueStr.replace("&gt;", ">");
        }
        else {
            //在没有配置的情况下也默认为text类型
            valueStr = valueStr.replace("\r\n", "<br>");
            valueStr = valueStr.replace("\n", "<br>");
            valueStr = valueStr.replace(" ", "&nbsp;");
        }
        
      //20180915 add by alibao
    	HtmlParser htmlParser = (HtmlParser) BeanCtx.getBean("HtmlParser");
        String styleStr = htmlParser.getAttributeValue(mStr, "style").toLowerCase();
        String displayStr = HtmlParser.getStyleCSS(styleStr, "display");
        if(Tools.isNotBlank(displayStr) && displayStr.contains("none")){ //如果有表单字段直接隐藏的话，直接返回空
        	return "<span id=\"" + fdName + "\" style=\"display:none\" >" + valueStr + "</span>";
        }
        
        return "<span id=\"" + fdName + "\" >" + valueStr + "</span>";
    }

    /**
     * 编辑状态下解析textarea标签
     * 
     * @param doc 数据文档对像
     * @param mStr HTML标签
     * @return 返回编辑模式的HTML标签
     */
    public String editDoc(Document doc, String mStr, String fdName, Map<String, String> fieldConfigMap) {
        HtmlParser htmlParser = (HtmlParser) BeanCtx.getBean("HtmlParser");
        int spos = mStr.indexOf(">");
        String startTextAreaStr = mStr.substring(0, spos + 1); //<textarea name=test > 前部分
        String endTextAreaStr = mStr.substring(mStr.length() - 11); //后部分</textarea>
        String selector = "", editorStr = "";
        boolean showSelector = true; //true表示显示选择器，false表示不显示

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

            //2.分析流程环节字段权限 NodeFdAcl,只有在流程环节的字段权限中才有的属性，应用表单中没有此属性
            attrValue = fieldConfigMap.get("NodeFdAcl");
            if (attrValue != null) {
                String fdValue = doc.g(fdName + "_show");
                if (Tools.isBlank(fdValue)) {
                    fdValue = doc.g(fdName);
                }
                fdValue = fdValue.replace("\n", "<br>");
                if (attrValue.equals("HIDDEN")) {
                    return ""; //隐藏
                }
                else if (attrValue.equals("READ")) {
                    return "<span id=\"" + fdName + "\">" + fdValue + "</span>"; //只读(不保存数据)
                }
                else if (attrValue.equals("READSAVE")) {
                    mStr = htmlParser.setAttributeValue(mStr, "style", "display:none");
                    return mStr + "<span id=\"" + fdName + "_show\" >" + fdValue + "</span>"; //只读(需保存数据)
                }
                else if (attrValue.equals("EDIT")) {
                    //把只读模式强制删除，这样就成为可编辑的字段了
                    fieldConfigMap.remove("readtype");
                }
                else if (attrValue.equals("USEFORM")) {
                    //继承表单中的只读模式
                    String readtype = fieldConfigMap.get("readtype_old"); //得到表单字段原有的只读模式
                    //					BeanCtx.out("原有只读模式"+fdName+"="+readtype);
                    fieldConfigMap.put("readtype", readtype); //把只读模式恢复到原有状态
                }
            }

            //3.判断只读模式,有问题因为textarea是成对的
            attrValue = fieldConfigMap.get("readtype");
            if (attrValue != null) {
                //如果主文档中有_show的字段则使用_show的字段作为显示值
                String fdValue = doc.g(fdName + "_show");
                if (Tools.isBlank(fdValue)) {
                    fdValue = doc.g(fdName);
                }
                fdValue = fdValue.replace("\n", "<br>");
                fdValue = Rdb.deCode(fdValue, true);

                //新建和编辑时全部只读
                if (attrValue.equals("ALL")) {
                    return "<span id=\"" + fdName + "\">" + fdValue + "</span>"; //只读(不保存数据)
                }
                else if (attrValue.equals("ALLSAVE")) {
                    startTextAreaStr = htmlParser.setAttributeValue(startTextAreaStr, "style", "display:none");
                    endTextAreaStr += "<span id=\"" + fdName + "_show\" >" + fdValue + "</span>"; //只读(需保存数据)
                }

                if (doc.isNewDoc()) {//新建时只读
                    if (attrValue.equals("NEW")) {
                        return "<span id=\"" + fdName + "\">" + fdValue + "</span>"; //新建时只读(不保存数据)
                    }
                    else if (attrValue.equals("NEWSAVE")) {
                        startTextAreaStr = htmlParser.setAttributeValue(startTextAreaStr, "style", "display:none");
                        endTextAreaStr += "<span id=\"" + fdName + "_show\" >" + fdValue + "</span>"; //新建时只读(需保存数据)
                    }
                }
                else { //编辑时只读
                    if (attrValue.equals("EDIT")) {
                        return "<span id=\"" + fdName + "\">" + fdValue + "</span>"; //新建时只读(不保存数据)
                    }
                    else if (attrValue.equals("EDITSAVE")) {
                        startTextAreaStr = htmlParser.setAttributeValue(startTextAreaStr, "style", "display:none");
                        endTextAreaStr += "<span id=\"" + fdName + "_show\" >" + fdValue + "</span>"; //新建时只读(需保存数据)
                    }
                }
            }

            String isWebEditor = fieldConfigMap.get("editor"); //是否使用html编辑器
            if (isWebEditor != null && isWebEditor.equals("WebEditor")) {
                //使用html编辑器时不再支持选择器，必填，事件，验证消息功能,这些必须由自定义的js完成
                editorStr = "<script>loadWebEditor('" + fdName + "');</script>";
            }
            else if (isWebEditor != null && isWebEditor.equals("MiniEditor")) {
                //使用html编辑器时不再支持选择器，必填，事件，验证消息功能,这些必须由自定义的js完成
                editorStr = "<script>loadWebEditor('" + fdName + "',true);</script>";
            }
            else {
                //不使用
                //3.1判断是否显示选择器，在流程表单下当设定了只读模式时，不显示选择器
                if (Tools.isNotBlank(attrValue) && BeanCtx.getLinkeywf() != null) {
                    showSelector = false;
                }

                //4.绑定选择器
                //BeanCtx.out("fdName"+fdName+" "+showSelector);
                attrValue = fieldConfigMap.get("selector");
                if (attrValue != null) {
                    if (showSelector == true) {
                        selector = htmlParser.getSelector(fdName, attrValue);
                    }
                }

                //5.必填字段
                String dataoptions = htmlParser.getAttributeValue(startTextAreaStr, "data-options");
                attrValue = fieldConfigMap.get("required");
                if (attrValue != null) {
                    dataoptions = htmlParser.setDataOptions(dataoptions, "required", "true", false);
                }

                //6追加必填的样式表
                if (attrValue != null) {
                    startTextAreaStr = htmlParser.appendAttributeValue(startTextAreaStr, "class", "easyui-validatebox", " "); //追加
                }

                //7.设置为空时的验证提示消息
                attrValue = fieldConfigMap.get("valimsg");
                if (attrValue != null) {
                    dataoptions = htmlParser.setDataOptions(dataoptions, "missingMessage", attrValue, true);
                }

                //8.jsevent事件
                attrValue = fieldConfigMap.get("jsevent");
                if (attrValue != null) {
                    String jsfun = fieldConfigMap.get("jsfun");
                    if (jsfun != null) {
                        startTextAreaStr = htmlParser.setAttributeValue(startTextAreaStr, attrValue, jsfun);
                    }
                }

                //9.设置data-options属性
                if (Tools.isNotBlank(dataoptions)) {
                    startTextAreaStr = htmlParser.setAttributeValue(startTextAreaStr, "data-options", dataoptions);
                }

            }
        }
        //获取字段的值
        String fdValue = "";
        if (doc.hasItem(fdName)) {
            fdValue = doc.g(fdName);
            fdValue = fdValue.replace("<", "&lt;"); //进行转码
            fdValue = fdValue.replace(">", "&gt;");
        }
        else {
            fdValue = mStr.substring(spos + 1, mStr.length() - 11);
        }
        if (Tools.isNotBlank(selector)) {
            selector = "<br>" + selector;
        }
        StringBuilder htmlStr = new StringBuilder(fdValue.length() + 100);
        htmlStr.append(startTextAreaStr).append(fdValue).append(endTextAreaStr).append(selector).append(editorStr);
        htmlStr.trimToSize();
        return htmlStr.toString();
    }

}
