package cn.linkey.form;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import cn.linkey.app.AppElement;
import cn.linkey.dao.Rdb;
import cn.linkey.dao.RdbCache;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.util.Tools;

// 分析HTML代码并填充Document对像到HTML代码中去
// 每个标签的分析能力算作是一种规则，如input [x][/X] textarea都定义为一个能力，可以在规则中进行扩展
// 本类为单例类
public class HtmlParser {
	
	

    /**
     * 解析HTML标签并填充字段值和字段属性 注意:自已写的span类型的标签不要给name属性，否则span里面包含的内容就不会再被解析了
     * 
     * @param doc 数据文档
     * @param fieldConfigJson 字段属性配置json格式
     * @param htmlStr 要分析的HTML代码
     * @param isMainForm分析的是否是主表单
     * @param readOnly true表示只读，false表示可编辑
     * @param defaultReadType字段没有配置属性时的缺省只读模式,如果不指定则传""即可,只在在readOnly为false的情况下生有效
     * @return 返回解析好的字符串
     * 2018.1.23 修改增加uiType
     */
    @SuppressWarnings("unchecked")
    public String parserHtml(Document doc, String htmlStr, boolean isMainForm, boolean readOnly, String defaultReadType,String UIType) throws Exception {
        //long ts = System.currentTimeMillis();
        //读取字段配置信息
        HashMap<String, Map<String, String>> fdAllMap;
        if (isMainForm) {
            fdAllMap = BeanCtx.getMainFormFieldConfig(); //主表单的配置字段
        }
        else {
            fdAllMap = BeanCtx.getSubFormFieldConfig(); //子表单的配置字段
        }

        if (fdAllMap == null) {
            fdAllMap = new HashMap<String, Map<String, String>>();//说明表单中没有任何字段配置信息,给一个空值
        }
        //开始分析表单的HTML代码
        HashMap<String, HashMap<String, HashMap<String, String>>> htmlTagConfigMapAll = (HashMap<String, HashMap<String, HashMap<String, String>>>) RdbCache.getSystemCache("BPM_HtmlTagConfig", "ALL"); //含PC和Mobile全部
        HashMap<String, HashMap<String, String>> htmlTagConfigCache=null;
        if (BeanCtx.isMobile()) {
        	htmlTagConfigCache = htmlTagConfigMapAll.get("MOBILE"); //获得Mobile端的所有配置规则
        }
        else if("3".equals(UIType)){
            htmlTagConfigCache = htmlTagConfigMapAll.get("PC"); //获得PC端的所有配置规则
        }else if("1".equals(UIType)){
            htmlTagConfigCache = htmlTagConfigMapAll.get("LayUI"); //20180131获得PC端的所有配置规则
        }
        
        Set<String> tagList = htmlTagConfigCache.keySet(); //默认支持可解析的标签列表在数据库表中配制
        HashMap<String, Object> params = new HashMap<String, Object>(); //准备运行标签分析规则所需参数
        params.put("DataDocument", doc); //准备分析标签的规则传入参数
        if (readOnly) {
            params.put("ReadOnly", "1");
        }
        else {
            params.put("ReadOnly", "0");
        }
        //htmlStr=parserXTag(htmlStr); //把x标签解析成为span标签
        //htmlStr=parserFTag(htmlStr); //把f标签解析成为span标签,兼容旧版本
        StringBuilder resultHtml = new StringBuilder(htmlStr.length());
        int spos = htmlStr.indexOf(" name=");
        int max = 0;
        String mStr;
        while (spos != -1) {
            if (max > 5000)
                break;
            max++;
            //取name=左边和右边字符串
            String lStr = htmlStr.substring(0, spos + 5); //<td><input formual="1<>2"...name=
            //String rStr=htmlStr.substring(spos+5,htmlBody.length()); //取右边字符串 '不包含name=  name=("test" value=1 ></td>
            htmlStr = htmlStr.substring(spos + 5, htmlStr.length()); //取右边字符串 '不包含name=  name=("test" value=1 ></td>
            String tagName = getTagName(lStr);
            //BeanCtx.p("tagName="+tagName);
            if (tagList.contains(tagName.toLowerCase())) {
                //如果是支持的标签则计算mStr
                HashMap<String, String> tagConfig = htmlTagConfigCache.get(tagName.toLowerCase()); //读取标签的配置信息

                int lspos = lStr.lastIndexOf("<");
                String lmStr = lStr.substring(lspos, lStr.length());
                lStr = lStr.substring(0, lspos);

                String rmStr; //中间右则字符串
                
                if (tagConfig.get("IsPair").equals("0")) {
                    //不成对标签，如：input没有</input>这样结尾
                    int rspos = htmlStr.indexOf(">");
                    rmStr = htmlStr.substring(0, rspos + 1);
                    htmlStr = htmlStr.substring(rspos + 1, htmlStr.length());
                }
                else if (tagConfig.get("IsPair").equals("2")) {
                    //span标签可能存在成对的问题，要进行成对标签分析 <span name="myspan" id="myspan">外面的内容<span name="tspan">里面的内容</span></span>
                    //rStr="myspan" id="myspan">外面的内容<span name="tspan">里面的内容</span></span>
                    // 存在问题如果一个span给定了name则这个span被认为是一个整体，而不会再对span中的其他标签进行分析，所以表单中的span尽量在嵌套时不要给定name值
                    int rspos = htmlStr.indexOf("</" + tagName + ">");
                    rmStr = htmlStr.substring(0, rspos + 7);
                    if (rmStr.indexOf("<" + tagName) == -1) {
                        //说明不存在span嵌套情况
                        htmlStr = htmlStr.substring(rspos + 7, htmlStr.length());
                    }
                    else {
                        //BeanCtx.p("有嵌套进入 rmStr="+rmStr);
                        rmStr = "";
                        rspos = htmlStr.indexOf("</" + tagName + ">", rspos + 7);
                        while (rspos > 0) {
                            rmStr += htmlStr.substring(0, rspos); //这里不加7，要判断完成对后再补上</span>不然成对判断会在第一对时就成立
                            //BeanCtx.p("有嵌套再查找 rmStr="+rmStr);
                            htmlStr = htmlStr.substring(rspos + 7, htmlStr.length());
                            if (StringUtils.countMatches(rmStr, "<" + tagName) == StringUtils.countMatches(rmStr, "</" + tagName + ">")) { //如果span 和</span>的出现次数相等则说明已经成对
                                //BeanCtx.p("r成对.."+rmStr);
                                rmStr += "</" + tagName + ">"; //补上</span>
                                break;
                            }
                            else {
                                //不成对继续往后查</span>
                                //BeanCtx.p("不成对.."+rmStr);
                                rspos = htmlStr.indexOf("</" + tagName + ">");
                            }
                            rmStr += "</" + tagName + ">"; //补上</span>
                        }
                    }
                }
                else {
                    //其他标签都有<tagname></tagname>这样结尾
                    int rspos = htmlStr.indexOf("</" + tagName + ">");
                    rmStr = htmlStr.substring(0, rspos + tagName.length() + 3);
                    htmlStr = htmlStr.substring(rspos + tagName.length() + 3, htmlStr.length());
                }
                mStr = lmStr + rmStr; //中间字符串含标签
                String fdName = getAttributeValue(mStr, "name"); //获得标签名称
                params.put("HtmlStr", mStr); //要分析的html标签内容
                params.put("FieldName", fdName); //字段名称
                Map<String, String> fieldConfigMap = fdAllMap.get(fdName);//获得字段属性配置
                if (fieldConfigMap == null && Tools.isNotBlank(defaultReadType)) {
                    //给定一个缺省的只读模式属性
                    fieldConfigMap = new HashMap<String, String>();
                    fieldConfigMap.put("readtype", defaultReadType);
                }
                params.put("FieldConfig", fieldConfigMap); //传字段配置属性
                
                mStr = BeanCtx.getExecuteEngine().run(tagConfig.get("RuleNum"), params); //调用分析标签规则解析后返回
                //BeanCtx.p("mStr="+mStr);
            }
            else {
                mStr = "";
            }
            resultHtml.append(lStr); //左边不断累积
            resultHtml.append(mStr); //左边不断累积
            spos = htmlStr.indexOf(" name="); //再次计算右边字符串的name=标记进入下一次循环
        }
        resultHtml.append(htmlStr);
        resultHtml.trimToSize();
        //BeanCtx.p("输出结果****************");
        //BeanCtx.p(resultHtml.toString());
        //BeanCtx.p("输出结束****************"+max);
        //long te = System.currentTimeMillis();
        //BeanCtx.log("D","总解析字段数="+max+" 耗时="+ (te - ts));

        if (BeanCtx.isMobile()) {
            return parserTrTag(resultHtml.toString());
        }
        else {
            return resultHtml.toString();
        }

    }

    
    

