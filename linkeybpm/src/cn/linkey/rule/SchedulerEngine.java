package cn.linkey.rule;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.quartz.CronExpression;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.TriggerBuilder;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.util.Tools;

/**
 * 定时规则调度引擎
 * 
 * 本类为单实例类
 * 
 * @author Administrator
 */
public class SchedulerEngine {
    private SchedulerFactory sf;
    private Scheduler sched;
    final private static String triggerGroupName = "TriggerRuleGroup";
    final private static String jobGroupName = "SchedRuleGroup";
    private static SchedulerEngine schEngine = null;

    private SchedulerEngine() {
    }

    /**
     * 获得一个定时规则引擎的单实例
     * 
     * @return
     */
    public static SchedulerEngine getIns() {
        if (schEngine == null) {
            schEngine = new SchedulerEngine();
        }
        return schEngine;
    }

    /**
     * 默认创建一个sf工厂对像
     * 
     * @return
     */
    public boolean run() {
        if (this.sf == null) {
            this.sf = new org.quartz.impl.StdSchedulerFactory();
        }
        return run(sf);
    }
    
    /**
     * 校验cron表达式是否合法
     * @param expressionStr
     * @return
     */
    private boolean isValidExpressionStr(String expressionStr) {
    	return CronExpression.isValidExpression(expressionStr);
    }
    
    /**
     * 获取定时规则的运行状态
     * @param ruleNum 定时规则的编号
     * @return
     * @throws SchedulerException 
     */
    public String getJobStatus(String ruleNum) throws SchedulerException {
    	RuleConfig insRuleConfig = (RuleConfig) BeanCtx.getBean("RuleConfig");
        Document ruleDoc = insRuleConfig.getRuleDoc(ruleNum);
        return ruleDoc.g("JobStatus");
    }
    
    /**
     * 设置定时规则的运行状态
     * @param doc 规则文档
     * @param ruleNum 规则编号
     */
    public void setJobStatus(Document doc, String ruleNum, SchedulerJobStatus status) {
    	BeanCtx.setDocNotEncode();
    	if (doc != null) {
    		doc.s("JobStatus", status.getStatus());
    		doc.save();
    		return;
    	} else {
    		RuleConfig insRuleConfig = (RuleConfig) BeanCtx.getBean("RuleConfig");
            Document ruleDoc = insRuleConfig.getRuleDoc(ruleNum);
            ruleDoc.s("JobStatus", status.getStatus());
            ruleDoc.save();
            return;
    	}
    }

