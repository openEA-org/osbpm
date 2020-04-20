package cn.linkey.rulelib.S015;

import java.io.File;
import java.util.*;
import org.apache.commons.io.FileUtils;
import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * @RuleName:生成规则帮助方法
 * @author admin
 * @version: 8.0
 * @Created: 2014-05-20 13:54
 */
final public class R_S015_B002 implements LinkeyRule {

    private HashSet<String> methodKeyValue = new HashSet<String>();
    private boolean creatMethodToSql = true; //是否创建方法到sql帮助中去

    @Override
    public String run(HashMap<String, Object> params) {

        //1.先删除所有方法再重新生成
        String sql = "delete from BPM_SystemHelp where FolderType='2'";
        Rdb.execSql(sql);

        //params为运行本规则时所传入的参数
        sql = "select * from BPM_SystemHelp where FolderType='1'";
        String srcPath = BeanCtx.getSystemConfig("ProjectJavaSrcPath");

        Document[] dc = Rdb.getAllDocumentsBySql(sql);
        for (Document doc : dc) {
            String className = doc.g("FolderName");
            int s = className.lastIndexOf(".");
            methodKeyValue.add(className.substring(s + 1)); //加入类名为关键字

            //if(!className.equals("cn.linkey.factory.BeanCtx")) continue;

            //再得到java源文件代码
            String filePath = srcPath + "/" + className.replace(".", "/") + ".java";
            BeanCtx.out("分析源文件=" + filePath);
            File file = FileUtils.getFile(filePath);
            if (file.exists()) {
                String javaCode = Tools.readFileToString(filePath, "utf-8");

                //先删除掉此类下面的方法不然当方法名改动时会造成重复的方法插入
                sql = "select Folderid from BPM_SystemHelp where FolderName='" + className + "'";
                String parentFolderid = Rdb.getValueBySql(sql);
                //				if(creatMethodToSql){
                //					sql="delete from BPM_SystemHelp where ParentFolderid='"+parentFolderid+"'";
                //					Rdb.execSql(sql);
                //				}

                getMethod(javaCode, className, parentFolderid);
            }
            else {
                BeanCtx.out(filePath + "源文件不存在<br>");
            }

        }
        BeanCtx.out(Tools.join(this.methodKeyValue, "|"));
        return "";
    }

    public void getMethod(String javaCode, String className, String parentFolderid) {
        int spos = javaCode.indexOf("{");
        int epos = javaCode.lastIndexOf("}");
        javaCode = javaCode.substring(spos + 1, epos);

        //开始分析public
        int i = 0;
        spos = javaCode.indexOf("public ");
        while (spos > 0 && i < 50000) {
            i++;

            //分析获得方法的注解
            String methodRemark = "";
            String startStr = javaCode.substring(0, spos);
            int xpos = startStr.indexOf("/**");
            if (xpos > 0) {
                int ypos = startStr.indexOf("*/", xpos);
                methodRemark = startStr.substring(xpos + 3, ypos);
            }

            //分析获得方法的全名称
            int interfaceX = 0;
            String methodFullName = "";
            epos = javaCode.indexOf("{", spos);
            if (epos < 0) {
                epos = javaCode.indexOf(";"); //如果是接口则是以;号结束的
                interfaceX = 1;
            }
            try {
                methodFullName = javaCode.substring(spos, epos); //方法全名称
                //BeanCtx.out("方法全称:"+javaCode.substring(spos,epos));
            }
            catch (Exception e) {
                BeanCtx.out("epos=" + epos);
                BeanCtx.out("javaCode=" + javaCode);
            }

            //分析获得方法的简称
            String methodShortName = methodFullName;
            int x = methodFullName.indexOf("(");
            String tmpStr = methodFullName.substring(0, x);
            String endStr = methodFullName.substring(x);
            x = tmpStr.lastIndexOf(" ");
            if (x > 0) {
                String method = tmpStr.substring(x).trim();
                methodKeyValue.add(method); //加入方法名为关键字
                methodShortName = method + endStr;
                //BeanCtx.out("简称为："+methodShortName);
            }

            javaCode = javaCode.substring(epos + interfaceX); //这里要加上接口的;分号偏移量为1
            spos = javaCode.indexOf("public ");

            //在sql中生成记录
            insertIntoHelp(parentFolderid, className, methodShortName, methodFullName, methodRemark);

        }
    }

    /**
     * 把方法名称插入到sql表中去
     */
    public int insertIntoHelp(String parentFolderid, String className, String folderName, String methodName, String methodRemark) {
        if (!creatMethodToSql) { //不创建方法到sql中
            return 0;
        }

        BeanCtx.setDocNotEncode();
        folderName = folderName.trim();

        Document doc = BeanCtx.getDocumentBean("BPM_SystemHelp");
        doc.s("Folderid", getNewFolder(parentFolderid));
        doc.s("ParentFolderid", parentFolderid);
        doc.s("FolderName", folderName);
        doc.s("MethodName", methodName);
        doc.s("MethodRemark", methodRemark);
        doc.s("FolderType", "2");
        int i = doc.save();
        return i;
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