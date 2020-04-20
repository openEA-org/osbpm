package cn.linkey.org;

import java.util.LinkedHashSet;

import cn.linkey.dao.Rdb;
import cn.linkey.dao.RdbCache;
import cn.linkey.doc.Document;
import cn.linkey.doc.Documents;
import cn.linkey.factory.BeanCtx;
import cn.linkey.util.Tools;

/**
 * 用户数据操作类,实现本类后必须调用init()方法来初化本类的参数
 * 
 * @author Administrator
 *
 */
public class UserModel {

    private String userid;
    private String cnName;
    private String password;
    private String jobTitle;
    private String secretary;
    private String phoneNumber;
    private String qq;
    private String weiXinid;
    private String indexFlag;
    private String sortNumber;
    private String internetAddress;
    private String lang;
    private String status;
    private String hourlyWage;
    private String otherFolderName;
    private String xmlData;

    /**
     * 必须指定userid才能创建本类的实例
     * 
     * @param userid
     */
    public void init(String userid) {
        this.userid = userid;
    }

    /**
     * 
     * @param otherFolderidList 用户所在兼职部门的folderid
     * @param folderid 所属部门的folderid
     * @return 返回0表示注册失败,1表示注册成功
     */
    public String reg(String folderid, String otherFolderidList) {
        //注册一个新用户

        if (userid.indexOf("'") != -1 || userid.indexOf("\"") != -1 || userid.indexOf("&") != -1) {
            return "0"; //有非法字符
        }

        String orgClass = BeanCtx.getSystemConfig("DefaultOrgClass"); //缺省行政架构
        String sql = "select Deptid from BPM_OrgDeptList where OrgClass='" + orgClass + "' and Folderid='" + folderid + "'";
        String deptid = Rdb.getValueBySql(sql);

        //1.先删除当前用户的所有关系记录
        sql = "delete from BPM_OrgUserDeptMap where OrgClass='" + orgClass + "' and Userid='" + userid + "'";
        Rdb.execSql(sql);

        //2.存或更新用户与主部门的关系记录
        Document mapdoc = BeanCtx.getDocumentBean("BPM_OrgUserDeptMap");
        mapdoc.s("OrgClass", orgClass);
        mapdoc.s("Userid", userid);
        mapdoc.s("Folderid", folderid);
        mapdoc.s("Deptid", deptid);
        mapdoc.s("CurrentFlag", "1");
        mapdoc.s("MainDept", "1"); //标识为主部门的关系记录
        mapdoc.save();

        //3.如果有选择兼职部门的情况下，存用户与兼职部门的关系记录
        if (Tools.isNotBlank(otherFolderidList)) {
            String[] folderArray = Tools.split(otherFolderidList, ",");
            for (String otherFolderid : folderArray) {
                sql = "select * from BPM_OrgDeptList where OrgClass='" + orgClass + "' and Folderid='" + otherFolderid + "'";
                Document deptDoc = Rdb.getDocumentBySql(sql);
                deptid = deptDoc.g("Deptid");
                if (Tools.isBlank(this.otherFolderName)) {
                    this.otherFolderName = deptDoc.g("FolderName");
                }
                else {
                    this.otherFolderName += "," + deptDoc.g("FolderName");
                }
                mapdoc = BeanCtx.getDocumentBean("BPM_OrgUserDeptMap");
                mapdoc.s("OrgClass", orgClass);
                mapdoc.s("Userid", userid);
                mapdoc.s("Folderid", otherFolderid);
                mapdoc.s("Deptid", deptid);
                mapdoc.s("CurrentFlag", "0");
                mapdoc.s("MainDept", "0"); //标识为兼职部门
                mapdoc.save();
            }
        }

        //清除用户的缓存
        removeUserCache();

        int j = save();
        if (j > 0) {
            return "1";
        }
        else {
            return "0";
        }
    }

    /**
     * 批量注册用户
     * 
     * @param excelFullPath excel的全文件路径
     * @param actionid 1表示用户已存在时跳过,2表示覆盖
     * @return 返回注册后的提示消息
     */
    public String batchReg(String excelFullPath, String actionid) {
        int i = 0;
        int u = 0, t = 0, r = 0;
        String userList = "";
        String colFieldList = "FolderName,OtherFolderName,Userid,CnName,JobTitle,PhoneNumber,InternetAddress,Password,SortNumber,Status";
        LinkedHashSet<Document> dc = Documents.excel2dc(excelFullPath, colFieldList, "BPM_OrgUserList");
        for (Document userdoc : dc) {
            if (i == 0) {
                i++;
            }
            else {
                //开始注册用户
                this.userid = userdoc.g("Userid");
                this.cnName = userdoc.g("CnName");
                this.jobTitle = userdoc.g("JobTitle");
                this.internetAddress = userdoc.g("InternetAddress");
                this.sortNumber = userdoc.g("SortNumber");
                this.phoneNumber = userdoc.g("PhoneNumber");
                this.status = userdoc.g("Status");

                //检测用户是否已存在
                String sql = "select Userid from BPM_OrgUserList where userid='" + userdoc.g("Userid") + "'";
                if (Rdb.hasRecord(sql)) {
                    userList += userdoc.g("Userid") + " ";
                    if (actionid.equals("2")) {
                        //表示覆盖
                        this.save();//更新存盘
                        u++;
                    }
                    else {
                        t++;
                    }
                    continue;
                }

                this.setPassword(userdoc.g("Password"));

                //获得部门folderid
                sql = "select folderid from BPM_OrgDeptList where FolderName='" + userdoc.g("FolderName") + "'";
                String folderid = Rdb.getValueBySql(sql);

                //只能兼一个部门的职位
                String sqlWhere = "";
                String[] otherFolderArray = Tools.split(userdoc.g("OtherFolderName"));
                for (String folderName : otherFolderArray) {
                    if (Tools.isBlank(sqlWhere)) {
                        sqlWhere = "FolderName='" + folderName + "'";
                    }
                    else {
                        sqlWhere += " or FolderName='" + folderName + "'";
                    }
                }
                String otherFolderid = "";
                if (Tools.isNotBlank(sqlWhere)) {
                    sql = "select folderid from BPM_OrgDeptList where FolderName='" + userdoc.g("OtherFolderName") + "'";
                    otherFolderid = Rdb.getValueBySql(sql);
                }

                this.reg(folderid, otherFolderid); //注册用户
                r++;
            }
        }
        return "共成功注册(" + r + ")个用户,因同名而跳过(" + t + ")个更新(" + u + ")个,同名的用户为:" + userList;
    }

