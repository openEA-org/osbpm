package cn.linkey.rulelib.S003;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.form.HtmlParser;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * 
 * Copyright © 2018 A Little Bao. All rights reserved.
 * 
 * @ClassName: R_S003_B082.java
 * @Description: 本规则负责分析input标签 layui
 *
 * @version: v1.0.0
 * @author: Alibao
 * @date: 2018年2月24日 下午4:41:43
 *
 *        Modification History: Date Author Version Description
 *        ---------------------------------------------------------* 2018年2月24日
 *        Alibao v1.0.0 修改原因
 */
public class R_S003_B082 implements LinkeyRule {
	private String defaultHeight = "28px";

	@Override
	@SuppressWarnings("unchecked")
	public String run(HashMap<String, Object> params) {
		String mStr = (String) params.get("HtmlStr"); // 传的的input标签的内容
		Document doc = (Document) params.get("DataDocument"); // 数据主文档对像，包含所有字段数据
		String readOnly = (String) params.get("ReadOnly"); // 1表示只读状态，0表示编辑状态
		String fdName = (String) params.get("FieldName"); // 字段名称
		Map<String, String> fieldConfigMap = (Map<String, String>) params.get("FieldConfig"); // 字段的配置属性
		if (readOnly.equals("1")) {
			return readDoc(doc, mStr, fdName, fieldConfigMap); // 阅读模式
		} else {
			// 编辑模式
			return editDoc(doc, mStr, fdName, fieldConfigMap);
		}
	}

	/**
	 * 只读状态下解析
	 * 
	 * @param doc
	 *            数据文档对像
	 * @param mStr
	 *            HTML标签
	 * @return 返回只读模式的HTML标签
	 */
	public String readDoc(Document doc, String mStr, String fdName, Map<String, String> fieldConfigMap) {
		HtmlParser htmlParser = (HtmlParser) BeanCtx.getBean("HtmlParser");
		String inputType = htmlParser.getAttributeValue(mStr, "type").toLowerCase();
		// 如果主文档中有_show的字段则使用_show的字段作为显示值
		String fdValue = doc.g(fdName + "_show");
		if (Tools.isBlank(fdValue)) {
			fdValue = doc.g(fdName);
		}

		//System.out.println("只读状态下解析");
		if (inputType.equals("checkbox")) {
			// 设置复选档框的值为选中状态 <input type="checkbox" value="1" checked >yes
			String value = htmlParser.getAttributeValue(mStr, "value");
			HashSet<String> vSet = Tools.splitAsSet(doc.g(fdName));// 复选框是多值的，多个同名的所以是用逗号分隔的
			if (vSet.contains(value)) {
				mStr = htmlParser.setAttribute(mStr, "checked");// 选中状态
			} else {
				mStr = htmlParser.removeAttribute(mStr, "checked");// 取消选中
			}
			mStr = htmlParser.setAttribute(mStr, "disabled"); // 设置为disabled
		} else if (inputType.equals("radio")) {
			// 设置单选的选中状态<input type="radio" value="1" checked >yes
			String value = htmlParser.getAttributeValue(mStr, "value");
			if (doc.g(fdName).equals(value)) {
				mStr = htmlParser.setAttribute(mStr, "checked");// 选中状态
			} else {
				mStr = htmlParser.removeAttribute(mStr, "checked");// 取消选中
			}
			mStr = htmlParser.setAttribute(mStr, "disabled"); // 设置为disabled
		} else if (inputType.equals("button") || inputType.equals("password")) {
			// 说明是按扭或者是密码框
			mStr = ""; // 按扭和密码的话直接返回空值进行隐藏
		} else if (inputType.equals("hidden")) {
			// 如果是隐藏字段则输出隐藏的内容
			mStr = "<span id=\"" + fdName + "\" style=\"display:none\" >" + fdValue + "</span>";
		} else {
			mStr = "<span id=\"" + fdName + "\" >" + fdValue + "</span>";
		}
		return mStr;
	}

