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
 * @RuleName:删除流程
 * @author  admin
 * @version: 8.0
 * @Created: 2017-09-18 09:41:28
 */
final public class R_S018_B005 implements LinkeyRule {
	@Override
	public String run(HashMap<String, Object> params) throws Exception  {
	    //params为运行本规则时所传入的参数
        String processid =BeanCtx.g("processId"); //流程id多个用逗号分隔
        if(Tools.isBlank(processid)){return RestUtil.formartResultJson("0", "processId不能为空");}
        
        int i=0;
        String tableList = BeanCtx.getSystemConfig("ProcessModTableList");
        String[] tableArray = Tools.split(tableList);
        String[] processArray=processid.split(",");
        for(String pid:processArray){
            String sql="select * from BPM_ModProcessList where ProcessId='"+pid+"'";
            if(Rdb.hasRecord(sql)){
            	i++;
	            for (String tableName : tableArray) {
	                sql = "select * from " + tableName + " where Processid='" + pid + "'";
	                Document[] dc = Rdb.getAllDocumentsBySql(sql);
	                if (dc.length > 0) {
	                    Documents.remove(dc);
	                }
	            }
            }
        }
        
        return RestUtil.formartResultJson("1", "成功删除("+i+")个流程!","");
	 
	}
}