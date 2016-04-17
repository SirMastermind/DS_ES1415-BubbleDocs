package pt.tecnico.bubbledocs.integrator;

import pt.tecnico.bubbledocs.exception.*;
import pt.tecnico.bubbledocs.service.local.CreateUser;
import pt.tecnico.bubbledocs.service.local.DeleteUser;
import pt.tecnico.bubbledocs.service.remote.IDRemoteServices;

public class CreateUserIntegrator extends BubbleDocsIntegrator {

	private String userToken;
	private String newUsername;
	private String email;
	private String name;
	private IDRemoteServices idrs;
	private CreateUser service;
	
    public CreateUserIntegrator(String userToken, String newUsername, String email, String name) {
	
    	this.userToken = userToken;
    	this.newUsername = newUsername;
    	this.email = email;
    	this.name = name;
    	this.idrs = new IDRemoteServices();
    	service = new CreateUser(userToken, newUsername, email, name);
    }
     
    public String getUsername(){
    	return newUsername;
    }
    
    public String getEmail(){
    	return email;
    }
    
    public String getName(){
    	return name;
    }
    
    public IDRemoteServices getRemoteService(){
    	return idrs;
    }
    
	@Override
	protected void dispatch() throws BubbleDocsException {
		this.service.execute();
		try {
			 idrs.createUser(this.newUsername, this.email);
		} catch (RemoteInvocationException rie) {
			// compensation transaction
			new DeleteUser(this.userToken, newUsername).execute();
			throw new UnavailableServiceException();
		} catch (LoginBubbleDocsException e) {
			// compensation transaction
			new DeleteUser(this.userToken, newUsername).execute();
			throw new LoginBubbleDocsException();
		}
	}
}