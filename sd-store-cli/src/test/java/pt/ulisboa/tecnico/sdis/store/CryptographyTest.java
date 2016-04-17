package pt.ulisboa.tecnico.sdis.store;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.xml.registry.JAXRException;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import mockit.Mock;
import mockit.MockUp;

import javax.xml.soap.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Node;

import pt.ulisboa.tecnico.sdis.exception.DocHasBeenChanged_Exception;
import pt.ulisboa.tecnico.sdis.store.ws.DocAlreadyExists_Exception;
import pt.ulisboa.tecnico.sdis.store.ws.DocDoesNotExist_Exception;
import pt.ulisboa.tecnico.sdis.store.ws.UserDoesNotExist_Exception;
import pt.ulisboa.tecnico.sdis.ws.handler.CypherHandler;
import pt.ulisboa.tecnico.sdis.ws.handler.KerberosHandler;

public class CryptographyTest {
	private static SDStoreClient _client;
	private static final String UDDI_URL = "http://localhost:8081";
	private static final String WS_NAME = "SD-STORE";
	
	private final static String SAMPLE_CONTENT = "Hello world!";
	
	private final static String EXISTENT_USER = "bruno";
	
	private final static String EXISTENT_DOC = "um";
	private final static String EXISTENT_DOC2 = "dois";
	private final static String EXISTENT_DOC3 = "tres";
	
	@BeforeClass
    public static void oneTimeSetUp() throws DocAlreadyExists_Exception, JAXRException, UserDoesNotExist_Exception {
    	_client = new SDStoreClient(WS_NAME,UDDI_URL);
    	new MockUp<KerberosHandler>(){
            @Mock
            public boolean handleMessage(SOAPMessageContext smc){
            	return true;
            }
    	};
		_client.createDocument(EXISTENT_USER, EXISTENT_DOC);
		_client.createDocument(EXISTENT_USER, EXISTENT_DOC2);
		_client.createDocument(EXISTENT_USER, EXISTENT_DOC3);
    }

    @AfterClass
    public static void oneTimeTearDown() {
    	_client = null;
    }

    // members
    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /** SOAP message factory */
    protected static final MessageFactory MESSAGE_FACTORY;

    static {
        try {
            MESSAGE_FACTORY = MessageFactory.newInstance();
        } catch(SOAPException e) {
            throw new RuntimeException(e);
        }
    }
  
    protected static SOAPMessage byteArrayToSOAPMessage(byte[] msg) throws Exception {
        ByteArrayInputStream byteInStream = new ByteArrayInputStream(msg);
        StreamSource source = new StreamSource(byteInStream);
        SOAPMessage newMsg = MESSAGE_FACTORY.createMessage();
        SOAPPart soapPart = newMsg.getSOAPPart();
        soapPart.setContent(source);
        return newMsg;
    }
    
    // tests
    @Test
    public void success() throws DocDoesNotExist_Exception, UserDoesNotExist_Exception {
    	new MockUp<KerberosHandler>(){
            @Mock
            public boolean handleMessage(SOAPMessageContext smc){
            	return true;
            }
    	};
    	
    	_client.storeDocument(EXISTENT_USER, EXISTENT_DOC, SAMPLE_CONTENT.getBytes());

    	byte[] response = _client.loadDocument(EXISTENT_USER, EXISTENT_DOC);
    	String s = new String(response);
    	assertEquals(s, SAMPLE_CONTENT);
    }
    
    /***********************/
    /*** Add Bad Content ***/
    /***********************/

    @Test(expected = DocHasBeenChanged_Exception.class)
    public void documentChangedBeforeCipheringAdd() throws DocHasBeenChanged_Exception, DocDoesNotExist_Exception, UserDoesNotExist_Exception {
		new MockUp<CypherHandler>() {
			@Mock
			private void testHandlerOut(SOAPMessageContext smc) throws SOAPException, IOException { 
			 	SOAPMessage message = smc.getMessage();
		    	try {
		    		SOAPBody soapBody = message.getSOAPPart().getEnvelope().getBody();
		            Node n = soapBody.getFirstChild();
		            Node content = null;
		            if (n.getNodeName().equals("ns2:store")) {
		            	for(Node s = n.getFirstChild(); s != null; s = s.getNextSibling()) {
		            		if(s.getNodeName().equals("contents")) {
		            			content = s;
		            			break;
		            		}
		            	}
		            	if (content != null) {
		            		String s = content.getTextContent();
		            		String newContent = s.substring(0,1) + "12345" + s.substring(2);
		            		content.setTextContent(newContent);
		            	}
		            }  
		        } catch (Exception e) {
		            // print error information
		            System.out.printf("Caught exception in main method: %s%n", e);
		        }
			};
		};
		new MockUp<KerberosHandler>(){
            @Mock
            public boolean handleMessage(SOAPMessageContext smc){
            	return true;
            }
    	};
		_client.storeDocument(EXISTENT_USER, EXISTENT_DOC, SAMPLE_CONTENT.getBytes());
    	_client.loadDocument(EXISTENT_USER, EXISTENT_DOC);
    }
 
