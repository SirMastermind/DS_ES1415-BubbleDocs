package pt.ulisboa.tecnico.sdis.store;

import javax.xml.registry.JAXRException;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import mockit.Mock;
import mockit.MockUp;

import org.junit.*;

import pt.ulisboa.tecnico.sdis.store.ws.DocAlreadyExists_Exception;
import pt.ulisboa.tecnico.sdis.store.ws.DocDoesNotExist_Exception;
import pt.ulisboa.tecnico.sdis.store.ws.UserDoesNotExist_Exception;
import pt.ulisboa.tecnico.sdis.ws.handler.KerberosHandler;
import static org.junit.Assert.*;

public class StoreTest {

    // static members
	private static SDStoreClient _client;
	private static final String UDDI_URL = "http://localhost:8081";
	private static final String WS_NAME = "SD-STORE";
	
	private final static String SAMPLE_CONTENT = "Hello world!";
	
	private final static String EXISTENT_USER = "bruno";
	private final static String EXISTENT_USER2 = "carla";
	private final static String NON_EXISTENT_USER = "tiago";
	
	private final static String EXISTENT_DOC = "sample123";
	private final static String EXISTENT_DOC2 = "sample123123";
	private final static String EXISTENT_DOC3 = "sample123123123";
	private final static String NON_EXISTENT_DOC = "notSample123";

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
    	
		_client.createDocument(EXISTENT_USER, EXISTENT_DOC);
		_client.createDocument(EXISTENT_USER, EXISTENT_DOC2);
		_client.createDocument(EXISTENT_USER, EXISTENT_DOC3);
    }

    @AfterClass
    public static void oneTimeTearDown() {
    	_client = null;
    }


    // members


    // initialization and clean-up for each test

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
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
    
    @Test(expected = UserDoesNotExist_Exception.class)
    public void expectNonExistentUser() throws DocDoesNotExist_Exception, UserDoesNotExist_Exception {
    	
    	new MockUp<KerberosHandler>(){
            @Mock
            public boolean handleMessage(SOAPMessageContext smc){
            	return true;
            }
    	};
    	
    	_client.storeDocument(NON_EXISTENT_USER, EXISTENT_DOC, SAMPLE_CONTENT.getBytes());
    }
    
    @Test(expected = DocDoesNotExist_Exception.class)
    public void expectNonExistentDoc() throws DocDoesNotExist_Exception, UserDoesNotExist_Exception {
    	
    	new MockUp<KerberosHandler>(){
            @Mock
            public boolean handleMessage(SOAPMessageContext smc){
            	return true;
            }
    	};
    	
    	_client.storeDocument(EXISTENT_USER2, NON_EXISTENT_DOC, SAMPLE_CONTENT.getBytes());
    }
    
    /*@Test(expected = CapacityExceeded_Exception.class)
    public void CapacityReached() throws DocDoesNotExist_Exception, UserDoesNotExist_Exception, CapacityExceeded_Exception {
    	StringBuilder t = new StringBuilder();
    	while(t.length() < 10240) {
    		t.append("a");
    	}
    	String s = t.toString();
    	byte[] test = s.getBytes();
    	byte[] test2 = SAMPLE_CONTENT.getBytes();
    	_client.storeDocument(EXISTENT_USER, EXISTENT_DOC2, test);
    	_client.storeDocument(EXISTENT_USER, EXISTENT_DOC3, test2);
    }*/
}