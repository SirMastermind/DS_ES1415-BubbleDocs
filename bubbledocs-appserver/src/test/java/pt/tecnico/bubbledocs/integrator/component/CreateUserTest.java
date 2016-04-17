package pt.tecnico.bubbledocs.integrator.component;

import static org.junit.Assert.*;

import org.junit.Test;

import pt.tecnico.bubbledocs.domain.User;
import pt.tecnico.bubbledocs.exception.*;
import pt.tecnico.bubbledocs.integrator.BubbleDocsServiceTest;
import pt.tecnico.bubbledocs.integrator.CreateUserIntegrator;
import pt.tecnico.bubbledocs.service.remote.IDRemoteServices;
import mockit.*;


public class CreateUserTest extends BubbleDocsServiceTest {

    // the tokens
    private String root;
    private String ars;

    private static final String USERNAME = "ars";
    private static final String USERNAME2 = "jose";
    private static final String EMAIL = "root@ist.pt";
    private static final String USERNAME_DOES_NOT_EXIST = "no-one";

    @Override
    public void populate4Test() {
        createUser(USERNAME, EMAIL, "António Rito Silva");
        root = addUserToSession("root");
        ars = addUserToSession("ars");
    }

    // Test 2.01: Description : It tests the creation with valid parameters.
    @Test
    public void success() {
        CreateUserIntegrator service = new CreateUserIntegrator(root, USERNAME_DOES_NOT_EXIST, EMAIL,
                "José Ferreira");
        
		new MockUp<IDRemoteServices>() {
			   @Mock
			   public void createUser(String username, String email) { }
		};
        
        service.execute();

	    // User is the domain class that represents a User
        User user = getUserFromUsername(USERNAME_DOES_NOT_EXIST);

        assertEquals(USERNAME_DOES_NOT_EXIST, user.getUsername());
        assertEquals(EMAIL, user.getEmail());
        assertEquals("José Ferreira", user.getName());
    }

    // Test 2.02: Description : It tests the creation with valid parameters, but a duplicated username.
    @Test(expected = UserAlreadyExistsException.class)
    public void usernameExists() {
		new MockUp<IDRemoteServices>() {
			   @Mock
			   public void createUser(String username, String email) {
				   throw new UserAlreadyExistsException();
			   }
		};
        CreateUserIntegrator service = new CreateUserIntegrator(root, USERNAME, EMAIL,
                "José Ferreira");
        service.execute();
    }

    // Test 2.03: Description : It tests the creation with invalid parameters, namely an empty new username.
    @Test(expected = InvalidUsernameException.class)
    public void emptyUsername() {
		new MockUp<IDRemoteServices>() {
			   @Mock
			   public void createUser(String username, String email) {
				   throw new InvalidUsernameException();
			   }
		};
		
        CreateUserIntegrator service = new CreateUserIntegrator(root, "", EMAIL, "José Ferreira");
        service.execute();
    }

    // Test 2.04: Description : It tests the creation with invalid parameters, namely an unauthorised user root.
    @Test(expected = UnauthorizedOperationException.class)
    public void unauthorizedUserCreation() {
        CreateUserIntegrator service = new CreateUserIntegrator(ars, USERNAME_DOES_NOT_EXIST, EMAIL,
                "José Ferreira");
        service.execute();
    }

    // Test 2.05: Description : It tests the creation with invalid parameters, namely an user not in session.
    @Test(expected = UserNotInSessionException.class)
    public void accessUsernameNotExist() {
        removeUserFromSession(root);
        CreateUserIntegrator service = new CreateUserIntegrator(root, USERNAME_DOES_NOT_EXIST, EMAIL,
                "José Ferreira");
        service.execute();
    }
   
    // Test 2.06: Description : It tests the creation of a user with the server SD-ID down.
    @Test(expected = UnavailableServiceException.class)
    public void testServiceAvailability(){
		new MockUp<IDRemoteServices>() {
			   @Mock
			   public void createUser(String username, String email) {
				   throw new RemoteInvocationException();
			   }
		};
    	CreateUserIntegrator service = new CreateUserIntegrator(root, USERNAME, EMAIL,
                "José Ferreira");
    	service.execute();
    }
    
    // Test 2.07: Description : It tests the creation of a user with the function call failing and the consequent removal of the user.
    @Test
    public void userDeleted() {
        CreateUserIntegrator service = new CreateUserIntegrator(root, USERNAME_DOES_NOT_EXIST, EMAIL,
                "José Ferreira");
        
		new MockUp<IDRemoteServices>() {
			   @Mock
			   public void createUser(String username, String email) {
				   throw new RemoteInvocationException();
			   }
		};
        try{
        	service.execute();
        } catch (UnavailableServiceException e) {
        	//Do Nothing
        }
        
        assertNull(getUserFromUsername(USERNAME_DOES_NOT_EXIST));
    }
    
    @Test(expected = DuplicateEmailException.class)
    public void emailExists() {
    	CreateUserIntegrator service = new CreateUserIntegrator(root, USERNAME2, EMAIL, "José Ferreira");
    	
		new MockUp<IDRemoteServices>() {
			   @Mock
			   public void createUser(String username, String email) {
				   throw new DuplicateEmailException();
			   }
		};
		
    			service.execute();
    			fail("DuplicateEmailException expected");
    }
}