    /**
     * table布局转div布局
     * 
     * @param htmlStr
     * @return
     */
    public String table2div(String htmlStr) {
        HtmlParser htmlParser = (HtmlParser) BeanCtx.getBean("HtmlParser");
        StringBuilder divStr = new StringBuilder();
        int spos = htmlStr.indexOf("<tr");
        int max = 0;
        String mStr;

        //以下代码用来取一个完整的<tr></tr>之间的mStr字符串如：mStr="<tr><td>test</td></tr>";
        while (spos != -1) {
            if (max > 5000)
                break;
            max++;

            //取<tr左边和右边字符串
            htmlStr = htmlStr.substring(spos + 3, htmlStr.length()); //取右边字符串 '不包含<tr  ><td>....

            //要进行成对分析
            int rspos = htmlStr.indexOf("</tr>");
            String rmStr = htmlStr.substring(0, rspos + 5);
            if (rmStr.indexOf("<tr") == -1) {
                //说明不存在<tr>嵌套情况
                //BeanCtx.out("没有嵌套进入 rmStr="+rmStr);
                htmlStr = htmlStr.substring(rspos + 5, htmlStr.length());
                //BeanCtx.out("没有嵌套进入 htmlStr="+htmlStr);
            }
            else {
                //				BeanCtx.out("有嵌套进入 rmStr="+rmStr);
                rmStr = "";
                rspos = htmlStr.indexOf("</tr>", rspos + 5);
                while (rspos > 0) {
                    rmStr += htmlStr.substring(0, rspos); //这里不加5，要判断完成对后再补上</tr>不然成对判断会在第一对时就成立
                    //					BeanCtx.out("有嵌套再查找 rmStr="+rmStr);
                    htmlStr = htmlStr.substring(rspos + 7, htmlStr.length());
                    if (StringUtils.countMatches(rmStr, "<tr") == StringUtils.countMatches(rmStr, "</tr>")) { //如果tr 和</tr>的出现次数相等则说明已经成对
                        //						BeanCtx.out("r成对.."+rmStr);
                        rmStr += "</tr>"; //补上</tr>
                        break;
                    }
                    else {
                        //不成对继续往后查</span>
                        //						BeanCtx.out("不成对.."+rmStr);
                        rspos = htmlStr.indexOf("</tr>");
                    }
                    rmStr += "</tr>"; //补上</span>
                }
            }

            mStr = "<tr" + rmStr; //中间字符串含标签<tr><td></td><td></td></tr>
            //			BeanCtx.out("mStr="+mStr);

            //去掉结尾的</tr>
            mStr = mStr.substring(0, mStr.indexOf("</tr>"));

            //开始分析<tr></tr>中间字符串在双数的td标
            // <tr><td>日期控件</td><td>2014-03-35</td><td>日期控件2</td><td>2014-03-35</td></tr>
            if (mStr.indexOf("</td>") != -1) { //只有最少出现4次以上时才进行分析
                String[] tdArray = StringUtils.splitByWholeSeparator(mStr, "</td>", 0);
                //				BeanCtx.out("tdArray.length="+tdArray.length);
                int cellnum = 12 / ((tdArray.length - 1) / 2); //bootstrap是固定12列，用12来除td的数得到bootstrap的列数
                //开始循环所有td
                int x = 0;
                String text = "", content = "";
                divStr.append("<div class=\"row\">");
                for (String tdStr : tdArray) {
                    if (Tools.isBlank(tdStr.trim())) {
                        continue;
                    }
                    //是文字,把<tr><td>中间的文字取出来</td>
                    int tpos = tdStr.indexOf("<td");
                    tdStr = tdStr.substring(tpos + 3, tdStr.length());
                    tpos = tdStr.indexOf(">");

                    if (x == 0) {
                        text = tdStr.substring(tpos + 1);
                        divStr.append("<div class=\"col-md-" + cellnum + "\">");
                        divStr.append("<div class=\"form-group\">");
                        divStr.append("<label class=\"control-label col-md-3\">" + text + "</label>");
                        x = 1;
                    }
                    else {
                        content = tdStr.substring(tpos + 1);
                        divStr.append("<div class=\"col-md-9\">");
                        if (content.indexOf("<input") != -1) {
                            content = htmlParser.appendAttributeValue(content, "class", "form-control", " ");
                        }
                        divStr.append(content);
                        divStr.append("</div>");
                        divStr.append("</div></div>"); //等=1时才加
                        x = 0;
                    }
                }
                divStr.append("</div>");
            }
            else {
                //tr中没有td
                divStr.append("<div class=\"row\"><div class=\"col-md-12\"></div></div>");
            }
            //td分析结束

            spos = htmlStr.indexOf("<tr"); //再次计算右边字符串的name=标记进入下一次循环
        }

        return divStr.toString();
    }

