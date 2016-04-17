package pt.tecnico.bubbledocs.integrator.system.remote;

import pt.tecnico.bubbledocs.service.remote.IDRemoteServices;
import org.junit.Test;
import pt.tecnico.bubbledocs.exception.*;
import pt.tecnico.bubbledocs.integrator.BubbleDocsServiceTest;

// add needed import declarations

public class LoginUserTestIT extends BubbleDocsServiceTest {

    private static final String USERNAME_ALICE = "alice";
    private static final String WRONGUSERNAME = "user";
    private static final String PASSWORD_ALICE = "Aaa1";
    private static final String WRONGPASSWORD = "lol";
    


    // Test 1.01: Description: It tests the login with valid parameters.
    @Test
    public void success() {

       IDRemoteServices service = new IDRemoteServices();
        service.loginUser(USERNAME_ALICE, PASSWORD_ALICE);
    }

    //Test 1.02: Description : It tests the login with valid parameters again.
    @Test
    public void successLoginTwice() {
    	
    	IDRemoteServices service = new IDRemoteServices();
    	service.loginUser(USERNAME_ALICE, PASSWORD_ALICE);
    	service.loginUser(USERNAME_ALICE, PASSWORD_ALICE);
    }

    // Test 1.03: Description : It tests the login with invalid parameters, specifically a wrong password.
    @Test(expected = LoginBubbleDocsException.class)
    public void loginWrongPassword() { 
    	IDRemoteServices service= new IDRemoteServices();
    	service.loginUser(USERNAME_ALICE, WRONGPASSWORD);
    }

    // Test 1.04: Description : It tests the login with invalid parameters, specifically a wrong user.
    @Test(expected = LoginBubbleDocsException.class)
    public void loginUnknowUser() {
    	IDRemoteServices service = new IDRemoteServices();
    	service.loginUser(WRONGUSERNAME, PASSWORD_ALICE);
    }
  
    
    

}