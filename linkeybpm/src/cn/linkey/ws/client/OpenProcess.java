package cn.linkey.ws.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for openProcess complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="openProcess">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="docXml" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="processid" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="docUnid" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
@XmlType(name = "openProcess", propOrder = { "docXml", "processid", "docUnid", "userid", "sysid", "syspwd" })
public class OpenProcess {

    protected String docXml;
    protected String processid;
    protected String docUnid;
    protected String userid;
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
