package cn.linkey.rulelib.S017;

import java.util.*;

import cn.linkey.dao.*;
import cn.linkey.util.*;
import cn.linkey.doc.*;
import cn.linkey.factory.*;
import cn.linkey.form.ModForm;
import cn.linkey.wf.*;
import cn.linkey.rest.RestUtil;
import cn.linkey.rule.LinkeyRule;

/**
 * @RuleName:Rest_Form查询流程表单的html代码
 * @author  admin
 * @version: 8.0
 * @Created: 2014-07-10 21:52
 */
final public class R_S017_B142 implements LinkeyRule {
	@Override
	public String run(HashMap<String, Object> params) throws Exception  {
	    //params为运行本规则时所传入的参数
        String processid =BeanCtx.g("processId"); //流程id
        String docUnid = BeanCtx.g("docUnid"); //实例id

        
        if(Tools.isBlank(processid)){return RestUtil.formartResultJson("0", "processId不能为空");}

		//初始化引擎
		ProcessEngine linkeywf=BeanCtx.getDefaultEngine();
		if(Tools.isBlank(docUnid)){docUnid=Rdb.getNewUnid();} //如果没有传入文档unid则说明要启动一个新文档
		linkeywf.init(processid,docUnid,BeanCtx.getUserid(),""); //初始化工作流引擎
		linkeywf.getDocument().appendFromRequest();

        String htmlBody = getProcessHtml(processid);
        String jsonStr=RestUtil.formartResultJson("1","", "{\"htmlCode\":\""+RestUtil.encodeJson(htmlBody)+"\"}");
		return jsonStr;
	}
	
