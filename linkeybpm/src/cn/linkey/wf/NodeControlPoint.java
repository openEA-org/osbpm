package cn.linkey.wf;

import cn.linkey.util.*;
import cn.linkey.factory.*;
import cn.linkey.doc.*;

import java.sql.Connection;
import java.util.HashMap;
import cn.linkey.dao.Rdb;

public class NodeControlPoint {
    /**
     * 管控点检测
     */
    public void runControlPoint() throws Exception {
        //1.首先获得当前环节的所有控制点计算规则
        ProcessEngine linkeywf = BeanCtx.getLinkeywf();
        String nodeControlPoint = BeanCtx.getSystemConfig("NodeControlPoint"); //获得所有管控点的配置值
        if (Tools.isBlank(nodeControlPoint) || Tools.isBlank(linkeywf.getCurrentNodeid())) {
            return;
        }

        //2.开始检测所有节点中的管控点
        String[] allConfigArray = Tools.split(nodeControlPoint);
        for (String configItem : allConfigArray) {
            //开始获得环节中的控制点配置值
            String controlNodeVal = linkeywf.getCurrentModNodeDoc().g(configItem);
            if (Tools.isNotBlank(controlNodeVal)) {
                String[] controlPointArray = Tools.split(controlNodeVal); //一次可以配置多个控制点用逗号分隔的
                for (String controlPointidVal : controlPointArray) {
                    String sql = "select * from BPM_MCControlPoint where ControlPointid='" + controlPointidVal + "'";
                    Document configDoc = Rdb.getDocumentBySql(sql);//控制点的配置文档
                    String computeRuleNum = configDoc.g("ComputeRuleNum"); //管控点计算规则编号
                    String replyRuleNum = configDoc.g("ReplyRuleNum"); //管控点应对规则编号
                    if (Tools.isBlank(computeRuleNum)) {
                        continue;
                    }

                    //计算规则与应对规则中可以通过runParams对像传参数，因为是引用对像类型
                    HashMap<String, Object> runParams = new HashMap<String, Object>();
                    runParams.put("ConfigDoc", configDoc); //管控点的配置文档对像

                    //1.先运行计算规则
                    String computeResult = BeanCtx.getExecuteEngine().run(computeRuleNum, runParams); //运行计算规则并获得结果
                    writelog(configDoc, computeResult, "Compute"); //计算结果存日记

                    //2.运行应对规则
                    runParams.put("ComputeResult", computeResult); //把计算规则的结果传入到应对规则中去，作为参数
                    if (Tools.isNotBlank(replyRuleNum)) {
                        String replyResult = BeanCtx.getExecuteEngine().run(replyRuleNum, runParams);//运行应对规则,并取得返回的结果字符串
                        writelog(configDoc, replyResult, "Reply");//应对结果存日记
                    }

                }
            }
        }

    }

    /**
     * 记录日记
     * 
     * @param configDoc 控制点的配置文档
     * @param result 计算结果
     * @param logType 日记类型
     */
    private void writelog(Document configDoc, String result, String logType) {
        Document doc = BeanCtx.getDocumentBean("BPM_MCControlPointLog");
        doc.s("DocUnid", BeanCtx.getLinkeywf().getDocUnid());
        doc.s("LogType", logType);
        doc.s("ControlPointType", configDoc.g("ControlPointType"));
        doc.s("ControlPointName", configDoc.g("ControlPointName"));
        doc.s("ControlPointid", configDoc.g("ControlPointid"));
        doc.s("Processid", BeanCtx.getLinkeywf().getProcessid());
        doc.s("Nodeid", BeanCtx.getLinkeywf().getCurrentNodeid());
        doc.s("NodeName", BeanCtx.getLinkeywf().getCurrentModNodeDoc().g("NodeName"));
        doc.s("Subject", BeanCtx.getLinkeywf().getDocument().g("Subject"));
        doc.s("Userid", BeanCtx.getUserid());
        doc.s("Result", result);
        doc.s("ActionNum", BeanCtx.getLinkeywf().getActionNum());

        Connection conn = null;
        try {
            conn = Rdb.getNewConnection("default"); //创建一个新的链接对像用来存，不然在回滚的时候日记也会被回滚掉
            doc.save(conn);
        }
        catch (Exception e) {
            BeanCtx.log(e, "E", "记录日记时出错!");
        }
        finally {
            Rdb.close(conn);
        }
    }

}
