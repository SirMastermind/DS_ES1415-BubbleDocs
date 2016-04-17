package pt.tecnico.bubbledocs.integrator.component;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import pt.tecnico.bubbledocs.domain.Sheet;
import pt.tecnico.bubbledocs.domain.User;
import pt.tecnico.bubbledocs.exception.SheetDoesntExistsException;
import pt.tecnico.bubbledocs.exception.UnauthorizedOperationException;
import pt.tecnico.bubbledocs.exception.UserNotInSessionException;
import pt.tecnico.bubbledocs.integrator.AssignBinaryCellIntegrator;
import pt.tecnico.bubbledocs.integrator.AssignLiteralCellIntegrator;
import pt.tecnico.bubbledocs.integrator.AssignReferenceCellIntegrator;
import pt.tecnico.bubbledocs.integrator.BubbleDocsServiceTest;
import pt.tecnico.bubbledocs.integrator.GetSpreadSheetContentIntegrator;

public class GetSpreadSheetContentTest extends BubbleDocsServiceTest {
	
	private static final String USER = "Tester";
	private static final String READ_USER = "readPerm";
	private static final String WRITE_USER = "writPerm";
	private static final String NO_RIGHTS_USER = "NoRight";
	private static final String USER_NAME = "UserName";
	private static final String SHEETNAME = "getSpreadSheetContentTesterSheetName";
	private static final int BADDOCID = 9001;
	private static final int ZERODOCID = 0;
	private static final int NEGATIVEDOCID = -9001;
	private static final int ROWS = 10;
	private static final int COLUMNS = 10;
	private static final String BADUSERTOKEN = "errado";
	private static String[][] CONTENT = new String[ROWS][COLUMNS];

	private static final String PASS = "getSpreadSheetContentTesterPass";
	private String userToken;
	private String writeToken;
	private String readToken;
	private String noRightsToken;
	private int sheetID;
	
	public void populate4Test() {
		// Creates users for testing
    	User tester = createUser(USER, PASS, USER_NAME);
    	createUser(READ_USER, PASS, USER_NAME);
    	createUser(WRITE_USER, PASS, USER_NAME);
    	createUser(NO_RIGHTS_USER, PASS, USER_NAME);
    	
    	// Creates a new sheet and add it to the database
    	Sheet sheet = createSpreadSheet(tester, SHEETNAME, ROWS, COLUMNS);
    	sheetID = sheet.getId();
    	
    	// Add users to Session
    	userToken = addUserToSession(USER);
    	writeToken = addUserToSession(WRITE_USER);
    	readToken = addUserToSession(READ_USER);
    	noRightsToken = addUserToSession(NO_RIGHTS_USER);
    	
    	//concede write and read permissions
    	addWritePermissions(SHEETNAME, WRITE_USER);
    	addReadPermissions(SHEETNAME, READ_USER);
    	
    	// Assign a literal to some cell
    	AssignLiteralCellIntegrator firstLiteral = new AssignLiteralCellIntegrator(userToken, sheetID, "1;1", "10");
    	firstLiteral.execute();
    	
    	// Assign a literal to some cell
    	AssignLiteralCellIntegrator secondLiteral = new AssignLiteralCellIntegrator(userToken, sheetID, "1;2", "5");
    	secondLiteral.execute();
    	
    	// Assign a reference to some cell
    	AssignReferenceCellIntegrator firstReference = new AssignReferenceCellIntegrator(userToken, sheetID, "1;3", "1;2");
    	firstReference.execute();
    	
    	// Assign a function to some cell
    	AssignBinaryCellIntegrator addFunction = new AssignBinaryCellIntegrator(userToken, sheetID, "1;4", "ADD" + "(" + "1;1" + "," + "1;2" + ")");
    	addFunction.execute();
    	
    	AssignBinaryCellIntegrator subFunction = new AssignBinaryCellIntegrator(userToken, sheetID, "1;5", "SUB" + "(" + "1;1" + "," + "1;2" + ")");
    	subFunction.execute();
    	
    	AssignBinaryCellIntegrator mulFunction = new AssignBinaryCellIntegrator(userToken, sheetID, "1;6", "MUL" + "(" + "1;1" + "," + "1;2" + ")");
    	mulFunction.execute();
    	
    	AssignBinaryCellIntegrator divFunction = new AssignBinaryCellIntegrator(userToken, sheetID, "1;7", "DIV" + "(" + "1;1" + "," + "1;2" + ")");
    	divFunction.execute();
    	
    	//Testing functions with reference arg
    	AssignBinaryCellIntegrator addFunctionRefArg = new AssignBinaryCellIntegrator(userToken, sheetID, "1;8", "ADD" + "(" + "1;1" + "," + "1;3" + ")");
    	addFunctionRefArg.execute();
    	
    	AssignBinaryCellIntegrator mulFunctionRefArg = new AssignBinaryCellIntegrator(userToken, sheetID, "1;9", "MUL" + "(" + "1;1" + "," + "1;3" + ")");
    	mulFunctionRefArg.execute();
    	fillContentMatrix();
	}
	
