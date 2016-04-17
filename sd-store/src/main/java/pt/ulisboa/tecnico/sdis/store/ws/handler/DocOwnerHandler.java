package pt.ulisboa.tecnico.sdis.store.ws.handler;

import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

public class DocOwnerHandler implements SOAPHandler<SOAPMessageContext> {
	
    public static final String RESPONSE_TYPE = "response.type.propery";
    public static final String DOC_OWNER = "loadResponse.owner.doc";
    
    public Set<QName> getHeaders() {
        return null;
    }

    public boolean handleMessage(SOAPMessageContext smc) {
        InsertDocOwner(smc);
        return true;
    }

    public boolean handleFault(SOAPMessageContext smc) {
    	InsertDocOwner(smc);
        return true;
    }

    // nothing to clean up
    public void close(MessageContext messageContext) {
    }
    
    private void InsertDocOwner(SOAPMessageContext smc) {
    	Boolean outbound = (Boolean) smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
    	if (outbound) {
    		String isLoad = (String) smc.get(RESPONSE_TYPE);
        	String owner = (String) smc.get(DOC_OWNER);
        	if (isLoad != null) {
        		if (isLoad.equals("loadResponse")) {
        			try {
	                	SOAPMessage message = smc.getMessage();
	            		SOAPPart soapPart = message.getSOAPPart();
	                	SOAPEnvelope soapEnvelope = soapPart.getEnvelope();
						SOAPBody soapBody = soapEnvelope.getBody();
		            	if (soapBody.getFirstChild().getNodeName().equals("ns2:loadResponse")) {
		            		Name name = soapEnvelope.createName("DocOwner");
		            		SOAPBodyElement el = soapBody.addBodyElement(name); 
		            		el.addTextNode(owner);
		            		soapBody.getFirstChild().appendChild(el);
		            	}
					} catch (SOAPException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
        		}
        	}
    	}
    }
}