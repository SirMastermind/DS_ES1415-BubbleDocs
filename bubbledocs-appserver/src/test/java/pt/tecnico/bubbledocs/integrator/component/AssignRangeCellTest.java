package pt.tecnico.bubbledocs.integrator.component;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import pt.tecnico.bubbledocs.domain.RangeFunction;
import pt.tecnico.bubbledocs.domain.Cell;
import pt.tecnico.bubbledocs.domain.Sheet;
import pt.tecnico.bubbledocs.domain.User;
import pt.tecnico.bubbledocs.exception.InvalidCellCoordinatesException;
import pt.tecnico.bubbledocs.exception.InvalidDataException;
import pt.tecnico.bubbledocs.exception.SheetDoesntExistsException;
import pt.tecnico.bubbledocs.exception.UnauthorizedOperationException;
import pt.tecnico.bubbledocs.exception.UserNotInSessionException;
import pt.tecnico.bubbledocs.integrator.AssignRangeCellIntegrator;
import pt.tecnico.bubbledocs.integrator.BubbleDocsServiceTest;
import pt.tecnico.bubbledocs.service.local.AssignLiteralCell;

public class AssignRangeCellTest extends BubbleDocsServiceTest {
	private String userToken;
	private String writeToken;
	private String readToken;
	private String noRightsToken;
	
	private static final String CELLCOORDS = "1;1";
	
	private static final String PROTECTED_CELL_COORDS = "1;5";
	
	private static final String FIRST_HORZ_COORDS = "1;2";
	private static final String MIDDLE_HORZ_COORDS = "1;3";	
	private static final String LAST_HORZ_COORDS = "1;4";
	
	private static final String FIRST_VERT_COORDS = "2;1";
	private static final String MIDDLE_VERT_COORDS = "3;1";	
	private static final String LAST_VERT_COORDS = "4;1";
	
	private static final String FIRST_DIAG_COORDS = "2;2";
	private static final String MIDDLE_DIAG_COORDS = "3;3";	
	private static final String LAST_DIAG_COORDS = "4;4";
	
	private static final String OTHER_COORDS_2_3 = "2;3";
	private static final String OTHER_COORDS_3_2 = "3;2";
	private static final String OTHER_COORDS_4_2 = "4;2";
	private static final String OTHER_COORDS_2_4 = "2;4";
	private static final String OTHER_COORDS_4_3 = "4;3";
	private static final String OTHER_COORDS_3_4 = "3;4";
	
	private static final String POTATO_STRING = "potato";
	private static final String OVER9K_STRING = "9001;9001";
	private static final String EMPTY_STRING = "";
	private static final String NEGATIVE_CELL_COORDS = "-1;-1";
	
	private static final String SHEET_NAME = "testSheet";
	private static final String USER_NAME = "testName";
	private static final String USERNAME = "testUser";
	private static final String USER_PASS = "testPass";
	
	private static final String WRITE_USER_NAME = "write";
	private static final String READ_USER_NAME = "readUser";
	private static final String NO_RIGHTS_NAME = "noRight";
	
	private static final String INT_ARG_1 ="1";
	private static final String INT_ARG_2 ="2";
	private static final String INT_ARG_3 ="3";
	
	private static final String AVG_RESULT_VERT_HORZ = "2";
	private static final String AVG_RESULT_DIAG = "1";
	
	private static final String PRD_RESULT = "6";
	
	private static final String AVG = "AVG";
	private static final String PRD = "PRD";
	
	private static final String BAD_FUNC_FEWER = "FF";
	private static final String BAD_FUNC_EXACT = "LOL";
	private static final String BAD_FUNC_MORE = "REKT";
	
	private static final int CELL_ROW = 1;
	private static final int CELL_COLUMN = 1;
	
