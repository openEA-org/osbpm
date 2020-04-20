package cn.linkey.rule;

import java.util.HashSet;

import cn.linkey.app.AppUtil;
import cn.linkey.dao.Rdb;
import cn.linkey.dao.RdbCache;
import cn.linkey.doc.Document;

/**
 * 本类为单例类,本类可以使用缓存功能
 * 
 * 主要获得规则文档
 * 
 * @author Administrator
 */
public class RuleConfig {

    /**
     * 根据规则编号返回规则文档对像
     * 
     * @param ruleNum
     * @return 返回规则文档
     */
    public Document getRuleDoc(String ruleNum) {
        //根据规则编号获得规则文档对像
        return AppUtil.getDocByid("BPM_RuleList", "RuleNum", ruleNum, true);
    }

    /**
     * 获得所有过虑器规则的文档对像
     * 
     * @return
     */
    public HashSet<String> getAllFilterRuleNum() {
        return null;
    }

    /**
     * 根据流程id和事件id和节点id获得配置表中配置的所有规则编号的集合
     * 
     * @param processid 流程id
     * @param eventid 事件id
     * @param nodeid 流程环节id
     * @return 返回所有事件规则中配置所对应的规则编号集合
     */
    public Document[] getEventConfig(String processid, String eventid, String nodeid) {
        String sql = "select RuleNum,Params from BPM_EngineEventConfig where  (processid='" + processid + "' or processid='*') and (nodeid='" + nodeid + "' or nodeid='*') and eventid='" + eventid
                + "' order by SortNum";
        return Rdb.getAllDocumentsBySql(sql);
    }

    /**
     * 判读Engine Actionid的类型是否是编辑状态下的Action动作
     * 
     * @param actionid 动作id号
     * @return true表示是，false表示否
     */
    public boolean isEditEngineAction(String actionid) {
        String sql = "select IsReadAction from BPM_EngineActionConfig where Actionid='" + actionid + "'";
        if (Rdb.getValueBySql(sql).equals("0")) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * 根据动作编号获得此动作所对应的规则编号,从BPM_EngineActionConfig表中进行转换
     * 
     * @param actionid 动作编号
     * @return 返回对应的规则编号
     */
    public String getRuleNumByEngineActionid(String actionid) {
        //动作Action应该进行缓存
        String ruleNum = (String) RdbCache.getSystemCache("BPM_EngineActionConfig", actionid);
        return ruleNum;
    }

    /**
     * 获得节点类型配置参数
     * 
     * @param nodeType 节点类型
     * @param ruleType 为StartRuleNum,EndRuleNum
     * @return
     */
    public String getNodeTypeConfig(String nodeType, String ruleType) {
        return Rdb.getValueBySql("select " + ruleType + " from BPM_EngineNodeTypeConfig where NodeType='" + nodeType + "'");
    }

}
