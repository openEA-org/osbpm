package cn.linkey.rulelib.S001;

import java.util.*;
import cn.linkey.dao.Rdb;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.form.FormDesigner;

/**
 * @RuleName:获得表单所有控件属性的JS
 * @author admin
 * @version: 8.0
 * @Created: 2014-04-19 22:19
 */
final public class R_S001_B042 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) {
        //params为运行本规则时所传入的参数
    	//20180206添加对UI类型的判断
    	String uiType = BeanCtx.getRequest().getParameter("uiType"); //UI 类型
    	String formHtmlBpdyJs = "Form_HtmlBodyJs";
    	String sql0 = "select Form_HtmlBodyJs from BPM_UIList where UIType='"+uiType+"'";
        formHtmlBpdyJs = Rdb.getValueBySql(sql0);

//    	if("1".equals(uiType)){
//    		formHtmlBpdyJs = "layui_Form_HtmlBodyJs";
//    	}
        String sql = "select DefaultCode from BPM_DevDefaultCode where CodeType='"+formHtmlBpdyJs+"'";
        String jsHeader = Rdb.getValueBySql(sql);
        //jsHeader=jsHeader.replace("\n","");
        //jsHeader=jsHeader.replace("\r","");
        BeanCtx.p(jsHeader);

        FormDesigner formDesigner = (FormDesigner) BeanCtx.getBean("FormDesigner");
        String jsonStr = formDesigner.getFormAttributeJson();
        jsonStr = jsonStr.replace("\n", "");
        jsonStr = jsonStr.replace("\r", "");
        jsonStr = jsonStr.replace("{WF_Appid}", BeanCtx.g("WF_Appid", true));
        BeanCtx.p(jsonStr); //所有属性的json对像

        String jsStr = formDesigner.getFormControlJsCode(uiType);
        jsStr = jsStr.replace("\n", "");
        jsStr = jsStr.replace("\r", "");
        jsStr = jsStr.replace("{WF_Appid}", BeanCtx.g("WF_Appid", true));
        BeanCtx.p(jsStr); //所有控件的js代码

        return "";
    }
}