	private static final String inputTest1 = AVG + "(" + FIRST_VERT_COORDS + ":" + LAST_VERT_COORDS + ")";
	private static final String inputTest2 = AVG + "(" + FIRST_HORZ_COORDS + ":" + LAST_HORZ_COORDS + ")";
	private static final String inputTest3 = AVG + "(" + FIRST_DIAG_COORDS + ":" + LAST_DIAG_COORDS + ")";
	private static final String inputTest4 = PRD + "(" + FIRST_VERT_COORDS + ":" + LAST_VERT_COORDS + ")";
	private static final String inputTest5 = PRD + "(" + FIRST_HORZ_COORDS + ":" + LAST_HORZ_COORDS + ")";
	private static final String inputTest6 = PRD + "(" + FIRST_DIAG_COORDS + ":" + LAST_DIAG_COORDS + ")";
	private static final String inputTest7 = AVG + "(" + FIRST_HORZ_COORDS + ":" + PROTECTED_CELL_COORDS + ")";
	private static final String inputTest8 = AVG + "(" + FIRST_DIAG_COORDS + ":" + LAST_DIAG_COORDS + ")";
	private static final String inputTest9 = BAD_FUNC_FEWER + "(" + FIRST_DIAG_COORDS + ":" + LAST_DIAG_COORDS + ")";
	private static final String inputTest10 = BAD_FUNC_EXACT + "(" + FIRST_DIAG_COORDS + ":" + LAST_DIAG_COORDS + ")";
	private static final String inputTest11 = BAD_FUNC_MORE + "(" + FIRST_DIAG_COORDS + ":" + LAST_DIAG_COORDS + ")";
	private static final String inputTest12 = EMPTY_STRING + "(" + FIRST_DIAG_COORDS + ":" + LAST_DIAG_COORDS + ")";
	private static final String inputTest13 = AVG + "(" + NEGATIVE_CELL_COORDS + ":" + LAST_DIAG_COORDS + ")";
	private static final String inputTest14 = AVG + "(" + EMPTY_STRING + ":" + LAST_DIAG_COORDS + ")";
	private static final String inputTest15 = AVG + "(" + POTATO_STRING + ":" + LAST_DIAG_COORDS + ")";
	private static final String inputTest16 = AVG + "(" + OVER9K_STRING + ":" + LAST_DIAG_COORDS + ")";
	private static final String inputTest17 = AVG + "(" + FIRST_DIAG_COORDS + ":" + NEGATIVE_CELL_COORDS + ")";
	private static final String inputTest18 = AVG + "(" + FIRST_DIAG_COORDS + ":" + EMPTY_STRING + ")";
	private static final String inputTest19 = AVG + "(" + FIRST_DIAG_COORDS + ":" + POTATO_STRING + ")";
	private static final String inputTest20 = AVG + "(" + FIRST_DIAG_COORDS + ":" + OVER9K_STRING + ")";
	private static final String inputTestRest = AVG + "(" + FIRST_DIAG_COORDS + ":" + LAST_DIAG_COORDS + ")";
	