    /**
     * 移动版自动分析TR标签
     * 
     * @param htmlStr
     * @return
     */
    private String parserTrTag(String htmlStr) {
        StringBuilder resultHtml = new StringBuilder(htmlStr.length());
        int spos = htmlStr.indexOf("<tr");
        int max = 0;
        String mStr;

        while (spos != -1) {
            if (max > 5000) {
                break;
            }
            max++;

            //取<tr左边和右边字符串
            String lStr = htmlStr.substring(0, spos); //<table><tr><td>....
            htmlStr = htmlStr.substring(spos + 3, htmlStr.length()); //取右边字符串 '不包含<tr  ><td>....

            //要进行成对分析
            int rspos = htmlStr.indexOf("</tr>");
            String rmStr = htmlStr.substring(0, rspos + 5);
            if (rmStr.indexOf("<tr") == -1) {
                //说明不存在<tr>嵌套情况
                htmlStr = htmlStr.substring(rspos + 5, htmlStr.length());
            }
            else {
                rmStr = "";
                rspos = htmlStr.indexOf("</tr>", rspos + 5);
                while (rspos > 0) {
                    rmStr += htmlStr.substring(0, rspos); //这里不加5，要判断完成对后再补上</tr>不然成对判断会在第一对时就成立
                    htmlStr = htmlStr.substring(rspos + 7, htmlStr.length());
                    if (StringUtils.countMatches(rmStr, "<tr") == StringUtils.countMatches(rmStr, "</tr>")) { //如果tr 和</tr>的出现次数相等则说明已经成对
                        rmStr += "</tr>"; //补上</tr>
                        break;
                    }
                    else {
                        //不成对继续往后查</span>
                        rspos = htmlStr.indexOf("</tr>");
                    }
                    rmStr += "</tr>"; //补上</span>
                }
            }

            mStr = "<tr" + rmStr; //中间字符串含标签<tr><td></td><td></td></tr>

            //开始分析<tr></tr>中间字符串在双数的td标 
            // <tr><td>日期控件</td><td>2014-03-35</td>
            //     <td>日期控件2</td><td>2014-03-35</td></tr>
            if (mStr.indexOf("</td>") != -1) { //只有最少出现4次以上时才进行分析
                StringBuilder tdmStr = new StringBuilder();
                String[] tdArray = StringUtils.splitByWholeSeparator(mStr, "</td>", 0);
                int i = 0;
                for (String tdStr : tdArray) {

                    String colspan = this.getAttributeValue(tdStr, "colspan");
                    if (Tools.isNotBlank(colspan)) {
                        if (Integer.valueOf(colspan) > 3) {
                            tdStr = this.setAttributeValue(tdStr, "colspan", "2");
                        }
                        else {
                            tdStr = this.setAttributeValue(tdStr, "colspan", "1");
                        }
                    }

                    i++;
                    if (i % 2 != 0) {
                        tdmStr.append(tdStr).append("</td>");
                    }
                    else {
                        tdmStr.append(tdStr).append("</td></tr><tr>");
                    }
                }
                mStr = tdmStr.toString();
            }
            else {
                //看是否有colspan如果有则需要把4改为2
                String colspan = this.getAttributeValue(mStr, "colspan");
                if (Tools.isNotBlank(colspan)) {
                    if (Integer.valueOf(colspan) > 3) {
                        mStr = this.setAttributeValue(mStr, "colspan", "2");
                    }
                    else {
                        mStr = this.setAttributeValue(mStr, "colspan", "1");
                    }
                }
            }
            //td分析结束

            resultHtml.append(lStr); //左边不断累积
            resultHtml.append(mStr); //左边不断累积
            spos = htmlStr.indexOf("<tr"); //再次计算右边字符串的name=标记进入下一次循环
        }

        resultHtml.append(htmlStr);
        resultHtml.trimToSize();
        return resultHtml.toString();
    }

