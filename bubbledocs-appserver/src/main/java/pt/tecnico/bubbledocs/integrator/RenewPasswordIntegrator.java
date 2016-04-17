package pt.tecnico.bubbledocs.integrator;

import pt.tecnico.bubbledocs.exception.BubbleDocsException;
import pt.tecnico.bubbledocs.exception.RemoteInvocationException;
import pt.tecnico.bubbledocs.exception.UnavailableServiceException;
import pt.tecnico.bubbledocs.service.local.GetUsername4Token;
import pt.tecnico.bubbledocs.service.local.RenewPassword;
import pt.tecnico.bubbledocs.service.local.RevalidatePassword;
import pt.tecnico.bubbledocs.service.remote.IDRemoteServices;

public class RenewPasswordIntegrator extends BubbleDocsIntegrator {

	private IDRemoteServices remoteService;
	private RenewPassword localService;
	private RevalidatePassword localRevalidatePasswordService;
	private GetUsername4Token username4tokenService;
	
	public RenewPasswordIntegrator(String userToken){
		this.remoteService = new IDRemoteServices(); 
		this.localService = new RenewPassword(userToken);
		this.localRevalidatePasswordService = new RevalidatePassword(userToken);
		this.username4tokenService = new GetUsername4Token(userToken);
	}
	

	private IDRemoteServices getRemoteService() {
		return remoteService;
	}
	
	private RenewPassword getLocalService() {
		return localService;
	}
	
	private RevalidatePassword getLocalRevalidatePasswordService() {
		return localRevalidatePasswordService;
	}

	private GetUsername4Token getUsername4TokenService(){
		return username4tokenService;
	}

	
	private String getUsernameFromToken(){
		getUsername4TokenService().execute();
		return getUsername4TokenService().getUsername();
	}
	
	@Override
	protected void dispatch() throws BubbleDocsException {
		
		getLocalService().execute();
		
		try{
			getRemoteService().renewPassword(getUsernameFromToken());
		}
		catch(RemoteInvocationException e){
			getLocalRevalidatePasswordService().execute();
			throw new UnavailableServiceException();
		}
	}
}
