package cn.linkey.rulelib.S017;

import java.util.HashMap;

import cn.linkey.factory.BeanCtx;
import cn.linkey.org.DeptModel;
import cn.linkey.rule.LinkeyRule;

/**
 * @RuleName:ORG_删除一个部门
 * @author admin
 * @version: 8.0
 * @Created: 2015-03-27 23:03
 */
final public class R_S017_B029 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        //params为运行本规则时所传入的参数
        //示例Json参数:{"Deptid":"DP1002"}
        //如果部门下面还有人员不允许删除部门，必须先删除人员才能删除部门
        String status = "", msg = "";
        String deptid = (String) params.get("Deptid"); //如果修改上级部门的folderid本部门下的人员并不会移动
        DeptModel deptModel = (DeptModel) BeanCtx.getBean("DeptModel");
        deptModel.setDeptid((String) params.get("Deptid")); //部门唯一id必须指定
        int i = deptModel.delete();
        if (i > 0) {
            status = "ok";
            msg = "部门删除成功!";
        }
        else {
            status = "error";
            msg = "部门删除失败";
        }

        return "{\"status\":\"" + status + "\",\"msg\":\"" + msg + "\"}";
    }
}