package cn.linkey.wf;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;

import cn.linkey.dao.Rdb;
import cn.linkey.dao.RdbCache;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.factory.IIIIILIIIIIIIII;
import cn.linkey.form.ModForm;
import cn.linkey.rule.RuleConfig;
import cn.linkey.util.DateUtil;
import cn.linkey.util.Tools;

/**
 * 本类为多实例类，可以保证在规则中再使用 Engine newlinkeywf=new Engine(); 这种用法
 * 
 * @author Administrator
 */
public class ProcessEngine {
	private String appid; //流程所属应用id
	private String mainTableName;//流程主数据库所在表
	private String arcTableName;//归档后的主数据库表
	private String userid; //当前登录的用户id
	private String userName; //当前登录用户的中文名
	private String docUnid; //文档unid
	private String formNumber; //当前使用的表单的formNumber
	private String processid; //当前流程的唯一id
	private String processNumber;//流程编号，多个流程可以使用同一个编号
	private String currentNodeid;//用户当前所处的节点ID
	private String currentNodeName;//用户当前所处的节点名称
	private String processName; //流程名称
	private String lockStatus;//文档锁定状态空表示未锁定，否则表示锁定者的用户id
	private boolean isNewProcess;//是否是新流程标记
	private boolean isProcessOwner = false;//当前用户是否流程管理员
	private boolean isFirstNode = false;//是否首环节标记，true表示是,false表示否
	private Document document; //要流转的文档对像
	private Document currentInsUserDoc; //用户当前环节的用户实例文档
	private Document currentModNodeDoc; //用户当前环节模型文档
	private Document processModNodedoc;//流程过程属性文档对像
	private Document formDoc; //当前流程所处表单模型，如果是更换表单则是新的表单的文档对像,问题，表单对像有点大，全局保存在内存中可能比较占内存
	private boolean readOnly = false; //当前用户对于本文档是否处于只读状态true表示只读，false表示可编辑
	private String actionNum; //本次操作的ActionNum随机数
	private boolean runStatus = false;//流程引擎运行状态标识true表示Action动作运行成功，false表示运行失败(有可能多种原因Action运行条件不符合等)
	private String endNodeid; //流程结束节点的nodeid,如果设置了流程将运行归档方法把文档强制归档
	private boolean debug = false; //true表示调试流程
	private String rollbackMsg = "";//流程主动回滚后的提示信息
	private String extendTable = "";//扩展业务表名称
	private HashSet<Document> maildc = new HashSet<Document>(); //需要发送路由邮件的文档集合,路由邮件不能在执行路径线时发送这样会得不到字段内容

	/**
	 * 初始化流程引擎
	 * 
	 * @param processUnid 流程32位唯一id
	 * @param docUnid 主文档32位唯一d
	 * @param userid 用户当前登录的id
	 * @param taskid 强制指定当前用户所处的任务节点，以支持一个用户有多个任务的情况下
	 */
	public void init(String processid, String docUnid, String userid, String taskid) throws Exception {
		//long ts = System.currentTimeMillis();
		this.mainTableName = "BPM_MainData";

		//1.初始化引擎对像
		this.document = BeanCtx.getDocumentBean(this.mainTableName);
		if (Tools.isBlank(docUnid)) {
			this.docUnid = Rdb.getNewUnid();//如果不传入文档id则表示要生成一个新的文档id
		} else {
			this.docUnid = docUnid; //使用传入进来的文档id号
			this.document.initByDocUnid(docUnid); //初始化主文档数据
			this.document.removeEditorField(BeanCtx.g("WF_DwEditorFdList")); //删除主文档中的动态表格字段
		}
		this.userid = userid;
		this.processid = processid;
		this.actionNum = Tools.getRandom(8);

		//2.初始化文档
		// BeanCtx.out("所有请求数据request="+BeanCtx.getRequest().getParameterMap());
		this.document.s("WF_OrUnid", this.docUnid);//重新设置一下当前文档的DocUnid以防被request中的参数修改 
		lockStatus = document.getLockStatus(userid); //获得文档的锁定状态

		//3.获得当前用户所处流程的当前环节Nodeid,过程属性中的相关变量，流程名称、流程所属应用、流程所属企业、流程过程读者,流程表单等
		((InsProcess) BeanCtx.getBean("InsProcess")).initProcessVar(taskid);

		//4.获得表单模型文档对像,在initProcessVar()中已经计算过是否需要更换表单了，如果当前环节要更换则已经是更换过的表单对像了
		formDoc = ((ModForm) BeanCtx.getBean("ModForm")).getFormDoc(formNumber); //formNumber在initProcessVar()方法中给值

		//6.检查流程有没有指定业务扩展表，如果有指定需要合并业务扩展表数据2015.6.3增加
		if (Tools.isNotBlank(docUnid) && !this.extendTable.equalsIgnoreCase("xmldata")) {
			String sql = "select * from " + this.extendTable + " where WF_OrUnid='" + this.docUnid + "'";
			Document extDoc = Rdb.getDocumentBySql(this.extendTable, sql);
			extDoc.copyAllItems(this.document);
			this.document.s("WF_OrUnid", this.docUnid);
		}

		this.document.appendFromRequest(BeanCtx.getRequest(), true); //把请求的数据也初始化到请求中去

		//long te = System.currentTimeMillis();
		// BeanCtx.out("流程初始化总时间毫秒="+ (te - ts));
	}

	/**
	 * 设置一个已经存在的文档对像为流程的主文档
	 * 
	 * @param doc
	 */
	public void setDocument(Document doc) {
		this.document = doc;
		this.docUnid = doc.getDocUnid();
		lockStatus = document.getLockStatus(userid); //获得文档的锁定状态
	}

