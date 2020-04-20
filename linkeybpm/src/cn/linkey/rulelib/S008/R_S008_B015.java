package cn.linkey.rulelib.S008;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * @RuleName:path路径解析
 * @author admin
 * @version: 8.0
 * @Created: 2015-10-08 14:02
 */
final public class R_S008_B015 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        //params为运行本规则时所传入的参数
        String tid = BeanCtx.g("tid", true);
        String sql = "select PageNum from BPM_PageList where IsHomePage='1' and WF_Appid='" + BeanCtx.getAppid() + "'";
        String homePageNum = Rdb.getValueBySql(sql);
        String homeUrl = "r?wf_num=" + homePageNum;
        String htmlCode = "<ul class=\"page-breadcrumb\">";
        if (BeanCtx.g("eid").equals(homePageNum)) {
            //当前本身就是首页
            htmlCode += "<li><i class=\"fa fa-home\"></i><a href=\"" + homeUrl + "\">Home</a></li>";
        }
        else if (Tools.isBlank(tid)) {
            String curUrl = "page?wf_num=" + homePageNum + "&eid=" + BeanCtx.g("eid", true);
            sql = "select eleName from BPM_AllElementList where eleid='" + BeanCtx.g("eid", true) + "'";
            String eleName = Rdb.getValueBySql(sql);
            htmlCode += "<li><i class=\"fa fa-home\"></i><a href=\"" + homeUrl + "\">Home</a><i class=\"fa fa-angle-right\"></i></li>" + "<li><a href=\"" + curUrl + "\">" + eleName + "</a></li>";
        }
        else {
            //左则导航树的路径
            String treeid = (String) params.get("XTagValue");
            String curUrl = "r?" + BeanCtx.getRequest().getQueryString().toString();
            sql = "select FolderName from BPM_NavTreeNode where Folderid='" + tid + "' and Treeid='" + treeid + "'";
            String folderName = Rdb.getValueBySql(sql);
            htmlCode += "<li><i class=\"fa fa-home\"></i><a href=\"" + homeUrl + "\">Home</a><i class=\"fa fa-angle-right\"></i></li>" + "<li><a href=\"" + curUrl + "\">" + folderName + "</a></li>";
        }
        htmlCode += "</ul>";
        return htmlCode;
    }

}