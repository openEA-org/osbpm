package cn.linkey.ws.client;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

/**
 * This object contains factory methods for each Java content interface and Java element interface generated in the cn.linkey.ws.client package.
 * <p>
 * An ObjectFactory allows you to programatically construct new instances of the Java representation for XML content. The Java representation of XML content can consist of schema derived interfaces
 * and classes representing the binding of schema type definitions, element declarations and model groups. Factory methods for each of these are provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _OpenProcess_QNAME = new QName("http://server.ws.linkey.cn/", "openProcess");
    private final static QName _OpenProcessResponse_QNAME = new QName("http://server.ws.linkey.cn/", "openProcessResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: cn.linkey.ws.client
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link OpenProcessResponse }
     * 
     */
    public OpenProcessResponse createOpenProcessResponse() {
        return new OpenProcessResponse();
    }

    /**
     * Create an instance of {@link OpenProcess }
     * 
     */
    public OpenProcess createOpenProcess() {
        return new OpenProcess();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link OpenProcess } {@code >}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.linkey.cn/", name = "openProcess")
    public JAXBElement<OpenProcess> createOpenProcess(OpenProcess value) {
        return new JAXBElement<OpenProcess>(_OpenProcess_QNAME, OpenProcess.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <} {@link OpenProcessResponse }{@code >}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.linkey.cn/", name = "openProcessResponse")
    public JAXBElement<OpenProcessResponse> createOpenProcessResponse(OpenProcessResponse value) {
        return new JAXBElement<OpenProcessResponse>(_OpenProcessResponse_QNAME, OpenProcessResponse.class, null, value);
    }

}
