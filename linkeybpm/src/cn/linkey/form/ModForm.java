package cn.linkey.form;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

import cn.linkey.dao.Rdb;
import cn.linkey.dao.RdbCache;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.util.Tools;
import cn.linkey.wf.ProcessEngine;

/**
 * 本类为表单操作类
 * 
 * @author lch
 */
public class ModForm {

    /**
     * 获得表单文档对像
     * 
     * @param formNumber
     * @return
     */
    public Document getFormDoc(String formNumber) {
        formNumber = RdbCache.getElementExtendsNum(formNumber);//看是否有继承设计

        Document doc = null;
        if (BeanCtx.getSystemConfig("SysDeveloperMode").equals("1")) {
            //每次从数据库中拿取,开发者模式下每次从数据库中拿
            String sql = "select * from BPM_FormList where FormNumber='" + formNumber + "' and Status='1'";
            doc = Rdb.getDocumentBySql("BPM_FormList", sql);
        }
        else {
            //从缓存中查找,只有在非开发模式下才启用缓存功能
            doc = (Document) RdbCache.get("TempCache", formNumber);
            if (doc == null) {
                String sql = "select * from BPM_FormList where FormNumber='" + formNumber + "' and Status='1'";
                doc = Rdb.getDocumentBySql("BPM_FormList", sql);
                RdbCache.put("TempCache", formNumber, doc); //30分钟不使用就会被清除缓存
            }
        }
        return doc;
    }

    /**
     * 获得表单文档对像 不支持缓存
     * 
     * @param formNumber
     * @return
     */
    public Document getFormDocByDocUnid(String docUnid) {
        String sql = "select * from BPM_FormList where WF_OrUnid='" + docUnid + "'";
        return Rdb.getDocumentBySql("BPM_FormList", sql);
    }

    /**
     * 获得表单的FormBody代码 不支持缓存 暂没有使用
     * 
     * @param formNumber
     * @return
     */
    public String getFormBody(String formNumber) {
        String sql = "select FormBody from BPM_FormList where FormNumber='" + formNumber + "' and Status='1'";
        return Rdb.getValueBySql(sql);
    }

    /**
     * 获得表单的FormBody代码 不支持缓存 暂没有使用
     * 
     * @param formNumber
     * @return
     */
    public String getFormBodyByDocUnid(String docUnid) {
        String sql = "select FormBody from BPM_FormList where WF_OrUnid='" + docUnid + "'";
        return Rdb.getValueBySql(sql);
    }

    /**
     * 应用表单专用 设置表单的字段配置信息
     * 
     * @param formDoc
     */
    @SuppressWarnings("unchecked")
    public void initAppFormFieldConfig(Document formDoc) {
        //获得表单的配置字段信息
        HashMap<String, Map<String, String>> mainFormAllFieldConfigMap = new HashMap<String, Map<String, String>>(); //所有字段的配置信息map对像
        String mainFormFieldConfig = formDoc.g("FieldConfig");
        int spos = mainFormFieldConfig.indexOf("[");
        if (spos != -1) {
            mainFormFieldConfig = mainFormFieldConfig.substring(spos, mainFormFieldConfig.lastIndexOf("]") + 1);
        }
        if (Tools.isNotBlank(mainFormFieldConfig)) {
            JSONArray mainFormJsonArr = JSON.parseArray(mainFormFieldConfig);
            for (int i = 0; i < mainFormJsonArr.size(); i++) {
                Map<String, String> mainFieldConfigMap = (Map<String, String>) JSON.parse(mainFormJsonArr.getString(i));
                mainFormAllFieldConfigMap.put(mainFieldConfigMap.get("name"), mainFieldConfigMap);
            }
        }
        formDoc.removeItem("FieldConfig"); //清除字段信息
        BeanCtx.setMainFormFieldConfig(mainFormAllFieldConfigMap); //应用表单只有主表单的配置信息
    }

