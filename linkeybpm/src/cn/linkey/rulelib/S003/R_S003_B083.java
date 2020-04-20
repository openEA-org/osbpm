package cn.linkey.rulelib.S003;

import java.util.HashMap;
import java.util.Map;

import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.form.HtmlParser;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * 本规则负责分析select标签
 * 
 * @author Administrator
 *
 */
public class R_S003_B083 implements LinkeyRule {

    @Override
    @SuppressWarnings("unchecked")
    public String run(HashMap<String, Object> params) {
        String mStr = (String) params.get("HtmlStr"); //传的的input标签的内容
        Document doc = (Document) params.get("DataDocument"); //数据主文档对像，包含所有字段数据
        String readOnly = (String) params.get("ReadOnly"); //1表示只读状态，0表示编辑状态
        String fdName = (String) params.get("FieldName"); //字段名称
        Map<String, String> fieldConfigMap = (Map<String, String>) params.get("FieldConfig"); //从参数中取到字段的配置字段信息
        if (readOnly.equals("1")) { //只读模式
            return readDoc(doc, mStr, fdName, fieldConfigMap);
        }
        else {
            //编辑模式
            return editDoc(doc, mStr, fdName, fieldConfigMap);
        }

    }

    /**
     * 只读状态下解析,返回select选中项的text值
     * 
     * @param doc 数据文档对像
     * @param mStr HTML标签
     * @return 返回只读模式的HTML标签
     */
    public String readDoc(Document doc, String mStr, String fdName, Map<String, String> fieldConfigMap) {

        String textStr=getSelectText(mStr, fdName, doc.g(fdName)); //分析select中的text值
        return "<span id=\""+fdName+"\">"+textStr+"</span>";
        
        /*
         * String fdValue=doc.g(fdName); //获得字段值 
         * int spos=mStr.indexOf(">"); 
         * int epos=mStr.lastIndexOf("<"); 
         * String optionStr=Tools.trim(mStr.substring(spos+1,epos)); //<option value=1>是</option>
         * if(Tools.isBlank(optionStr)){return doc.g(fdName);} //如果没有option则直接返回字段值 //有option的情况下进行分析得到option的text 
         * String optionKey=optionStr.substring(optionStr.length()-9); //得到</option>因为有可能是大小写所以从optionStr中去取就是正确的 
         * String[] optionSet=optionStr.split(optionKey); 
         * for(String optionTag:optionSet) { 
         *     String optionValue=htmlParser.getAttributeValue(optionTag, "value"); 
         *     if(optionValue.equals(fdValue)){ 
         *         //如果值相等就取本option的text进行返回 
         *         return optionTag.substring(optionTag.lastIndexOf(">")+1); 
         *     }
         * }
         * return fdValue;
         */
    }

    /**
     * 编辑状态下解析,select的option标签必须是成对的<option></option>不成对时会出错
     * 
     * @param doc 数据文档对像
     * @param mStr HTML标签
     * @return 返回编辑模式的HTML标签
     */
    public String editDoc(Document doc, String mStr, String fdName, Map<String, String> fieldConfigMap) {
        //		BeanCtx.out("select=mstr="+mStr);
        HtmlParser htmlParser = (HtmlParser) BeanCtx.getBean("HtmlParser");
        String fdType = htmlParser.getAttributeValue(mStr, "exttype").toLowerCase();//先读取标签中的扩展类型，扩展类型优先
        if (Tools.isBlank(fdType)) {
            fdType = htmlParser.getAttributeValue(mStr, "type").toLowerCase(); //标签原始类型
        }
        if (Tools.isBlank(fdType) || fdType.equals("select-one")) {
            mStr = editSetSelectOneAttribute(doc, mStr, fdName, fieldConfigMap);
        }
        else if (fdType.equals("comboselect")) {
            mStr = editSetComboSelectAttribute(doc, mStr, fdName, fieldConfigMap); //普通 select combobox
        }
        else if (fdType.equals("combotree")) {
            mStr = editSetCombotreeAttribute(doc, mStr, fdName, fieldConfigMap); //select combotree
        }
        return mStr;
    }

