package pt.ulisboa.tecnico.sdis.store.ws.handler;

import java.io.StringWriter;
import java.security.Key;
import java.util.*;

import javax.xml.namespace.QName;
import javax.xml.soap.*;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.MessageContext.Scope;
import javax.xml.ws.handler.soap.*;

import pt.ulisboa.tecnico.sdis.protocol.Communication.*;
import pt.ulisboa.tecnico.sdis.protocol.Domain.*;
import pt.ulisboa.tecnico.sdis.protocol.toolkit.DataConvertor;
import pt.ulisboa.tecnico.sdis.protocol.toolkit.Encrypt;

public class KerberosHandler implements SOAPHandler<SOAPMessageContext> {

    public static final String REQUEST_PROPERTY_SKEY = "sKey";
    public static final String PROPERTY_TREQ = "tReq";
    public static final String PROPERTY_CSKEY = "csKey";

    public static final String REQUEST_HEADER = "sdStoreClientRequest";
    public static final String REQUEST_NS = "urn:store-ws";

    public static final String RESPONSE_HEADER = "sdStoreResponse";
    public static final String RESPONSE_NS = REQUEST_NS;
    

    public boolean handleMessage(SOAPMessageContext smc) {
        Boolean outbound = (Boolean) smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
        if (outbound) {
        	
           handleOutboundMessage(smc);

        } else {
        	
        	handleInboundMessage(smc);
        }

        return true;
    }

    public boolean handleFault(SOAPMessageContext smc) {
        return true;
    }

    public Set<QName> getHeaders() {
        return null;
    }

    public void close(MessageContext messageContext) {
    }
    
    
    
    public String getSOAPBodyContent(SOAPMessage sm) throws SOAPException, TransformerConfigurationException, TransformerException, TransformerFactoryConfigurationError{
    	
		SOAPBody body = sm.getSOAPBody();
	
		//getting body string
        DOMSource source = new DOMSource(body);
        StringWriter stringResult = new StringWriter();
        TransformerFactory.newInstance().newTransformer().transform(source, new StreamResult(stringResult));
        String bodyString = stringResult.toString();
		return bodyString;
	}
    
    
    
    public void handleInboundMessage(SOAPMessageContext smc) throws RuntimeException{
    	
    	Key sKey = Encrypt.generateMD5Key("SERVER-ID");
    	
        
        try {
            // get SOAP envelope header
            SOAPMessage msg = smc.getMessage();
            SOAPPart sp = msg.getSOAPPart();
            SOAPEnvelope se = sp.getEnvelope();
            SOAPHeader sh = se.getHeader();

            // check header
            if (sh == null) {
                throw new RuntimeException();
            }

            // get first header element
            Name name = se.createName(REQUEST_HEADER, "", REQUEST_NS);
            @SuppressWarnings("rawtypes")
			Iterator it = sh.getChildElements(name);
            // check header element
            if (!it.hasNext()) {
                throw new RuntimeException();
            }
            SOAPElement element = (SOAPElement) it.next();

           
            // get header element value
            String xmlReceived = element.getValue();
            byte[] requestXMLBytes = DataConvertor.getBytesFromBase64String(xmlReceived);

            
            CSRequest request = CommunicationHandler.parseCSRequest(requestXMLBytes, sKey);
            
            Ticket ticket = request.getTicket();
            Authentication auth = request.getAuth();
            HMAC hmac = request.getHmac();
            Date tReq = request.gettReq();
            
            // put csKey in  context to get it later
            smc.put(PROPERTY_CSKEY, DataConvertor.keyToString(ticket.getCsKey()));
            // set property scope to application so that client class can access property
            smc.setScope(PROPERTY_CSKEY, Scope.APPLICATION);
            
            
            /*System.out.println("Ticket ID = " + ticket.getClientID() + "\nAuth ID = " + 
            		auth.getClientID() + "\ntReq = " + DataConvertor.dateToString(tReq) + 
            		"\nTicket T1: " + DataConvertor.dateToString(ticket.getT1()) + 
            		"\nTicket T2: " + DataConvertor.dateToString(ticket.getT2()));*/
            		
            
           if(	!ticket.getClientID().equals(auth.getClientID()) || (tReq.compareTo(auth.gettRequest()) != 0) ||
        	   !tReq.after(ticket.getT1()) || !tReq.before(ticket.getT2()) ){
        	   // invalid authentication ...
        	   System.out.println("Invalid authentication");
        	   throw new RuntimeException();
           }
           
          
           String bodyString = getSOAPBodyContent(msg);
           
           byte[] bodyReceived = Encrypt.generateMD5(bodyString);
           
           //System.out.println("\n\n"+ bodyString);
           
           if(!hmac.isValid(bodyReceived)){
        	   //integrity violated
        	   System.out.println("Integrity violated");
        	   throw new RuntimeException();
           }
        	            	  
            
           // put tReq in  context
            smc.put(PROPERTY_TREQ, xmlReceived);
            // set property scope to application so that client class can access property
            smc.setScope(PROPERTY_TREQ, Scope.APPLICATION);
            
        } catch (Exception e){
        	throw new RuntimeException();
        }

    }
    
    
    public void handleOutboundMessage(SOAPMessageContext smc) throws RuntimeException{
    	
    	 String stringTReq = (String) smc.get(PROPERTY_TREQ);
         

         try {
             // get SOAP envelope
             SOAPMessage msg = smc.getMessage();
             SOAPPart sp = msg.getSOAPPart();
             SOAPEnvelope se = sp.getEnvelope();

             // add header
             SOAPHeader sh = se.getHeader();
             if (sh == null)
                 sh = se.addHeader();

             // add header element (name, namespace prefix, namespace)
             Name name = se.createName(RESPONSE_HEADER, "", RESPONSE_NS);
             SOAPHeaderElement element = sh.addHeaderElement(name);

             String strkey = (String) smc.get(PROPERTY_CSKEY);
             Key csKey = DataConvertor.stringToKey(strkey);	
             
             byte[] response = CommunicationHandler.createSResponse(DataConvertor.stringToDate(stringTReq), csKey);
             String xmlToSend = DataConvertor.getStringFromBytes(response);
             
             // add header element value
             element.addTextNode(xmlToSend);


         } catch (Exception e){
         	throw new RuntimeException();
         }
    }
    

}
