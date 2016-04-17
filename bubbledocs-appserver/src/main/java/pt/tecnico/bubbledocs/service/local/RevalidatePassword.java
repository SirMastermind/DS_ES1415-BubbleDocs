package pt.tecnico.bubbledocs.service.local;

import pt.tecnico.bubbledocs.exception.BubbleDocsException;

public class RevalidatePassword extends SessionBasedService {

	public RevalidatePassword(String userToken) {
		super(userToken);
	}

	@Override
	protected void dispatch() throws BubbleDocsException {
		getUser().setValidPassword(true);
	}
}
