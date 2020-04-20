package cn.linkey.rulelib.S001;

import java.util.*;
import cn.linkey.rule.LinkeyRule;
import javax.servlet.*;
import cn.linkey.factory.BeanCtx;
import cn.linkey.dao.Rdb;
import cn.linkey.util.Tools;
import cn.linkey.doc.Document;
import cn.linkey.form.FormDesigner;

/**
 * @RuleName:表单设计器
 * @author admin
 * @version: 8.0
 * @Created: 2014-07-01 14:27
 * @change  20180202 添加对UI类型的判断
 */
final public class R_S001_B064 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        //params为运行本规则时所传入的参数

        String docunid = BeanCtx.g("wf_eldocunid");
        String sqlTableName = "BPM_FormList";
        String formNumber = "";
        
        String uiType = "3";  //默认是EasyUI类型
        Document formDoc = null;
        if (Tools.isBlank(docunid)) {
            BeanCtx.p("Error : wf_docunid cannot be empty! WF_OrUnid=" + docunid);
            return "";
        }
        else {
            String sql = "select * from " + sqlTableName + " where WF_OrUnid='" + docunid + "'";
            formDoc = Rdb.getDocumentBySql(sqlTableName, sql); //得到表单文档数据
            formNumber = formDoc.g("FormNumber"); //表单编号
            uiType = formDoc.g("UIType");  //表单UI类型
            
            if (formDoc.isNull()) {
                BeanCtx.p("Error : Could not find the form! WF_OrUnid=" + docunid);
                return "";
            }
        }
        if (formDoc.g("UseCodeMode").equals("1")) {
            //重定向到代码模式
            RequestDispatcher rd = BeanCtx.getRequest().getRequestDispatcher("form_htmlcode.jsp");
            rd.forward(BeanCtx.getRequest(), BeanCtx.getResponse());
            BeanCtx.close();
            return "";
        }

        FormDesigner appFormDesigner = (FormDesigner) BeanCtx.getBean("FormDesigner");
        StringBuilder htmlBody = new StringBuilder();

        htmlBody.append("\r\n");
        htmlBody.append("<!DOCTYPE html><html><head><title>Form Attribute</title>\r\n");
        htmlBody.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\r\n");
        htmlBody.append("<script>\r\n");
        htmlBody.append("var formNumber=\"");
        htmlBody.append(formNumber);
        htmlBody.append("\";\r\n");
        htmlBody.append("var WF_Appid=\"");
        htmlBody.append(formDoc.g("WF_Appid"));
        htmlBody.append("\";\r\n");
        htmlBody.append("</script>\r\n");
        htmlBody.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"linkey/bpm/easyui/themes/gray/easyui.css\">\r\n");
        htmlBody.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"linkey/bpm/easyui/themes/icon.css\">\r\n");
        htmlBody.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"linkey/bpm/css/app_openform.css\">\r\n");
        htmlBody.append("<script type=\"text/javascript\" src=\"linkey/bpm/easyui/jquery.min.js\"></script>\r\n");
        htmlBody.append("<script type=\"text/javascript\" src=\"linkey/bpm/easyui/jquery.easyui.min.js\"></script>\r\n");
        htmlBody.append("<script type=\"text/javascript\" src=\"linkey/bpm/jscode/sharefunction.js\"></script>\r\n");
        htmlBody.append("<script type=\"text/javascript\" src=\"linkey/bpm/jscode/app_openform.js\"></script>\r\n");
        htmlBody.append("<script type=\"text/javascript\" src=\"rule?wf_num=R_S001_B042&WF_Appid=" + formDoc.g("WF_Appid") + "&uiType="+ formDoc.g("UIType"));
        htmlBody.append(formDoc.g("WF_Appid"));
        htmlBody.append("\"></script>\r\n");
        htmlBody.append("</head>\r\n");
        htmlBody.append("<body  style=\"margin:1px;overflow:hidden\"  >\r\n");
        htmlBody.append(" <div class=\"easyui-layout\" id=\"panel\" fit=\"true\" style=\"width:100%;\">\r\n");
        htmlBody.append("    \t<div data-options=\"region:'north',split:true\" style=\"height:39px\">\r\n");
        htmlBody.append("\t\t\t<div style=\"padding:2px;border:1px solid #ddd;background:#f4f4f4\">\r\n");
        htmlBody.append("\t\t\t\t| <a href=\"#\" class=\"easyui-linkbutton\" plain=\"true\" data-options=\"iconCls:'icon-edit'\" onclick=\"gotoCodeDesigner();\">切换到纯代码模式</a>\r\n");
        htmlBody.append("    \t\t\t| <a href=\"#\" class=\"easyui-linkbutton\" data-options=\"plain:true,iconCls:'icon-reload'\" onclick=\"location.reload();\">");
        htmlBody.append(BeanCtx.getMsg("Designer", "Refresh"));
        htmlBody.append("</a>\r\n");
        htmlBody.append("\t\t\t</div>\r\n");
        htmlBody.append("\t\t</div>\r\n");
        htmlBody.append("\r\n");
        htmlBody.append("        <div data-options=\"region:'west',split:true\"  style=\"width:150px;\">\r\n");
        htmlBody.append("        \t    <div class=\"easyui-accordion\" data-options=\"fit:true,border:false\">\r\n");
        htmlBody.append("                <div title=\"基础控件\" data-options=\"selected:true\" style=\"padding:5px;\">\r\n");
        htmlBody.append("                ");
        htmlBody.append(appFormDesigner.getFormControlHtmlCode("1",uiType));
        htmlBody.append("\r\n");
        htmlBody.append("                </div>\r\n");
        htmlBody.append("                \r\n");
        htmlBody.append("                <div title=\"流程相关控件\"  style=\"padding:5px;\">\r\n");
        htmlBody.append("              \t");
        htmlBody.append(appFormDesigner.getFormControlHtmlCode("2",uiType));
        htmlBody.append("\r\n");
        htmlBody.append("                </div>\r\n");
        htmlBody.append("                \r\n");
        htmlBody.append("                <div title=\"自定义控件\" style=\"padding:5px\">\r\n");
        htmlBody.append("                    ");
        htmlBody.append(appFormDesigner.getFormControlHtmlCode("3",uiType));
        htmlBody.append("\r\n");
        htmlBody.append("                </div>\r\n");
        htmlBody.append("                \r\n");
        htmlBody.append("                <div title=\"数据表字段\" style=\"padding:0px\" class=\"easyui-tree\" id=\"fdtreepanel\" data-options=\"url:'../rule?wf_num=R_S001_B013&wf_docunid=");
        htmlBody.append(docunid);
        htmlBody.append(
                "',method:'get',animate:false,dnd:false,lines:true,onDblClick:function(node){insertText(node.text);},onContextMenu: function(e,node){e.preventDefault();$(this).tree('select',node.target);$('#treefdmm').menu('show',{left: e.pageX,top: e.pageY});}\"  >\r\n");
        htmlBody.append("                    <div id=\"treefdmm\" class=\"easyui-menu\" style=\"width:120px;\">\r\n");
        htmlBody.append("        \t\t\t\t<div onclick=\"var node = $('#fdtreepanel').tree('getSelected');insertText(node.text)\" data-options=\"iconCls:'icon-form-input'\">插入文本框</div>\r\n");
        htmlBody.append("       \t\t\t\t\t<div onclick=\"var node = $('#fdtreepanel').tree('getSelected');insertCheckbox(node.text)\" data-options=\"iconCls:'icon-form-input'\">插入复选框</div>\r\n");
        htmlBody.append("       \t\t\t\t\t<div onclick=\"var node = $('#fdtreepanel').tree('getSelected');insertRadio(node.text)\" data-options=\"iconCls:'icon-form-input'\">插入单选框</div>\r\n");
        htmlBody.append("       \t\t\t\t\t<div onclick=\"var node = $('#fdtreepanel').tree('getSelected');insertDateonly(node.text)\" data-options=\"iconCls:'icon-form-input'\">日期控件</div>\r\n");
        htmlBody.append("       \t\t\t\t\t<div onclick=\"var node = $('#fdtreepanel').tree('getSelected');insertSelect(node.text)\" data-options=\"iconCls:'icon-form-input'\">插入列表框</div>\r\n");
        htmlBody.append("       \t\t\t\t\t<div onclick=\"var node = $('#fdtreepanel').tree('getSelected');insertTextarea(node.text)\" data-options=\"iconCls:'icon-form-input'\">多行文本框</div>\r\n");
        htmlBody.append("       \t\t\t\t\t<div onclick=\"var node = $('#fdtreepanel').tree('getSelected');insertxfield(node.text)\" data-options=\"iconCls:'icon-form-input'\">插入计算文本</div>\r\n");
        htmlBody.append("    \t\t\t\t</div>\r\n");
        htmlBody.append("                </div>\r\n");
        htmlBody.append("                \r\n");
        htmlBody.append("            </div>\r\n");
        htmlBody.append("        </div>\r\n");
        htmlBody.append("        \r\n");
        htmlBody.append("        <div region=\"center\" border=\"true\" fit=\"false\"  >\r\n");
        htmlBody.append("        \t<div class=\"easyui-layout\" id=\"centerpanel\" fit=\"true\" style=\"width:100%;\">\r\n");
        htmlBody.append("        \t\r\n");
        htmlBody.append("        \t<div data-options=\"region:'north',split:true\" style=\"height:800px\">\r\n");
        htmlBody.append("        \t\t<form  method='post' name='linkeyform' id=\"linkeyform\" >\r\n");
        htmlBody.append("        \t\t<textarea id=\"FieldConfig\" name=\"FieldConfig\" style=\"display:none;width:800px;height:200px\" >");
        htmlBody.append(formDoc.g("FieldConfig"));
        htmlBody.append("</textarea>\r\n");
        htmlBody.append(" \t\t\t\t<textarea  id=\"FormBody\" name=\"FormBody\" style=\"width:1px;height:1px\" >");

        String formBody = formDoc.g("FormBody");
        formBody = formBody.replace("<", "&lt;");
        formBody = formBody.replace(">", "&gt;");
        htmlBody.append(formBody);

        htmlBody.append("</textarea>\r\n");
        htmlBody.append(" \t\t\t\t<input name='WF_Action' id=\"WF_Action\" value=\"\" type=\"hidden\" >\r\n");
        htmlBody.append("\t\t\t\t<input name='WF_DocUnid' id=\"WF_DocUnid\" value=\"");
        htmlBody.append(docunid);
        htmlBody.append("\" type=\"hidden\" >\r\n");
        htmlBody.append(" \t\t\t\t</form>\r\n");
        htmlBody.append("\t\t\t</div>\r\n");
        htmlBody.append("\t\t\t\r\n");
        htmlBody.append("\t\t\t<div data-options=\"region:'south',split:true\" style=\"height:60px;overflow:hidden\">\r\n");
        htmlBody.append("\t\t\t\t<textarea id=\"htmlcode\" style=\"height:26px;width:100%\" ></textarea>\r\n");
        htmlBody.append("\t\t\t\t<a href=\"#\" onclick=\"editEditorHtml();return false;\" title=\"Change html\" ><img src=\"../linkey/bpm/images/icons/sok.gif\" border=\"0\"></a>\r\n");
        htmlBody.append("\t\t\t</div>\r\n");
        htmlBody.append("\t\t\t\r\n");
        htmlBody.append("\t\t\t</div>\r\n");
        htmlBody.append(" \t\t</div>\r\n");
        htmlBody.append("\r\n");
        htmlBody.append("\t\t<div data-options=\"region:'east',split:true\" title=\"控件属性\" style=\"width:280px;\">\r\n");
        htmlBody.append("\t\t \t<table id=\"elpg\" class=\"easyui-propertygrid\" data-options=\"\r\n");
        htmlBody.append("                columns:[[{field:'name',title:'属性名',width:80,sortable:true},{field:'value',title:'属性值',width:180,sortable:true}]],\r\n");
        htmlBody.append("                showGroup:true,\r\n");
        htmlBody.append("                fit:true,\r\n");
        htmlBody.append("                scrollbarSize:0\r\n");
        htmlBody.append("            \">\r\n");
        htmlBody.append("    \t\t</table>\r\n");
        htmlBody.append("\t\t</div>\r\n");
        htmlBody.append("\t\t\r\n");
        htmlBody.append("</div>\r\n");
        htmlBody.append("</body></html>\r\n");
        BeanCtx.p(htmlBody.toString());

        return "";
    }
}
