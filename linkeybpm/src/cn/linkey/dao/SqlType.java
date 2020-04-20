package cn.linkey.dao;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Sql语句选择类，每条sql语句在list中的数据库类型按顺序分别是mssql、mysql、oracle、db2
 * @author yanjun
 */
public class SqlType {
	
	private static Map<String, List<String>> allTemplateSql = new HashMap<String, List<String>>();
	
	static {
		//模板
		/*allTemplateSql.put("_SQL1", Arrays.asList(new String[]{
				"",
				"",
				"",
				""
		}));*/
		
		// 流程效率前10名v2
		/* Mysql数据库格式化语句：
				SELECT WF_ProcessName, CAST(SUM(numb) AS DECIMAL(10, 4)) / (
						SELECT COUNT(WF_ProcessName)
						FROM BPM_ArchivedData
						WHERE wf_processname = f.wf_processname
						GROUP BY WF_ProcessName
					) AS tmp
				FROM (
					SELECT WF_ProcessName
						, TIMESTAMPDIFF(HOUR, STR_TO_DATE(wf_doccreated, '%Y-%m-%d %H:%i:%s'), STR_TO_DATE(wf_endtime, '%Y-%m-%d %H:%i:%s')) AS numb
					FROM BPM_ArchivedData
				) f
				GROUP BY WF_ProcessName
				ORDER BY tmp ASC
				LIMIT 0, 10
		*/
		allTemplateSql.put("R_S016_E012_SQL1", Arrays.asList(new String[]{
				"select top 10 WF_ProcessName,cast(sum(numb) as numeric(10,4))/(select COUNT(WF_ProcessName)  from BPM_ArchivedData where wf_processname=f.wf_processname group by WF_ProcessName) as tmp from (SELECT WF_ProcessName,datediff(hour,wf_doccreated,wf_endtime) as numb FROM BPM_ArchivedData ) as f group by WF_ProcessName order by tmp asc",
				"SELECT WF_ProcessName,CAST(SUM(numb) AS DECIMAL(10,4))/(SELECT COUNT(WF_ProcessName) FROM BPM_ArchivedData WHERE wf_processname=f.wf_processname GROUP BY WF_ProcessName) AS tmp FROM (SELECT WF_ProcessName,TIMESTAMPDIFF(HOUR,STR_TO_DATE(wf_doccreated,'%Y-%m-%d %H:%i:%s'),STR_TO_DATE(wf_endtime,'%Y-%m-%d %H:%i:%s')) AS numb FROM BPM_ArchivedData ) AS f GROUP BY WF_ProcessName ORDER BY tmp ASC LIMIT 0,10",
				"SELECT WF_ProcessName, CAST(SUM(numb) AS NUMBER(10, 4)) / (SELECT COUNT(WF_ProcessName) FROM BPM_ArchivedData WHERE wf_processname = f.wf_processname GROUP BY WF_ProcessName) AS tmp FROM (SELECT WF_ProcessName, ROUND(TO_NUMBER(TO_DATE(wf_endtime, 'yyyy-MM-dd HH24:mi:ss') - TO_DATE(wf_doccreated, 'yyyy-MM-dd HH24:mi:ss')) * 24) AS numb FROM BPM_ArchivedData) f WHERE ROWNUM <= 10 GROUP BY WF_ProcessName ORDER BY tmp ASC",
				""
		}));
		
		// 流程效率后10名
		/* Mysql数据库格式化语句：
				SELECT WF_ProcessName, CAST(SUM(numb) AS DECIMAL(10, 4)) / (
						SELECT COUNT(WF_ProcessName)
						FROM BPM_ArchivedData
						WHERE wf_processname = f.wf_processname
						GROUP BY WF_ProcessName
					) AS tmp
				FROM (
					SELECT WF_ProcessName
						, TIMESTAMPDIFF(HOUR, STR_TO_DATE(wf_doccreated,'%Y-%m-%d %H:%i:%s'), STR_TO_DATE(wf_endtime,'%Y-%m-%d %H:%i:%s')) AS numb
					FROM BPM_ArchivedData
				) f
				GROUP BY WF_ProcessName
				ORDER BY tmp DESC
				LIMIT 0, 10
		*/
		allTemplateSql.put("R_S016_E013_SQL1", Arrays.asList(new String[]{
				"select top 10 WF_ProcessName,cast(sum(numb) as numeric(10,4))/(select COUNT(WF_ProcessName)  from BPM_ArchivedData where wf_processname=f.wf_processname group by WF_ProcessName) as tmp from (SELECT WF_ProcessName,datediff(hour,wf_doccreated,wf_endtime) as numb FROM BPM_ArchivedData ) as f group by WF_ProcessName order by tmp desc",
				"SELECT WF_ProcessName, CAST(SUM(numb) AS DECIMAL(10, 4)) / (SELECT COUNT(WF_ProcessName) FROM BPM_ArchivedData WHERE wf_processname = f.wf_processname GROUP BY WF_ProcessName) AS tmp FROM (SELECT WF_ProcessName, TIMESTAMPDIFF(HOUR, STR_TO_DATE(wf_doccreated,'%Y-%m-%d %H:%i:%s'), STR_TO_DATE(wf_endtime,'%Y-%m-%d %H:%i:%s')) AS numb FROM BPM_ArchivedData) f GROUP BY WF_ProcessName ORDER BY tmp DESC LIMIT 0, 10",
				"SELECT WF_ProcessName, CAST(SUM(numb) AS NUMBER(10, 4)) / (SELECT COUNT(WF_ProcessName) FROM BPM_ArchivedData WHERE wf_processname = f.wf_processname GROUP BY WF_ProcessName) AS tmp FROM (SELECT WF_ProcessName, ROUND(TO_NUMBER(TO_DATE(wf_endtime, 'yyyy-MM-dd HH24:mi:ss') - TO_DATE(wf_doccreated, 'yyyy-MM-dd HH24:mi:ss')) * 24) AS numb FROM BPM_ArchivedData) f WHERE ROWNUM<=10 GROUP BY WF_ProcessName ORDER BY tmp DESC",
				""
		}));
		
		// 用户效率前10名
		/* Mysql数据库格式化语句：
				SELECT userid, CAST(SUM(numb) AS DECIMAL(10, 4)) / (
						SELECT COUNT(Userid)
						FROM BPM_InsUserList
						WHERE Userid = f.userid
							AND STATUS = 'end'
					) AS tmp
				FROM (
					SELECT userid, CONVERT(totaltime, SIGNED) AS numb
					FROM BPM_InsUserList f
					WHERE STATUS = 'end'
						AND Userid IS NOT NULL
				) f
				GROUP BY userid
				ORDER BY tmp ASC
				LIMIT 0, 10
		*/
		allTemplateSql.put("R_S016_E014_SQL1", Arrays.asList(new String[]{
				"select top 10 userid,cast(sum(numb) as numeric(10,4))/(select COUNT(Userid) from BPM_InsUserList where Userid=f.userid and Status='end')as tmp from (SELECT userid,convert(int,totaltime) as numb FROM BPM_InsUserList as f where status='end' and Userid is not null) as f group by userid  order by tmp asc",
				"SELECT userid, CAST(SUM(numb) AS DECIMAL(10, 4)) / (SELECT COUNT(Userid) FROM BPM_InsUserList WHERE Userid = f.userid AND STATUS = 'end') AS tmp FROM (SELECT userid, CONVERT(totaltime, SIGNED) AS numb FROM BPM_InsUserList f WHERE STATUS = 'end' AND Userid IS NOT NULL) f GROUP BY userid ORDER BY tmp ASC LIMIT 0, 10",
				"SELECT userid, CAST(SUM(numb) AS NUMBER(10, 4)) / (SELECT COUNT(Userid) FROM BPM_InsUserList WHERE Userid = f.userid AND STATUS = 'end') AS tmp FROM (SELECT userid, TO_NUMBER(totaltime) AS numb FROM BPM_InsUserList f WHERE STATUS = 'end' AND Userid IS NOT NULL) f WHERE ROWNUM<=10 GROUP BY userid ORDER BY tmp ASC",
				""
		}));
		
		// 用户效率后10名
		/* Mysql数据库格式化语句：
				SELECT userid, CAST(SUM(numb) AS DECIMAL(10, 4)) / (
						SELECT COUNT(Userid)
						FROM BPM_InsUserList
						WHERE Userid = f.userid
							AND STATUS = 'end'
					) AS tmp
				FROM (
					SELECT userid, CONVERT(totaltime, SIGNED) AS numb
					FROM BPM_InsUserList f
					WHERE STATUS = 'end'
						AND Userid IS NOT NULL
				) f
				GROUP BY userid
				ORDER BY tmp DESC
				LIMIT 0, 10
		*/
		allTemplateSql.put("R_S016_E015_SQL1", Arrays.asList(new String[]{
				"select top 10 userid,cast(sum(numb) as numeric(10,4))/(select COUNT(Userid) from BPM_InsUserList where Userid=f.userid and Status='end')as tmp from (SELECT userid,convert(int,totaltime) as numb FROM BPM_InsUserList as f where status='end' and Userid is not null) as f group by userid  order by tmp desc",
				"SELECT userid, CAST(SUM(numb) AS DECIMAL(10, 4)) / (SELECT COUNT(Userid) FROM BPM_InsUserList WHERE Userid = f.userid AND STATUS = 'end') AS tmp FROM (SELECT userid, CONVERT(totaltime, SIGNED) AS numb FROM BPM_InsUserList f WHERE STATUS = 'end' AND Userid IS NOT NULL) f GROUP BY userid ORDER BY tmp DESC LIMIT 0, 10",
				"SELECT userid, CAST(SUM(numb) AS NUMBER(10, 4)) / (SELECT COUNT(Userid) FROM BPM_InsUserList WHERE Userid = f.userid AND STATUS = 'end') AS tmp FROM (SELECT userid, TO_NUMBER(totaltime) AS numb FROM BPM_InsUserList f WHERE STATUS = 'end' AND Userid IS NOT NULL) f WHERE ROWNUM<=10 GROUP BY userid ORDER BY tmp DESC",
				""
		}));
		
		// 运行周期前10名
		/* Mysql数据库格式化语句：
				SELECT WF_processid, CAST(AVG(numb) AS DECIMAL(10, 4)) AS tmp
				FROM (
					SELECT wf_processid, CONVERT(WF_TotalTime, SIGNED) AS numb
					FROM BPM_ArchivedData
					WHERE wf_totaltime IS NOT NULL
				) f
				GROUP BY Wf_processid
				ORDER BY tmp ASC
				LIMIT 0, 10
		*/
		allTemplateSql.put("R_S016_E016_SQL1", Arrays.asList(new String[]{
				"select top 10 WF_processid,cast(avg(numb) as numeric(10,4)) as tmp from (SELECT wf_processid,convert(int,WF_TotalTime) as numb FROM BPM_ArchivedData where wf_totaltime is not null) as f group by Wf_processid  order by tmp asc",
				"SELECT WF_processid, CAST(AVG(numb) AS DECIMAL(10, 4)) AS tmp FROM (SELECT wf_processid, CONVERT(WF_TotalTime, SIGNED) AS numb FROM BPM_ArchivedData WHERE wf_totaltime IS NOT NULL) f GROUP BY Wf_processid ORDER BY tmp ASC LIMIT 0, 10",
				"SELECT WF_processid, CAST(AVG(numb) AS NUMBER(10, 4)) AS tmp FROM (SELECT wf_processid, TO_NUMBER(WF_TotalTime) AS numb FROM BPM_ArchivedData WHERE wf_totaltime IS NOT NULL) f WHERE ROWNUM<=10 GROUP BY Wf_processid ORDER BY tmp ASC",
				""
		}));
		
		// 运行周期后10名
		/* Mysql数据库格式化语句：
				SELECT WF_processid, CAST(AVG(numb) AS DECIMAL(10, 4)) AS tmp
				FROM (
					SELECT wf_processid, CONVERT(WF_TotalTime, SIGNED) AS numb
					FROM BPM_ArchivedData
					WHERE wf_totaltime IS NOT NULL
				) f
				GROUP BY Wf_processid
				ORDER BY tmp DESC
				LIMIT 0, 10
		*/
		allTemplateSql.put("R_S016_E017_SQL1", Arrays.asList(new String[]{
				"select top 10 WF_processid,cast(avg(numb) as numeric(10,4)) as tmp from (SELECT wf_processid,convert(int,WF_TotalTime) as numb FROM BPM_ArchivedData where wf_totaltime is not null) as f group by Wf_processid  order by tmp desc",
				"SELECT WF_processid, CAST(AVG(numb) AS DECIMAL(10, 4)) AS tmp FROM (SELECT wf_processid, CONVERT(WF_TotalTime, SIGNED) AS numb FROM BPM_ArchivedData WHERE wf_totaltime IS NOT NULL) f GROUP BY Wf_processid ORDER BY tmp DESC LIMIT 0, 10",
				"SELECT WF_processid, CAST(AVG(numb) AS NUMBER(10, 4)) AS tmp FROM (SELECT wf_processid, TO_NUMBER(WF_TotalTime) AS numb FROM BPM_ArchivedData WHERE wf_totaltime IS NOT NULL) f WHERE ROWNUM<=10 GROUP BY Wf_processid ORDER BY tmp DESC",
				""
		}));
		
		// 执行超时时长用户前10名
		/* Mysql数据库格式化语句：
				SELECT userid, CAST(AVG(numb) AS DECIMAL(10, 0)) AS tmp
				FROM (
					SELECT userid, CONVERT(OverTimeNum, SIGNED) AS numb
					FROM BPM_InsRemarkList
					WHERE OverTimeNum IS NOT NULL
				) f
				GROUP BY userid
				ORDER BY tmp DESC
				LIMIT 0, 10
		*/
		allTemplateSql.put("R_S016_E020_SQL1", Arrays.asList(new String[]{
				"select top 10 userid,cast(avg(numb) as numeric(10,0)) as tmp from (SELECT userid,convert(int,OverTimeNum) as numb FROM BPM_InsRemarkList where OverTimeNum is not null) as f group by userid  order by tmp desc",
				"SELECT userid, CAST(AVG(numb) AS DECIMAL(10, 0)) AS tmp FROM (SELECT userid, CONVERT(OverTimeNum, SIGNED) AS numb FROM BPM_InsRemarkList WHERE OverTimeNum IS NOT NULL) f GROUP BY userid ORDER BY tmp DESC LIMIT 0, 10",
				"SELECT userid, CAST(AVG(numb) AS NUMBER(10, 0)) AS tmp FROM (SELECT userid, TO_NUMBER(OverTimeNum) AS numb FROM BPM_InsRemarkList WHERE OverTimeNum IS NOT NULL) f WHERE ROWNUM<=10 GROUP BY userid ORDER BY tmp DESC",
				""
		}));
		
		// 执行超时次数最多前10名
		allTemplateSql.put("R_S016_E021_SQL1", Arrays.asList(new String[]{
				"select top 10 userid,COUNT(*)as count  from   BPM_InsRemarkList where OverTimeFlag<>0 group by userid order by count desc ",
				"SELECT userid,COUNT(*) AS COUNT FROM BPM_InsRemarkList WHERE OverTimeFlag<>0 GROUP BY userid ORDER BY COUNT DESC LIMIT 0, 10",
				"SELECT userid,COUNT(*) AS COUNT FROM BPM_InsRemarkList WHERE OverTimeFlag<>0 AND ROWNUM<=10 GROUP BY userid ORDER BY COUNT DESC",
				""
		}));
		
		// 及时响应用户前10名
		allTemplateSql.put("R_S016_E023_SQL1", Arrays.asList(new String[]{
				"SELECT top 10 userid,count(*) as num,avg(datediff(minute,convert(datetime,starttime),convert(datetime,FirstReadTime))) as time FROM BPM_InsRemarkList where FirstReadTime<>'' group by userid order by time asc ",
				"SELECT userid,COUNT(*) AS num,AVG(TIMESTAMPDIFF(MINUTE,STR_TO_DATE(starttime,'%Y-%m-%d %H:%i:%s'),STR_TO_DATE(FirstReadTime,'%Y-%m-%d %H:%i:%s'))) AS TIME FROM BPM_InsRemarkList WHERE FirstReadTime<>'' GROUP BY userid ORDER BY TIME ASC LIMIT 0, 10",
				"SELECT userid,COUNT(*) AS num,AVG(ROUND(TO_NUMBER(TO_DATE(FirstReadTime, 'yyyy-MM-dd HH24:mi:ss') - TO_DATE(starttime, 'yyyy-MM-dd HH24:mi:ss')) * 24 * 60)) AS TIME FROM BPM_InsRemarkList WHERE FirstReadTime<>'' AND ROWNUM<=10 GROUP BY userid ORDER BY TIME ASC",
				""
		}));
		
		// 及时响应用户后10名
		allTemplateSql.put("R_S016_E024_SQL1", Arrays.asList(new String[]{
				"SELECT top 10 userid,count(*) as num,avg(datediff(minute,convert(datetime,starttime),convert(datetime,FirstReadTime))) as time FROM BPM_InsRemarkList where FirstReadTime<>'' group by userid order by time desc ",
				"SELECT userid,COUNT(*) AS num,AVG(TIMESTAMPDIFF(MINUTE,STR_TO_DATE(starttime,'%Y-%m-%d %H:%i:%s'),STR_TO_DATE(FirstReadTime,'%Y-%m-%d %H:%i:%s'))) AS TIME FROM BPM_InsRemarkList WHERE FirstReadTime<>'' GROUP BY userid ORDER BY TIME DESC LIMIT 0, 10",
				"SELECT userid,COUNT(*) AS num,AVG(ROUND(TO_NUMBER(TO_DATE(FirstReadTime, 'yyyy-MM-dd HH24:mi:ss') - TO_DATE(starttime, 'yyyy-MM-dd HH24:mi:ss')) * 24 * 60)) AS TIME FROM BPM_InsRemarkList WHERE FirstReadTime<>'' AND ROWNUM<=10 GROUP BY userid ORDER BY TIME DESC",
				""
		}));
		
		// 流程及环节平均耗时
		allTemplateSql.put("R_S016_E026_SQL1", Arrays.asList(new String[]{
				"select avg(convert(int,diftime))as time,nodename,COUNT(nodename)as num from BPM_InsRemarkList where Processid='{1}' group by NodeName",
				"SELECT AVG(CONVERT(diftime, SIGNED)) AS TIME,nodename,COUNT(nodename) AS num FROM BPM_InsRemarkList WHERE Processid='{1}' GROUP BY NodeName",
				"SELECT AVG(TO_NUMBER(diftime)) AS TIME,nodename,COUNT(nodename) AS num FROM BPM_InsRemarkList WHERE Processid='{1}' GROUP BY NodeName",
				""
		}));
		
		// 流程超时概率统计
		allTemplateSql.put("R_S016_E027_SQL1", Arrays.asList(new String[]{
				"select wf_ProcessName,DATEDIFF(HOUR,CONVERT(datetime,WF_LastModified),getdate()) as difftime from BPM_ArchivedData group by WF_ProcessName,WF_LastModified",
				"SELECT wf_ProcessName,TIMESTAMPDIFF(HOUR,STR_TO_DATE(WF_LastModified,'%Y-%m-%d %H:%i:%s'),NOW()) AS difftime FROM BPM_ArchivedData GROUP BY WF_ProcessName,WF_LastModified",
				"SELECT wf_ProcessName,ROUND(TO_NUMBER(TRUNC(SYSDATE) - TO_DATE(WF_LastModified, 'yyyy-MM-dd HH24:mi:ss')) * 24) AS difftime FROM BPM_ArchivedData GROUP BY WF_ProcessName,WF_LastModified",
				""
		}));
		allTemplateSql.put("R_S016_E027_SQL2", Arrays.asList(new String[]{
				"SELECT wf_ProcessName,DATEDIFF(HOUR,CONVERT(DATETIME,WF_LastModified),getdate()) AS difftime FROM BPM_ArchivedData WHERE wf_processname='{1}' GROUP BY WF_ProcessName,WF_LastModified",
				"SELECT wf_ProcessName,TIMESTAMPDIFF(HOUR,STR_TO_DATE(WF_LastModified,'%Y-%m-%d %H:%i:%s'),NOW()) AS difftime FROM BPM_ArchivedData WHERE wf_processname='{1}' GROUP BY WF_ProcessName,WF_LastModified",
				"SELECT wf_ProcessName,ROUND(TO_NUMBER(TRUNC(SYSDATE) - TO_DATE(WF_LastModified, 'yyyy-MM-dd HH24:mi:ss')) * 24) AS difftime FROM BPM_ArchivedData WHERE wf_processname='{1}' GROUP BY WF_ProcessName,WF_LastModified",
				""
		}));
		
		// 流程及时概率按流程
		allTemplateSql.put("R_S016_E029_SQL1", Arrays.asList(new String[]{
				"select wf_ProcessName,DATEDIFF(HOUR,CONVERT(datetime,WF_LastModified),getdate()) as difftime from BPM_ArchivedData group by WF_ProcessName,WF_LastModified",
				"SELECT wf_ProcessName,TIMESTAMPDIFF(HOUR,STR_TO_DATE(WF_LastModified,'%Y-%m-%d %H:%i:%s'),NOW()) AS difftime FROM BPM_ArchivedData GROUP BY WF_ProcessName,WF_LastModified",
				"SELECT wf_ProcessName,ROUND(TO_NUMBER(TRUNC(SYSDATE) - TO_DATE(WF_LastModified, 'yyyy-MM-dd HH24:mi:ss')) * 24) AS difftime FROM BPM_ArchivedData GROUP BY WF_ProcessName,WF_LastModified",
				""
		}));
		allTemplateSql.put("R_S016_E029_SQL2", Arrays.asList(new String[]{
				"select wf_ProcessName,DATEDIFF(HOUR,CONVERT(datetime,WF_LastModified),getdate()) as difftime from BPM_ArchivedData where wf_processname='{1}' group by WF_ProcessName,WF_LastModified",
				"SELECT wf_ProcessName,TIMESTAMPDIFF(HOUR,STR_TO_DATE(WF_LastModified,'%Y-%m-%d %H:%i:%s'),NOW()) AS difftime FROM BPM_ArchivedData WHERE wf_processname='{1}' GROUP BY WF_ProcessName,WF_LastModified",
				"SELECT wf_ProcessName,ROUND(TO_NUMBER(TRUNC(SYSDATE) - TO_DATE(WF_LastModified, 'yyyy-MM-dd HH24:mi:ss')) * 24) AS difftime FROM BPM_ArchivedData WHERE wf_processname='{1}' GROUP BY WF_ProcessName,WF_LastModified",
				""
		}));
		
		// 待办最长延时前10用户
		allTemplateSql.put("R_S016_E031_SQL1", Arrays.asList(new String[]{
				"SELECT Userid,CONVERT(INT,overdatenum) AS TIME FROM BPM_InsUserList WHERE STATUS='current' AND OverDateNum<>0 ORDER BY TIME DESC",
				"SELECT Userid,CONVERT(overdatenum,SIGNED) AS TIME FROM BPM_InsUserList WHERE STATUS='current' AND OverDateNum<>0 ORDER BY TIME DESC",
				"SELECT Userid,TO_NUMBER(overdatenum) AS TIME FROM BPM_InsUserList WHERE STATUS='current' AND OverDateNum<>0 ORDER BY TIME DESC",
				""
		}));
		
		// 待办平均延时前10用户
		allTemplateSql.put("R_S016_E032_SQL1", Arrays.asList(new String[]{
				"SELECT DISTINCT Userid,AVG(CONVERT(INT,overdatenum)) AS TIME FROM BPM_InsUserList WHERE STATUS='current' AND OverDateNum<>0 GROUP BY userid ORDER BY TIME DESC",
				"SELECT DISTINCT Userid,AVG(CONVERT(overdatenum,SIGNED)) AS TIME FROM BPM_InsUserList WHERE STATUS='current' AND OverDateNum<>0 GROUP BY userid ORDER BY TIME DESC",
				"SELECT DISTINCT Userid,AVG(TO_NUMBER(overdatenum)) AS TIME FROM BPM_InsUserList WHERE STATUS='current' AND OverDateNum<>0 GROUP BY userid ORDER BY TIME DESC",
				""
		}));
		
		// 流程运行天数分布状态
		allTemplateSql.put("R_S016_E034_SQL1", Arrays.asList(new String[]{
				"SELECT DISTINCT wf_processid,wf_doccreated,WF_LastModified,DATEDIFF(DAY,CONVERT(DATETIME,wf_doccreated),getdate()) AS num FROM BPM_MainData ORDER BY num DESC",
				"SELECT DISTINCT wf_processid,wf_doccreated,WF_LastModified,TIMESTAMPDIFF(DAY,STR_TO_DATE(wf_doccreated,'%Y-%m-%d %H:%i:%s'),NOW()) AS num FROM BPM_MainData ORDER BY num DESC",
				"SELECT DISTINCT wf_processid,wf_doccreated,WF_LastModified,ROUND(TO_NUMBER(TRUNC(SYSDATE) - TO_DATE(wf_doccreated, 'yyyy-MM-dd HH24:mi:ss'))) AS num FROM BPM_MainData ORDER BY num DESC",
				""
				
				// "SELECT DISTINCT wf_processid,wf_doccreated,WF_LastModified,DATEDIFF(DAY,CONVERT(DATETIME,wf_doccreated),CONVERT(DATETIME,WF_LastModified)) AS num FROM BPM_MainData ORDER BY num DESC",
				// "SELECT DISTINCT wf_processid,wf_doccreated,WF_LastModified,TIMESTAMPDIFF(DAY,STR_TO_DATE(wf_doccreated,'%Y-%m-%d %H:%i:%s'),STR_TO_DATE(WF_LastModified,'%Y-%m-%d %H:%i:%s')) AS num FROM BPM_MainData ORDER BY num DESC",
				// "SELECT DISTINCT wf_processid,wf_doccreated,WF_LastModified,ROUND(TO_NUMBER(TO_DATE(WF_LastModified, 'yyyy-MM-dd HH24:mi:ss') - TO_DATE(wf_doccreated, 'yyyy-MM-dd HH24:mi:ss'))) AS num FROM BPM_MainData ORDER BY num DESC",
		}));
		
		// 流程运行耗时统计
		allTemplateSql.put("R_S016_E036_SQL1", Arrays.asList(new String[]{
				"SELECT wf_processid,avg(datediff(hour,Convert(datetime,WF_LastModified),getdate())) as time FROM BPM_MainData group by WF_Processid  order by time desc",
				"SELECT wf_processid,AVG(TIMESTAMPDIFF(HOUR,STR_TO_DATE(WF_LastModified,'%Y-%m-%d %H:%i:%s'),NOW())) AS TIME FROM BPM_MainData GROUP BY WF_Processid ORDER BY TIME DESC",
				"SELECT wf_processid,AVG(ROUND(TO_NUMBER(TRUNC(SYSDATE) - TO_DATE(WF_LastModified, 'yyyy-MM-dd HH24:mi:ss')) * 24)) AS TIME FROM BPM_MainData GROUP BY WF_Processid ORDER BY TIME DESC",
				""
		}));
		
		// 同比流程超时趋势
		allTemplateSql.put("R_S016_E041_mainsql", Arrays.asList(new String[]{
				"select wf_processid,WF_DocCreated,DATEDIFF(HOUR,CONVERT(datetime,wf_doccreated),getdate()) as difftime from BPM_MainData where WF_DocCreated between '{1}' and '{2}'",
				"SELECT wf_processid,WF_DocCreated,TIMESTAMPDIFF(HOUR,STR_TO_DATE(wf_doccreated,'%Y-%m-%d %H:%i:%s'),NOW()) AS difftime FROM BPM_MainData WHERE WF_DocCreated BETWEEN '{1}' AND '{2}'",
				"SELECT wf_processid,WF_DocCreated,ROUND(TO_NUMBER(TRUNC(SYSDATE) - TO_DATE(wf_doccreated, 'yyyy-MM-dd HH24:mi:ss')) * 24) AS difftime FROM BPM_MainData WHERE WF_DocCreated BETWEEN '{1}' AND '{2}'",
				""
		}));
		allTemplateSql.put("R_S016_E041_archsql", Arrays.asList(new String[]{
				"select wf_processid,WF_DocCreated,DATEDIFF(HOUR,CONVERT(datetime,wf_doccreated),getdate()) as difftime from BPM_ArchivedData where WF_DocCreated between '{1}' and '{2}'",
				"SELECT wf_processid,WF_DocCreated,TIMESTAMPDIFF(HOUR,STR_TO_DATE(wf_doccreated,'%Y-%m-%d %H:%i:%s'),NOW()) AS difftime FROM BPM_ArchivedData WHERE WF_DocCreated BETWEEN '{1}' AND '{2}'",
				"SELECT wf_processid,WF_DocCreated,ROUND(TO_NUMBER(TRUNC(SYSDATE) - TO_DATE(wf_doccreated, 'yyyy-MM-dd HH24:mi:ss')) * 24) AS difftime FROM BPM_ArchivedData WHERE WF_DocCreated BETWEEN '{1}' AND '{2}'",
				""
		}));
		allTemplateSql.put("R_S016_E041_mainsql1", Arrays.asList(new String[]{
				"select wf_processid,WF_DocCreated,DATEDIFF(HOUR,CONVERT(datetime,wf_doccreated),getdate()) as difftime from BPM_MainData where WF_DocCreated between '{1}' and '{2}'",
				"SELECT wf_processid,WF_DocCreated,TIMESTAMPDIFF(HOUR,STR_TO_DATE(wf_doccreated,'%Y-%m-%d %H:%i:%s'),NOW()) AS difftime FROM BPM_MainData WHERE WF_DocCreated BETWEEN '{1}' AND '{2}'",
				"SELECT wf_processid,WF_DocCreated,ROUND(TO_NUMBER(TRUNC(SYSDATE) - TO_DATE(wf_doccreated, 'yyyy-MM-dd HH24:mi:ss')) * 24) AS difftime FROM BPM_MainData WHERE WF_DocCreated BETWEEN '{1}' AND '{2}'",
				""
		}));
		allTemplateSql.put("R_S016_E041_archsql1", Arrays.asList(new String[]{
				"select wf_processid,WF_DocCreated,DATEDIFF(HOUR,CONVERT(datetime,wf_doccreated),getdate()) as difftime from BPM_ArchivedData where WF_DocCreated between '{1}' and '{2}'",
				"SELECT wf_processid,WF_DocCreated,TIMESTAMPDIFF(HOUR,STR_TO_DATE(wf_doccreated,'%Y-%m-%d %H:%i:%s'),NOW()) AS difftime FROM BPM_ArchivedData WHERE WF_DocCreated BETWEEN '{1}' AND '{2}'",
				"SELECT wf_processid,WF_DocCreated,ROUND(TO_NUMBER(TRUNC(SYSDATE) - TO_DATE(wf_doccreated, 'yyyy-MM-dd HH24:mi:ss')) * 24) AS difftime FROM BPM_ArchivedData WHERE WF_DocCreated BETWEEN '{1}' AND '{2}'",
				""
		}));
		
		// 环比流程超时趋势
		allTemplateSql.put("R_S016_E042_mainsql", Arrays.asList(new String[]{
				"select wf_processid,WF_DocCreated,DATEDIFF(HOUR,CONVERT(datetime,wf_doccreated),getdate()) as difftime from BPM_MainData where WF_DocCreated between '{1}' and '{2}'",
				"SELECT wf_processid,WF_DocCreated,TIMESTAMPDIFF(HOUR,STR_TO_DATE(wf_doccreated,'%Y-%m-%d %H:%i:%s'),NOW()) AS difftime FROM BPM_MainData WHERE WF_DocCreated BETWEEN '{1}' AND '{2}'",
				"SELECT wf_processid,WF_DocCreated,ROUND(TO_NUMBER(TRUNC(SYSDATE) - TO_DATE(wf_doccreated, 'yyyy-MM-dd HH24:mi:ss')) * 24) AS difftime FROM BPM_MainData WHERE WF_DocCreated BETWEEN '{1}' AND '{2}'",
				""
		}));
		allTemplateSql.put("R_S016_E042_archsql", Arrays.asList(new String[]{
				"select wf_processid,WF_DocCreated,DATEDIFF(HOUR,CONVERT(datetime,wf_doccreated),getdate()) as difftime from BPM_ArchivedData where WF_DocCreated between '{1}' and '{2}'",
				"SELECT wf_processid,WF_DocCreated,TIMESTAMPDIFF(HOUR,STR_TO_DATE(wf_doccreated,'%Y-%m-%d %H:%i:%s'),NOW()) AS difftime FROM BPM_ArchivedData WHERE WF_DocCreated BETWEEN '{1}' AND '{2}'",
				"SELECT wf_processid,WF_DocCreated,ROUND(TO_NUMBER(TRUNC(SYSDATE) - TO_DATE(wf_doccreated, 'yyyy-MM-dd HH24:mi:ss')) * 24) AS difftime FROM BPM_ArchivedData WHERE WF_DocCreated BETWEEN '{1}' AND '{2}'",
				""
		}));
		allTemplateSql.put("R_S016_E042_mainsql1", Arrays.asList(new String[]{
				"select wf_processid,WF_DocCreated,DATEDIFF(HOUR,CONVERT(datetime,wf_doccreated),getdate()) as difftime from BPM_MainData where WF_DocCreated between '{1}' and '{2}'",
				"SELECT wf_processid,WF_DocCreated,TIMESTAMPDIFF(HOUR,STR_TO_DATE(wf_doccreated,'%Y-%m-%d %H:%i:%s'),NOW()) AS difftime FROM BPM_MainData WHERE WF_DocCreated BETWEEN '{1}' AND '{2}'",
				"SELECT wf_processid,WF_DocCreated,ROUND(TO_NUMBER(TRUNC(SYSDATE) - TO_DATE(wf_doccreated, 'yyyy-MM-dd HH24:mi:ss')) * 24) AS difftime FROM BPM_MainData WHERE WF_DocCreated BETWEEN '{1}' AND '{2}'",
				""
		}));
		allTemplateSql.put("R_S016_E042_archsql1", Arrays.asList(new String[]{
				"select wf_processid,WF_DocCreated,DATEDIFF(HOUR,CONVERT(datetime,wf_doccreated),getdate()) as difftime from BPM_ArchivedData where WF_DocCreated between '{1}' and '{2}'",
				"SELECT wf_processid,WF_DocCreated,TIMESTAMPDIFF(HOUR,STR_TO_DATE(wf_doccreated,'%Y-%m-%d %H:%i:%s'),NOW()) AS difftime FROM BPM_ArchivedData WHERE WF_DocCreated BETWEEN '{1}' AND '{2}'",
				"SELECT wf_processid, WF_DocCreated, ROUND(TO_NUMBER(TRUNC(SYSDATE) - TO_DATE(wf_doccreated, 'yyyy-MM-dd HH24:mi:ss')) * 24) AS difftime FROM BPM_ArchivedData WHERE WF_DocCreated BETWEEN '{1}' AND '{2}'",
				""
		}));
		
		// 环比流程效率趋势
		allTemplateSql.put("R_S016_E045_timesql", Arrays.asList(new String[]{
				"select wf_processname,SUM(DATEDIFF(hour,CONVERT(datetime,wf_doccreated),convert(datetime,wf_lastmodified))) as time from BPM_ArchivedData where wf_processid='{1}' and WF_DocCreated between '{2}' and '{3}' group by wf_processname",
				"select wf_processname,SUM(TIMESTAMPDIFF(HOUR,STR_TO_DATE(wf_doccreated,'%Y-%m-%d %H:%i:%s'),STR_TO_DATE(wf_lastmodified,'%Y-%m-%d %H:%i:%s'))) as time from BPM_ArchivedData where wf_processid='{1}' and WF_DocCreated between '{2}' and '{3}' group by wf_processname",
				"select wf_processname,SUM(ROUND(TO_NUMBER(TO_DATE(wf_lastmodified, 'yyyy-MM-dd HH24:mi:ss') - TO_DATE(wf_doccreated, 'yyyy-MM-dd HH24:mi:ss')) * 24)) as time from BPM_ArchivedData where wf_processid='{1}' and WF_DocCreated between '{2}' and '{3}' group by wf_processname",
				""
		}));
		allTemplateSql.put("R_S016_E045_numsql", Arrays.asList(new String[]{
				"select COUNT(*) as num from BPM_ArchivedData where WF_Processid='{1}' and WF_DocCreated between '{2}' and '{3}'",
				"select COUNT(*) as num from BPM_ArchivedData where WF_Processid='{1}' and WF_DocCreated between '{2}' and '{3}'",
				"select COUNT(*) as num from BPM_ArchivedData where WF_Processid='{1}' and WF_DocCreated between '{2}' and '{3}'",
				""
		}));
		allTemplateSql.put("R_S016_E045_timesql1", Arrays.asList(new String[]{
				"select wf_processname,SUM(DATEDIFF(hour,CONVERT(datetime,wf_doccreated),convert(datetime,wf_lastmodified))) as time from BPM_ArchivedData where wf_processid='{1}' and WF_DocCreated between '{2}' and '{3}' group by wf_processname",
				"select wf_processname,SUM(TIMESTAMPDIFF(HOUR,STR_TO_DATE(wf_doccreated,'%Y-%m-%d %H:%i:%s'),STR_TO_DATE(wf_lastmodified,'%Y-%m-%d %H:%i:%s'))) as time from BPM_ArchivedData where wf_processid='{1}' and WF_DocCreated between '{2}' and '{3}' group by wf_processname",
				"select wf_processname,SUM(ROUND(TO_NUMBER(TO_DATE(wf_lastmodified, 'yyyy-MM-dd HH24:mi:ss') - TO_DATE(wf_doccreated, 'yyyy-MM-dd HH24:mi:ss')) * 24)) as time from BPM_ArchivedData where wf_processid='{1}' and WF_DocCreated between '{2}' and '{3}' group by wf_processname",
				""
		}));
		allTemplateSql.put("R_S016_E045_numsql1", Arrays.asList(new String[]{
				"select COUNT(*) as num from BPM_ArchivedData where WF_Processid='{1}' and WF_DocCreated between '{2}' and '{3}'",
				"select COUNT(*) as num from BPM_ArchivedData where WF_Processid='{1}' and WF_DocCreated between '{2}' and '{3}'",
				"select COUNT(*) as num from BPM_ArchivedData where WF_Processid='{1}' and WF_DocCreated between '{2}' and '{3}'",
				""
		}));
		
		// 同比流程效率趋势
		allTemplateSql.put("R_S016_E046_timesql", Arrays.asList(new String[]{
				"select wf_processname,SUM(DATEDIFF(hour,CONVERT(datetime,wf_doccreated),convert(datetime,wf_lastmodified))) as time from BPM_ArchivedData where wf_processid='{1}' and WF_DocCreated between '{2}' and '{3}' group by wf_processname",
				"select wf_processname,SUM(TIMESTAMPDIFF(HOUR,STR_TO_DATE(wf_doccreated,'%Y-%m-%d %H:%i:%s'),STR_TO_DATE(wf_lastmodified,'%Y-%m-%d %H:%i:%s'))) as time from BPM_ArchivedData where wf_processid='{1}' and WF_DocCreated between '{2}' and '{3}' group by wf_processname",
				"select wf_processname,SUM(ROUND(TO_NUMBER(TO_DATE(wf_lastmodified, 'yyyy-MM-dd HH24:mi:ss') - TO_DATE(wf_doccreated, 'yyyy-MM-dd HH24:mi:ss')) * 24)) as time from BPM_ArchivedData where wf_processid='{1}' and WF_DocCreated between '{2}' and '{3}' group by wf_processname",
				""
		}));
		allTemplateSql.put("R_S016_E046_numsql", Arrays.asList(new String[]{
				"select COUNT(*) as num from BPM_ArchivedData where WF_Processid='{1}' and WF_DocCreated between '{2}' and '{3}'",
				"select COUNT(*) as num from BPM_ArchivedData where WF_Processid='{1}' and WF_DocCreated between '{2}' and '{3}'",
				"select COUNT(*) as num from BPM_ArchivedData where WF_Processid='{1}' and WF_DocCreated between '{2}' and '{3}'",
				""
		}));
		allTemplateSql.put("R_S016_E046_timesql1", Arrays.asList(new String[]{
				"select wf_processname,SUM(DATEDIFF(hour,CONVERT(datetime,wf_doccreated),convert(datetime,wf_lastmodified))) as time from BPM_ArchivedData where wf_processid='{1}' and WF_DocCreated between '{2}' and '{3}' group by wf_processname",
				"select wf_processname,SUM(TIMESTAMPDIFF(HOUR,STR_TO_DATE(wf_doccreated,'%Y-%m-%d %H:%i:%s'),STR_TO_DATE(wf_lastmodified,'%Y-%m-%d %H:%i:%s'))) as time from BPM_ArchivedData where wf_processid='{1}' and WF_DocCreated between '{2}' and '{3}' group by wf_processname",
				"select wf_processname,SUM(ROUND(TO_NUMBER(TO_DATE(wf_lastmodified, 'yyyy-MM-dd HH24:mi:ss') - TO_DATE(wf_doccreated, 'yyyy-MM-dd HH24:mi:ss')) * 24)) as time from BPM_ArchivedData where wf_processid='{1}' and WF_DocCreated between '{2}' and '{3}' group by wf_processname",
				""
		}));
		allTemplateSql.put("R_S016_E046_numsql1", Arrays.asList(new String[]{
				"select COUNT(*) as num from BPM_ArchivedData where WF_Processid='{1}' and WF_DocCreated between '{2}' and '{3}'",
				"select COUNT(*) as num from BPM_ArchivedData where WF_Processid='{1}' and WF_DocCreated between '{2}' and '{3}'",
				"select COUNT(*) as num from BPM_ArchivedData where WF_Processid='{1}' and WF_DocCreated between '{2}' and '{3}'",
				""
		}));
	}
	
