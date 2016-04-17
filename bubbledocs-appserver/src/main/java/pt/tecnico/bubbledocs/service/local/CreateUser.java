package pt.tecnico.bubbledocs.service.local;

import pt.tecnico.bubbledocs.domain.User;
import pt.tecnico.bubbledocs.exception.*;

public class CreateUser extends SessionBasedService {

	private String newUsername;
	private String email;
	private String name;
	
    public CreateUser(String userToken, String newUsername, String email, String name) {
	
    	super(userToken);
    	this.newUsername = newUsername;
    	this.email = email;
    	this.name = name;
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
   
    
    @Override
    protected void checkData(){
    	super.checkData();
    	if(!getUser().isRoot()) 
    		throw new UnauthorizedOperationException();
    		
    }
	
    @Override
    protected void dispatch() throws BubbleDocsException {
    	
    	try{    	
	    	getBubbleDocsServer().addUsers(new User(getUsername(), getEmail(), getName()));
    	} catch(RemoteInvocationException e){
    		throw new UnavailableServiceException();
    	}

    }
}