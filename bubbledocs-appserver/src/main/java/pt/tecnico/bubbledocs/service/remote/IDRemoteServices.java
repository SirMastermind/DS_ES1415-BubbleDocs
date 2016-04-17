package pt.tecnico.bubbledocs.service.remote;

import pt.tecnico.bubbledocs.exception.*;
import pt.ulisboa.tecnico.sdis.exception.NoAnswerFromServerException;
import pt.ulisboa.tecnico.sdis.exception.WServiceNotFoundException;
import pt.ulisboa.tecnico.sdis.id.SDIdClient;
import pt.ulisboa.tecnico.sdis.id.ws.*;

public class IDRemoteServices {
	
	private SDIdClient client = null;
	
	public IDRemoteServices(){
		
	}
	
	private void connect(){
		
		if(client!=null) return;
		
		try {
			this.client = new SDIdClient("SD-ID", "http://localhost:8081");
		} catch (WServiceNotFoundException | NoAnswerFromServerException e) {
			throw new RemoteInvocationException();
		}
	}
	
	public void createUser(String username, String email)
			throws InvalidUsernameException, UserAlreadyExistsException,
			DuplicateEmailException, InvalidEmailException,
			RemoteInvocationException {
				
			connect();
			
			try {
				client.createUser(username, email);
			} catch (NoAnswerFromServerException e) {
				throw new RemoteInvocationException();
			} catch (EmailAlreadyExists_Exception | InvalidEmail_Exception 
					| InvalidUser_Exception | UserAlreadyExists_Exception e) {
				throw new LoginBubbleDocsException();
			}
	}
	

	public void loginUser(String username, String password) throws LoginBubbleDocsException, RemoteInvocationException {

		connect();
		
		try {
			client.requestAuthentication(username, password);
		} catch (NoAnswerFromServerException e) {
			throw new RemoteInvocationException();
		} catch (AuthReqFailed_Exception e) {
			throw new LoginBubbleDocsException();
		}
			
	}
	
	public void removeUser(String username) throws LoginBubbleDocsException, RemoteInvocationException {
		connect();
		
		
		try {
			client.removeUser(username);
		} catch (NoAnswerFromServerException e) {
			throw new RemoteInvocationException();
		} catch (UserDoesNotExist_Exception e) {
			throw new LoginBubbleDocsException();
		}

	}

	public void renewPassword(String username) throws LoginBubbleDocsException, RemoteInvocationException {
			
		connect();
		
				try {
					client.renewPassword(username);
				} catch (NoAnswerFromServerException e) {
					throw new RemoteInvocationException();
				} catch (UserDoesNotExist_Exception e) {
					throw new LoginBubbleDocsException();
				}
	}
}
