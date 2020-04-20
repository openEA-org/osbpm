package cn.linkey.rulelib.S005;

import java.util.HashMap;

import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.ExcelUtil;
import cn.linkey.util.Tools;

/**
 * @RuleName:从视图导出到Excel
 * @author admin
 * @version: 8.0
 * @Created: 2014-06-15 00:03
 */
final public class R_S005_B014 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        //params为运行本规则时所传入的参数
        String docUnidList = BeanCtx.g("WF_OrUnid", true);
        String gridNum = BeanCtx.g("GridNum", true);
        String fileName = ExcelUtil.grid2Excel(gridNum, docUnidList);
        if (Tools.isNotBlank(fileName)) {
            BeanCtx.p(Tools.jmsg("ok", fileName));
        }
        else {
            BeanCtx.p(Tools.jmsg("error", "导出失败!"));
        }
        return "";
    }
}