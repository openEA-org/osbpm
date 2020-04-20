package cn.linkey.rulelib.S003;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;
import cn.linkey.wf.ProcessEngine;
import cn.linkey.wf.InsUser;
import cn.linkey.wf.ModNode;
import cn.linkey.wf.InsNode;

/**
 * 本规则运行逻辑
 * 
 * <pre>
 *  1.首先结束当前用户的任务实例 
 *  2.看本环节有没有后置事件节点，如果没有则看本环节能不能结束，如果可以结束就直接结束了 
 *  3.如果有，则直接运行本环节后面的路由线，由路由线推进到后置事件中
 * </pre>
 * 
 * @author Administrator
 *
 */
public class R_S003_B020 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        ProcessEngine linkeywf = BeanCtx.getLinkeywf();
        String processid = linkeywf.getProcessid();
        String docUnid = linkeywf.getDocUnid();
        String runNodeid = (String) params.get("WF_RunNodeid"); //获得要运行的节点id
        ModNode insModNode = (ModNode) BeanCtx.getBean("ModNode");
        InsNode insNode = (InsNode) BeanCtx.getBean("InsNode");
        InsUser insUser = (InsUser) BeanCtx.getBean("InsUser");

        //BeanCtx.p("userTask节点("+runNodeid+")用户任务结束开始...");

        //1.结束当前用户所处实例任务
        insUser.endUser(processid, docUnid, runNodeid, BeanCtx.getUserid());

        //2.查找本节点有没有配置后置事件节点,如果没有后置节点则说明有多个用户审批时只要有一个用户提交了就可以直接结束了
        String rearEventNodeid = insModNode.getRearEventNodeid(processid, runNodeid);
        if (Tools.isBlank(rearEventNodeid)) {
            //说明没有配置后置节点,循环类型有用
            String loopType = linkeywf.getCurrentModNodeDoc().g("LoopType"); //循环类型
            if (loopType.equals("1")) {
                //说明环节是的循环类型为标准，只要有一个用户处理就可以结束环节，并推进到本环节的后继路由线
                insNode.endNode(processid, docUnid, runNodeid);
            }
            else if (loopType.equals("2")) {
                //说明是多实例循环，需要本环节所有用户处理完成才能结束本环节,即每一个用户都可以往前推进一次后继环节
                String sql = "select WF_OrUnid from BPM_InsUserList where docUnid='" + docUnid + "' and processid='" + processid + "' and nodeid='" + runNodeid + "' and Status='Current'";
                if (!Rdb.hasRecord(sql)) {
                    insNode.endNode(processid, docUnid, runNodeid); //所有用户已处理完成，可以结束
                }
            }
            else if (loopType.equals("3")) {
                //说明是标准会签模式，所有用户处理完成才能结束本环节
                String sql = "select WF_OrUnid from BPM_InsUserList where docUnid='" + docUnid + "' and processid='" + processid + "' and nodeid='" + runNodeid + "' and Status='Current'";
                if (Rdb.hasRecord(sql)) {
                    return ""; //说明本环节还有用户未处理完成直接返回，停止推进后继路由线
                }
                else {
                    insNode.endNode(processid, docUnid, runNodeid); //所有用户已会签完成，可以结束
                }
            }
        }
        else {
            //说明有配置了后置节点,LoopType循环类型无用，交由后置事件节点去控制
            //本节点的结束由后置节点进行控制，本规则中无需实现,后置事件在结束时会反过来结束本环节
        }

        //3.前面程序都通过后推进到本userTask节点后面的路由线,路由线后面如果接的是后置节点，则会反过来结束本节点
        String nodeType = (String) params.get("WF_StopNodeType"); //检测是否需要停止推进
        if (nodeType == null || !nodeType.equals("userTask")) {
            linkeywf.goToNextNode(runNodeid, params);
        }

        return "";
    }

}