	@Override
    public void populate4Test() {
		//create users
        User u = createUser(USERNAME, USER_PASS, USER_NAME);
        createUser(WRITE_USER_NAME, USER_PASS, USER_NAME);
    	createUser(READ_USER_NAME, USER_PASS, USER_NAME);
    	createUser(NO_RIGHTS_NAME, USER_PASS, USER_NAME);
    	
    	//create sheet
        createSpreadSheet(u, SHEET_NAME, 5, 5);
        
        
        //add users to sessions
        this.userToken = addUserToSession(u.getUsername());
        this.writeToken = addUserToSession(WRITE_USER_NAME);
    	this.readToken = addUserToSession(READ_USER_NAME);
    	this.noRightsToken = addUserToSession(NO_RIGHTS_NAME);
    	
    	// Vertical range
    	AssignLiteralCell addLiteral = new AssignLiteralCell(userToken, 1, FIRST_VERT_COORDS, INT_ARG_1);
    	addLiteral.nonAtomicExecute();
    	
    	AssignLiteralCell addLiteral2 = new AssignLiteralCell(userToken, 1, MIDDLE_VERT_COORDS, INT_ARG_2);
    	addLiteral2.nonAtomicExecute();
    	
    	AssignLiteralCell addLiteral3 = new AssignLiteralCell(userToken, 1, LAST_VERT_COORDS, INT_ARG_3);
    	addLiteral3.nonAtomicExecute();
    	
    	// Horizontal range
    	AssignLiteralCell addLiteral4 = new AssignLiteralCell(userToken, 1, FIRST_HORZ_COORDS, INT_ARG_1);
    	addLiteral4.nonAtomicExecute();
    	
    	AssignLiteralCell addLiteral5 = new AssignLiteralCell(userToken, 1, MIDDLE_HORZ_COORDS, INT_ARG_2);
    	addLiteral5.nonAtomicExecute();
    	
    	AssignLiteralCell addLiteral6 = new AssignLiteralCell(userToken, 1, LAST_HORZ_COORDS, INT_ARG_3);
    	addLiteral6.nonAtomicExecute();
    	
    	// Diagonal range
    	AssignLiteralCell addLiteral7 = new AssignLiteralCell(userToken, 1, FIRST_DIAG_COORDS, INT_ARG_1);
    	addLiteral7.nonAtomicExecute();
    	
    	AssignLiteralCell addLiteral8 = new AssignLiteralCell(userToken, 1, MIDDLE_DIAG_COORDS, INT_ARG_2);
    	addLiteral8.nonAtomicExecute();
    	
    	AssignLiteralCell addLiteral9 = new AssignLiteralCell(userToken, 1, LAST_DIAG_COORDS, INT_ARG_3);
    	addLiteral9.nonAtomicExecute();
    	
    	// Assign a literal to some cell
    	AssignLiteralCell addLiteral10 = new AssignLiteralCell(userToken, 1, PROTECTED_CELL_COORDS, INT_ARG_2);
    	addLiteral10.nonAtomicExecute();
    	
    	// Assign literal to remaining cells
    	AssignLiteralCell addLiteral11 = new AssignLiteralCell(userToken, 1, OTHER_COORDS_2_3, INT_ARG_1);
    	addLiteral11.nonAtomicExecute();
    	
    	// Assign literal to remaining cells
    	AssignLiteralCell addLiteral12 = new AssignLiteralCell(userToken, 1, OTHER_COORDS_3_2, INT_ARG_1);
    	addLiteral12.nonAtomicExecute();
    	
    	// Assign literal to remaining cells
    	AssignLiteralCell addLiteral13 = new AssignLiteralCell(userToken, 1, OTHER_COORDS_2_4, INT_ARG_1);
    	addLiteral13.nonAtomicExecute();
    	
    	// Assign literal to remaining cells
    	AssignLiteralCell addLiteral14 = new AssignLiteralCell(userToken, 1, OTHER_COORDS_4_2, INT_ARG_1);
    	addLiteral14.nonAtomicExecute();
    	
    	// Assign literal to remaining cells
    	AssignLiteralCell addLiteral15 = new AssignLiteralCell(userToken, 1, OTHER_COORDS_4_3, INT_ARG_1);
    	addLiteral15.nonAtomicExecute();
    	
    	// Assign literal to remaining cells
    	AssignLiteralCell addLiteral16 = new AssignLiteralCell(userToken, 1, OTHER_COORDS_3_4, INT_ARG_1);
    	addLiteral16.nonAtomicExecute();
    	
    	//set some cells as protected
    	setCellAsProtected(SHEET_NAME, PROTECTED_CELL_COORDS);
        
    	//concede write and read permissions
    	addWritePermissions(SHEET_NAME, WRITE_USER_NAME);
    	addReadPermissions(SHEET_NAME, READ_USER_NAME);
    }
	
    private RangeFunction getRangeFunction(Sheet s, int row, int column) {
		Cell c = s.getCellByCoords(row, column);
    	return (RangeFunction)c.getContent();
   	
    }
    
	// ******************** //
    // ***** AVG Tests **** //
    // ******************** //
    
    //Test 10.01 - It tests the assignment of an AVG function with valid parameters, namely a vertical range. 
    @Test
    public void successAVG_VERT() {
        AssignRangeCellIntegrator service = new AssignRangeCellIntegrator(userToken, 1, CELLCOORDS, inputTest1);
		service.execute();
		
		//check if the value is the same from the Range function's result
		assertEquals("Failure", AVG_RESULT_VERT_HORZ, service.getResult());
		
        // check if RangeFunction was created
		Sheet s = getSpreadSheet(SHEET_NAME);
		RangeFunction b = getRangeFunction(s, CELL_ROW, CELL_COLUMN);
        assertNotNull("RangeFunction was not created", b);
    }
    
    //Test 10.02 - It tests the assignment of an AVG function with valid parameters, namely a horizontal range. 
    @Test
    public void successAVG_HORZ() {
        AssignRangeCellIntegrator service = new AssignRangeCellIntegrator(userToken, 1, CELLCOORDS, inputTest2);
		service.execute();
		
		//check if the value is the same from the Range function's result
		assertEquals("Failure", AVG_RESULT_VERT_HORZ, service.getResult());
		
        // check if RangeFunction was created
		Sheet s = getSpreadSheet(SHEET_NAME);
		RangeFunction b = getRangeFunction(s, CELL_ROW, CELL_COLUMN);
        assertNotNull("RangeFunction was not created", b);
    }
    
