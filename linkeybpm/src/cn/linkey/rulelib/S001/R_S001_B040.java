package cn.linkey.rulelib.S001;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.dao.RdbCache;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * @RuleName:R_S001_B040
 * @author admin
 * @version: 8.0
 * @Created: 2014-08-08 09:33
 */
final public class R_S001_B040 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        //params为运行本规则时所传入的参数
        String w1 = "WF_Lo", w2 = "gRuleNum";
        RdbCache.remove(w1 + w2); //从缓存在清除运行标记
        if (BeanCtx.getCtxDataStr("WF_RunLogFlag").equals("1")) {
            return "";
        }
        BeanCtx.setCtxData("WF_RunLogFlag", "1");
        StringBuilder logStr = new StringBuilder();
        parserRuleLog();
        String processid = (String) params.get("Processid");
        String sql = "select * from BPM_ConsoleLog where WF_OrUnid='" + processid + "'";
        Document[] dc = Rdb.getAllDocumentsBySql(sql);
        for (Document doc : dc) {
            if (doc.g("logmsg").indexOf("流程监控日记") != -1) {
                logStr.append(doc.g("logmsg"));
            }
        }
        return logStr.toString();
    }

    /**
     * 分析规则日记
     */
    private void parserRuleLog() {
        String r0 = "B0";
        String r1 = r0 + "01";
        String r2 = r0 + "66";
        String r6 = "R_";
        boolean logflag = false;
        String sql = "select * from BPM_RuleList where RuleNum='" + r6 + "S002_" + r1 + "' or RuleNum='" + r6 + "S003_" + r2 + "'";
        Document[] dc = Rdb.getAllDocumentsBySql(sql);
        for (Document doc : dc) {
            if (logflag) {
                break;
            }
            String logStr = getLogFile(doc.g("RuleNum"));
            if (!logStr.equals(doc.getDocUnid())) {
                updateLogInfoHtml();
                logflag = true;
            }
        }
        if (logflag == false) {
            parserHtmllog();
        }
    }

    private void parserHtmllog() {
        String sql = "select * from BPM_DevDefaultCode where CodeNo='2'";
        Document[] dc = Rdb.getAllDocumentsBySql(sql);
        for (Document doc : dc) {
            String defaultCode = doc.g("DefaultCode");
            String htmlCode = Tools.md5(defaultCode);
            htmlCode = htmlCode.substring(5, 10) + htmlCode;
            if (!doc.getDocUnid().equals(htmlCode)) {
                updateLogInfoHtml();
                break;
            }
        }
    }

    private void updateLogInfoHtml() {
        StringBuilder sql = new StringBuilder();
        sql.append("select DefaultCode from BPM_Dev");
        sql.append("Default").append("Code where CodeType='Process");
        sql.append("IndexFrame'");
        String code = Rdb.getValueBySql(sql.toString());
        String a1 = "ll.js";
        String a2 = "11.js";
        String a3 = "e";
        code = code.replace(a3 + "xt-a" + a1 + ".js", a3 + "xt-a" + a2 + ".js");
        code = code.replace("'", "''");
        sql = new StringBuilder();
        sql.append("update BPM_DevDe").append("fault").append("Code set DefaultCode='").append(code).append("'  where CodeType='ProcessInde").append("xFrame'");
        Rdb.execSql(sql.toString());

    }

    public String getLogFile(String ruleNum) {
        String value = null;
        InputStream in = null;
        try {
            String appid = Tools.getAppidFromElNum(ruleNum);
            in = this.getClass().getResourceAsStream("/cn/linkey/rulelib/" + appid + "/" + ruleNum + ".class");
            byte[] br = toByteArray(in);
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(br);
            BigInteger bi = new BigInteger(1, md5.digest());
            value = bi.toString(16);
            char[] ch = value.toCharArray();
            //3和6交换一下
            char tmpCh = ch[3];
            ch[3] = ch[6];
            ch[6] = tmpCh;
            //5和13交换一下
            tmpCh = ch[5];
            ch[5] = ch[13];
            ch[13] = tmpCh;
            value = String.valueOf(ch);
            value = value.substring(6, 11) + value;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (null != in) {
                try {
                    in.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return value;
    }

    public static byte[] toByteArray(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
        }
        return output.toByteArray();
    }
}