package cn.linkey.rulelib.S003;

import java.util.HashMap;

import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.form.HtmlParser;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.wf.ProcessEngine;

/**
 * 本rule主要获得流程表单的中间主要部分并进行解析
 * 
 * @author Administrator
 *
 */
public class R_S003_B088 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        ProcessEngine linkeywf = BeanCtx.getLinkeywf();
        Document formDoc = (Document) params.get("FormDoc"); /* 传入的表单模型文档对像 */
        StringBuilder mainFormBody = new StringBuilder(5000);/* 主表单的HTML代码 */

        /* 1.获得表单设计的HTML代码 */
        mainFormBody.append(formDoc.g("FormBody")); /* 表单模型中取 */

        /* 2.分析html代码并填上字段内容 */
        HtmlParser insHtmlParser = (HtmlParser) BeanCtx.getBean("HtmlParser");

        //3.看是否有强制主表单只读的选项
        boolean readOnly = linkeywf.isReadOnly();
        if (linkeywf.getCurrentModNodeDoc() != null) {
            if (linkeywf.getCurrentModNodeDoc().g("MainFormAllReadOnly").equals("1")) {
                readOnly = true;
            }
        }

        //4.看当前环节中有没有指定缺省字段为只读
        String defaultReadType = "";
        if (formDoc.g("FormType").equals("2")) {
            if (linkeywf.getCurrentModNodeDoc() != null) {
                if (linkeywf.isFirstNode() == false) {
                    //首环节不能设置默认只读
                    if (!linkeywf.getCurrentModNodeDoc().g("MainFormReadOnly").equals("1")) {
                        defaultReadType = "ALL";
                    }
                }
            }
        }

        Document document = linkeywf.getDocument();
        //修改 2018.03.1 添加formDoc.g("UIType"))
        mainFormBody = new StringBuilder(insHtmlParser.parserHtml(document, mainFormBody.toString(), true, readOnly, defaultReadType,formDoc.g("UIType")));
        mainFormBody.trimToSize();
        return insHtmlParser.parserXTagValue(document, mainFormBody.toString());
    }

}