    //Test 10.03 - It tests the assignment of an AVG function with valid parameters, namely an area.
    @Test
    public void successAVG_DIAG() {
        AssignRangeCellIntegrator service = new AssignRangeCellIntegrator(userToken, 1, CELLCOORDS, inputTest3);
		service.execute();
		
		//check if the value is the same from the Range function's result
		assertEquals("Failure", AVG_RESULT_DIAG, service.getResult());
		
        // check if RangeFunction was created
		Sheet s = getSpreadSheet(SHEET_NAME);
		RangeFunction b = getRangeFunction(s, CELL_ROW, CELL_COLUMN);
        assertNotNull("RangeFunction was not created", b);
    }
    
	// ******************** //
    // ***** PRD Tests **** //
    // ******************** //
    
    //Test 10.04 - It tests the assignment of an PRD function with valid parameters, namely a vertical range.
    @Test
    public void successPRD_VERT() {
        AssignRangeCellIntegrator service = new AssignRangeCellIntegrator(userToken, 1, CELLCOORDS, inputTest4);
		service.execute();
		
		//check if the value is the same from the Range function's result
		assertEquals("Failure", PRD_RESULT, service.getResult());
		
        // check if RangeFunction was created
		Sheet s = getSpreadSheet(SHEET_NAME);
		RangeFunction b = getRangeFunction(s, CELL_ROW, CELL_COLUMN);
        assertNotNull("RangeFunction was not created", b);
    }
    
    //Test 10.05 - It tests the assignment of an PRD function with valid parameters, namely a horizontal range.
    @Test
    public void successPRD_HORZ() {
        AssignRangeCellIntegrator service = new AssignRangeCellIntegrator(userToken, 1, CELLCOORDS, inputTest5);
		service.execute();
		
		//check if the value is the same from the Range function's result
		assertEquals("Failure", PRD_RESULT, service.getResult());
		
        // check if RangeFunction was created
		Sheet s = getSpreadSheet(SHEET_NAME);
		RangeFunction b = getRangeFunction(s, CELL_ROW, CELL_COLUMN);
        assertNotNull("RangeFunction was not created", b);
    }
    
    //Test 10.06 - It tests the assignment of an PRD function with valid parameters, namely an area.
    @Test
    public void successPRD_DIAG() {
        AssignRangeCellIntegrator service = new AssignRangeCellIntegrator(userToken, 1, CELLCOORDS, inputTest6);
		service.execute();
		
		//check if the value is the same from the Range function's result
		assertEquals("Failure", PRD_RESULT, service.getResult());
		
        // check if RangeFunction was created
		Sheet s = getSpreadSheet(SHEET_NAME);
		RangeFunction b = getRangeFunction(s, CELL_ROW, CELL_COLUMN);
        assertNotNull("RangeFunction was not created", b);
    }
    
	// ************************* //
    // * Protected Cells Tests * //
    // ************************* //
    // Note: The behavior of each RangeFunction regarding having as an argument a protected cell is the same to every one of them.
    //		 Therefore, only the ADD Function will be tested.
    //Test 10.07 - It tests the assignment of an AVG function with valid parameters, namely two cells, one of them protected.  
    @Test
    public void successAVG_HORZ_1Protected() {
        AssignRangeCellIntegrator service = new AssignRangeCellIntegrator(userToken, 1, CELLCOORDS, inputTest7);
		service.execute();
		
		//check if the value is the same from the Range function's result
		assertEquals("Failure", AVG_RESULT_VERT_HORZ, service.getResult());
		
        // check if RangeFunction was created
		Sheet s = getSpreadSheet(SHEET_NAME);
		RangeFunction b = getRangeFunction(s, CELL_ROW, CELL_COLUMN);
        assertNotNull("RangeFunction was not created", b);
    }
    
    //Test 10.08 - It tests the assignment of an AVG function with invalid parameters, namely the target cell parameter, specifically this one being a protected cell. 
    @Test(expected = UnauthorizedOperationException.class)
    public void assignToProtected() {
        AssignRangeCellIntegrator service = new AssignRangeCellIntegrator(userToken, 1, PROTECTED_CELL_COORDS, inputTest8);
		service.execute();
    }
    
	// ************************* //
    // * Other Functions Tests * //
    // ************************* //
    