	/**
	 * 编辑状态下解析
	 * 
	 * @param doc
	 *            数据文档对像
	 * @param mStr
	 *            HTML标签
	 * @return 返回编辑模式的HTML标签
	 */
	public String editDoc(Document doc, String mStr, String fdName, Map<String, String> fieldConfigMap) {
		HtmlParser htmlParser = (HtmlParser) BeanCtx.getBean("HtmlParser");
		String inputType = htmlParser.getAttributeValue(mStr, "exttype").toLowerCase();// 先读取标签中的扩展类型，扩展类型优先
		if (Tools.isBlank(inputType)) {
			// inputType = htmlParser.getAttributeValue(mStr,
			// "typename").toLowerCase(); // 20180424添加layui的解析属性
			// if (Tools.isBlank(inputType)) {
			inputType = htmlParser.getAttributeValue(mStr, "type").toLowerCase(); // 标签原始类型
			// }
		}
		if (Tools.isBlank(inputType) || inputType.equals("text")) {
			mStr = editAttrForInputText(doc, fdName, mStr, fieldConfigMap);// 设置普通文本字段的属性和值,支持
																			// NodeFdAcl
		}
		// else if (inputType.equals("date") || inputType.equals("datetime")) {
		// //mStr = editAttrForInputDate(doc, fdName, mStr, fieldConfigMap);//
		// 设置日期字段的属性和值,支持
		// if(inputType.equals("date")) mStr+="<script>layui.use('laydate',
		// function(){var laydate = layui.laydate;laydate.render({elem:
		// '#"+fdName+"'});});</script>";
		// else if(inputType.equals("datetime"))
		// mStr+="<script>layui.use('laydate',
		// function(){var laydate = layui.laydate;laydate.render({elem:
		// '#"+fdName+"',type:'datetime'});});</script>";
		else if (inputType.equals("date") || inputType.equals("datetime")) {
			mStr = editAttrForInputDate(doc, fdName, mStr, fieldConfigMap, inputType);// 设置日期字段的属性和值,支持

		} else if (inputType.equals("radio")) {
			mStr = editAttrForRadio(doc, fdName, mStr, fieldConfigMap); // 设置radio字段的属性和值,支持
																		// NodeFdAcl
		} else if (inputType.equals("checkbox")) {
			mStr = editAttrForCheckbox(doc, fdName, mStr, fieldConfigMap); // 设置checkbox字段的属性和值,支持
																			// NodeFdAcl
		} else if (inputType.equals("combobox")) {
			mStr = editAttrForInputComboBox(doc, fdName, mStr, fieldConfigMap); // 设置combobox字段的属性和值,支持
																				// NodeFdAcl
		} else if (inputType.equals("combotree")) {
			mStr = editAttrForInputComboTree(doc, fdName, mStr, fieldConfigMap); // 设置combotree字段的属性和值,支持
																					// NodeFdAcl
		} else if (inputType.equals("button")) {
			mStr = editAttrForButton(doc, fdName, mStr, fieldConfigMap);// 设置按扭字段的属性和值
		} else if (inputType.equals("password")) {
			mStr = editAttrForPwdText(doc, fdName, mStr, fieldConfigMap);// 设置密码字段的属性和值
		} else if (inputType.equals("hidden")) {
			if (doc.hasItem(fdName)) {
				mStr = htmlParser.setAttributeValue(mStr, "value", doc.g(fdName));
			} // 直接设置字段的值即可
		}
		return mStr;
	}

	/**
	 * 解析input-text类型的标签
	 * 
	 * @param doc
	 * @param mStr
	 * @param fieldConfigMap
	 * @return
	 */
	public String editAttrForInputText(Document doc, String fdName, String mStr, Map<String, String> fieldConfigMap) {
		HtmlParser htmlParser = (HtmlParser) BeanCtx.getBean("HtmlParser");
		// 设置字段的值
		if (doc.hasItem(fdName)) {
			mStr = htmlParser.setAttributeValue(mStr, "value", doc.g(fdName));
		}
		// 说明没有字段属性配置
		if (fieldConfigMap == null) {
			return mStr;
		}
		boolean showSelector = true; // true表示显示选择器，false表示不显示

		// 1.首先判断隐藏模式,是否编辑时隐藏如果是，则首先返回不用判断其他类型了
		String attrValue = fieldConfigMap.get("hiddentype");
		if (attrValue != null) {
			if (doc.isNewDoc()) {
				if (attrValue.indexOf("NEW") != -1) {
					return "";
				}
			} else if (attrValue.indexOf("EDIT") != -1) {
				return "";
			}
		}

		// 2.分析流程环节字段权限 NodeFdAcl,只有在流程环节的字段权限中才有的属性，应用表单中没有此属性
		String nodeFdAcl = fieldConfigMap.get("NodeFdAcl");
		if (nodeFdAcl != null) {
			String fdValue = doc.g(fdName + "_show");
			if (Tools.isBlank(fdValue)) {
				fdValue = doc.g(fdName);
			}
			if (nodeFdAcl.equals("HIDDEN")) {
				return ""; // 隐藏
			} else if (nodeFdAcl.equals("READ")) {
				return "<span id=\"" + fdName + "\">" + fdValue + "</span>"; // 只读(不保存数据)
			} else if (nodeFdAcl.equals("READSAVE")) {
				mStr = htmlParser.setAttributeValue(mStr, "style", "display:none");
				return mStr + "<span id=\"" + fdName + "_show\" >" + fdValue + "</span>"; // 只读(需保存数据)
			} else if (nodeFdAcl.equals("EDIT")) {
				// 把只读模式强制删除，这样就成为可编辑的字段了
				fieldConfigMap.remove("readtype");
			} else if (nodeFdAcl.equals("USEFORM")) {
				// 继承表单中的只读模式
				String readtype = fieldConfigMap.get("readtype_old"); // 得到表单字段原有的只读模式
				// BeanCtx.out("原有只读模式"+fdName+"="+readtype);
				fieldConfigMap.put("readtype", readtype); // 把只读模式恢复到原有状态
			}
		}

		// 3.分析只读模式加上_show的span,因为input文本框只读时后面有可能进行不为空较验，还有callback的js事件也需要起作用
		String readtype = fieldConfigMap.get("readtype");
		if (readtype != null) {
			mStr = htmlParser.parserReadType(doc, mStr, readtype, fdName);
		}

		// 3.1只有在流程模式下才需要判断,判断是否显示选择器
		if (BeanCtx.getLinkeywf() != null) {
			if (nodeFdAcl != null) {
				// 环节中有配置的情况下,只有Edit模式和继承表单模式时字段旁边才可以显示选择器
				if (!nodeFdAcl.equals("EDIT") && !nodeFdAcl.equals("USEFORM")) {
					showSelector = false;
					// BeanCtx.out(fdName+"=环节中设定为可编辑");
				}
			} else if (readtype != null) {
				// 环节中没有配置字段权限时，只有节点中选择了默认字段为只读时才禁止显示选择器
				String nodeDefaultRead = fieldConfigMap.get("NodeDefaultRead");
				if (nodeDefaultRead == null) {
					nodeDefaultRead = "1";
				}
				if (readtype.equals("ALL") && nodeDefaultRead.equals("1")) {
					showSelector = false;
					// BeanCtx.out(fdName+"=环节中默认为只读");
				}
			}
		}

		// 4.绑定选择器
		String selector = "";
		attrValue = fieldConfigMap.get("selector");
		if (attrValue != null) {
			if (showSelector == true) {
				selector = htmlParser.getSelector(fdName, attrValue); // 显示选择器
			}
		}
		// 2018.1.24 注释掉dataoptions
		String dataoptions = "";

		// // 5.必填字段验证
		if (mStr.indexOf("<span") == -1)
			attrValue = fieldConfigMap.get("required");
		else
			attrValue = null;
		// attrValue = fieldConfigMap.get("required");
		if (attrValue != null) {
			mStr = htmlParser.setAttributeValue(mStr, "lay-verify", "required");
			// mStr = htmlParser.appendAttributeValue(mStr,
			// "lay-verify","required", "");
		}

		// 6.验证规则 20180315修改
		// 2018.03.16 当span标签时不进行验证
		if (mStr.indexOf("<span") == -1)
			attrValue = fieldConfigMap.get("lay-verify");
		else
			attrValue = null;
		// attrValue = fieldConfigMap.get("lay-verify");
		if (attrValue != null) {
			mStr = htmlParser.setAttributeValue(mStr, "lay-verify", attrValue);
		}
		// 7.只有在字段没有选为只读的情况下才显示必填选项的class，否则在span标签中就出现必填的叹号
		if (mStr.indexOf(fdName + "_show") == -1 && mStr.indexOf("<input") != -1) { // 如果字段没有被设置为只读状态则需要追加
																					// class
			// 7.追加验证规则的class
			String className = htmlParser.getAttributeValue(mStr, "class");
			if (className.indexOf("layui-") == -1) {
				mStr = htmlParser.appendAttributeValue(mStr, "class", "layui-input", ""); // 追加
			}
		}

		// 8.设置为空时的验证提示消息
		attrValue = fieldConfigMap.get("valimsg");
		if (attrValue != null) {
			dataoptions = htmlParser.setDataOptions(dataoptions, "missingMessage", attrValue, true);
		}

		// 9.jsevent事件,js事件要在只读模式前面，因为只读时也可能有callback事件
		attrValue = fieldConfigMap.get("jsevent");
		if (attrValue != null) {
			String jsfun = fieldConfigMap.get("jsfun");
			if (jsfun != null) {
				mStr = htmlParser.setAttributeValue(mStr, attrValue, jsfun);
			}
		}

		// 10.设置data-options属性
		if (Tools.isNotBlank(dataoptions)) {
			mStr = htmlParser.setAttributeValue(mStr, "lay-data", dataoptions);
		}

		mStr = mStr + selector;
		return mStr;
	}

