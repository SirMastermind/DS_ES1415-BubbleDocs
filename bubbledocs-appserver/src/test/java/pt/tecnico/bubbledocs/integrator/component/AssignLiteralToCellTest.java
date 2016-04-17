package pt.tecnico.bubbledocs.integrator.component;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import pt.tecnico.bubbledocs.domain.User;
import pt.tecnico.bubbledocs.exception.*;
import pt.tecnico.bubbledocs.integrator.AssignLiteralCellIntegrator;
import pt.tecnico.bubbledocs.integrator.BubbleDocsServiceTest;


public class AssignLiteralToCellTest extends BubbleDocsServiceTest {
	
	private String userToken;
	private String writeToken;
	private String readToken;
	public  String noRightsToken;
	private static final String USERNAME = "testUser";
	private static final String USER_NAME = "testName";
	private static final String USER_PASS = "testPass";
	private static final String SHEET_NAME = "testSheet";
	private static final String CELL_COORDS = "1;1";
	private static final String INT_ARG = "42";
	private static final String PROTECTED_CELL_COORDS = "1;2";
	private static final String NEGATIVE_CELL_COORDS = "-1;-1";
	private static final String POTATO_STRING = "potato";
	private static final String EMPTY_STRING = "";
	private static final String OVER9K_STRING = "9001;9001";
	private static final String WRITE_USER_NAME = "write";
	private static final String READ_USER_NAME = "read";
	private static final String NO_RIGHTS_NAME = "noRight";
	
	
	@Override
    public void populate4Test() {
    	// Creates users for testing
    	User tester = createUser(USERNAME, USER_PASS, USER_NAME);
    	createUser(WRITE_USER_NAME, USER_PASS, USER_NAME);
    	createUser(READ_USER_NAME, USER_PASS, USER_NAME);
    	createUser(NO_RIGHTS_NAME, USER_PASS, USER_NAME);
    	
    	// Creates a new sheet and add it to the database
    	createSpreadSheet(tester, SHEET_NAME, 10, 10);
    	setCellAsProtected(SHEET_NAME, PROTECTED_CELL_COORDS); 

    	//add users to session
    	this.userToken = addUserToSession(USERNAME);
    	this.writeToken = addUserToSession(WRITE_USER_NAME);
    	this.readToken = addUserToSession(READ_USER_NAME);
    	this.noRightsToken = addUserToSession(NO_RIGHTS_NAME);
    	
    	//concede write and read permissions
    	addWritePermissions(SHEET_NAME, WRITE_USER_NAME);
    	addReadPermissions(SHEET_NAME, READ_USER_NAME);
    	
    }
    
    // Test 5.01: It tests the assignment with valid parameters.
    @Test
    public void success() {
    	// Creates service with good parameters
    	AssignLiteralCellIntegrator service = new AssignLiteralCellIntegrator(this.userToken, 1, CELL_COORDS, INT_ARG);
		service.execute();

    	assertEquals("Failure", INT_ARG, service.getResult());
    }

    // Test 5.02: It tests the assignment with invalid parameters, namely the docId, specifically a non-existent one.
    @Test(expected = SheetDoesntExistsException.class)
    public void invalidSheetID() {
    	AssignLiteralCellIntegrator service = new AssignLiteralCellIntegrator(this.userToken, 9001, CELL_COORDS, INT_ARG);
    	service.execute();
    }

    // Test 5.03: It tests the assignment with invalid parameters, namely the cellId, specifically an out of range cell.
    @Test(expected = InvalidCellCoordinatesException.class)
    public void coordinatesOutOfRange() {
    	AssignLiteralCellIntegrator service = new AssignLiteralCellIntegrator(this.userToken, 1, OVER9K_STRING, INT_ARG);
    	service.execute();
    }

    // Test 5.04: It tests the assignment with valid parameters to a protected cell.
    @Test(expected = UnauthorizedOperationException.class)
    public void invalidAssignmentToProtectedCell() {
    	AssignLiteralCellIntegrator service = new AssignLiteralCellIntegrator(this.userToken, 1, PROTECTED_CELL_COORDS, INT_ARG);
    	service.execute();
    }
    
    // Test 5.05: It tests the assignment with invalid parameters, namely the literal value, specifically this one being a String.
    @Test(expected = InvalidLiteralException.class)
    public void invalidLiteralStringCoordinates() {
    	AssignLiteralCellIntegrator service = new AssignLiteralCellIntegrator(this.userToken, 1, CELL_COORDS, POTATO_STRING);
    	service.execute();
    }
    
