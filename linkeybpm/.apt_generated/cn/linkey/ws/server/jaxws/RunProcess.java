
package cn.linkey.ws.server.jaxws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "runProcess", namespace = "http://server.ws.linkey.cn/")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "runProcess", namespace = "http://server.ws.linkey.cn/", propOrder = {
    "docXml",
    "actionid",
    "processid",
    "docUnid",
    "nextNodeid",
    "nextUserList",
    "userid",
    "remark",
    "sysid",
    "syspwd"
})
public class RunProcess {

    @XmlElement(name = "docXml", namespace = "")
    private String docXml;
    @XmlElement(name = "actionid", namespace = "")
    private String actionid;
    @XmlElement(name = "processid", namespace = "")
    private String processid;
    @XmlElement(name = "docUnid", namespace = "")
    private String docUnid;
    @XmlElement(name = "nextNodeid", namespace = "")
    private String nextNodeid;
    @XmlElement(name = "nextUserList", namespace = "")
    private String nextUserList;
    @XmlElement(name = "userid", namespace = "")
    private String userid;
    @XmlElement(name = "remark", namespace = "")
    private String remark;
    @XmlElement(name = "sysid", namespace = "")
    private String sysid;
    @XmlElement(name = "syspwd", namespace = "")
    private String syspwd;

    /**
     * 
     * @return
     *     returns String
     */
    public String getDocXml() {
        return this.docXml;
    }

    /**
     * 
     * @param docXml
     *     the value for the docXml property
     */
    public void setDocXml(String docXml) {
        this.docXml = docXml;
    }

    /**
     * 
     * @return
     *     returns String
     */
    public String getActionid() {
        return this.actionid;
    }

    /**
     * 
     * @param actionid
     *     the value for the actionid property
     */
    public void setActionid(String actionid) {
        this.actionid = actionid;
    }

    /**
     * 
     * @return
     *     returns String
     */
    public String getProcessid() {
        return this.processid;
    }

    /**
     * 
     * @param processid
     *     the value for the processid property
     */
    public void setProcessid(String processid) {
        this.processid = processid;
    }

    /**
     * 
     * @return
     *     returns String
     */
    public String getDocUnid() {
        return this.docUnid;
    }

    /**
     * 
     * @param docUnid
     *     the value for the docUnid property
     */
    public void setDocUnid(String docUnid) {
        this.docUnid = docUnid;
    }

    /**
     * 
     * @return
     *     returns String
     */
    public String getNextNodeid() {
        return this.nextNodeid;
    }

    /**
     * 
     * @param nextNodeid
     *     the value for the nextNodeid property
     */
    public void setNextNodeid(String nextNodeid) {
        this.nextNodeid = nextNodeid;
    }

    /**
     * 
     * @return
     *     returns String
     */
    public String getNextUserList() {
        return this.nextUserList;
    }

    /**
     * 
     * @param nextUserList
     *     the value for the nextUserList property
     */
    public void setNextUserList(String nextUserList) {
        this.nextUserList = nextUserList;
    }

    /**
     * 
     * @return
     *     returns String
     */
    public String getUserid() {
        return this.userid;
    }

    /**
     * 
     * @param userid
     *     the value for the userid property
     */
    public void setUserid(String userid) {
        this.userid = userid;
    }

    /**
     * 
     * @return
     *     returns String
     */
    public String getRemark() {
        return this.remark;
    }

    /**
     * 
     * @param remark
     *     the value for the remark property
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

    /**
     * 
     * @return
     *     returns String
     */
    public String getSysid() {
        return this.sysid;
    }

    /**
     * 
     * @param sysid
     *     the value for the sysid property
     */
    public void setSysid(String sysid) {
        this.sysid = sysid;
    }

    /**
     * 
     * @return
     *     returns String
     */
    public String getSyspwd() {
        return this.syspwd;
    }

    /**
     * 
     * @param syspwd
     *     the value for the syspwd property
     */
    public void setSyspwd(String syspwd) {
        this.syspwd = syspwd;
    }

}
