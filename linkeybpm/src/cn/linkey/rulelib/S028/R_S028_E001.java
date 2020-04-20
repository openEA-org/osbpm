package cn.linkey.rulelib.S028;

import java.util.HashMap;
import java.util.HashSet;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * @RuleName:新增文件夹事件
 * @author admin
 * @version: 8.0
 * @Created: 2014-07-25 11:07
 */
final public class R_S028_E001 implements LinkeyRule {

    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        //获取事件运行参数
        Document formDoc = (Document) params.get("FormDoc"); //表单配置文档
        Document doc = (Document) params.get("DataDoc"); //数据主文档
        String eventName = (String) params.get("EventName");//事件名称
        if (eventName.equals("onFormOpen")) {
            String readOnly = (String) params.get("ReadOnly"); //1表示只读，0表示编辑
            return onFormOpen(doc, formDoc, readOnly);
        }
        else if (eventName.equals("onFormSave")) {
            return onFormSave(doc, formDoc);
        }
        return "1";
    }

    public String onFormOpen(Document doc, Document formDoc, String readOnly) throws Exception {
        //当表单打开时
        if (readOnly.equals("1")) {
            return "1";
        } //如果是阅读状态则可不执行
        if (doc.isNewDoc()) {
            //可以对表单字段进行初始化如:doc.s("fdname",fdvalue),可以获取字段值 doc.g("fdname")

        }
        return "1"; //成功必须返回1，否则表示退出并显示返回的字符串
    }

    public String onFormSave(Document doc, Document formDoc) throws Exception {
        //当表单存盘前
        //首先得到当前文件夹的旧的上级文件夹编号
        String folderid = "";
        String parentFolderid = doc.g("ParentFolderid");//新的上级文件夹编号
        if (doc.isNewDoc()) {
            folderid = getNewFolderid(parentFolderid);
        }
        else {
            //说明是编辑文档，看是否修改了上级文件夹
            String sql = "select ParentFolderid from BPM_DataDicConfig where Folderid='" + doc.g("Folderid") + "'";
            String oldParentFolderid = Rdb.getValueBySql(sql);
            if (!oldParentFolderid.equals(parentFolderid)) {
                //说明有修改了上级文件夹，需要重新计算一次文件夹编号
                folderid = getNewFolderid(parentFolderid);
                moveAllSubFolder(doc.g("Folderid"), folderid);
            }
        }

        if (Tools.isNotBlank(folderid)) {
            doc.s("Folderid", folderid);
        }

        return "1"; //成功必须返回1，否则表示退出存盘
    }

    /**
     * 如果修改了文件夹的层次则需要移动下面的所有子文件夹
     * 
     * @param oldFolderid 旧文夹id
     * @param newFolderid 新文件夹id
     */
    public void moveAllSubFolder(String oldFolderid, String newFolderid) {
        String sql = "select * from BPM_DataDicConfig where  ParentFolderid like '" + oldFolderid + "%'";
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

    /**
     * 获得一个新的文件夹编号
     * 
     * @param parentFolderid 上级文件夹编号
     * @return
     */
    public String getNewFolderid(String parentFolderid) {
        String newSubFolderid = "";
        //获得文件夹下面的所有子文件夹的已有编号
        String sql = "select Folderid from BPM_DataDicConfig where ParentFolderid='" + parentFolderid + "'";
        HashSet<String> allSubFolderSet = Rdb.getValueSetBySql(sql);
        if (allSubFolderSet.size() == 0) {
            //还没有子文件夹的情况下
            if (parentFolderid.equals("root")) {
                parentFolderid = "";
            }
            newSubFolderid = parentFolderid + "001";
        }
        else {
            //已经有子文件夹的情况下
            if (parentFolderid.equals("root")) {
                parentFolderid = "";
            }
            for (int i = 1; i < 100; i++) {
                String newNum = "00000" + i;
                newNum = newNum.substring(newNum.length() - 3); //每次取最后3位数的编号
                String newFolderid = parentFolderid + newNum;
                if (!allSubFolderSet.contains(newFolderid)) {
                    newSubFolderid = newFolderid; //如果在已有的文件夹中找不到则返回新文件夹id号
                    break;
                }
            }
        }
        return newSubFolderid;
    }

}