	private void fillContentMatrix() {
		for(int i = 0; i < ROWS; i++) {
			for(int j = 0; j < COLUMNS; j++) {
				CONTENT[i][j] = "#VALUE";
			}
		}
		CONTENT[0][0] = "10";
		CONTENT[0][1] = "5";
		CONTENT[0][2] = "5";
		CONTENT[0][3] = "15";
		CONTENT[0][4] = "5";
		CONTENT[0][5] = "50";
		CONTENT[0][6] = "2";
		CONTENT[0][7] = "15";
		CONTENT[0][8] = "50";
	}
	
	//Test 11.01 - It tests getting the sheet's content with valid parameters.  
	@Test
	public void successSheetOwner() {
		// Creates service with good parameters
		GetSpreadSheetContentIntegrator integrator = new GetSpreadSheetContentIntegrator(userToken, sheetID);
		integrator.execute();
		String[][] s = integrator.getResult();
		for(int i = 0; i < ROWS; i++) {
			for(int j = 0; j < COLUMNS; j++) {
				assertEquals("Not the same content!", CONTENT[i][j], s[i][j]);
			}
		}
	}
	
	//Test 11.02 - It tests getting the sheet's content with valid parameters but from a user with read permissions.  
	@Test
	public void successReadPermissions() {
		// Creates service with good parameters
		GetSpreadSheetContentIntegrator integrator = new GetSpreadSheetContentIntegrator(readToken, sheetID);
		integrator.execute();
		String[][] s = integrator.getResult();
		for(int i = 0; i < ROWS; i++) {
			for(int j = 0; j < COLUMNS; j++) {
				assertEquals("Not the same content!", CONTENT[i][j], s[i][j]);
			}
		}
	}
	
	//Test 11.03 - It tests getting the sheets content with valid parameters but from a user with write permissions.  
	@Test
	public void userWithWritePermissions() {
		GetSpreadSheetContentIntegrator integrator = new GetSpreadSheetContentIntegrator(writeToken, sheetID);
		integrator.execute();
	}
	
	//Test 11.04 - It tests getting the sheets content with valid parameters but from a user with no permissions.  
	@Test(expected = UnauthorizedOperationException.class)
	public void userWithNoPermissions() {
		GetSpreadSheetContentIntegrator integrator = new GetSpreadSheetContentIntegrator(noRightsToken, sheetID);
		integrator.execute();
	}
	
	//Test 11.05 - It tests getting the sheets content with valid parameters but from a user with no session.  
	@Test(expected = UserNotInSessionException.class) 
	public void userNotInSession() {
		removeUserFromSession(noRightsToken);
		GetSpreadSheetContentIntegrator integrator = new GetSpreadSheetContentIntegrator(noRightsToken, sheetID);
		integrator.execute();
	}
	
	//Test 11.06 - It tests getting the sheets content, but with invalid parameters, namely an empty string.  
	@Test(expected = UserNotInSessionException.class)
	public void emptyUserToken() {
		GetSpreadSheetContentIntegrator integrator = new GetSpreadSheetContentIntegrator("", sheetID);
		integrator.execute();
	}
	
	//Test 11.07 - It tests getting the sheets content, but with invalid parameters, namely a null value.  
	@Test(expected = UserNotInSessionException.class)
	public void nulluserToken() {
		GetSpreadSheetContentIntegrator integrator = new GetSpreadSheetContentIntegrator(null, sheetID);
		integrator.execute();
	}
	
	//Test 11.08 - It tests getting the sheets content, but with invalid parameters, namely a non existent user.  
	@Test(expected = UserNotInSessionException.class)
	public void invalidUserToken() {
		GetSpreadSheetContentIntegrator integrator = new GetSpreadSheetContentIntegrator(BADUSERTOKEN, sheetID);
		integrator.execute();
	}
	
	//Test 11.09 - It tests getting the sheets content, but with invalid parameters, namely a non existent sheet.  
	@Test(expected = SheetDoesntExistsException.class)
    public void invalidSheetId() {
		GetSpreadSheetContentIntegrator integrator = new GetSpreadSheetContentIntegrator(userToken, BADDOCID);
		integrator.execute();
    }
	
	//Test 11.10 - It tests getting the sheets content, but with invalid parameters, namely a non existent sheet.  
	@Test(expected = SheetDoesntExistsException.class)
	public void negativeSheetId() {
		GetSpreadSheetContentIntegrator integrator = new GetSpreadSheetContentIntegrator(userToken, NEGATIVEDOCID);
		integrator.execute();
	}
	
	//Test 11.11 - It tests getting the sheets content, but with invalid parameters, namely a non existent sheet.  
	@Test(expected = SheetDoesntExistsException.class)
	public void zeroSheetId() {
		GetSpreadSheetContentIntegrator integrator = new GetSpreadSheetContentIntegrator(userToken, ZERODOCID);
		integrator.execute();
	}
	
	//Test 11.12 - It tests getting the sheets content with valid parameters but from a user with no write permissions.  
	@Test
	public void noPermissionsOwner() {
		removeWritePermissions(SHEETNAME, USER);
		GetSpreadSheetContentIntegrator integrator = new GetSpreadSheetContentIntegrator(userToken, sheetID);
		integrator.execute();
	}
}
