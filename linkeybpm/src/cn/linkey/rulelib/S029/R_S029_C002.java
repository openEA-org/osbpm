package cn.linkey.rulelib.S029;

import java.util.HashMap;

import cn.linkey.app.AppUtil;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * @RuleName:检测设计元素的访问权限
 * @author admin
 * @version: 8.0
 * @Created: 2014-04-16 23:10
 */
final public class R_S029_C002 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) {
        //params为运行规则时所传入的参数
        String elNum = BeanCtx.g("wf_num"); //请求设计元素的编号,在Action调用本代码之前wf_num已经检查过是合法的了，已防止了sql注入的可能性
        String eType = elNum.substring(0, 1);
        String idFdName = "", tableName = "";
        if (eType.equals("P")) {
            tableName = "BPM_PageList";
            idFdName = "PageNum";
        }
        else if (eType.equals("F")) {
            tableName = "BPM_FormList";
            idFdName = "FormNumber";
        }
        else if (eType.equals("V")) {
            tableName = "BPM_GridList";
            idFdName = "GridNum";
        }
        else if (eType.equals("D")) {
            tableName = "BPM_DataSourceList";
            idFdName = "Dataid";
        }
        else if (eType.equals("R")) {
            tableName = "BPM_RuleList";
            idFdName = "RuleNum";
        }
        if (Tools.isNotBlank(tableName)) {
            //有些设计元素并不限定权限，所以需要判断tablename不为空时才可执行以下程序
            Document eldoc = AppUtil.getDocByid(tableName, idFdName, elNum, true);
            if (!eldoc.isNull()) {
                String roles = eldoc.g("Roles");
                if (!BeanCtx.getLinkeyUser().inRoles(BeanCtx.getUserid(), roles)) {
                    //说明用户没有访问权限
                    BeanCtx.showErrorMsg(BeanCtx.getMsg("Common", "ElementUnAuthorization", ""));
                    return "0";
                }
            }
        }

        // BeanCtx.out("执行过虑器规则(R_S001_C002)进行权限检测");
        return "1"; //返回1表示继续执行，返回0表示中止执行后续程序
    }
}