    //获得html标签类型
    public String getTagName(String lStr) {
        try {
            String tagName;
            int spos = lStr.lastIndexOf("<");
            int epos = lStr.indexOf(" ", spos); //lStr 后面一定有一个name= 因为是从name=开始循环的所以只需要往后取一个空格就可以得到
            tagName = lStr.substring(spos + 1, epos);
            return tagName;
        }
        catch (Exception e) {
            BeanCtx.log(e, "E", "分析标签出错=" + lStr);
            return "";
        }
    }

    /**
     * 删除HTML标签属性
     * 
     * @param htmlTag
     * @param attributeName
     * @return
     */
    public String removeAttribute(String htmlTag, String attributeName) {
        return setOrRemoveAttribute(htmlTag, attributeName, false);
    }

    /**
     * 设置HTML标签的属性与setAttributeValue的区别是此属性没有=这样的值
     * 
     * @param htmlTag
     * @param attributeName属性名
     * @return
     */
    public String setAttribute(String htmlTag, String attributeName) {
        return setOrRemoveAttribute(htmlTag, attributeName, true);
    }

    /**
     * 设置或取消html标签的属性
     * 
     * @param htmlTag
     * @param isSet true表示设置，false表示取消此属性
     * @return
     */
    private String setOrRemoveAttribute(String htmlTag, String attributeName, boolean isSet) {
        htmlTag = Tools.trim(htmlTag); //一定要取消两边的空格，不然属性插不进去
        int gpos = htmlTag.toLowerCase().indexOf(attributeName + "="); //属性有=如:disabled="true"
        if (gpos == -1) { //没有=的情况下
            int spos = htmlTag.toLowerCase().indexOf(attributeName);
            if (isSet) {//设置属性
                if (spos != -1) {
                    return htmlTag;
                }
                else {
                    spos = htmlTag.indexOf(" "); //找到第一个空格插入进去
                    if (spos == -1) {
                        spos = htmlTag.indexOf(">");
                    } //找不到空格则以>为插入点
                    return htmlTag.substring(0, spos) + " " + attributeName + " " + htmlTag.substring(spos);
                }
            }
            else { //取消属性
                if (spos == -1) {
                    return htmlTag;
                }
                else {
                    return htmlTag.replaceAll("(?i)" + attributeName, "");
                } //直接替换属性即可
            }
        }
        else { //属性是用=来设置的，要找到属性的全部值进行替换
            String newStr = "";
            String lStr = htmlTag.substring(0, gpos);
            int spos = htmlTag.indexOf(" ", gpos); //找第一个空格
            int epos = htmlTag.indexOf(">"); //找结束位
            if (spos != -1 && spos < epos) {
                //空格结束
                if (isSet) {
                    newStr = lStr + " " + attributeName + " " + htmlTag.substring(spos);
                }
                else {
                    newStr = lStr + " " + htmlTag.substring(spos);
                }
            }
            else {
                //以>结束
                if (isSet) {
                    newStr = lStr + " " + attributeName + " " + htmlTag.substring(epos);
                }
                else {
                    newStr = lStr + " " + htmlTag.substring(epos);
                }
            }
            return newStr;
        }
    }

