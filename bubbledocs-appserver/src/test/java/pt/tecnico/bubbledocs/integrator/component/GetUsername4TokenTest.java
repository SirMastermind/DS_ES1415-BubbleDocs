package pt.tecnico.bubbledocs.integrator.component;

import static org.junit.Assert.*;

import org.junit.Test;

import pt.tecnico.bubbledocs.exception.InvalidDataException;
import pt.tecnico.bubbledocs.exception.UserNotInSessionException;
import pt.tecnico.bubbledocs.integrator.BubbleDocsServiceTest;
import pt.tecnico.bubbledocs.service.local.GetUsername4Token;

public class GetUsername4TokenTest extends BubbleDocsServiceTest {
	
	private String token;
	private static final String USERNAME = "lolito";
    private static final String EMAIL = "eheh@lel.com";
    private static final String NAME = "Lelandro";
    private static final String INVALIDTOKEN = "tokenlol";
    

    @Override
    public void populate4Test() {
        createUser(USERNAME, EMAIL, NAME );
        token = addUserToSession(USERNAME);
    }
    
    // Test 13.01: Description: Tests success case obtaining Username from token
    @Test
    public void successAtGettingUsername4Token() {
        GetUsername4Token service = new GetUsername4Token(token);
        service.execute();
       
        assertEquals(USERNAME,service.getUsername());

    }

	// Test 13.02 - It tests getting the username with invalid parameters, namely a bad user.
    @Test(expected = UserNotInSessionException.class)
    public void invalidTokenAtGettingUsername4Token(){
    	GetUsername4Token service = new GetUsername4Token(INVALIDTOKEN);
        service.execute();
    }
    
    // Test 13.03 - It tests getting the username with invalid parameters, namely a null value.
    @Test(expected = InvalidDataException.class)
    public void nullTokenAtGettingUsername4Token(){
    	GetUsername4Token service = new GetUsername4Token(null);
        service.execute();
    }
    
    // Test 13.04 - It tests getting the username with invalid parameters, namely an empty string.
    @Test(expected = UserNotInSessionException.class)
    public void emptyTokenAtGettingUsername4Token(){
    	GetUsername4Token service = new GetUsername4Token("");
        service.execute();
    }
    
    // Test 13.05 - It tests getting the username with invalid parameters, namely an empty string.
    @Test(expected = UserNotInSessionException.class)
    public void noSessionTokenAtGettingUsername4Token(){
    	removeUserFromSession(token);
    	GetUsername4Token service = new GetUsername4Token(token);
        service.execute();
    }
}