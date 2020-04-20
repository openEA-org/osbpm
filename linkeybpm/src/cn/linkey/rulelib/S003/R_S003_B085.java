package cn.linkey.rulelib.S003;

import java.util.HashMap;
import java.util.Map;

import cn.linkey.app.AppElement;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.form.DataDictionary;
import cn.linkey.form.HtmlParser;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;
import cn.linkey.wf.Remark;

/**
 * 本规则负责分析span标签 
 * 自定义的span标签不要给name=属性，否则里面的内空不会被递归循环解析 
 * 注意事项：<span name="test"><input name="test222"></span> 
 * span标签里面的将不会被解析，因为span中的内容被看成字符串进行处理。
 * 
 * @author Administrator
 *
 */
public class R_S003_B085 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
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
    private String readDoc(Document doc, String mStr, String fdName, Map<String, String> fieldConfigMap) throws Exception {
        HtmlParser htmlParser = (HtmlParser) BeanCtx.getBean("HtmlParser");
        String sType = htmlParser.getAttributeValue(mStr, "type");
        //		BeanCtx.out("sType="+sType);
        if (sType.equals("file")) {
            return "<div id=\"" + fdName + "\" class=\"attachmentlist\" readtype=\"true\"  ></div>";
        }
        else if (sType.equals("xlabel")) { //国际标签
            if (fieldConfigMap != null) {
                String labelid = fieldConfigMap.get("labelid");
                if (labelid != null) {
                    mStr = BeanCtx.getLabel(labelid);
                }
                else {
                    mStr = labelid;
                }
            }
            else {
                mStr = fdName;
            }
            return mStr;
        }
        else if (sType.equals("remark")) {
            //说明是显示办理意见
            int spos = mStr.indexOf(">");
            String lStr = mStr.substring(0, spos + 1); //<span name='test' type='xfiled' >
            String remarkType = htmlParser.getAttributeValue(lStr, "remarktype"); //意见类型
            String remarkTable = doc.g("WF_Status").equals("Current") ? "BPM_InsRemarkList" : "BPM_ArchivedRemarkList";
            Remark remark = (Remark) BeanCtx.getBean("Remark");
            String spanValue = remark.getRemarkList(doc.getDocUnid(), remarkType, remarkTable, "0");
            return spanValue; //这里不再返回span标签，如果需要有id包围可以在外面增加div标签
        }
        else if (sType.equals("view")) {
            //显示视图
            String spanValue = "";
            if (fieldConfigMap == null) {
                return spanValue;
            } //视图没有配置字段信息时返回空值
            String gridNum = fieldConfigMap.get("view"); //视图编号
            if (gridNum != null) {
                String beanid = "view", viewKey = "";
                int spos = gridNum.lastIndexOf("_");
                viewKey = gridNum.substring(spos + 1, spos + 2);
                if (viewKey.equals("G")) {
                	//beanid = "view";
                    beanid = "layui_view"; //20180418
                }
                else if (viewKey.equals("E")) {
                    beanid = "layui_editorgrid"; //20180418
                }
                else if (viewKey.equals("T")) {
                    beanid = "treegrid";
                }
                else if (viewKey.equals("C")) {
                    beanid = "categorygrid";
                }
                else if (viewKey.equals("M")) {
                    beanid = "customgrid";
                }
                String useiframe = fieldConfigMap.get("useiframe");
                if (useiframe != null && useiframe.equals("iframe")) {
                    //使用iframe来显示
                    StringBuilder spanHtml = new StringBuilder();

                    //准备参数
                    String urlArg = Tools.parserStrByDocument(doc, fieldConfigMap.get("UrlArg"));
                    if (Tools.isNotBlank(urlArg)) {
                        urlArg = "&" + urlArg;
                    }

                    String htmlAttr = fieldConfigMap.get("HtmlAttr");
                    if (Tools.isBlank(htmlAttr)) {
                        htmlAttr = "style='width:100%;height:100%;border:0'";
                    }

                    //生成iframe代码
                    String viewurl = "r?wf_num=" + gridNum + "&wf_action=read&wf_docunid=" + doc.getDocUnid() + urlArg;
                    spanHtml.append("<iframe id=\"").append(fdName).append("\" src='").append(viewurl).append("'").append(htmlAttr).append("></iframe>");
                    spanValue = spanHtml.toString();
                }
                else {
                    //直接插入html代码时的模式
                    AppElement appElement = (AppElement) BeanCtx.getBean(beanid);
                    spanValue = appElement.getElementBody(gridNum, true); //返回只读状态的视图Html代码
                }
            }
            return spanValue;
        }
        else if (sType.equals("xfield")) {
            String fdValue = doc.g(fdName);
            if (fieldConfigMap != null) {
                String dataid = fieldConfigMap.get("dataDic");
                if (dataid != null) {
                    fdValue = DataDictionary.getValueByDataid(dataid);
                }
            }
            return "<span id=\"" + fdName + "\" >" + fdValue + "</span>";
        }
        else if (sType.equals("page")) {
            //页面没有配置字段信息时返回空值
            if (fieldConfigMap == null) {
                return "";
            }
            AppElement appElement = (AppElement) BeanCtx.getBean("page");
            String pageNum = fieldConfigMap.get("page"); //页面编号
            return appElement.getElementBody(pageNum, true);
        }
        else {
            return mStr;
        }
    }

    /**
     * 编辑状态下解析
     * 
     * @param doc 数据文档对像
     * @param mStr HTML标签
     * @return 返回编辑模式的HTML标签
     */
    private String editDoc(Document doc, String mStr, String fdName, Map<String, String> fieldConfigMap) throws Exception {
        HtmlParser htmlParser = (HtmlParser) BeanCtx.getBean("HtmlParser");
        int spos = mStr.indexOf(">");
        String lStr = mStr.substring(0, spos + 1); //<span name='test' type='xfiled' >
        String sType = htmlParser.getAttributeValue(lStr, "type");
        String rStr = mStr.substring(mStr.length() - 7); //</span>
        String spanValue = "";
        if (sType.equals("xfield")) {
            //计算文档字段<span id="xfield" name="xfield" type="xfield">{xfield}</span>
            String fdValue = doc.g(fdName);
            if (fieldConfigMap != null) {
                String dataid = fieldConfigMap.get("dataDic");
                if (dataid != null) {
                    fdValue = DataDictionary.getValueByDataid(dataid);
                }
            }
            mStr = lStr + fdValue + rStr;
        }
        else if (sType.equals("file")) {
            mStr = editForSpanFile(doc, fdName, fieldConfigMap);//附件上载框
        }
        else if (sType.equals("remark")) {
            //说明是显示办理意见
            String remarkType = htmlParser.getAttributeValue(lStr, "remarktype"); //意见类型
            String remarkTable = doc.g("WF_Status").equals("Current") ? "BPM_InsRemarkList" : "BPM_ArchivedRemarkList";
            Remark remark = (Remark) BeanCtx.getBean("Remark");
            spanValue = remark.getRemarkList(doc.getDocUnid(), remarkType, remarkTable, "0");
            mStr = lStr + spanValue + rStr;
        }
        else if (sType.equals("view")) {
            if (fieldConfigMap != null) {
                mStr = editForSpanView(doc, fdName, fieldConfigMap, lStr, rStr);//显示视图
            }
        }
        else if (sType.equals("page")) {
            if (fieldConfigMap != null) {
                AppElement appElement = (AppElement) BeanCtx.getBean("page");
                String pageNum = fieldConfigMap.get("page"); //页面编号
                if (pageNum != null) {
                    mStr = lStr + appElement.getElementBody(pageNum, true) + rStr;
                }
            }
        }
        else if (sType.equals("xlabel")) { //国际标签
            if (fieldConfigMap != null) {
                String labelid = fieldConfigMap.get("labelid");
                if (labelid != null) {
                    mStr = BeanCtx.getLabel(labelid);
                }
                else {
                    mStr = labelid;
                }
            }
            else {
                mStr = fdName;
            }
        }
        return mStr;
    }

    private String editForSpanView(Document doc, String fdName, Map<String, String> fieldConfigMap, String lStr, String rStr) throws Exception {
        //显示视图
        if (fieldConfigMap == null) {
            return "";
        }
        String gridNum = fieldConfigMap.get("view"); //视图编号
        if (gridNum == null) {
            return "";
        }
        String beanid = "view", viewKey = "";
        int spos = gridNum.lastIndexOf("_");
        viewKey = gridNum.substring(spos + 1, spos + 2);
        if (viewKey.equals("G")) {
            //beanid = "view";
        	beanid = "layui_view";  //20180223修改
        }
        else if (viewKey.equals("E")) {
            beanid = "layui_editorgrid";
        }
        else if (viewKey.equals("T")) {
            beanid = "treegrid";
        }
        else if (viewKey.equals("C")) {
            beanid = "categorygrid";
        }
        else if (viewKey.equals("M")) {
            beanid = "customgrid";
        }

        //判断视图的只读属性
        boolean readOnly = false; //默认为可编辑
        String readtype = fieldConfigMap.get("readtype"); //表单中设定的只读模式或者流程中默认给的ALL只读模式
        String nodeFdAcl = fieldConfigMap.get("NodeFdAcl");//环节中指定的字段权限
        //BeanCtx.out("readtype="+readtype+" nodeFdAcl="+nodeFdAcl);
        if (Tools.isNotBlank(nodeFdAcl)) {//说明在环节中有设定以环节中的设定权限为准
            //隐藏
            if (nodeFdAcl.equals("HIDDEN")) {
                return "";
            }
            else if (nodeFdAcl.equalsIgnoreCase("EDIT")) { //可编辑
                readOnly = false;
            }
            else {
                readOnly = true; //只读模式
            }
        }
        else if (readtype != null) { //使用只读模式来决定
            //全部只读，在流程环节中默认为全部只读
            if (readtype.equals("ALL")) {
                readOnly = true;
            }
            else {
                if (doc.isNewDoc() && readtype.equalsIgnoreCase("NEW")) { //新建时只读
                    readOnly = true;
                }
                else if (readtype.equalsIgnoreCase("EDIT")) { //编辑时只读
                    readOnly = true;
                }
            }
        }
        //只读模式判断结束

        //开始判断显示模式是insertHtml还是用iframe来显示
        // BeanCtx.out("fieldConfigMap="+fieldConfigMap);
        String useiframe = fieldConfigMap.get("useiframe2");
        if (useiframe == null || useiframe.equals("iframe")) {
            //使用iframe来显示
            StringBuilder spanValue = new StringBuilder();

            //准备参数
            String urlArg = Tools.parserStrByDocument(doc, fieldConfigMap.get("UrlArg"));
            if (Tools.isNotBlank(urlArg)) {
                urlArg = "&" + urlArg;
            }

            String htmlAttr = fieldConfigMap.get("HtmlAttr");
            if (Tools.isBlank(htmlAttr)) {
                htmlAttr = "style='width:100%;height:100%;border:0'";
            }
            String action = "edit";
            if (readOnly) {
                action = "read";
            }
            String viewurl = "r?wf_num=" + gridNum + "&wf_action=" + action + "&wf_docunid=" + doc.getDocUnid() + urlArg;
            spanValue.append("<iframe frameborder='0' name='").append(fdName).append("' id=\"").append(fdName).append("\" src='").append(viewurl).append("'").append(htmlAttr).append("></iframe>");
            return spanValue.toString();
        }
        else {
            //直接插入视图的html代码
            AppElement appElement = (AppElement) BeanCtx.getBean(beanid);
            // spanValue.append(lStr).append(appElement.getElementBody(gridNum,readOnly)).append(rStr);
            // return spanValue.toString();
            return appElement.getElementBody(gridNum, readOnly);
        }
    }

    /**
     * 
    * @Description: 附件上载框解析
    *
    * @param:文本对象，控件名称，控件属性map
    * @return：返回附件上载对应html
    * @author: alibao
    * @date: 2018年3月21日 下午2:24:18
     */
    private String editForSpanFile(Document doc, String fdName, Map<String, String> fieldConfigMap) {
        String url = "rule?wf_num=R_S004_B005&FdName=" + fdName +  "&appId=" + BeanCtx.getAppid();
        String iFrameHeight = "38px";
        String uploadFrame = "<iframe name=\"fileframe_" + fdName + "\" id=\"fileframe_" + fdName + "\" src=\"" + url + "\" frameborder='0' height=\"" + iFrameHeight + "\" width=\"180px\"  ></iframe>";

        //1.首先检测环节中设 定的只读模式
        boolean isNodeConfig = false;
        if (BeanCtx.getLinkeywf() != null) {
            if (fieldConfigMap != null) {
                String attrValue = fieldConfigMap.get("NodeFdAcl");
                if (Tools.isNotBlank(attrValue)) {
                    isNodeConfig = true;
                    if (attrValue.equals("HIDDEN")) {
                        return ""; //隐藏
                    }
                    else if (attrValue.equals("READ") || attrValue.equals("READALL")) {
                        uploadFrame = "";
                    }
                }
            }
        }

        //2.检测表单中设定的只读模式
        if (isNodeConfig == false && fieldConfigMap != null) {
            String attrValue = fieldConfigMap.get("readtype");
            if (attrValue != null) {
                //新建和编辑时全部只读
                if (attrValue.equals("ALL") || attrValue.equals("ALLSAVE")) {
                    uploadFrame = "";
                }
                if (doc.isNewDoc()) {
                    //新建时只读
                    if (attrValue.equals("NEW") || attrValue.equals("NEWSAVE")) {
                        uploadFrame = "";
                    }
                }
                else {
                    //编辑时只读
                    if (attrValue.equals("EDIT") || attrValue.equals("EDITSAVE")) {
                        uploadFrame = "";
                    }
                }
            }
        }

        if (Tools.isBlank(uploadFrame)) {
            return "<div id=\"" + fdName + "\" readtype=\"true\"  class=\"attachmentlist\"  ></div>";
        }
        else {
            return uploadFrame + "<div id=\"" + fdName + "\" readtype=\"false\"  class=\"attachmentlist\"  ></div>";
        }
    }

}
