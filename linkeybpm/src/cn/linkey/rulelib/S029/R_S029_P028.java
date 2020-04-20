package cn.linkey.rulelib.S029;

import java.util.*;
import cn.linkey.dao.*;
import cn.linkey.factory.*;
import cn.linkey.wf.*;
import cn.linkey.rule.LinkeyRule;

/**
 * @RuleName:强制跳转到任意节点
 * @author admin
 * @version: 8.0
 * @Created: 2015-03-28 08:31
 */
final public class R_S029_P028 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        //params为运行本规则时所传入的参数
        //1.开启事务并初始化工作流引擎
        String processid = "fe3bb2d403e8d04f9a09c84092dd79b1b07b";
        String docUnid = "f0e50ba6064f5041b9088c003fc5c9588ae6";
        String userid = BeanCtx.getUserid();
        String nextNodeid = "T10004";
        String nextUserid = "admin";

        Rdb.setAutoCommit(false); //开启事务
        ProcessEngine linkeywf = new ProcessEngine();
        BeanCtx.setLinkeywf(linkeywf); //把工作流引擎对像设置为全局变量对像
        linkeywf.init(processid, docUnid, userid, ""); //初始化工作流引擎

        //2.准备节点参数
        HashSet<String> nextNodeSet = new HashSet<String>();//提交的后继节点列表
        nextNodeSet.add(nextNodeid);

        //3.准备用户参数
        HashMap<String, String> nextUserMap = new HashMap<String, String>(); //提交后继环节的用户列表
        nextUserMap.put(nextNodeid, nextUserid);

        //4.准备启动流程的节点和用户参数
        HashMap<String, Object> runParams = new HashMap<String, Object>();
        runParams.put("WF_NextNodeid", nextNodeSet); //要提交的节点类型为HashSet<String> 值为nodeid多个逗号分隔
        runParams.put("WF_NextUserList", nextUserMap); //要提交的用户类型为HashMap<String,String> key为nodeid,value为userid多个逗号分隔,不包含部门id信息
        runParams.put("WF_Remark", "同意");

        //5.提交工作流引擎运行
        String msg = linkeywf.run("GoToAnyNode", runParams);

        BeanCtx.p("运行结果=" + msg);

        return "";
    }
}