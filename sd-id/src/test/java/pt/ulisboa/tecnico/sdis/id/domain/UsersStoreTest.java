package pt.ulisboa.tecnico.sdis.id.domain;

import static org.junit.Assert.*;
import org.junit.*;
import pt.ulisboa.tecnico.sdis.id.ws.*;


public class UsersStoreTest {

	private static final String USERNAME         = "bruno1";
	private static final String USERNAME2        = "bruno2";
	private static final String EMAIL            = "bruno1@localhost";
	private static final String EMAIL2           = "bruno2@helloworld.com";
	private static final String INVALID_USERNAME = "";
	private static final String INVALID_EMAIL    = "helloworld@";
	private static UsersStore uStore; 
	
	@BeforeClass
	public static void oneTimeSetup(){
		uStore = new UsersStore();
	}
	
	@After //to reset the uStore state after each test
	public void afterTest(){
		uStore = new UsersStore();
	}
	
	@AfterClass
	public static void oneTimeTearDown(){
		uStore = null;
	}
	
	
	
	@Test //test the users store and valid user creation and respective get
	public void test() throws Exception{
		
		assertNotNull(uStore);
		uStore.createNewUser(USERNAME, EMAIL);
		User user = uStore.getUser(USERNAME);
		assertEquals(USERNAME, user.getUsername());
		assertEquals(EMAIL, user.getEmail());
			
	}
	
	//test user creation with repeated username
	@Test (expected = UserAlreadyExists_Exception.class)
	public void repeatedUsername() throws Exception{
			
		uStore.createNewUser(USERNAME, EMAIL);
		uStore.createNewUser(USERNAME, EMAIL2);
	}
	
	//test user creation with repeated email
	@Test (expected = EmailAlreadyExists_Exception.class)
	public void repeatedEmail() throws Exception{
			
		uStore.createNewUser(USERNAME, EMAIL);
		uStore.createNewUser(USERNAME2, EMAIL);
	}
	
	
	//test user creation with invalid username
	@Test (expected = InvalidUser_Exception.class)
	public void invalidUsername() throws Exception{
		
		uStore.createNewUser(INVALID_USERNAME, EMAIL);
	}
	
	//test user creation with invalid username
	@Test (expected = InvalidEmail_Exception.class)
	public void invalidEmail() throws Exception{
		
		uStore.createNewUser(USERNAME, INVALID_EMAIL);
	}
	
	//test the getUser method to a non existent user
	@Test(expected = UserDoesNotExist_Exception.class)
	public void invalidgetUser() throws Exception{
		uStore.getUser(USERNAME);
	}
	
	
	@Test // test the renew password method from a valid user
	public void validRenewPassword() throws Exception{
		
		uStore.createNewUser(USERNAME, EMAIL);
		User user = uStore.getUser(USERNAME);
		byte[] oldPass = user.getPassword();
		
		uStore.renewPassword(USERNAME);
		user = uStore.getUser(USERNAME);
		
		assertNotEquals(oldPass, user.getPassword());
	}
	
	
	//test the renewpassword from a nonexistent user
	@Test (expected = UserDoesNotExist_Exception.class)
	public void invalidRenewPasswordWrongUser() throws Exception{
		
		uStore.removeUser(USERNAME);
	}
	
	
	@Test //test the remove user method from 
	public void validRemoveUser() throws Exception{
		uStore.createNewUser(USERNAME, EMAIL);
		assertNotNull(uStore.getUser(USERNAME));
		
		uStore.removeUser(USERNAME);
		uStore.createNewUser(USERNAME, EMAIL);
		//that ensures that the remove user method worked fine
	}
	
	@Test (expected = UserDoesNotExist_Exception.class)
	public void invalidRemoveUser() throws Exception{
		uStore.removeUser(USERNAME);
	}
	
	@Test (expected = UserDoesNotExist_Exception.class)
	public void invalidRemoveUserNull() throws Exception{
		uStore.removeUser(null);
	}
	
	
	
}
