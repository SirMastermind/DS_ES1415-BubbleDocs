package pt.tecnico.bubbledocs.service.local;

import pt.tecnico.bubbledocs.domain.BubbleDocsServer;
import pt.tecnico.bubbledocs.domain.User;
import pt.tecnico.bubbledocs.exception.UserNotInSessionException;

public abstract class SessionBasedService extends BubbleDocsService{
	
	private String userToken;
	private User user;
	
	protected SessionBasedService(String userToken){
		this.userToken = userToken;
	}
	
	protected String getUserToken(){
		return userToken;
	}
	
	public User getUser(){
		return user;
	}
	
	@Override
	protected void checkData() throws UserNotInSessionException{
		BubbleDocsServer bd = getBubbleDocsServer();
		this.user = bd.getUserFromSession(getUserToken()); //throws exception if not in session
	}
	
}
