package pt.tecnico.bubbledocs.integrator;


import pt.tecnico.bubbledocs.domain.BubbleDocsServer;
import pt.tecnico.bubbledocs.exception.BubbleDocsException;
import pt.tecnico.bubbledocs.exception.LoginBubbleDocsException;
import pt.tecnico.bubbledocs.exception.RemoteInvocationException;
import pt.tecnico.bubbledocs.exception.UnavailableServiceException;
import pt.tecnico.bubbledocs.exception.UserDoesntExistsException;
import pt.tecnico.bubbledocs.service.local.CreateUser;
import pt.tecnico.bubbledocs.service.local.DeleteUser;
import pt.tecnico.bubbledocs.service.local.GetUserInfo;
import pt.tecnico.bubbledocs.service.remote.IDRemoteServices;

public class DeleteUserIntegrator extends BubbleDocsIntegrator {
	
	private String usernameTodelete;
	private String userToken;
	private DeleteUser localDeleteUserservice;
	private IDRemoteServices remoteDeleteUserservice;
	private GetUserInfo userinfoservice;
	
	public DeleteUserIntegrator(String userToken, String usernametodelete){
		this.usernameTodelete = usernametodelete;
		this.userToken = userToken;
		this.localDeleteUserservice = new DeleteUser(userToken, usernametodelete);
		this.remoteDeleteUserservice = new IDRemoteServices();
		this.userinfoservice = new GetUserInfo(usernametodelete);
		
	}
	
    public String getUsernameToDelete(){
    	return usernameTodelete;
    }
    
	public String getUserToken(){
		return userToken;
	}
	
	public IDRemoteServices getIDRemoteservices(){
		return this.remoteDeleteUserservice;
	}
	
	public DeleteUser getDeleteUser(){
		return this.localDeleteUserservice;				
	}
	
	public GetUserInfo getUserInfo(){
		return this.userinfoservice;
	}
	

	@Override
	protected void dispatch() throws BubbleDocsException {
		String email;
		String name;
		
		BubbleDocsServer bd = getBubbleDocsServer();
		if(!bd.hasUser(this.usernameTodelete))
			throw new UserDoesntExistsException();
		
		userinfoservice.execute();
		email = userinfoservice.getUserDTO().getEmail();
		name = userinfoservice.getUserDTO().getName();
		
		localDeleteUserservice.execute();
		 try {
			 getIDRemoteservices().removeUser(this.usernameTodelete);
			 
		 } catch (RemoteInvocationException rie) {
			 // compensation transaction
			 new CreateUser(this.userToken, this.usernameTodelete, email, name).execute();
			 throw new UnavailableServiceException();
		 } catch (LoginBubbleDocsException e) {
			 // compensation transaction
			 new CreateUser(this.userToken, this.usernameTodelete, email, name).execute();
			 throw new LoginBubbleDocsException();
		 }
		
	}

}
