package pt.tecnico.bubbledocs.service.local;

import pt.tecnico.bubbledocs.domain.BubbleDocsServer;
import pt.tecnico.bubbledocs.domain.User;
import pt.tecnico.bubbledocs.exception.*;


public class DeleteUser extends SessionBasedService {

	private String toDeleteUsername;
	
    public DeleteUser(String userToken, String toDeleteUsername) {
    	super(userToken);
    	this.toDeleteUsername = toDeleteUsername;
    }
    
    
    public String getUsernameToDelete(){
    	return toDeleteUsername;
    }
    
    @Override
    protected void checkData(){
    	super.checkData();
    	
    	if(!getBubbleDocsServer().hasUser(getUsernameToDelete()))
    		throw new UserDoesntExistsException();
    	if(!getUser().isRoot()) 
    		throw new UnauthorizedOperationException();
    		
    }
    
    @Override
    protected void dispatch() throws BubbleDocsException{
    		BubbleDocsServer bd = getBubbleDocsServer();
        	User removedUser = bd.getUser(getUsernameToDelete());
          	bd.removeUsers(removedUser);
        	removedUser.delete();
    }

}
