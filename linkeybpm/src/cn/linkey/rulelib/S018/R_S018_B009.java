package cn.linkey.rulelib.S018;

import java.util.*;

import cn.linkey.dao.*;
import cn.linkey.util.*;
import cn.linkey.doc.*;
import cn.linkey.factory.*;
import cn.linkey.wf.*;
import cn.linkey.rest.RestUtil;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.org.LinkeyUser;
/**
 * @RuleName:复制流程
 * @author  admin
 * @version: 1.0
 */
final public class R_S018_B009 implements LinkeyRule {
	@Override
	public String run(HashMap<String, Object> params) throws Exception  {
	    //params为运行本规则时所传入的参数
        String processid =BeanCtx.g("processId"); //流程id多个用逗号分隔
        if(Tools.isBlank(processid)){return RestUtil.formartResultJson("0", "processId不能为空");}
        
        BeanCtx.setDocNotEncode();
        int i=0;
        String tableList = BeanCtx.getSystemConfig("ProcessModTableList");
        String[] tableArray = Tools.split(tableList);
        
        String[] processArray=processid.split(",");
        for(String pid:processArray){
	        String newProcessid = Rdb.getNewUnid();
	        for (String tableName : tableArray) {
                i++;
	            String sql = "select * from " + tableName + " where Processid='" + pid + "'";
	            Document[] dc = Rdb.getAllDocumentsBySql(sql);
	            for (Document nodedoc : dc) {
	                nodedoc.s("WF_OrUnid", Rdb.getNewid(""));
	                nodedoc.s("Processid", newProcessid);
	                if (nodedoc.g("NodeType").equals("Process")) {
	                    nodedoc.s("NodeName", nodedoc.g("NodeName") + "(copy)");
	                }
	                nodedoc.save();
	            }
	        }
        }
        
        return RestUtil.formartResultJson("1", "成功复制("+i+")个流程!","");
	 
	}
}