    @Test(expected = DocHasBeenChanged_Exception.class)
    public void documentChangedAfterCipheringAdd() throws DocHasBeenChanged_Exception, DocDoesNotExist_Exception, UserDoesNotExist_Exception {
		new MockUp<CypherHandler>() {
			@Mock
			private void testHandlerOutAftCipher(SOAPMessageContext smc) throws SOAPException, IOException { 
			 	SOAPMessage message = smc.getMessage();
		    	try {
		    		SOAPBody soapBody = message.getSOAPPart().getEnvelope().getBody();
		            Node n = soapBody.getFirstChild();
		            Node content = null;
		            if (n.getNodeName().equals("ns2:store")) {
		            	for(Node s = n.getFirstChild(); s != null; s = s.getNextSibling()) {
		            		if(s.getNodeName().equals("contents")) {
		            			content = s;
		            			break;
		            		}
		            	}
		            	if (content != null) {
		            		String s = content.getTextContent();
		            		String newContent = s.substring(0,1) + "12345" + s.substring(2);
		            		content.setTextContent(newContent);
		            	}
		            }  
		        } catch (Exception e) {
		            // print error information
		            System.out.printf("Caught exception in main method: %s%n", e);
		        }
			};
		};
		new MockUp<KerberosHandler>(){
            @Mock
            public boolean handleMessage(SOAPMessageContext smc){
            	return true;
            }
    	};
		_client.storeDocument(EXISTENT_USER, EXISTENT_DOC, SAMPLE_CONTENT.getBytes());
    	_client.loadDocument(EXISTENT_USER, EXISTENT_DOC);
    }
 
    @Test(expected = DocHasBeenChanged_Exception.class)
    public void documentChangedBeforeDecipheringAdd() throws DocHasBeenChanged_Exception, DocDoesNotExist_Exception, UserDoesNotExist_Exception {

		new MockUp<CypherHandler>() {
			@Mock
			private void testHandlerInBefDecipher(SOAPMessageContext smc) throws SOAPException, IOException { 
			 	SOAPMessage message = smc.getMessage();
		    	try {
		    		SOAPBody soapBody = message.getSOAPPart().getEnvelope().getBody();
		            Node n = soapBody.getFirstChild();
		            Node content = null;
		            if (n.getNodeName().equals("ns2:loadResponse")) {
		            	for(Node s = n.getFirstChild(); s != null; s = s.getNextSibling()) {
		            		if(s.getNodeName().equals("contents")) {
		            			content = s;
		            			break;
		            		}
		            	}
		            	if (content != null) {
		            		String s = content.getTextContent();
		            		String newContent = s.substring(0,1) + "12345" + s.substring(2);
		            		content.setTextContent(newContent);
		            	}
		            }  
		        } catch (Exception e) {
		            // print error information
		            System.out.printf("Caught exception in main method: %s%n", e);
		        }
			};
		};
		new MockUp<KerberosHandler>(){
            @Mock
            public boolean handleMessage(SOAPMessageContext smc){
            	return true;
            }
    	};
    	_client.storeDocument(EXISTENT_USER, EXISTENT_DOC, SAMPLE_CONTENT.getBytes());
    	_client.loadDocument(EXISTENT_USER, EXISTENT_DOC);
    }
 