    //Test 10.09: It tests the assignment with invalid parameters, namely the function name parameter, specifically this one having fewer letters than required.  
    @Test(expected = InvalidDataException.class)
    public void badFunctionFewer(){
        AssignRangeCellIntegrator service = new AssignRangeCellIntegrator(userToken, 1, CELLCOORDS, inputTest9);
		service.execute();
    }
  
    //Test 10.10: It tests the assignment with invalid parameters, namely the function name parameter, specifically this one being a name that doesn't exist in the domain.
    @Test(expected = InvalidDataException.class)
    public void badFunctionExact(){
        AssignRangeCellIntegrator service = new AssignRangeCellIntegrator(userToken, 1, CELLCOORDS, inputTest10);
		service.execute();
    }
    
    //Test 10.11: It tests the assignment with invalid parameters, namely the function name parameter, specifically this one having more letters than required.   
    @Test(expected = InvalidDataException.class)
    public void badFunctionMore(){
        AssignRangeCellIntegrator service = new AssignRangeCellIntegrator(userToken, 1, CELLCOORDS, inputTest11);
		service.execute();
    }
    
    //Test 10.12: It tests the assignment with invalid parameters, namely the function name parameter, specifically this one being an empty string.
    @Test(expected = InvalidDataException.class)
    public void badFunctionEmpty(){
        AssignRangeCellIntegrator service = new AssignRangeCellIntegrator(userToken, 1, CELLCOORDS, inputTest12);
		service.execute();
    }
    
    // ************************ //
    // **** Left Arg Tests **** //
    // ************************ //
    
    //Test 10.13: It tests the assignment with invalid parameters, namely the left argument parameter, specifically this one being negative coordinates.  
    @Test(expected = InvalidDataException.class)
    public void badLeftArgNegative(){
        AssignRangeCellIntegrator service = new AssignRangeCellIntegrator(userToken, 1, CELLCOORDS, inputTest13);
		service.execute();
    }    
    
    //Test 10.14: It tests the assignment with invalid parameters, namely the left argument parameter, specifically this one being empty string. 
    @Test(expected = InvalidDataException.class)
    public void badLeftArgEmpty(){
        AssignRangeCellIntegrator service = new AssignRangeCellIntegrator(userToken, 1, CELLCOORDS, inputTest14);
		service.execute();
    }    
    
    //Test 10.15: It tests the assignment with invalid parameters, namely the left argument parameter, specifically this one being a string of characters without numbers.
    @Test(expected = InvalidDataException.class)
    public void badLeftArgString(){
        AssignRangeCellIntegrator service = new AssignRangeCellIntegrator(userToken, 1, CELLCOORDS, inputTest15);
		service.execute();
    }    
    
    //Test 10.16: It tests the assignment with invalid parameters, namely the left argument parameter, specifically this one being coordinates out of range.   
    @Test(expected = InvalidCellCoordinatesException.class)
    public void badLeftArgCellOutOfRange(){
        AssignRangeCellIntegrator service = new AssignRangeCellIntegrator(userToken, 1, CELLCOORDS, inputTest16);
		service.execute();
    }
    
    // ************************ //
    // **** Right Arg Tests **** //
    // ************************ //
    
    //Test 10.17: It tests the assignment with invalid parameters, namely the right argument parameter, specifically this one being negative coordinates.   
    @Test(expected = InvalidDataException.class)
    public void badRightArgNegative(){
        AssignRangeCellIntegrator service = new AssignRangeCellIntegrator(userToken, 1, CELLCOORDS, inputTest17);
		service.execute();
    }  
    
    //Test 10.18: It tests the assignment with invalid parameters, namely the right argument parameter, specifically this one being empty string.   
    @Test(expected = InvalidDataException.class)
    public void badRightArgEmpty(){
        AssignRangeCellIntegrator service = new AssignRangeCellIntegrator(userToken, 1, CELLCOORDS, inputTest18);
		service.execute();
    }    
    
    //Test 10.19: It tests the assignment with invalid parameters, namely the right argument parameter, specifically this one being a string of characters without numbers.  
    @Test(expected = InvalidDataException.class)
    public void badRightArgString(){
    	AssignRangeCellIntegrator service = new AssignRangeCellIntegrator(userToken, 1, CELLCOORDS, inputTest19);
		service.execute();
    }    
    
