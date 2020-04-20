package cn.linkey.rulelib.S001;

import java.util.HashMap;

import cn.linkey.app.AppPage;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * @RuleName:页面引入解析标签，用来解析[X]page.P_S001_001[/X]这样的标签
 * @author admin
 * @version: 8.0
 * @Created: 2015-10-08 14:54
 */
final public class R_S001_B073 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        //params为运行本规则时所传入的参数
        String pageNum = (String) params.get("XTagValue");
        String eleid = (String) params.get("eleid");
        if (Tools.isNotBlank(eleid)) {
            pageNum = eleid;
        } //以R_S008_B003中传入的优先
        AppPage appPage = (AppPage) BeanCtx.getBean("page");
        String pageHtml = appPage.getElementBody(pageNum, true);
        return pageHtml;
    }
}