	/**
	 * 提交流程按照actionid的方式进行流转
	 * 
	 * @param actionid 动作id 有：EndUserTask,GoToFirstNode,GoToPrvNode,GoToPrvUser,GoToArchived,ReturnToPrevUser,StartUser可通过getCurrentActionid()自动获取
	 * @param runParams 运行时参数有
	 * 
	 *        <pre>
	 *          运行时参数有(通过runParams来传参数可以让引擎不依赖于WEB服务器的request对像)：
	 *          WF_NextNodeid   要提交的节点列表 HashSet集合
	 *          WF_NextUserList 要提交的用户列表 HashMap集合，包含WF_CopyUserList对像，放在COPYUSER的key中
	 *          WF_ReassignmentFlag 转交时是否需要转交者返回的标记1表示不需要2表示需要
	 *          WF_SendSms      发送手机短信标记1表示发送，0表示不发送
	 *          WF_RunActionid  提交的动作Actionid，自动由Actionid转入 
	 *          WF_StopNodeType 用来指定需要停止推进的节点类型如：rearEvent ,userTask等 
	 *          WF_AllRouterPath 所有已经指定(已知)的路径集合 
	 *          WF_RunNodeid    WF_RunNodeid运行的节点id 
	 *          WF_UsePostOwner 强制使用
	 *          WF_NextUserList 为启动者标记 runParams示例:
	 *                          HashMap<String,String> params=new HashMap String,String>();
	 *                          params.put("WF_NextUserList","user01,user02");
	 *        </pre>
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String run(String actionid, HashMap<String, Object> runParams) throws Exception {
		//注意:流程运行时不需要进行表单字段配置的初始化 initEngineFormFieldConfig()
		//运行或提交流程
		RuleConfig ruleConfig = (RuleConfig) BeanCtx.getBean("RuleConfig");

		//检测在编辑状态下的Action动作是否有权进行审批
		if (ruleConfig.isEditEngineAction(actionid)) {
			if (Tools.isBlank(currentNodeid)) {
				return BeanCtx.getMsg("Engine", "NoApprovalDocument");
			}
		}

		//检测文档是否已经归档，如果已经归档过了不允许再次提交，否则再提交会成为一份新的流程文档
		String sql = "select WF_OrUnid from BPM_ArchivedData where WF_OrUnid='" + this.docUnid + "'";
		if (Rdb.hasRecord(sql)) {
			return BeanCtx.getMsg("Engine", "NoApprovalDocument");
		}

		runParams.put("WF_RunActionid", actionid); //加入参数中

		//1.触发流程运行前事件
		BeanCtx.getEventEngine().run(processid, "Process", "EngineBeforeRun", runParams);

		/* 2.如果是新流程则进行流程启动 */
		if (this.isNewProcess) {
			InsProcess insProcess = (InsProcess) BeanCtx.getBean("InsProcess");
			insProcess.startProcess(docUnid, processid);
		}

		//3.运行根据Actionid来运行流程引擎所配置的动作Action,actionid要转换成为对应的规则编号才能执行,每个run动作都应在BPM_RuleEngineActionConfig表中配置相应的规则
		String ruleNum = ruleConfig.getRuleNumByEngineActionid(actionid);
		if (Tools.isBlank(ruleNum)) {
			return BeanCtx.getMsg("Engine", "Error_EngineRun");
		}
		String runMsg = BeanCtx.getExecuteEngine().run(ruleNum, runParams);

		//4.运行抄送的用户规则
		HashMap<String, String> nextUserList = (HashMap<String, String>) runParams.get("WF_NextUserList"); //要提交的后继用户
		if (nextUserList != null) {
			String copyUserList = nextUserList.get("COPYUSER"); //获得要传阅的用户列表
			if (copyUserList != null && Tools.isNotBlank(copyUserList)) {
				ruleNum = ruleConfig.getRuleNumByEngineActionid("CopyTo");
				BeanCtx.getExecuteEngine().run(ruleNum, runParams);
			}
		}

		//5.存子表单数据，用来显示子表单的数据变化过程
		if (this.getCurrentModNodeDoc() != null) {
			String formNumber = getCurrentModNodeDoc().g("SubFormNumberLoad");
			if (Tools.isNotBlank(formNumber)) {
				((MainDoc) BeanCtx.getBean("MainDoc")).saveSubFormData(formNumber);
			}
		}

		//如果节点中配置了邮件发送的Action则不管是否归档都需要发送节点中配置的邮件
		//        MessageImpl message = (MessageImpl) BeanCtx.getBean("Message");
		//        message.sendActionMail(actionid);

		//6.存盘流程主文档
		String saveDocMsg = saveDocument();
		if (!saveDocMsg.equals("1")) {
			return saveDocMsg;
		} //存盘失败

		//如果节点中配置了邮件发送的Action则不管是否归档都需要发送节点中配置的邮件
		//20190823 调整节点发送邮件顺序，先保存主表单数据再发送
		MessageImpl message = (MessageImpl) BeanCtx.getBean("Message");
		message.sendActionMail(actionid);

		//7.如果运行了结束环节则需要结束整个流程并归档,归档要在所在环节运行结束后才从过程属性中去拿归档信息
		if (Tools.isNotBlank(this.getEndNodeid())) {
			int r = ((MainDoc) BeanCtx.getBean("MainDoc")).archiveDocument(); //运行归档逻辑程序并发送归档后的邮件通知
			if (r > 0) {
				endParentSubProcessNode(runParams); //尝试结束主流程的子流程节点
				message.cancelToDo(docUnid);
			}
		} else if (this.currentModNodeDoc != null) {
			//检测空用户时是否不允许提交流程
			if (this.processModNodedoc.g("NullUserErrorFlag").equals("Y")
					&& Tools.isBlank(this.document.g("WF_Author"))) {
				BeanCtx.out(this.processName + "->流程设定处理人为空时不允许提交!");
				BeanCtx.setRollback(true); //回滚所有数据
			} else {
				//发送手机短信
				if (this.document.g("WF_SendSms").equals("1")) {
					message.sendSms(actionid);
				}

				//发送待办待阅队例
				if (BeanCtx.getSystemConfig("SendToDoInBox").equals("1")) {

					//1.先取消已完成用户的待办和待阅数据行
					message.cancelToDo(docUnid, document.g("WF_OutEndUser"));

					//2.发送待办队例
					message.sendToDo(actionid, document.g("WF_InNewUser"), true);

					//3.发送待阅队例
					HashSet<String> copyUserList = Tools.splitAsSet(document.g("WF_CopyUser")); //已经在待阅中的用户
					HashSet<String> newUserList = Tools.splitAsSet(document.g("WF_CopyUserList")); //新发送的待阅用户
					newUserList.removeAll(copyUserList); //减去已经在待阅队列中的用户为要发送的用户
					message.sendToDo(actionid, Tools.join(newUserList, ","), false); //发送待阅的消息
				}

				//发送路由线上的待办通知邮件
				message.sendRouterMail();
			}
		}

