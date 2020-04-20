package cn.linkey.rulelib.S017;

import java.util.HashMap;

import cn.linkey.factory.BeanCtx;
import cn.linkey.org.DeptModel;
import cn.linkey.rule.LinkeyRule;

/**
 * @RuleName:ORG_修改部门信息
 * @author admin
 * @version: 8.0
 * @Created: 2015-03-27 23:03
 */
final public class R_S017_B028 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        //params为运行本规则时所传入的参数
        //示例Json参数:{"Deptid":"DP1002","ParentFolderid":"root","Folderid":"001002","DeptName":"财务部","OrgClass":"1","DeptFlag":"Department"}
        //如果不想修改其中的信息可以传入空值
        String status = "", msg = "";
        String parentFolderid = (String) params.get("ParentFolderid"); //如果修改上级部门的folderid本部门下的人员并不会移动
        DeptModel deptModel = (DeptModel) BeanCtx.getBean("DeptModel");
        deptModel.setOrgClass((String) params.get("OrgClass")); //架构标识
        deptModel.setFolderName((String) params.get("DeptName")); //部门名称
        deptModel.setParentFolderid(parentFolderid); //上级部门的folderid
        deptModel.setFolderid((String) params.get("Folderid")); //当前部门的folderid
        deptModel.setDeptid((String) params.get("Deptid")); //部门唯一id必须指定
        deptModel.setDeptFlag((String) params.get("DeptFlag")); //部门类型Company表示公司,Department表示部门,Team表示科室,
        int i = deptModel.save();
        if (i > 0) {
            status = "ok";
            msg = "部门修改成功!";
        }
        else {
            status = "error";
            msg = "部门修改失败";
        }

        return "{\"status\":\"" + status + "\",\"msg\":\"" + msg + "\"}";
    }
}