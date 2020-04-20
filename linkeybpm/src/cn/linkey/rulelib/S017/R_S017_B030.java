package cn.linkey.rulelib.S017;

import java.util.HashMap;

import cn.linkey.factory.BeanCtx;
import cn.linkey.org.DeptModel;
import cn.linkey.rule.LinkeyRule;

/**
 * @RuleName:ORG_移动一个部门,指定一个新的上级部门folderid移动到此部门下面包含人员
 * @author admin
 * @version: 8.0
 * @Created: 2015-03-27 23:03
 */
final public class R_S017_B030 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        //params为运行本规则时所传入的参数
        //示例Json参数:{"NewParentFolderid":"001002","Deptid":"DP1002"}
        String status = "", msg = "";
        String newParentFolderid = (String) params.get("NewParentFolderid"); //新的上级部门的folderid
        DeptModel deptModel = (DeptModel) BeanCtx.getBean("DeptModel");
        deptModel.setDeptid((String) params.get("Deptid")); //部门唯一id必须指定
        int i = deptModel.move(newParentFolderid);
        if (i > 0) {
            status = "ok";
            msg = "部门移动成功!";
        }
        else {
            status = "error";
            msg = "部门移动失败";
        }

        return "{\"status\":\"" + status + "\",\"msg\":\"" + msg + "\"}";
    }
}