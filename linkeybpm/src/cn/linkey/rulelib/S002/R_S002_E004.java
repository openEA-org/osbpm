package cn.linkey.rulelib.S002;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;

import org.dom4j.Element;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.form.HtmlParser;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;
import cn.linkey.util.XmlParser;

/**
 * @RuleName:导入旧版本流程图
 * @author admin
 * @version: 8.0
 * @Created: 2014-12-12 10:55
 */
final public class R_S002_E004 implements LinkeyRule {

    @Override
    public String run(HashMap<String, Object> params) throws Exception {
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

    public String onFormOpen(Document doc, Document formDoc, String readOnly) throws Exception {
        //当表单打开时
        if (readOnly.equals("1")) {
            return "1";
        } //如果是阅读状态则可不执行
        if (doc.isNewDoc()) {
            //可以对表单字段进行初始化如:doc.s("fdname",fdvalue),可以获取字段值 doc.g("fdname")

        }
        return "1"; //成功必须返回1，否则表示退出并显示返回的字符串
    }

    public String onFormSave(Document doc, Document formDoc) throws Exception {
        //当表单存盘前
        BeanCtx.setCtxData("WF_NoEncode", "1"); // 指定存盘时不进行编码操作
        LinkedHashSet<String> fileList = doc.getAttachmentsNameAndPath();
        if (fileList.size() == 0) {
            return "请上传一个Xml的流程文件!";
        }
        int i = 0;
        for (String filePath : fileList) {
            i++;
            importDominoProcess(filePath);
        }
        doc.removeAllAttachments(true); //删除上传的临时文件

        return "成功导入(" + i + ")个流程!"; //成功必须返回1，否则表示退出存盘
    }

    public String importDominoProcess(String filePath) throws Exception {
        //当表单存盘前
        BeanCtx.setCtxData("WF_NoEncode", "1"); // 指定存盘时不进行编码操作
        LinkedHashSet<Document> dc = dominoXml2dc(filePath);
        Document graphicDoc = null;
        for (Document lotusDoc : dc) {
            String formName = lotusDoc.g("form");
            if (formName.equals("frmnewflow")) {
                //说明是导入流程图的设计
                graphicDoc = lotusDoc;
            }
            else if (formName.equalsIgnoreCase("process")) {
                //生成过程属性
                creatProcessNode(lotusDoc);
            }
            else if (formName.equalsIgnoreCase("Activity")) {
                //生成Task节点
                creatTaskNode(lotusDoc);
            }
            else if (formName.equalsIgnoreCase("AutoActivity")) {
                //生成自动Task节点
                creatAutoTaskNode(lotusDoc);
            }
            else if (formName.equalsIgnoreCase("Router")) {
                //生成路由线
                creatRouterNode(lotusDoc);
            }
            else if (formName.equalsIgnoreCase("StartNode")) {
                //生成开始节点
                creatStartNode(lotusDoc);
            }
            else if (formName.equalsIgnoreCase("EndNode")) {
                //生成结束节点
                creatEndNode(lotusDoc);
            }
            else if (formName.equalsIgnoreCase("SubProcess")) {
                //生成结束节点
                creatSubProcessNode(lotusDoc);
            }
            else if (formName.equalsIgnoreCase("Edge")) {
                //生成结决策点
                creatEdgeNode(lotusDoc);
            }
        }

        creatGraphicNode(graphicDoc); //先导入所有节点最后再转存图形

        return "1"; //成功必须返回1，否则表示退出存盘
    }

    /**
     * 生成图形节点
     * 
     * @param lotusDoc
     */
    public void creatGraphicNode(Document lotusDoc) {
        String body = lotusDoc.g("body");
        body = unSetCode(body);
        body = toPolyline(body, lotusDoc.g("unid"));
        body = toActivity(body);
        body = toShapeType(body);
        body = toShape(body);
        body = toOval(body);
        //		body=toSpan(body);
        Document ndoc = BeanCtx.getDocumentBean("BPM_ModGraphicList");
        ndoc.s("Processid", lotusDoc.g("unid"));
        ndoc.s("GraphicBody", body);
        ndoc.s("WF_OrUnid", lotusDoc.getDocUnid());
        ndoc.save();
    }

    /**
     * 生成决策点
     * 
     * @param lotusDoc
     */
    public void creatEdgeNode(Document lotusDoc) {
        Document ndoc = BeanCtx.getDocumentBean("BPM_ModGatewayList");
        ndoc.s("Processid", lotusDoc.g("unid"));
        ndoc.s("WF_Appid", lotusDoc.g("AppId"));
        ndoc.s("WF_OrUnid", lotusDoc.getDocUnid());
        String nodeid = lotusDoc.g("Nodeid").replace("Node", "");
        nodeid = "00000" + nodeid;
        nodeid = "G1" + nodeid.substring(nodeid.length() - 4);
        ndoc.s("Nodeid", nodeid);
        ndoc.s("NodeType", "Gateway");
        ndoc.s("NodeName", lotusDoc.g("Subject"));
        if (lotusDoc.g("Condition").equals("2")) {
            ndoc.s("ExtNodeType", "exclusiveGateway");//唯一网关
        }
        else if (lotusDoc.g("Condition").equals("3")) {
            ndoc.s("ExtNodeType", "parallelGateway");//并行网关
        }
        else {
            ndoc.s("ExtNodeType", "complexGateway");//复杂网关
        }
        ndoc.save();
    }

    /**
     * 生成开始节点
     * 
     * @param lotusDoc
     */
    public void creatStartNode(Document lotusDoc) {
        Document ndoc = BeanCtx.getDocumentBean("BPM_ModEventList");
        ndoc.s("Processid", lotusDoc.g("unid"));
        ndoc.s("WF_Appid", lotusDoc.g("AppId"));
        ndoc.s("WF_OrUnid", lotusDoc.getDocUnid());
        String nodeid = lotusDoc.g("Nodeid").replace("Node", "");
        nodeid = "00000" + nodeid;
        nodeid = "E1" + nodeid.substring(nodeid.length() - 4);
        ndoc.s("Nodeid", nodeid);
        ndoc.s("NodeType", "Event");
        ndoc.s("ExtNodeType", "startEvent");
        ndoc.s("NodeName", lotusDoc.g("Subject"));
        ndoc.s("Terminate", "1");
        ndoc.save();
    }

    /**
     * 生成结束节点
     * 
     * @param lotusDoc
     */
    public void creatEndNode(Document lotusDoc) {
        Document ndoc = BeanCtx.getDocumentBean("BPM_ModEventList");
        ndoc.s("Processid", lotusDoc.g("unid"));
        ndoc.s("WF_Appid", lotusDoc.g("AppId"));
        ndoc.s("WF_OrUnid", lotusDoc.getDocUnid());
        String nodeid = lotusDoc.g("Nodeid").replace("Node", "");
        nodeid = "00000" + nodeid;
        nodeid = "E1" + nodeid.substring(nodeid.length() - 4);
        ndoc.s("Nodeid", nodeid);
        ndoc.s("NodeType", "Event");
        ndoc.s("ExtNodeType", "endEvent");
        ndoc.s("NodeName", lotusDoc.g("Subject"));
        ndoc.s("Terminate", "1");
        ndoc.s("EndBusinessName", lotusDoc.g("WF_EndBusName"));
        ndoc.s("EndBusinessid", lotusDoc.g("WF_BUS_STATUS"));
        ndoc.save();
    }

    /**
     * 生成开始节点
     * 
     * @param lotusDoc
     */
    public void creatSubProcessNode(Document lotusDoc) {
        Document ndoc = BeanCtx.getDocumentBean("BPM_ModSubProcessList");
        ndoc.s("Processid", lotusDoc.g("unid"));
        ndoc.s("WF_Appid", lotusDoc.g("AppId"));
        ndoc.s("WF_OrUnid", lotusDoc.getDocUnid());
        String nodeid = lotusDoc.g("Nodeid").replace("Node", "");
        nodeid = "00000" + nodeid;
        nodeid = "S1" + nodeid.substring(nodeid.length() - 4);
        ndoc.s("Nodeid", nodeid);
        ndoc.s("NodeType", "SubProcess");
        ndoc.s("ExtNodeType", "subProcess");
        ndoc.s("NodeName", lotusDoc.g("Subject"));
        ndoc.save();
    }

    /**
     * 生成路由线
     * 
     * @param lotusDoc
     */
    public void creatRouterNode(Document lotusDoc) {
        Document ndoc = BeanCtx.getDocumentBean("BPM_ModSequenceFlowList");
        ndoc.s("Processid", lotusDoc.g("unid"));
        ndoc.s("WF_Appid", lotusDoc.g("AppId"));
        ndoc.s("WF_OrUnid", lotusDoc.getDocUnid());
        String pid = lotusDoc.g("Nodeid").replace("polyline", "");
        pid = "00000" + pid.replace("_", "");
        pid = "R" + pid.substring(pid.length() - 5); //结束节点id
        ndoc.s("Nodeid", pid);

        ndoc.s("NodeType", "SequenceFlow");
        ndoc.s("ExtNodeType", "sequenceFlow");
        ndoc.s("NodeName", lotusDoc.g("Subject"));
        ndoc.s("SourceNode", lotusDoc.g("ProcessNumber"));
        ndoc.s("TargetNode", lotusDoc.g("TypeNum"));
        ndoc.s("GatewayType", lotusDoc.g("Condition"));
        ndoc.s("DefaultSelected", lotusDoc.g("SelectedFlag"));
        ndoc.s("BackToPrvUser", lotusDoc.g("BackToPrvUser"));

        ndoc.save();
    }

    /**
     * 生成过程节点
     * 
     * @param lotusDoc
     */
    public void creatProcessNode(Document lotusDoc) {
        Document ndoc = BeanCtx.getDocumentBean("BPM_ModProcessList");
        ndoc.s("Processid", lotusDoc.g("unid"));
        ndoc.s("WF_Appid", lotusDoc.g("AppId"));
        ndoc.s("WF_OrUnid", lotusDoc.getDocUnid());
        ndoc.s("Nodeid", "Process");
        ndoc.s("NodeType", "Process");
        ndoc.s("ExtNodeType", "process");
        ndoc.s("NodeName", lotusDoc.g("Subject"));
        ndoc.s("ProcessNumber", lotusDoc.g("ProcessNumber"));
        ndoc.s("Folderid", lotusDoc.g("TypeNum"));
        ndoc.s("ProcessOwner", lotusDoc.g("ProcessOwnerOS"));
        ndoc.s("ProcessDesigner", lotusDoc.g("ProcessDesigner"));
        ndoc.s("ProcessStarter", lotusDoc.g("StartProcessOwnerOS"));
        ndoc.s("ProcessReader", lotusDoc.g("DocReader"));
        ndoc.save();
    }

    /**
     * 创建自动活动节点
     * 
     * @param lotusDoc
     */
    public void creatAutoTaskNode(Document lotusDoc) {
        Document ndoc = BeanCtx.getDocumentBean("BPM_ModTaskList");
        ndoc.s("Processid", lotusDoc.g("unid"));
        ndoc.s("WF_Appid", lotusDoc.g("AppId"));
        ndoc.s("WF_OrUnid", lotusDoc.getDocUnid());
        String nodeid = lotusDoc.g("Nodeid").replace("Node", "");
        nodeid = "00000" + nodeid;
        nodeid = "T1" + nodeid.substring(nodeid.length() - 4);
        ndoc.s("Nodeid", nodeid);
        ndoc.s("NodeType", "Task");
        ndoc.s("ExtNodeType", "businessRuleTask");
        ndoc.s("NodeName", lotusDoc.g("Subject"));
        ndoc.s("SendTo", lotusDoc.g("UserName"));
        ndoc.s("MailTitle", lotusDoc.g("NotesSubject"));
        ndoc.s("MailBody", lotusDoc.g("NotesBody"));
        ndoc.s("SendMailFlag", lotusDoc.g("SendMail"));
        ndoc.save();
    }

    /**
     * 创建任务节点
     * 
     * @param lotusDoc
     */
    public void creatTaskNode(Document lotusDoc) {
        Document ndoc = BeanCtx.getDocumentBean("BPM_ModTaskList");
        ndoc.s("Processid", lotusDoc.g("unid"));
        ndoc.s("WF_Appid", lotusDoc.g("AppId"));
        ndoc.s("WF_OrUnid", lotusDoc.getDocUnid());
        String nodeid = lotusDoc.g("Nodeid").replace("Node", "");
        nodeid = "00000" + nodeid;
        nodeid = "T1" + nodeid.substring(nodeid.length() - 4);
        ndoc.s("Nodeid", nodeid);
        ndoc.s("NodeType", "Task");
        ndoc.s("ExtNodeType", "userTask");
        ndoc.s("NodeName", lotusDoc.g("Subject"));
        ndoc.s("ApprovalFormOwner", lotusDoc.g("ActivityOwnerOS"));
        ndoc.s("ApprovalFormOwner_show", lotusDoc.g("ActivityOwnerOS_CN"));
        ndoc.s("OwnerSelectType", lotusDoc.g("AddType"));
        if (lotusDoc.g("AllSignFlag").equals("1")) {
            ndoc.s("LoopType", "3"); //会签
        }
        ndoc.s("OwnerMaxUserNum", lotusDoc.g("UserNum"));
        ndoc.s("OwnerMinUserNum", lotusDoc.g("MinUserNum"));
        ndoc.s("OwnerUserSize", lotusDoc.g("UserSize"));
        ndoc.s("OwnerSelectFlag", lotusDoc.g("CanSelUser"));
        ndoc.s("IsSequential", lotusDoc.g("SerialFlag"));
        if (lotusDoc.g("ReassignmentNestingOS").equals("0")) {
            ndoc.s("NoReassFlag", "1"); //禁止转交
        }
        if (lotusDoc.g("ReassignmentAllowanceOS").equals("1")) {
            ndoc.s("NoMoreReassFlag", ""); //允许进一步转交
        }
        else {
            ndoc.s("NoMoreReassFlag", "1");
        }
        if (lotusDoc.g("CanReassSelUser").equals("1")) {
            ndoc.s("NoReassSelUserFlag", ""); //允许选择用户
        }
        else {
            ndoc.s("NoReassSelUserFlag", "1");
        }
        ndoc.s("ReassignmentOwner", lotusDoc.g("Reassignment"));
        ndoc.s("ReassignmentOwner_show", lotusDoc.g("Reassignment_CN"));
        if (lotusDoc.g("CopyUserFlag").equals("1")) {
            ndoc.s("NoCopyToFlag", ""); //允许传阅
        }
        else {
            ndoc.s("NoCopyToFlag", "1"); //禁止传阅
        }
        ndoc.s("CopyToOwner", lotusDoc.g("CopyUser"));
        ndoc.s("CopyToOwner_show", lotusDoc.g("CopyUser_CN"));
        ndoc.s("RemarkType", lotusDoc.g("TraceType"));
        ndoc.save();

    }

    private String toOval(String vmlStr) {
        String rStr = "", StartStr = "", EndStr = "", tempStr = "";
        long startpos;
        int max = 0;
        String startcode = "<v:Oval";
        String endcode = "</v:Oval>";
        startpos = vmlStr.indexOf(startcode);
        HtmlParser htmlParser = (HtmlParser) BeanCtx.getBean("HtmlParser");
        if (startpos > 0) {
            rStr = vmlStr;
            while (startpos != -1) {
                max = max + 1;
                if (max > 200)
                    break;
                StartStr = rStr.substring(0, rStr.indexOf(startcode)); //线前面的代码
                EndStr = rStr.substring(rStr.indexOf(startcode) + startcode.length(), rStr.length());
                String vmlCode = startcode + EndStr.substring(0, EndStr.indexOf(endcode)) + endcode; //线的代码
                EndStr = EndStr.substring(EndStr.indexOf(endcode) + endcode.length(), EndStr.length()); //线后面的代码

                //转换节点id
                String id = htmlParser.getAttributeValue(vmlCode, "id");
                String oldid = id;
                id = "00000" + id.replace("Node", "");
                id = "1" + id.substring(id.length() - 4); //节点id

                //转换textbox文字的id
                vmlCode = vmlCode.replace("TextBox" + oldid, "TextBoxNode" + id);

                vmlCode = htmlParser.setAttributeValue(vmlCode, "id", "Node" + id);
                vmlCode = htmlParser.setAttributeValue(vmlCode, "NodeNum", id);
                vmlCode = htmlParser.setAttributeValue(vmlCode, "Nodeid", "E" + id);

                //转换链接线的id
                vmlCode = getStartAndEndLineObj(vmlCode, "LinkeyStartObj");
                vmlCode = getStartAndEndLineObj(vmlCode, "LinkeyEndObj");

                tempStr += StartStr + vmlCode; //转换过的新代码

                rStr = EndStr;
                startpos = rStr.indexOf(startcode);
            }
            tempStr += EndStr;
        }
        else {
            tempStr = vmlStr;
        }
        return tempStr;
    }

    private String toActivity(String vmlStr) {
        String rStr = "", StartStr = "", EndStr = "", tempStr = "";
        long startpos;
        int max = 0;
        String startcode = "<v:roundrect";
        String endcode = "</v:roundrect>";
        startpos = vmlStr.indexOf(startcode);
        HtmlParser htmlParser = (HtmlParser) BeanCtx.getBean("HtmlParser");
        if (startpos > 0) {
            rStr = vmlStr;
            while (startpos != -1) {
                max = max + 1;
                if (max > 200)
                    break;
                StartStr = rStr.substring(0, rStr.indexOf(startcode)); //线前面的代码
                EndStr = rStr.substring(rStr.indexOf(startcode) + startcode.length(), rStr.length());
                String vmlCode = startcode + EndStr.substring(0, EndStr.indexOf(endcode)) + endcode; //线的代码
                EndStr = EndStr.substring(EndStr.indexOf(endcode) + endcode.length(), EndStr.length()); //线后面的代码

                //转换节点id
                String id = htmlParser.getAttributeValue(vmlCode, "id");
                String oldid = id;
                id = "00000" + id.replace("Node", "");
                id = "1" + id.substring(id.length() - 4); //节点id

                //转换textbox文字的id
                vmlCode = vmlCode.replace("TextBox" + oldid, "TextBoxNode" + id);

                vmlCode = htmlParser.setAttributeValue(vmlCode, "id", "Node" + id);
                vmlCode = htmlParser.setAttributeValue(vmlCode, "NodeNum", id);
                vmlCode = htmlParser.setAttributeValue(vmlCode, "Nodeid", "T" + id);

                //转换链接线的id
                vmlCode = getStartAndEndLineObj(vmlCode, "LinkeyStartObj");
                vmlCode = getStartAndEndLineObj(vmlCode, "LinkeyEndObj");

                tempStr += StartStr + vmlCode; //转换过的新代码

                rStr = EndStr;
                startpos = rStr.indexOf(startcode);
            }
            tempStr += EndStr;
        }
        else {
            tempStr = vmlStr;
        }
        return tempStr;
    }

    /**
     * 分析LinkeyStartObj和LinkeyEndObj
     * 
     * @param vmlCode
     * @return
     */
    public String getStartAndEndLineObj(String vmlCode, String idType) {
        //转换链接线的id
        HtmlParser htmlParser = (HtmlParser) BeanCtx.getBean("HtmlParser");
        String linkeyStartObj = htmlParser.getAttributeValue(vmlCode, idType);
        if (Tools.isBlank(linkeyStartObj)) {
            return vmlCode;
        }
        String[] objArray = linkeyStartObj.split(",");
        String lineCode = "";
        //		BeanCtx.out(idType+"="+linkeyStartObj);
        for (String pid : objArray) {
            pid = pid.replace("polyline", "");
            String startNo = "00000" + pid.substring(0, pid.indexOf("_"));
            String endNo = "00000" + pid.substring(pid.indexOf("_") + 1);
            startNo = "1" + startNo.substring(startNo.length() - 4); //开始节点id
            endNo = "1" + endNo.substring(endNo.length() - 4); //结束节点id
            if (Tools.isBlank(lineCode)) {
                lineCode = "polyline" + startNo + "_" + endNo;
            }
            else {
                lineCode += ",polyline" + startNo + "_" + endNo;
            }
        }
        //		BeanCtx.out(lineCode);
        vmlCode = htmlParser.setAttributeValue(vmlCode, idType, lineCode);
        return vmlCode;
    }

    private String toSpan(String vmlStr) {
        String rStr = "", StartStr = "", EndStr = "", tempStr = "";
        long startpos;
        int max = 0;
        String startcode = "<SPAN ";
        String endcode = "</SPAN>";
        startpos = vmlStr.indexOf(startcode);
        HtmlParser htmlParser = (HtmlParser) BeanCtx.getBean("HtmlParser");
        if (startpos > 0 && vmlStr.indexOf("sType=\"Router\"") != -1) {
            rStr = vmlStr;
            while (startpos != -1) {
                max = max + 1;
                if (max > 200)
                    break;
                StartStr = rStr.substring(0, rStr.indexOf(startcode)); //前面的代码
                EndStr = rStr.substring(rStr.indexOf(startcode) + startcode.length(), rStr.length());
                String vmlCode = startcode + EndStr.substring(0, EndStr.indexOf(endcode)) + endcode; //代码
                EndStr = EndStr.substring(EndStr.indexOf(endcode) + endcode.length(), EndStr.length()); //后面的代码

                //转换节点id
                String id = htmlParser.getAttributeValue(vmlCode, "id");
                BeanCtx.out(vmlStr);
                BeanCtx.out(id);
                id = id.replace("TextBoxpolyline", "");
                String startNo = "00000" + id.substring(0, id.indexOf("_"));
                String endNo = "00000" + id.substring(id.indexOf("_") + 1);
                startNo = "1" + startNo.substring(startNo.length() - 4); //开始节点id
                endNo = "1" + endNo.substring(endNo.length() - 4); //结束节点id
                String newid = "TextBoxpolyline" + startNo + "_" + endNo;
                vmlCode = htmlParser.setAttributeValue(vmlCode, "id", newid);

                rStr = EndStr;
                startpos = rStr.indexOf(startcode);
            }
            tempStr += EndStr;
        }
        else {
            tempStr = vmlStr;
        }
        return tempStr;
    }

    private String toShapeType(String vmlStr) {
        String rStr = "", StartStr = "", EndStr = "", tempStr = "";
        long startpos;
        int max = 0;
        String startcode = "<v:shapetype";
        String endcode = "</v:shapetype>";
        startpos = vmlStr.indexOf(startcode);
        HtmlParser htmlParser = (HtmlParser) BeanCtx.getBean("HtmlParser");
        if (startpos > 0) {
            rStr = vmlStr;
            while (startpos != -1) {
                max = max + 1;
                if (max > 200)
                    break;
                StartStr = rStr.substring(0, rStr.indexOf(startcode)); //前面的代码
                EndStr = rStr.substring(rStr.indexOf(startcode) + startcode.length(), rStr.length());
                String vmlCode = startcode + EndStr.substring(0, EndStr.indexOf(endcode)) + endcode; //代码
                EndStr = EndStr.substring(EndStr.indexOf(endcode) + endcode.length(), EndStr.length()); //后面的代码

                //转换节点id
                String id = htmlParser.getAttributeValue(vmlCode, "id");
                id = "00000" + id.replace("SNode", "");
                id = "1" + id.substring(id.length() - 4); //节点id
                vmlCode = htmlParser.setAttributeValue(vmlCode, "id", "SNode" + id);
                tempStr += StartStr + vmlCode; //转换过的新代码

                rStr = EndStr;
                startpos = rStr.indexOf(startcode);
            }
            tempStr += EndStr;
        }
        else {
            tempStr = vmlStr;
        }
        return tempStr;
    }

    private String toShape(String vmlStr) {
        String rStr = "", StartStr = "", EndStr = "", tempStr = "";
        long startpos;
        int max = 0;
        String startcode = "<v:shape ";
        String endcode = "</v:shape>";
        startpos = vmlStr.indexOf(startcode);
        HtmlParser htmlParser = (HtmlParser) BeanCtx.getBean("HtmlParser");
        if (startpos > 0) {
            rStr = vmlStr;
            while (startpos != -1) {
                max = max + 1;
                if (max > 200)
                    break;
                StartStr = rStr.substring(0, rStr.indexOf(startcode)); //前面的代码
                EndStr = rStr.substring(rStr.indexOf(startcode) + startcode.length(), rStr.length());
                String vmlCode = startcode + EndStr.substring(0, EndStr.indexOf(endcode)) + endcode; //代码
                //				BeanCtx.out(vmlCode);
                EndStr = EndStr.substring(EndStr.indexOf(endcode) + endcode.length(), EndStr.length()); //后面的代码

                //转换节点id
                String id = htmlParser.getAttributeValue(vmlCode, "id");
                String oldid = id;
                id = "00000" + id.replace("Node", "");
                id = "1" + id.substring(id.length() - 4); //节点id
                vmlCode = htmlParser.setAttributeValue(vmlCode, "id", "Node" + id);
                vmlCode = htmlParser.setAttributeValue(vmlCode, "NodeNum", id);
                if (vmlCode.indexOf("SubProcess") == -1) {
                    vmlCode = htmlParser.setAttributeValue(vmlCode, "Nodeid", "G" + id); //网关
                }
                else {
                    vmlCode = htmlParser.setAttributeValue(vmlCode, "Nodeid", "S" + id); //子流程
                }
                vmlCode = htmlParser.setAttributeValue(vmlCode, "type", "#SNode" + id);

                //转换textbox文字的id
                vmlCode = vmlCode.replace("TextBox" + oldid, "TextBoxNode" + id);

                //转换链接线的id
                vmlCode = getStartAndEndLineObj(vmlCode, "LinkeyStartObj");
                vmlCode = getStartAndEndLineObj(vmlCode, "LinkeyEndObj");
                //				BeanCtx.out(vmlCode);
                tempStr += StartStr + vmlCode; //转换过的新代码

                rStr = EndStr;
                startpos = rStr.indexOf(startcode);
            }
            tempStr += EndStr;
        }
        else {
            tempStr = vmlStr;
        }
        return tempStr;
    }

    /**
     * 路由线转换为新版本的
     * 
     * @param htmlStr
     * @return
     */
    private String toPolyline(String vmlStr, String processid) {
        String rStr = "", StartStr = "", EndStr = "", tempStr = "";
        long startpos;
        int max = 0;
        String startcode = "<v:polyline";
        String endcode = "</v:polyline>";
        startpos = vmlStr.indexOf(startcode);
        HtmlParser htmlParser = (HtmlParser) BeanCtx.getBean("HtmlParser");
        if (startpos > 0) {
            rStr = vmlStr;
            while (startpos != -1) {
                max = max + 1;
                if (max > 200)
                    break;
                StartStr = rStr.substring(0, rStr.indexOf(startcode)); //线前面的代码
                EndStr = rStr.substring(rStr.indexOf(startcode) + startcode.length(), rStr.length());
                String vmlCode = startcode + EndStr.substring(0, EndStr.indexOf(endcode)) + endcode; //线的代码
                EndStr = EndStr.substring(EndStr.indexOf(endcode) + endcode.length(), EndStr.length()); //线后面的代码
                //				BeanCtx.out(vmlCode);
                //				BeanCtx.out(max+"右边的代码="+EndStr);

                //转换id
                String id = htmlParser.getAttributeValue(vmlCode, "id");
                String oldid = id;
                id = id.replace("polyline", "");
                String routerid = "00000" + id.replace("_", "");
                String startNo = "00000" + id.substring(0, id.indexOf("_"));
                String endNo = "00000" + id.substring(id.indexOf("_") + 1);
                startNo = "1" + startNo.substring(startNo.length() - 4); //开始节点id
                endNo = "1" + endNo.substring(endNo.length() - 4); //结束节点id
                routerid = "R" + routerid.substring(routerid.length() - 5); //路由线的id

                String newid = "polyline" + startNo + "_" + endNo;
                vmlCode = htmlParser.setAttributeValue(vmlCode, "id", newid);
                vmlCode = htmlParser.setAttributeValue(vmlCode, "LinkeyNode", startNo + "_" + endNo);
                String sourceNode = getTargetNodeid(processid, startNo);
                String targetNode = getTargetNodeid(processid, endNo);
                vmlCode = htmlParser.setAttributeValue(vmlCode, "SourceNode", sourceNode);
                vmlCode = htmlParser.setAttributeValue(vmlCode, "TargetNode", targetNode);

                //更新路由节点的SourceNode和TargetNode
                String sql = "update BPM_ModSequenceFlowList set SourceNode='" + sourceNode + "',TargetNode='" + targetNode + "' where Processid='" + processid + "' and Nodeid='" + routerid + "'";
                Rdb.execSql(sql);

                //转换nodeid
                String rid = "00000" + id.replace("_", "");
                rid = "R" + rid.substring(rid.length() - 5); //路由线的Nodeid
                vmlCode = htmlParser.setAttributeValue(vmlCode, "Nodeid", rid);

                tempStr += StartStr + vmlCode; //转换过的新代码

                rStr = EndStr;
                startpos = rStr.indexOf(startcode);
            }
            tempStr += EndStr;
        }
        else {
            tempStr = vmlStr;
        }
        return tempStr;
    }

    /**
     * 获得路由线的sourceNodeid
     * 
     * @param startNo
     * @return
     */
    public String getTargetNodeid(String processid, String startNo) {
        String sql = "select Nodeid from BPM_AllModNodeList where Processid='" + processid + "' and Nodeid like '%" + startNo + "'";
        return Rdb.getValueBySql(sql);
    }

    /**
     * 解码流程图
     * 
     * @param getStr
     * @return
     */
    private String unSetCode(String getStr) {
        //取消代码的混合 
        String unSetCode = "";
        unSetCode = getStr.replace("$H%", " ");
        unSetCode = unSetCode.replace("$R%", "<");
        unSetCode = unSetCode.replace("$X%", ">");
        unSetCode = unSetCode.replace("$A%", "polyline");
        unSetCode = unSetCode.replace("$B%", "SetProperty");
        unSetCode = unSetCode.replace("$C%", "TextBox");
        unSetCode = unSetCode.replace("$D%", "shapetype");
        unSetCode = unSetCode.replace("$G%", "vml");
        unSetCode = unSetCode.replace("$J%", "LinkeyEndObj");
        unSetCode = unSetCode.replace("$K%", "LinkeyStartObj");
        unSetCode = unSetCode.replace("$P%", "style=");
        unSetCode = unSetCode.replace("$Y%", "HEIGHT:");
        unSetCode = unSetCode.replace("$V%", "MARGIN-");
        return unSetCode;
    }

    /**
     * xml文件需要去掉dtd说明，database的属性要进行简化 IBM Domino的xml数据转换为java的文档集合对像
     * 
     * @param xmlFilePath xml文件所在路径
     * @return
     */
    public LinkedHashSet<Document> dominoXml2dc(String xmlFilePath) {
        LinkedHashSet<Document> dc = new LinkedHashSet<Document>();
        int i = 0;
        String filePath = BeanCtx.getAppPath() + xmlFilePath;
        java.io.File file = new java.io.File(filePath);
        if (file.exists()) {
            org.dom4j.Document xmldoc = XmlParser.load(filePath);
            List<Element> list = xmldoc.selectNodes("/database/document");
            for (Element docItem : list) {
                Document doc = BeanCtx.getDocumentBean("");
                String formName = docItem.attribute("form").getValue();
                doc.s("form", formName);//获得表单名

                //获得文档的unid
                List<Element> noteList = docItem.selectNodes("noteinfo");
                for (Element item : noteList) {
                    String docUnid = item.attribute("unid").getValue();
                    doc.s("WF_OrUnid", docUnid);
                }

                //获得文档的所有字段
                List<Element> docList = docItem.selectNodes("item");
                for (Element item : docList) {
                    String fdName = item.attribute("name").getValue();
                    String textVal = XmlParser.getElementText(item);
                    doc.s(fdName, textVal);
                }
                dc.add(doc);
            }
        }
        else {
            BeanCtx.out(filePath + "文件不存在");
        }
        return dc;
    }

}