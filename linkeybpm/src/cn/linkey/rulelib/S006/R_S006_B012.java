package cn.linkey.rulelib.S006;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * @RuleName:矩阵添加或删除部门用户
 * @author admin
 * @version: 8.0
 * @Created: 2014-04-26 13:58
 */
final public class R_S006_B012 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) {
        // params为运行本规则时所传入的参数
        String action = BeanCtx.g("WF_Action");
        if (action.equals("add")) {
            adduser();
        }
        else if (action.equals("remove")) {
            removeuser();
        }
        return "";
    }

    public void adduser() {
        // 存或更新用户与部门的关系记录
        String orgClass = BeanCtx.g("OrgClass", true);
        String folderid = BeanCtx.g("Folderid", true);
        String userlist = BeanCtx.g("UserList", true);
        String deptid = BeanCtx.getLinkeyUser().getDeptidByFolderid(folderid, orgClass);
        String[] userarray = Tools.split(userlist);
        int i = 0;
        for (String userid : userarray) {
            String sql = "select * from BPM_OrgUserDeptMap where Userid='" + userid + "' and Deptid='" + deptid + "'";
            Document mapdoc = Rdb.getDocumentBySql(sql);
            mapdoc.s("OrgClass", orgClass);
            mapdoc.s("Userid", userid);
            mapdoc.s("Folderid", folderid);
            mapdoc.s("Deptid", deptid);
            sql = "select * from BPM_OrgUserDeptMap where CurrentFlag='1' and Userid='" + userid + "' and OrgClass='" + orgClass + "'";
            if (Rdb.hasRecord(sql)) {
                // 如果已存在CurrentFlag标识为1的则不能再标识了，必须要由用户主动切换到那个部门才可以了
                mapdoc.s("CurrentFlag", "0");
            }
            else {
                mapdoc.s("CurrentFlag", "1");
            }
            mapdoc.s("MainDept", "2");
            mapdoc.save();
            i++;
        }
        BeanCtx.p(Tools.jmsg("ok", "共成功添加(" + i + ")个用户!"));
    }

    public void removeuser() {
        // 移除用户
        String orgClass = BeanCtx.g("OrgClass");
        String folderid = BeanCtx.g("Folderid");
        String userlist = BeanCtx.g("UserList");
        String[] userarray = Tools.split(userlist);
        int i = 0;
        for (String userid : userarray) {
            String sql = "delete from BPM_OrgUserDeptMap where Userid='" + userid + "' and OrgClass='" + orgClass + "' and folderid='" + folderid + "'";
            Rdb.execSql(sql);
            i++;
        }
        BeanCtx.p(Tools.jmsg("ok", "共成功移除(" + i + ")个用户!"));
    }
}