    /**
     * 工作流引擎专用 获得并设置表单字段的配置信息以及表单打开事件，并且合当前环节的字段配置信息以及子表单的字段配置信息到formdoc中去 把表单的配置信息放入到线程变量中去，这样方便在所有的地方进行字段权限的动态控制
     * 
     * @param fieldConfig
     * @return
     */
    @SuppressWarnings("unchecked")
    public void initEngineFormFieldConfig(Document formDoc) throws Exception {
        String mainFormReadOnly = "0"; //1表示主表单默认字段为可编辑，0表示默认字段为只读
        ProcessEngine linkeywf = BeanCtx.getLinkeywf();

        //1.当前环节的所有字段配置信息
        HashMap<String, Map<String, String>> curFieldConfigMap = new HashMap<String, Map<String, String>>(); //当前节点配置信息
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
                    curFieldConfigMap.put(fieldItemMap.get("name"), fieldItemMap);
                }
            }
        }

        if (linkeywf.isDebug()) {
            BeanCtx.out("Debug:当前环节中设定的字段属性为=" + curFieldConfigMap.toString());
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
                Map<String, String> curFieldJsonObject = curFieldConfigMap.get(fieldName); //当前节点的字段配置信息
                if (curFieldJsonObject != null) {
                    //说明在当前环节中有配置字段信息
                    for (String attrName : curFieldJsonObject.keySet()) {
                        if (!attrName.equals("name")) {
                            mainFieldConfigMap.put(attrName, curFieldJsonObject.get(attrName)); //追加或替换主表单中已有的字段属性
                        }
                    }
                }
                curFieldConfigMap.remove(fieldName); //从当前节点的配置信息中删除此字段(不允许主表单的字段配置值覆盖节点的配置值)，见3.2如果后面还有的话就得追加到配置中去了

                //设置字段的默认权限,只有在编辑状态下才需要设置
                if (Tools.isNotBlank(linkeywf.getCurrentNodeid())) {
                    //String readtype=mainFieldConfigMap.get("readtype"); //字段只读模式
                    //if(readtype==null){ //如果字段没有被设置readtype则进行默认设定
                    if (mainFormReadOnly.equals("0")) { //环节中默认字段为只读状态，设置为计算模式输出span标签不能保存数据
                        mainFieldConfigMap.put("readtype_old", mainFieldConfigMap.get("readtype")); //把旧的只读模式存起来,在流程环节中设定为可编辑时要进行恢复(R_S003_B026)
                        mainFieldConfigMap.put("readtype", "ALL"); //默认为只读模式不保存数据
                        mainFieldConfigMap.put("NodeDefaultRead", "1"); //默认只读标记
                    }
                    else if (mainFormReadOnly.equals("1")) { //环节中默认字段为只读状态，设置为计算模式输出span标签不能保存数据
                        mainFieldConfigMap.put("readtype_old", mainFieldConfigMap.get("readtype")); //把旧的只读模式存起来,在流程环节中设定为可编辑时要进行恢复(R_S003_B026)
                        mainFieldConfigMap.put("NodeDefaultRead", "0"); //默认只读标记
                    }
                }
                mainFormAllFieldConfigMap.put(fieldName, mainFieldConfigMap);
            }
        }

        //3.2追加当前节点中多出来的字段到主配置中去
        for (String fdName : curFieldConfigMap.keySet()) {
            mainFormAllFieldConfigMap.put(fdName, curFieldConfigMap.get(fdName));
        }

        //设置到主表单的配置信息中去
        BeanCtx.setMainFormFieldConfig(mainFormAllFieldConfigMap);

        if (linkeywf.isDebug()) {
            BeanCtx.out("Debug:所有主表单字段的属性为=" + mainFormAllFieldConfigMap.toString());
        }

        //只有有权审批时才设置
        if (Tools.isNotBlank(linkeywf.getCurrentNodeid())) {
            getEngineSubFormFieldConfig(); //设置子表单的控制权限
        }

        if (linkeywf.isDebug()) {
            BeanCtx.out("Debug:合并后当前用户的字段属性为=" + BeanCtx.getMainFormFieldConfig().toString());
        }
    }

    /**
     * 设置子表单的字段控制权限
     */
    @SuppressWarnings("unchecked")
    private void getEngineSubFormFieldConfig() {

        ProcessEngine linkeywf = BeanCtx.getLinkeywf();

        //3.覆盖或追加调入的子表单和自定义的处理单的字段配置信息
        int spos = 0;
        HashMap<String, Map<String, String>> subFormAllFieldConfigMap = new HashMap<String, Map<String, String>>(); //所有字段的配置信息map对像
        //追加调入的子表单的字段信息
        String subFormNumber = linkeywf.getCurrentModNodeDoc().g("SubFormNumberLoad");
        if (Tools.isNotBlank(subFormNumber)) {
            Document subFormDoc = ((ModForm) BeanCtx.getBean("ModForm")).getFormDoc(subFormNumber);
            if (!subFormDoc.isNull()) {
                String subFormFieldConfig = subFormDoc.g("FieldConfig");
                spos = subFormFieldConfig.indexOf("[");
                if (spos != -1) {
                    subFormFieldConfig = subFormFieldConfig.substring(spos, subFormFieldConfig.lastIndexOf("]") + 1);
                }
                if (Tools.isNotBlank(subFormFieldConfig)) {
                    JSONArray subFormJsonArr = JSON.parseArray(subFormFieldConfig);
                    for (int i = 0; i < subFormJsonArr.size(); i++) {
                        Map<String, String> subFieldConfigMap = (Map<String, String>) JSON.parse(subFormJsonArr.getString(i));
                        subFormAllFieldConfigMap.put(subFieldConfigMap.get("name"), subFieldConfigMap);
                    }
                }
            }
        }

        //追加自定义处理单的字段配置信息
        String approvalFormNumber = linkeywf.getCurrentModNodeDoc().g("CusApprovalFormNum");
        if (Tools.isNotBlank(approvalFormNumber)) {
            Document subFormDoc = ((ModForm) BeanCtx.getBean("ModForm")).getFormDoc(approvalFormNumber);
            if (!subFormDoc.isNull()) {
                String subFormFieldConfig = subFormDoc.g("FieldConfig");
                spos = subFormFieldConfig.indexOf("[");
                if (spos != -1) {
                    subFormFieldConfig = subFormFieldConfig.substring(spos, subFormFieldConfig.lastIndexOf("]") + 1);
                }
                if (Tools.isNotBlank(subFormFieldConfig)) {
                    JSONArray subFormJsonArr = JSON.parseArray(subFormFieldConfig);
                    for (int i = 0; i < subFormJsonArr.size(); i++) {
                        Map<String, String> subFieldConfigMap = (Map<String, String>) JSON.parse(subFormJsonArr.getString(i));
                        subFormAllFieldConfigMap.put(subFieldConfigMap.get("name"), subFieldConfigMap);
                    }
                }
            }
        }

        if (linkeywf.isDebug()) {
            BeanCtx.out("Debug:所有子表单字段的属性为=" + subFormAllFieldConfigMap.toString());
        }

        BeanCtx.setSubFormFieldConfig(subFormAllFieldConfigMap); //设置子表单的字段控制权限
    }

    /**
     * 运行流程中子表单的事件,应用表单中没有子表单功能
     * 
     * @param eventAction 要执行的表单事件onFormOpen,onFormSave
     * @param isApproval 是否包启流程审批单中的事件true表示包含，否则表示不包含
     * @return
     */
    public String runSubFormEvent(String eventAction, boolean isApproval) throws Exception {
        //5运行子表单打开事件
        String subFormNumber = BeanCtx.getLinkeywf().getCurNodeSubFormList(isApproval);
        if (Tools.isNotBlank(subFormNumber)) {
            String[] formList = Tools.split(subFormNumber);
            for (String formNumber : formList) {
                Document subFormDoc = ((ModForm) BeanCtx.getBean("ModForm")).getFormDoc(formNumber);
                if (!subFormDoc.isNull()) {
                    String subRuleNum = subFormDoc.g("EventRuleNum");
                    if (Tools.isNotBlank(subRuleNum)) {
                        HashMap<String, Object> params = new HashMap<String, Object>(); //准备运行规则的参数
                        params = new HashMap<String, Object>();
                        params.put("FormDoc", subFormDoc);
                        params.put("DataDoc", BeanCtx.getLinkeywf().getDocument());
                        params.put("EventName", eventAction);
                        params.put("ReadOnly", "0");
                        String ruleStr = BeanCtx.getExecuteEngine().run(subRuleNum, params); //运行表单打开事件
                        if (!ruleStr.equals("1")) {
                            //说明事件中要退出本次表单打开
                            return ruleStr;
                        }
                    }
                }
            }
        }
        return "1";
    }

}