	/**
	 * 解析input-combotree类型的标签
	 * 
	 * @param doc
	 * @param mStr
	 * @param fieldConfigMap
	 * @return
	 */
	public String editAttrForInputComboTree(Document doc, String fdName, String mStr,
			Map<String, String> fieldConfigMap) {

		HtmlParser htmlParser = (HtmlParser) BeanCtx.getBean("HtmlParser");

		// 设置字段的值
		if (doc.hasItem(fdName)) {
			mStr = htmlParser.setAttributeValue(mStr, "value", doc.g(fdName));
		}
		mStr = htmlParser.appendAttributeValue(mStr, "style", "height:" + defaultHeight + "", ";");// 统一设置高度

		// 说明没有字段属性配置
		if (fieldConfigMap == null) {
			return mStr;
		}

		// 1.首先判断是否编辑时隐藏如果是，则首先返回不用判断其他类型了
		String attrValue = fieldConfigMap.get("hiddentype");
		if (attrValue != null) {
			if (doc.isNewDoc()) {
				if (attrValue.indexOf("NEW") != -1) {
					return "";
				}
			} else if (attrValue.indexOf("EDIT") != -1) {
				return "";
			}
		}

		// 2.分析流程环节字段权限 NodeFdAcl,只有在流程环节的字段权限中才有的属性，应用表单中没有此属性
		attrValue = fieldConfigMap.get("NodeFdAcl");
		if (attrValue != null) {
			String fdValue = doc.g(fdName);
			if (attrValue.equals("HIDDEN")) {
				return ""; // 隐藏
			} else if (attrValue.equals("READ") || attrValue.equals("READSAVE")) {
				return "<span id=\"" + fdName + "\">" + fdValue + "</span>"; // 只读(不保存数据)
			} else if (attrValue.equals("EDIT")) {
				fieldConfigMap.remove("readtype"); // 删除只读模式，恢复到可编辑状态
			}
		}

		// 2.判断只读模式,combotree在只读模式下是不能保存数据的，所以也不用进行必填验证等，直接返回只读字符串即可
		attrValue = fieldConfigMap.get("readtype");
		if (attrValue != null) {
			return htmlParser.parserReadType(doc, mStr, attrValue, fdName);
		}

		// 3.必填字段验证
		String dataoptions = htmlParser.getAttributeValue(mStr, "data-options");
		attrValue = fieldConfigMap.get("required");
		if (attrValue != null) {
			// dataoptions = htmlParser.setDataOptions(dataoptions, "required",
			// "true", false);
			htmlParser.appendAttributeValue(fdName, "lay-verify", "required", " ");
			// htmlParser.setAttributeValue(mStr, "lay-verify", "required");
			// htmlParser.setAttributeValue(mStr, "required", "required");
		}

		// 4.验证规则
		attrValue = fieldConfigMap.get("validtype");
		if (attrValue != null) {
			dataoptions = htmlParser.setDataOptions(dataoptions, "validType", attrValue, true);
		}

		// 5.设置为空时的验证提示消息
		attrValue = fieldConfigMap.get("valimsg");
		if (attrValue != null) {
			dataoptions = htmlParser.setDataOptions(dataoptions, "missingMessage", attrValue, true);
		}

		// 6.设置json数据源
		attrValue = fieldConfigMap.get("url");
		if (attrValue != null) {
			String url = attrValue;
			if (url.startsWith("D_") || url.startsWith("T_")) {
				url = "r?wf_num=" + url;
			}
			url = Tools.parserStrByDocument(doc, url); // 分析url中的{}中所包含的字段参数
			String method = fieldConfigMap.get("method");
			String formatter = fieldConfigMap.get("formatter");
			String multiple = fieldConfigMap.get("multiple");
			if (Tools.isNotBlank(dataoptions)) {
				dataoptions += ",";
			}
			dataoptions += "lines:true,cascadeCheck:false,url:'" + url + "'";
			if (multiple != null) {
				dataoptions += ",multiple:" + multiple;
			}
			if (method != null) {
				dataoptions += ",method:'" + method + "'";
			}
			if (formatter != null) {
				dataoptions += ",formatter:" + formatter;
			}

		}

		// 7.jsevent事件
		String jsEventStr = "";
		attrValue = fieldConfigMap.get("onSelect");
		if (attrValue != null) {
			dataoptions += ",onSelect:function(node){" + attrValue + "}";
		}

		// 10.设置data-options属性
		if (Tools.isNotBlank(dataoptions)) {
			mStr = htmlParser.setAttributeValue(mStr, "data-options", dataoptions);
		}

		mStr = mStr + jsEventStr;
		return mStr;
	}

