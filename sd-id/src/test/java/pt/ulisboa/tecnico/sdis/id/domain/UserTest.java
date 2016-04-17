package pt.ulisboa.tecnico.sdis.id.domain;

import static org.junit.Assert.*;

import org.junit.*;

public class UserTest {
	
	/*
	 * Very simple test to the domain user class
	 */

	private static final String USERNAME = "bruno";
	private static final String EMAIL    = "bruno@localhost";
	
	@BeforeClass
	public static void oneTimeSetup(){
		//NOTHING TO DO
	}
	
	@AfterClass
	public static void oneTimeTearDown(){
		//NOTHING TO DO
	}
	
	@Test
	public void test(){
		User user = new User(USERNAME, EMAIL);
		byte[] oldPass;
		
		//test user creation and attrs
		assertNotNull("user not created", user);
		assertNotNull("password not created", user.getPassword());
		assertEquals(USERNAME, user.getUsername());
		assertEquals(EMAIL, user.getEmail());
		
		 //test the generateNewPassword method
		 oldPass = user.getPassword();
		 user.generateNewPassword();
		 assertNotEquals(oldPass, user.getPassword());
	}
	
	

}
