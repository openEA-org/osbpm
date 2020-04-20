package cn.linkey.rulelib.S017;

import java.util.*;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

import cn.linkey.dao.*;
import cn.linkey.util.*;
import cn.linkey.doc.*;
import cn.linkey.factory.*;
import cn.linkey.wf.*;
import cn.linkey.rest.RestUtil;
import cn.linkey.rule.LinkeyRule;

/**
 * @RuleName:Rest_Form获取表单所有字段配置的JSON
 * @author  admin
 * @version: 1.0
 */
final public class R_S017_B145 implements LinkeyRule {
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

		//获得所有字段的配置（含流程节点中设置的属性）的map
		HashMap<String, Map<String, String>>  allFiledMapConfig = getMainFormAndNodeFieldConfig(linkeywf.getFormDoc());
        //获得表单的属性json
        Document formDoc=linkeywf.getFormDoc();
        
        //给所有字段追加文档的值
        for(String key:allFiledMapConfig.keySet()){
        	Map<String,String> fieldMap=allFiledMapConfig.get(key);
        	String fieldName=fieldMap.get("name");
        	if(fieldName!=null){
        		fieldMap.put("value", linkeywf.getDocument().g(fieldName));
        	}else{
        		fieldMap.put("value", "");
        	}
        }
        
        //追加表单属性
        Map<String,String> formMap=new HashMap<String,String>();
        formMap.put("name", formDoc.g("FormName"));
        formMap.put("FormNumber", formDoc.g("FormNumber"));
        formMap.put("LayoutColsNum", formDoc.g("ColsNum"));
        allFiledMapConfig.put("FormAttribue", formMap);
        
        
        String jsonStr=JSON.toJSONString(allFiledMapConfig);
        jsonStr=RestUtil.formartResultJson("1", "", jsonStr);
		return jsonStr;
	}
	
	//获得主表单中的字段配置和节点的字段配置map对像
    public  HashMap<String, Map<String, String>> getMainFormAndNodeFieldConfig(Document formDoc) throws Exception {
        String mainFormReadOnly = "0"; //1表示主表单默认字段为可编辑，0表示默认字段为只读
        ProcessEngine linkeywf = BeanCtx.getLinkeywf();

        //1.首先获取当前环节的上配置的所有表单字段配置信息
        HashMap<String, Map<String, String>> currentNodeFieldConfigMap = new HashMap<String, Map<String, String>>(); //当前节点配置信息
        if (Tools.isNotBlank(linkeywf.getCurrentNodeid())) {
            if (linkeywf.getIsNewProcess() || linkeywf.isFirstNode()) {
                mainFormReadOnly = "1"; //如果是新流程或者是首环节时默认字段为可编辑
            }
            else {
                mainFormReadOnly = linkeywf.getCurrentModNodeDoc().g("MainFormReadOnly");
            }
            if (Tools.isBlank(mainFormReadOnly)) {
                mainFormReadOnly = "0";
            }
            String curNodeFieldConfig = "[" + linkeywf.getCurrentModNodeDoc().g("FieldConfig") + "]";
            if (Tools.isNotBlank(curNodeFieldConfig)) {
                JSONArray curFormJsonArr = JSON.parseArray(curNodeFieldConfig);
                for (int i = 0; i < curFormJsonArr.size(); i++) {
                    Map<String, String> fieldItemMap = (Map<String, String>) JSON.parse(curFormJsonArr.getString(i));
                    currentNodeFieldConfigMap.put(fieldItemMap.get("name"), fieldItemMap);
                }
            }
        }


        //2.获得主表单的配置字段信息
        HashMap<String, Map<String, String>> mainFormAllFieldConfigMap = new HashMap<String, Map<String, String>>(); //所有字段的配置信息map对像
        String mainFormFieldConfig = formDoc.g("FieldConfig");
        int spos = mainFormFieldConfig.indexOf("[");
        if (spos != -1) {
            mainFormFieldConfig = mainFormFieldConfig.substring(spos, mainFormFieldConfig.lastIndexOf("]") + 1);
            JSONArray mainFormJsonArr = JSON.parseArray(mainFormFieldConfig);
            for (int i = 0; i < mainFormJsonArr.size(); i++) {
                Map<String, String> mainFieldConfigMap = (Map<String, String>) JSON.parse(mainFormJsonArr.getString(i));
                String fieldName = mainFieldConfigMap.get("name"); //主表单字段名称
                Map<String, String> curFieldJsonObject = currentNodeFieldConfigMap.get(fieldName); //当前节点的字段配置信息
                if (curFieldJsonObject != null) {
                    //说明在当前环节中有配置字段信息
                    for (String attrName : curFieldJsonObject.keySet()) {
                        if (!attrName.equals("name")) {
                            mainFieldConfigMap.put(attrName, curFieldJsonObject.get(attrName)); //追加或替换主表单中已有的字段属性
                        }
                    }
                }
                currentNodeFieldConfigMap.remove(fieldName); //从当前节点的配置信息中删除此字段(不允许主表单的字段配置值覆盖节点的配置值)，见3.2如果后面还有的话就得追加到配置中去了

                //设置字段的默认权限,只有在编辑状态下才需要设置
                if (Tools.isNotBlank(linkeywf.getCurrentNodeid())) {
                    //String readtype=mainFieldConfigMap.get("readtype"); //字段只读模式
                    //if(readtype==null){ //如果字段没有被设置readtype则进行默认设定
                    if (mainFormReadOnly.equals("0")) { //环节中默认字段为只读状态，设置为计算模式输出span标签不能保存数据
                        mainFieldConfigMap.put("readtype", "ALL"); //默认为只读模式不保存数据
                        mainFieldConfigMap.put("NodeDefaultRead", "1"); //默认只读标记
                    }
                    else if (mainFormReadOnly.equals("1")) { //环节中默认字段为只读状态，设置为计算模式输出span标签不能保存数据
                        mainFieldConfigMap.put("NodeDefaultRead", "0"); //默认只读标记
                    }
                }
                mainFormAllFieldConfigMap.put(fieldName, mainFieldConfigMap);
            }
        }

        //3.2追加当前节点中多出来的字段到主配置中去
        for (String fdName : currentNodeFieldConfigMap.keySet()) {
            mainFormAllFieldConfigMap.put(fdName, currentNodeFieldConfigMap.get(fdName));
        }



        //只有有权审批时才设置
        if (Tools.isNotBlank(linkeywf.getCurrentNodeid())) {
//            getEngineSubFormFieldConfig(); //设置子表单的控制权限
        }

        return mainFormAllFieldConfigMap;
        
    }
    
}