	/**
	 * 根据数据库类型选择不同的sql语句模板
	 * <p>
	 * 不带参数的sql模板调用该方法，假设模板key值为"sql1"<br>
	 * 			模板：select * from student where name='张三' and age=20<br>
	 * 			调用：createSql("mysql", "sql1");<br>
	 * </p>
	 * <p>
	 * 带参数的sql模板调用该方法，假设模板key值为"sql1"<br>
	 * 			模板：select * from student where name='{1}' and age={2}<br>
	 * 			调用：createSql("mysql", "sql1", "张三", "20");<br>
	 * </p>
	 * 
	 * @param dBType 数据库类型，不区分大小写，枚举值：mssql、mysql、oracle、db2
	 * @param templateKey sql模板对应的key值，用于在容器中获取sql
	 * @param params 参数，用于替换模板中的不确定的条件参数等
	 * @return 返回根据数据库类型和模板key值选择的sql语句
	 * @throws Exception 模板Sql不存在、没有匹配的数据库类型、参数数目不匹配会抛出异常信息
	 */
	public static String createSql(String dBType, String templateKey, Object... params) throws Exception {
		String sql;
		List<String> sqlTemplateList = allTemplateSql.get(templateKey);
		if (sqlTemplateList == null) throw new Exception("ERROR: " + templateKey + "对应的模板Sql不存在！请检查cn.linkey.app.dao.SqlType类中allTemplateSql容器是否存在该key！");
		switch (dBType.toLowerCase()) {
			case "mssql":
				sql = sqlTemplateList.get(0);
				break;
			case "mysql":
				sql = sqlTemplateList.get(1);
				break;
			case "oracle":
				sql = sqlTemplateList.get(2);
				break;
			case "db2":
				sql = sqlTemplateList.get(3);
				break;
			default:
				throw new Exception("ERROR: 没有匹配的" + dBType + "数据库类型！");
		}
		int index = sql.lastIndexOf("}");
		if (index != -1) {
			int paramsLen = Integer.valueOf(sql.substring(index - 1, index));
			if (paramsLen != params.length) throw new Exception("Error: 参数数目不匹配！按照模板应携带" + paramsLen + "个参数");
		}
		for (int i = 0; i < params.length; i++) {
			sql = sql.replace("{" + (i + 1) + "}", String.valueOf(params[i]));
		}
		return sql;
	}
}
