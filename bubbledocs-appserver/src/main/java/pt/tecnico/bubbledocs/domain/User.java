package pt.tecnico.bubbledocs.domain;

import pt.tecnico.bubbledocs.exception.InvalidUsernameException;

public class User extends User_Base {
    
	private static final int minUsernameLength = 3;
	private static final int maxUsernameLength = 8;
	
	public User(){
		  super();
	 }
	
  
	 public User(String username, String email, String name) {
	        super();
	        
	        if(!validUsername(username))
	        	throw new InvalidUsernameException();
	        	
	        setUsername(username);
	        setPassword(null);
	        setValidPassword(false);
	        setEmail(email);
	        setName(name);
	 }
	 
	private boolean validUsername(String username){
		return username.length()>=minUsernameLength 
				&& username.length()<=maxUsernameLength;
	}
 
	 public boolean isRoot() {
		 return getUsername() == "root";
	 }
	 
	 
	 /*
	  * Get sheets that user has access to
	  */
	 
	 public Sheet getOwnedSheet(int sheetID){
		 for(Sheet sheet : getOwnedSheetsSet()) {
		    if(sheet.getId() == sheetID) {
		    	return sheet;
			}
		}
		return null;
	 }
	 
	 
	 public Sheet getReadModeSheet(int sheetID){
		 for(Sheet sheet : getReadModeSheetsSet()) {
		    if(sheet.getId() == sheetID) {
		    	return sheet;
			}
		}
		return null;
	 }
	 
	 public Sheet getWriteModeSheet(int sheetID){
		 for(Sheet sheet : getWriteModeSheetsSet()) {
		    if(sheet.getId() == sheetID) {
		    	return sheet;
			}
		}
		return null;
	 }
	 
	 /*
	  * Tests user permissions about one sheet
	  */
	 public boolean isOwner(int sheetID){
		 return getOwnedSheet(sheetID) != null;
	 }
	
	 public boolean hasWritePermissions(int sheetID){
		 return getWriteModeSheet(sheetID) != null || isOwner(sheetID);
	 }
	 
	 public boolean hasReadPermissions(int sheetID){
		 return hasWritePermissions(sheetID) || getReadModeSheet(sheetID) != null;
	 }

	 /*
	  * Changes user permissions about one sheet
	  */
	 
	 @Override
	 public void addReadModeSheets(Sheet sheet){
		 if (hasWritePermissions(sheet.getId())) return; //ignore if already has write permissions
		 super.addReadModeSheets(sheet);
	 }

	 @Override
	 public void addWriteModeSheets(Sheet sheet){
		 if(hasReadPermissions(sheet.getId())){ //remove from read mode sheets if has read permissions
			 removeReadModeSheets(sheet);
		 }
		 super.addWriteModeSheets(sheet);
	 }
	 
	 @Override
	 public void addOwnedSheets(Sheet sheet){
		 addWriteModeSheets(sheet);
		 super.addOwnedSheets(sheet);
	 }
	 
	 public void removePermissions(Sheet sheet){
		 if(hasReadPermissions(sheet.getId())){ 
			 removeReadModeSheets(sheet);
		 }
		 if(hasWritePermissions(sheet.getId())){ 
			 removeWriteModeSheets(sheet);
		 }
	 }
	 

	public void delete() {
		for(Sheet sheet : getOwnedSheetsSet()) {
			sheet.delete();
  		}
		for(Sheet sheet : getWriteModeSheetsSet()) {
			sheet.removeWriteUser(this);
  		}
		for(Sheet sheet : getReadModeSheetsSet()) {
			sheet.removeReadUser(this);
  		}
		
		Session session = getSession();
		if (session != null) session.delete();
		setSession(null);
		
		deleteDomainObject();
	}
	

	 @Override
	 public void setBd(BubbleDocsServer bd){
		 if(bd.hasUser(getUsername())) return;
		 bd.addUsers(this);
	 }
	 
	  
}
