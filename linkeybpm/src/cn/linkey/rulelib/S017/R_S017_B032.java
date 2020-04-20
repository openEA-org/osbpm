package cn.linkey.rulelib.S017;

import java.util.HashMap;
import java.util.LinkedHashSet;

import cn.linkey.doc.Document;
import cn.linkey.doc.Documents;
import cn.linkey.factory.BeanCtx;
import cn.linkey.form.HtmlParser;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;
import cn.linkey.wf.ModNode;
import cn.linkey.wf.ProcessEngine;

/**
 * @RuleName:Mobile_获得流程节点移动设备字段配置
 * @author admin
 * @version: 8.0
 * @Created: 2015-07-22 14:28
 */
final public class R_S017_B032 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        //params为运行本规则时所传入的参数

        //示例参数:{"Processid":"a8bb004c00173041060b5d502bada1435268","DocUnid":"351332d6027260453b088960719e75351c52"}
        String processid = (String) params.get("Processid");
        String docUnid = (String) params.get("DocUnid");

        Document returnDoc = BeanCtx.getDocumentBean("");

        //0.看是否传入了流程id
        if (Tools.isBlank(processid) || Tools.isBlank(docUnid)) {
            returnDoc.s("WF_SucessFlag", "0");
            returnDoc.s("WF_Msg", "Error:processid or docUnid is null!");
            return returnDoc.toJson();
        }

        //打开流程引擎并获得节点中配置的json字符串
        LinkedHashSet<Document> pdc = new LinkedHashSet<Document>();
        ModNode modNode = (ModNode) BeanCtx.getBean("ModNode");
        HtmlParser htmlParser = (HtmlParser) BeanCtx.getBean("HtmlParser");
        ProcessEngine linkeywf = BeanCtx.getDefaultEngine();
        linkeywf.init(processid, docUnid, BeanCtx.getUserid(), ""); //初始化工作流引擎

        //先先读取Process节点的移动终端配置信息
        Document processDoc = modNode.getNodeDoc(processid, "Process");
        String jsonStr = processDoc.g("MobileJson");
        String mobileRuleNum = processDoc.g("MobileRuleNum");//移动设备中绑定的二次运行规则
        if (Tools.isNotBlank(mobileRuleNum)) {
            BeanCtx.getExecuteEngine().run(mobileRuleNum);
        } //执行运算规则
        jsonStr = htmlParser.parserXTagValue(linkeywf.getDocument(), jsonStr);
        if (Tools.isNotBlank(jsonStr)) {
            pdc = Documents.jsonStr2dc(jsonStr);
        }

        LinkedHashSet<Document> mobiledc = new LinkedHashSet<Document>();
        //其次读取当前节点的配置信息
        if (Tools.isNotBlank(linkeywf.getCurrentNodeid())) {
            LinkedHashSet<Document> nodedc = new LinkedHashSet<Document>();
            String nodeJsonStr = linkeywf.getCurrentModNodeDoc().g("MobileJson");
            String mobileNodeRuleNum = processDoc.g("MobileRuleNum");//移动设备中绑定的二次运行规则
            if (Tools.isNotBlank(mobileNodeRuleNum)) {
                BeanCtx.getExecuteEngine().run(mobileNodeRuleNum);
            } //执行运算规则
            nodeJsonStr = htmlParser.parserXTagValue(linkeywf.getDocument(), nodeJsonStr);
            if (Tools.isNotBlank(nodeJsonStr)) {
                nodedc = Documents.jsonStr2dc(nodeJsonStr);
            }

            //对Process节点和当前节点的配置信息进行合并，以当前节点的配置信息为优先替换过程节点中的配置信息
            for (Document nodedoc : nodedc) {
                boolean inProcessConfig = false;
                for (Document processdoc : pdc) {
                    if (processdoc.g("FdName").equalsIgnoreCase(nodedoc.g("FdName"))) { //如果两个字段的名称一至则进行替换
                        inProcessConfig = true;
                        mobiledc.add(nodedoc); //加入节点
                    }
                }
                if (inProcessConfig == false) {
                    mobiledc.add(nodedoc); //加入Process节点中的配置值
                }
            }

            //把Process节点中的配置的，节点中没有配置的加入到mobiledc中去
            for (Document processdoc : pdc) {
                boolean inNodeConfig = false;
                for (Document nodedoc : nodedc) {
                    if (processdoc.g("FdName").equalsIgnoreCase((nodedoc.g("FdName")))) { //如果两个字段的名称一至则进行替换
                        inNodeConfig = true;
                    }
                }
                if (inNodeConfig == false) {
                    mobiledc.add(processdoc); //加入Process节点中的配置值
                }
            }

        }
        else {
            mobiledc.addAll(pdc); //如果没有权限审批则直接加入ProcessDoc中的配置值
        }

        return Documents.dc2json(mobiledc, "");
    }
}