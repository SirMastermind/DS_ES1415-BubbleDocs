package pt.ulisboa.tecnico.sdis.id;

import org.junit.*;

import pt.ulisboa.tecnico.sdis.exception.NoAnswerFromServerException;
import pt.ulisboa.tecnico.sdis.exception.SDIDCommunicationException;
import pt.ulisboa.tecnico.sdis.id.ws.*;

public class RemoveUserTest {

	private static SDIdClient _client;
	private static final String UDDIURL = "http://localhost:8081";
	private static final String WSNAME = "SD-ID";
	private static final String USERNAME1 = "nice_userr";
	private static final String EMAIL = "holaa@domain.com";
	private static final String NOT_EXISTING_USER = "lol";
	
	@BeforeClass
	public static void oneTimeSetup() throws SDIDCommunicationException {
		_client = new SDIdClient(WSNAME, UDDIURL);
	}
	
	@AfterClass
	public static void oneTimeTearDown(){
		_client = null;
	}
	

	@Test
    public void removeUserSuccess() 
    		throws EmailAlreadyExists_Exception, InvalidEmail_Exception, InvalidUser_Exception, 
    		UserAlreadyExists_Exception, UserDoesNotExist_Exception, NoAnswerFromServerException{
		
    	_client.createUser(USERNAME1, EMAIL);
    	_client.removeUser(USERNAME1);
    	
    	//to assure that last remove was executed with success
    	_client.createUser(USERNAME1, EMAIL);
    	_client.removeUser(USERNAME1);
    }
	
	//test that tries to remove a user that does not exist
	@Test(expected=UserDoesNotExist_Exception.class)
	public void removeUserThatDoesNotExist() 
			throws UserDoesNotExist_Exception, NoAnswerFromServerException{
		
		_client.removeUser(NOT_EXISTING_USER);
	}
	
}
