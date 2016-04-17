package pt.ulisboa.tecnico.sdis.store;



import javax.xml.registry.JAXRException;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import mockit.Mock;
import mockit.MockUp;

import org.junit.*;

import static org.junit.Assert.*;
import pt.ulisboa.tecnico.sdis.exception.DocHasBeenChanged_Exception;
import pt.ulisboa.tecnico.sdis.store.ws.DocAlreadyExists_Exception;
import pt.ulisboa.tecnico.sdis.store.ws.DocDoesNotExist_Exception;
import pt.ulisboa.tecnico.sdis.store.ws.UserDoesNotExist_Exception;
import pt.ulisboa.tecnico.sdis.ws.handler.KerberosHandler;


public class LoadTest {

    // static members
	
	private static SDStoreClient _client;
	private static final String UDDI_URL = "http://localhost:8081";
	private static final String WS_NAME = "SD-STORE";
	
	private final static String EXISTENT_USER = "bruno";
	private final static String EXISTENT_USER2 = "carla";
	private final static String NON_EXISTENT_USER = "tiago";
	
	private final static String EXISTENT_DOC = "sample";
	private final static String NON_EXISTENT_DOC = "notSample";
	
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

    // tests

    @Test
    public void success() throws DocDoesNotExist_Exception, UserDoesNotExist_Exception, DocHasBeenChanged_Exception {
    	
    	new MockUp<KerberosHandler>(){
            @Mock
            public boolean handleMessage(SOAPMessageContext smc){
            	return true;
            }
    	};
    	
    	byte[] test = _client.loadDocument(EXISTENT_USER, EXISTENT_DOC);
    	String s = new String(test);
    	assertEquals(s, SAMPLE_CONTENT);
    }
    
    @Test(expected = UserDoesNotExist_Exception.class)
    public void expectNonExistentUser() throws DocDoesNotExist_Exception, UserDoesNotExist_Exception, DocHasBeenChanged_Exception {
    	
    	new MockUp<KerberosHandler>(){
            @Mock
            public boolean handleMessage(SOAPMessageContext smc){
            	return true;
            }
    	};
    	
    	_client.loadDocument(NON_EXISTENT_USER, EXISTENT_DOC);
    }
    
    @Test(expected = DocDoesNotExist_Exception.class)
    public void expectNonExistentDoc() throws DocDoesNotExist_Exception, UserDoesNotExist_Exception, DocHasBeenChanged_Exception {
    	
    	new MockUp<KerberosHandler>(){
            @Mock
            public boolean handleMessage(SOAPMessageContext smc){
            	return true;
            }
    	};
    	
    	_client.loadDocument(EXISTENT_USER2, NON_EXISTENT_DOC);
    }
   /* @Test
    public void differentLoads(@Mocked final SDStore port, @Mocked final UDDINaming uddiNaming, @Mocked final FrontEnd fe) throws DocHasBeenChanged_Exception, DocDoesNotExist_Exception, UserDoesNotExist_Exception, CapacityExceeded_Exception, JAXRException{
    	new Expectations() {{
    		
    		uddiNaming.list(anyString);
    		ArrayList<String> endpoint = new ArrayList<String>();
    		endpoint.add("servidor1");
    		endpoint.add("servidor2");
    		endpoint.add("servidor3");
    		endpoint.add("servidor4");
    		endpoint.add("servidor5");
    		result = endpoint;
    		result = endpoint;
    		fe.renewStubs();
    		result = null;
        	port.load(null);
            result = SAMPLE_CONTENT.getBytes();
            result = "Coisas".getBytes();
            result = "Coisas2".getBytes();
            result = "Hello world!".getBytes();
            result = "Hello world!".getBytes();
        }};
    	_client.loadDocument(EXISTENT_USER, EXISTENT_DOC);
    }*/
}