    /**
     * 在现有属性下追加新的属性进去，如果属性不存在则会创建一个新的属性
     * 
     * @param htmlStr 要设置的HTML标签
     * @param attributeName 属性名称
     * @param attributeValue 属性值
     * @param symbol为多个属性之间的分隔符
     * @return
     */
    public String appendAttributeValue(String htmlStr, String attributeName, String attributeValue, String symbol) {
        String newStr = "";
        htmlStr = Tools.trim(htmlStr); //要去掉两端的空格，不然找第一个空格插入时会出错
        int spos = htmlStr.indexOf(attributeName + "=");
        if (spos == -1) {
            //原来没有此属性,新增加一个
            spos = htmlStr.indexOf(" "); //找到第一个空格插入进去
            newStr = htmlStr.substring(0, spos) + " " + attributeName + "=\"" + attributeValue + "\"" + htmlStr.substring(spos);
        }
        else {
            //原来就有，要替换此属性值
            String lStr = htmlStr.substring(0, spos + attributeName.length() + 1); //<input name=
            String rStr = htmlStr.substring(spos + attributeName.length() + 1); //"test" value="124" />
            String keyStr = rStr.substring(0, 1); //看属性值是以什么开头和结尾包含起来的如：name="value" name='value'  name=value>
            if (keyStr.equals("\"") || keyStr.equals("'")) {
                int epos = rStr.indexOf(keyStr, 1); //找keyStr的结束位置
                String oldAttrValue = rStr.substring(1, epos); //旧的属性值
                if (oldAttrValue.indexOf(attributeValue) == -1) {
                    newStr = lStr + "\"" + oldAttrValue + symbol + attributeValue + "\" " + rStr.substring(epos + 1);
                }
                else {
                    newStr = htmlStr; //说明旧属性中已经有了，直接返回
                }
            }
            else {
                //是以空格或者>结尾的
                spos = rStr.indexOf(" "); //空格位
                int epos = rStr.indexOf(">"); //结束位
                if (spos != -1 && spos < epos) {
                    //空格结束 <input name=test value=123 >
                    String oldAttrValue = rStr.substring(0, spos);
                    if (oldAttrValue.indexOf(attributeValue) == -1) {
                        newStr = lStr + "\"" + oldAttrValue + symbol + attributeValue + "\"" + rStr.substring(spos);
                    }
                    else {
                        newStr = htmlStr; //说明旧属性中已经有了，直接返回
                    }
                }
                else {
                    //以>结束
                    String oldAttrValue = rStr.substring(0, spos);
                    if (oldAttrValue.indexOf(attributeValue) == -1) {
                        newStr = lStr + "\"" + oldAttrValue + symbol + attributeValue + "\" " + rStr.substring(epos);
                    }
                    else {
                        newStr = htmlStr; //说明旧属性中已经有了，直接返回
                    }
                }
            }
        }
        return newStr;
    }

