
package cn.linkey.ws.server.jaxws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "runRule", namespace = "http://server.ws.linkey.cn/")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "runRule", namespace = "http://server.ws.linkey.cn/", propOrder = {
    "rulenum",
    "params",
    "userid",
    "sysid",
    "syspwd"
})
public class RunRule {

    @XmlElement(name = "rulenum", namespace = "")
    private String rulenum;
    @XmlElement(name = "params", namespace = "")
    private String params;
    @XmlElement(name = "userid", namespace = "")
    private String userid;
    @XmlElement(name = "sysid", namespace = "")
    private String sysid;
    @XmlElement(name = "syspwd", namespace = "")
    private String syspwd;

    /**
     * 
     * @return
     *     returns String
     */
    public String getRulenum() {
        return this.rulenum;
    }

    /**
     * 
     * @param rulenum
     *     the value for the rulenum property
     */
    public void setRulenum(String rulenum) {
        this.rulenum = rulenum;
    }

    /**
     * 
     * @return
     *     returns String
     */
    public String getParams() {
        return this.params;
    }

    /**
     * 
     * @param params
     *     the value for the params property
     */
    public void setParams(String params) {
        this.params = params;
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
