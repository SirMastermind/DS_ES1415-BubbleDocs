package pt.tecnico.bubbledocs.integrator.component;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import pt.tecnico.bubbledocs.dto.UserDTO;
import pt.tecnico.bubbledocs.exception.UserDoesntExistsException;
import pt.tecnico.bubbledocs.integrator.BubbleDocsServiceTest;
import pt.tecnico.bubbledocs.service.local.GetUserInfo;


public class GetUserInfoTest extends BubbleDocsServiceTest {

    private static final String USERNAME = "jplol";
    private static final String EMAIL = "jp@localhost.com";
    private static final String NAME = "Pereira";
    private static final String WRONGUSER = "abc";
    

    @Override
    public void populate4Test() {
        createUser(USERNAME, EMAIL, NAME );
    }
    
    // Test 12.01 - It tests getting the info with valid parameters.
    @Test
    public void success() {
        GetUserInfo service = new GetUserInfo(USERNAME);
        //assertNull("Local Password should be null before first login", getUserFromUsername(USERNAME).getPassword());
        service.execute();
        
	
        UserDTO user = service.getUserDTO();
       
        assertEquals(EMAIL, user.getEmail());
        assertEquals(NAME, user.getName());
        assertEquals(USERNAME, user.getUsername());
    }

	// Test 12.02 - It tests getting the info with invalid parameters, namely a bad user.
    @Test(expected = UserDoesntExistsException.class)
    public void UserDoesNotExists(){
    	GetUserInfo service = new GetUserInfo(WRONGUSER);
    	service.execute();
    }
    
    // Test 12.03 - It tests getting the info with invalid parameters, namely a null value.
    @Test(expected = UserDoesntExistsException.class)
    public void NullUser(){
    	GetUserInfo service = new GetUserInfo(null);
    	service.execute();
    }
	
    // Test 12.04 - It tests getting the info with invalid parameters, namely an empty string.
    @Test(expected = UserDoesntExistsException.class)
    public void EmptyUser(){
    	GetUserInfo service = new GetUserInfo("");
    	service.execute();
    }
    
    //Test 12.05 - It tests getting the info with valid parameters, namely a user without session.  
    @Test
    public void NoSessionUser(){
    	removeUserFromSession(USERNAME);
    	GetUserInfo service = new GetUserInfo(USERNAME);
    	service.execute();
    }
}