    // Test 5.06: It tests the assignment with invalid parameters, namely the literal value, specifically this one being a double.
    @Test(expected = InvalidLiteralException.class)
    public void invalidLiteralDoubleCoordinates() {
    	AssignLiteralCellIntegrator service = new AssignLiteralCellIntegrator(this.userToken, 1, CELL_COORDS, "5.5");
    	service.execute();
    }

    // Test 5.07: It tests the assignment with invalid parameters, namely the literal value, specifically this one being empty (empty string).
    @Test(expected = InvalidLiteralException.class)
    public void invalidLiteralEmptyCoordinates() {
    	AssignLiteralCellIntegrator service = new AssignLiteralCellIntegrator(this.userToken, 1, CELL_COORDS, EMPTY_STRING);
    	service.execute();
    }
    
    // Test 5.08: It tests the assignment with invalid parameters, namely the literal value, specifically this one being empty (null value).
    @Test(expected = InvalidLiteralException.class)
    public void invalidLiteralNullCoordinates() {
    	AssignLiteralCellIntegrator service = new AssignLiteralCellIntegrator(this.userToken, 1, CELL_COORDS, null);
    	service.execute();
    }
    
    // Test 5.09: It tests the assignment with invalid parameters, namely the cellId parameter, specifically this one being empty (null value).
    @Test(expected = InvalidCellCoordinatesException.class)
    public void invalidCoordinatesNullCoordinates() {
    	AssignLiteralCellIntegrator service = new AssignLiteralCellIntegrator(this.userToken, 1, null, INT_ARG);
    	service.execute();
    }
    
    // Test 5.10: It tests the assignment with invalid parameters, namely the cellId parameter, specifically this one being empty (empty string).
    @Test(expected = InvalidCellCoordinatesException.class)
    public void invalidCoordinatesEmptyCoordinates() {
    	AssignLiteralCellIntegrator service = new AssignLiteralCellIntegrator(this.userToken, 1, EMPTY_STRING, INT_ARG);
    	service.execute();
    }
    
    // Test 5.11: It tests the assignment with invalid parameters, namely the cellId parameter, specifically this one with negative coordinates.
    @Test(expected = InvalidCellCoordinatesException.class)
    public void invalidCoordinatesNegativeCoordinates() {
    	AssignLiteralCellIntegrator service = new AssignLiteralCellIntegrator(this.userToken, 1, NEGATIVE_CELL_COORDS, INT_ARG);
    	service.execute();
    }
    
    // Test 5.12: It tests the assignment with invalid parameters, namely the cellId parameter, specifically this one with not correct coordinates format.
    @Test(expected = InvalidCellCoordinatesException.class)
    public void invalidCoordinatesStringCoordinates() {
    	AssignLiteralCellIntegrator service = new AssignLiteralCellIntegrator(this.userToken, 1, POTATO_STRING, INT_ARG);
    	service.execute();
    }
    
    //Test 5.13: It tests the assignment with valid parameters but without valid session
    @Test(expected = UserNotInSessionException.class)
    public void invalidSession(){
    	removeUserFromSession(this.userToken);
    	AssignLiteralCellIntegrator service = new AssignLiteralCellIntegrator(this.userToken, 1, CELL_COORDS, INT_ARG);
		service.execute();
    }
    
    //Test 5.14: It tests the assignment with valid parameters from a user with write permissions
    @Test
    public void writePermissions(){
    	AssignLiteralCellIntegrator service = new AssignLiteralCellIntegrator(this.writeToken, 1, CELL_COORDS, INT_ARG);
		service.execute();
		
		assertEquals("Failure", INT_ARG, service.getResult());
    }
    
    //Test 5.15: It tests the assignment with valid parameters but from a user with read permissions
    @Test(expected = UnauthorizedOperationException.class)
    public void readPermissions(){
    	AssignLiteralCellIntegrator service = new AssignLiteralCellIntegrator(this.readToken, 1, CELL_COORDS, INT_ARG);
		service.execute();
    }
    
    //Test 5.16: It tests the assignment with valid parameters from the owner after the write permissions were removed to him
    @Test
    public void ownerNoWritePermissions(){
    	removeWritePermissions(SHEET_NAME, USERNAME);
    	AssignLiteralCellIntegrator service = new AssignLiteralCellIntegrator(this.userToken, 1, CELL_COORDS, INT_ARG);
		service.execute();
		
		assertEquals("Failure", INT_ARG, service.getResult());
    }
    
    //Test 5.17: It tests the assignment with valid parameters from a user without any kind of rights over the sheet
    @Test(expected = UnauthorizedOperationException.class)
    public void noPermissions(){
    	AssignLiteralCellIntegrator service = new AssignLiteralCellIntegrator(this.noRightsToken, 1, CELL_COORDS, INT_ARG);
		service.execute();
    }
}