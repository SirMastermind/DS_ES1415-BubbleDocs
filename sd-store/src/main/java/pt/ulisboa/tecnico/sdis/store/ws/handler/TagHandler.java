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
import javax.xml.ws.handler.MessageContext.Scope;
import javax.xml.ws.handler.soap.*;

import org.w3c.dom.Node;

import pt.ulisboa.tecnico.sdis.store.domain.Tag;

public class TagHandler implements SOAPHandler<SOAPMessageContext> {
	
	public static final String REQUEST_PROPERTY = "my.request.property";
    public static final String RESPONSE_PROPERTY = "my.response.property";

    public static final String REQUEST_HEADER = "myRequestHeader";
    public static final String REQUEST_NS = "urn:example";

    public static final String RESPONSE_HEADER = "myResponseHeader";
    public static final String RESPONSE_NS = REQUEST_NS;
    
    public static final String RESPONSE_TYPE = "response.type.propery";
    
    public Set<QName> getHeaders() {
        return null;
    }

    public boolean handleMessage(SOAPMessageContext smc) {
        HandleDocumentTags(smc);
        return true;
    }

    public boolean handleFault(SOAPMessageContext smc) {
    	HandleDocumentTags(smc);
        return true;
    }

    // nothing to clean up
    public void close(MessageContext messageContext) {
    }

    private void HandleDocumentTags(SOAPMessageContext smc) {
        Boolean outbound = (Boolean) smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
        if (outbound) {
        	String responseType = (String) smc.get(RESPONSE_TYPE);
        	String propertyValue = (String) smc.get(RESPONSE_PROPERTY);
        	if (responseType != null) {
	        	if (responseType.equals("loadResponse")) {
	                try {
	                	SOAPMessage message = smc.getMessage();
	            		SOAPPart soapPart = message.getSOAPPart();
	                	SOAPEnvelope soapEnvelope = soapPart.getEnvelope();
						SOAPBody soapBody = soapEnvelope.getBody();
		            	if (soapBody.getFirstChild().getNodeName().equals("ns2:loadResponse")) {
		            		Name name = soapEnvelope.createName("tag");
		            		SOAPBodyElement el = soapBody.addBodyElement(name); 
		            		el.addTextNode(propertyValue);
		            		soapBody.getFirstChild().appendChild(el);
		            	}
					} catch (SOAPException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	        	}
	        	else {
	        		
	        	}
        	}

        } else {
        	try {
            	SOAPMessage message = smc.getMessage();
        		SOAPPart soapPart = message.getSOAPPart();
            	SOAPEnvelope soapEnvelope = soapPart.getEnvelope();
				SOAPBody soapBody = soapEnvelope.getBody();
				Node tag = null;
            	if (soapBody.getFirstChild().getNodeName().equals("ns2:store")) {
            		for(Node n = soapBody.getFirstChild().getFirstChild(); n != null; n = n.getNextSibling()) {
            			if(n.getNodeName().equals("tag")) {
            				tag = n;
            				break;
            			}
            		}
            		if (tag != null) {
	            		String s = tag.getTextContent();
	            		if(!s.matches("[0-9]+,[0-9]+")) {
	            			System.out.println("Wrong parameter: Tag");
	            		} 
	            		else {
	            			String[] sx = s.split(",");
	            			Tag t = new Tag(Integer.parseInt(sx[0]), Integer.parseInt(sx[1]));
	            			smc.put(REQUEST_PROPERTY, t);
		            		smc.setScope(REQUEST_PROPERTY, Scope.APPLICATION);
	            		}
            		}
            	}
			} catch (SOAPException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
    }

}
