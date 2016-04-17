package pt.ulisboa.tecnico.sdis.store;

import java.io.*;
import java.security.Key;
import java.util.*;

import javax.xml.soap.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import javax.xml.ws.handler.*;
import javax.xml.ws.handler.MessageContext.Scope;
import javax.xml.ws.handler.soap.*;

import org.junit.*;

import pt.ulisboa.tecnico.sdis.protocol.Communication.CommunicationHandler;
import pt.ulisboa.tecnico.sdis.protocol.Communication.SResponse;
import pt.ulisboa.tecnico.sdis.protocol.Domain.Authentication;
import pt.ulisboa.tecnico.sdis.protocol.Domain.HMAC;
import pt.ulisboa.tecnico.sdis.protocol.Domain.KerberosFactory;
import pt.ulisboa.tecnico.sdis.protocol.Domain.Ticket;
import pt.ulisboa.tecnico.sdis.protocol.Exception.InvalidDataException;
import pt.ulisboa.tecnico.sdis.protocol.Exception.KerberosException;
import pt.ulisboa.tecnico.sdis.protocol.Messages.EncryptedXMLMessage;
import pt.ulisboa.tecnico.sdis.protocol.Messages.Message;
import pt.ulisboa.tecnico.sdis.protocol.toolkit.DataConvertor;
import pt.ulisboa.tecnico.sdis.protocol.toolkit.Encrypt;
import pt.ulisboa.tecnico.sdis.store.ws.handler.KerberosHandler;
import static org.junit.Assert.*;
import mockit.*;

public class KerberosHandlerTest extends AbstractHandlerTest {

	public static final String REQUEST_HEADER = "sdStoreClientRequest";
	public static final String REQUEST_PROPERTY_USERNAME = "client-username";
	public static final String PROPERTY_TREQ = "tReq";
	public static final String PROPERTY_CSKEY = "csKey";
	public static final String REQUEST_NS = "urn:store-ws";
	public static final String RESPONSE_HEADER = "sdStoreResponse";
    public static final String RESPONSE_NS = REQUEST_NS;
	
	private static final String CLIENTID = "andriymz";
	private static final int SERVERID = 321;
	
	private static String stringT1;
	private static String stringT2;
	private static final Key csKey = Encrypt.generateKey();
	private static final Key sKey = Encrypt.generateMD5Key("SERVER-ID");
	private static Ticket ticket; 
	private static Authentication auth;
	
	private static Date DATE_TREQ = getExpiresInXhoursDate(1);
	
	private static byte[] encodedTicket;
	

	
	 // one-time initialization and clean-up

    @BeforeClass
    public static void oneTimeSetUp() {
    	
    	try {
			stringT1 = DataConvertor.dateToString(new Date());
			stringT2 = DataConvertor.dateToString(getExpiresInXhoursDate(3));
			ticket = KerberosFactory.createTicket(CLIENTID, SERVERID, stringT1, stringT2, csKey);
			encodedTicket = getXMLTicketBytes(ticket);
			
			auth = KerberosFactory.createAuthentication(CLIENTID, DataConvertor.dateToString(DATE_TREQ));
			
		} catch (InvalidDataException e) {
			//nothing to do 
		} catch (KerberosException e) {
			//nothing to do
		}
    
    }

    @AfterClass
    public static void oneTimeTearDown() {

    }
	
	public static Date getExpiresInXhoursDate(int hoursToExpire){
		Calendar cal = Calendar.getInstance();
	    cal.setTime(new Date());
	    cal.add(Calendar.HOUR_OF_DAY, hoursToExpire); 
	    return cal.getTime();
	}
	
