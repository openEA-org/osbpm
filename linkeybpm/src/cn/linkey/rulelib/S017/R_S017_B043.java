package cn.linkey.rulelib.S017;

import java.util.*;

import cn.linkey.dao.*;
import cn.linkey.util.*;
import cn.linkey.doc.*;
import cn.linkey.factory.*;
import cn.linkey.wf.*;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.org.LinkeyUser;
/**
 * @RuleName::Engine_结束用户任务用户的待将会取消，但是节点仍然是活动的.
 * @author  admin
 * @version: 8.0
 * @Created: 2016-10-12 10:41:34
 */
final public class R_S017_B043 implements LinkeyRule {
	@Override
	public String run(HashMap<String, Object> params) throws Exception  {
	    //params为运行本规则时所传入的参数
		//示例参数:{"Processid":"5c6dc7140233f04b6f09a5a0547a35a316a8", "DocUnid":"0bed75550c97504d100a0e709d60d8488221","Nodeid":"S10018","EndUserid":"*"}
		
        String processid = (String) params.get("Processid"); //流程id
        String docUnid = (String) params.get("DocUnid"); //实例id
        String nodeid=(String)params.get("Nodeid"); //节点id
        String endUserList=(String)params.get("EndUserid"); //要结束的用户id,如果传入*号表示结束此节点的所有用户,多个用户用逗号分隔
        
        if(endUserList.equals("*")){
        	//得到此节点的所有活动用户
            String sql = "select userid from bpm_InsUserList where DocUnid='" + docUnid + "' and Processid='" + processid + "' and Nodeid='" + nodeid + "' and Status='Current'";
            endUserList = Rdb.getValueBySql(sql);
        }
        
        String msg=ProcessUtil.endUser(processid, docUnid, nodeid, endUserList);
        
        return msg;
	}
}