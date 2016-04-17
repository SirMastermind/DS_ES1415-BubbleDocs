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

import pt.ulisboa.tecnico.sdis.PersistenceManager.PersistenceManager;
import pt.ulisboa.tecnico.sdis.protocol.Communication.CSRequest;
import pt.ulisboa.tecnico.sdis.protocol.Communication.CommunicationHandler;
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
import pt.ulisboa.tecnico.sdis.ws.handler.KerberosHandler;
import static org.junit.Assert.*;
import mockit.*;


public class KerberosHandlerTest extends AbstractHandlerTest {

	public static final String REQUEST_HEADER = "sdStoreClientRequest";
	public static final String REQUEST_PROPERTY_USERNAME = "client-username";
	public static final String PROPERTY_TREQ = "tReq";
	public static final String REQUEST_NS = "urn:store-ws";
	public static final String RESPONSE_HEADER = "sdStoreResponse";
    public static final String RESPONSE_NS = REQUEST_NS;
    private static final int SESSIONTIME = 2*3600*1000;
	private static final String CLIENTID = "andriymz";
	private static final int SERVERID = 321;
	
	private static String stringT1;
	private static String stringT2;
	private static final Key csKey = Encrypt.generateKey();
	private static final Key csKeyBad = Encrypt.generateKey();
	private static final Key sKey = Encrypt.generateMD5Key("SERVER-ID");
	private static final int SESSION_TTL = 2;
	private static Ticket ticket; 
	
	private static Date DATE_TREQ = new Date();
	private static Date INVALID_DATE_TREQ = getExpiresInXhoursDate(1);
	
	private static byte[] encodedTicket;
	

	
	 // one-time initialization and clean-up

