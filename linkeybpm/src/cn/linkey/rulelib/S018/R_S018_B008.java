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
 * @RuleName:发布流程
 * @author  admin
 * @version: 8.0
 * @Created: 2017-09-18 09:41:28
 */
final public class R_S018_B008 implements LinkeyRule {
	@Override
	public String run(HashMap<String, Object> params) throws Exception  {
	    //params为运行本规则时所传入的参数
        String processid =BeanCtx.g("processId"); //流程id多个用逗号分隔
        String publish =BeanCtx.g("publish"); //1表示发布，0表示取消发布
        if(Tools.isBlank(processid)){return RestUtil.formartResultJson("0", "processId不能为空");}
        if(Tools.isBlank(publish)){return RestUtil.formartResultJson("0", "publish不能为空");}
        String tableName="BPM_ModProcessList";
        
        int x=0;
        String[] processArray=processid.split(",");
        for(String pid:processArray){
        	 String sql="update "+tableName+" set Status='"+publish+"' where ProcessId='"+pid+"'";
        	 int i=Rdb.execSql(sql);
        	 if(i>0){
        		 x++;
        	 }
        }
        if(x>0 && publish.equals("1")){
        	 return RestUtil.formartResultJson("1", "成功发布("+x+")个流程!","");
        }else{
        	 return RestUtil.formartResultJson("1", "成功停止("+x+")个流程!","");
        }
	 
	}
}