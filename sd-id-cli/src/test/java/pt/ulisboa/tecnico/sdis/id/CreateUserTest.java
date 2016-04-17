package pt.ulisboa.tecnico.sdis.id;

import org.junit.*;
import pt.ulisboa.tecnico.sdis.exception.NoAnswerFromServerException;
import pt.ulisboa.tecnico.sdis.exception.SDIDCommunicationException;
import pt.ulisboa.tecnico.sdis.id.ws.*;

public class CreateUserTest {

	private static SDIdClient _client;
	private static final String UDDIURL = "http://localhost:8081";
	private static final String WSNAME = "SD-ID";
	private static final String USERNAME1 = "nice_user";
	private static final String USERNAME2 = "bad_user";
	private static final String INVALID_USERNAME = "";
	private static final String EMAIL = "miau@gmail.com";
	private static final String EMAIL2 = "miau2@gmail.com";
	private static final String INVALID_EMAIL = "lol@@domain";
	
	@BeforeClass
	public static void oneTimeSetup() throws SDIDCommunicationException{
		_client = new SDIdClient(WSNAME, UDDIURL);
	}
	
	@AfterClass
	public static void oneTimeTearDown(){
		_client = null;
	}
	
	 @After
    public void tearDown() throws NoAnswerFromServerException{
		
		try {
			_client.removeUser(USERNAME1);
			_client.removeUser(USERNAME2);
		} 
		catch (UserDoesNotExist_Exception e) {
			//Silence
		}
    }

	@Test
    public void createUserSuccess() 
    		throws EmailAlreadyExists_Exception, InvalidEmail_Exception, InvalidUser_Exception, 
    			UserAlreadyExists_Exception, NoAnswerFromServerException{
		
    	_client.createUser(USERNAME1, EMAIL);
    }
	
	@Test(expected=EmailAlreadyExists_Exception.class)
	public void createUserExistingEmail() 
			throws EmailAlreadyExists_Exception, InvalidEmail_Exception, InvalidUser_Exception, 
				UserAlreadyExists_Exception, NoAnswerFromServerException{
		
		_client.createUser(USERNAME1, EMAIL);
		_client.createUser(USERNAME2, EMAIL);
	}
	
	@Test(expected=InvalidEmail_Exception.class)
	public void createUserInvalidEmail() 
			throws EmailAlreadyExists_Exception, InvalidEmail_Exception, InvalidUser_Exception, 
				UserAlreadyExists_Exception, NoAnswerFromServerException{
		
		_client.createUser(USERNAME1, INVALID_EMAIL);
	}
	
	@Test(expected=InvalidUser_Exception.class)
	public void createUserInvalidUser() 
			throws EmailAlreadyExists_Exception, InvalidEmail_Exception, InvalidUser_Exception, 
			UserAlreadyExists_Exception, NoAnswerFromServerException{
		
		_client.createUser(INVALID_USERNAME, EMAIL);
	}
	
	@Test(expected=UserAlreadyExists_Exception.class)
	public void createUserUserAlreadyExists() 
			throws EmailAlreadyExists_Exception, InvalidEmail_Exception, InvalidUser_Exception, 
			UserAlreadyExists_Exception, NoAnswerFromServerException{
		
		_client.createUser(USERNAME1, EMAIL);
		_client.createUser(USERNAME1, EMAIL2);
	}
	
}