    @BeforeClass
    public static void oneTimeSetUp() {
    	
    	try {
			stringT1 = DataConvertor.dateToString(new Date());
			stringT2 = DataConvertor.dateToString(getExpiresInXhoursDate(SESSION_TTL));
			ticket = KerberosFactory.createTicket(CLIENTID, SERVERID, stringT1, stringT2, csKey);
			encodedTicket = getXMLTicketBytes(ticket);
			
			
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
	
	/*
	 * Test that the outbound message has the correct parameters on the header
	 */
	
    @Test
    public void testHeaderHandlerOutbound(
        @Mocked final SOAPMessageContext soapMessageContext, @Mocked final PersistenceManager pm)
        throws Exception {

        final String soapText = SOAP_REQUEST;

        final SOAPMessage soapMessage = byteArrayToSOAPMessage(soapText.getBytes());
        final Boolean soapOutbound = true;

        // an "expectation block"
        // One or more invocations to mocked types, causing expectations to be recorded.
        new StrictExpectations() {{
            soapMessageContext.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
            result = soapOutbound;
            
            soapMessageContext.get(REQUEST_PROPERTY_USERNAME);
            result = CLIENTID;
            
            soapMessageContext.getMessage();
            result = soapMessage;
            
            PersistenceManager.getInfo(CommunicationHandler.TICKET_TAG);
        	result = DataConvertor.getStringFromBytes(encodedTicket);
        	
        	PersistenceManager.getInfo(CommunicationHandler.CS_KEY_TAG);
        	result = DataConvertor.keyToString(csKey);
        	
        	soapMessageContext.put(PROPERTY_TREQ, anyString);
        	soapMessageContext.setScope(PROPERTY_TREQ, Scope.APPLICATION);
        }};
        
        //get SOAP message body
        String bodyContent = getSOAPBodyContent(soapMessage);
        

        //assert handleMessage success (true return)
        KerberosHandler handler = new KerberosHandler();
        boolean handleResult = handler.handleMessage(soapMessageContext);
        assertTrue(handleResult);

        // assert header
        SOAPPart soapPart = soapMessage.getSOAPPart();
        SOAPEnvelope soapEnvelope = soapPart.getEnvelope();
        SOAPHeader soapHeader = soapEnvelope.getHeader();
        assertNotNull(soapHeader);
        

        // assert all header elements
        Name name = soapEnvelope.createName(REQUEST_HEADER, "", REQUEST_NS);
        @SuppressWarnings("rawtypes")
		Iterator it = soapHeader.getChildElements(name);
        assertTrue(it.hasNext());
       
        // assert header element value
        SOAPElement element = (SOAPElement) it.next();
        String valueString = element.getValue();
        byte[] headerRequest = DataConvertor.getBytesFromBase64String(valueString);
       
        //to ensure that header value is valid
        CSRequest receivedRequest = CommunicationHandler.parseCSRequest(headerRequest, sKey);
        
        //get all elements
        HMAC hmac = receivedRequest.getHmac();
        Ticket ticket = receivedRequest.getTicket();
        Authentication auth = receivedRequest.getAuth();
        Date tReq = receivedRequest.gettReq();
        
        //assert hmac transfer (generated from body)
        assertTrue(hmac.isValid(Encrypt.generateMD5(bodyContent)));
        
        //assert tReq
        assertTrue(new Date().getTime() - tReq.getTime() < 10000);
        
        //assert ticket
        assertEquals(CLIENTID, ticket.getClientID());
        assertEquals(SERVERID, ticket.getServerID());
        assertArrayEquals(csKey.getEncoded(), ticket.getCsKey().getEncoded());
        assertEquals(ticket.getT2().getTime() - ticket.getT1().getTime(), SESSIONTIME);
		long minus = new Date().getTime() - ticket.getT1().getTime();
		assertTrue(minus < 10000);
        
        //assert Auth
        assertEquals(CLIENTID, auth.getClientID());
        assertEquals(auth.gettRequest(), tReq);
    }
    

	/*
	 * Test succes when the inbound message has the correct parameters on the header
	 */
    
    @Test
    public void testHeaderHandlerInbound(
        @Mocked final SOAPMessageContext soapMessageContext,  @Mocked final PersistenceManager pm)
        throws Exception {

    	
    	byte[] request = CommunicationHandler.createSResponse(DATE_TREQ, csKey);
    	String xmlEncrypted = DataConvertor.getStringFromBytes(request);
    	
        // Preparation code not specific to JMockit, if any.
        final String soapText = SOAP_REQUEST.replace("<SOAP-ENV:Header/>",
            "<SOAP-ENV:Header><" + RESPONSE_HEADER + " xmlns=\"" + RESPONSE_NS + "\">"
            + xmlEncrypted + "</" + RESPONSE_HEADER + ">" + 
            "</SOAP-ENV:Header>");

        final SOAPMessage soapMessage = byteArrayToSOAPMessage(soapText.getBytes());
        final Boolean soapOutbound = false;

        // an "expectation block"
        // One or more invocations to mocked types, causing expectations to be recorded.
        new StrictExpectations() {{
        	
	    	soapMessageContext.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
	        result = soapOutbound;
             
        	PersistenceManager.getInfo(CommunicationHandler.CS_KEY_TAG);
        	result = DataConvertor.keyToString(csKey);

            soapMessageContext.getMessage();
            result = soapMessage;

            soapMessageContext.get(PROPERTY_TREQ);
            result = DataConvertor.dateToString(DATE_TREQ);
        }};
        

        // Unit under test is exercised.
        KerberosHandler handler = new KerberosHandler();
        boolean handleResult = handler.handleMessage(soapMessageContext);


       // assert that message would proceed normally
       assertTrue(handleResult);
    }
    
    /*
     * Test when inbound message tReq does not match the given one
     */
    
    @Test(expected=RuntimeException.class)
    public void testHeaderHandlerInboundBadTReq(
            @Mocked final SOAPMessageContext soapMessageContext,  @Mocked final PersistenceManager pm)
            throws Exception {

                	
        	byte[] request = CommunicationHandler.createSResponse(INVALID_DATE_TREQ, csKey);
        	String xmlEncrypted = DataConvertor.getStringFromBytes(request);
        	
            // Preparation code not specific to JMockit, if any.
            final String soapText = SOAP_REQUEST.replace("<SOAP-ENV:Header/>",
                "<SOAP-ENV:Header><" + RESPONSE_HEADER + " xmlns=\"" + RESPONSE_NS + "\">"
                + xmlEncrypted + "</" + RESPONSE_HEADER + ">" + 
                "</SOAP-ENV:Header>");

            final SOAPMessage soapMessage = byteArrayToSOAPMessage(soapText.getBytes());
            final Boolean soapOutbound = false;

            // an "expectation block"
            // One or more invocations to mocked types, causing expectations to be recorded.
            new NonStrictExpectations() {{
            	
    	    	soapMessageContext.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
    	        result = soapOutbound;
                 
            	PersistenceManager.getInfo(CommunicationHandler.CS_KEY_TAG);
            	result = DataConvertor.keyToString(csKey);

                soapMessageContext.getMessage();
                result = soapMessage;

                soapMessageContext.get(PROPERTY_TREQ);
                result = DataConvertor.dateToString(DATE_TREQ);

            }};
            

            // Unit under test is exercised.
            KerberosHandler handler = new KerberosHandler();
            handler.handleMessage(soapMessageContext);

    }
    
    
    /*
     * test when the inbound msg has no header
     */
    
    @Test(expected=RuntimeException.class)
    public void testHeaderHandlerInboundBadHeader(
            @Mocked final SOAPMessageContext soapMessageContext,  @Mocked final PersistenceManager pm)
            throws Exception {
        	
            // Preparation code not specific to JMockit, if any.
            final String soapText = SOAP_REQUEST;

            final SOAPMessage soapMessage = byteArrayToSOAPMessage(soapText.getBytes());
            final Boolean soapOutbound = false;

            // an "expectation block"
            // One or more invocations to mocked types, causing expectations to be recorded.
            new NonStrictExpectations() {{
            	
    	    	soapMessageContext.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
    	        result = soapOutbound;
                 
            	PersistenceManager.getInfo(CommunicationHandler.CS_KEY_TAG);
            	result = DataConvertor.keyToString(csKey);

                soapMessageContext.getMessage();
                result = soapMessage;

                soapMessageContext.get(PROPERTY_TREQ);
                result = DataConvertor.dateToString(DATE_TREQ);
                
            }};
            

            // Unit under test is exercised.
            KerberosHandler handler = new KerberosHandler();
            handler.handleMessage(soapMessageContext);

    }
    
    /*
     * test when the inbound msg has invalid header
     */
    
    @Test(expected=RuntimeException.class)
    public void testHeaderHandlerInboundBadHeader2(
            @Mocked final SOAPMessageContext soapMessageContext,  @Mocked final PersistenceManager pm)
            throws Exception {
        	
    		String badHeader = "HELLO WORLD";
    	
    		// Preparation code not specific to JMockit, if any.
        	final String soapText = SOAP_REQUEST.replace("<SOAP-ENV:Header/>",
            "<SOAP-ENV:Header><" + RESPONSE_HEADER + " xmlns=\"" + RESPONSE_NS + "\">"
            + badHeader + "</" + RESPONSE_HEADER + ">" + 
            "</SOAP-ENV:Header>");

            final SOAPMessage soapMessage = byteArrayToSOAPMessage(soapText.getBytes());
            final Boolean soapOutbound = false;

            // an "expectation block"
            // One or more invocations to mocked types, causing expectations to be recorded.
            new NonStrictExpectations() {{
            	
    	    	soapMessageContext.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
    	        result = soapOutbound;
                 
            	PersistenceManager.getInfo(CommunicationHandler.CS_KEY_TAG);
            	result = DataConvertor.keyToString(csKey);

                soapMessageContext.getMessage();
                result = soapMessage;

                soapMessageContext.get(PROPERTY_TREQ);
                result = DataConvertor.dateToString(DATE_TREQ);
                
            }};
            

            // Unit under test is exercised.
            KerberosHandler handler = new KerberosHandler();
            handler.handleMessage(soapMessageContext);
 
    }
   
    /*
     * test when the inbound msg has the auth encrypted with wrong sessionKey
     */
    
    @Test(expected=RuntimeException.class)
    public void testHeaderHandlerInboundBadHeader3(
            @Mocked final SOAPMessageContext soapMessageContext,  @Mocked final PersistenceManager pm)
            throws Exception {
        	
    		byte[] request = CommunicationHandler.createSResponse(DATE_TREQ, csKeyBad);
    		String xmlEncrypted = DataConvertor.getStringFromBytes(request);
    	
    		// Preparation code not specific to JMockit, if any.
        	final String soapText = SOAP_REQUEST.replace("<SOAP-ENV:Header/>",
            "<SOAP-ENV:Header><" + RESPONSE_HEADER + " xmlns=\"" + RESPONSE_NS + "\">"
            + xmlEncrypted + "</" + RESPONSE_HEADER + ">" + 
            "</SOAP-ENV:Header>");

            final SOAPMessage soapMessage = byteArrayToSOAPMessage(soapText.getBytes());
            final Boolean soapOutbound = false;

            // an "expectation block"
            // One or more invocations to mocked types, causing expectations to be recorded.
            new NonStrictExpectations() {{
            	
    	    	soapMessageContext.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
    	        result = soapOutbound;
                 
            	PersistenceManager.getInfo(CommunicationHandler.CS_KEY_TAG);
            	result = DataConvertor.keyToString(csKey);

                soapMessageContext.getMessage();
                result = soapMessage;

                soapMessageContext.get(PROPERTY_TREQ);
                result = DataConvertor.dateToString(DATE_TREQ);
                
            }};
            

            // Unit under test is exercised.
            KerberosHandler handler = new KerberosHandler();
            handler.handleMessage(soapMessageContext);
    }
    
   
}

