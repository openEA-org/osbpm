package cn.linkey.rulelib.S029;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.DateUtil;
import cn.linkey.util.Tools;

/**
 * @RuleName:检测流程环节超时(半小时执行一次)
 * @author admin
 * @version: 8.0
 * @Created: 2014-06-07 22:24
 */
final public class R_S029_T005 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {

        //得到所有有时限设置的用户实例
        String sql = "select * from BPM_InsUserList where Status='Current' and (ExceedTime<>'0' or LimitTime<>'')";
        Document[] dc = Rdb.getAllDocumentsBySql(sql);
        String endTime = DateUtil.getNow(); //现在的时间
        for (Document doc : dc) {
            String overDateNum = doc.g("OverDateNum"); //用户实例超时次数0都表示没有超时
            if (Tools.isNotBlank(doc.g("LimitTime"))) {
                //如果限制完成时间不为空则优先以此时间为准
                if (DateUtil.lessTime(doc.g("LimitTime"), endTime) == false) {
                    //说明限制时间已大于现在的时间，用户实例已超时

                    //这个要先运行
                    if (overDateNum.equals("0") || !doc.g("RepeatTime").equals("0")) {
                        //overDateNum=0表示是首次触发规则,RepeatTime表示需要重复触发规则
                        if (runOverTimeRule(doc)) {
                            setDocOverTimeInfo(doc); //记录超时信息
                        }
                    }
                }
            }
            else {
                //在环节中有指定节点持续时间单位为小时
                String firstTriggerTime = doc.g("FirstTriggerTime");
                if (Tools.isBlank(firstTriggerTime)) {
                    firstTriggerTime = "0";
                }
                String difTime = DateUtil.getDifTime(doc.g("StartTime"), endTime);
                int arriveHour = Integer.valueOf(difTime) / 60; //得到用户已到达时间/小时
                int exceedTime = Integer.valueOf(doc.g("ExceedTime")); //环节中设定的持续时间
                int noteTime = exceedTime - Integer.valueOf(doc.g("firstTriggerTime")); //减去第一次触发提前时间得到触发规则的时间
                if (arriveHour >= noteTime && overDateNum.equals("0")) {
                    //说明用户实例到达时间已到达预定的提前触发事件时间
                    if (runOverTimeRule(doc)) {
                        setDocOverTimeInfo(doc); //这个要后运行，因为要改变节点文档字段
                    }
                }
                else if (arriveHour >= exceedTime) {
                    //说明用户实例到达时间已超过环节中设定的持续时间
                    if (overDateNum.equals("0") || !doc.g("RepeatTime").equals("0")) {
                        //overDateNum=0表示是首次触发规则,RepeatTime表示需要重复触发规则
                        if (runOverTimeRule(doc)) {
                            setDocOverTimeInfo(doc); //这个要后运行，因为要改变节点文档字段
                        }
                    }
                }
            }
        }

        return "";
    }

    /**
     * 运行超时后的规则
     * 
     * @param doc
     */
    public boolean runOverTimeRule(Document doc) throws Exception {
        if (!doc.g("RepeatTime").equals("0")) {
            //需要判断是否已到达重复间隔的时间
            String lastTime = doc.g("OverDateTime").trim(); //20180516
            String endTime = DateUtil.getNow();
            String difTime = DateUtil.getAllDifTime(lastTime, endTime); //获得上次触发与现在的间隔时间,不进行节假日分析
            int nowRepeatTime = Integer.valueOf(difTime) / 60; //已间隔小时
            int nodeRepeatTime = Integer.valueOf(doc.g("RepeatTime")); //节点中设定的重复间隔时间
            if (nowRepeatTime <= nodeRepeatTime) {
                //重复间隔时间还没有到，不触发事件
                return false;
            }
        }

        //运行环节中指定的超时规则
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("UserDoc", doc); //传入用户实例文档到超时规则中去,在超时规则中可以通过此参数获得是那个用户实例超时
        BeanCtx.getEventEngine().run(doc.g("Processid"), doc.g("Nodeid"), "NodeTimeout", params); //触发流程超时事件

        return true;
    }

    /**
     * 记录用户的超时信息,本方法要在runOverTimeRule()方法运行之后再运行
     */
    public void setDocOverTimeInfo(Document doc) {
        //1.首先记录用户的超时次数
        String overDateNum = doc.g("OverDateNum");
        if (Tools.isBlank(overDateNum)) {
            overDateNum = "1";
        }
        else {
            overDateNum = String.valueOf(Integer.valueOf(overDateNum) + 1);
        }
        doc.s("OverDateNum", overDateNum);

        //2.记录用户最后一次超时的时间
        doc.s("OverDateTime", DateUtil.getNow());

        doc.save();
    }

}