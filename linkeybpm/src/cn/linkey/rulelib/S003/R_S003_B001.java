package cn.linkey.rulelib.S003;

import java.util.HashMap;

import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.wf.ProcessEngine;
import cn.linkey.wf.InsUser;

/**
 * 本规则负责传阅，把COPYUSER传阅给指定的用户 参数说明:需传入WF_NextUserList中的map参数，map的key为COPYUSER
 * 
 * @author Administrator
 */
public class R_S003_B001 implements LinkeyRule {
    @Override
    @SuppressWarnings("unchecked")
    public String run(HashMap<String, Object> params) throws Exception {
        ProcessEngine linkeywf = BeanCtx.getLinkeywf();
        String processid = linkeywf.getProcessid();

        //1.获得并启动要传阅的用户
        int x = 0;
        String copyUserList = ((HashMap<String, String>) params.get("WF_NextUserList")).get("COPYUSER"); //获得要传阅的用户列表
        if (copyUserList != null) {
            InsUser insUser = (InsUser) BeanCtx.getBean("InsUser");
            x = insUser.copyToUser(processid, linkeywf.getCurrentNodeid(), copyUserList);
        }

        linkeywf.setRunStatus(true);//表示运行成功
        return String.valueOf(x);
    }

}