    /**
     * 编辑状态下解析,select的option标签必须是成对的<option></option>不成对时会出错
     * 
     * @param doc 数据文档对像
     * @param mStr HTML标签
     * @return 返回编辑模式的HTML标签
     */
    public String editSetSelectOneAttribute(Document doc, String mStr, String fdName, Map<String, String> fieldConfigMap) {
        HtmlParser htmlParser = (HtmlParser) BeanCtx.getBean("HtmlParser");
        int spos = mStr.indexOf(">");
        String startSelectStr = mStr.substring(0, spos + 1); //<select name=test > 前部分

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
                String textStr = getSelectText(mStr, fdName, doc.g(fdName));
                if (attrValue.equals("HIDDEN")) {
                    return ""; //隐藏
                }
                else if (attrValue.equals("READ")) {
                    return "<span id=\"" + fdName + "\">" + textStr + "</span>"; //只读(不保存数据)
                }
                else if (attrValue.equals("READSAVE")) {
                    mStr = htmlParser.setAttributeValue(mStr, "style", "display:none");
                    return mStr + "<span id=\"" + fdName + "_show\" >" + textStr + "</span>"; //只读(需保存数据)
                }
                else if (attrValue.equals("EDIT")) {
                    //把只读模式强制删除，这样就成为可编辑的字段了
                    fieldConfigMap.remove("readtype");
                }
                else if (attrValue.equals("USEFORM")) {
                    //继承表单中的只读模式
                    String readtype = fieldConfigMap.get("readtype_old"); //得到表单字段原有的只读模式
                    fieldConfigMap.put("readtype", readtype); //把只读模式恢复到原有状态
                }
            }

            //2.1.判断只读模式,select控件不支持只读时保存数据
            attrValue = fieldConfigMap.get("readtype");
            if (attrValue != null) {
                String textStr = getSelectText(mStr, fdName, doc.g(fdName));
                if (attrValue.equals("ALL")) {
                    return "<span id=\"" + fdName + "\">" + textStr + "</span>"; //全部只读(不保存数据)
                }
                if (doc.isNewDoc()) {//新建时只读
                    if (attrValue.equals("NEW")) {
                        return "<span id=\"" + fdName + "\">" + textStr + "</span>"; //新建时只读(不保存数据)
                    }
                }
                else { //编辑时只读
                    if (attrValue.equals("EDIT")) {
                        return "<span id=\"" + fdName + "\">" + textStr + "</span>"; //新建时只读(不保存数据)
                    }
                }
            }

            //4.必填字段
            String dataoptions = htmlParser.getAttributeValue(startSelectStr, "data-options");
            attrValue = fieldConfigMap.get("required");
            if (attrValue != null) {
                dataoptions = htmlParser.setDataOptions(dataoptions, "required", "true", false);
            }

            //4.1追加必填的样式表
            if (attrValue != null) {
                String className = htmlParser.getAttributeValue(startSelectStr, "class");
                if (className.indexOf("easyui-") == -1) {
                    startSelectStr = htmlParser.appendAttributeValue(startSelectStr, "class", "easyui-validatebox", " "); //追加
                }
            }

            //5.设置为空时的验证提示消息
            attrValue = fieldConfigMap.get("valimsg");
            if (attrValue != null) {
                dataoptions = htmlParser.setDataOptions(dataoptions, "missingMessage", attrValue, true);
            }

            //6.jsevent事件
            attrValue = fieldConfigMap.get("jsevent");
            if (attrValue != null) {
                String jsfun = fieldConfigMap.get("jsfun");
                if (jsfun != null) {
                    startSelectStr = htmlParser.setAttributeValue(startSelectStr, attrValue, jsfun);
                }
            }

