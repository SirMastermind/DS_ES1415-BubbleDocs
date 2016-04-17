package pt.tecnico.bubbledocs.integrator;

import javax.transaction.NotSupportedException;
import javax.transaction.SystemException;

import org.junit.After;
import org.junit.Before;

import pt.ist.fenixframework.FenixFramework;
import pt.ist.fenixframework.core.WriteOnReadError;
import pt.tecnico.bubbledocs.domain.*;
import pt.tecnico.bubbledocs.exception.SheetDoesntExistsException;
import pt.tecnico.bubbledocs.exception.UserDoesntExistsException;
import pt.tecnico.bubbledocs.exception.UserNotInSessionException;

// add needed import declarations

public class BubbleDocsServiceTest {

    @Before
    public void setUp() throws Exception {

        try {
            FenixFramework.getTransactionManager().begin(false);
            populate4Test();
        } catch (WriteOnReadError | NotSupportedException | SystemException e1) {
            e1.printStackTrace();
        }
    }

    @After
    public void tearDown() {
        try {
            FenixFramework.getTransactionManager().rollback();
        } catch (IllegalStateException | SecurityException | SystemException e) {
            e.printStackTrace();
        }
    }

    // should redefine this method in the subclasses if it is needed to specify
    // some initial state
    public void populate4Test() {
    }

    // auxiliary methods that access the domain layer and are needed in the test classes
    // for defining the iniital state and checking that the service has the expected behavior
    
    public BubbleDocsServer getBubbleDocsServer(){
    	return BubbleDocsServer.getInstance();
    }
    
    public User createUser(String username, String email, String name) {

		BubbleDocsServer bd = getBubbleDocsServer();
		
		User usr = new User(username, email, name);
    	bd.addUsers(usr);
    	return usr;
		
    }

    public Sheet createSpreadSheet(User user, String name, int row, int column) {
    	
		BubbleDocsServer bd = getBubbleDocsServer();
 	    Sheet sheet = new Sheet(name, bd.getSheetNewID()+1, user, row, column);
 	    
	    user.addOwnedSheets(sheet);
    	bd.addSheets(sheet);
    	
		return sheet;
    }

    // returns a spreadsheet whose name is equal to name
    public Sheet getSpreadSheet(String name) {
    	try{
    		BubbleDocsServer bd = getBubbleDocsServer();
    		return bd.getSheet(name);
    	}
    	catch(SheetDoesntExistsException e){
    		return null;
    	}

    }

    // returns the user registered in the application whose username is equal to username
    public User getUserFromUsername(String username) {
    	try{
    		BubbleDocsServer bd = getBubbleDocsServer();
    		return bd.getUser(username);
    	}
    	catch(UserDoesntExistsException e){
    		return null;
    	}    	
    }

    // put a user into session and returns the token associated to it
    public String addUserToSession(String username) {
    	BubbleDocsServer bd = getBubbleDocsServer();
    	SessionManager sm = bd.getSessionManager();
    	User user = getUserFromUsername(username);
    	return sm.loginUser(user);
    }

    // remove a user from session given its token
    public void removeUserFromSession(String token) {
    	try {
    		BubbleDocsServer bd = getBubbleDocsServer();
			bd.logOut(token);
		} catch (UserNotInSessionException e) {
			//ignore
		}
    }

    // return the user registered in session whose token is equal to token
    public User getUserFromSession(String token) {
    	try {
    		BubbleDocsServer bd = getBubbleDocsServer();
			return bd.getUserFromSession(token);
		} catch (UserNotInSessionException e) {
			return null;
		}
    }
    
    //sets the cell from that sheet as protected 
    protected void setCellAsProtected(String sheetName, String coords){
    	String[] coordinates = coords.split(";");
		int row = Integer.parseInt(coordinates[0]);
		int column = Integer.parseInt(coordinates[1]);
		Sheet sheet = getSpreadSheet(sheetName);
		sheet.setProtected(true, row, column);
    }
    
    //gives write permissions over that sheet to the user
    protected void addWritePermissions(String sheetName, String username){
    	Sheet sheet = getSpreadSheet(sheetName);
    	User user = getUserFromUsername(username);
    	user.addWriteModeSheets(sheet);
    }
    
    //gives read permissions over that sheet to the user
    protected void addReadPermissions(String sheetName, String username){
    	Sheet sheet = getSpreadSheet(sheetName);
    	User user = getUserFromUsername(username);
    	user.addReadModeSheets(sheet);
    }
    
    //removes all permissions (write and read) over that sheet to user
    protected void removeWritePermissions(String sheetName, String username){
    	Sheet sheet = getSpreadSheet(sheetName);
    	User user = getUserFromUsername(username);
    	user.removePermissions(sheet);
    }

}
