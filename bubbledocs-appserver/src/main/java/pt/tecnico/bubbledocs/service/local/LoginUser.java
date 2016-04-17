package pt.tecnico.bubbledocs.service.local;

import pt.tecnico.bubbledocs.domain.BubbleDocsServer;
import pt.tecnico.bubbledocs.domain.SessionManager;
import pt.tecnico.bubbledocs.domain.User;

import pt.tecnico.bubbledocs.exception.*;
import pt.tecnico.bubbledocs.toolkit.DataValidator;

public class LoginUser extends BubbleDocsService {

    private String userToken;
    private String username;
    private String password;
	private boolean remoteFailed;
    

    public LoginUser(String username, String password) {
    	this.username = username;
    	this.password = password;
    	this.remoteFailed = false;
    }
    
    public void setRemoteFailed(boolean remoteFailed){
    	this.remoteFailed = remoteFailed;
    }
    
    public String getUserToken() {
    	return userToken;
    }
    
    public String getUsername(){
    	return username;
    }
    
    public String getPassword(){
    	return password;
    }
    
    
	@Override
	protected void checkData(){
		
		if(!DataValidator.validString(getUsername()))
			throw new InvalidDataException();
		
		if(!DataValidator.validString(getPassword()))
			throw new InvalidDataException();
	}
	
    @Override
    protected void dispatch() throws BubbleDocsException {
    	
    	BubbleDocsServer bd = getBubbleDocsServer();
		SessionManager sm = bd.getSessionManager();
		User user = bd.getUser(username);
		
    	if(this.remoteFailed){
    		if(!user.getValidPassword()) 
    			throw new UnavailableServiceException();
    		
    		if(!user.getPassword().equals(password)) 
    			throw new UnavailableServiceException();
    		
    		this.userToken = sm.loginUser(user);
    	}
    	else{
    		user.setPassword(password);
    		user.setValidPassword(true);
    		this.userToken = sm.loginUser(user);
    	}

    }
    
    
    


}