    /**
     * 通过servlet传入sf工厂对像
     * 
     * @param sf SchedulerFactory工厂对像
     * @return
     */
    public boolean run(SchedulerFactory sf) {
        try {
            this.sf = sf;

            sched = sf.getScheduler();

            //1.获得一个调度器
            JobDetail jobDetail; //任务
            CronTrigger trigger; //触发器

            //获得所有定时规则并且加入到任务调度器中去
            String sql = "select * from BPM_RuleList where RuleType='5' and Status='1'";
            Document[] dc = Rdb.getAllDocumentsBySql(sql);
            for (Document doc : dc) {
                String serverName = doc.g("ServerIP"); //如果服务器名称为空则表示在所有集群服务器上运行
                if (serverName.equals("*") || serverName.equals(Tools.getServerIP())) {
                    //创建一个要执行的任务
                    //TaskName任务名称,TaskGroup任务组
                    String taskName = "Task_" + doc.g("RuleNum");
                    jobDetail = JobBuilder.newJob(SchedulerJob.class).withIdentity(taskName, jobGroupName).build();
                    jobDetail.getJobDataMap().put("RuleNum", doc.g("RuleNum"));//传参数到任务类中去
                    if (!sched.checkExists(jobDetail.getKey())) {
                        String cronSchedule = doc.g("RuleSchedule"); //表达式字符串
                        if (Tools.isNotBlank(cronSchedule)) {
                            String triggerName = "Trigger_" + doc.g("RuleNum");
                            
                            // 20181227 新增对定时规则表达式合法性校验
                            if (!isValidExpressionStr(cronSchedule)) {
                            	BeanCtx.out("定时规则(" + taskName + ")的表达式(" + cronSchedule + ")不合法，启动失败！");
                            	continue;
                            }
                            // 20181227 END
                            
                            trigger = (CronTrigger) TriggerBuilder.newTrigger().withIdentity(triggerName, triggerGroupName).withSchedule(CronScheduleBuilder.cronSchedule(cronSchedule)).build();
                            Date ft = sched.scheduleJob(jobDetail, trigger);
                            
                            // 20181228 新增定时规则状态设置(下同)
                            setJobStatus(doc, null, SchedulerJobStatus.RUN);
                            // 20181228 END
                            
                            BeanCtx.out(jobDetail.getKey() + "已经被安排: " + ft + " 定时表达式为: " + trigger.getCronExpression());
                        }
                        else {
                            BeanCtx.out("定时规则(" + taskName + ")的表达式为空!");
                        }
                    }
                    else {
                        BeanCtx.out("任务已经在安排中了(" + taskName + ")");
                    }
                }
            }

            //启动调度器
            if (!sched.isStarted()) {
                sched.start();
                BeanCtx.out("任务调度器启动成功...");
            }
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 启动一个定时规则
     * 
     * @param ruleNum 定时规则的编号
     * @return
     */
    public String startJob(String ruleNum, boolean notify) throws Exception {
        JobDetail jobDetail; //任务
        CronTrigger trigger; //触发器
        RuleConfig insRuleConfig = (RuleConfig) BeanCtx.getBean("RuleConfig");
        Document ruleDoc = insRuleConfig.getRuleDoc(ruleNum);
        String serverName = ruleDoc.g("ServerIP"); //如果服务器名称为空则表示在所有集群服务器上运行
        if (serverName.equals("*") || serverName.equals(Tools.getServerIP())) {
            String taskName = "Task_" + ruleNum;
            jobDetail = JobBuilder.newJob(SchedulerJob.class).withIdentity(taskName, jobGroupName).build();
            jobDetail.getJobDataMap().put("RuleNum", ruleNum);//传参数到任务类中去
            if (!sched.checkExists(jobDetail.getKey())) {
                String cronSchedule = ruleDoc.g("RuleSchedule"); //表达式字符串
                if (Tools.isNotBlank(cronSchedule)) {
                    String triggerName = "Trigger_" + ruleNum;
                    
                    // 20181227 新增对定时规则表达式合法性校验
                    if (!isValidExpressionStr(cronSchedule)) {
                    	String msg = "定时规则(" + taskName + ")的表达式(" + cronSchedule + ")不合法，启动失败！";
                    	BeanCtx.out(msg);
                        return msg;
                    }
                    // 20181227 END
                    
                    trigger = (CronTrigger) TriggerBuilder.newTrigger().withIdentity(triggerName, triggerGroupName).withSchedule(CronScheduleBuilder.cronSchedule(cronSchedule)).build();
                    Date ft = sched.scheduleJob(jobDetail, trigger);
                    notifyOtherServer(ruleNum, "startJob", notify);
                    setJobStatus(ruleDoc, null, SchedulerJobStatus.RUN);
                    String msg = jobDetail.getKey() + "已经被安排: " + ft + " 定时表达式为: " + trigger.getCronExpression();
                    BeanCtx.out(msg);
                    return msg;
                }
                else {
                    String msg = "错误:定时规则(" + ruleNum + ")没有指定表达式或表达式为空或处于禁用状态，启动失败!";
                    BeanCtx.out(msg);
                    return msg;
                }
            }
            else {
                String msg = "错误:定时规则已经启动，不能重复启动!";
                BeanCtx.out(msg);
                return msg;
            }
        }
        else {
            notifyOtherServer(ruleNum, "startJob", notify);
            String msg = "提示:当前定时规则没有指定在本服务器上运行，系统已通知其他集群服务器启动本定时规则";
            BeanCtx.out(msg);
            return msg;
        }
    }

    /**
     * 从定时任务管理器中移除一个定时任务
     * 
     * @param jobkey
     * @return
     * @throws SchedulerException
     */
    public boolean deleteJob(String ruleNum, boolean notity) throws SchedulerException {
        String taskName = "Task_" + ruleNum;
        JobDetail jobDetail = JobBuilder.newJob(SchedulerJob.class).withIdentity(taskName, "SchedRuleGroup").build();
        try {
            sched.deleteJob(jobDetail.getKey());
            notifyOtherServer(ruleNum, "deleteJob", notity);
            BeanCtx.out(jobDetail.getKey() + " 定时规则被成功移除....");
            return true;
        }
        catch (Exception e) {
            BeanCtx.log(e, "E", "");
            return false;
        }
    }

    /**
     * 暂停一个定时规则的运行
     * 
     * @param ruleNum
     * @return
     */
    public String pauseJob(String ruleNum, boolean notify) {
        String msg = "";
    	String taskName = "Task_" + ruleNum;
        JobDetail jobDetail = JobBuilder.newJob(SchedulerJob.class).withIdentity(taskName, jobGroupName).build();
        try {
        	if (!"1".equals(getJobStatus(ruleNum))) {
        		msg = "无法暂停一个不处于运行状态下的定时规则！";
        		BeanCtx.out(msg);
        		return msg;
        	}
            sched.pauseJob(jobDetail.getKey());
            notifyOtherServer(ruleNum, "pauseJob", notify);
            setJobStatus(null, ruleNum, SchedulerJobStatus.PAUSE);
            msg = jobDetail.getKey() + " 定时规则被成功暂停....";
            BeanCtx.out(msg);
            return msg;
        }
        catch (Exception e) {
            BeanCtx.log(e, "E", "");
            return "发送异常，请查看后台管理日记!";
        }
    }

    /**
     * 恢复一个暂停状态的定时规则为运行状态
     * 
     * @param ruleNum
     * @return
     */
    public String resumeJob(String ruleNum, boolean notify) {
        String msg = "";
    	String taskName = "Task_" + ruleNum;
        JobDetail jobDetail = JobBuilder.newJob(SchedulerJob.class).withIdentity(taskName, "SchedRuleGroup").build();
        try {
        	if (!"2".equals(getJobStatus(ruleNum))) {
        		msg = "无法恢复一个不处于暂停状态下的定时规则！";
        		return msg;
        	}
            sched.resumeJob(jobDetail.getKey());
            notifyOtherServer(ruleNum, "resumeJob", notify);
            setJobStatus(null, ruleNum, SchedulerJobStatus.RUN);
            msg = jobDetail.getKey() + " 定时规则被成功恢复运行....";
            BeanCtx.out(msg);
            return msg;
        }
        catch (Exception e) {
            BeanCtx.log(e, "E", "");
            return "发送异常，请查看后台管理日记!";
        }
    }

    /**
     * 所有正在运行的job
     * 
     * @return
     * @throws SchedulerException
     */
    public String getRunningJob() throws SchedulerException {
        String jobStr = "";
        List<JobExecutionContext> executingJobs = sched.getCurrentlyExecutingJobs();
        for (JobExecutionContext executingJob : executingJobs) {
            JobDetail jobDetail = executingJob.getJobDetail();
            JobKey jobKey = jobDetail.getKey();
            jobStr += jobKey.getName();
        }
        return jobStr;
    }

    /**
     * 暂停调度中所有的job任务
     * 
     * @throws SchedulerException
     */
    public void pauseAll(boolean notify) throws SchedulerException {
        notifyOtherServer("*", "pauseAll", notify);
        sched.pauseAll();
    }

    /**
     * 恢复调度中所有的job的任务
     * 
     * @throws SchedulerException
     */
    public void resumeAll(boolean notify) throws SchedulerException {
        notifyOtherServer("*", "resumeAll", notify);
        sched.resumeAll();
    }

    public Scheduler getSched() {
        return sched;
    }

    public void setSched(Scheduler sched) {
        this.sched = sched;
    }

    /**
     * 停止作业调度器的运行
     * 
     * @return
     */
    public boolean shutdown(boolean notify) {
        try {
            sched.shutdown(true);
            notifyOtherServer("*", "shutdown", notify);
            BeanCtx.out("作业调度引擎已成功停止");
            return true;
        }
        catch (Exception e) {
            BeanCtx.log(e, "E", "作业调度引擎停止失败");
            return false;
        }
    }

    /**
     * 同时同步清除其他所有服务器的缓存数据
     * 
     * @param cacheName
     * @param key
     * @param configid
     */
    public static void notifyOtherServer(String ruleNum, String actionid, boolean notify) {
        if (notify == false) {
            return;
        } //无需同步

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("RuleNum", ruleNum);
        params.put("Actionid", actionid);
        try {
            BeanCtx.getExecuteEngine().run("R_S001_B078", params); //运行一个规则去进行定时规则同步
        }
        catch (Exception e) {
            BeanCtx.log(e, "W", "定时规则同步失败(" + ruleNum + "=" + actionid + ")");
        }
    }

    /**
     * 根据传入的ruleNum,actionid运行相应的方法，用来同步各服务器之间的定时规则使用
     * 
     * @param ruleNum
     * @param actionid
     */
    public void runActionForServer(String ruleNum, String actionid) throws Exception {
        if (actionid.equalsIgnoreCase("shutdown")) {
            this.shutdown(false);
        }
        else if (actionid.equalsIgnoreCase("resumeAll")) {
            this.resumeAll(false);
        }
        else if (actionid.equalsIgnoreCase("pauseAll")) {
            this.pauseAll(false);
        }
        else if (actionid.equalsIgnoreCase("resumeJob")) {
            this.resumeJob(ruleNum, false);
        }
        else if (actionid.equalsIgnoreCase("pauseJob")) {
            this.pauseJob(ruleNum, false);
        }
        else if (actionid.equalsIgnoreCase("deleteJob")) {
            this.deleteJob(ruleNum, false);
        }
        else if (actionid.equalsIgnoreCase("startJob")) {
            this.startJob(ruleNum, false);
        }
        else if (actionid.equalsIgnoreCase("run")) {
            this.run();
        }
    }
}