            //8.设置data-options属性
            if (Tools.isNotBlank(dataoptions)) {
                startSelectStr = htmlParser.setAttributeValue(startSelectStr, "data-options", dataoptions);
            }

        }
        mStr = startSelectStr + mStr.substring(spos + 1);
        mStr = setSelectValue(doc, mStr, fdName, fieldConfigMap); //设置字段的值
        return mStr;
    }

    /**
     * 编辑状态下解析comboselect类型的控件
     * 
     * @param doc 数据文档对像
     * @param mStr HTML标签
     * @return 返回编辑模式的HTML标签
     */
    public String editSetComboSelectAttribute(Document doc, String mStr, String fdName, Map<String, String> fieldConfigMap) {
        HtmlParser htmlParser = (HtmlParser) BeanCtx.getBean("HtmlParser");
        int spos = mStr.indexOf(">");
        String startSelectStr = mStr.substring(0, spos + 1); //<select name=test > 前部分
        String jscode = "";

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
                String textStr = doc.g(fdName + "_show");
                if (Tools.isBlank(textStr)) {
                    textStr = doc.g(fdName);
                }
                if (attrValue.equals("HIDDEN")) {
                    return ""; //隐藏
                }
                else if (attrValue.equals("READ")) {
                    return "<span id=\"" + fdName + "\">" + textStr + "</span>"; //只读(不保存数据)
                }
                else if (attrValue.equals("READSAVE")) {
                    mStr = htmlParser.setAttributeValue(mStr, "style", "display:none");
                    return mStr + "<span id=\"" + fdName + "_show\" >" + textStr + "</span>"; //只读(需保存数据)
                }
                else if (attrValue.equals("EDIT")) {
                    String readtype = fieldConfigMap.get("readtype_old"); //得到表单字段原有的只读模式
                    fieldConfigMap.put("readtype", readtype); //把只读模式恢复到原有状态
                }
            }

            //2.判断只读模式
            attrValue = fieldConfigMap.get("readtype");
            if (attrValue != null) {
                return htmlParser.parserReadType(doc, mStr, attrValue, fdName);
            }

            //3.必填字段
            String dataoptions = htmlParser.getAttributeValue(startSelectStr, "data-options");
            attrValue = fieldConfigMap.get("required");
            if (attrValue != null) {
                dataoptions = htmlParser.setDataOptions(dataoptions, "required", "true", false);
            }

            //4.设置为空时的验证提示消息
            attrValue = fieldConfigMap.get("valimsg");
            if (attrValue != null) {
                dataoptions = htmlParser.setDataOptions(dataoptions, "missingMessage", attrValue, true);
            }

            //设置json数据源
            attrValue = fieldConfigMap.get("url");
            if (attrValue != null) {
                String url = attrValue;
                String valuefield = fieldConfigMap.get("valuefield");
                String textField = fieldConfigMap.get("textField");
                String groupField = fieldConfigMap.get("groupField");
                String method = fieldConfigMap.get("method");
                String multiple = fieldConfigMap.get("multiple");
                String formatter = fieldConfigMap.get("formatter");
                if (Tools.isNotBlank(dataoptions)) {
                    dataoptions += ",";
                }
                dataoptions += "url:'" + url + "'";
                if (valuefield != null) {
                    dataoptions += ",valueField:'" + valuefield + "'";
                }
                if (textField != null) {
                    dataoptions += ",textField:'" + textField + "'";
                }
                if (groupField != null) {
                    dataoptions += ",groupField:'" + groupField + "'";
                }
                if (multiple != null) {
                    dataoptions += ",multiple:" + multiple;
                }
                if (method != null) {
                    dataoptions += ",method:'" + method + "'";
                }
                if (formatter != null) {
                    dataoptions += ",formatter:'" + formatter + "'";
                }
                dataoptions += ",multiple:true";
            }

            //5.jsevent事件
            attrValue = fieldConfigMap.get("onSelect");
            if (attrValue != null) {
                if (Tools.isBlank(dataoptions)) {
                    dataoptions += "onSelect:function(rc){" + attrValue + "}";
                }
                else {
                    dataoptions += ",onSelect:function(rc){" + attrValue + "}";
                }
            }

            //8.设置data-options属性
            if (Tools.isNotBlank(dataoptions)) {
                startSelectStr = htmlParser.setAttributeValue(startSelectStr, "data-options", dataoptions);
            }

        }
        mStr = startSelectStr + mStr.substring(spos + 1);
        mStr = setSelectValue(doc, mStr, fdName, fieldConfigMap) + jscode; //设置字段的值
        return mStr;
    }

    /**
     * 解析select-combotree类型的标签
     * 
     * @param doc
     * @param mStr
     * @param fieldConfigMap
     * @return
     */
    public String editSetCombotreeAttribute(Document doc, String mStr, String fdName, Map<String, String> fieldConfigMap) {
        HtmlParser htmlParser = (HtmlParser) BeanCtx.getBean("HtmlParser");
        int spos = mStr.indexOf(">");
        String startSelectStr = mStr.substring(0, spos + 1); //<select name=test > 前部分
        String jscode = "";

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

            //2.判断只读模式
            attrValue = fieldConfigMap.get("readtype");
            if (attrValue != null) {
                return htmlParser.parserReadType(doc, mStr, attrValue, fdName);
            }

            //3.必填字段
            String dataoptions = htmlParser.getAttributeValue(startSelectStr, "data-options");
            attrValue = fieldConfigMap.get("required");
            if (attrValue != null) {
                dataoptions = htmlParser.setDataOptions(dataoptions, "required", "true", false);
            }

            //4.设置为空时的验证提示消息
            attrValue = fieldConfigMap.get("valimsg");
            if (attrValue != null) {
                dataoptions = htmlParser.setDataOptions(dataoptions, "missingMessage", attrValue, true);
            }

            //5.设置json数据源
            attrValue = fieldConfigMap.get("url");
            if (attrValue != null) {
                String url = attrValue;
                if (url.substring(0, 2).equals("D_")) {
                    url = "treedata?wf_num=" + url;
                }
                url = Tools.parserStrByDocument(doc, url); //分析url中的{}中所包含的字段参数
                String valuefield = fieldConfigMap.get("valuefield");
                if (Tools.isBlank(valuefield)) {
                    valuefield = "id";
                }
                String textField = fieldConfigMap.get("textField");
                if (Tools.isBlank(textField)) {
                    textField = "text";
                }
                String groupField = fieldConfigMap.get("groupField");
                String method = fieldConfigMap.get("method");
                String formatter = fieldConfigMap.get("formatter");
                String multiple = fieldConfigMap.get("multiple");
                if (Tools.isNotBlank(dataoptions)) {
                    dataoptions += ",";
                }
                dataoptions += "lines:true,cascadeCheck:false,url:'" + url + "'";
                dataoptions += ",valueField:'" + valuefield + "'";
                dataoptions += ",textField:'" + textField + "'";
                if (groupField != null) {
                    dataoptions += ",groupField:'" + groupField + "'";
                }
                if (multiple != null) {
                    dataoptions += ",multiple:" + multiple;
                }
                if (method != null) {
                    dataoptions += ",method:'" + method + "'";
                }
                if (formatter != null) {
                    dataoptions += ",formatter:" + formatter;
                }

            }

            //6.jsevent事件
            attrValue = fieldConfigMap.get("jsevent");
            if (attrValue != null) {
                String jsfun = fieldConfigMap.get("jsfun");
                if (jsfun != null) {
                    jscode += "<script>$('#" + fdName + "').comboboxtree({" + attrValue + ": function(rc){" + jsfun + "}});</script>";
                }
            }
            //8.设置data-options属性
            if (Tools.isNotBlank(dataoptions)) {
                startSelectStr = htmlParser.setAttributeValue(startSelectStr, "data-options", dataoptions);
            }

        }
        mStr = startSelectStr + "<option value=\"RS005\" selected >RS005</option>" + mStr.substring(spos + 1);
        return mStr;
    }

    /**
     * 编辑状态下设置select的值（选中）,select的option标签必须是成对的<option></option>不成对时会出错
     * 
     * @param doc 数据文档对像
     * @param mStr HTML标签
     * @return 返回编辑模式的HTML标签
     */
    public String setSelectValue(Document doc, String mStr, String fdName, Map<String, String> fieldConfigMap) {
        //BeanCtx.p("mStr="+mStr);
        HtmlParser htmlParser = (HtmlParser) BeanCtx.getBean("HtmlParser");
        if (!doc.hasItem(fdName)) {
            return mStr;
        } //如果文档中没有些字段则直接返回，不用分析
        String fdValue = doc.g(fdName); //字段值
        //BeanCtx.out("mStr="+mStr);
        //BeanCtx.out("fdVaue="+fdValue);
        StringBuilder newmStr = new StringBuilder();
        int spos = mStr.indexOf(">");
        int epos = mStr.lastIndexOf("<");
        String startStr = mStr.substring(0, spos + 1); //<select name=test > 前部分
        String endStr = mStr.substring(epos); //</select> 后部分
        newmStr.append(startStr);
        if (fdValue.indexOf("|") != -1) {
            //说明要根据值对select进行初始化如： 是|1|selected,否|0,其他|2
            String[] vArray = Tools.split(fdValue);
            for (String v : vArray) {
                String[] tmpArray = Tools.split(v, "|");
                String optionText = tmpArray[0];
                String optionValue = tmpArray.length > 1 ? tmpArray[1] : "";
                String optionSelected = "";
                if (tmpArray.length > 2) {
                    optionSelected = tmpArray[2];
                }
                newmStr.append("<option value=\"" + optionValue + "\" " + optionSelected + " >" + optionText + "</option>");
            }
        }
        else {
            //根据值进行选中
            String optionStr = Tools.trim(mStr.substring(spos + 1, epos)); //<option value=1>是</option>
            //StringBuilder newmStr=new StringBuilder();
            //BeanCtx.out("optionStr="+optionStr);
            if (Tools.isBlank(optionStr)) {
                //没有option的情况下
                newmStr.append("<option value=\"" + fdValue + "\">" + fdValue + "</option>");
            }
            else {
                //有option的情况下
                String optionKey = optionStr.substring(optionStr.length() - 9); //得到</option>因为有可能是大小写所以从optionStr中去取就是正确的
                //BeanCtx.out("全部optionStr={"+optionStr+"}");
                String[] optionArray = optionStr.split(optionKey);
                for (String optionTag : optionArray) {
                    String optionValue = htmlParser.getAttributeValue(optionTag, "value");
                    //BeanCtx.out("optionValue="+optionValue);
                    if (optionValue.equals(fdValue)) {
                        //如果值相等就选中
                        //BeanCtx.out("找到相等的optionTag={"+optionTag+"} 值为："+optionValue+"="+fdValue);
                        optionTag = htmlParser.setAttribute(optionTag, "selected");
                        //BeanCtx.out("设置属性后optionTag={"+optionTag+"}");
                    }
                    else {
                        //不相等则取消选中
                        optionTag = htmlParser.removeAttribute(optionTag, "selected");
                    }
                    newmStr.append(optionTag + optionKey);
                }
            }
        }
        newmStr.append(endStr);
        return newmStr.toString();
    }

    /**
     * 分析获得Select标签中选中的text的值
     * 
     * @param doc
     * @param mStr
     * @param fdName
     * @return
     */
    public String getSelectText(String mStr, String fdName, String fdValue) {
        HtmlParser htmlParser = (HtmlParser) BeanCtx.getBean("HtmlParser");
        int spos = mStr.indexOf(">");
        int epos = mStr.lastIndexOf("<");
        String optionStr = Tools.trim(mStr.substring(spos + 1, epos)); //<option value=1>是</option>
        if (Tools.isBlank(optionStr)) {
            return fdValue;
        } //如果没有option则直接返回字段值

        //有option的情况下进行分析得到option的text
        String optionKey = optionStr.substring(optionStr.length() - 9); //得到</option>因为有可能是大小写所以从optionStr中去取就是正确的
        String[] optionSet = optionStr.split(optionKey);
        for (String optionTag : optionSet) {
            String optionValue = htmlParser.getAttributeValue(optionTag, "value");
            if (optionValue.equals(fdValue)) {
                //如果值相等就取本option的text进行返回
                return optionTag.substring(optionTag.lastIndexOf(">") + 1); //找到text直接返回
            }
        }
        return fdValue;
    }

}
