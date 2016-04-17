package pt.tecnico.bubbledocs.service.local;

import pt.tecnico.bubbledocs.domain.BubbleDocsServer;
import pt.tecnico.bubbledocs.domain.User;
import pt.tecnico.bubbledocs.exception.BubbleDocsException;
import pt.tecnico.bubbledocs.exception.InvalidDataException;

public class GetUsername4Token extends BubbleDocsService {

	private String token;
	private String username;
	
	public GetUsername4Token(String token){
		this.token = token;
	}
	
	public String getToken() {
		return this.token;
	}

	public String getUsername() {
		return username;
	}

	private void setUsername(String username) {
		this.username = username;
	}

	@Override
	protected void dispatch() throws BubbleDocsException {
		if(getToken() == null)
			throw new InvalidDataException();
		
			BubbleDocsServer bd = getBubbleDocsServer();
			User user = bd.getUserFromSession(getToken());
			
			setUsername(user.getUsername());
			
	}

	@Override
	protected void checkData() throws BubbleDocsException {
	}

}
