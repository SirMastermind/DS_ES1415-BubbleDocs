package pt.tecnico.bubbledocs.domain;

import pt.ist.fenixframework.FenixFramework;
import pt.tecnico.bubbledocs.exception.*;
import java.util.Set;

public class BubbleDocsServer extends BubbleDocsServer_Base {
    
	/*
	 * Function that gets the BubbleDocsServer Instance - Singleton
	 */
    public static BubbleDocsServer getInstance() {
		BubbleDocsServer bd = FenixFramework.getDomainRoot().getBubbleDocsServer();
		if (bd == null)
		    bd = new BubbleDocsServer();

		return bd;
    }
    
    /*
     * Function to set the Singleton instance
     */
    private BubbleDocsServer() {
    	addUsers(new User("root", "rootroot", "Root User"));
    	setSessionManager(new SessionManager());
		FenixFramework.getDomainRoot().setBubbleDocsServer(this);
    }
    
    /*
     * Function that gets the User Object from the app User's set by its username
     */
    private User getUserByName(String username) {
		for(User user : getUsersSet()) {
		    if(user.getUsername().equals(username)) {
		    	return user;
			}
		}
		
		return null;
	}
    
    /*
     * Functions that recognizes application users
     */
    public boolean hasUser(String username) {
    	return getUserByName(username) != null;
    }
    
    
	public boolean hasUser(User user){
    	return user != null && getUserByName(user.getUsername()) == user;
    }
	
	
	public User getUser(String username){
		User user = getUserByName(username);
		
		if(user == null)
			throw new UserDoesntExistsException();
		
		return user;
			
	}
    
	/*
     * Functions that recognizes application sheets
     */
    
    public Sheet getSheetByID(int sheetID) {
  		for(Sheet sheet : getSheetsSet()) {
  		    if(sheet.getId() == sheetID) {
  		    	return sheet;
  			}
  		}
  		return null;
  	}
    
    private Sheet getSheetByName(String name){
    	for(Sheet sheet : getSheetsSet()) {
  		    if(sheet.getName().equals(name)) {
  		    	return sheet;
  			}
  		}
  		return null;
    }
    
    
    public boolean hasSheet(Sheet sheet){
    	return sheet != null && getSheetByID(sheet.getId()) == sheet;
    }
    
	public boolean hasSheet(int docID) {
		return hasSheet(getSheetByID(docID));
	}
    
	//public sheet getters
	public Sheet getSheet(int sheetID){
		Sheet sheet = getSheetByID(sheetID);
		
		if(sheet == null)
			throw new SheetDoesntExistsException();
		
		return sheet;
	}
	
	public Sheet getSheet(String name){
		Sheet sheet = getSheetByName(name);
		
		if(sheet == null)
			throw new SheetDoesntExistsException();
		
		return sheet;
	}

    @Override
    public void addSheets(Sheet sheets){
    	super.addSheets(sheets);
    	setSheetNewID(getSheetNewID()+1);
    }

    
    /*
     * Function that removes sheets from the aplication
     */
    
    public void removeSheet(User deleter, int sheetID) throws UserDoesntExistsException, SheetDoesntExistsException, UnauthorizedOperationException, UserNotInSessionException{
    	
    	Sheet sheet = getSheet(sheetID);
    	
    	if(!hasUser(deleter)){
    		throw new UserDoesntExistsException();
    	}
    	if(!(sheet.getOwner().getUsername().equals(deleter.getUsername()))){
    		throw new UnauthorizedOperationException();
    	}
    	
    	sheet.delete();
    	
    }
    

    /*
     * Function that adds Write Permissions to one user
     */
    
    public void addWritePermissions(User user1, String user2Username, int sheetID) throws UserDoesntExistsException, SheetDoesntExistsException, UnauthorizedOperationException, UserNotInSessionException{
    	
    	User user2 = getUser(user2Username);
    	Sheet sheet = getSheet(sheetID);
    	
    	if(!hasUser(user1)){
    		throw new UserDoesntExistsException();
    	}  	
    	if(!user1.hasWritePermissions(sheetID)){
    		throw new UnauthorizedOperationException();
    	}
    	
    	user2.addWriteModeSheets(sheet);	
    }

    
    /*
     * Function that removes permissions from one user
     */
    
    public void addReadPermissions(User user1, String user2Username, int sheetID) throws UserDoesntExistsException, SheetDoesntExistsException, UnauthorizedOperationException, UserNotInSessionException{
    	
    	User user2 = getUser(user2Username);
    	Sheet sheet = getSheet(sheetID);
    	
    	if(!hasUser(user1)){
    		throw new UserDoesntExistsException();
    	}   	
    	if(!user1.hasWritePermissions(sheetID)){
    		throw new UnauthorizedOperationException();
    	}
    	
    	user2.addReadModeSheets(sheet);	
    	
    }
    
    
    /*
     * Function that Removes perssions from one user
     */
    
    public void removePermissions(User user1, String user2Username, int sheetID) throws UserDoesntExistsException, SheetDoesntExistsException, UnauthorizedOperationException, UserNotInSessionException{
    	
    	User user2 = getUser(user2Username);
    	Sheet sheet = getSheet(sheetID);
    	
    	if(!hasUser(user1)){
    		throw new UserDoesntExistsException();
    	}
    	if(!user1.hasWritePermissions(sheetID)){
    		throw new UnauthorizedOperationException();
    	}
    	
    	user2.removePermissions(sheet);
    }
    
        

    /*
     * Function that return the user sheets
     */
    
	public Set<Sheet> getUserSheets(String username) throws UserDoesntExistsException {
		
    	User owner = getUser(username);
    	return owner.getOwnedSheetsSet();
    }
	
    /*
     * Function that return the user sheets with that name
     */
	
	public Sheet getUserSheets(String username, String sheetName) throws UserDoesntExistsException, SheetDoesntExistsException{
	
    	User owner = getUser(username);
		
		if(owner.getOwnedSheetsSet().isEmpty()) 
			throw new SheetDoesntExistsException();
		
		for (Sheet sheet : owner.getOwnedSheetsSet()) {
			return sheet; //return the first as requested in project manual
	    }
		
		return null;
    }
	
	
	public void logOut(String userToken) throws UserNotInSessionException {
		SessionManager sm = getSessionManager();
		User user = getUserFromSession(userToken);
		sm.logoutUser(user);
	}
	
	public User getUserFromSession(String token) throws UserNotInSessionException{
		SessionManager sm = getSessionManager();
    	return sm.getUserFromToken(token);
	}




	

















}
