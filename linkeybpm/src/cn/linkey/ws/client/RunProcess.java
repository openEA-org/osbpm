package cn.linkey.ws.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for runProcess complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="runProcess">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="docXml" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="actionid" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="processid" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="docUnid" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="nextNodeid" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="nextUserList" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="userid" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="remark" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sysid" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="syspwd" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "runProcess", propOrder = { "docXml", "actionid", "processid", "docUnid", "nextNodeid", "nextUserList", "userid", "remark", "sysid", "syspwd" })
public class RunProcess {

    protected String docXml;
    protected String actionid;
    protected String processid;
    protected String docUnid;
    protected String nextNodeid;
    protected String nextUserList;
    protected String userid;
    protected String remark;
    protected String sysid;
    protected String syspwd;

    /**
     * Gets the value of the docXml property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getDocXml() {
        return docXml;
    }

    /**
     * Sets the value of the docXml property.
     * 
     * @param value allowed object is {@link String }
     * 
     */
    public void setDocXml(String value) {
        this.docXml = value;
    }

    /**
     * Gets the value of the actionid property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getActionid() {
        return actionid;
    }

    /**
     * Sets the value of the actionid property.
     * 
     * @param value allowed object is {@link String }
     * 
     */
    public void setActionid(String value) {
        this.actionid = value;
    }

    /**
     * Gets the value of the processid property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getProcessid() {
        return processid;
    }

    /**
     * Sets the value of the processid property.
     * 
     * @param value allowed object is {@link String }
     * 
     */
    public void setProcessid(String value) {
        this.processid = value;
    }

    /**
     * Gets the value of the docUnid property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getDocUnid() {
        return docUnid;
    }

    /**
     * Sets the value of the docUnid property.
     * 
     * @param value allowed object is {@link String }
     * 
     */
    public void setDocUnid(String value) {
        this.docUnid = value;
    }

    /**
     * Gets the value of the nextNodeid property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getNextNodeid() {
        return nextNodeid;
    }

    /**
     * Sets the value of the nextNodeid property.
     * 
     * @param value allowed object is {@link String }
     * 
     */
    public void setNextNodeid(String value) {
        this.nextNodeid = value;
    }

    /**
     * Gets the value of the nextUserList property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getNextUserList() {
        return nextUserList;
    }

    /**
     * Sets the value of the nextUserList property.
     * 
     * @param value allowed object is {@link String }
     * 
     */
    public void setNextUserList(String value) {
        this.nextUserList = value;
    }

    /**
     * Gets the value of the userid property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getUserid() {
        return userid;
    }

    /**
     * Sets the value of the userid property.
     * 
     * @param value allowed object is {@link String }
     * 
     */
    public void setUserid(String value) {
        this.userid = value;
    }

    /**
     * Gets the value of the remark property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getRemark() {
        return remark;
    }

    /**
     * Sets the value of the remark property.
     * 
     * @param value allowed object is {@link String }
     * 
     */
    public void setRemark(String value) {
        this.remark = value;
    }

    /**
     * Gets the value of the sysid property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getSysid() {
        return sysid;
    }

    /**
     * Sets the value of the sysid property.
     * 
     * @param value allowed object is {@link String }
     * 
     */
    public void setSysid(String value) {
        this.sysid = value;
    }

    /**
     * Gets the value of the syspwd property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getSyspwd() {
        return syspwd;
    }

    /**
     * Sets the value of the syspwd property.
     * 
     * @param value allowed object is {@link String }
     * 
     */
    public void setSyspwd(String value) {
        this.syspwd = value;
    }

}
