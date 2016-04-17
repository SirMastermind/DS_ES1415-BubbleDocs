package pt.tecnico.bubbledocs.service.local;

import pt.tecnico.bubbledocs.domain.User;
import pt.tecnico.bubbledocs.dto.UserDTO;
import pt.tecnico.bubbledocs.exception.BubbleDocsException;
import pt.tecnico.bubbledocs.exception.RemoteInvocationException;
import pt.tecnico.bubbledocs.exception.UnavailableServiceException;

public class GetUserInfo extends BubbleDocsService {
	private String _username;
	private UserDTO _userDTO;
	public GetUserInfo(String username) {
		// TODO Auto-generated constructor stub
		_username = username;
	}

	@Override
	protected void dispatch() throws BubbleDocsException {
		
    	try{  
    		User user = getBubbleDocsServer().getUser(_username);
    		String name = user.getName();
    		String email = user.getEmail();
    		_userDTO = new UserDTO(_username, name, email);
   
    	} catch(RemoteInvocationException e){
    		throw new UnavailableServiceException();
    	}
    	
	}

	@Override
	protected void checkData() throws BubbleDocsException {
		// TODO Auto-generated method stub
		
	}

	public UserDTO getUserDTO() {
		return _userDTO;
	}

}