    /**
     * 保存用户文档
     * 
     * @return
     */
    public int save() {
        //开始创建用户文档对像
        Document userdoc = this.getUserDoc();
        if (Tools.isNotBlank(userid)) {
            userdoc.s("Userid", userid);
        }
        if (Tools.isNotBlank(cnName))
            userdoc.s("cnName", cnName);
        if (Tools.isNotBlank(password))
            userdoc.s("password", password);
        if (Tools.isNotBlank(jobTitle))
            userdoc.s("jobTitle", jobTitle);
        if (Tools.isNotBlank(this.getSecretary()))
            userdoc.s("secretary", this.getSecretary());
        if (Tools.isNotBlank(phoneNumber))
            userdoc.s("phoneNumber", phoneNumber);
        if (Tools.isNotBlank(qq))
            userdoc.s("qq", qq);
        if (Tools.isNotBlank(weiXinid))
            userdoc.s("weiXinid", weiXinid);
        if (Tools.isNotBlank(indexFlag))
            userdoc.s("indexFlag", indexFlag);
        if (Tools.isNotBlank(sortNumber))
            userdoc.s("sortNumber", sortNumber);
        if (Tools.isNotBlank(internetAddress))
            userdoc.s("internetAddress", internetAddress);
        if (Tools.isNotBlank(lang))
            userdoc.s("lang", lang);
        if (Tools.isNotBlank(status))
            userdoc.s("status", status);
        if (Tools.isNotBlank(hourlyWage))
            userdoc.s("hourlyWage", hourlyWage);
        if (Tools.isNotBlank(otherFolderName))
            userdoc.s("WF_OtherFolderid_show", otherFolderName);
        if (Tools.isNotBlank(this.getXmlData()))
            userdoc.appendFromXml(this.getXmlData());
        return userdoc.save();
    }

    /**
     * 获得用户文档对像
     * 
     * @return
     */
    public Document getUserDoc() {
        String sql = "select * from BPM_OrgUserList where Userid='" + userid + "'";
        return Rdb.getDocumentBySql(sql);
    }

    /**
     * 删除用户
     */
    public void delete() {

        String sql = "";

        //删除部门关系文档
        sql = "delete from BPM_OrgUserDeptMap where Userid='" + userid + "'";
        Rdb.execSql(sql);

        //删除角色，岗位中的成员
        sql = "delete from BPM_OrgRoleMembers where Member='" + userid + "'";
        Rdb.execSql(sql);

        Document userdoc = this.getUserDoc();
        userdoc.remove(true);

    }

    /**
     * 清除用户的缓存数据
     */
    public void removeUserCache() {
        RdbCache.remove("UserCacheStrategy", userid);
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getCnName() {
        return cnName;
    }

    public void setCnName(String cnName) {
        this.cnName = cnName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        //把密码加密成md5格式的
        if (!password.equals("*")) {
            password = Tools.md5(password);
        }
        this.password = password;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getSecretary() {
        return secretary;
    }

    public void setSecretary(String secretary) {
        this.secretary = secretary;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getWeiXinid() {
        return weiXinid;
    }

    public void setWeiXinid(String weiXinid) {
        this.weiXinid = weiXinid;
    }

    public String getIndexFlag() {
        return indexFlag;
    }

    public void setIndexFlag(String indexFlag) {
        this.indexFlag = indexFlag;
    }

    public String getSortNumber() {
        return sortNumber;
    }

    public void setSortNumber(String sortNumber) {
        this.sortNumber = sortNumber;
    }

    public String getInternetAddress() {
        return internetAddress;
    }

    public void setInternetAddress(String internetAddress) {
        this.internetAddress = internetAddress;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getHourlyWage() {
        return hourlyWage;
    }

    public void setHourlyWage(String hourlyWage) {
        this.hourlyWage = hourlyWage;
    }

    public String getXmlData() {
        return xmlData;
    }

    public void setXmlData(String xmlData) {
        this.xmlData = xmlData;
    }

}
