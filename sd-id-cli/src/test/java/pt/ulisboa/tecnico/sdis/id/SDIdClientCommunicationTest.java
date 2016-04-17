package pt.ulisboa.tecnico.sdis.id;


import javax.xml.registry.JAXRException;
import mockit.*;
import org.junit.*;
import static org.junit.Assert.*;
import pt.ulisboa.tecnico.sdis.exception.NoAnswerFromServerException;
import pt.ulisboa.tecnico.sdis.exception.WServiceNotFoundException;
import pt.ulisboa.tecnico.sdis.id.ws.UserDoesNotExist_Exception;
import pt.ulisboa.tecnico.sdis.uddi.UDDINaming;

public class SDIdClientCommunicationTest {

	
	private static final String USERNAME = "bruno";
	private static final String USERNAME2 = "bruno2";
	private static final String UDDIURL = "http://localhost:8081";
	private static final String WSNAME = "SD-ID";
	
	
	@BeforeClass
	public static void oneTimeSetup(){
		//NOTHING TO DO
	}
	
	@AfterClass
	public static void oneTimeTearDown(){
		//NOTHING TO DO
	}
	
	/*
	 * test WS host server comunication problems
	 */
	
	//Simulate server comunication exception
	@Test(expected=NoAnswerFromServerException.class)
    public void testSDIdServer(@Mocked final SDIdClient client) 
    		throws UserDoesNotExist_Exception, NoAnswerFromServerException {
      
		
	        new Expectations() {{
	        	client.removeUser(anyString);
	        	result = new NoAnswerFromServerException("fabricated");
	        }};
	        
	        // call to mocked server
	        client.removeUser(USERNAME);

	}
	
	
	//Simulate server comunication exception at second call
    @Test
    public void testMockServerExceptionOnSecondCall(@Mocked final SDIdClient client) 
    		throws UserDoesNotExist_Exception, NoAnswerFromServerException {
        
    	
    	new Expectations() {{
        	client.removeUser(anyString);
        	result = null;
        	result = new NoAnswerFromServerException("fabricated");
        }};
        
        // first call to mocked server
        try {
            client.removeUser(USERNAME);
        } catch(NoAnswerFromServerException e) {
            // exception is not expected
            fail();
        }

        // second call to mocked server
        try {
        	client.removeUser(USERNAME2);;
            fail();
        } catch(NoAnswerFromServerException e) {
            // exception is expected
            assertEquals("fabricated", e.getMessage());
        }
    }
    
   
	/*
	 * Test uddi errors 
	 */
	
	//Test when client connection to Uddi fails
	@Test
    public void testUDDIAddress(@Mocked final UDDINaming uddinaming)
    		throws  WServiceNotFoundException, JAXRException {
		
	        new Expectations() {{
	        	new UDDINaming(UDDIURL);
	            result = new JAXRException("Fabricated");
	        }};
	        
	        try{
	        	new SDIdClient(WSNAME, UDDIURL);
	        	fail();
	        }
	        catch(NoAnswerFromServerException e){
	        	assertEquals("No Response from uddi server at: " + UDDIURL, e.getMessage());
	        }
	        
	}
	

	//Test when uddi does not find the service asked from client
	@Test(expected=WServiceNotFoundException.class)
    public void testWSNotFound(@Mocked final UDDINaming uddinaming) 
    		throws  JAXRException, WServiceNotFoundException, NoAnswerFromServerException {
		
	        new Expectations() {{
	        	uddinaming.lookup(WSNAME);
	            result = null;
	        }};

	        new SDIdClient(WSNAME, UDDIURL);
	}
}
