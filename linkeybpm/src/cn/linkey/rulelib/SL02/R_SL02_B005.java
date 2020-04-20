package cn.linkey.rulelib.SL02;

import java.util.HashMap;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import com.qq.weixin.mp.aes.WeiXinUtil;

/**
 * @RuleName:主动调用微信发消息
 * @author Mr.Yun
 * @version: 8.0
 * @Created: 2016-08-19 16:36
 */
final public class R_SL02_B005 implements LinkeyRule {
	@Override
	public String run(HashMap<String, Object> params) throws Exception {
		String url = BeanCtx.getSystemConfig("HttpServerUrl") + "/rule?wf_num=R_S003_B052&wf_docunid=858ac6ac0c2fe04d090acf10e69f4f603a0a";
		/**
		 * 主动发送文本消息
		 * @param touser 接收者的工号（BPM）多个用|分隔[BPM主要发送者]
		 * @param topart 接收部门的id（微信）多个用|分隔
		 * @param totag 接收标签的id（微信）多个用|分隔
		 * @param agentid 应用id
		 * @param body 消息内容
		 * @return
		 */
		String touser = "Mr.Yun|dream";
		String topart = "";
		String totag = "";
		String agentid = "1";
		String body = "你有一个待办需要处理：<a href=\"" + url + "\" target=_blank><u>点击打开</u></a>";
		String msg = WeiXinUtil.sendTextMsg(touser, topart, totag, agentid, body);
		BeanCtx.p(msg);
		return "";
	}
}