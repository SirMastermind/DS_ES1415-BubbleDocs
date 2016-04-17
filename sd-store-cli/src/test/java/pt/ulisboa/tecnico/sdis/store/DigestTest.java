package pt.ulisboa.tecnico.sdis.store;

import java.io.IOException;

import javax.xml.registry.JAXRException;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import mockit.Mock;
import mockit.MockUp;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Node;

import pt.ulisboa.tecnico.sdis.exception.DocHasBeenChanged_Exception;
import pt.ulisboa.tecnico.sdis.store.ws.DocAlreadyExists_Exception;
import pt.ulisboa.tecnico.sdis.store.ws.DocDoesNotExist_Exception;
import pt.ulisboa.tecnico.sdis.store.ws.UserDoesNotExist_Exception;
import pt.ulisboa.tecnico.sdis.ws.handler.CypherHandler;
import pt.ulisboa.tecnico.sdis.ws.handler.KerberosHandler;

public class DigestTest {
	private static SDStoreClient _client;
	private static final String UDDI_URL = "http://localhost:8081";
	private static final String WS_NAME = "SD-STORE";
	
	private final static String EXISTENT_USER = "bruno";
	
	private final static String EXISTENT_DOC = "sampl3321";
	
	private final static String SAMPLE_CONTENT = "Hello world!";

    // one-time initialization and clean-up

    @BeforeClass
    public static void oneTimeSetUp() throws JAXRException, DocDoesNotExist_Exception, UserDoesNotExist_Exception, DocAlreadyExists_Exception {
    	_client = new SDStoreClient(WS_NAME,UDDI_URL);
    	
    	
    	new MockUp<KerberosHandler>(){
            @Mock
            public boolean handleMessage(SOAPMessageContext smc){
            	return true;
            }
    	};
    	
    	_client.createDocument(EXISTENT_USER, EXISTENT_DOC);
    	_client.storeDocument(EXISTENT_USER,EXISTENT_DOC, SAMPLE_CONTENT.getBytes());
    }

    @AfterClass
    public static void oneTimeTearDown() {
    	_client = null;
    }
	
	@Test(expected = DocHasBeenChanged_Exception.class)
    public void documentChangedAtResponse() throws DocHasBeenChanged_Exception, DocDoesNotExist_Exception, UserDoesNotExist_Exception {
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
		            		String newContent = s.substring(0,1)+'x'+s.substring(2);
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
		
    	_client.loadDocument(EXISTENT_USER, EXISTENT_DOC);
    }
    
    @Test(expected = DocHasBeenChanged_Exception.class)
    public void documentChangedAtRequest() throws DocHasBeenChanged_Exception, DocDoesNotExist_Exception, UserDoesNotExist_Exception {
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
		            		String newContent = s.substring(0,1)+'x'+s.substring(2);
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
    public void digestChangedAtResponse() throws DocHasBeenChanged_Exception, DocDoesNotExist_Exception, UserDoesNotExist_Exception {
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
		            		String newContent = s.substring(0,s.length()-2)+'x'+s.substring(s.length()-1);
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
		
    	_client.loadDocument(EXISTENT_USER, EXISTENT_DOC);
    }
    
    @Test(expected = DocHasBeenChanged_Exception.class)
    public void digestChangedAtRequest() throws DocHasBeenChanged_Exception, DocDoesNotExist_Exception, UserDoesNotExist_Exception {
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
		            		String newContent = s.substring(0,s.length()-2)+'x'+s.substring(s.length()-1);
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
