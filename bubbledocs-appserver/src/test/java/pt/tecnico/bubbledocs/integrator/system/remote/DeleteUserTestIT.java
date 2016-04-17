package pt.tecnico.bubbledocs.integrator.system.remote;

import pt.tecnico.bubbledocs.service.remote.*;
import pt.tecnico.bubbledocs.exception.*;
import pt.tecnico.bubbledocs.integrator.BubbleDocsServiceTest;

import org.junit.Test;

// add needed import declarations

public class DeleteUserTestIT extends BubbleDocsServiceTest {

    private static final String USERNAME_SMF = "smf";
    private static final String EMAIL = "cenas@gmail.com";
    private static final String USERNAME_DOES_NOT_EXIST = "no-one";

    

    // Test 3.01: Description : It tests the removal with valid parameters, the user in session.
    @Test
    public void success() {
        IDRemoteServices service = new IDRemoteServices();
        service.createUser(USERNAME_SMF, EMAIL);
        service.removeUser(USERNAME_SMF);    
    }



    // Test 3.03: Description : It tests the removal with invalid parameters, the user does not exist.
    @Test(expected = LoginBubbleDocsException.class)
    public void userToDeleteDoesNotExist() {
    	IDRemoteServices service = new IDRemoteServices();
    	service.removeUser(USERNAME_DOES_NOT_EXIST);
    }
    


    
}