    @Test(expected = DocHasBeenChanged_Exception.class)
    public void documentChangedAfterDecipheringAdd() throws DocHasBeenChanged_Exception, DocDoesNotExist_Exception, UserDoesNotExist_Exception {
		new MockUp<CypherHandler>() {
			@Mock
			private void testHandlerIn(SOAPMessageContext smc) throws SOAPException, IOException { 
			 	SOAPMessage message = smc.getMessage();
		    	try {
		    		SOAPBody soapBody = message.getSOAPPart().getEnvelope().getBody();
		            Node n = soapBody.getFirstChild();
		            Node content = null;
		            if (n.getNodeName().equals("ns2:loadResponse")) {
		            	for(Node s = n.getFirstChild(); s != null; s = s.getNextSibling()) {
		            		if(s.getNodeName().equals("contents")) {
		            			content = s;
		            			break;
		            		}
		            	}
		            	if (content != null) {
		            		String s = content.getTextContent();
		            		String newContent = s.substring(0,1) + "12345" + s.substring(2);
		            		content.setTextContent(newContent);
		            	}
		            }  
		        } catch (Exception e) {
		            // print error information
		            System.out.printf("Caught exception in main method: %s%n", e);
		        }
			};
		};
		new MockUp<KerberosHandler>(){
            @Mock
            public boolean handleMessage(SOAPMessageContext smc){
            	return true;
            }
    	};

    	_client.storeDocument(EXISTENT_USER, EXISTENT_DOC, SAMPLE_CONTENT.getBytes());
    	_client.loadDocument(EXISTENT_USER, EXISTENT_DOC);
    }
    
    /**********************/
    /**** Take Content ****/
    /**********************/
//TODO: Load is responding nullpointer. why?? the same thing happens in other tests
 @Test(expected = DocHasBeenChanged_Exception.class)
    public void documentChangedBeforeCipheringTake() throws DocHasBeenChanged_Exception, DocDoesNotExist_Exception, UserDoesNotExist_Exception {
		new MockUp<CypherHandler>() {
			@Mock
			private void testHandlerOut(SOAPMessageContext smc) throws SOAPException, IOException { 
			 	SOAPMessage message = smc.getMessage();
		    	try {
		    		SOAPBody soapBody = message.getSOAPPart().getEnvelope().getBody();
		            Node n = soapBody.getFirstChild();
		            Node content = null;
		            if (n.getNodeName().equals("ns2:store")) {
		            	for(Node s = n.getFirstChild(); s != null; s = s.getNextSibling()) {
		            		if(s.getNodeName().equals("contents")) {
		            			content = s;
		            			break;
		            		}
		            	}
		            	if (content != null) {
		            		String s = content.getTextContent();
		            		String newContent = s.substring(0,3);
		            		content.setTextContent(newContent);
		            	}
		            }  
		        } catch (Exception e) {
		            // print error information
		            System.out.printf("Caught exception in main method: %s%n", e);
		        }
			};
		};
		new MockUp<KerberosHandler>(){
            @Mock
            public boolean handleMessage(SOAPMessageContext smc){
            	return true;
            }
    	};
		_client.storeDocument(EXISTENT_USER, EXISTENT_DOC, SAMPLE_CONTENT.getBytes());
    	_client.loadDocument(EXISTENT_USER, EXISTENT_DOC);
    }
 /* ------> You need to fix the Store to unlock this character. Line 45 in Store.java throws a NullPointerException (contents is null)
 @Test(expected = SOAPException.class)
    public void documentChangedAfterCipheringTake() throws DocHasBeenChanged_Exception, DocDoesNotExist_Exception, UserDoesNotExist_Exception {
		new MockUp<CypherHandler>() {
			@Mock
			private void testHandlerOutAftCipher(SOAPMessageContext smc) throws SOAPException, IOException { 
			 	SOAPMessage message = smc.getMessage();
		    	try {
		    		SOAPBody soapBody = message.getSOAPPart().getEnvelope().getBody();
		            Node n = soapBody.getFirstChild();
		            Node content = null;
		            if (n.getNodeName().equals("ns2:store")) {
		            	for(Node s = n.getFirstChild(); s != null; s = s.getNextSibling()) {
		            		if(s.getNodeName().equals("contents")) {
		            			content = s;
		            			break;
		            		}
		            	}
		            	if (content != null) {
		            		String s = content.getTextContent();
		            		String newContent = s.substring(0,1) + s.substring(3);
		            		content.setTextContent(newContent);
		            	}
		            }  
		        } catch (Exception e) {
		            // print error information
		            System.out.printf("Caught exception in main method: %s%n", e);
		        }
			};
		};
		_client.storeDocument(EXISTENT_USER, EXISTENT_DOC, SAMPLE_CONTENT.getBytes());
    	_client.loadDocument(EXISTENT_USER, EXISTENT_DOC);
    }
 */
 @Test(expected = DocHasBeenChanged_Exception.class)
    public void documentChangedBeforeDecipheringTake() throws DocHasBeenChanged_Exception, DocDoesNotExist_Exception, UserDoesNotExist_Exception {
		new MockUp<CypherHandler>() {
			@Mock
			private void testHandlerInBefDecipher(SOAPMessageContext smc) throws SOAPException, IOException { 
			 	SOAPMessage message = smc.getMessage();
		    	try {
		    		SOAPBody soapBody = message.getSOAPPart().getEnvelope().getBody();
		            Node n = soapBody.getFirstChild();
		            Node content = null;
		            if (n.getNodeName().equals("ns2:loadResponse")) {
		            	for(Node s = n.getFirstChild(); s != null; s = s.getNextSibling()) {
		            		if(s.getNodeName().equals("contents")) {
		            			content = s;
		            			break;
		            		}
		            	}
		            	if (content != null) {
		            		String s = content.getTextContent();
		            		String newContent = s.substring(0,3);
		            		content.setTextContent(newContent);
		            	}
		            }  
		        } catch (Exception e) {
		            // print error information
		            System.out.printf("Caught exception in main method: %s%n", e);
		        }
			};
		};
		new MockUp<KerberosHandler>(){
            @Mock
            public boolean handleMessage(SOAPMessageContext smc){
            	return true;
            }
    	};
    	_client.storeDocument(EXISTENT_USER, EXISTENT_DOC, SAMPLE_CONTENT.getBytes());
    	_client.loadDocument(EXISTENT_USER, EXISTENT_DOC);
    }
 