		//7.删除临时用户存的意见数据和锁定记录
		Rdb.execSql("delete from BPM_TempRemarkList where Userid='" + BeanCtx.getUserid() + "' and DocUnid='" + docUnid
				+ "'");
		this.document.unlock(); //解锁文档

		//8.触发流程运行结束后事件
		BeanCtx.getEventEngine().run(processid, "Process", "EngineAfterRun", runParams); //流程运行结束后事件

		//9.对当前环节的管控点进行检测，只有符合管控点的运行才是有效的运行，否则将根据处理规则进行回滚或放行
		((NodeControlPoint) BeanCtx.getBean("NodeControlPoint")).runControlPoint();

		return runMsg;
	}

	/**
	 * 尝试结束主流程的SubProcess子流程节点
	 */
	public void endParentSubProcessNode(HashMap<String, Object> runParams) throws Exception {
		ProcessEngine currentlinkeywf = BeanCtx.getLinkeywf(); //当前的流程引擎变量
		String mainDocUnid = document.g("WF_MainDocUnid"); //主流程的文档unid
		String mainNodeid = document.g("WF_MainNodeid"); //主流程的节点id

		if (Tools.isNotBlank(mainDocUnid) && !mainDocUnid.equals(getDocUnid())) {
			//说明是子流程文档，要检测主流程的子流程节点是否可以结束
			ModNode insModNode = (ModNode) BeanCtx.getBean("ModNode");
			Document endModNodeDoc = insModNode.getNodeDoc(processid, this.getEndNodeid()); //结束环节的属性文档
			if (endModNodeDoc.g("BackToMainProcess").equals("1")) {
				//说明结束环节中要求返回主流程
				Document parentDocument = Rdb.getDocumentBySql(
						"select * from " + this.mainTableName + " where WF_OrUnid='" + mainDocUnid + "'");
				String mainProcessid = parentDocument.g("WF_Processid"); //获得主流程的processid
				if (BeanCtx.getLinkeywf().isDebug()) {
					BeanCtx.out("准备返回主流程Processid=" + mainProcessid + ",主流程节点=" + mainNodeid + ",主流程实例docunid="
							+ mainDocUnid);
				}
				ProcessEngine parentlinkeywf = new ProcessEngine(); //新的一个流程引擎变量
				if (BeanCtx.getLinkeywf().isDebug()) {
					parentlinkeywf.setDebug(true);
				} //继承调试状态
				BeanCtx.setLinkeywf(parentlinkeywf); //切换到主流程引擎
				parentlinkeywf.init(mainProcessid, "", BeanCtx.getUserid(), "");
				parentlinkeywf.setDocument(parentDocument); //重新强制设定为主文档对像

				//开始拷贝数据
				if (endModNodeDoc.g("SubCopyData").equals("1")) {
					this.getDocument().copyAllItems(parentlinkeywf.getDocument(), true);//子流程返回主流程时只copy回业务数据，系统字段不返回不然会破坏主流程的WF_MainDocUnid等信息
				}

				//拷贝附件
				if (endModNodeDoc.g("subCopyAttach").equals("1")) {
					this.getDocument().copyAttachment(parentlinkeywf.getDocument());
				}

				//这里必须创建一个新的params参数传入到run()方法中去，不然旧的params参数中有WF_AllRouterPath等信息，这样就会按这个路径走就不对了
				HashMap<String, Object> newRunParams = new HashMap<String, Object>();
				newRunParams.put("WF_SubNextUserList", runParams.get("WF_SubNextUserList"));
				newRunParams.put("WF_Remark", runParams.get("WF_Remark"));
				newRunParams.put("WF_EngineAction", "BackToParentProcess"); //返回主流程节点运行标识,在R_S003_B019中有用到

				//执行数据转换规则
				String subRuleNum = endModNodeDoc.g("SubRuleNum");
				if (Tools.isNotBlank(subRuleNum)) {
					//执行数据转换规则
					newRunParams.put("WF_NodeDoc", endModNodeDoc); //节点文档
					newRunParams.put("WF_SubDoc", parentlinkeywf.getDocument()); //子流程文档
					newRunParams.put("WF_MainDoc", this.getDocument()); //主流程文档
					BeanCtx.getExecuteEngine().run(subRuleNum, newRunParams); //在数据转换规则通过WF_SubDoc可获得子文档对像
				}
				newRunParams.put("MainNodeid", mainNodeid); //传入子流程节点的Nodeid

				//运行工作流run()方法
				String msg = parentlinkeywf.run("EndSubProcessNode", newRunParams); //R_S003_B064
				if (parentlinkeywf.isDebug()) {
					BeanCtx.out("父流程运行结果=" + msg);
				}

				BeanCtx.setLinkeywf(currentlinkeywf); //重新切换为当前的流程引擎对像

			}
		}
	}

	@SuppressWarnings("unchecked")
	public String open() throws Exception {
		/*
		 * 启动流程者权限判断
		 */
		boolean isFirstNode = BeanCtx.getLinkeywf().isFirstNode;
		// 查询流程模型Document
		Document processDoc = Rdb
				.getDocumentBySql("select * from BPM_ModProcessList where Processid = '" + processid + "'");
		if (isFirstNode) {
			// 获取流程有权启动者
			String processStarter = processDoc.g("processStarter");
			if (!BeanCtx.getLinkeyUser().inRoles(BeanCtx.getUserid(), processStarter)) {
				return BeanCtx.getMsg("Engine", "NoStartDocument");
			}
		}
		/*
		 * 如果不是新启动流程， 则需要判断阅读者权限
		 */
		if (!isFirstNode) {
			// 先获取全局阅读者权限，如果该字段为空，表示所有人可看
			String readerAll = processDoc.g("ProcessReader");
			if (Tools.isNotBlank(readerAll)) {
				/*
				 * 如果全局阅读者不为空，则追加流程实例的阅读者 阅读者权限为BPM_MainData表WF_AllReaders字段和BPM_ModProcessList表的ProcessReader全局阅读者字段
				 */
				readerAll += "," + document.g("WF_AllReaders");
				// 如果当前用户不在这些角色组（包含部门、角色、用户）中
				if (!BeanCtx.getLinkeyUser().inRoles(BeanCtx.getUserid(), readerAll)) {
					return BeanCtx.getMsg("Engine", "NoAccessDocument");
				}
			}
		}
		//打开流程表单
		//0.如果当前用户可审批状态，则锁定当前文
		if (Tools.isNotBlank(this.currentNodeid)) {
			Document.lock(document.getDocUnid());
		}

		//1.触发流程过程中指定的打开前事件,规则中不返回1就表示要退出流程打开
		String EwMsg = BeanCtx.getEventEngine().run(processid, "Process", "EngineBeforeOpen");
		if (!EwMsg.equals("1")) {
			return EwMsg;
		}

		//2.如果当前用户有审批权限则执行流程表单打开前事件,因为用户无权审批时不知道他位于那个节点，所以不能执行这种类型的事件
		if (Tools.isNotBlank(currentNodeid)) {
			BeanCtx.getEventEngine().run(processid, currentNodeid, "EngineFormBeforeOpen");
		}

		/* 3.运行绑定表单字段的事件 */
		String docStatus = "EDIT"; // 编辑状态
		if (readOnly) {
			docStatus = "READ";
		} // 只读状态
		if (this.isNewProcess) {
			docStatus = "NEW";
		} // 新建状态

		//初始化引擎表单的字段配置信息,只有在流程打开时才需要，在流程提交时暂不初始化表单字段配置信息
		((ModForm) BeanCtx.getBean("ModForm")).initEngineFormFieldConfig(formDoc);

		//获得所有表单的字段配置信息包括子表单的，然后执行字段绑定的后端规则
		HashMap<String, Map<String, String>> formFieldConfig = new HashMap<String, Map<String, String>>();
		HashMap<String, Map<String, String>> mainFormFieldConfig = BeanCtx.getMainFormFieldConfig(); //主表单的字段配置
		if (mainFormFieldConfig != null) {
			formFieldConfig.putAll(BeanCtx.getMainFormFieldConfig()); //主表单的字段配置
		}
		HashMap<String, Map<String, String>> subFormFieldConfig = BeanCtx.getSubFormFieldConfig(); //子表单字段配置
		if (subFormFieldConfig != null) {
			formFieldConfig.putAll(BeanCtx.getSubFormFieldConfig()); //追加子表单的字段配置
		}
		HashMap<String, Object> params = new HashMap<String, Object>(); //准备运行规则的参数
		params.put("Document", this.document);
		for (String fieldName : formFieldConfig.keySet()) {
			Map<String, String> fieldMapObject = formFieldConfig.get(fieldName);
			//BeanCtx.out("fieldJsonObject="+fieldMapObject.toString());
			String rule = fieldMapObject.get("NodeRule"); //流程环节中绑定的规则编号，要优先执行
			if (Tools.isNotBlank(rule)) {
				//节点规则执行后就不用执行表单中的字段规则属性中的规则了,而且节点规则不受编辑新建影响，肯定运行
				params.put("FieldName", fieldMapObject.get("name")); // 字段名称
				BeanCtx.getExecuteEngine().run(rule, params); //运行字段数据源并传入字段名称和文档对像
			} else {
				//在没有节点规则的情况下才执行
				rule = fieldMapObject.get("rule"); // 表单字段属性中绑定的规则编号,
				if (Tools.isNotBlank(rule)) {
					String ruleoption = fieldMapObject.get("ruleoption"); // 规则运行方式NEW,EDIT,READ
					if (Tools.isBlank(ruleoption)) {
						ruleoption = "NEW";
					} //默认为新建
					if (Tools.isBlank(ruleoption) || ruleoption.indexOf(docStatus) != -1) {
						params.put("FieldName", fieldMapObject.get("name")); // 字段名称
						BeanCtx.getExecuteEngine().run(rule, params); //运行字段数据源并传入字段名称和文档对像
					}
				}
			}
		}

		/* 4.运行主表单打开事件 */
		String ruleNum = formDoc.g("EventRuleNum");
		if (Tools.isNotBlank(ruleNum)) {
			params = new HashMap<String, Object>();
			params.put("FormDoc", formDoc);
			params.put("DataDoc", document);
			params.put("EventName", "onFormOpen");
			if (readOnly) {
				params.put("ReadOnly", "1");
			} else {
				params.put("ReadOnly", "0");
			}
			String ruleStr = BeanCtx.getExecuteEngine().run(ruleNum, params); //运行表单打开事件
			if (!ruleStr.equals("1")) {
				//说明事件中要退出本次表单打开
				return ruleStr;
			}
		}

		//5运行子表单打开事件
		String ruleResult = ((ModForm) BeanCtx.getBean("ModForm")).runSubFormEvent("onFormOpen", true);
		if (!ruleResult.equals("1")) {
			//说明事件中要退出本次表单打开
			return ruleResult;
		}

		//6.记录首次阅读时间
		if (Tools.isNotBlank(this.currentNodeid) && !isNewProcess) {
			if (!this.currentInsUserDoc.g("WF_IsNewInsUserDoc").equals("1")
					&& Tools.isBlank(this.currentInsUserDoc.g("FirstReadTime"))) {
				this.currentInsUserDoc.s("FirstReadTime", DateUtil.getNow());
				this.currentInsUserDoc.save();
			}
		}

		//7.如果用户在待阅记录中则自动标识为已阅
		String copyUserList = document.g("WF_CopyUser");
		if (Tools.isNotBlank(copyUserList)) {
			copyUserList = "," + copyUserList + ",";
			if (copyUserList.indexOf("," + BeanCtx.getUserid() + ",") != -1) {
				//1.把当前用户标记为已阅
				InsUser insUser = (InsUser) BeanCtx.getBean("InsUser");
				Document insUserDoc = insUser.endCopyUser(BeanCtx.getUserid());

				//2.增加已阅记录
				Remark remark = (Remark) BeanCtx.getBean("Remark");
				remark.AddReadRemark(insUserDoc.g("Nodeid"), insUserDoc.g("NodeName"), insUserDoc.g("StartTime"));

				//更新主文档的WF_CopyUser字段
				copyUserList = Tools.join(((NodeUser) BeanCtx.getBean("NodeUser")).getCopyUser(docUnid), ",");
				String sql;
				if (document.g("WF_Status").equals("ARC")) {
					sql = "update BPM_ArchivedData set WF_CopyUser='" + copyUserList + "' where WF_OrUnid='" + docUnid
							+ "'";

					//已归档的文件则需要清除待阅记录
					if (Tools.isNotBlank(copyUserList)) {
						Rdb.execSql("delete from BPM_InsCopyUserList where Userid='" + BeanCtx.getUserid()
								+ "' and DocUnid='" + docUnid + "'");
					} else {
						Rdb.execSql("delete from BPM_InsCopyUserList where DocUnid='" + docUnid + "'"); //没有已阅的用户清除全部的记录
					}
				} else {
					sql = "update BPM_MainData set WF_CopyUser='" + copyUserList + "' where WF_OrUnid='" + docUnid
							+ "'";
				}
				Rdb.execSql(sql);
			}
		}

		//8.开始返回流程表单的HTML代码
		if (isValidSysDate()) {
			return "";
		}
		StringBuilder formBody = new StringBuilder(10000);
		HashMap<String, LinkedHashSet<String>> engineFormCache = (HashMap<String, LinkedHashSet<String>>) RdbCache
				.getSystemCache("BPM_EngineFormPluginConfig", "ALL"); //含PC和Mobile全部
		LinkedHashSet<String> pluginList = null;
		;
		// 2018.03.02 添加获取UIType 
		String sqlTableName = "BPM_FormList";
		String sql = "select * from " + sqlTableName + " where FormNumber='" + formNumber + "'";
		Document formDoc1 = Rdb.getDocumentBySql(sqlTableName, sql); //得到表单文档数据
		if (BeanCtx.isMobile()) {
			pluginList = engineFormCache.get("MOBILE"); //获得移动终端的表单处理规则
		}
		//2018.03.02 修改获取解析类
		else if ("3".equals(formDoc1.g("UIType"))) {
			pluginList = engineFormCache.get("PC"); //获得PC端的表单处理规则
		} else if ("1".equals(formDoc1.g("UIType"))) {
			pluginList = engineFormCache.get("LayUI");
		}
		params = new HashMap<String, Object>();
		for (String pluginRuleNum : pluginList) {
			params.put("FormDoc", formDoc);
			params.put("FormBody", formBody);
			formBody.append(BeanCtx.getExecuteEngine().run(pluginRuleNum, params)); //循环执行所有插件并返回HTML代码进行组装
		}

		//9.如果当前用户有审批权限则执行流程表单打开后事件,因为用户无权审批时不知道他位于那个节点，所以不能执行这种类型的事件
		if (Tools.isNotBlank(currentNodeid)) {
			BeanCtx.getEventEngine().run(processid, currentNodeid, "EngineFormAfterOpen");
		}

		//10.执行流程打开后事件，不管用户有没有审批权限都执行
		BeanCtx.getEventEngine().run(processid, "Process", "EngineAfterOpen");

		formBody.trimToSize();
		return formBody.toString();
	}

	/**
	 * 依据前环节推进到当前环节的后继环节中去
	 * 
	 * @param curNodeid 当前所处节点的id
	 * @param runParams, runPraram中传入 WF_AllRouterPath 参数可以指定运行的路由集合
	 * @return 返回运行成功后规则中所返回的字符串结果,如果数据驻留在中间环节则返回中间环节的节点名称
	 */
	@SuppressWarnings("unchecked")
	public void goToNextNode(String curNodeid, HashMap<String, Object> runParams) throws Exception {
		ModNode insModNode = ((ModNode) BeanCtx.getBean("ModNode"));
		HashSet<String> allRouterNodeSet = (HashSet<String>) runParams.get("WF_AllRouterPath"); //所有已经指定(已知)的路径集合
		HashSet<String> nextNodeSet = new HashSet<String>(); //本节点后面的路由线集合
		if (allRouterNodeSet == null) {
			//没有指定路径，直接计算本环节后面的符合条件的路由线,并推进 
			nextNodeSet = insModNode.getNextNodeid(processid, curNodeid, true); //得到所有符合条件的路由线段或节点
			if (this.debug) {
				BeanCtx.out(curNodeid + "->linkeywf.goToNextNode()获得所有后继符合条件的路由=" + nextNodeSet.toString());
			}
		} else {
			//指定路径的情况下进行推进，需要计算当前环节后面所有可能的环节或路由线然后再与指定的路径集合进行比较，他们的交集才是需要推进的节点
			nextNodeSet = insModNode.getNextNodeid(processid, curNodeid, false); //得到本节点后面所有路由线段或节点，不用计算条件
			if (!insModNode.getNodeType(processid, curNodeid).equals("SequenceFlow")) {
				//如果本身就是路由线是不用进行以下检测的，因为路由线后面肯定只有一个节点而且是符合要求的,只有路径才需要检测
				if (this.debug) {
					BeanCtx.out("linkeywf.goToNextNode()使用已知的路径集合=" + allRouterNodeSet.toString());
					BeanCtx.out("linkeywf.goToNextNode()得到本节点(" + curNodeid + ")后面的路由集合=" + nextNodeSet.toString());
				}
				nextNodeSet.retainAll(allRouterNodeSet); //得到当前路由线与指定路由线的交集，就是指这下要推进行路由线
				if (this.debug) {
					BeanCtx.out("linkeywf.goToNextNode()得到路由交集=" + nextNodeSet.toString());
				}
			}
		}

		//允许节点后面没有节点的情况存在如自动活动，事件等后面都可以不再接环节,改为只提示警告信息
		if (nextNodeSet.size() == 0) {
			BeanCtx.log("D", "警告:节点(" + processid + "->" + curNodeid + ")没有配置后续路由线,请检查流程配置是否符合业务需求!");
		}

		//循环运行每一条路由线或节点
		for (String nextNodeid : nextNodeSet) {
			//循环推进到后继节点,根据节点扩展类型去BPM_RuleNodeTypeConfig表中去查找相对应支持节点规则并运行
			runNode(processid, nextNodeid, "StartRuleNum", runParams);
		}
	}

	/**
	 * 执行节点，通过节点类型配置来对节点进行执行
	 * 
	 * @param processid 流程id
	 * @param nodeid 要运行的节点
	 * @param ruleType 要运行的类型StartRuleNum,EndRuleNum....
	 * @return 返回运行成功后的结果字符串
	 */
	public void runNode(String processid, String nodeid, String ruleType, HashMap<String, Object> runParams)
			throws Exception {
		ModNode insModNode = ((ModNode) BeanCtx.getBean("ModNode"));
		Document nodeDoc = insModNode.getNodeDoc(processid, nodeid);
		runParams.put("WF_RunNodeid", nodeid); //默认加入运行节点的参数
		String ruleNum = ((RuleConfig) BeanCtx.getBean("RuleConfig")).getNodeTypeConfig(nodeDoc.g("ExtNodeType"),
				ruleType);
		if (Tools.isBlank(ruleNum)) {
			BeanCtx.log("E",
					"不支持的节点类型(" + nodeDoc.g("ExtNodeType") + "),请在BPM_RuleNodeTypeConfig中配置相应的节点和" + ruleType + "规则!");
		}

		if (ruleType.equals("StartRuleNum")) {
			//说明是启动环节，注册节点启动事件
			BeanCtx.getEventEngine().run(processid, nodeid, "NodeBeforeStarted", runParams); //注册节点启动前事件
		} else if (ruleType.equals("EndRuleNum")) {
			BeanCtx.getEventEngine().run(processid, nodeid, "NodeBeforeEnd", runParams); //注册节点结束前事件
		}

		BeanCtx.getExecuteEngine().run(ruleNum, runParams); //运行节点类型配置表中配置的节点启动和结束规则，规则中会启动或者结束相应的环节

		if (ruleType.equals("StartRuleNum")) {
			//说明是启动环节，注册节点启动事件
			BeanCtx.getEventEngine().run(processid, nodeid, "NodeAfterStarted", runParams); //注册节点启动后事件
		} else if (ruleType.equals("EndRuleNum")) {
			BeanCtx.getEventEngine().run(processid, nodeid, "NodeAfterEnd", runParams); //注册节点结束后事件
		}
	}

	/**
	 * 获得流程提交后的提示消息
	 * 
	 * @return
	 */
	public String getRunMsg() {
		String returnMsg = "";
		if (this.getEndNodeid() == null) {
			//计算本次提交所影响的所有活动的环节和用户列表
			String sql = "select NodeName from BPM_InsNodeList where DocUnid='" + docUnid
					+ "' and (NodeType='Task' or NodeType='SubProcess') and ActionNum='" + actionNum
					+ "' and Status='Current'";
			String nextNodeName = Rdb.getValueBySql(sql); //得到活动的环节的名称
			sql = "select Userid from BPM_InsUserList where DocUnid='" + docUnid + "' and ActionNum='" + actionNum
					+ "' and Status='Current'";
			String nextUserList = Rdb.getValueBySql(sql); //得到提交的活动的用户的id
			if (Tools.isNotBlank(nextUserList)) {
				nextUserList = BeanCtx.getLinkeyUser().getCnName(nextUserList);
			}
			if (Tools.isNotBlank(nextNodeName) && Tools.isNotBlank(nextUserList)) {
				returnMsg = BeanCtx.getMsg("Engine", "RunMsgNodeAndUser", nextNodeName, nextUserList); //有节点有用户
			} else if (Tools.isNotBlank(nextNodeName) && Tools.isBlank(nextUserList)) {
				returnMsg = BeanCtx.getMsg("Engine", "RunMsgOnlyNode", nextNodeName); //只有节点没有用户
			} else if (Tools.isBlank(nextNodeName) && Tools.isNotBlank(nextUserList)) {
				returnMsg = BeanCtx.getMsg("Engine", "RunMsgOnlyUser", nextUserList); //只有用户没有节点
			} else {
				//得到所有活动的用户
				sql = "select Userid from BPM_InsUserList where DocUnid='" + docUnid + "' and Status='Current'";
				nextUserList = Rdb.getValueBySql(sql);
				if (Tools.isNotBlank(nextUserList)) {
					//有其他活动的用户
					nextUserList = BeanCtx.getLinkeyUser().getCnName(nextUserList);
					returnMsg = BeanCtx.getMsg("Engine", "RunMsgOnlyOtherUser", nextUserList);
				} else {
					//没有活动的用户
					returnMsg = BeanCtx.getMsg("Engine", "RunMsgSuccess");
				}
			}
		} else {
			returnMsg = BeanCtx.getMsg("Engine", "ProcessArchived"); //流程已归档
		}
		return returnMsg;
	}

	/**
	 * <pre>
	 * 检测当前用户是否是在以下几种Action之中:
	 *     1.串行审批
	 *     2.会签审批,如果是则只有最后一个用户才可以选择后继环节和用户 
	 *     3.返回给转交者 
	 *     4.返回给回退者
	 * </pre>
	 * 
	 * @return 返回数据标识
	 * <pre>
	 *   可根据标识来获得工作流引擎所需的Actionid参数
	 *   0：最后的用户可以选择后继节点和用户(EndUserTask) 
	 *   1：串行审批(GoToNextSerialUser) 
	 *   2：返回转交者(BackToDeliver) 
	 *   3：表示返回给回退者(BackToReturnUser) 
	 *   4：表示会签中(GoToNextParallelUser)
	 * </pre>
	 */
	public int canSelectNodeAndUser() {

		//强制显示后继环节和人员选项，不再进行会签，返回等的判断
		if (BeanCtx.getCtxDataStr("WF_CanGoToNextNode").equals("1")) {
			return 0;
		}

		//1.看是否串行审批
		if (currentModNodeDoc.g("IsSequential").equals("1")) {
			//说明是串行会签,看是否还有Wait状态的用户，如果有就说明还不是最后一个
			String sql = "select * from BPM_InsUserList where DocUnid='" + docUnid + "' and Nodeid='" + currentNodeid
					+ "' and Status='Wait' order by SerialIndexNum";
			if (Rdb.hasRecord(sql)) {
				return 1;
			}
		}

		//2.看是否需要返回转交者,如果是且要等于2说明要返回给转交者
		if (currentInsUserDoc.g("ReassignmentFlag").equals("2")) {
			return 2;
		}

		//3.看是否需要返回给回退者,如果是且要等于2说明要返回给转交者
		if (currentInsUserDoc.g("IsBackFlag").equals("2")) {
			return 3;
		}

		//4.看是否会签即LoopType=3的时候
		if (currentModNodeDoc.g("LoopType").equals("3")) {
			//说明是标准会签模式，所有用户处理完成才能结束本环节
			String sql = "select WF_OrUnid from BPM_InsUserList where docUnid='" + docUnid + "' and Userid<>'"
					+ BeanCtx.getUserid() + "' and nodeid='" + currentNodeid + "' and Status='Current'";
			if (Rdb.hasRecord(sql)) {
				if (currentModNodeDoc.g("AgreeButtonFlag").equals("Y")) {
					return 5;
				} else {
					return 4; //说明本环节还有用户未处理完成直接返回，停止推进后继路由线
				}
			}
		}

		return 0;
	}

	/**
	 * 存盘驻留中间状态环节的数据,如前置事件，后置事件，网关等 只有节点和用户的提交数据才是有效的中间驻留数据，不存在单独提交用户而没有目标节点的可能性
	 * 
	 * @param processid 流程id
	 * @param nodeid 节点id
	 * @param docUnid 文档id
	 * @param params 运行参数
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	public void saveStayData(String processid, String nodeid, String docUnid, HashMap<String, Object> params) {
		HashSet<String> nextNodeList = (HashSet<String>) params.get("WF_NextNodeid");//要提交的节点类型为HashSet<String> 值为nodeid多个逗号分隔
		HashMap<String, String> nextUserList = (HashMap<String, String>) params.get("WF_NextUserList");//key为nodeid,value为userid多个逗号分隔
		HashMap<String, HashMap<String, String>> nextUserDept = (HashMap<String, HashMap<String, String>>) params
				.get("WF_NextUserDept");//兼职时有用 key为nodeid,value为用户与部门id的map组合

		//如果没有目标节点则不存中间数据
		if (nextNodeList == null) {
			return;
		}
		String nextUserStr = "";
		for (String nextNodeid : nextNodeList) {
			if (nextUserList != null) {
				nextUserStr = nextUserList.get(nextNodeid);
			}
			//获得本环节的userid与deptid的组合字符串
			HashMap<String, String> userDeptSet = new HashMap<String, String>();
			if (nextUserDept != null) {
				userDeptSet = nextUserDept.get(nextNodeid);
			}

			//存临时的驻留数据
			Document doc = Rdb.getDocumentBySql("BPM_InsStayData", "select * from BPM_InsStayData where DocUnid='"
					+ docUnid + "' and Userid='" + BeanCtx.getUserid() + "' and WF_NextNodeid='" + nextNodeid + "'");
			doc.s("Processid", processid);
			doc.s("StayNodeid", nodeid);
			doc.s("CurNodeid", BeanCtx.getLinkeywf().getCurrentNodeid());
			doc.s("DocUnid", docUnid);
			doc.s("Userid", BeanCtx.getUserid());
			doc.s("WF_RunActionid", (String) params.get("WF_RunActionid"));
			doc.s("WF_NextNodeid", nextNodeid);
			doc.s("WF_NextUserList", nextUserStr);
			doc.s("WF_NextUserDept", nextUserDept.toString());
			doc.s("WF_SendSms", BeanCtx.g("WF_SendSms", true));
			doc.s("WF_TimeRuleNum", (String) params.get("WF_TimeRuleNum"));
			doc.s("WF_TimeDelay", (String) params.get("WF_TimeDelay"));
			doc.save();
		}
	}

	private boolean isValidSysDate() {
		//如果是正式用户则不进行检测1表示正式用户，2表示试用用户
		if (BeanCtx.getSystemConfig("SystemType").equals("1")) {
			return false;
		}
		Document doc = Rdb.getDocumentBySql("select * from BPM_SystemInfo");
		String ctdName = doc.g("CtdName");
		ctdName = IIIIILIIIIIIIII.L1IIIL1L1IIIIIIIIIILLLIIIIIIIIIIIIIIIIIIIIIILLL(ctdName);
		String startDate = IIIIILIIIIIIIII.L1IIIL1L1IIIIIIIIIILLLIIIIIIIIIIIIIIIIIIIIIILLL(doc.g("EndDate"));
		if (Tools.isBlank(startDate)) {
			return true;
		}
		if (DateUtil.lessTime(startDate + " 00:00", DateUtil.getNow())) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 获得当前用户所处环节需要加载的子表单列表
	 * 
	 * @param a true表示包含处理子表单 false表示否
	 * @return 返回字符串，多个用逗号分隔
	 */
	public String getCurNodeSubFormList(boolean approvalForm) {
		//只读时返回空值
		if (readOnly) {
			return "";
		}
		String subFormNumber = currentModNodeDoc.g("SubFormNumberLoad"); //当前环节是否需要加载子表单
		if (approvalForm) {
			//如果需要包含审批子表单
			if (currentModNodeDoc.g("ApprovalAutoFlag").equals("2")) {
				//说明指定了处理单的子表单
				String approvalFormNumber = currentModNodeDoc.g("CusApprovalFormNum");
				if (Tools.isNotBlank(approvalFormNumber)) {
					if (Tools.isNotBlank(subFormNumber)) {
						subFormNumber = subFormNumber + "," + approvalFormNumber;
					} else {
						subFormNumber = approvalFormNumber;
					}
				}
			}
		}
		return subFormNumber;
	}

	/**
	 * 获得当前用户与当前流程会话的Action动作id即linkeywf.Run()中所要求的Actionid
	 * 
	 * @return
	 */
	public String getCurrentActionid() {
		if (Tools.isBlank(this.currentNodeid)) {
			return "";//当前用户无权审批
		}
		int canNextNodeFlag = canSelectNodeAndUser(); //获得是否可以选择后继环节返回0表示可以
		String actionid = "";
		if (canNextNodeFlag == 0) {
			actionid = "EndUserTask";
		} else if (canNextNodeFlag == 1) {
			actionid = "GoToNextSerialUser";
		} else if (canNextNodeFlag == 2) {
			actionid = "BackToDeliver";
		} else if (canNextNodeFlag == 3) {
			actionid = "BackToReturnUser";
		} else if (canNextNodeFlag == 4) {
			actionid = "GoToNextParallelUser";
		}
		return actionid;
	}

	/**
	 * 存盘流程主文档到数据库中
	 */
	public String saveDocument() throws Exception {
		return ((MainDoc) BeanCtx.getBean("MainDoc")).saveDocument();
	}

	/**
	 * 清空linkeywf所持有的集合对像
	 */
	public void clear() {
		if (this.document != null) {
			this.document.clear();
		}
		if (this.currentInsUserDoc != null) {
			this.currentInsUserDoc.clear();
		}
		if (this.currentModNodeDoc != null) {
			this.currentModNodeDoc.clear();
		}
		//        if(this.formDoc!=null){
		//            this.formDoc.clear(); 这个不能清，因为formDoc是有缓存的，一清的话就把缓存中的表单数据也给清除了
		//        }
	}

	//以下为属性的设置和获取方法
	/**
	 * 设置工作流引擎的结束节点id，设置后流程将自动结束并归档
	 * 
	 * @return
	 */
	public void setEndNodeid(String nodeid) {
		this.endNodeid = nodeid;
	}

	/**
	 * 获得工作流引擎设置的结束状态节点
	 * 
	 * @return
	 */
	public String getEndNodeid() {
		return this.endNodeid;
	}

	public String getAppid() {
		return appid;
	}

	public Document getCurrentInsUserDoc() {
		return currentInsUserDoc;
	}

	public void setCurrentInsUserDoc(Document currentInsUserDoc) {
		this.currentInsUserDoc = currentInsUserDoc;
	}

	public Document getCurrentModNodeDoc() {
		return currentModNodeDoc;
	}

	public void setCurrentModNodeDoc(Document currentModNodeDoc) {
		this.currentModNodeDoc = currentModNodeDoc;
	}

	public void setAppid(String appid) {
		this.appid = appid;
	}

	public String getUserid() {
		return userid;
	}

	public String getUserName() {
		return userName;
	}

	public String getDocUnid() {
		return docUnid;
	}

	public String getFormNumber() {
		return formNumber;
	}

	public void setFormNumber(String formNumber) {
		this.formNumber = formNumber;
	}

	public String getProcessid() {
		return processid;
	}

	public void setProcessid(String processid) {
		this.processid = processid;
	}

	public String getProcessNumber() {
		return processNumber;
	}

	public void setProcessNumber(String processNumber) {
		this.processNumber = processNumber;
	}

	public String getCurrentNodeid() {
		return currentNodeid;
	}

	public void setCurrentNodeid(String currentNodeid) {
		this.currentNodeid = currentNodeid;
	}

	public String getProcessName() {
		return processName;
	}

	public void setProcessName(String processName) {
		this.processName = processName;
	}

	public Document getDocument() {
		return document;
	}

	public boolean getIsNewProcess() {
		return isNewProcess;
	}

	public void setNewProcess(boolean isNewProcess) {
		this.isNewProcess = isNewProcess;
	}

	public String getLockStatus() {
		return lockStatus;
	}

	public String getCurrentNodeName() {
		return currentNodeName;
	}

	public void setCurrentNodeName(String currentNodeName) {
		this.currentNodeName = currentNodeName;
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	public String getActionNum() {
		return actionNum;
	}

	public void setActionNum(String actionNum) {
		this.actionNum = actionNum;
	}

	public String getSourceOrUnid() {
		if (getCurrentInsUserDoc() != null) {
			return getCurrentInsUserDoc().g("WF_OrUnid");
		} else {
			return "";
		}
	}

	public Document getFormDoc() {
		return formDoc;
	}

	public void setFormDoc(Document formDoc) {
		this.formDoc = formDoc;
	}

	public void setProcessOwner(boolean isProcessOwner) {
		this.isProcessOwner = isProcessOwner;
	}

	public boolean isProcessOwner() {
		return isProcessOwner;
	}

	public boolean isRunStatus() {
		return runStatus;
	}

	public void setRunStatus(boolean runStatus) {
		this.runStatus = runStatus;
	}

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public String getRollbackMsg() {
		return rollbackMsg;
	}

	public void setRollbackMsg(String rollbackMsg) {
		this.rollbackMsg = rollbackMsg;
	}

	public boolean isFirstNode() {
		return isFirstNode;
	}

	public void setFirstNode(boolean isFirstNode) {
		this.isFirstNode = isFirstNode;
	}

	public String getMainTableName() {
		return mainTableName;
	}

	public void setMainTableName(String mainTableName) {
		this.mainTableName = mainTableName;
	}

	public String getArcTableName() {
		return arcTableName;
	}

	public void setArcTableName(String arcTableName) {
		this.arcTableName = arcTableName;
	}

	public String getExtendTable() {
		return extendTable;
	}

	public void setExtendTable(String extendTable) {
		this.extendTable = extendTable;
	}

	public HashSet<Document> getMaildc() {
		return maildc;
	}

	public void setMaildc(HashSet<Document> maildc) {
		this.maildc = maildc;
	}

	public Document getProcessModNodedoc() {
		return processModNodedoc;
	}

	public void setProcessModNodedoc(Document processModNodedoc) {
		this.processModNodedoc = processModNodedoc;
	}

	//属性获取和设置结束

}
