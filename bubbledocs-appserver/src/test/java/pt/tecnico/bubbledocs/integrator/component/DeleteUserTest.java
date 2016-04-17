package pt.tecnico.bubbledocs.integrator.component;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import pt.tecnico.bubbledocs.domain.User;
import pt.tecnico.bubbledocs.service.remote.*;
import pt.tecnico.bubbledocs.exception.*;
import pt.tecnico.bubbledocs.integrator.BubbleDocsServiceTest;
import pt.tecnico.bubbledocs.integrator.DeleteUserIntegrator;
import mockit.*;

import org.junit.Test;

// add needed import declarations

public class DeleteUserTest extends BubbleDocsServiceTest {

    private static final String USERNAME_TO_DELETE = "smf";
    private static final String USERNAME = "ars";
    private static final String PASSWORD = "ars";
    private static final String ROOT_USERNAME = "root";
    private static final String USERNAME_DOES_NOT_EXIST = "no-one";
    private static final String SPREADSHEET_NAME = "spread";

    // the tokens for user root
    private String root;

    @Override
    public void populate4Test() {
        createUser(USERNAME, PASSWORD, "António Rito Silva");
        User smf = createUser(USERNAME_TO_DELETE, "smf", "Sérgio Fernandes");
        createSpreadSheet(smf, USERNAME_TO_DELETE, 20, 20);

        root = addUserToSession(ROOT_USERNAME);
    };

    // Test 3.01: Description : It tests the removal with valid parameters, the user in session.
    @Test
    public void success() {
        DeleteUserIntegrator service = new DeleteUserIntegrator(root, USERNAME_TO_DELETE);
        
        new MockUp<IDRemoteServices>() {
			   @Mock
			   public void removeUser(String username){}
		};
        service.execute();
        
        
        boolean deleted = getUserFromUsername(USERNAME_TO_DELETE) == null;
        assertTrue("user was not deleted", deleted);
        assertNull("Spreadsheet was not deleted", getSpreadSheet(SPREADSHEET_NAME));
    }

    // Test 3.02: Description : It tests the removal with valid parameters, the user not in session.
    @Test
    public void successToDeleteIsNotInSession() {
    	removeUserFromSession(USERNAME_TO_DELETE);
        success();
    }

    // Test 3.03: Description : It tests the removal with invalid parameters, the user does not exist.
    @Test(expected = UserDoesntExistsException.class)
    public void userToDeleteDoesNotExist() {
    	new MockUp<IDRemoteServices>() {
			   @Mock
			   public void removeUser(String username){
				   throw new UserDoesntExistsException();
			   }
		};
        new DeleteUserIntegrator(root, USERNAME_DOES_NOT_EXIST).execute();
    }
    
    // Test 3.04: Description : It tests the removal with invalid parameters, the root user argument isn't the root.
    @Test(expected = UnauthorizedOperationException.class)
    public void notRootUser() {
        String ars = addUserToSession(USERNAME);
        new DeleteUserIntegrator(ars, USERNAME_TO_DELETE).execute();
    }

    // Test 3.05: Description : It tests the removal with invalid parameters, the root user isn't in session.
    @Test(expected = UserNotInSessionException.class)
    public void rootNotInSession() {
        removeUserFromSession(root);
        new DeleteUserIntegrator(root, USERNAME_TO_DELETE).execute();
    }

    // Test 3.06: Description : It tests the removal with invalid parameters, the root user argument isn't the root and it isn't in session.
    @Test(expected = UserNotInSessionException.class)
    public void notInSessionAndNotRoot() {
        String ars = addUserToSession(USERNAME);
        removeUserFromSession(ars);
        new DeleteUserIntegrator(ars, USERNAME_TO_DELETE).execute();
    }

    // Test 3.07: Description : It tests the removal with invalid parameters, the user root does not exist.
    @Test(expected = UserNotInSessionException.class)
    public void accessUserDoesNotExist() {
        new DeleteUserIntegrator(USERNAME_DOES_NOT_EXIST, USERNAME_TO_DELETE).execute();
    }
    
    // Test 3.08: Description : It tests the removal of the user from the domain and the session.
    @Test
    public void successToDeleteIsInSession() {
        String token = addUserToSession(USERNAME_TO_DELETE);
        success();
        assertNull("Removed user but not removed from session", getUserFromSession(token));
    }
    
    // Test 3.09: Description : It tests the removal of a user with the server SD-ID down.
    @Test(expected = UnavailableServiceException.class)
    public void deleteUserWithServerDown(){
        new MockUp<IDRemoteServices>() {
			   @Mock
			   public void removeUser(String username){
				   throw new RemoteInvocationException();
			   }
		};
		
    	new DeleteUserIntegrator(root, USERNAME_TO_DELETE).execute();
    }
    
    
    // Test 3.10: Description : It ensures the user was not deleted due to the RemoteInvocationException.
    @Test
    public void checkUserWasnotDeleted(){
        new MockUp<IDRemoteServices>() {
			   @Mock
			   public void removeUser(String username){
				   throw new RemoteInvocationException();
			   }
		};
        try{
        	new DeleteUserIntegrator(root, USERNAME_TO_DELETE).execute();
        	fail();
        }
        catch(UnavailableServiceException rie){
        	assertNotNull(getUserFromUsername(USERNAME_TO_DELETE));
        }
        catch(Exception e){
        	fail();
        }
    }
}