    @Test(expected = DocHasBeenChanged_Exception.class)
    public void documentChangedAfterDecipheringTake() throws DocHasBeenChanged_Exception, DocDoesNotExist_Exception, UserDoesNotExist_Exception {
		new MockUp<CypherHandler>() {
			@Mock
			private void testHandlerIn(SOAPMessageContext smc) throws SOAPException, IOException { 
			 	SOAPMessage message = smc.getMessage();
		    	try {
		    		SOAPBody soapBody = message.getSOAPPart().getEnvelope().getBody();
		            Node n = soapBody.getFirstChild();
		            Node content = null;
		            if (n.getNodeName().equals("ns2:loadResponse")) {
		            	for(Node s = n.getFirstChild(); s != null; s = s.getNextSibling()) {
		            		if(s.getNodeName().equals("contents")) {
		            			content = s;
		            			break;
		            		}
		            	}
		            	if (content != null) {
		            		String s = content.getTextContent();
		            		String newContent = s.substring(0,3);
		            		content.setTextContent(newContent);
		            	}
		            }  
		        } catch (Exception e) {
		            // print error information
		            System.out.printf("Caught exception in main method: %s%n", e);
		        }
			};
		};
		new MockUp<KerberosHandler>(){
            @Mock
            public boolean handleMessage(SOAPMessageContext smc){
            	return true;
            }
    	};

    	_client.storeDocument(EXISTENT_USER, EXISTENT_DOC, SAMPLE_CONTENT.getBytes());
    	_client.loadDocument(EXISTENT_USER, EXISTENT_DOC);
    }
    
    /************************/
    /**** Change Content ****/
    /************************/
    
    @Test(expected = DocHasBeenChanged_Exception.class)
    public void documentChangedBeforeCipheringChange() throws DocHasBeenChanged_Exception, DocDoesNotExist_Exception, UserDoesNotExist_Exception {
		new MockUp<CypherHandler>() {
			@Mock
			private void testHandlerOut(SOAPMessageContext smc) throws SOAPException, IOException { 
			 	SOAPMessage message = smc.getMessage();
		    	try {
		    		SOAPBody soapBody = message.getSOAPPart().getEnvelope().getBody();
		            Node n = soapBody.getFirstChild();
		            Node content = null;
		            if (n.getNodeName().equals("ns2:store")) {
		            	for(Node s = n.getFirstChild(); s != null; s = s.getNextSibling()) {
		            		if(s.getNodeName().equals("contents")) {
		            			content = s;
		            			break;
		            		}
		            	}
		            	if (content != null) {
		            		String s = content.getTextContent();
		            		String newContent = s.substring(3) + s.substring(0,1);
		            		content.setTextContent(newContent);
		            	}
		            }  
		        } catch (Exception e) {
		            // print error information
		            System.out.printf("Caught exception in main method: %s%n", e);
		        }
			};
		};
		new MockUp<KerberosHandler>(){
            @Mock
            public boolean handleMessage(SOAPMessageContext smc){
            	return true;
            }
    	};
		_client.storeDocument(EXISTENT_USER, EXISTENT_DOC, SAMPLE_CONTENT.getBytes());
    	_client.loadDocument(EXISTENT_USER, EXISTENT_DOC);
    }
 
