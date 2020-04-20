package cn.linkey.wf;

import java.util.LinkedHashSet;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.org.LinkeyUser;
import cn.linkey.util.Tools;
import cn.linkey.util.DateUtil;

import org.apache.commons.lang3.StringUtils;

public class RemarkImpl implements Remark {
    /*
     * (non-Javadoc)
     * @see cn.linkey.wf.Remark#getRemarkList(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public String getRemarkList(String docUnid, String remarkType, String remarkTable, String isReadFlag) {
        String remarkStyle = BeanCtx.getSystemConfig("RemarkStyle"); //从通用配置中读取意见显示模式
        if (Tools.isBlank(remarkStyle)) {
            remarkStyle = "<loop><div style=\"padding-left:8px;color:#0066cc\">#Body #SignImage</div>"
                    + "<div align=right style=\"padding-right:30px;\">#StartNode  <font color=\"#339900\">#UserName/#DeptName</font> "
                    + "<font color=\"#999999\">#EndTime</font></div><hr style=\"border: 1px dashed #cccccc; width: 98%; height: 1px;\" align=left /></loop>";
        }
        String startStr = StringUtils.substringBefore(remarkStyle, "<loop>");
        String loopStr = StringUtils.substringBetween(remarkStyle, "<loop>", "</loop>");
        String endStr = StringUtils.substringAfter(remarkStyle, "</loop>");
        if (Tools.isBlank(remarkTable)) {
            remarkTable = "BPM_AllRemarkList";
        } //意见记录所在数据库表
        String sql;
        if (remarkType.equals("ALL")) {
            sql = "select * from " + remarkTable + " where DocUnid='" + docUnid + "' and IsReadFlag='" + isReadFlag + "' order by EndTime";
        }
        else {
            sql = "select * from " + remarkTable + " where DocUnid='" + docUnid + "' and IsReadFlag='" + isReadFlag + "' and RemarkType='" + remarkType + "' order by EndTime";
        }
        StringBuilder remarkList = new StringBuilder();
        remarkList.append(startStr);
        String tmpStr = loopStr;
        LinkedHashSet<Document> dc = Rdb.getAllDocumentsSetBySql(remarkTable, sql); //得到所有流转记录文档
        
        String sql2 = "";
        for (Document doc : dc) {
            if (doc.g("ShowBlankFlag").equals("1") && Tools.isBlank(doc.g("Remark"))) {
                continue;
            } //不显示空意见
            
            //20180428添加手写签名的显示
            sql2 = "select WF_ImageEncode,isSignature,ImageStyle from BPM_Signature_Union where WF_DocUnid='" + doc.g("WF_OrUnid") + "'";
            Document imageDoc = Rdb.getDocumentBySql(sql2);
            if("1".equals(imageDoc.g("isSignature"))){
            	String signatureStr = " <img "+ imageDoc.g("ImageStyle") +" src=\"" + imageDoc.g("WF_ImageEncode") + "\">";
                tmpStr = StringUtils.replace(loopStr, "#Body", signatureStr);
            }else{
            	tmpStr = StringUtils.replace(loopStr, "#Body", doc.g("Remark"));
            }
            
            tmpStr = StringUtils.replace(tmpStr, "#DeptName", doc.g("DeptName"));
            tmpStr = StringUtils.replace(tmpStr, "#UserName", doc.g("UserName"));
            tmpStr = StringUtils.replace(tmpStr, "#StartNode", doc.g("NodeName"));
            tmpStr = StringUtils.replace(tmpStr, "#StartTime", doc.g("StartTime"));
            tmpStr = StringUtils.replace(tmpStr, "#EndTime", doc.g("EndTime"));
            if (tmpStr.indexOf("DateOnly") != -1) {
                tmpStr = StringUtils.replace(tmpStr, "#StartDateOnly", doc.g("StartTime").substring(0, 10));
                tmpStr = StringUtils.replace(tmpStr, "#EndDateOnly", doc.g("EndTime").substring(0, 10));
            }

            //看用户是否有签名图片如果有就显示,系统配置中要设置UseSignImage=1才可启用签名图片
            //这里应该启用缓存功能，缓存每个用户的签名图片的路径用来加快显示速度
            if (BeanCtx.getSystemConfig("UseSignImage").equals("1")) {
                sql = "select WF_OrUnid from BPM_Template where Itemid='StampSign' and Readers='" + doc.g("Userid") + "'";
                String remarkdocUnid = Rdb.getValueBySql(sql);
                if (Tools.isNotBlank(remarkdocUnid)) {
                    sql = "select FileName,WF_OrUnid,FilePath from BPM_AttachmentsList where DocUnid='" + remarkdocUnid + "'";
                    Document filedoc = Rdb.getDocumentBySql(sql);
                    String fileName = filedoc.g("FileName");
                    fileName = filedoc.g("FilePath") + filedoc.g("WF_OrUnid") + fileName.substring(fileName.lastIndexOf("."));
                    tmpStr = StringUtils.replace(tmpStr, "#SignImage", "<img src=\"" + fileName + "\" align=\"absmiddle\" border='0' >");
                }
                else {
                    tmpStr = StringUtils.replace(tmpStr, "#SignImage", ""); //替换为空值
                }
            }
            else {
                tmpStr = StringUtils.replace(tmpStr, "#SignImage", ""); //替换为空值
            }

            remarkList.append(tmpStr);
        }
        remarkList.append(endStr);
        //BeanCtx.p("remarkList="+remarkList.toString());
        return remarkList.toString();
    }

    /*
     * (non-Javadoc)
     * @see cn.linkey.wf.Remark#AddReadRemark(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public int AddReadRemark(String nodeid, String nodeName, String startTime) {
        ProcessEngine linkeywf = BeanCtx.getLinkeywf();
        String docUnid = linkeywf.getDocUnid();
        String processid = linkeywf.getProcessid();
        String processName = linkeywf.getProcessName();
        String processNumber = linkeywf.getProcessNumber();
        Document doc = BeanCtx.getDocumentBean("BPM_InsRemarkList");
        doc.s("DocUnid", docUnid);
        doc.s("Processid", processid);
        doc.s("ProcessNumber", processNumber);
        doc.s("Nodeid", nodeid);
        doc.s("Userid", BeanCtx.getUserid());
        doc.s("UserName", BeanCtx.getLinkeyUser().getCnName(BeanCtx.getUserid()));
        doc.s("StartTime", startTime);
        doc.s("EndTime", DateUtil.getNow());
        doc.s("DifTime", DateUtil.getDifTime(startTime, DateUtil.getNow()));
        doc.s("ProcessName", processName);
        doc.s("NodeName", nodeName);
        doc.s("Deptid", BeanCtx.getLinkeyUser().getDeptidByUserid(BeanCtx.getUserid(), false));
        doc.s("DeptName", BeanCtx.getLinkeyUser().getFullDeptNameByDeptid(doc.g("Deptid"), false));
        doc.s("Remark", "已阅");
        doc.s("ActionName", "阅读");
        doc.s("Actionid", "Read");
        doc.s("IsReadFlag", "1");
        return doc.save();
    }

    /*
     * (non-Javadoc)
     * @see cn.linkey.wf.Remark#AddRemark(java.lang.String, java.lang.String)
     */
    @Override
    public int AddRemark(String actionid, String remark) {
        return AddRemark(actionid, remark, "1");
    }