	public static byte[] getXMLTicketBytes(Ticket ticket) throws KerberosException{
		
		Message ticketMessage = new EncryptedXMLMessage(sKey);
		
		ticketMessage.addElement(CommunicationHandler.CLIENT_TAG, ticket.getClientID());
		ticketMessage.addElement(CommunicationHandler.SERVER_TAG, Integer.toString(ticket.getServerID()));
		ticketMessage.addElement(CommunicationHandler.T1_TAG, DataConvertor.dateToString(ticket.getT1()));
		ticketMessage.addElement(CommunicationHandler.T2_TAG, DataConvertor.dateToString(ticket.getT2()));
		ticketMessage.addElement(CommunicationHandler.CS_KEY_TAG, DataConvertor.keyToString(ticket.getCsKey()));
	
		return ticketMessage.getTransmissionData();
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
		
    @Test
    public void successAtHandlerOutbound(
        @Mocked final SOAPMessageContext soapMessageContext)
        throws Exception {

        final String soapText = SOAP_RESPONSE;

        final SOAPMessage soapMessage = byteArrayToSOAPMessage(soapText.getBytes());
        final Boolean soapOutbound = true;

        // an "expectation block"
        // One or more invocations to mocked types, causing expectations to be recorded.
        new StrictExpectations() {{
            soapMessageContext.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
            result = soapOutbound;
            
            soapMessageContext.get(PROPERTY_TREQ);
            result = DataConvertor.dateToString(DATE_TREQ);
            
            soapMessageContext.getMessage();
            result = soapMessage;
            
            soapMessageContext.get(PROPERTY_CSKEY);
            result = DataConvertor.keyToString(csKey);
            
        }};
        
        

        //assert handleMessage success (true return)
        KerberosHandler handler = new KerberosHandler();
        boolean handleResult = handler.handleMessage(soapMessageContext);
        assertTrue(handleResult);

        // assert header
        SOAPPart soapPart = soapMessage.getSOAPPart();
        SOAPEnvelope soapEnvelope = soapPart.getEnvelope();
        SOAPHeader soapHeader = soapEnvelope.getHeader();
        assertNotNull(soapHeader);
        

        // assert header element
        Name name = soapEnvelope.createName(RESPONSE_HEADER, "", RESPONSE_NS);
        @SuppressWarnings("rawtypes")
		Iterator it = soapHeader.getChildElements(name);
        assertTrue(it.hasNext());
       
        // assert header element value
        SOAPElement element = (SOAPElement) it.next();
        String valueString = element.getValue();
        byte[] headerResponse = DataConvertor.getBytesFromBase64String(valueString);
       
        //to ensure that header value is valid
        SResponse receivedResponse = CommunicationHandler.parseSResponse(headerResponse, csKey);
        
        //assert that tReq being sent is correct
        assertEquals(DataConvertor.dateToString(receivedResponse.gettReq()),DataConvertor.dateToString(DATE_TREQ));

    }

    
    /*
     *  Test 1: success at inbound message
     */
    @Test
    public void successAtHandlerInbound(
    	@Mocked final SOAPMessageContext soapMessageContext)
    			throws Exception {

    	String bodyContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><S:Body xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\"><ns2:sayHello xmlns:ns2=\"http://ws.example/\"><arg0>friend</arg0></ns2:sayHello></S:Body>"; 
    	
    	HMAC hmac = KerberosFactory.createHMAC(Encrypt.generateMD5(bodyContent));
    	
    	byte[] request = CommunicationHandler.createCSRequest(encodedTicket, auth, hmac, DATE_TREQ, csKey);
    	String xmlEncrypted = DataConvertor.getStringFromBytes(request);
    	
        // Preparation code not specific to JMockit, if any.
        final String soapText = SOAP_REQUEST.replace("<SOAP-ENV:Header/>",
            "<SOAP-ENV:Header><" + REQUEST_HEADER + " xmlns=\"" + REQUEST_NS + "\">"
            + xmlEncrypted + "</" + REQUEST_HEADER + ">" + 
            "</SOAP-ENV:Header>");
        System.out.println(soapText);

        final SOAPMessage soapMessage = byteArrayToSOAPMessage(soapText.getBytes());
        final Boolean soapOutbound = false;
        

        // an "expectation block"
        // One or more invocations to mocked types, causing expectations to be recorded.
        new StrictExpectations() {{
        	
	    	soapMessageContext.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
	        result = soapOutbound;
          
            soapMessageContext.getMessage();
            result = soapMessage;
            
            soapMessageContext.put(PROPERTY_CSKEY, anyString);
            soapMessageContext.setScope(PROPERTY_CSKEY, Scope.APPLICATION);

            soapMessageContext.put(PROPERTY_TREQ, anyString);
            soapMessageContext.setScope(PROPERTY_TREQ, Scope.APPLICATION);
        }};
        

        // Unit under test is exercised.
        KerberosHandler handler = new KerberosHandler();
        boolean handleResult = handler.handleMessage(soapMessageContext);


        // assert that message would proceed normally
       assertTrue(handleResult);
       
    }
    
    
    /*
     * Test 2: fail at inbound message with bad TReq (isn't between Ticket session period limits)
     */
    
    @Test(expected=RuntimeException.class)
    public void failAtHandlerInboundBadTReq(
    	@Mocked final SOAPMessageContext soapMessageContext)
    			throws Exception {

    	String bodyContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><S:Body xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\"><ns2:sayHello xmlns:ns2=\"http://ws.example/\"><arg0>friend</arg0></ns2:sayHello></S:Body>"; 
    	
    	HMAC hmac = KerberosFactory.createHMAC(Encrypt.generateMD5(bodyContent));
    	
    	Date date = getExpiresInXhoursDate(5);	// Ticket expires in 3 hours
    	Authentication auxAuth = KerberosFactory.createAuthentication(CLIENTID, DataConvertor.dateToString(date));
    	
    	byte[] request = CommunicationHandler.createCSRequest(encodedTicket, auxAuth, hmac, date, csKey);
    	String xmlEncrypted = DataConvertor.getStringFromBytes(request);
    	
        // Preparation code not specific to JMockit, if any.
        final String soapText = SOAP_REQUEST.replace("<SOAP-ENV:Header/>",
            "<SOAP-ENV:Header><" + REQUEST_HEADER + " xmlns=\"" + REQUEST_NS + "\">"
            + xmlEncrypted + "</" + REQUEST_HEADER + ">" + 
            "</SOAP-ENV:Header>");
        System.out.println(soapText);

        final SOAPMessage soapMessage = byteArrayToSOAPMessage(soapText.getBytes());
        final Boolean soapOutbound = false;
        

        // an "expectation block"
        // One or more invocations to mocked types, causing expectations to be recorded.
        new StrictExpectations() {{
        	
	    	soapMessageContext.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
	        result = soapOutbound;
          
            soapMessageContext.getMessage();
            result = soapMessage;
            
            soapMessageContext.put(PROPERTY_CSKEY, anyString);
            soapMessageContext.setScope(PROPERTY_CSKEY, Scope.APPLICATION);

        }};
        

        // Unit under test is exercised.
        KerberosHandler handler = new KerberosHandler();
        handler.handleMessage(soapMessageContext);
       
    }
    
    /*
     * Test 2: fail at inbound message with bad TReq ( is different from Auth TReq)
     */
    
    @Test(expected=RuntimeException.class)
    public void failAtHandlerInboundBadTReq2(
    	@Mocked final SOAPMessageContext soapMessageContext)
    			throws Exception {

    	String bodyContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><S:Body xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\"><ns2:sayHello xmlns:ns2=\"http://ws.example/\"><arg0>friend</arg0></ns2:sayHello></S:Body>"; 
    	
    	HMAC hmac = KerberosFactory.createHMAC(Encrypt.generateMD5(bodyContent));
    	
    	Date date = getExpiresInXhoursDate(5);	// Ticket expires in 3 hours
    	Authentication auxAuth = KerberosFactory.createAuthentication(CLIENTID, DataConvertor.dateToString(date));
    	
    	byte[] request = CommunicationHandler.createCSRequest(encodedTicket, auxAuth, hmac, date, csKey);
    	String xmlEncrypted = DataConvertor.getStringFromBytes(request);
    	
        // Preparation code not specific to JMockit, if any.
        final String soapText = SOAP_REQUEST.replace("<SOAP-ENV:Header/>",
            "<SOAP-ENV:Header><" + REQUEST_HEADER + " xmlns=\"" + REQUEST_NS + "\">"
            + xmlEncrypted + "</" + REQUEST_HEADER + ">" + 
            "</SOAP-ENV:Header>");
        System.out.println(soapText);

        final SOAPMessage soapMessage = byteArrayToSOAPMessage(soapText.getBytes());
        final Boolean soapOutbound = false;
        

        // an "expectation block"
        // One or more invocations to mocked types, causing expectations to be recorded.
        new StrictExpectations() {{
        	
	    	soapMessageContext.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
	        result = soapOutbound;
          
            soapMessageContext.getMessage();
            result = soapMessage;
            
            soapMessageContext.put(PROPERTY_CSKEY, anyString);
            soapMessageContext.setScope(PROPERTY_CSKEY, Scope.APPLICATION);

        }};
        

        // Unit under test is exercised.
        KerberosHandler handler = new KerberosHandler();
        handler.handleMessage(soapMessageContext);
       
    }
    
    /*
     * Test 2: fail at inbound message with bad HMAC
     */
    
    @Test(expected=RuntimeException.class)
    public void failAtHandlerInboundBadTReq3(
    	@Mocked final SOAPMessageContext soapMessageContext)
    			throws Exception {

    	String bodyContent = "bad hmac lel"; 
    	
    	HMAC hmac = KerberosFactory.createHMAC(Encrypt.generateMD5(bodyContent));
    	
    	Authentication auxAuth = KerberosFactory.createAuthentication(CLIENTID, DataConvertor.dateToString(DATE_TREQ));
    	
    	byte[] request = CommunicationHandler.createCSRequest(encodedTicket, auxAuth, hmac, DATE_TREQ, csKey);
    	String xmlEncrypted = DataConvertor.getStringFromBytes(request);
    	
        // Preparation code not specific to JMockit, if any.
        final String soapText = SOAP_REQUEST.replace("<SOAP-ENV:Header/>",
            "<SOAP-ENV:Header><" + REQUEST_HEADER + " xmlns=\"" + REQUEST_NS + "\">"
            + xmlEncrypted + "</" + REQUEST_HEADER + ">" + 
            "</SOAP-ENV:Header>");
        System.out.println(soapText);

        final SOAPMessage soapMessage = byteArrayToSOAPMessage(soapText.getBytes());
        final Boolean soapOutbound = false;
        

        // an "expectation block"
        // One or more invocations to mocked types, causing expectations to be recorded.
        new StrictExpectations() {{
        	
	    	soapMessageContext.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
	        result = soapOutbound;
          
            soapMessageContext.getMessage();
            result = soapMessage;
            
            soapMessageContext.put(PROPERTY_CSKEY, anyString);
            soapMessageContext.setScope(PROPERTY_CSKEY, Scope.APPLICATION);

        }};
        

        // Unit under test is exercised.
        KerberosHandler handler = new KerberosHandler();
        handler.handleMessage(soapMessageContext);
       
    }
}

