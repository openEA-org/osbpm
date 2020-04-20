package cn.linkey.rulelib.S006;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * 
* Copyright © 2018 A Little Bao. All rights reserved.
* 
* @ClassName: R_S006_B021.java
* @Description: 角色多选器
*
* @version: v1.0.0
* @author: alibao
* @date: 2018年7月13日 下午1:53:20 
*
* Modification History:
* Date         Author          Version            Description
*---------------------------------------------------------*
* 2018年7月13日     alibao           v1.0.0               修改原因
 */
final public class R_S006_B021 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        String roleType = BeanCtx.g("roleType");
        if (Tools.isBlank(roleType) || roleType.equals("1")) {
            roleType = "1";
        }
        else {
            roleType = "2";
        }
        saveRoleMembers(roleType);
        return "";
    }

    //给角色/岗位添加成员 
    private void saveRoleMembers(String roleType) {
        Document doc = new Document("BPM_OrgRoleMembers");
        String Member = BeanCtx.g("Member");
        String MemberName = BeanCtx.g("MemberName");
        String Deptid = BeanCtx.g("Deptid");
        String memberNameTemp[] = MemberName.split(",");
        String Membertemp[] = Member.split(",");
        String DeptIdtemp[] = Deptid.split(",");

        for (int i = 0; i < Membertemp.length; i++) {
            doc = new Document("BPM_OrgRoleMembers");
            String roleNum = BeanCtx.g("RoleNumber");
            String memberName = memberNameTemp[i];
            boolean flag = Rdb.hasRecord("select WF_OrUnid from BPM_OrgRoleMembers where RoleNumber='" + roleNum +"' and MemberName='" + memberName+ "' and RoleType='" + roleType + "'");
            if(flag) {
            	
            	//20180713 add 添加修改排序号
            	String sql2 = "select * from BPM_OrgRoleMembers where WF_OrUnid ='" + BeanCtx.g("WF_DocUnid") + "'";
            	Document doctemp = Rdb.getDocumentBySql(sql2);
            	if(!doctemp.isNull()){
            		doctemp.s("SortNum", BeanCtx.g("SortNum"));
            		doctemp.save();
            	}
            	//============================
                continue;
            }

            doc.s("WF_OrUnid", doc.getDocUnid());
            doc.s("RoleType", roleType);
            doc.s("RoleNumber", roleNum);
            doc.s("MemberName", memberName);
            doc.s("Member", Membertemp[i]);

            doc.s("Deptid", DeptIdtemp[i]);
            String sql = "select OrgClass,FolderName,Folderid from BPM_OrgDeptList where Deptid='" + DeptIdtemp[i] + "'";
            Document doc_DeptInfo = Rdb.getDocumentBySql(sql);
            doc.s("OrgClass", doc_DeptInfo.g("OrgClass"));
            doc.s("Folderid", doc_DeptInfo.g("Folderid"));

            doc.s("SortNum", BeanCtx.g("SortNum"));
            doc.save();
        }
        String data_msg = "{'Status':'ok','msg':'文档成功保存!'}";
        BeanCtx.p(data_msg);
    }

}