	/**
	 * 解析ComboBox类型的标签
	 * 
	 * @param doc
	 * @param mStr
	 * @param fieldConfigMap
	 * @return
	 */
	public String editAttrForInputComboBox(Document doc, String fdName, String mStr,
			Map<String, String> fieldConfigMap) {
		HtmlParser htmlParser = (HtmlParser) BeanCtx.getBean("HtmlParser");
		if (doc.hasItem(fdName)) {
			mStr = htmlParser.setAttributeValue(mStr, "value", doc.g(fdName));
		} // 设置字段的值
		mStr = htmlParser.appendAttributeValue(mStr, "style", "height:" + defaultHeight + "", ";");// 统一设置高度
		if (fieldConfigMap == null) {
			return mStr;
		} // 说明没有字段属性配置

		// 1.首先判断是否编辑时隐藏如果是，则首先返回不用判断其他类型了
		String attrValue = fieldConfigMap.get("hiddentype");
		if (attrValue != null) {
			if (doc.isNewDoc()) {
				if (attrValue.indexOf("NEW") != -1) {
					return "";
				}
			} else if (attrValue.indexOf("EDIT") != -1) {
				return "";
			}
		}

		// 2.分析流程环节字段权限 NodeFdAcl,只有在流程环节的字段权限中才有的属性，应用表单中没有此属性
		attrValue = fieldConfigMap.get("NodeFdAcl");
		if (attrValue != null) {
			String fdValue = doc.g(fdName);
			if (attrValue.equals("HIDDEN")) {
				return ""; // 隐藏
			} else if (attrValue.equals("READ") || attrValue.equals("READSAVE")) {
				return "<span id=\"" + fdName + "\">" + fdValue + "</span>"; // 只读(不保存数据)
			} else if (attrValue.equals("EDIT")) {
				fieldConfigMap.remove("readtype"); // 删除只读模式，恢复到可编辑状态
			}
		}

		// 2.判断只读模式,combobox在只读模式下是不能保存数据的，所以也不用进行必填验证等，直接返回只读字符串即可
		attrValue = fieldConfigMap.get("readtype");
		if (attrValue != null) {
			return htmlParser.parserReadType(doc, mStr, attrValue, fdName);
		}

		// 3.必填字段验证
		String dataoptions = htmlParser.getAttributeValue(mStr, "data-options");
		attrValue = fieldConfigMap.get("required");
		if (attrValue != null) {
			dataoptions = htmlParser.setDataOptions(dataoptions, "required", "true", false);
		}

		// 4.验证规则
		attrValue = fieldConfigMap.get("validtype");
		if (attrValue != null) {
			dataoptions = htmlParser.setDataOptions(dataoptions, "validType", attrValue, true);
		}

		// 5.设置为空时的验证提示消息
		attrValue = fieldConfigMap.get("valimsg");
		if (attrValue != null) {
			dataoptions = htmlParser.setDataOptions(dataoptions, "missingMessage", attrValue, true);
		}

		// 6.设置json数据源
		attrValue = fieldConfigMap.get("url");
		if (attrValue != null) {
			String url = attrValue;
			if (url.startsWith("D_") || url.startsWith("T_") || url.startsWith("S_")) { // 141215增加S_类型的数据字典
				url = "r?wf_num=" + url;
			}
			url = Tools.parserStrByDocument(doc, url); // 分析url中的{}中所包含的字段参数
			String valuefield = fieldConfigMap.get("valuefield");
			String textField = fieldConfigMap.get("textField");
			String groupField = fieldConfigMap.get("groupField");
			String method = fieldConfigMap.get("method");
			String formatter = fieldConfigMap.get("formatter");
			String multiple = fieldConfigMap.get("multiple");
			if (Tools.isNotBlank(dataoptions)) {
				dataoptions += ",";
			}
			dataoptions += "url:'" + url + "'";
			if (valuefield != null) {
				dataoptions += ",valueField:'" + valuefield + "'";
			}
			if (textField != null) {
				dataoptions += ",textField:'" + textField + "'";
			}
			if (groupField != null) {
				dataoptions += ",groupField:'" + groupField + "'";
			}
			if (multiple != null) {
				dataoptions += ",multiple:" + multiple;
			}
			if (method != null) {
				dataoptions += ",method:'" + method + "'";
			}
			if (formatter != null) {
				dataoptions += ",formatter:" + formatter;
			}

		}

		// 7.jsevent事件
		attrValue = fieldConfigMap.get("onSelect");
		if (attrValue != null) {
			dataoptions += ",onSelect:function(rc){" + attrValue + "}";
		}

		// 10.设置data-options属性
		if (Tools.isNotBlank(dataoptions)) {
			mStr = htmlParser.setAttributeValue(mStr, "data-options", dataoptions);
		}

		return mStr;
	}

