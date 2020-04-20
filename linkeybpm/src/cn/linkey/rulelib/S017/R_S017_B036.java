package cn.linkey.rulelib.S017;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;
import cn.linkey.util.Vml2Svg;
import cn.linkey.wf.NodeUser;

/**
 * @RuleName:Engine_输出SVG流程图
 * @author admin
 * @version: 8.0
 * @Created: 2015-12-02 20:45
 */
final public class R_S017_B036 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        //params为运行本规则时所传入的参数
        //示例参数:{"Processid":"流程id","DocUnid":"流程实列id"}
        //返回的SVGXML为utf-8编码的，在客户端需要解码后才是svg

        String processid = (String) params.get("Processid"); //获得流程id
        String docUnid = (String) params.get("DocUnid"); //流程实例id
        //	    processid="fe3bb2d403e8d04f9a09c84092dd79b1b07b";
        //	    docUnid="171026180ac07041380b0b104356807934a2";
        String sql = "select GraphicBody from BPM_ModGraphicList where Processid='" + Rdb.formatArg(processid) + "'";
        String xmlBody = Rdb.getValueBySql(sql);
        xmlBody = Rdb.deCode(xmlBody, false);
        xmlBody = Vml2Svg.getSvgXml(xmlBody); //把vml转为svg

        //获得活动和已结束的节点id
        NodeUser nodeUser = (NodeUser) BeanCtx.getBean("NodeUser");
        String currentNodeid = nodeUser.getCurrentNodeid(docUnid);
        sql = "select Nodeid from BPM_InsNodeList where docUnid='" + Rdb.formatArg(docUnid) + "' and Status='End'  and NodeType<>'Process' order by StartTime";
        String endNodeid = Rdb.getValueBySql(sql);
        String jsonStr = "{\"SVGXML\":\"" + Tools.encode(xmlBody) + "\",\"CurrentNodeid\":\"" + currentNodeid + "\",\"EndNodeid\":\"" + endNodeid + "\"}";
        BeanCtx.p(jsonStr);
        return jsonStr;
    }
}