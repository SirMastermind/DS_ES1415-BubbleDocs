package pt.tecnico.bubbledocs.integrator.system.remote;

import static org.junit.Assert.*;

import org.junit.Test;
import pt.tecnico.bubbledocs.exception.*;
import pt.tecnico.bubbledocs.integrator.BubbleDocsServiceTest;
import pt.tecnico.bubbledocs.service.remote.IDRemoteServices;


public class CreateUserTestIT extends BubbleDocsServiceTest {


    private static final String USERNAME = "ars";
    private static final String USERNAME2 = "jose";
    private static final String EMAIL = "root@ist.pt";

    // Test 2.01: Description : It tests the creation with valid parameters.
    @Test
    public void success() {
        IDRemoteServices service = new IDRemoteServices();
        service.createUser(USERNAME, EMAIL);
	    // User is the domain class that represents a User
    }

    // Test 2.02: Description : It tests the creation with valid parameters, but a duplicated username.
    @Test(expected = LoginBubbleDocsException.class)
    public void usernameExists() {

        IDRemoteServices service = new IDRemoteServices();
        service.createUser(USERNAME, EMAIL);
    }

    // Test 2.03: Description : It tests the creation with invalid parameters, namely an empty new username.
    @Test(expected = LoginBubbleDocsException.class)
    public void emptyUsername() {

        IDRemoteServices service = new IDRemoteServices();
        service.createUser("", EMAIL);
    }
 
    @Test(expected = LoginBubbleDocsException.class)
    public void emailExists() {
    	IDRemoteServices service = new IDRemoteServices();
        service.createUser(USERNAME2, EMAIL);
		fail("DuplicateEmailException expected");

    }
}