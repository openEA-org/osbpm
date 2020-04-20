package cn.linkey.rulelib.S003;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.form.HtmlParser;
import cn.linkey.form.ModForm;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;
import cn.linkey.wf.ProcessEngine;

/**
 * 本规则主要负责解析流程的子表单
 * 
 * 根据所有环节中配置的子表单列表读取formbody中的内容解析后输出
 * 
 * @author Administrator
 *
 */
public class R_S003_B089 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
    	ProcessEngine linkeywf = BeanCtx.getLinkeywf();
        StringBuilder formBody = (StringBuilder) params.get("FormBody"); /* 传入的表单HTML代码 */
        StringBuilder subFormBody = new StringBuilder();/* 子表单的HTML代码 */
        HtmlParser insHtmlParser = (HtmlParser) BeanCtx.getBean("HtmlParser");/* 分析html代码并填上字段内容 */

        /* 1.获得当前环节的所有子表单的HTML代码 */
        String sql = "select FormTitle,WF_OrUnid,NodeName,UserName,WF_DocCreated from BPM_SubFormData where DocUnid='" + linkeywf.getDocUnid() + "' and ReadOnly='1' order by WF_DocCreated";
        Document[] dc = Rdb.getAllDocumentsBySql(sql);
        for (Document doc : dc) {
            subFormBody.append("<span id=\"" + doc.g("WF_OrUnid") + "\"  class=\"CollapseSubForm\" onclick=\"ExpandSubFormBody(this)\" >");
            subFormBody.append(doc.g("FormTitle")).append("(").append(doc.g("NodeName")).append(" ").append(doc.g("UserName")).append(" ").append(doc.g("WF_DocCreated")).append(")");
            subFormBody.append("</span><br>\n<div id=\"SUBFORM_" + doc.g("WF_OrUnid") + "\" class=\"CollapseSubFormBody\"></div>");
        }
        //		BeanCtx.out(subFormBody.toString());

        if (!linkeywf.isReadOnly()) {
            /* 编辑状态下只有当前环节的子表单为编辑状态 */
            String subFormNumber = linkeywf.getCurrentModNodeDoc().g("SubFormNumberLoad");
            if (Tools.isNotBlank(subFormNumber)) {
                Document subFormDoc = ((ModForm) BeanCtx.getBean("ModForm")).getFormDoc(subFormNumber);
                if (!subFormDoc.isNull()) {
                    String subFormHtml = subFormDoc.g("FormBody");
                    subFormHtml = insHtmlParser.parserHtml(linkeywf.getDocument(), subFormHtml, false, false, "","1");//解析子表单填上字段内容

                    //BeanCtx.p("SubFormCollapsed="+linkeywf.getCurrentModNodeDoc().g("SubFormCollapsed"));
                    if (linkeywf.getCurrentModNodeDoc().g("SubFormCollapsed").equals("1")) { /* 看是否需要折叠子表单 */
                        String id = "CollapseSubForm_1";
                        String title = linkeywf.getCurrentModNodeDoc().g("SubFormCollapsedTitle"); /* 折叠子表单时可以在环节中指定标题 */
                        if (Tools.isBlank(title)) {
                            title = subFormDoc.g("FormName");
                        }
                        subFormBody.append("<span id=\"" + id + "\" Nodeid=\"" + linkeywf.getCurrentNodeid() + "\" class=\"ExpandSubForm\" onclick=\"ExpandSubForm(this)\" >");
                        subFormBody.append(title);
                        subFormBody.append("</span><br>\n<div id=\"SUBFORM_" + id + "\" class=\"CollapseSubFormBody\">\n");
                        subFormBody.append(subFormHtml); /* 解析后追加 */
                        subFormBody.append("\n</div>");
                    }
                    else {
                        /* 不需要折叠 */
                        subFormBody.append(subFormHtml);
                        /* 解析后追加 */
                    }
                }
            }
        }

        //BeanCtx.p("subFormBody="+subFormBody);
        /* 2.追加子表单内容到主表单中去 */
        int spos = formBody.indexOf("[SUBFORM]");
        if (spos != -1) {
            if (subFormBody.length() > 0) {
                formBody = formBody.replace(spos, spos + 9, subFormBody.toString()); //把主表单插入到子表单的指定位置
            }
            else {
                formBody = formBody.replace(spos, spos + 9, "<LISTSUBFORM></LISTSUBFORM>"); //换成空值
            }
        }
        else { /* 追加到主表单的最后 */
            formBody.append("\n<!-- SUBFORM Begin-->\n<LISTSUBFORM>\n");
            formBody.append(subFormBody);
            formBody.append("\n</LISTSUBFORM>\n<!-- SUBFORM End-->\n");
        }
        return "";
    }
}
