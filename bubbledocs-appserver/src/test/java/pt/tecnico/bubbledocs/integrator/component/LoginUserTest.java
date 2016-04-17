package pt.tecnico.bubbledocs.integrator.component;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import pt.tecnico.bubbledocs.service.remote.IDRemoteServices;
import mockit.*;

import org.junit.Test;
import org.joda.time.LocalTime;
import org.joda.time.Seconds;

import pt.tecnico.bubbledocs.domain.*;
import pt.tecnico.bubbledocs.exception.*;
import pt.tecnico.bubbledocs.integrator.BubbleDocsServiceTest;
import pt.tecnico.bubbledocs.integrator.LoginUserIntegrator;

// add needed import declarations

public class LoginUserTest extends BubbleDocsServiceTest {

    private static final String USERNAME = "jplol";
    private static final String PASSWORD = "jp#";
    private static final String EMAIL = "jp@localhost.com";
    private static final String NAME = "Pereira";
    private static final String WRONGPASSWORD = "lol";
    

    @Override
    public void populate4Test() {
        createUser(USERNAME, EMAIL, NAME );
    }

    // returns the time of the last access for the user with token userToken.
    // It must get this data from the session object of the application
    private LocalTime getLastAccessTimeInSession(String userToken) {
    	SessionManager sm = getBubbleDocsServer().getSessionManager();
    	return sm.lastAccessTime(userToken);
    	
    }

    // Test 1.01: Description: It tests the login with valid parameters.
    @Test
    public void success() {
    	//mock options 
    	 new MockUp<IDRemoteServices>() {
    		
    		 @Mock
    		 public void loginUser(String username, String password){
    			 return;
    		 }
		};
		
        LoginUserIntegrator service = new LoginUserIntegrator(USERNAME, PASSWORD);
        assertNull("Local Password should be null before first login", getUserFromUsername(USERNAME).getPassword());
        service.execute();
        
        LocalTime currentTime = new LocalTime();
	
        String token = service.getUserToken();

        User user = getUserFromSession(service.getUserToken());
        assertEquals(USERNAME, user.getUsername());
        assertEquals(PASSWORD, user.getPassword());

	int difference = Seconds.secondsBetween(getLastAccessTimeInSession(token), currentTime).getSeconds();

		assertTrue("Access time in session not correctly set", difference >= 0);
		assertTrue("diference in seconds greater than expected", difference < 2);
    }

    //Test 1.02: Description : It tests the login with valid parameters again.
    @Test
    public void successLoginTwice() {
    	
		 new MockUp<IDRemoteServices>() {
			
			 @Mock
			 public void loginUser(String username, String password){
				 return;
			 }
		};
			
		LoginUserIntegrator service = new LoginUserIntegrator(USERNAME, PASSWORD);

        service.execute();
        String token1 = service.getUserToken();
              
        
        service.execute();
        String token2 = service.getUserToken();

        User user = getUserFromSession(token1);
        assertNull(user);
        user = getUserFromSession(token2);
        assertEquals(USERNAME, user.getUsername());
        assertEquals(PASSWORD, user.getPassword());
    }

    // Test 1.03: Description : It tests the login with invalid parameters, specifically a wrong user.
    @Test(expected = LoginBubbleDocsException.class)
    public void loginUnknownUser() {
    	
   	 	new MockUp<IDRemoteServices>() {
 		
			 @Mock
			 public void loginUser(String username, String password){
				throw new LoginBubbleDocsException();
			 }
   	 	};
		
   	 	new LoginUserIntegrator("jp2", "jp").execute();
    }

    // Test 1.04: Description : It tests the login with invalid parameters, specifically a wrong password.
    @Test(expected = LoginBubbleDocsException.class)
    public void loginUserWithinWrongPassword() {
    	
    	new MockUp<IDRemoteServices>() {
     		
			 @Mock
			 public void loginUser(String username, String password){
				throw new LoginBubbleDocsException();
			 }
  	 	};
		
  	 	new LoginUserIntegrator(USERNAME, "jp2").execute();
    }
    
    //Test 1.05: Description : It tests the login with the server SD-ID down and local password is not yet defined.
    @Test (expected = UnavailableServiceException.class)
    public void loginWithIDServerDown(){
    	
    	new MockUp<IDRemoteServices>() {
     		
			 @Mock
			 public void loginUser(String username, String password){
				throw new RemoteInvocationException();
			 }
  	 	};
		
  	  new LoginUserIntegrator(USERNAME, PASSWORD).execute();
				
    }
    
    // Test 1.06: Description : It tests the login with the server SD-ID down and local password does not match the given one.
    @Test (expected = UnavailableServiceException.class)
    public void loginWithIDServerDown2(){
    	
    	User user = getUserFromUsername(USERNAME);
    	user.setPassword(PASSWORD);
    	user.setValidPassword(true);
    	
    	new MockUp<IDRemoteServices>() {
     		
			 @Mock
			 public void loginUser(String username, String password){
				throw new RemoteInvocationException();
			 }
  	 	};
		
  	 	  new LoginUserIntegrator(USERNAME, WRONGPASSWORD).execute();
  	 	  
    }
    
    //Test 1.07: Description : It tests the login with the server SD-ID down and local password matches the given one.
    @Test
    public void loginWithSucessIDDown(){
    	
    	User user = getUserFromUsername(USERNAME);
    	user.setPassword(PASSWORD);
    	user.setValidPassword(true);
    	
    	new MockUp<IDRemoteServices>() {
     		
			 @Mock
			 public void loginUser(String username, String password){
				throw new RemoteInvocationException();
			 }
 	 	};
   		
	  new LoginUserIntegrator(USERNAME, PASSWORD).execute();

    }
}