	public String getProcessHtml(String processid) throws Exception {
			String currentNodeid=BeanCtx.getLinkeywf().getCurrentNodeid();
			Document formDoc=BeanCtx.getLinkeywf().getFormDoc();
			
	        //1.触发流程过程中指定的打开前事件,规则中不返回1就表示要退出流程打开
	        String EwMsg = BeanCtx.getEventEngine().run(processid, "Process", "EngineBeforeOpen");
	        if (!EwMsg.equals("1")) {
	            return EwMsg;
	        }

	        //2.如果当前用户有审批权限则执行流程表单打开前事件,因为用户无权审批时不知道他位于那个节点，所以不能执行这种类型的事件
	        if (Tools.isNotBlank(currentNodeid)) {
	            BeanCtx.getEventEngine().run(processid, currentNodeid, "EngineFormBeforeOpen");
	        }

	        /* 3.运行绑定表单字段的事件 */
	        String docStatus = "EDIT"; // 编辑状态
	        if (BeanCtx.getLinkeywf().isReadOnly()) {
	            docStatus = "READ";
	        } // 只读状态
	        if (BeanCtx.getLinkeywf().getIsNewProcess()) {
	            docStatus = "NEW";
	        } // 新建状态

	        //初始化引擎表单的字段配置信息,只有在流程打开时才需要，在流程提交时暂不初始化表单字段配置信息
	        ((ModForm) BeanCtx.getBean("ModForm")).initEngineFormFieldConfig(BeanCtx.getLinkeywf().getFormDoc());

	        //获得所有表单的字段配置信息包括子表单的，然后执行字段绑定的后端规则
	        HashMap<String, Map<String, String>> formFieldConfig = new HashMap<String, Map<String, String>>();
	        HashMap<String, Map<String, String>> mainFormFieldConfig = BeanCtx.getMainFormFieldConfig(); //主表单的字段配置
	        if (mainFormFieldConfig != null) {
	            formFieldConfig.putAll(BeanCtx.getMainFormFieldConfig()); //主表单的字段配置
	        }
	        HashMap<String, Map<String, String>> subFormFieldConfig = BeanCtx.getSubFormFieldConfig(); //子表单字段配置
	        if (subFormFieldConfig != null) {
	            formFieldConfig.putAll(BeanCtx.getSubFormFieldConfig()); //追加子表单的字段配置
	        }
	        HashMap<String, Object> params = new HashMap<String, Object>(); //准备运行规则的参数
	        params.put("Document", BeanCtx.getLinkeywf().getDocument());
	        for (String fieldName : formFieldConfig.keySet()) {
	            Map<String, String> fieldMapObject = formFieldConfig.get(fieldName);
	            //BeanCtx.out("fieldJsonObject="+fieldMapObject.toString());
	            String rule = fieldMapObject.get("NodeRule"); //流程环节中绑定的规则编号，要优先执行
	            if (Tools.isNotBlank(rule)) {
	                //节点规则执行后就不用执行表单中的字段规则属性中的规则了,而且节点规则不受编辑新建影响，肯定运行
	                params.put("FieldName", fieldMapObject.get("name")); // 字段名称
	                BeanCtx.getExecuteEngine().run(rule, params); //运行字段数据源并传入字段名称和文档对像
	            }
	            else {
	                //在没有节点规则的情况下才执行
	                rule = fieldMapObject.get("rule"); // 表单字段属性中绑定的规则编号,
	                if (Tools.isNotBlank(rule)) {
	                    String ruleoption = fieldMapObject.get("ruleoption"); // 规则运行方式NEW,EDIT,READ
	                    if (Tools.isBlank(ruleoption)) {
	                        ruleoption = "NEW";
	                    } //默认为新建
	                    if (Tools.isBlank(ruleoption) || ruleoption.indexOf(docStatus) != -1) {
	                        params.put("FieldName", fieldMapObject.get("name")); // 字段名称
	                        BeanCtx.getExecuteEngine().run(rule, params); //运行字段数据源并传入字段名称和文档对像
	                    }
	                }
	            }
	        }

	        /* 4.运行主表单打开事件 */
	        String ruleNum = formDoc.g("EventRuleNum");
	        if (Tools.isNotBlank(ruleNum)) {
	            params = new HashMap<String, Object>();
	            params.put("FormDoc", formDoc);
	            params.put("DataDoc", BeanCtx.getLinkeywf().getDocument());
	            params.put("EventName", "onFormOpen");
	            if (BeanCtx.getLinkeywf().isReadOnly()) {
	                params.put("ReadOnly", "1");
	            }
	            else {
	                params.put("ReadOnly", "0");
	            }
	            String ruleStr = BeanCtx.getExecuteEngine().run(ruleNum, params); //运行表单打开事件
	            if (!ruleStr.equals("1")) {
	                //说明事件中要退出本次表单打开
	                return ruleStr;
	            }
	        }

	        //5运行子表单打开事件
	        String ruleResult = ((ModForm) BeanCtx.getBean("ModForm")).runSubFormEvent("onFormOpen", true);
	        if (!ruleResult.equals("1")) {
	            //说明事件中要退出本次表单打开
	            return ruleResult;
	        }

	        StringBuilder formBody = new StringBuilder(10000);
	        params = new HashMap<String, Object>();
	        params.put("FormDoc", formDoc);
	        params.put("FormBody", formBody);
	        formBody.append(BeanCtx.getExecuteEngine().run("R_S003_B011", params)); //主表单body部分
	        formBody.append(BeanCtx.getExecuteEngine().run("R_S003_B060", params)); //子表单body部分  
	        
	        //9.如果当前用户有审批权限则执行流程表单打开后事件,因为用户无权审批时不知道他位于那个节点，所以不能执行这种类型的事件
	        if (Tools.isNotBlank(currentNodeid)) {
	            BeanCtx.getEventEngine().run(processid, currentNodeid, "EngineFormAfterOpen");
	        }

	        //10.执行流程打开后事件，不管用户有没有审批权限都执行
	        BeanCtx.getEventEngine().run(processid, "Process", "EngineAfterOpen");

	        formBody.trimToSize();
	        return formBody.toString();
	    }
	
}