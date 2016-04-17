package pt.tecnico.bubbledocs.integrator.component;

import mockit.*;
import pt.tecnico.bubbledocs.exception.RemoteInvocationException;
import pt.tecnico.bubbledocs.exception.UnavailableServiceException;
import pt.tecnico.bubbledocs.exception.UserNotInSessionException;
import pt.tecnico.bubbledocs.integrator.BubbleDocsServiceTest;
import pt.tecnico.bubbledocs.integrator.RenewPasswordIntegrator;
import pt.tecnico.bubbledocs.service.remote.IDRemoteServices;

import org.junit.Test;

import static org.junit.Assert.*;



public class RenewPasswordTest extends BubbleDocsServiceTest {

	public static String USERNAME = "andriy";
	public static String PASSWORD = "huehuehue";
	public static String INVALID_TOKEN = "invalidToken";
	String token;
	
	@Override
    public void populate4Test() {
		 createUser(USERNAME, PASSWORD, "Andriy Zabolotnyy");
		 this.token = addUserToSession(USERNAME);
    }
	
    // Test 8.01: Description : It tests the renew of a password of a user in session.
	@Test
	public void successRenewPassword(){
		RenewPasswordIntegrator service = new RenewPasswordIntegrator(this.token);
		
		new MockUp<IDRemoteServices>(){
			
			@Mock
			public void renewPassword(String username){
			}
		};
		 
		 service.execute();	
		 assertFalse(getUserFromUsername(USERNAME).getValidPassword());
	}
	
    // Test 8.02: Description : It tests the renew of a password of a user in session, but with the SD-ID server down.
	//The renewPassword fails remotely and should not be affected locally.
	@Test
	public void remoteInvocationExceptionAtRenewPassword(){
		RenewPasswordIntegrator service = new RenewPasswordIntegrator(this.token);
		
		new MockUp<IDRemoteServices>(){
			
			@Mock
			public void renewPassword(String username){
				throw new RemoteInvocationException();
			}
		};
	   
		try{
			service.execute();
		   fail("RemoteInvocationException not thrown");
		}
		catch(UnavailableServiceException e){
		  assertTrue(getUserFromUsername(USERNAME).getValidPassword());
		}
		catch(Exception e){
		  fail("RemoteInvocationException not thrown");
		} 	
	}	
	
    // Test 8.03: Description : It tests the renew of a password of a user not in session.
	//An exception should be caught (fails locally) and remote call isn't performed (isn't called remotely)
	@Test(expected=UserNotInSessionException.class)
	public void userNotInSessionAtRenewPassword(){
		RenewPasswordIntegrator service = new RenewPasswordIntegrator(INVALID_TOKEN);
		
		new MockUp<IDRemoteServices>(){
			
			@Mock
			public void renewPassword(String username){
				fail("Remote call shouldn't be done");
			}
		};
		
			service.execute(); 	
	}	
	
	@Test(expected=UserNotInSessionException.class)
	public void removeUserRenewPassword(){
		removeUserFromSession(this.token);
		RenewPasswordIntegrator service = new RenewPasswordIntegrator(this.token);
		
		new MockUp<IDRemoteServices>(){
			
			@Mock
			public void renewPassword(String username){
			}
		};
		 
		 service.execute();
	}
	
	@Test(expected=UserNotInSessionException.class)
	public void emptyUserRenewPassword(){
		RenewPasswordIntegrator service = new RenewPasswordIntegrator("");
		
		new MockUp<IDRemoteServices>(){
			
			@Mock
			public void renewPassword(String username){
			}
		};
		 
		 service.execute();
	}
	
	@Test(expected=UserNotInSessionException.class)
	public void nullUserRenewPassword(){
		RenewPasswordIntegrator service = new RenewPasswordIntegrator(null);
		
		new MockUp<IDRemoteServices>(){
			
			@Mock
			public void renewPassword(String username){
			}
		};
		 
		 service.execute();
	}
}