    //Test 10.20: It tests the assignment with invalid parameters, namely the right argument parameter, specifically this one being coordinates out of range.   
    @Test(expected = InvalidCellCoordinatesException.class)
    public void badRightArgCellOutOfRange(){
        AssignRangeCellIntegrator service = new AssignRangeCellIntegrator(userToken, 1, CELLCOORDS, inputTest20);
		service.execute();
    }
    
    // ************************ //
    // **** Sheet ID Tests **** //
    // ************************ //
    
    //Test 10.21: It tests the assignment with invalid parameters, namely the sheet identifier parameter, specifically this one being an inexistent sheet (9001).
    @Test(expected = SheetDoesntExistsException.class)
    public void invalidSheetId9001() {
        AssignRangeCellIntegrator service = new AssignRangeCellIntegrator(userToken, 9001, CELLCOORDS, inputTestRest);
		service.execute();
    }
    
    //Test 10.22: It tests the assignment with invalid parameters, namely the sheet identifier parameter, specifically this one being an inexistent sheet (0).
    @Test(expected = SheetDoesntExistsException.class)
    public void invalidSheetId0() {
        AssignRangeCellIntegrator service = new AssignRangeCellIntegrator(userToken, 0, CELLCOORDS, inputTestRest);
		service.execute();
    }
    
	// ************************ //
    // *** User Token Tests *** //
    // ************************ //
    
    //Test 10.23 - It tests the assignment with valid parameters but without a valid session
    @Test(expected = UserNotInSessionException.class)
    public void invalidSession() {
    	removeUserFromSession(userToken);
        AssignRangeCellIntegrator service = new AssignRangeCellIntegrator(userToken, 1, CELLCOORDS, inputTestRest);
		service.execute();
    } 
    
    //Test 10.24: It tests the assignment with valid parameters from a user with write permissions
    @Test
    public void writePermissions(){
        AssignRangeCellIntegrator service = new AssignRangeCellIntegrator(writeToken, 1, CELLCOORDS, inputTestRest);
		service.execute();
		
		//check if the value is the same from the binary function's result
		assertEquals("Failure", AVG_RESULT_DIAG, service.getResult());
		
        // check if RangeFunction was created
		Sheet s = getSpreadSheet(SHEET_NAME);
		RangeFunction b = getRangeFunction(s, CELL_ROW, CELL_COLUMN);
        assertNotNull("RangeFunction was not created", b);
    }
    
    //Test 10.25: It tests the assignment with valid parameters but from a user with read permissions
    @Test(expected = UnauthorizedOperationException.class)
    public void readPermissions(){
        AssignRangeCellIntegrator service = new AssignRangeCellIntegrator(readToken, 1, CELLCOORDS, inputTestRest);
		service.execute();
    }
    
    //Test 10.26: It tests the assignment with valid parameters from the owner after the write permissions were removed to him
    @Test
    public void ownerNoWritePermissions(){
    	removeWritePermissions(SHEET_NAME, USERNAME);
    	
        AssignRangeCellIntegrator service = new AssignRangeCellIntegrator(userToken, 1, CELLCOORDS, inputTestRest);
		service.execute();
		
		//check if the value is the same from the binary function's result
		assertEquals("Failure", AVG_RESULT_DIAG, service.getResult());
		
        // check if RangeFunction was created
		Sheet s = getSpreadSheet(SHEET_NAME);
		RangeFunction b = getRangeFunction(s, CELL_ROW, CELL_COLUMN);
        assertNotNull("RangeFunction was not created", b);
    }
    
    //Test 10.27: It tests the assignment with valid parameters but from a user without any kind of permissions
    @Test(expected = UnauthorizedOperationException.class)
    public void noPermissions(){
        AssignRangeCellIntegrator service = new AssignRangeCellIntegrator(noRightsToken, 1, CELLCOORDS, inputTestRest);
		service.execute();
    }
    
    //Test 10.28 - It tests the assignment with invalid parameters, namely an empty string as user's token.
    @Test(expected = UserNotInSessionException.class)
    public void emptyUserToken() {
        AssignRangeCellIntegrator service = new AssignRangeCellIntegrator("", 1, CELLCOORDS, inputTestRest);
        service.execute();
    }
    
    //Test 10.29 - It tests the assignment with invalid parameters, namely a null value as user's token.
    @Test(expected = UserNotInSessionException.class)
    public void nullUserToken() {
        AssignRangeCellIntegrator service = new AssignRangeCellIntegrator(null, 1, CELLCOORDS, inputTestRest);
        service.execute();
    }
}
