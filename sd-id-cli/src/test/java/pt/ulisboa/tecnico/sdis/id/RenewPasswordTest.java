package pt.ulisboa.tecnico.sdis.id;

import org.junit.*;

import pt.ulisboa.tecnico.sdis.exception.NoAnswerFromServerException;
import pt.ulisboa.tecnico.sdis.exception.SDIDCommunicationException;
import pt.ulisboa.tecnico.sdis.id.ws.*;

public class RenewPasswordTest {

	private static SDIdClient _client;
	private static final String UDDIURL = "http://localhost:8081";
	private static final String WSNAME = "SD-ID";
	private static final String USERNAME = "carla";
	private static final String PASSWORD = "Ccc3";
	private static final String NOT_EXISTING_USER = "lol";
	
	@BeforeClass
	public static void oneTimeSetup() throws SDIDCommunicationException {
		_client = new SDIdClient(WSNAME, UDDIURL);
	}
	
	@AfterClass
	public static void oneTimeTearDown() throws UserDoesNotExist_Exception{
		_client = null;
	}

	@Test(expected=AuthReqFailed_Exception.class)
    public void renewPasswordSuccess() 
    		throws AuthReqFailed_Exception, UserDoesNotExist_Exception, 
    			NoAnswerFromServerException{
		
		_client.renewPassword(USERNAME);
		
		//now login should fail with old password
		_client.requestAuthentication(USERNAME, PASSWORD);
    }
	
	//Test that tries to renew a password from a user that does not exist
	@Test(expected=UserDoesNotExist_Exception.class)
	public void renewNotExistingUserPassword() 
			throws UserDoesNotExist_Exception, NoAnswerFromServerException{
		
		_client.renewPassword(NOT_EXISTING_USER);
	}
	
}
