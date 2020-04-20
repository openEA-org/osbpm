package cn.linkey.rule;

import cn.linkey.dao.RdbCache;
import cn.linkey.factory.BeanCtx;
import cn.linkey.util.Tools;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

// 执行规则引擎
public class ExecuteEngine {
    /**
     * 执行一个规则
     * 
     * @param ruleNum 规则编号
     * @param params 执行规则时需传入的参数
     * @return 返回字符串
     */
    public String run(String ruleNum, HashMap<String, Object> params) throws Exception {
        long ts = System.currentTimeMillis();
        if (Tools.isBlank(ruleNum)) {
            //BeanCtx.log("E", "ExecuteEngine不能运行空规则,传入的规则编号为空值!");
            //if (BeanCtx.getResponse() != null) {
            //	BeanCtx.showErrorMsg("Error:wf_num cannot be empty!");
            //}
            throw new Exception("ExecuteEngine不能运行空规则,传入的规则编号为空值!");
            //return "";
        }

        // 执行规则
        String ruleMsg = "";
        LinkeyRule insLinkeyRule = (LinkeyRule) BeanCtx.getBeanByRuleNum(ruleNum);
        if (insLinkeyRule != null) {
            ruleMsg = insLinkeyRule.run(params);
        }
        long te = System.currentTimeMillis();
        Performancelog(ruleNum, "", te - ts);
        return ruleMsg;
    }

    /**
     * 执行一个规则，不需要传入参数
     * 
     * @param ruleNum 规则编号
     * @return 字符串
     */
    public String run(String ruleNum) throws Exception {
        long ts = System.currentTimeMillis();
        String result = run(ruleNum, new HashMap<String, Object>());
        long te = System.currentTimeMillis();
        Performancelog(ruleNum, "", te - ts);
        return result;
    }

    /**
     * 性能记录
     * 
     * @param wf_num 元素编号
     * @param appid 应用编号
     * @param totalTime 总耗时
     */
    @SuppressWarnings("unchecked")
    public void Performancelog(String wf_num, String appid, long totalTime) {

        //看是否开启了性能监控
        //BeanCtx.out("StopPerformancelog="+BeanCtx.getSystemConfig("StopPerformancelog"));
        if (BeanCtx.getSystemConfig("StopPerformancelog").equals("1")) {
            return;
        }

        //1.首先看有没有性能缓存对像
        ConcurrentHashMap<String, HashMap<String, String>> performanceCache = (ConcurrentHashMap<String, HashMap<String, String>>) RdbCache.get("PerformanceCache");
        if (performanceCache == null) {
            performanceCache = new ConcurrentHashMap<String, HashMap<String, String>>(1000);
            RdbCache.put("PerformanceCache", performanceCache);
        }

        //如果应用编号为空则从wf_num中计算应用编号
        if (Tools.isBlank(appid)) {
            appid = Tools.getAppidFromElNum(wf_num);
        }

        //2.在缓存对像中查看wf_num是否已经有记录,没有就创建一个
        HashMap<String, String> elitem = performanceCache.get(wf_num);
        if (elitem == null) {
            elitem = new HashMap<String, String>();
            elitem.put("wf_num", wf_num);
            elitem.put("WF_Appid", appid);
            elitem.put("AverageNum", "1"); //平均记录次数，算平均时间时使用
            elitem.put("AverageTime", String.valueOf(totalTime)); //平均运行时间
            elitem.put("MaxTime", String.valueOf(totalTime)); //最大运行时间
            elitem.put("MinTime", String.valueOf(totalTime)); //最小运行时间
            elitem.put("TotalTime", String.valueOf(totalTime)); //总运行时间
            elitem.put("TotalAccess", "1");
            performanceCache.put(wf_num, elitem);
        }
        else {
            long totalAccess = Long.parseLong(elitem.get("TotalAccess")) + 1; //总访问次数加1
            elitem.put("TotalAccess", String.valueOf(totalAccess)); //总访问次数

            long maxTime = Integer.parseInt(elitem.get("MaxTime")); //获得当前的最大运行时间
            if (totalTime > maxTime) {
                elitem.put("MaxTime", String.valueOf(totalTime)); //重设最大运行时间
            }

            long minTime = Integer.parseInt(elitem.get("MinTime")); //获得当前的最小运行时间
            if (totalTime < minTime) {
                elitem.put("MinTime", String.valueOf(totalTime)); //重设最小运行时间
            }

            int avgNum = Integer.parseInt(elitem.get("AverageNum")); //平均记数次
            if (avgNum < 10000) {
                avgNum++;
                long allRunTime = Long.parseLong(elitem.get("TotalTime")) + totalTime; //总运行时间
                long avgTime = allRunTime / avgNum; //平均运行时间
                elitem.put("TotalTime", String.valueOf(allRunTime)); //总运行时间
                elitem.put("AverageTime", String.valueOf(avgTime)); //平均运行时间
                elitem.put("AverageNum", String.valueOf(avgNum)); //平均记数器加1
            }
            else {
                elitem.put("AverageTime", String.valueOf(totalTime)); //平均运行时间
                elitem.put("TotalTime", String.valueOf(totalTime)); //总运行时间
                elitem.put("AverageNum", "1"); //平均记数器=1
            }
        }

    }

}
