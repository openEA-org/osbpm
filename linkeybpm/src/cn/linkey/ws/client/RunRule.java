package cn.linkey.ws.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for runRule complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="runRule">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="rulenum" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="params" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="userid" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
@XmlType(name = "runRule", propOrder = { "rulenum", "params", "userid", "sysid", "syspwd" })
public class RunRule {

    protected String rulenum;
    protected String params;
    protected String userid;
    protected String sysid;
    protected String syspwd;

    /**
     * Gets the value of the rulenum property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getRulenum() {
        return rulenum;
    }

    /**
     * Sets the value of the rulenum property.
     * 
     * @param value allowed object is {@link String }
     * 
     */
    public void setRulenum(String value) {
        this.rulenum = value;
    }

    /**
     * Gets the value of the params property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getParams() {
        return params;
    }

    /**
     * Sets the value of the params property.
     * 
     * @param value allowed object is {@link String }
     * 
     */
    public void setParams(String value) {
        this.params = value;
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
