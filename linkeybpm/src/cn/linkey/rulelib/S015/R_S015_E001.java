package cn.linkey.rulelib.S015;

import java.util.HashMap;
import java.util.HashSet;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * @RuleName:新增包或方法事件
 * @author admin
 * @version: 8.0
 * @Created: 2014-05-20 12:00
 */
final public class R_S015_E001 implements LinkeyRule {

    @Override
    public String run(HashMap<String, Object> params) {
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

    public String onFormOpen(Document doc, Document formDoc, String readOnly) {
        //当表单打开时
        if (readOnly.equals("1")) {
            return "1";
        } //如果是阅读状态则可不执行
        if (doc.isNewDoc()) {
            //可以对表单字段进行初始化如:doc.s("fdname",fdvalue),可以获取字段值 doc.g("fdname")

        }
        return "1"; //成功必须返回1，否则表示退出并显示返回的字符串
    }

    public String onFormSave(Document doc, Document formDoc) {
        //当表单存盘前
        String oldParentFolderid = Rdb.getValueBySql("select ParentFolderid from BPM_SystemHelp where WF_OrUnid='" + doc.g("WF_OrUnid") + "'");
        if (Tools.isBlank(doc.g("Folderid")) || !doc.g("ParentFolderid").equals(oldParentFolderid)) {
            String folderid = getNewFolder(doc.g("ParentFolderid"));
            doc.s("Folderid", folderid);
        }

        if (Tools.isBlank(doc.g("ParentFolderid"))) {
            doc.s("ParentFolderid", "root");
        }

        return "1"; //成功必须返回1，否则表示退出存盘
    }

    /**
     * 获得一个新的文件夹编号
     * 
     * @param parentFolderid 上级文件夹编号
     * @return
     */
    public String getNewFolder(String parentFolderid) {
        String newSubFolderid = "";
        //获得文件夹下面的所有子文件夹的已有编号
        String sql = "select Folderid from BPM_SystemHelp where ParentFolderid='" + parentFolderid + "'";
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