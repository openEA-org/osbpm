package cn.linkey.rulelib.S006;

import java.util.*;
import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * @RuleName:角色岗位体系树维护
 * @author admin
 * @version: 8.0
 * @Created: 2014-04-02 14:47
 */
final public class R_S006_B007 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) {
        String action = BeanCtx.g("wf_action");
        if (action.equals("edit")) {
            editFolder(); //修改名称
        }
        else if (action.equals("delete")) {
            deleteFolder();//删除菜单
        }
        else if (action.equals("move")) {
            moveFolder();//移动菜单
        }
        else {
            newFolder(); //新增菜单
        }
        return "";
    }

    public String newFolder() {
        //创建新的文件夹
        String folderName = BeanCtx.g("folderName", true); //新文件夹名称
        String parentDocUnid = BeanCtx.g("parentDocUnid", true); //当前文件夹的上级文件夹的unid
        String treeid = BeanCtx.g("treeid", true);
        String newSortNum = "1001"; //新的排序号
        String newSubFolderid = ""; //新的文件夹编号
        String parentFolderid = "root"; //上级文件夹编号,默认为root
        if (parentDocUnid.equals("root")) {
            //获得root文件夹下面的所有子文件夹的已有编号
            String sql = "select Folderid from BPM_OrgRoleTree where Treeid='" + treeid + "' and ParentFolderid='root'";
            HashSet<String> allSubFolderSet = Rdb.getValueSetBySql(sql);
            //BeanCtx.out(allSubFolderSet.toString());
            if (allSubFolderSet.size() == 0) {
                //还没有子文件夹的情况下
                newSubFolderid = "001";
            }
            else {
                //已经有子文件夹的情况下
                for (int i = 1; i < 100; i++) {
                    String newNum = "00000" + i;
                    newNum = newNum.substring(newNum.length() - 3); //每次取最后3位数的编号
                    String newFolderid = newNum;
                    if (!allSubFolderSet.contains(newFolderid)) {
                        newSubFolderid = newFolderid; //如果在已有的文件夹中找不到则返回新文件夹id号
                        break;
                    }
                }
                //计算最大的排序号,让新增的文件夹位于排序的最后
                sql = "select SortNum from BPM_OrgRoleTree where Treeid='" + treeid + "' and ParentFolderid='root' order by SortNum desc";
                newSortNum = Rdb.getValueTopOneBySql(sql);
                newSortNum = String.valueOf(Integer.parseInt(newSortNum) + 1);
            }
        }
        else {
            //在其他文件夹下面创建
            String sql = "select * from BPM_OrgRoleTree where WF_OrUnid='" + parentDocUnid + "'";
            Document parentDoc = Rdb.getDocumentBySql(sql);
            parentFolderid = parentDoc.g("Folderid");

            //获得上级文件夹下面的所有子文件夹的已有编号
            sql = "select Folderid from BPM_OrgRoleTree where Treeid='" + treeid + "' and ParentFolderid='" + parentFolderid + "'";
            HashSet<String> allSubFolderSet = Rdb.getValueSetBySql(sql);
            //BeanCtx.out(allSubFolderSet.toString());
            if (allSubFolderSet.size() == 0) {
                //还没有子文件夹的情况下
                newSubFolderid = parentFolderid + "001";
            }
            else {
                //已经有子文件夹的情况下
                for (int i = 1; i < 100; i++) {
                    String newNum = "00000" + i;
                    newNum = newNum.substring(newNum.length() - 3); //每次取最后3位数的编号
                    String newFolderid = newNum;
                    newFolderid = parentFolderid + newFolderid;
                    if (!allSubFolderSet.contains(newFolderid)) {
                        newSubFolderid = newFolderid; //如果在已有的文件夹中找不到则返回新文件夹id号
                        break;
                    }
                }
                //计算最大的排序号,让新增的文件夹位于排序的最后
                sql = "select SortNum from BPM_OrgRoleTree where Treeid='" + treeid + "' and ParentFolderid='" + parentFolderid + "' order by SortNum desc";
                newSortNum = Rdb.getValueTopOneBySql(sql);
                newSortNum = String.valueOf(Integer.parseInt(newSortNum) + 1);
            }

        }

        //创建一个子菜单文档对像
        Document newSubDoc = BeanCtx.getDocumentBean("BPM_OrgRoleTree");
        newSubDoc.s("WF_OrUnid", Rdb.getNewid("BPM_OrgRoleTree"));
        newSubDoc.s("FolderName", folderName);
        newSubDoc.s("ParentFolderid", parentFolderid);
        newSubDoc.s("Treeid", treeid);
        newSubDoc.s("SortNum", newSortNum);
        newSubDoc.s("Folderid", newSubFolderid);
        int i = newSubDoc.save(); //子菜单存盘
        if (i > 0) {
            BeanCtx.p("{'Status':'ok','docUnid':'" + newSubDoc.g("WF_OrUnid") + "','id':'" + newSubFolderid + "'}");
        }
        else {
            BeanCtx.p(Tools.jmsg("error", BeanCtx.getMsg("Designer", "Can not creat the folder!")));
        }

        return "";
    }

    public void editFolder() {
        //修改菜单名称
        String docUnid = BeanCtx.g("docUnid", true);
        String folderName = BeanCtx.g("folderName", true);
        String sql = "select * from BPM_OrgRoleTree where WF_OrUnid='" + docUnid + "'";
        Document doc = Rdb.getDocumentBySql(sql);
        if (doc.isNull()) {
            BeanCtx.p(Tools.jmsg("error", BeanCtx.getMsg("Designer", "ActionError")));
        }
        else {
            doc.s("FolderName", folderName);
            doc.save();
            BeanCtx.p(Tools.jmsg("ok", ""));
        }
    }

    public void moveFolder() {
        //移动菜单
        String parentDocUnid = BeanCtx.g("parentDocUnid", true); //当前文件夹的上级文件夹的unid
        String curDocUnid = BeanCtx.g("curDocUnid", true); //当前文件夹的unid
        String treeid = BeanCtx.g("treeid", true);
        String newSortNum = "1001"; //新的排序号
        String newSubFolderid = ""; //新的文件夹编号
        String parentFolderid = "root"; //上级文件夹编号,默认为root
        if (parentDocUnid.equals("root")) {
            //移动到根文件夹下面
            //获得root文件夹下面的所有子文件夹的已有编号
            String sql = "select Folderid from BPM_OrgRoleTree where Treeid='" + treeid + "' and ParentFolderid='root'";
            HashSet<String> allSubFolderSet = Rdb.getValueSetBySql(sql);
            // BeanCtx.out(allSubFolderSet.toString());
            if (allSubFolderSet.size() == 0) {
                //还没有子文件夹的情况下
                newSubFolderid = "001";
            }
            else {
                //已经有子文件夹的情况下
                for (int i = 1; i < 100; i++) {
                    String newNum = "00000" + i;
                    newNum = newNum.substring(newNum.length() - 3); //每次取最后3位数的编号
                    String newFolderid = newNum;
                    if (!allSubFolderSet.contains(newFolderid)) {
                        newSubFolderid = newFolderid; //如果在已有的文件夹中找不到则返回新文件夹id号
                        break;
                    }
                }
                //计算最大的排序号,让新增的文件夹位于排序的最后
                sql = "select SortNum from BPM_OrgRoleTree where Treeid='" + treeid + "' and ParentFolderid='root' order by SortNum desc";
                newSortNum = Rdb.getValueTopOneBySql(sql);
                newSortNum = String.valueOf(Integer.parseInt(newSortNum) + 1);
            }
        }
        else {
            //移动到其他文件夹下面
            String sql = "select * from BPM_OrgRoleTree where WF_OrUnid='" + parentDocUnid + "'";
            Document parentDoc = Rdb.getDocumentBySql(sql);
            parentFolderid = parentDoc.g("Folderid");

            //获得上级文件夹下面的所有子文件夹的已有编号
            sql = "select Folderid from BPM_OrgRoleTree where Treeid='" + treeid + "' and ParentFolderid='" + parentFolderid + "'";
            HashSet<String> allSubFolderSet = Rdb.getValueSetBySql(sql);
            // BeanCtx.out(allSubFolderSet.toString());
            if (allSubFolderSet.size() == 0) {
                //还没有子文件夹的情况下
                newSubFolderid = parentFolderid + "001";
            }
            else {
                //已经有子文件夹的情况下
                for (int i = 1; i < 100; i++) {
                    String newNum = "00000" + i;
                    newNum = newNum.substring(newNum.length() - 3); //每次取最后3位数的编号
                    String newFolderid = newNum;
                    newFolderid = parentFolderid + newFolderid;
                    if (!allSubFolderSet.contains(newFolderid)) {
                        newSubFolderid = newFolderid; //如果在已有的文件夹中找不到则返回新文件夹id号
                        break;
                    }
                }
                //计算最大的排序号,让新增的文件夹位于排序的最后
                sql = "select SortNum from BPM_OrgRoleTree where Treeid='" + treeid + "' and ParentFolderid='" + parentFolderid + "' order by SortNum desc";
                newSortNum = Rdb.getValueTopOneBySql(sql);
                newSortNum = String.valueOf(Integer.parseInt(newSortNum) + 1);
            }

        }

        //更新当前文件夹的编号和上级文件夹以及排序号
        String sql = "select * from BPM_OrgRoleTree where WF_OrUnid='" + curDocUnid + "'";
        Document doc = Rdb.getDocumentBySql(sql);
        this.moverAllSubFolder(treeid, doc.g("Folderid"), newSubFolderid); //移动本文件夹下面的所有子文件夹
        doc.s("Folderid", newSubFolderid);
        doc.s("ParentFolderid", parentFolderid);
        doc.s("SortNum", newSortNum);
        int i = doc.save();
        if (i > 0) {
            BeanCtx.p(Tools.jmsg("ok", ""));
        }
        else {
            BeanCtx.p(Tools.jmsg("error", BeanCtx.getMsg("Designer", "ActionError")));
        }

    }

    /**
     * 移动一个文件夹下面的所有子文件夹
     * 
     * @param appid 应用编号
     * @param treeid 树id
     * @param oldFolderid 旧文夹
     * @param newFolderid
     */
    public void moverAllSubFolder(String treeid, String oldFolderid, String newFolderid) {
        String sql = "select * from BPM_OrgRoleTree where ParentFolderid like '" + oldFolderid + "%'";
        Document[] dc = Rdb.getAllDocumentsBySql(sql);
        for (Document doc : dc) {
            //oldFolderid=001,newFolderid=003002
            String subParentFolderid = doc.g("ParentFolderid"); //001
            String subFolderid = doc.g("Folderid"); //001002,001002001,001002003
            String newSubFolderid = newFolderid + subFolderid.substring(oldFolderid.length()); //计算后:003002+002,003002+002001,003002+002003
            String newSubParentFolderid = newFolderid + subParentFolderid.substring(oldFolderid.length());//计算后:003003+""
            doc.s("ParentFolderid", newSubParentFolderid);
            doc.s("Folderid", newSubFolderid);
            doc.save();
        }
    }

    public void deleteFolder() {
        //删除菜单
        String docUnid = BeanCtx.g("docUnid", true);
        String sql = "select * from BPM_OrgRoleTree where WF_OrUnid='" + docUnid + "'";
        Document doc = Rdb.getDocumentBySql(sql);
        if (doc.isNull()) {
            BeanCtx.p(Tools.jmsg("error", BeanCtx.getMsg("Designer", "ActionError")));
        }
        else {
            doc.remove(true);
            BeanCtx.p(Tools.jmsg("ok", ""));
        }
    }
}