	/**
	 * 解析input-date类型的标签
	 * 
	 * @param doc
	 * @param mStr
	 * @param fieldConfigMap
	 * @return 201803015 新增参数 inputType
	 */
	public String editAttrForInputDate(Document doc, String fdName, String mStr, Map<String, String> fieldConfigMap,
			String inputType) {
		HtmlParser htmlParser = (HtmlParser) BeanCtx.getBean("HtmlParser");
		if (doc.hasItem(fdName)) {
			mStr = htmlParser.setAttributeValue(mStr, "value", doc.g(fdName));
		} // 设置字段的值
		mStr = htmlParser.appendAttributeValue(mStr, "style", "height:" + defaultHeight + "", ";");// 统一设置高度

		// 20180413去除控件配置里面已经添加JS代码，在此添加
		if (inputType.equals("date"))
			mStr += "<script>layui.use('laydate', function(){var laydate = layui.laydate;laydate.render({elem: '#"
					+ fdName + "'});});</script>";
		else if (inputType.equals("datetime"))
			mStr += "<script>layui.use('laydate', function(){var laydate = layui.laydate;laydate.render({elem: '#"
					+ fdName + "',type:'datetime'});});</script>";

		if (fieldConfigMap == null) {
			// 20180413注释掉，发现在控件配置里面已经添加了此JS代码
			// if (inputType.equals("date"))
			// mStr += "<script>layui.use('laydate', function(){var laydate =
			// layui.laydate;laydate.render({elem: '#"
			// + fdName + "'});});</script>";
			// else if (inputType.equals("datetime"))
			// mStr += "<script>layui.use('laydate', function(){var laydate =
			// layui.laydate;laydate.render({elem: '#"
			// + fdName + "',type:'datetime'});});</script>";
			return mStr;
		} // 说明没有字段属性配置

		// 1.首先判断是否编辑时隐藏如果是，则首先返回不用判断其他类型了
		String attrValue = fieldConfigMap.get("hiddentype");
		if (attrValue != null) {
			if (doc.isNewDoc()) {
				if (attrValue.indexOf("NEW") != -1) {
					return "";
				}
			} else if (attrValue.indexOf("EDIT") != -1) {
				return "";
			}
		}

		// 2.分析流程环节字段权限 NodeFdAcl,只有在流程环节的字段权限中才有的属性，应用表单中没有此属性
		attrValue = fieldConfigMap.get("NodeFdAcl");
		if (attrValue != null) {
			String fdValue = doc.g(fdName);
			if (attrValue.equals("HIDDEN")) {
				return ""; // 隐藏
			} else if (attrValue.equals("READ")) {
				return "<span id=\"" + fdName + "\">" + fdValue + "</span>"; // 只读(不保存数据)
			} else if (attrValue.equals("READSAVE")) {
				mStr = htmlParser.setAttributeValue(mStr, "style", "display:none");
				return mStr + "<span id=\"" + fdName + "_show\" >" + fdValue + "</span>"; // 只读(需保存数据)
			} else if (attrValue.equals("EDIT")) {
				fieldConfigMap.remove("readtype"); // 删除日期时间控件的只读模式，变为可编辑状态
			}
		}

		// 2.1.判断表单中设定的只读模式，时间格式的输入框一般不会作只读模式，因为时间选择是一个控件，所以直接返回只读模式即可
		attrValue = fieldConfigMap.get("readtype");
		if (attrValue != null) {
			// 新建和编辑时全部只读
			String fdValue = doc.g(fdName);
			if (attrValue.equals("ALL")) {
				return "<span id=\"" + fdName + "\">" + fdValue + "</span>"; // 只读(不保存数据)
			} else if (attrValue.equals("ALLSAVE")) {
				mStr = htmlParser.setAttributeValue(mStr, "style", "display:none");
				return mStr + "<span id=\"" + fdName + "_show\" >" + fdValue + "</span>"; // 只读(需保存数据)
			}

			if (doc.isNewDoc()) {// 新建时只读
				if (attrValue.equals("NEW")) {
					return "<span id=\"" + fdName + "\">" + fdValue + "</span>"; // 新建时只读(不保存数据)
				} else if (attrValue.equals("NEWSAVE")) {
					mStr = htmlParser.setAttributeValue(mStr, "style", "display:none");
					return mStr + "<span id=\"" + fdName + "_show\" >" + fdValue + "</span>"; // 新建时只读(需保存数据)
				}
			} else { // 编辑时只读
				if (attrValue.equals("EDIT")) {
					return "<span id=\"" + fdName + "\">" + fdValue + "</span>"; // 编辑时只读(不保存数据)
				} else if (attrValue.equals("EDITSAVE")) {
					mStr = htmlParser.setAttributeValue(mStr, "style", "display:none");
					return mStr + "<span id=\"" + fdName + "_show\" >" + fdValue + "</span>"; // 编辑时只读(需保存数据)
				}
			}
		}

		// 3.必填字段验证
		String dataoptions = htmlParser.getAttributeValue(mStr, "data-options");
		attrValue = fieldConfigMap.get("required");
		if (attrValue != null) {
			dataoptions = htmlParser.setDataOptions(dataoptions, "required", "true", false);
		}

		// 4.验证规则
		attrValue = fieldConfigMap.get("validtype");
		if (attrValue != null) {
			dataoptions = htmlParser.setDataOptions(dataoptions, "validType", attrValue, true);
		}

		// 5.设置为空时的验证提示消息
		attrValue = fieldConfigMap.get("valimsg");
		if (attrValue != null) {
			dataoptions = htmlParser.setDataOptions(dataoptions, "missingMessage", attrValue, true);
		}

		// 7.设置data-options属性
		if (Tools.isNotBlank(dataoptions)) {
			mStr = htmlParser.setAttributeValue(mStr, "data-options", dataoptions);
		}
		if (inputType.equals("date"))
			mStr += "<script>layui.use('laydate', function(){var laydate = layui.laydate;laydate.render({elem: '#"
					+ fdName + "'});});</script>";
		else if (inputType.equals("datetime"))
			mStr += "<script>layui.use('laydate', function(){var laydate = layui.laydate;laydate.render({elem: '#"
					+ fdName + "',type:'datetime'});});</script>";
		return mStr;
	}

