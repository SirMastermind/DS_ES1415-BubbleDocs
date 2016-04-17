package pt.tecnico.bubbledocs.integrator.system.remote;

import pt.tecnico.bubbledocs.exception.LoginBubbleDocsException;
import pt.tecnico.bubbledocs.integrator.BubbleDocsServiceTest;
import pt.tecnico.bubbledocs.service.remote.IDRemoteServices;

import org.junit.Test;




public class RenewPasswordTestIT extends BubbleDocsServiceTest {

	public static String INVALID_USERNAME = "invalid_user";
	public static String USERNAME_BRUNO = "bruno";



    // Test 8.01: Description : It tests the renew of a password of a user in session.
	@Test
	public void successRenewPassword(){
		IDRemoteServices service = new IDRemoteServices();
		 service.renewPassword(USERNAME_BRUNO);
	}
	
	@Test(expected=LoginBubbleDocsException.class)
	public void invalidUserRenewPassword(){
		IDRemoteServices service = new IDRemoteServices();
		 service.renewPassword(INVALID_USERNAME);
	}


}