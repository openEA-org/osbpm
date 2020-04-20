package cn.linkey.rulelib.S005;

import java.util.HashMap;

import cn.linkey.app.AppUtil;
import cn.linkey.dao.Rdb;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.DateUtil;
import cn.linkey.util.Tools;

/**
 * @RuleName:高级搜索
 * @author admin
 * @version: 8.0
 * @Created: 2014-06-19 10:09
 */
final public class R_S005_BS01 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) {
        //params为运行本规则时所传入的参数
        String sqlWhere = "";

        String subject = BeanCtx.g("Subject", true);
        String docNumber = BeanCtx.g("DocNumber", true);
        String addUser = BeanCtx.g("AddUser", true);
        String startDate = BeanCtx.g("StartDate", true);
        String endDate = BeanCtx.g("EndDate", true);
        String author = BeanCtx.g("Author", true);
        String curNodeName = BeanCtx.g("CurNodeName", true);
        String process = BeanCtx.g("Process", true);
        String docStatus = BeanCtx.g("DocStatus", true);
        if (Tools.isBlank(subject + docNumber + addUser + startDate + endDate + author + curNodeName + process + docStatus)) {
            BeanCtx.p("{\"total\":0,\"rows\":[]}");
            return "";
        }

        //文档编号
        if (!Tools.isBlank(docNumber)) {
            if (Tools.isBlank(sqlWhere)) {
                sqlWhere = "WF_DocNumber='" + docNumber + "'";
            }
            else {
                sqlWhere = sqlWhere + " and WF_DocNumber='" + docNumber + "'";
            }
        }
        //申请人
        if (!Tools.isBlank(addUser)) {
            if (Tools.isBlank(sqlWhere)) {
                sqlWhere = "WF_AddName='" + addUser + "'";
            }
            else {
                sqlWhere = sqlWhere + " and WF_AddName='" + addUser + "'";
            }
        }
        //创建时间
        if (!Tools.isBlank(startDate) && !Tools.isBlank(endDate)) {
            if (Tools.isBlank(startDate)) {
                startDate = "2000-01-01";
            }
            startDate = startDate + " 00:00";
            if (Tools.isBlank(endDate)) {
                endDate = DateUtil.getNow("yyyy-MM-dd");
            }
            endDate = endDate + " 23:59";
            String sSqlDate = "";
            if (Rdb.getDbType().equals("MSSQL")) {
                sSqlDate = "CONVERT(DATETIME,WF_DocCreated,120) between '" + startDate + "' and '" + endDate + "'";
            }
            else if (Rdb.getDbType().equals("MYSQL")) {
                sSqlDate = "STR_TO_DATE(WF_DocCreated,'%Y-%m-%d %H:%i:%s') between '" + startDate + "' and '" + endDate + "'";
            }
            else {
                //oracle
                sSqlDate = "to_date(WF_DocCreated,'yyyy-mm-dd hh24:mi') between '" + startDate + "' and '" + endDate + "'";
            }
            if (Tools.isBlank(sqlWhere)) {
                sqlWhere = sSqlDate;
            }
            else {
                sqlWhere = sqlWhere + " and " + sSqlDate;
            }
        }
        //当前处理人
        if (!Tools.isBlank(author)) {
            if (Rdb.getDbType().equals("MSSQL")) {
                if (Tools.isBlank(sqlWhere)) {
                    sqlWhere = "charindex('," + author + ",',','+WF_Author+',')<>0";
                }
                else {
                    sqlWhere = sqlWhere + " and charindex('," + author + ",',','+WF_Author+',')<>0";
                }
            }
            else if (Rdb.getDbType().equals("MYSQL")) {
                if (Tools.isBlank(sqlWhere)) {
                    sqlWhere = "instr(concat(',',WF_Author,','),'," + author + ",')>0";
                }
                else {
                    sqlWhere = sqlWhere + " and instr(concat(',',WF_Author,','),'," + author + ",')>0";
                }
            }
            else {
                if (Tools.isBlank(sqlWhere)) {
                    sqlWhere = "instr(','||WF_Author||',','," + author + ",')>0";
                }
                else {
                    sqlWhere = sqlWhere + " and instr(','||WF_Author||',','," + author + ",')>0";
                }
            }
        }
        //所属流程-多选
        if (!Tools.isBlank(process)) {
            String sSqlProcess = "";
            String[] arrProcess = Tools.split(process, ",");
            for (String processid : arrProcess) {
                if (Tools.isBlank(sSqlProcess)) {
                    sSqlProcess = "WF_Processid='" + processid + "'";
                }
                else {
                    sSqlProcess = sSqlProcess + " or WF_Processid='" + processid + "'";
                }
            }
            sSqlProcess = " (" + sSqlProcess + ")";
            if (Tools.isBlank(sqlWhere)) {
                sqlWhere = sSqlProcess;
            }
            else {
                sqlWhere = sqlWhere + " and" + sSqlProcess;
            }
        }
        //文档状态-多选
        if (!Tools.isBlank(docStatus)) {
            String sSqlDocStatus = "";
            if (docStatus.indexOf("Ins") != -1) {
                if (Tools.isBlank(sSqlDocStatus)) {
                    sSqlDocStatus = "WF_Status='Current'";
                }
                else {
                    sSqlDocStatus = sSqlDocStatus + " or WF_Status='Current'";
                }
            }
            if (docStatus.indexOf("Arc") != -1) {
                if (Tools.isBlank(sSqlDocStatus)) {
                    sSqlDocStatus = "WF_Status='ARC'";
                }
                else {
                    sSqlDocStatus = sSqlDocStatus + " or WF_Status='ARC'";
                }
            }
            sSqlDocStatus = "(" + sSqlDocStatus + ")";
            if (Tools.isBlank(sqlWhere)) {
                sqlWhere = sSqlDocStatus;
            }
            else {
                sqlWhere = sqlWhere + " and " + sSqlDocStatus;
            }
        }
        //标题
        if (!Tools.isBlank(subject)) {
            if (Tools.isBlank(sqlWhere)) {
                sqlWhere = "Subject like '%" + subject + "%'";
            }
            else {
                sqlWhere = sqlWhere + " and Subject like '%" + subject + "%'";
            }
        }
        //当前节点状态
        if (!Tools.isBlank(curNodeName)) {
            if (Tools.isBlank(sqlWhere)) {
                sqlWhere = "WF_CurrentNodeName like '%" + curNodeName + "%'";
            }
            else {
                sqlWhere = sqlWhere + " and WF_CurrentNodeName like '%" + curNodeName + "%'";
            }
        }

        if (!Tools.isBlank(sqlWhere)) {
            sqlWhere = " Where (" + sqlWhere + ") and " + Rdb.getInReaderSql("WF_AllReaders");
        }
        else {
            sqlWhere = " Where " + Rdb.getInReaderSql("WF_AllReaders");
        }

        String json = AppUtil.getDataGridJson("BPM_AllDocument", "WF_DocNumber,Subject,WF_Author_CN,WF_CurrentNodeName,WF_AddName_CN,WF_DocCreated,WF_ProcessName,WF_Status", sqlWhere, true);
        BeanCtx.out("json");
        BeanCtx.print(json);

        return "";
    }
}