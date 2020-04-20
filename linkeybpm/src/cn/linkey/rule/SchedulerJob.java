package cn.linkey.rule;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import cn.linkey.factory.BeanCtx;
import cn.linkey.util.DateUtil;

/**
 * 定时任务执行程序,通过SchedulerEngine中传入的规则编号去执行定时规则
 * 
 * @author Administrator
 */
public class SchedulerJob implements Job {
    @Override
    public void execute(JobExecutionContext cntxt) throws JobExecutionException {
        //定时规则中默认为admin用户进行运行
        BeanCtx.init("admin", null, null);
        String ruleNum = (String) cntxt.getJobDetail().getJobDataMap().get("RuleNum");
        if (BeanCtx.getSystemConfig("SchedulerDebug").equals("1")) {
            //调试定时规则
            BeanCtx.out(DateUtil.getNow("yyyy-MM-dd hh:mm:ss") + ":定时运行规则" + ruleNum);
        }
        try {
            //String sql="select Status from BPM_RuleList where RuleNum='"+ruleNum+"'";
            //String status=Rdb.getValueBySql(sql);
            // 只执行启用中的定时规则
            //if(status.equals("1")){
            BeanCtx.getExecuteEngine().run(ruleNum);
            //}
        }
        catch (Exception e) {
            BeanCtx.log(e, "E", "定时规则(" + ruleNum + ")执行错误!");
        }
        BeanCtx.close();
    }
}
