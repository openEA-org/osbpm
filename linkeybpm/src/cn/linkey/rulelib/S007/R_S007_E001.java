package cn.linkey.rulelib.S007;

import java.util.HashMap;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.form.ModForm;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;
import cn.linkey.wf.ModNode;

/**
 * @RuleName:获得表单字段列表
 * @author admin
 * @version: 8.0
 * @Created: 2014-05-04 14:12
 */
final public class R_S007_E001 implements LinkeyRule {

    @Override
    public String run(HashMap<String, Object> params) {
        //获取事件运行参数
        Document formDoc = (Document) params.get("FormDoc"); //表单配置文档
        Document doc = (Document) params.get("DataDoc"); //数据主文档
        String eventName = (String) params.get("EventName");//事件名称
        if (eventName.equals("onFormOpen")) {
            String readOnly = (String) params.get("ReadOnly"); //1表示只读，0表示编辑
            return onFormOpen(doc, formDoc, readOnly);
        }
        else if (eventName.equals("onFormSave")) {
            return onFormSave(doc, formDoc);
        }
        return "1";
    }

    public String onFormOpen(Document doc, Document formDoc, String readOnly) {
        //当表单打开时
        if (readOnly.equals("1")) {
            return "1";
        } //如果是阅读状态则可不执行
        if (doc.isNewDoc()) {
            //可以对表单字段进行初始化如:doc.s("fdname",fdvalue),可以获取字段值 doc.g("fdname")
        }

        ModNode modNode = (ModNode) BeanCtx.getBean("ModNode");
        String processid = BeanCtx.g("Processid", true);
        String nodeid = BeanCtx.g("Nodeid", true);

        //获得节点文档是否有选择更换主表单
        String nodeTableName = modNode.getNodeTableName(processid, nodeid); //节点所在数据库表
        String sql = "select * from " + nodeTableName + " where Processid='" + processid + "' and Nodeid='" + nodeid + "'";
        Document nodeDoc = Rdb.getDocumentBySql(sql);
        String formNumber = "";
        if (Tools.isNotBlank(nodeDoc.g("FormNumber"))) {
            //说明有更换主表单
            formNumber = nodeDoc.g("FormNumber");
        }
        else {
            sql = "select FormNumber from BPM_ModProcessList where Processid='" + processid + "' and Nodeid='Process'";
            formNumber = Rdb.getValueBySql(sql);
        }

        //	    BeanCtx.out("formNumber="+formNumber);
        StringBuilder fdList = new StringBuilder();
        Document mainFormDoc = ((ModForm) BeanCtx.getBean("ModForm")).getFormDoc(formNumber);
        String fieldConfig = "{\"rows\":[]}";
        if (!mainFormDoc.isNull()) {
//        	if(Tools.isNotBlank(mainFormDoc.g("FieldConfig"))){  //20180921 FieldConfig进行非空校验
        		fieldConfig = mainFormDoc.g("FieldConfig");
                int spos = fieldConfig.indexOf("[");
                int epos = fieldConfig.lastIndexOf("]");
                fieldConfig = fieldConfig.substring(spos, epos + 1);
//        	}else{
//        		fieldConfig = "[]";
//        	}
        	
            if (Tools.isNotBlank(fieldConfig)) { /* 获得每个字段的后端规则并执行 */
                JSONArray jsonArr = JSON.parseArray(fieldConfig);
                for (int i = 0; i < jsonArr.size(); i++) {
                    JSONObject fieldItem = (JSONObject) jsonArr.get(i);
                    String fdName = fieldItem.getString("name"); // 字段名称
                    String fdText = fieldItem.getString("remark"); // 字段备注
                    if (Tools.isBlank(fdText)) {
                        fdText = fdName;
                    }
                    if (fdList.length() > 0) {
                        fdList.append(",");
                    }
                    fdList.append(fdName + "|" + fdText);
                }
            }

        }
        doc.s("FieldList", fdList.toString());

        return "1"; //成功必须返回1，否则表示退出并显示返回的字符串
    }

    public String onFormSave(Document doc, Document formDoc) {
        //当表单存盘前

        return "1"; //成功必须返回1，否则表示退出存盘
    }

}