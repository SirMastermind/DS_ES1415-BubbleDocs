package pt.tecnico.bubbledocs.service.local;

import pt.tecnico.bubbledocs.exception.UserNotInSessionException;


public class RenewPassword extends SessionBasedService {

	
	public RenewPassword(String token){
		super(token);
	}
	
	@Override
	protected void dispatch() throws UserNotInSessionException {
			getUser().setValidPassword(false);
	}
}