	/**
	 * 解析input-checkbox类型的标签
	 * 
	 * @param doc
	 * @param mStr
	 * @param fieldConfigMap
	 * @return
	 */
	public String editAttrForCheckbox(Document doc, String fdName, String mStr, Map<String, String> fieldConfigMap) {
		HtmlParser htmlParser = (HtmlParser) BeanCtx.getBean("HtmlParser");

		String attrValue = "";
		if (doc.hasItem(fdName)) {
			// 如果字段存在的情况下，设置复选档框的值为选中状态 <input type="checkbox" value="1" checked
			// >yes
			String value = htmlParser.getAttributeValue(mStr, "value");
			HashSet<String> vSet = Tools.splitAsSet(doc.g(fdName));// 复选框是多值的，多个同名的所以是用逗号分隔的
			// BeanCtx.out("vSet=" + vSet);
			// BeanCtx.out("value=" + value);
			if (vSet.contains(value)) {
				mStr = htmlParser.setAttribute(mStr, "checked");// 选中状态
			} else {
				mStr = htmlParser.removeAttribute(mStr, "checked");// 取消选中
			}
		}

		if (fieldConfigMap != null) {
			// 默认选中 2018.03.27
			attrValue = fieldConfigMap.get("checked");
			if (!doc.hasItem(fdName) && attrValue != null) {
				String checked = fieldConfigMap.get("checked");
				if (checked != null && "true".equals(checked)) {
					mStr = htmlParser.setAttribute(mStr, "checked");// 选中状态
				}
			}

			// 1.首先判断是否编辑时隐藏如果是，则首先返回不用判断其他类型了
			attrValue = fieldConfigMap.get("hiddentype");
			if (attrValue != null) {
				if (doc.isNewDoc()) {
					if (attrValue.indexOf("NEW") != -1) {
						return "";
					}
				} else if (attrValue.indexOf("EDIT") != -1) {
					return "";
				}
			}

			// 2.jsevent事件
			attrValue = fieldConfigMap.get("jsevent");
			if (attrValue != null) {
				String jsfun = fieldConfigMap.get("jsfun");
				if (jsfun != null) {
					mStr = htmlParser.setAttributeValue(mStr, attrValue, jsfun);
				}
			}

			// 3.分析流程环节字段权限 NodeFdAcl,只有在流程环节的字段权限中才有的属性，应用表单中没有此属性
			attrValue = fieldConfigMap.get("NodeFdAcl");
			// BeanCtx.out("R_S003_B026="+fdName+"="+attrValue);
			if (attrValue != null) {
				if (attrValue.equals("HIDDEN")) {
					return ""; // 隐藏
				} else if (attrValue.equals("READ") || attrValue.equals("READSAVE")) {
					return htmlParser.setAttribute(mStr, "disabled"); // 只读(不保存数据)
				} else if (attrValue.equals("EDIT")) {
					// 对于checkbox类型的设置为可编辑时，只需要去掉disabled即可，不用像input
					// text类型的需要恢复原有模式
					return htmlParser.removeAttribute(mStr, "disabled"); // 可编辑
				}
			}

			// 4.判断只读模式
			attrValue = fieldConfigMap.get("readtype");
			if (attrValue != null) {
				// 新建和编辑时全部只读
				if (attrValue.equals("ALL")) {
					return htmlParser.setAttribute(mStr, "disabled"); // 只读(不保存数据)
				}
				if (doc.isNewDoc()) {// 新建时只读
					if (attrValue.equals("NEW")) {
						return htmlParser.setAttribute(mStr, "disabled"); // 只读(不保存数据)
					}
				} else { // 编辑时只读
					if (attrValue.equals("EDIT")) {
						return htmlParser.setAttribute(mStr, "disabled"); // 只读(不保存数据)
					}
				}
			}

			// 5.验证规则
			String dataoptions = "";
			attrValue = fieldConfigMap.get("lay-verify");
			if (attrValue != null) {
				mStr = htmlParser.setAttributeValue(mStr, "lay-verify", attrValue);
			}

			// 6.设置为空时的验证提示消息
			attrValue = fieldConfigMap.get("valimsg");
			if (attrValue != null) {
				dataoptions = htmlParser.setDataOptions(dataoptions, "missingMessage", attrValue, true);
			}

			// 7.设置data-options属性
			if (Tools.isNotBlank(dataoptions)) {
				mStr = htmlParser.setAttributeValue(mStr, "lay-data", dataoptions);
			}

		}

		return mStr;

	}

