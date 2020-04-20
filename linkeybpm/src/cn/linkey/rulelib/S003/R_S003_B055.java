package cn.linkey.rulelib.S003;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.wf.ProcessEngine;

/**
 * @RuleName:恢复流程
 * @author admin
 * @version: 8.0
 * @Created: 2014-05-20 17:51 参数说明:无需传入参数
 */
final public class R_S003_B055 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) {
        //params为运行本规则时所传入的参数
        ProcessEngine linkeywf = BeanCtx.getLinkeywf();
        String docUnid = linkeywf.getDocUnid();
        if (!linkeywf.getDocument().g("WF_Status").equals("Pause")) {
            return "错误:不能恢复未暂停的流程!";
        }

        //1.把所有暂停的用户实例标识为活动
        String sql = "update BPM_InsUserList set Status='Current' where Status='Pause' and DocUnid='" + docUnid + "'";
        Rdb.execSql(sql);

        //2.把所有暂停的活动环节标识为活动
        sql = "update BPM_InsNodeList set Status='Current' where Status='Pause' and DocUnid='" + docUnid + "'";
        Rdb.execSql(sql);

        //3.把主文档的状态改为Current
        linkeywf.getDocument().s("WF_Status", "Current");

        linkeywf.setRunStatus(true);//表示运行成功
        return "流程已成功恢复流转!";
    }
}