    /**
     * 设置html标签中的属性值设置后格式为 attrName="attrValue"
     * 
     * @param htmlStr
     * @return
     */
    public String setAttributeValue(String htmlStr, String attributeName, String attributeValue) {
        String newStr = "";
        htmlStr = Tools.trim(htmlStr); //要去掉两端的空格，不然找第一个空格插入时会出错
        int spos = htmlStr.indexOf(attributeName + "=");
        attributeValue = attributeValue.replace("\"", "&quot;"); //进行双引号转义
        if (spos == -1) {
            //原来没有此属性,新增加一个
            spos = htmlStr.indexOf(" "); //找到第一个空格插入进去
            newStr = htmlStr.substring(0, spos) + " " + attributeName + "=\"" + attributeValue + "\"" + htmlStr.substring(spos);
        }
        else {
            //原来就有，要替换此属性值
            String lStr = htmlStr.substring(0, spos + attributeName.length() + 1);
            String rStr = htmlStr.substring(spos + attributeName.length() + 1);
            String keyStr = rStr.substring(0, 1); //看属性值是以什么开头和结尾包含起来的如：name="value" name='value'  name=value>
            if (keyStr.equals("'") || keyStr.equals("\"")) {
                spos = rStr.indexOf(keyStr, 1); //找keyStr的结束位置
                newStr = lStr + "\"" + attributeValue + "\" " + rStr.substring(spos + 1);
            }
            else {
                //是以空格或者>结尾的
                spos = rStr.indexOf(" "); //空格位
                int epos = rStr.indexOf(">"); //结束位
                if (spos != -1 && spos < epos) {
                    //空格结束
                    newStr = lStr + "\"" + attributeValue + "\"" + rStr.substring(spos);
                }
                else {
                    //以>结束
                    newStr = lStr + "\"" + attributeValue + "\" " + rStr.substring(epos);
                }
            }
        }
        return newStr;
    }

    /**
     * 获得html标签中的属性值
     * 
     * @param htmlStr
     * @return
     */
    public String getAttributeValue(String htmlStr, String attributeName) {
        attributeName = " " + attributeName; //属性名称前面一定会有空格出现如：<input name="test" class="red">
        int spos = htmlStr.indexOf(attributeName + "=");
        if (spos == -1) {
            return "";
        } //没有属性返回空值
        String vStr = htmlStr.substring(spos + attributeName.length() + 1);

        String keyStr = vStr.substring(0, 1); //看属性值是以什么开头和结尾包含起来的如：name="value" name='value'  name=value>
        if (keyStr.equals("'") || keyStr.equals("\"")) {
            spos = vStr.indexOf(keyStr, 1); //找keyStr的结束位置
            vStr = vStr.substring(1, spos);
        }
        else {
            //是以空格或者>结尾的
            spos = vStr.indexOf(" "); //空格位
            int epos = vStr.indexOf(">"); //结束位
            if (spos != -1 && spos < epos) {
                //空格结束
                vStr = vStr.substring(0, spos);
            }
            else {
                //以>结束
                vStr = vStr.substring(0, epos);
            }
        }
        return vStr;
    }

    /**
     * 分析[X]标签并返回字面值，仅适用于分析jsheader和页面的Html代码
     * 
     * @param doc
     * @param htmlStr
     * @return
     */
    @SuppressWarnings("unchecked")
    public String parserXTagValue(Document doc, String htmlCode) throws Exception {
        String startCode = "[X]";
        String endCode = "[/X]";
        int spos = htmlCode.indexOf(startCode);
        if (spos == -1) {
            return htmlCode;
        } //没有x标签直接返回
        StringBuilder newHtmlStr = new StringBuilder(htmlCode.length());
        HashMap<String, String> configmap = (HashMap<String, String>) RdbCache.getSystemCache("BPM_XTagConfig", "ALL");
        HashMap<String, Object> params = new HashMap<String, Object>();
        while (spos != -1) {
            int epos = htmlCode.indexOf(endCode);
            String fdName = htmlCode.substring(spos + 3, epos);
            String lStr = htmlCode.substring(0, spos);
            htmlCode = htmlCode.substring(epos + 4, htmlCode.length());
            newHtmlStr.append(lStr);
            if (fdName.startsWith("L_")) {
                newHtmlStr.append(BeanCtx.getLabel(fdName)); //说明是国际标签
            }
            else {
                String configid = fdName;
                String xtagValue = fdName;
                int tpos = fdName.lastIndexOf(".");
                if (tpos != -1) {
                    configid = fdName.substring(0, tpos);
                    xtagValue = fdName.substring(tpos + 1);
                }
                if (configmap.containsKey(configid)) {
                    //说明有配置，直接调用配置的规则进行解析
                    String ruleNum = configmap.get(configid);
                    params.put("XTagName", fdName);//把值传入到规则中去,在规则中可以根据此参数进行进一步运算
                    params.put("XTagValue", xtagValue);//把值传入到规则中去,在规则中可以根据此参数进行进一步运算
                    String returnCode = BeanCtx.getExecuteEngine().run(ruleNum, params); //规则运行后返回的字符串
                    newHtmlStr.append(returnCode);
                }
                else {
                    newHtmlStr.append(doc.g(fdName)); //说明是普通计算标签
                }
            }
            spos = htmlCode.indexOf(startCode);
        }
        newHtmlStr.append(htmlCode);
        return newHtmlStr.toString();
    }

