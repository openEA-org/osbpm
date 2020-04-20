package cn.linkey.rulelib.S008;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.form.HtmlParser;
import cn.linkey.form.ModForm;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.DateUtil;
import cn.linkey.util.Tools;
import cn.linkey.wf.InsUser;
import cn.linkey.wf.NodeUser;
import cn.linkey.wf.ProcessEngine;
import cn.linkey.wf.Remark;

/**
 * 打开流程文档并返回表单的HTML代码
 * 
 * @author Administrator
 *
 */
public class R_S008_B006 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> nodeParams) throws Exception {
        String htmlBody = "";
        try {
            String docUnid = BeanCtx.g("wf_docunid", true); //文档id
            String taskid = BeanCtx.g("wf_taskid", true); //指定任务id
            if (Tools.isBlank(docUnid)) {
                docUnid = BeanCtx.g("WF_DocUnid", true);
            } //兼容小写
            String processid = BeanCtx.g("wf_processid", true); //流程id
            if (Tools.isBlank(processid)) {
                processid = BeanCtx.g("WF_Processid", true);
            } //兼容小写
            if (Tools.isBlank(processid) && Tools.isNotBlank(docUnid)) {
                processid = Rdb.getValueBySql("select WF_Processid from BPM_MainData where WF_OrUnid='" + docUnid + "'");
            }

            //检测非法字符串
            if (!Tools.isString(processid)) {
                String msg = BeanCtx.getMsg("Engine", "Error_EngineOpen");
                BeanCtx.showErrorMsg(msg);
                return "";
            }

            ProcessEngine linkeywf = new ProcessEngine();
            BeanCtx.setLinkeywf(linkeywf);
            linkeywf.init(processid, docUnid, BeanCtx.getUserid(), taskid);
            //增加调试功能
            if (linkeywf.isDebug()) {
                BeanCtx.out("*******流程打开调试开始流程id:" + linkeywf.getProcessid() + "实例id为:" + linkeywf.getDocUnid() + " *************");
            }

            htmlBody = openEngineDocument();

            //增加调试功能
            if (linkeywf.isDebug()) {
                BeanCtx.out("*******流程打开调试信息输出结束*************");
            }

            //增加操作记录
            addProcessReadLog(docUnid, processid, "阅读");

        }
        catch (Exception e) {
            String msg = BeanCtx.getMsg("Engine", "Error_EngineOpen");
            BeanCtx.log(e, "E", msg);
            htmlBody = msg;
        }
        BeanCtx.p(htmlBody);
        return "";
    }

    public String openEngineDocument() throws Exception {

        ProcessEngine linkeywf = BeanCtx.getLinkeywf();
        Document document = linkeywf.getDocument();
        String processid = linkeywf.getProcessid();
        String currentNodeid = linkeywf.getCurrentNodeid();
        Boolean readOnly = linkeywf.isReadOnly();
        Document formDoc = linkeywf.getFormDoc();
        Document currentInsUserDoc = linkeywf.getCurrentInsUserDoc();
        String docUnid = linkeywf.getDocUnid();

        //打开流程表单
        //0.如果当前用户可审批状态，则锁定当前文
        if (Tools.isNotBlank(currentNodeid)) {
            Document.lock(document.getDocUnid());
        }

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
        if (readOnly) {
            docStatus = "READ";
        } // 只读状态
        if (linkeywf.getIsNewProcess()) {
            docStatus = "NEW";
        } // 新建状态

        //初始化引擎表单的字段配置信息,只有在流程打开时才需要，在流程提交时暂不初始化表单字段配置信息
        ((ModForm) BeanCtx.getBean("ModForm")).initEngineFormFieldConfig(formDoc);

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
        params.put("Document", document);
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
            params.put("DataDoc", document);
            params.put("EventName", "onFormOpen");
            if (readOnly) {
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

        //6.记录首次阅读时间
        if (Tools.isNotBlank(currentNodeid) && !linkeywf.getIsNewProcess()) {
            if (!currentInsUserDoc.g("WF_IsNewInsUserDoc").equals("1") && Tools.isBlank(currentInsUserDoc.g("FirstReadTime"))) {
                currentInsUserDoc.s("FirstReadTime", DateUtil.getNow());
                currentInsUserDoc.save();
            }
        }

        //7.如果用户在待阅记录中则自动标识为已阅
        String copyUserList = document.g("WF_CopyUser");
        if (Tools.isNotBlank(copyUserList)) {
            copyUserList = "," + copyUserList + ",";
            if (copyUserList.indexOf("," + BeanCtx.getUserid() + ",") != -1) {
                //1.把当前用户标记为已阅
                InsUser insUser = (InsUser) BeanCtx.getBean("InsUser");
                Document insUserDoc = insUser.endCopyUser(BeanCtx.getUserid());

                //2.增加已阅记录
                Remark remark = (Remark) BeanCtx.getBean("Remark");
                remark.AddReadRemark(insUserDoc.g("Nodeid"), insUserDoc.g("NodeName"), insUserDoc.g("StartTime"));

                //更新主文档的WF_CopyUser字段
                copyUserList = Tools.join(((NodeUser) BeanCtx.getBean("NodeUser")).getCopyUser(docUnid), ",");
                String sql = "update " + document.getTableName() + " set WF_CopyUser='" + copyUserList + "' where WF_OrUnid='" + docUnid + "'";
                Rdb.execSql(sql);
                if (document.g("WF_Status").equals("ARC")) {
                    //已归档的文件则需要清除待阅记录
                    if (Tools.isNotBlank(copyUserList)) {
                        Rdb.execSql("delete from BPM_InsCopyUserList where Userid='" + BeanCtx.getUserid() + "' and WF_OrUnid='" + docUnid + "'");
                    }
                    else {
                        Rdb.execSql("delete from BPM_InsCopyUserList where WF_OrUnid='" + docUnid + "'"); //没有已阅的用户清除全部的记录
                    }
                }
            }
        }

        //8.开始返回流程表单的HTML代码
        StringBuilder formBody = new StringBuilder(10000);
        params.put("FormDoc", formDoc);
        params.put("FormBody", formBody);
        formBody.append(BeanCtx.getExecuteEngine().run("R_S008_B010", params));
        formBody.append(BeanCtx.getExecuteEngine().run("R_S003_B011", params));
        formBody.append(BeanCtx.getExecuteEngine().run("R_S008_B014", params));

        //9.如果当前用户有审批权限则执行流程表单打开后事件,因为用户无权审批时不知道他位于那个节点，所以不能执行这种类型的事件
        if (Tools.isNotBlank(currentNodeid)) {
            BeanCtx.getEventEngine().run(processid, currentNodeid, "EngineFormAfterOpen");
        }

        //10.执行流程打开后事件，不管用户有没有审批权限都执行
        BeanCtx.getEventEngine().run(processid, "Process", "EngineAfterOpen");

        formBody.trimToSize();
        return formBody.toString();
    }

    /**
     * 记录文件阅读记录
     */
    public static void addProcessReadLog(String docUnid, String processid, String remark) {
        if (BeanCtx.getSystemConfig("ProcessDocReadLog").equals("1")) {
            String ip = "";
            if (BeanCtx.getRequest() != null) {
                ip = BeanCtx.getRequest().getRemoteAddr();
            }
            remark = remark.replace("'", "''");
            String sql = "insert into BPM_AttachmentLog(WF_OrUnid,DocUnid,Userid,Processid,IP,Remark,WF_DocCreated) " + "values('" + Rdb.getNewUnid() + "','" + docUnid + "','" + BeanCtx.getUserName()
                    + "(" + BeanCtx.getUserid() + ")" + "','" + processid + "','" + ip + "','" + remark + "','" + DateUtil.getNow() + "')";
            Rdb.execSql(sql);
        }
    }

    /**
     * table布局转div布局
     * 
     * @param htmlStr
     * @return
     */
    private String table2div(String htmlStr) {
        HtmlParser htmlParser = (HtmlParser) BeanCtx.getBean("HtmlParser");
        StringBuilder divStr = new StringBuilder();
        int spos = htmlStr.indexOf("<tr");
        int max = 0;
        String mStr;

        //以下代码用来取一个完整的<tr></tr>之间的mStr字符串如：mStr="<tr><td>test</td></tr>";
        while (spos != -1) {
            if (max > 5000) {
                break;
            }
            max++;

            //取<tr左边和右边字符串
            String lStr = htmlStr.substring(0, spos); //<table><tr><td>....
            htmlStr = htmlStr.substring(spos + 3, htmlStr.length()); //取右边字符串 '不包含<tr  ><td>....

            //要进行成对分析
            int rspos = htmlStr.indexOf("</tr>");
            String rmStr = htmlStr.substring(0, rspos + 5);
            if (rmStr.indexOf("<tr") == -1) {
                //说明不存在<tr>嵌套情况
                //BeanCtx.out("没有嵌套进入 rmStr="+rmStr);
                htmlStr = htmlStr.substring(rspos + 5, htmlStr.length());
                //BeanCtx.out("没有嵌套进入 htmlStr="+htmlStr);
            }
            else {
                //				BeanCtx.out("有嵌套进入 rmStr="+rmStr);
                rmStr = "";
                rspos = htmlStr.indexOf("</tr>", rspos + 5);
                while (rspos > 0) {
                    rmStr += htmlStr.substring(0, rspos); //这里不加5，要判断完成对后再补上</tr>不然成对判断会在第一对时就成立
                    //					BeanCtx.out("有嵌套再查找 rmStr="+rmStr);
                    htmlStr = htmlStr.substring(rspos + 7, htmlStr.length());
                    if (StringUtils.countMatches(rmStr, "<tr") == StringUtils.countMatches(rmStr, "</tr>")) { //如果tr 和</tr>的出现次数相等则说明已经成对
                        //						BeanCtx.out("r成对.."+rmStr);
                        rmStr += "</tr>"; //补上</tr>
                        break;
                    }
                    else {
                        //不成对继续往后查</span>
                        //						BeanCtx.out("不成对.."+rmStr);
                        rspos = htmlStr.indexOf("</tr>");
                    }
                    rmStr += "</tr>"; //补上</span>
                }
            }

            mStr = "<tr" + rmStr; //中间字符串含标签<tr><td></td><td></td></tr>
            //			BeanCtx.out("mStr="+mStr);

            //开始分析<tr></tr>中间字符串在双数的td标
            //			<tr><td>日期控件</td><td>2014-03-35</td><td>日期控件2</td><td>2014-03-35</td></tr>
            if (mStr.indexOf("</td>") != -1) { //只有最少出现4次以上时才进行分析
                StringBuilder tdmStr = new StringBuilder();
                String[] tdArray = StringUtils.splitByWholeSeparator(mStr, "</td>", 0);
                int cellnum = 12 / tdArray.length; //bootstrap是固定12列，用12来除td的数得到bootstrap的列数
                //开始循环所有td
                int x = 0;
                String text = "", content = "";
                divStr.append("<div class=\"row\">");
                divStr.append("<div class=\"col-md-" + cellnum + "\">");
                divStr.append("<div class=\"form-group\">");
                for (String tdStr : tdArray) {
                    //是文字,把<tr><td>中间的文字取出来</td>
                    int tpos = mStr.indexOf("<td");
                    mStr = mStr.substring(tpos + 3, mStr.length());
                    tpos = mStr.indexOf(">");
                    if (x == 0) {
                        text = mStr.substring(tpos + 1);
                        divStr.append("<label class=\"control-label\">" + text + "</label>");
                        x = 1;
                    }
                    else {
                        content = mStr.substring(tpos + 1);
                        divStr.append(content);
                        x = 0;
                    }
                }
                divStr.append("</div></div></div>");
                BeanCtx.out("分析结果=" + divStr);
                mStr = tdmStr.toString();
            }
            else {
                //tr中没有td
                divStr.append("<div class=\"row\"><div class=\"col-md-12\"></div></div>");
            }
            //td分析结束

            spos = htmlStr.indexOf("<tr"); //再次计算右边字符串的name=标记进入下一次循环
        }

        //		BeanCtx.out("总解析tr数="+max);
        return divStr.toString();

    }
}
