package pt.ulisboa.tecnico.sdis.ws.handler;

import java.io.StringWriter;
import java.security.Key;
import java.util.*;

import javax.xml.namespace.QName;
import javax.xml.soap.*;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.MessageContext.Scope;
import javax.xml.ws.handler.soap.*;

import pt.ulisboa.tecnico.sdis.PersistenceManager.PersistenceManager;
import pt.ulisboa.tecnico.sdis.protocol.Communication.CommunicationHandler;
import pt.ulisboa.tecnico.sdis.protocol.Communication.SResponse;
import pt.ulisboa.tecnico.sdis.protocol.Domain.Authentication;
import pt.ulisboa.tecnico.sdis.protocol.Domain.HMAC;
import pt.ulisboa.tecnico.sdis.protocol.Domain.KerberosFactory;
import pt.ulisboa.tecnico.sdis.protocol.toolkit.DataConvertor;
import pt.ulisboa.tecnico.sdis.protocol.toolkit.Encrypt;

public class KerberosHandler implements SOAPHandler<SOAPMessageContext> {

	public static final String REQUEST_PROPERTY_USERNAME = "client-username";
    
    public static final String PROPERTY_TREQ = "tReq";

    public static final String REQUEST_HEADER = "sdStoreClientRequest";
    public static final String REQUEST_NS = "urn:store-ws";

    public static final String RESPONSE_HEADER = "sdStoreResponse";
    public static final String RESPONSE_NS = REQUEST_NS;
    
    private Date date;
   

    public boolean handleMessage(SOAPMessageContext smc) {
    	
        Boolean outbound = (Boolean) smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
        date = new Date();
        
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

    
    
    public void handleOutboundMessage(SOAPMessageContext smc) throws RuntimeException {
    	

    	//gets client username and password  from stubs
    	String clientUsername = (String) smc.get(REQUEST_PROPERTY_USERNAME);
            
        
        try{
        	
       	 	// get SOAP envelope
            SOAPMessage msg = smc.getMessage();
            SOAPPart sp = msg.getSOAPPart();
            SOAPEnvelope se = sp.getEnvelope();
            
            //get SOAP body
            SOAPBody body = msg.getSOAPBody();  
            
            //getting body string
            DOMSource source = new DOMSource(body);
            StringWriter stringResult = new StringWriter();
            TransformerFactory.newInstance().newTransformer().transform(source, new StreamResult(stringResult));
            String bodyString = stringResult.toString();
        	
            String strencodedticket = PersistenceManager.getInfo(CommunicationHandler.TICKET_TAG);
            byte[] encodedTicket = DataConvertor.getBytesFromBase64String(strencodedticket);
            Authentication auth = KerberosFactory.createAuthentication(clientUsername, DataConvertor.dateToString(this.date));
            HMAC hmac = KerberosFactory.createHMAC(Encrypt.generateMD5(bodyString));
            String strkey = PersistenceManager.getInfo(CommunicationHandler.CS_KEY_TAG);
            Key csKey = DataConvertor.stringToKey(strkey);	
            
            		
            byte[] request = CommunicationHandler.createCSRequest(encodedTicket, auth, hmac, date, csKey);
            String xmlToSend = DataConvertor.getStringFromBytes(request);

           
            // add header
            SOAPHeader sh = se.getHeader();
            if (sh == null){
                sh = se.addHeader();
            }

            // add header element (name, namespace prefix, namespace)
            Name name = se.createName(REQUEST_HEADER, "", REQUEST_NS);
            SOAPHeaderElement element = sh.addHeaderElement(name);         
            
            // add header element value
            element.addTextNode(xmlToSend);
            
            //put tReq in response context so that later can get it
            smc.put(PROPERTY_TREQ, DataConvertor.dateToString(date));
            // set property scope to application so that client class can access property
            smc.setScope(PROPERTY_TREQ, Scope.APPLICATION);

        } catch (Exception e) {
        	throw new RuntimeException();
        }

    }
    
    public void handleInboundMessage(SOAPMessageContext smc) throws RuntimeException {
    	

        // get xmlReceived from response SOAP header
        try {
        	
        	String strkey = PersistenceManager.getInfo(CommunicationHandler.CS_KEY_TAG);
            Key csKey = DataConvertor.stringToKey(strkey);
            
            
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
            Name name = se.createName(RESPONSE_HEADER, "", RESPONSE_NS);
            @SuppressWarnings("rawtypes")
			Iterator it = sh.getChildElements(name);
            
            // check header element
            if (!it.hasNext()) {
                throw new RuntimeException();
            }
            SOAPElement element = (SOAPElement) it.next();

           
            // get header element value
            String xmlReceived = element.getValue();
            byte[] responseXMLBytes = DataConvertor.getBytesFromBase64String(xmlReceived);
            SResponse response = CommunicationHandler.parseSResponse(responseXMLBytes, csKey); //handle exceptions
            
            Date tReqResponse = response.gettReq();
            
            Date expectedTReq = DataConvertor.stringToDate((String) smc.get(PROPERTY_TREQ));
       
            if( tReqResponse.compareTo(expectedTReq) != 0 ){
            	throw new RuntimeException();
            }
            
        } catch (Exception e) {
        	throw new RuntimeException();
		}
    }
     
}