    @Test(expected = DocHasBeenChanged_Exception.class)
    public void documentChangedAfterCipheringChange() throws DocHasBeenChanged_Exception, DocDoesNotExist_Exception, UserDoesNotExist_Exception {
		new MockUp<CypherHandler>() {
			@Mock
			private void testHandlerOutAftCipher(SOAPMessageContext smc) throws SOAPException, IOException { 
			 	SOAPMessage message = smc.getMessage();
		    	try {
		    		SOAPBody soapBody = message.getSOAPPart().getEnvelope().getBody();
		            Node n = soapBody.getFirstChild();
		            Node content = null;
		            if (n.getNodeName().equals("ns2:store")) {
		            	for(Node s = n.getFirstChild(); s != null; s = s.getNextSibling()) {
		            		if(s.getNodeName().equals("contents")) {
		            			content = s;
		            			break;
		            		}
		            	}
		            	if (content != null) {
		            		String s = content.getTextContent();
		            		String newContent = s.substring(3) + s.substring(0,1);
		            		content.setTextContent(newContent);
		            	}
		            }  
		        } catch (Exception e) {
		            // print error information
		            System.out.printf("Caught exception in main method: %s%n", e);
		        }
			};
		};
		new MockUp<KerberosHandler>(){
            @Mock
            public boolean handleMessage(SOAPMessageContext smc){
            	return true;
            }
    	};
		_client.storeDocument(EXISTENT_USER, EXISTENT_DOC, SAMPLE_CONTENT.getBytes());
    	_client.loadDocument(EXISTENT_USER, EXISTENT_DOC);
    }
 
    @Test(expected = DocHasBeenChanged_Exception.class)
    public void documentChangedBeforeDecipheringChange() throws DocHasBeenChanged_Exception, DocDoesNotExist_Exception, UserDoesNotExist_Exception {
		new MockUp<CypherHandler>() {
			@Mock
			private void testHandlerInBefDecipher(SOAPMessageContext smc) throws SOAPException, IOException { 
			 	SOAPMessage message = smc.getMessage();
		    	try {
		    		SOAPBody soapBody = message.getSOAPPart().getEnvelope().getBody();
		            Node n = soapBody.getFirstChild();
		            Node content = null;
		            if (n.getNodeName().equals("ns2:loadResponse")) {
		            	for(Node s = n.getFirstChild(); s != null; s = s.getNextSibling()) {
		            		if(s.getNodeName().equals("contents")) {
		            			content = s;
		            			break;
		            		}
		            	}
		            	if (content != null) {
		            		String s = content.getTextContent();
		            		String newContent = s.substring(3) + s.substring(0,1);
		            		content.setTextContent(newContent);
		            	}
		            }  
		        } catch (Exception e) {
		            // print error information
		            System.out.printf("Caught exception in main method: %s%n", e);
		        }
			};
		};
		new MockUp<KerberosHandler>(){
            @Mock
            public boolean handleMessage(SOAPMessageContext smc){
            	return true;
            }
    	};
    	_client.storeDocument(EXISTENT_USER, EXISTENT_DOC, SAMPLE_CONTENT.getBytes());
    	_client.loadDocument(EXISTENT_USER, EXISTENT_DOC);
    }
 
    @Test(expected = DocHasBeenChanged_Exception.class)
    public void documentChangedAfterDecipheringChange() throws DocHasBeenChanged_Exception, DocDoesNotExist_Exception, UserDoesNotExist_Exception {
		new MockUp<CypherHandler>() {
			@Mock
			private void testHandlerIn(SOAPMessageContext smc) throws SOAPException, IOException { 
			 	SOAPMessage message = smc.getMessage();
		    	try {
		    		SOAPBody soapBody = message.getSOAPPart().getEnvelope().getBody();
		            Node n = soapBody.getFirstChild();
		            Node content = null;
		            if (n.getNodeName().equals("ns2:loadResponse")) {
		            	for(Node s = n.getFirstChild(); s != null; s = s.getNextSibling()) {
		            		if(s.getNodeName().equals("contents")) {
		            			content = s;
		            			break;
		            		}
		            	}
		            	if (content != null) {
		            		String s = content.getTextContent();
		            		String newContent = s.substring(3) + s.substring(0,1);
		            		content.setTextContent(newContent);
		            	}
		            }  
		        } catch (Exception e) {
		            // print error information
		            System.out.printf("Caught exception in main method: %s%n", e);
		        }
			};
		};
		new MockUp<KerberosHandler>(){
            @Mock
            public boolean handleMessage(SOAPMessageContext smc){
            	return true;
            }
    	};
    	_client.storeDocument(EXISTENT_USER, EXISTENT_DOC, SAMPLE_CONTENT.getBytes());
    	_client.loadDocument(EXISTENT_USER, EXISTENT_DOC);
    }
}