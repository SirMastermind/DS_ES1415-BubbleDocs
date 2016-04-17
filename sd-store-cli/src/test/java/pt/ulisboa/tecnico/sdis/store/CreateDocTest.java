package pt.ulisboa.tecnico.sdis.store;

import java.util.List;

import javax.xml.registry.JAXRException;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import mockit.Mock;
import mockit.MockUp;

import org.junit.*;

import pt.ulisboa.tecnico.sdis.store.ws.*;
import pt.ulisboa.tecnico.sdis.ws.handler.KerberosHandler;
import static org.junit.Assert.*;

public class CreateDocTest {

    // static members
	private static SDStoreClient _client;
	private static final String UDDI_URL = "http://localhost:8081";
	private static final String WS_NAME = "SD-STORE";
	
	private static final String CORRECT_USER = "alice";
	private static final String INCORRECT_USER = "bino";
	
	private static final String DOC_NAME = "cenas1234";
	private static final String DOC_NAME2 = "cenas21234";

    // one-time initialization and clean-up

    @BeforeClass
    public static void oneTimeSetUp() throws JAXRException {
    	_client = new SDStoreClient(WS_NAME,UDDI_URL);
    }

    @AfterClass
    public static void oneTimeTearDown() {
    	_client = null;
    }

    // tests

    @Test
    public void success() throws Exception {
    	
    	new MockUp<KerberosHandler>(){
            @Mock
            public boolean handleMessage(SOAPMessageContext smc){
            	return true;
            }
    	};
    	
    	_client.createDocument(CORRECT_USER, DOC_NAME);
    	List<String> list = _client.listDocuments(CORRECT_USER);
    	assertEquals(DOC_NAME, list.get(0));
    }
    
    @Test(expected = DocAlreadyExists_Exception.class) 
    public void DocAlreadyExists() throws DocAlreadyExists_Exception, UserDoesNotExist_Exception {
    	
    	new MockUp<KerberosHandler>(){
            @Mock
            public boolean handleMessage(SOAPMessageContext smc){
            	return true;
            }
    	};
    	
    	_client.createDocument(CORRECT_USER, DOC_NAME2);
    	_client.createDocument(CORRECT_USER, DOC_NAME2);
    }
    
    //Removed UserDoesNotExistException
    @Test
    public void UserDoesNotExistSuccess() throws UserDoesNotExist_Exception, DocAlreadyExists_Exception {
    	
    	new MockUp<KerberosHandler>(){
            @Mock
            public boolean handleMessage(SOAPMessageContext smc){
            	return true;
            }
    	};
    	
    	_client.createDocument(INCORRECT_USER, DOC_NAME2);

    	List<String> list = _client.listDocuments(INCORRECT_USER);
    	assertEquals(DOC_NAME2, list.get(0));
    }
}


/*
*	Tests
*		-> 1 servidor nao responde
*
*
*/