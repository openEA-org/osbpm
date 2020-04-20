package cn.linkey.util;

import java.util.List;
import java.util.HashMap;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import cn.linkey.app.AppUtil;
import cn.linkey.factory.BeanCtx;
import cn.linkey.util.Tools;

/**
 * 本类为单实例静态方法类
 * 
 * @author Administrator
 * 
 */
public class XmlParser {

    /**
     * 获得一个节点下所有text文本内容
     * 
     * @param item 节点对像
     * @return
     */
    public static String getElementText(Element item) {
        String getText = "";
        List<Element> itemList = item.elements();
        for (Element subItem : itemList) {
            int subSize = subItem.elements().size();
            if (subSize > 0) {
                getText += getElementText(subItem);
            }
            else {
                getText = subItem.getText();
            }
        }
        return getText;
    }

    /**
     * 把xmldata字符串转换成为hashmap对像
     * 
     * @param xml xml格式的字符串 <Items><WFItem name=\"NewField\">linkey</WFItem><WFItem name=\"MeetingAddress\">新的&amp;会议室</WFItem></Items>
     * @return 返回字段名和字段值的map对像
     */
    protected static HashMap<String, String> getXmlData(String xml) {
        String fdName, fdValue;
        HashMap<String, String> fdMap = new HashMap<String, String>();
        if (Tools.isBlank(xml)) {
            return fdMap;
        }
        Document doc = string2XmlDoc(xml);
        List<Element> list = doc.selectNodes("/Items/WFItem");
        for (Element item : list) {
            fdName = item.attribute("name").getValue();
            fdValue = item.getText();
            fdMap.put(fdName, fdValue);
        }
        return fdMap;
    }

    /**
     * string2Document 将字符串转为Document
     * 
     * @return
     * @param s xml格式的字符串
     */
    public static Document string2XmlDoc(String s) {
        Document doc = null;
        try {
            doc = DocumentHelper.parseText(s);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return doc;
    }

    /**
     * doc2String 将xml文档内容转为String
     * 
     * @return 字符串
     * @param document
     */
    protected static String doc2String(Document document) {
        String s = "";
        try {
            // 使用输出流来进行转化
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            // 使用GB2312编码
            OutputFormat format = new OutputFormat("  ", true, "UTF-8");
            XMLWriter writer = new XMLWriter(out, format);
            writer.write(document);
            s = out.toString("UTF-8");
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return s;
    }

    /**
     * doc2XmlFile 将Document对象保存为一个xml文件到本地
     * 
     * @return true:保存成功 flase:失败
     * @param filename 保存的文件名
     * @param 指定输出的xml文件内容所使用的编号默认为utf-8
     * @param document 需要保存的document对象
     */
    public static boolean doc2XmlFile(Document document, String filename, String encType) {
        boolean flag = true;
        try {
            /* 将document中的内容写入文件中 */
            // 默认为UTF-8格式，指定为"GB2312"
            if (Tools.isBlank(encType)) {
                encType = "UTF-8";
            }
            OutputFormat format = OutputFormat.createPrettyPrint();
            format.setEncoding(encType);
            XMLWriter writer = new XMLWriter(new FileOutputStream(new File(filename)), format);
            writer.write(document);
            writer.close();
        }
        catch (Exception ex) {
            flag = false;
            ex.printStackTrace();
        }
        return flag;
    }

    /**
     * string2XmlFile 将xml格式的字符串保存为本地文件，如果字符串格式不符合xml规则，则返回失败
     * 
     * @return true:保存成功 flase:失败
     * @param filename 保存的文件名
     * @param str 需要保存的字符串
     */
    public static boolean string2XmlFile(String str, String filename, String encType) {
        boolean flag = true;
        try {
            Document doc = DocumentHelper.parseText(str);
            flag = doc2XmlFile(doc, filename, encType);
        }
        catch (Exception ex) {
            flag = false;
            ex.printStackTrace();
            String filePath = AppUtil.getPackagePath() + "initdata/error.xml";
            BeanCtx.out("XmlParser.string2XmlFile()分析Xml字符串时出错,错误的xml文件已写入到" + filePath + "文件中!");
            Tools.writeStringToFile(filePath, str, "utf-8", false);
        }
        return flag;
    }

    /**
     * load 载入一个xml文档
     * 
     * @return 成功返回Document对象，失败返回null
     * @param uri 文件的全路径
     */
    public static Document load(String filename) {
        Document document = null;
        try {
            SAXReader saxReader = new SAXReader();
            document = saxReader.read(new File(filename));
        }
        catch (Exception ex) {
            BeanCtx.log(ex, "E", "导入xml文件时出错(" + filename + ")");
        }
        return document;
    }

}
