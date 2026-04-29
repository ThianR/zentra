package com.zentra.middleware.sifen.schema;

import jakarta.xml.bind.annotation.*;
import org.w3c.dom.Element;

/**
 * Clase envoltorio para la petición siRecepDE (recepción de DTE síncrona).
 * Utiliza @XmlAnyElement para evitar el escape de caracteres en el tag xDE.
 */
@XmlRootElement(name = "rEnviDe", namespace = "http://ekuatia.set.gov.py/sifen/xsd")
@XmlAccessorType(XmlAccessType.FIELD)
public class REnviDeRequest {

    @XmlElement(namespace = "http://ekuatia.set.gov.py/sifen/xsd")
    private String dId;

    @XmlElement(namespace = "http://ekuatia.set.gov.py/sifen/xsd")
    private XdeWrapper xDE;

    public REnviDeRequest() {
    }

    public REnviDeRequest(String dId, Element rdeElement) {
        this.dId = dId;
        this.xDE = new XdeWrapper(rdeElement);
    }

    public String getdId() {
        return dId;
    }

    public void setdId(String dId) {
        this.dId = dId;
    }

    public XdeWrapper getxDE() {
        return xDE;
    }

    public void setxDE(XdeWrapper xDE) {
        this.xDE = xDE;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class XdeWrapper {
        
        @XmlAnyElement
        private Element xmlNode;

        public XdeWrapper() {
        }

        public XdeWrapper(Element xmlNode) {
            this.xmlNode = xmlNode;
        }

        public Element getXmlNode() {
            return xmlNode;
        }

        public void setXmlNode(Element xmlNode) {
            this.xmlNode = xmlNode;
        }
    }
}