    /**
     * 已方法已不再使用2015.10.8 分析[APP]page.P_W001_001[/APP]标签并返回页面的内容，仅适用于页面中嵌套其他页面的代码
     * 
     * @param htmlStr 要分析的页面HTML代码
     * @return 返回分析的结果字符串
     */
    public String parserAppTagValue(String htmlCode) throws Exception {
        String startCode = "[APP]";
        String endCode = "[/APP]";
        int spos = htmlCode.indexOf(startCode);
        if (spos == -1) {
            return htmlCode;
        } //没有page标签直接返回
        StringBuilder newHtmlStr = new StringBuilder(htmlCode.length());
        AppElement appEle = null;
        while (spos != -1) {
            int epos = htmlCode.indexOf(endCode);
            String eleNum = htmlCode.substring(spos + 5, epos); //设计编号
            int x = eleNum.indexOf(".");
            String eleHtml = "";
            if (x != -1) {
                String eleType = eleNum.substring(0, x); //设计类型
                eleNum = eleNum.substring(x + 1); //设计编号
                appEle = (AppElement) BeanCtx.getBean(eleType);
                eleHtml = appEle.getElementBody(eleNum, true); //获得设计HTML内容
            }
            String lStr = htmlCode.substring(0, spos);
            htmlCode = htmlCode.substring(epos + 6, htmlCode.length());
            newHtmlStr.append(lStr);
            newHtmlStr.append(eleHtml);
            spos = htmlCode.indexOf(startCode);
        }
        newHtmlStr.append(htmlCode);
        return newHtmlStr.toString();
    }

    /**
     * 分析[JS]标签并返回值，仅适用jsheader代码在多个设计之间进行共享
     * 
     * @param htmlStr
     * @return
     */
    public String parserJsTagValue(String htmlCode) {
        String startCode = "[JS]";
        String endCode = "[/JS]";
        int spos = htmlCode.indexOf(startCode);
        if (spos == -1) {
            return htmlCode;
        } //没有x标签直接返回
        StringBuilder newHtmlStr = new StringBuilder(htmlCode.length());
        while (spos != -1) {
            int epos = htmlCode.indexOf(endCode);
            String fdName = htmlCode.substring(spos + 4, epos);
            String lStr = htmlCode.substring(0, spos);
            htmlCode = htmlCode.substring(epos + 5, htmlCode.length());
            newHtmlStr.append(lStr);
            int tpos = fdName.indexOf(".");
            if (tpos != -1) {
                String eltype = fdName.substring(0, tpos).toLowerCase();
                String elnum = fdName.substring(tpos + 1);
                String sql = "";
                if (eltype.equals("grid")) {
                    sql = "select JsHeader from BPM_GridList where GridNum='" + elnum + "'";
                }
                else if (eltype.equals("form")) {
                    sql = "select JsHeader from BPM_FormList where FormNumber='" + elnum + "'";
                }
                else if (eltype.equals("page")) {
                    sql = "select JsHeader from BPM_PageList where PageNum='" + elnum + "'";
                }
                if (Tools.isNotBlank(sql)) {
                    newHtmlStr.append(Rdb.getValueBySql(sql));
                }
            }
            spos = htmlCode.indexOf(startCode);
        }
        newHtmlStr.append(htmlCode);
        return newHtmlStr.toString();
    }

    /**
     * 获得绑定的选择器代码
     * 
     * @param fdName 字段名称
     * @param fieldConfigMap 字段配置信息
     * @return 返回选择器代码
     */
    @SuppressWarnings("unchecked")
    public String getSelector(String fdName, String attrValue) {
        HashMap<String, String> selectCache = (HashMap<String, String>) RdbCache.getSystemCache("BPM_FormSelectorConfig", "ALL");
        String selectCode = selectCache.get(attrValue);
        if (selectCode == null) {
            String sql = "select SelectorCode from BPM_FormSelectorConfig where Selectorid='" + attrValue + "'";
            selectCode = Rdb.getValueBySql(sql);
        }
        selectCode = selectCode.replace("{target}", fdName);
        return selectCode;
    }

