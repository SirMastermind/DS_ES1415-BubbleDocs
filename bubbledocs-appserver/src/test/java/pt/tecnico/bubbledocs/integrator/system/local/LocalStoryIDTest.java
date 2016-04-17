package pt.tecnico.bubbledocs.integrator.system.local;

import org.junit.Test;

import pt.tecnico.bubbledocs.exception.UserDoesntExistsException;
import pt.tecnico.bubbledocs.integrator.LoginUserIntegrator;

public class LocalStoryIDTest extends LocalStory {
	
    private String root;
    
    private String token_tma;
    private String token_amz;
    
    private static final String USERNAME_A = "tma";
    private static final String EMAIL_A = "sir.mastermind94@gmail.com";
    private static final String PASSWORD_A = "tma3";
    
    private static final String USERNAME_B = "amz";
    private static final String EMAIL_B = "andriymz@hotmail.com";
    private static final String PASSWORD_B = "amz4";
    
    @Override
    public void populate4Test() {
        root = addUserToSession("root");
        introIDLocal();
    }
    
    private void introIDLocal() {
    	System.out.println(">>>>>>>>>> ID Story <<<<<<<<<<");
    	System.out.println(">>> In this story, only the following functionalities will be tested: ");
    	System.out.println("> Create user;");
    	System.out.println("> Login user;");
    	System.out.println("> Delete user;");
    	System.out.println("> Get user's info;");
    	System.out.println("> Get username from token; and");
    	System.out.println("> Renew password.");
    	System.out.println("<>");
    }
    
    private void introIDStory() {
    	System.out.println(">>> The instructions are the following: ");
    	System.out.println("> Creation of an user named <Tomas Martins Alves>;");
    	System.out.println("> Creation of an user named <Andriy Zabolotnyy>;");
    	System.out.println("> Login of the user <Tomas Martins Alves>;");
    	System.out.println("> Login of the user <Andriy Zabolotnyy>;");
    	System.out.println("> Get info from user <Tomas Martins Alves>;");
    	System.out.println("> Get username from token of user <Tomas Martins Alves>;");
    	System.out.println("> Renew password from user <Tomas Martins Alves>;");
    	System.out.println("> Login of the user <Tomas Martins Alves>;");
    	System.out.println("> Deletion of an user named <Andriy Zabolotnyy>;");
    	System.out.println("> Get info from user <Andriy Zabolotnyy>;");
    	System.out.println("<>");
    }
    
    @Test
    public void IDStorySuccess() {
    	
    	introIDStory();
    	
    	/*****************************************************************************************************************/
    	
    	// Creation of a user named <Tomas Martins Alves>
        createUserStory(root, USERNAME_A, EMAIL_A, "Tomas Martins Alves");
        
        // Creation of a user named <Andriy Zabolotnyy>
        createUserStory(root, USERNAME_B, EMAIL_B, "Andriy Zabolotnyy");
        
        /*****************************************************************************************************************/
		
		// Login of the user <Tomas Martins Alves>
        LoginUserIntegrator service_tma = loginUserStory(USERNAME_A, PASSWORD_A);
        token_tma = service_tma.getUserToken();
        System.out.println(">>> Token associated with user tma: " + token_tma);
		
		// Login of the user <Andriy Zabolotnyy>
        LoginUserIntegrator service_amz = loginUserStory(USERNAME_B, PASSWORD_B);
		token_amz = service_amz.getUserToken();
        System.out.println(">>> Token associated with user amz: " + token_amz);
		
		/*****************************************************************************************************************/
		
		getUserInfoStory(USERNAME_A);
		
		/*****************************************************************************************************************/
		
		getUsernameFromTokenStory(token_tma);
		
		/*****************************************************************************************************************/
		
		renewPasswordStory(token_tma);
		
        System.out.println(">>> Test to see if the password is the same");
		if(!getUserFromUsername(USERNAME_A).getValidPassword()) {
			System.out.println(">>>>> FATAL <<<<<");
			System.out.println(">>>>> Wrong password from user: " + USERNAME_A);
			System.out.println("<>;");
		}
		
		/*****************************************************************************************************************/
		
		deleteUserStory(root, USERNAME_B);
		try {
			getUserInfoStory(USERNAME_B);
		}
		catch (UserDoesntExistsException e) {
			System.out.println(">>>>> FATAL <<<<<");
			System.out.println(">>>>> Unable to find information from user: " + USERNAME_B);
			System.out.println("<>;");
		}
    }
}