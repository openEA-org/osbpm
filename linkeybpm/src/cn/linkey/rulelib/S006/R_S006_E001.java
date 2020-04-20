package cn.linkey.rulelib.S006;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.rule.LinkeyRule;

/**
 * @RuleName:组织架构首页统计
 * @author admin
 * @version: 8.0
 * @Created: 2014-04-21 10:16
 */
final public class R_S006_E001 implements LinkeyRule {

    @Override
    public String run(HashMap<String, Object> params) {
        //获取事件运行参数
        Document pageDoc = (Document) params.get("PageDoc"); //页面配置文档
        Document doc = (Document) params.get("DataDoc"); //数据主文档
        String eventName = (String) params.get("EventName");//事件名称
        if (eventName.equals("onPageOpen")) {
            return onPageOpen(doc, pageDoc);
        }
        return "1";
    }

    public String onPageOpen(Document doc, Document pageDoc) {
        //可以对页面的[X]字段名[/X]进行初始化如:doc.s("fdname",fdvalue)
        String sql = "select count(*) as TotalNum from BPM_OrgDeptList";
        String deptNum = Rdb.getValueBySql(sql);
        doc.s("DeptNum", deptNum);

        sql = "select count(*) as TotalNum from BPM_OrgUserList";
        String userNum = Rdb.getValueBySql(sql);
        doc.s("UserNum", userNum);

        sql = "select count(*) as TotalNum from BPM_OrgRoleList";
        String rolesNum = Rdb.getValueBySql(sql);
        doc.s("RolesNum", rolesNum);

        return "1"; //成功必须返回1，否则表示退出并显示返回的字符串
    }

}