	/**
	 * 解析input-radio类型的标签
	 * 
	 * @param doc
	 * @param mStr
	 * @param fieldConfigMap
	 * @return
	 */
	public String editAttrForRadio(Document doc, String fdName, String mStr, Map<String, String> fieldConfigMap) {
		HtmlParser htmlParser = (HtmlParser) BeanCtx.getBean("HtmlParser");

		// 设置单选的选中状态<input type="radio" value="1" checked >yes
		if (doc.hasItem(fdName)) { // 如果字段不存在则直不设置值
			String value = htmlParser.getAttributeValue(mStr, "value");
			if (doc.g(fdName).equals(value)) {
				mStr = htmlParser.setAttribute(mStr, "checked");// 选中状态
			} else {
				mStr = htmlParser.removeAttribute(mStr, "checked");// 取消选中
			}
		}
		// 默认选中 2018.03.27
		String attrValue = "";
		if (fieldConfigMap != null) {
//			attrValue = fieldConfigMap.get("checked");
//			if (!doc.hasItem(fdName) && attrValue != null) {
//				String value = htmlParser.getAttributeValue(mStr, "value");
//				if (doc.g(fdName).equals(value)) {
//					mStr = htmlParser.setAttribute(mStr, "checked");// 选中状态
//				} else {
//					mStr = htmlParser.removeAttribute(mStr, "checked");// 取消选中
//				}
//			}
			// 1.首先判断是否编辑时隐藏如果是，则首先返回不用判断其他类型了
			attrValue = fieldConfigMap.get("hiddentype");
			if (attrValue != null) {
				if (doc.isNewDoc()) {
					if (attrValue.indexOf("NEW") != -1) {
						return "";
					}
				} else if (attrValue.indexOf("EDIT") != -1) {
					return "";
				}
			}

			// 2.jsevent事件
			attrValue = fieldConfigMap.get("jsevent");
			if (attrValue != null) {
				String jsfun = fieldConfigMap.get("jsfun");
				if (jsfun != null) {
					mStr = htmlParser.setAttributeValue(mStr, attrValue, jsfun);
				}
			}

			// 3.分析流程环节字段权限 NodeFdAcl,只有在流程环节的字段权限中才有的属性，应用表单中没有此属性
			attrValue = fieldConfigMap.get("NodeFdAcl");
			// BeanCtx.out("R_S003_B026="+fdName+"="+attrValue);
			if (attrValue != null) {
				if (attrValue.equals("HIDDEN")) {
					return ""; // 隐藏
				} else if (attrValue.equals("READ") || attrValue.equals("READSAVE")) {
					return htmlParser.setAttribute(mStr, "disabled"); // 只读(不保存数据)
				} else if (attrValue.equals("EDIT")) {
					// 对于radio类型的设置为可编辑时，只需要去掉disabled即可，不用像input
					// text类型的需要恢复原有模式
					return htmlParser.removeAttribute(mStr, "disabled"); // 可编辑
				}
			}

			// 4.判断只读模式
			attrValue = fieldConfigMap.get("readtype");
			if (attrValue != null) {
				// 新建和编辑时全部只读
				if (attrValue.equals("ALL")) {
					return htmlParser.setAttribute(mStr, "disabled"); // 只读(不保存数据)
				}
				if (doc.isNewDoc()) {// 新建时只读
					if (attrValue.equals("NEW")) {
						return htmlParser.setAttribute(mStr, "disabled"); // 只读(不保存数据)
					}
				} else { // 编辑时只读
					if (attrValue.equals("EDIT")) {
						return htmlParser.setAttribute(mStr, "disabled"); // 只读(不保存数据)
					}
				}
			}

			// 5.验证规则
			String dataoptions = htmlParser.getAttributeValue(mStr, "data-options");
			attrValue = fieldConfigMap.get("validtype");
			if (attrValue != null) {
				dataoptions = htmlParser.setDataOptions(dataoptions, "validType", attrValue, true);
				mStr = htmlParser.appendAttributeValue(mStr, "class", "easyui-validatebox", " "); // 追加验证规则的class
			}

			// 6.设置为空时的验证提示消息
			attrValue = fieldConfigMap.get("valimsg");
			if (attrValue != null) {
				dataoptions = htmlParser.setDataOptions(dataoptions, "missingMessage", attrValue, true);
			}

			// 7.设置data-options属性
			if (Tools.isNotBlank(dataoptions)) {
				mStr = htmlParser.setAttributeValue(mStr, "data-options", dataoptions);
			}

		}

		return mStr;

	}

