package pt.ulisboa.tecnico.sdis.store;

import java.util.ArrayList;
import java.util.List;

import javax.xml.registry.JAXRException;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import mockit.Mock;
import mockit.MockUp;

import org.junit.*;

import pt.ulisboa.tecnico.sdis.store.ws.*;
import pt.ulisboa.tecnico.sdis.ws.handler.KerberosHandler;
import static org.junit.Assert.*;

public class ListDocsTest {

    // static members
	private static SDStoreClient _client;
	private static final String UDDI_URL = "http://localhost:8081";
	private static final String WS_NAME = "SD-STORE";
	
	private static final String USERNAME_1FILE = "carla";
	private static final String USERNAME_2FILES = "duarte";
	private static final String USERNAME_NO_FILES = "eduardo";
	private static final String INCORRECT_USERNAME = "tiago";
	
	private static final String DOC_NAME = "1";
	private static final String DOC_NAME2 = "2";
	
	

    // one-time initialization and clean-up

    @BeforeClass
    public static void oneTimeSetUp() throws DocAlreadyExists_Exception, JAXRException, UserDoesNotExist_Exception {
    	_client = new SDStoreClient(WS_NAME,UDDI_URL);
    	
    	new MockUp<KerberosHandler>(){
            @Mock
            public boolean handleMessage(SOAPMessageContext smc){
            	return true;
            }
    	};
    	
    	_client.createDocument(USERNAME_1FILE, DOC_NAME);	
    	_client.createDocument(USERNAME_2FILES, DOC_NAME);
    	_client.createDocument(USERNAME_2FILES, DOC_NAME2);
    }

    @AfterClass
    public static void oneTimeTearDown() {
    	_client = null;
    }


    // tests

    @Test
    public void testOneDoc() throws UserDoesNotExist_Exception, DocAlreadyExists_Exception {
    	List<String> expected = new ArrayList<String>();
    	expected.add("1");
    	
    	new MockUp<KerberosHandler>(){
            @Mock
            public boolean handleMessage(SOAPMessageContext smc){
            	return true;
            }
    	};
    	
        assertEquals(expected,_client.listDocuments(USERNAME_1FILE));
    }
    
    @Test
    public void testVariousDoc() throws UserDoesNotExist_Exception, DocAlreadyExists_Exception {
    	List<String> expected = new ArrayList<String>();
    	expected.add("1");
    	expected.add("2");

    	new MockUp<KerberosHandler>(){
            @Mock
            public boolean handleMessage(SOAPMessageContext smc){
            	return true;
            }
    	};
    	
        assertEquals(expected,_client.listDocuments(USERNAME_2FILES) );
    }
    
    @Test
    public void testNoDoc() throws UserDoesNotExist_Exception {
    	List<String> expected = new ArrayList<String>();
    	
    	new MockUp<KerberosHandler>(){
            @Mock
            public boolean handleMessage(SOAPMessageContext smc){
            	return true;
            }
    	};
    	
        assertEquals(expected,_client.listDocuments(USERNAME_NO_FILES) );
    }
    @Test(expected = UserDoesNotExist_Exception.class)
    public void testInvalidUser() throws UserDoesNotExist_Exception, DocAlreadyExists_Exception {
    	
    	new MockUp<KerberosHandler>(){
            @Mock
            public boolean handleMessage(SOAPMessageContext smc){
            	return true;
            }
    	};
    	
    	_client.listDocuments(INCORRECT_USERNAME);
    }
}


/*
 * 
 * Tests to do:
 * 
 * 		->1 servidor nao responde
 *      ->1 servidor nao responde e as 2 respostas sao ambas incompletas
 * 
 */
