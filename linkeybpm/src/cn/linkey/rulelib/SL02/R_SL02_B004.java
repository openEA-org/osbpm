package cn.linkey.rulelib.SL02;

import java.util.HashMap;

import cn.linkey.rule.LinkeyRule;
/**
 * @RuleName:微信按钮配置页
 * @author Mr.Yun
 * @version: 8.0
 * @Created: 2016-08-19 16:36
 */
final public class R_SL02_B004 implements LinkeyRule {
//	public WxCpService wxCpService;
//	@Override
	public String run(HashMap<String, Object> params) throws Exception {
//		try {
//			/**
//			 * 1、获取wxCpService、用户信息及微信链接传入的code字段
//			 */
//			wxCpService = WxCpserviceInf.getInstance().getWxCpService();
//			String token = wxCpService.getAccessToken();
//			String code = BeanCtx.g("code").trim();
//			String result = HttpClientUtils.getUserInfo(token, code);
//			// 获取用户信息
//			UserInfo info = (UserInfo) JsonUtil.toBeanFromJson(UserInfo.class,result);
//			
//			/**
//			 * 可通过BeanCtx.getQueryString()方法获取微信post过来的参数有哪些
//			 * 在微信中state字段一般为定义给用户使用，以该参数的不同值来区分不同的功能，好让应用知道应该执行或返回什么给用户
//			 */
//			// BeanCtx.out("Query-->"+BeanCtx.getQueryString()+"<--Query");
//			
//			/**
//			 * 2、获取自定义的连接参数字段，基本只需要配置此处即可
//			 * 可依据参数区分不同的功能或操作
//			 */
//			String state = BeanCtx.g("state");
//			// 移动首页
//			String page = "P_S023_006";
//			if (Tools.isNotBlank(state)) {
//				// 我的待办 ToDo
//				if (state.equals("ToDo")) {
//					page = "P_S023_004";
//				} else if (state.equals("Read")) {
//					// 我的待阅 Read
//					page = "P_S023_007";
//				} else if (state.equals("Done")) {
//					// 我的已办 Done
//					page = "P_S023_008";
//				} else if(state.equals("Entr")){
//					// 我委托的 Entr
//					page = "P_S023_009";
//				} else if (state.equals("Appy")) {
//					// 我的申请 Appy
//					page = "P_S023_010";
//				} else if (state.equals("Arch")) {
//					// 已归档的 Arch
//					page = "P_S023_011";
//				}
//			}
//			
//			/**
//			 * 3、跳转到指定链接
//			 * 该链接weixinid字段的格式不可修改（weixinid=WeiXin#工号#时间戳），该字段主要用来自动登陆，免去用户再次登陆的情况
//			 */
//			String url = BeanCtx.getSystemConfig("HttpServerUrl") + "/r?wf_num=" + page + "&weixinid=" + base64("WeiXin#" + info.getUserId() + "#" + System.currentTimeMillis());
//			// 转到页面
//			BeanCtx.getResponse().sendRedirect(url);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		return "";
	}
//	/**
//	 * 把字符串编码为base64格式的
//	 * @param str 要编码的字符串
//	 * @return 返回编码后的字符串
//	 */
//	public String base64(String str) {
//		if (Tools.isBlank(str)){
//			return "";
//		}
//		try {
//			String base64str = new String(org.apache.commons.codec.binary.Base64.encodeBase64(str.getBytes()), "UTF-8");
//			return base64str;
//		} catch (Exception e) {
//			return "";
//		}
//	}
}