    /*
     * (non-Javadoc)
     * @see cn.linkey.wf.Remark#AddRemark(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public int AddRemark(String actionid, String remark, String remarkLevel) {

        //获得实始变量值
        ProcessEngine linkeywf = BeanCtx.getLinkeywf();
        Document currentInsUserDoc = linkeywf.getCurrentInsUserDoc();
        Document currentModNodeDoc = linkeywf.getCurrentModNodeDoc();
        String docUnid = linkeywf.getDocUnid();
        String actionNum = linkeywf.getActionNum();
        String processid = linkeywf.getProcessid();
        String processName = linkeywf.getProcessName();
        String processNumber = linkeywf.getProcessNumber();
        String currentNodeid = linkeywf.getCurrentNodeid();

        //获得本次提交所影响的坏节和用户信息
        LinkeyUser linkeyUser = (LinkeyUser) BeanCtx.getBean("LinkeyUser");
        String sql = "select NodeName from BPM_InsNodeList where DocUnid='" + docUnid + "' and ActionNum='" + actionNum + "' and Status='Current' and NodeType='Task'";
        String nextNodeName = Rdb.getValueBySql(sql); //所有本次操作后所激活的节点名称
        sql = "select userid from BPM_InsUserList where DocUnid='" + docUnid + "' and ActionNum='" + actionNum + "' and Status='Current'";
        String nextUserList = Rdb.getValueBySql(sql); //所有本次操作所激活的用户
        if (Tools.isNotBlank(nextUserList)) {
            nextUserList = linkeyUser.getCnName(nextUserList); //转换成为中文名称
        }

        //1.计算处理有没有超时
        String overTimeNum = "0";
        long exceedHour = 0;
        String overTimeFlag = "0";
        if (currentInsUserDoc == null) {
            currentInsUserDoc = BeanCtx.getDocumentBean("BPM_InsUserList"); //创建一个空文档,防止后面获得字段时出错
            currentInsUserDoc.s("StartTime", DateUtil.getNow());
        }
        if (currentModNodeDoc == null) {
            currentModNodeDoc = BeanCtx.getDocumentBean("BPM_InsNodeList"); //创建一个空文档,防止后面获得字段时出错
        }

        String exceedTime = currentInsUserDoc.g("ExceedTime"); //当前用户实例中指定的持续时间，单位为小时
        if (Tools.isNotBlank(exceedTime)) {
            exceedHour = Long.parseLong(exceedTime);
        }
        String limitTime = currentInsUserDoc.g("limitTime"); //当前用户实例中指定的完成时间
        String startTime = currentInsUserDoc.g("StartTime"); //任务开始时间
        String endTime = DateUtil.getNow(); //任务结束时间

        //BeanCtx.p("AddRemark->limitTime="+limitTime);
        if (Tools.isBlank(limitTime)) {
            //计算结束时间与开始时间的差值
            //BeanCtx.p("AddRemark->exceedHour="+exceedHour);
            if (exceedHour > 0) {
                String difTime = DateUtil.getDifTime(startTime, endTime);
                long endHour = Long.parseLong(difTime) / 60; //得到单位小时
                if (endHour > exceedHour) {
                    overTimeFlag = "1"; //表示超时
                    overTimeNum = String.valueOf(endHour - exceedHour); //获得超时时间数，单位小时
                }
            }
            else {
                overTimeFlag = "0"; //表示未超时
                overTimeNum = "0";
            }
        }
        else {
            //直接指定了具体的完成时间
            String difTime = DateUtil.getDifTime(limitTime, endTime);
            if (difTime.equals("-1")) {
                //说明完成时间小于指定的limitTime
                overTimeFlag = "0"; //表示未超时
                overTimeNum = "0";
            }
            else {
                //说明已经超时
                overTimeFlag = "1"; //表示已超时
                overTimeNum = difTime; //超时数
            }
        }

        //2.检测是否是代他人办理
        if (Tools.isNotBlank(currentInsUserDoc.g("EntrustUserid"))) {
            //说明是代他人办理
            remark = remark + "(代" + currentInsUserDoc.g("EntrustUserid") + "办理)";
        }

        //3.创建流转记录文档并存盘
        Document doc = BeanCtx.getDocumentBean("BPM_InsRemarkList");
        doc.s("DocUnid", docUnid);
        doc.s("Processid", processid);
        doc.s("ProcessNumber", processNumber);
        doc.s("Nodeid", currentNodeid);
        doc.s("Userid", BeanCtx.getUserid());
        doc.s("UserName", linkeyUser.getCnName(BeanCtx.getUserid()));
        doc.s("StartTime", currentInsUserDoc.g("StartTime"));
        doc.s("FirstReadTime", currentInsUserDoc.g("FirstReadTime"));
        doc.s("EndTime", DateUtil.getNow());
        doc.s("DifTime", DateUtil.getDifTime(doc.g("StartTime"), DateUtil.getNow()));
        doc.s("RemarkType", currentModNodeDoc.g("RemarkType"));
        doc.s("RemarkAcl", currentModNodeDoc.g("RemarkAcl"));
        doc.s("RemarkAclForMe", currentModNodeDoc.g("RemarkAclForMe"));
        doc.s("ProcessName", processName);
        doc.s("NodeName", currentModNodeDoc.g("NodeName"));
        doc.s("Deptid", linkeyUser.getDeptidByUserid(BeanCtx.getUserid(), false));
        doc.s("DeptName", linkeyUser.getFullDeptNameByDeptid(doc.g("Deptid"), false));
        doc.s("NextUserList", nextUserList);
        doc.s("NextNodeName", nextNodeName);
        doc.s("NodeDuration", currentModNodeDoc.g("NodeDuration"));
        doc.s("OverTimeFlag", overTimeFlag);
        doc.s("OverTimeNum", overTimeNum);
        doc.s("Remark", remark);
        doc.s("IsReadFlag", "0");
        doc.s("Actionid", actionid);
        doc.s("ActionName", Rdb.getValueBySql("select ActionType from BPM_EngineActionConfig where Actionid='" + actionid + "'"));
        doc.s("RemarkLevel", remarkLevel);
        doc.s("ShowBlankFlag", currentModNodeDoc.g("RemarkBlankFlag"));
        return doc.save();
    }

}