    /**
     * 设置data-options的属性
     * 
     * @param dataoptions 已有的dataoptions字符串
     * @param attrName 属性名称
     * @param attrValue 属性值
     * @param isString 值是否是字符串类型
     * @return 返回设置好的dataoptions
     */
    public String setDataOptions(String dataoptions, String attrName, String attrValue, boolean isString) {
        int spos = dataoptions.indexOf(attrName);
        attrValue = attrValue.replace("\"", "&#34;");
        if (spos == -1) {
            //原来没有此属性
            if (Tools.isNotBlank(dataoptions)) {
                dataoptions += ",";
            }
            if (isString) {
                dataoptions += attrName + ":'" + attrValue + "'";
            }
            else {
                dataoptions += attrName + ":" + attrValue;
            }
        }
        else {
            //原来已经设置了此属性，用新的值进行替换
            spos = spos + attrName.length();
            String startStr = dataoptions.substring(0, spos); // required:true,url:'test.jsp',disabled:true
            String endStr = dataoptions.substring(spos);
            //有可能是,号或者是最后
            spos = endStr.indexOf(",");
            if (spos != -1) {
                //说明是,号结束的
                endStr = endStr.substring(spos); //endStr包含,号
            }
            else {
                //是最后的属性
                endStr = "";
            }
            if (isString) {
                dataoptions = startStr + ":'" + attrValue + "'" + endStr; //starttr不包含:符号
            }
            else {
                dataoptions = startStr + ":" + attrValue + endStr; //starttr不包含:符号
            }
        }
        return dataoptions;
    }

    /**
     * 根据字段只读模式返回只读的字段的htmlStr
     * 
     * @param doc 主数据文档
     * @param mStr 字段的HTML代码
     * @param attrValue 只读模式设置的值
     * @param fdName 字段名称
     * @return
     */
    public String parserReadType(Document doc, String mStr, String attrValue, String fdName) {

        //如果主文档中有_show的字段则使用_show的字段作为显示值
        String fdValue = doc.g(fdName + "_show");
        if (Tools.isBlank(fdValue)) {
            fdValue = doc.g(fdName);
        }

        //解决只读标签的默认值不显示的问题
        //TODO： 去掉doc.isNewDoc()，在流程表表单中，doc.isNewDoc()永远是false，在流程的init()方法已经初始化了doc
        if (Tools.isBlank(fdValue)) {
            fdValue = this.getAttributeValue(mStr, "value"); //如果是新文档获得input标签中的value值
        }

        //新建和编辑时全部只读
        if (attrValue.equals("ALL")) {
            return "<span id=\"" + fdName + "\">" + fdValue + "</span>"; //只读(不保存数据)
        }
        else if (attrValue.equals("ALLSAVE")) {
            mStr = setAttributeValue(mStr, "style", "display:none");
            return mStr + "<span id=\"" + fdName + "_show\" >" + fdValue + "</span>"; //只读(需保存数据)
        }

        if (doc.isNewDoc()) {//新建时只读
            if (attrValue.equals("NEW")) {
                return "<span id=\"" + fdName + "\">" + fdValue + "</span>"; //新建时只读(不保存数据)
            }
            else if (attrValue.equals("NEWSAVE")) {
                mStr = setAttributeValue(mStr, "style", "display:none");
                return mStr + "<span id=\"" + fdName + "_show\" >" + fdValue + "</span>"; //新建时只读(需保存数据)
            }
        }
        else { //编辑时只读
            if (attrValue.equals("EDIT")) {
                return "<span id=\"" + fdName + "\">" + fdValue + "</span>"; //编辑时只读(不保存数据)
            }
            else if (attrValue.equals("EDITSAVE")) {
                mStr = setAttributeValue(mStr, "style", "display:none");
                return mStr + "<span id=\"" + fdName + "_show\" >" + fdValue + "</span>"; //编辑时只读(需保存数据)
            }
        }
        return mStr;
    }
    
    /**
	 * 
	 * @Description: 获取一段字符串中某个css样式的值
	 *
	 * @param: mStr 一段字符串
	 * @param: property 某个样式，如display
	 * @return：返回对应的css样式，例如：“display：none”
	 */
    //20180915 add by alibao 
	public static String getStyleCSS(String mStr,String property){
		
		int i = mStr.indexOf(property + ":");
	    if(i != -1){
	    	String tempStr = mStr.substring(i, mStr.length());
	    	if(tempStr.indexOf(";") != -1){
	    		return tempStr.substring(0, tempStr.indexOf(";"));
	    	}else{
	    		return tempStr.substring(0, tempStr.length());
	    	}
	    }
		return "";
	}

}
