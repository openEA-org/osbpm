package cn.linkey.rulelib.S001;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

import cn.linkey.dao.Rdb;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * 获得数据库表的所有字段Json
 * 
 * @author Administrator 编号:R_S001_B010
 */
public class R_S001_B010 implements LinkeyRule {
	@Override
	public String run(HashMap<String, Object> params) throws Exception {
		BeanCtx.getResponse().setContentType("text/json;charset=utf-8");
		String sqlTableName = BeanCtx.g("WF_TableName");
		String dataSourceid = BeanCtx.g("DataSourceid");
		if (Tools.isBlank(sqlTableName) || Rdb.isExistTable(dataSourceid, sqlTableName) == false) {
			BeanCtx.p("[]");
			return "";
		}
		StringBuilder json = new StringBuilder();
		json.append("[");
		Set<String> fdList;
		if (Tools.isBlank(dataSourceid) || dataSourceid.equals("default")) {
			fdList = Rdb.getTableColumnName(sqlTableName).keySet();
		} else {
			Connection conn = Rdb.getNewConnection(dataSourceid);
			fdList = Rdb.getTableColumnName(conn, sqlTableName).keySet();
			Rdb.close(conn);
		}

		//获得所有字段配置信息
		HashMap<String, HashMap<String, String>> fdmap = getTableConfig(sqlTableName);

		//BeanCtx.out("fdList="+fdList);
		if (fdList != null && fdList.size() > 0) {
			TreeSet<String> treeSet = new TreeSet(fdList);
			treeSet.comparator();

			// 获得所有字段列表
			int i = 0;
			for (String fdName : treeSet) {
				String remark = "", fdType = "", fdKey = "", fdVal = "", fdNull = "", fdLen = "";
				HashMap<String, String> fdItemMap = fdmap.get(fdName);
				if (fdItemMap != null) {
					remark = fdItemMap.get("FdRemark");
					remark = Tools.isBlank(remark) ? "" : remark;

					fdType = fdItemMap.get("FdType");
					fdType = Tools.isBlank(fdType) ? "" : fdType;

					fdKey = fdItemMap.get("FdKey");
					fdKey = Tools.isBlank(fdKey) ? "N" : fdKey;

					fdVal = fdItemMap.get("FdVal");
					fdVal = Tools.isBlank(fdVal) ? "" : fdVal;

					fdNull = fdItemMap.get("FdNull");
					fdNull = Tools.isBlank(fdNull) ? "N" : fdNull;

					fdLen = fdItemMap.get("FdLen");
					fdLen = Tools.isBlank(fdLen) ? "" : fdLen;
				}
				//String itemStr = "{\"ColumnName\":\"" + fdName + "\",\"Remark\":\"" + remark + "\"}";

				String itemStr = "{\"ColumnName\":\"" + fdName + "\",\"Remark\":\"" + remark + "\",\"FdType\":\""
						+ fdType + "\",\"FdKey\":\"" + fdKey + "\",\"FdVal\":\"" + fdVal + "\",\"FdNull\":\"" + fdNull
						+ "\",\"FdLen\":\"" + fdLen + "\"}";

				if (i == 1) {
					json.append(",");
				} else {
					i = 1;
				}
				json.append(itemStr);
			}
		}
		json.append("]");
		BeanCtx.print(json.toString());

		return "";
	}

	/**
	 * 获得数据库表的配置信息
	 * 
	 * @param tableName
	 */
	public HashMap<String, HashMap<String, String>> getTableConfig(String tableName) {
		String sql = "select FieldConfig from BPM_TableConfig where TableName='" + tableName + "'";
		String fieldConfig = Rdb.getValueTopOneBySql(sql);
		//		BeanCtx.out("fieldConfig="+fieldConfig);
		HashMap<String, HashMap<String, String>> fdmap = new HashMap<String, HashMap<String, String>>();
		//获得所有字段的配置集合
		int spos = fieldConfig.indexOf("[");
		if (spos != -1) {
			fieldConfig = fieldConfig.substring(spos, fieldConfig.lastIndexOf("]") + 1);
			com.alibaba.fastjson.JSONArray jsonArr = com.alibaba.fastjson.JSON.parseArray(fieldConfig);
			for (int i = 0; i < jsonArr.size(); i++) {
				com.alibaba.fastjson.JSONObject rowItem = (com.alibaba.fastjson.JSONObject) jsonArr.get(i);

				/*String fdName = rowItem.getString("FdName"); // 字段名称
				String fdType = rowItem.getString("FdType"); // 字段类型
				String fdRemark = rowItem.getString("FdRemark");//备注
				HashMap<String, String> fdItemMap = new HashMap<String, String>();
				fdItemMap.put("FdName", fdName);
				fdItemMap.put("FdRemark", fdRemark);
				fdItemMap.put("FdType", fdType);
				fdmap.put(fdName, fdItemMap);*/

				// 20181226 修复数据库表视图载入字段配置不全的问题-------------------------------
				HashMap<String, String> fdItemMap = new HashMap<String, String>();
				for (String key : rowItem.keySet()) {
					String fdName = rowItem.getString("FdName"); // 字段名称
					fdItemMap.put(key, rowItem.getString(key));
					fdmap.put(fdName, fdItemMap);
				}
				// 20181226 END --------------------------------------------------------
			}
		}
		return fdmap;
	}
}