	/**
	 * 解析input-button类型的标签
	 * 
	 * @param doc
	 * @param mStr
	 * @param fieldConfigMap
	 * @return
	 */
	public String editAttrForButton(Document doc, String fdName, String mStr, Map<String, String> fieldConfigMap) {
		HtmlParser htmlParser = (HtmlParser) BeanCtx.getBean("HtmlParser");
		if (doc.hasItem(fdName)) {
			mStr = htmlParser.setAttributeValue(mStr, "value", doc.g(fdName));
		} // 设置字段的值
		if (fieldConfigMap == null) {
			return mStr;
		} // 说明没有字段属性配置

		// 1.首先判断是否编辑时隐藏如果是，则首先返回不用判断其他类型了
		String attrValue = fieldConfigMap.get("hiddentype");
		if (attrValue != null) {
			if (doc.isNewDoc()) {
				if (attrValue.indexOf("NEW") != -1) {
					return "";
				}
			} else if (attrValue.indexOf("EDIT") != -1) {
				return "";
			}
		}

		// 3.jsevent事件
		attrValue = fieldConfigMap.get("jsevent");
		if (attrValue != null) {
			String jsfun = fieldConfigMap.get("jsfun");
			if (jsfun != null) {
				mStr = htmlParser.setAttributeValue(mStr, attrValue, jsfun);
			}
		}

		return mStr;

	}

	/**
	 * 解析input-password类型的标签
	 * 
	 * @param doc
	 * @param mStr
	 * @param fieldConfigMap
	 * @return
	 */
	public String editAttrForPwdText(Document doc, String fdName, String mStr, Map<String, String> fieldConfigMap) {
		HtmlParser htmlParser = (HtmlParser) BeanCtx.getBean("HtmlParser");
		if (doc.hasItem(fdName)) {
			mStr = htmlParser.setAttributeValue(mStr, "value", doc.g(fdName));
		} // 设置字段的值
		if (fieldConfigMap == null) {
			return mStr;
		} // 说明没有字段属性配置

		// 1.首先判断是否编辑时隐藏如果是，则首先返回不用判断其他类型了
		String attrValue = fieldConfigMap.get("hiddentype");
		if (attrValue != null) {
			if (doc.isNewDoc()) {
				if (attrValue.indexOf("NEW") != -1) {
					return "";
				}
			} else if (attrValue.indexOf("EDIT") != -1) {
				return "";
			}
		}

		// 2.判断只读模式
		attrValue = fieldConfigMap.get("readtype");
		if (attrValue != null) {
			return htmlParser.parserReadType(doc, mStr, attrValue, fdName);
		}

		// 3.必填字段验证
		attrValue = fieldConfigMap.get("required");
		if (attrValue != null) {
			mStr = htmlParser.setAttributeValue(mStr, "lay-verify", "required");
			// mStr = htmlParser.appendAttributeValue(mStr,
			// "lay-verify","required", "");
		}

		// 4.验证规则 20180315修改
		attrValue = fieldConfigMap.get("lay-verify");
		if (attrValue != null) {
			mStr = htmlParser.setAttributeValue(mStr, "lay-verify", attrValue);
		}
		// 5.只有在字段没有选为只读的情况下才显示必填选项的class，否则在span标签中就出现必填的叹号
		if (mStr.indexOf(fdName + "_show") == -1 && mStr.indexOf("<input") != -1) { // 如果字段没有被设置为只读状态则需要追加
																					// class
			// 7.追加验证规则的class
			String className = htmlParser.getAttributeValue(mStr, "class");
			if (className.indexOf("layui-") == -1) {
				mStr = htmlParser.appendAttributeValue(mStr, "class", "layui-input", ""); // 追加
			}
		}

		String dataoptions = "";

		// 6.设置为空时的验证提示消息
		attrValue = fieldConfigMap.get("valimsg");
		if (attrValue != null) {
			dataoptions = htmlParser.setDataOptions(dataoptions, "missingMessage", attrValue, true);
		}

		// 7.jsevent事件
		attrValue = fieldConfigMap.get("jsevent");
		if (attrValue != null) {
			String jsfun = fieldConfigMap.get("jsfun");
			if (jsfun != null) {
				mStr = htmlParser.setAttributeValue(mStr, attrValue, jsfun);
			}
		}

		// 8.设置data-options属性
		if (Tools.isNotBlank(dataoptions)) {
			mStr = htmlParser.setAttributeValue(mStr, "data-options", dataoptions);
		}

		return mStr;

	}

}
