package cn.linkey.servlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.linkey.app.AppUtil;
import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rest.ParameterRequestWrapper;
import cn.linkey.rest.RestUtil;
//import cn.linkey.util.RestCloudUtil;
import cn.linkey.util.Tools;

public class RestAction extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		runRestUrl(request, response);
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		Map<String, String[]> map = request.getParameterMap();
		runRestUrl(request, response);
	}

	//访问路径http://localhost:8080/restbpm/rest/pathparam
	public void runRestUrl(HttpServletRequest request, HttpServletResponse response) {
		String appid = "";
		String wf_num = "";
		String requestUrl = getRequestUrl(request); //获得请求地址//访问地址:http://localhost/restcloud/rest/appid/test得到/rest/appid/test
		try {
			request.setCharacterEncoding("UTF-8"); // 设置编码为utf-8\
			BeanCtx.init("", request, response); //首先初始化
			Document restConfigDoc = getRestInterfaceId(requestUrl);
			//获得发布接口的配置信息
			if (restConfigDoc == null) {
				response.setContentType("text/json;charset=UTF-8");
				BeanCtx.p(Tools.jmsg("0", "未找到" + requestUrl + "对应的Rest服务接口配置地址"));
				return;
			}
			//设置响应头类型
			String contentType = restConfigDoc.g("ContentType");
			if (Tools.isBlank(contentType)) {
				contentType = "text/json;charset=UTF-8";
			}
			response.setContentType(contentType);

			//检测method是否符合要求
			if (!restConfigDoc.g("Method").equalsIgnoreCase(request.getMethod())) {
				BeanCtx.p(Tools.jmsg("0",
						"Rest接口(" + restConfigDoc.g("url") + "只允许" + restConfigDoc.g("Method") + "请求)"));
				return;
			}

			//检测规则是否有绑定
			appid = restConfigDoc.g("WF_Appid");
			wf_num = restConfigDoc.g("RuleNum"); //要执行的规则编号
			if (Tools.isBlank(wf_num)) {
				BeanCtx.p(Tools.jmsg("0", "Rest接口(" + restConfigDoc.g("url") + ")中没有绑定规则"));
				return;
			}

			BeanCtx.setAppid(appid);//当前应用的编号设置到线程变量中去
			BeanCtx.setWfnum(wf_num); //把当前设计的编号设置到线程变量中去

			//4.获得应用文档检测应用是否允许匿名访问
			String userid = "";
			Document appdoc = AppUtil.getDocByid("BPM_AppList", "WF_Appid", appid, true);
			if (appdoc.isNull() || appdoc.g("Status").equals("0")) {
				//应用不存在,或者应用未启用禁止访问
				BeanCtx.p(Tools.jmsg("0", "应用(" + appid + ")不存在或者应用已禁止访问"));
				return;
			} else {
				userid = Tools.getRequestParamValue(request, "userId"); //20190410 修改，先获得header指定userId，如果没有再获得已登录用户

				if (Tools.isBlank(userid)) {
					//20180807 
					//userid=RestCloudUtil.getDataFromToken(request).get("userId"); //从token中取用户userId
					//userid = tokenMap;
					userid = DESUserid.getLoginUserid(request, response);//从session中获取用户名
				}
				//userid="admin";
				if (appdoc.g("Anonymous").equals("1")) {
					//说明允许匿名访问
					if (Tools.isBlank(userid)) {
						userid = "Anonymous"; //允许匿名访问且用户还没有登录
					}
				} else {
					// 2.1.看用户是否已登录
					//	            	userid=request.getHeader("userId"); //用户id在http头中传入
					//	            	userid="admin";
					//	            	userid=BeanCtx.g("WF_Userid"); //指定当前登录的用户是谁，问题，需要与登录系统进行统一修改
					if (Tools.isBlank(userid)) { // 用户未登录
						BeanCtx.p(Tools.jmsg("0", "请先登录再调用本接口或者在http header中传入userId参数来指定用户"));
						return;
					}
				}
			}

			//检测业务系统的id和密码是否正确,需要在http header中传入sysid,syspwd来验证接口调用权限
			//20180926  添加sysid和syspwd的验证
			String systemId = Tools.getRequestParamValue(request, "sysid");
			String systemPwd = Tools.getRequestParamValue(request, "syspwd");

			if (Tools.isBlank(systemId) || Tools.isBlank(systemPwd)) {
				BeanCtx.p(Tools.jmsg("0", "请传递业务系统的id(sysid)和接入的密码(syspwd),不予许为空"));
				return;
			}
			//	        String systemId=request.getHeader("sysid");
			//	        String systemPwd=request.getHeader("syspwd");
			//	        systemId="bpm";
			//	        systemPwd="pass";

			// 20190226 校验业务系统是否登录
			String sqlTemp = "select Status from bpm_businesssystem where Systemid='" + systemId + "'";
			String isLogin = Rdb.getValueBySql(sqlTemp);
			if (!"1".equals(isLogin)) {
				BeanCtx.p(Tools.jmsg("0", "业务系统" + systemId + "未登录，请登录后再调用Rest接口！"));
				return;
			}
			// 20190226 END

			//			if(systemPwd.length() < 32){
			//				systemPwd = Tools.md5(systemPwd);
			//			}

			//1.检测业务系统和密码是否正确
			 if (systemPwd.length() < 32) {
				 systemPwd = Tools.md5(systemPwd);
		        }
			String sql = "select * from BPM_BusinessSystem where Systemid='" + systemId + "' and SystemPwd='"
					+ systemPwd + "'";
			if (!Rdb.hasRecord(sql)) {
				BeanCtx.p(Tools.jmsg("0", "系统id或密码错误,请在传入sysid和syspwd参数!"));
				return;
			}

			// 20190124 添加/{params} 
			//参数到request中去=======================================start

			/*//5.设置已登录用户的userid,并重新初始BeanCtx
			BeanCtx.init(userid, request, response);
			
			//运行规则并返回结果
			HashMap<String, Object> params = getPathParams(requestUrl, restConfigDoc.g("url"));*/

			HashMap<String, Object> newParam = new HashMap(request.getParameterMap());
			HashMap<String, Object> params = getPathParams(requestUrl, restConfigDoc.g("url"));
			newParam.putAll(params);

			ParameterRequestWrapper wrapRequest = new ParameterRequestWrapper(request, newParam);

			BeanCtx.init(userid, wrapRequest, response); //首先初始化
			//=======================================================end

			String result = BeanCtx.getExecuteEngine().run(wf_num, params); //执行规则

			BeanCtx.p(result);//返回规则执行结果
			return;
		} catch (Exception e) {
			BeanCtx.log(e, "W", "");
			BeanCtx.setRollback(true); //设置为需要回滚如果没有开启事务则设置了回滚系统也不会回滚数据
			BeanCtx.p(Tools.jmsg("0", "接口运行错误，请查看后台管理日记"));
		} finally {
			BeanCtx.close(); // 这句一定要执行，要收回资源
		}

	}

	/**
	 * 根据请求地址获得rest配置数据
	 * @param requestUrl
	 * @return
	 */
	public Document getRestInterfaceId(String requestUrl) {
		Document restConfigDoc = null;
		Document[] dc = this.getAllRestConfigDocs();
		//1.先看是否有相等的url配置，如果有就说明url中没有带路径参数，直接找到返回即可
		for (Document doc : dc) {
			String configUrl = doc.g("url");
			if (configUrl.equalsIgnoreCase(requestUrl)) {
				restConfigDoc = doc;
				break;
			}
		}
		//2.如果没有相等的则要进行路径参数的比较看那一个url配置是适配的
		if (restConfigDoc == null) {
			String matchUrl = requestUrl;
			int i = matchUrl.lastIndexOf("/");
			int x = 0;//循环次数
			int max = 30; //最大支持30层的目录,避免进行死循环
			while (i > 0 && x < max) {
				x++;
				matchUrl = matchUrl.substring(0, i);//请求/rest/core/test/{001} matchUrl=/rest/core/test
				i = matchUrl.lastIndexOf("/");
				//				BeanCtx.out("matchUrl="+matchUrl);
				for (Document doc : dc) {
					String configUrl = doc.g("url");
					if (configUrl.startsWith(matchUrl)) {
						//说明配置地址与url的开始部分是相等的如：配置url:/rest/core/test/{001},/rest/core/test/id/{id} 请求地址=/rest/core/test/0003 是相匹配的,但有可能是多个同时匹配
						if (matchMapUrl(requestUrl, configUrl)) {
							restConfigDoc = doc;
							break;
						}
					}
				}
			}
		}
		return restConfigDoc;
	}

	private boolean matchMapUrl(String requestUrl, String mapConfigUrl) {
		String[] reqUrlArray = requestUrl.split("/"); //请求的url路径
		String[] mapConfigUrlArray = mapConfigUrl.split("/"); //配置的url路径

		//		BeanCtx.out("requestUrl="+requestUrl+" mapConfigUrl="+mapConfigUrl);
		//		BeanCtx.out("reqUrlArray="+reqUrlArray.length+" mapConfigUrlArray="+mapConfigUrlArray.length);

		//如果路径的目录个数不相等不匹配，直接返回
		if (reqUrlArray.length != mapConfigUrlArray.length) {
			return false;
		}

		//如果路径目录数相等的情况下对每一个目录进行比较，只有除了路径参数的不比外，其也的路径目录必须全部相等才可以正确匹配
		for (int x = 0; x < mapConfigUrlArray.length; x++) {
			String pathParamsName = mapConfigUrlArray[x];//配置的路径目录
			String urlParamsName = reqUrlArray[x]; //请求的路径目录
			if (Tools.isNotBlank(pathParamsName)) {
				if (pathParamsName.indexOf("{") != -1 && pathParamsName.indexOf("}") != -1) {
					//说明是路径参数
					//不用进行比较，直接比下一个目录是否相等
					continue;
				} else {
					//是目录，不是路径参数,区分大小写
					//如果不是路径参数，而且值又不相等的情况下说明不匹配
					if (!pathParamsName.equals(urlParamsName)) {
						return false;
					}
				}
			}
		}

		return true;
	}

	/**
	 * 根据请求和配置路径进行路径参数的计算
	 * 格式：/{id},/{id}/test
	 * @param requestUrl 请求url
	 * @param mapConfigUrl 配置url
	 */
	private HashMap<String, Object> getPathParams(String requestUrl, String mapConfigUrl) {
		HashMap<String, Object> params = new HashMap<String, Object>();

		requestUrl = RestUtil.decodeAll(requestUrl); //20190124 添加对url解码

		String[] urlArray = requestUrl.split("/");
		String[] mapConfigUrlArray = mapConfigUrl.split("/"); //配置的url数据
		for (int x = 0; x < mapConfigUrlArray.length; x++) {
			String pathParamsName = mapConfigUrlArray[x];
			int spos = pathParamsName.indexOf("{");
			int epos = pathParamsName.indexOf("}");
			if (Tools.isNotBlank(pathParamsName) && spos != -1 && epos != -1) {
				//说明是一个路径参数的配置如：/{templateid}/或者 /{id}.html/这样的格式也是可以的
				String paramsName = pathParamsName.substring(spos + 1, epos);
				String paramsValue = urlArray[x]; //从请求url中取参数值
				params.put(paramsName, paramsValue); //设置到请求参数中去方便使用BeanCtx.g()方法获取参数
				//				doc.s(paramsName,paramsValue); //把url中的值作为参数放入上下文档doc对像中方便取
			}
		}
		return params;
	}

	/**
	 * 获得所有配置的rest接口地址
	 * @return
	 */
	public Document[] getAllRestConfigDocs() {
		String sql = "select * from BPM_RestInterfaceList where RestType='3'";
		Document[] dc = Rdb.getAllDocumentsBySql(sql);
		return dc;
		//		return (Document[])RdbCache.getSystemCache("BPM_RestInterfaceList", "ALL");
	}

	/**
	 * 获取请求的服务地址,去掉host前面一段的服务地址
	 * @param request
	 * @return
	 */
	public String getRequestUrl(HttpServletRequest request) {
		String url = request.getRequestURI().substring(request.getContextPath().length());//访问地址:http://localhost/restcloud/rest/appid/test.do得到/rest/appid/test.do
		return url;
	}

}