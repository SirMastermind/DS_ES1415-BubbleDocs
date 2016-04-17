package pt.tecnico.bubbledocs.integrator;

import pt.tecnico.bubbledocs.exception.BubbleDocsException;
import pt.tecnico.bubbledocs.exception.RemoteInvocationException;
import pt.tecnico.bubbledocs.service.local.LoginUser;
import pt.tecnico.bubbledocs.service.remote.IDRemoteServices;

public class LoginUserIntegrator extends BubbleDocsIntegrator {
	
	private IDRemoteServices remoteService;
	private LoginUser localService;
	private String username;
	private String password;
	
	public LoginUserIntegrator(String username, String password) {
		this.username = username;
		this.password = password;
		this.localService = new LoginUser(username, password);
		this.remoteService = new IDRemoteServices();
	}
	
	protected LoginUser getLocalService(){
		return this.localService;
	}
	
	protected IDRemoteServices getRemoteService(){
		return this.remoteService;
	}
	
	protected String getUsername(){
		return username;
	}
	
	protected String getPassword(){
		return password;
	}
	
	public String getUserToken() {
		return getLocalService().getUserToken();
	}
	
	@Override
	protected void dispatch() throws BubbleDocsException {
		
		try{
			getRemoteService().loginUser(getUsername(), getPassword());
			getLocalService().execute();
		}
		catch(RemoteInvocationException e){
			getLocalService().setRemoteFailed(true);
			getLocalService().execute();
		}
	}



}
