package pt.tecnico.bubbledocs.integrator.component;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import pt.tecnico.bubbledocs.domain.BinaryFunction;
import pt.tecnico.bubbledocs.domain.Cell;
import pt.tecnico.bubbledocs.domain.Sheet;
import pt.tecnico.bubbledocs.domain.User;
import pt.tecnico.bubbledocs.exception.InvalidCellCoordinatesException;
import pt.tecnico.bubbledocs.exception.InvalidDataException;
import pt.tecnico.bubbledocs.exception.SheetDoesntExistsException;
import pt.tecnico.bubbledocs.exception.UnauthorizedOperationException;
import pt.tecnico.bubbledocs.exception.UserNotInSessionException;
import pt.tecnico.bubbledocs.integrator.AssignBinaryCellIntegrator;
import pt.tecnico.bubbledocs.integrator.BubbleDocsServiceTest;
import pt.tecnico.bubbledocs.service.local.AssignLiteralCell;

public class AssignBinaryCellTest extends BubbleDocsServiceTest {

	private String userToken;
	private String writeToken;
	private String readToken;
	private String noRightsToken;
	
	private static final String CELLCOORDS = "1;1";
	private static final String PROTECTED_CELL_COORDS = "1;4";
	private static final String LEFT_COORDS = "1;2";
	private static final String RIGHT_COORDS = "1;3";
	
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
	
	private static final String INT_ARG_42 ="42";
	private static final String INT_ARG_1 ="1";
	private static final String INT_ARG_0 = "0";
	
	private static final String ADD_RESULT = "43";
	private static final String SUB_RESULT = "41";
	private static final String MUL_DIV_RESULT = "42";
	
	private static final String ADD = "ADD";
	private static final String SUB = "SUB";
	private static final String MUL = "MUL";
	private static final String DIV = "DIV";
	
	private static final String BAD_FUNC_FEWER = "FF";
	private static final String BAD_FUNC_EXACT = "LOL";
	private static final String BAD_FUNC_MORE = "REKT";
	
	private static final int CELL_ROW = 1;
	private static final int CELL_COLUMN = 1;
	
	private static final String inputTest1 = ADD + "(" + LEFT_COORDS + "," + RIGHT_COORDS + ")";
	private static final String inputTest2 = ADD + "(" + LEFT_COORDS + "," + INT_ARG_1 + ")";
	private static final String inputTest3 = ADD + "(" + INT_ARG_42 + "," + INT_ARG_1 + ")";
	private static final String inputTest4 = SUB + "(" + LEFT_COORDS + "," + RIGHT_COORDS + ")";
	private static final String inputTest5 = SUB + "(" + LEFT_COORDS + "," + INT_ARG_1 + ")";
	private static final String inputTest6 = SUB + "(" + INT_ARG_42 + "," + INT_ARG_1 + ")";
	private static final String inputTest7 = MUL + "(" + LEFT_COORDS + "," + RIGHT_COORDS + ")";
	private static final String inputTest8 = MUL + "(" + LEFT_COORDS + "," + INT_ARG_1 + ")";
	private static final String inputTest9 = MUL + "(" + INT_ARG_42 + "," + INT_ARG_1 + ")";
	private static final String inputTest10 = DIV + "(" + LEFT_COORDS + "," + RIGHT_COORDS + ")";
	private static final String inputTest11 = DIV + "(" + LEFT_COORDS + "," + INT_ARG_1 + ")";
	private static final String inputTest12 = DIV + "(" + INT_ARG_42 + "," + INT_ARG_1 + ")";
	private static final String inputTest13 = DIV + "(" + INT_ARG_42 + "," + INT_ARG_0 + ")";
	private static final String inputTest14 = ADD + "(" + LEFT_COORDS + "," + PROTECTED_CELL_COORDS + ")";
	private static final String inputTest15 = ADD + "(" + INT_ARG_42 + "," + PROTECTED_CELL_COORDS + ")";
	private static final String inputTest16 = ADD + "(" + INT_ARG_42 + "," + INT_ARG_1 + ")";
	private static final String inputTest17 = BAD_FUNC_FEWER + "(" + INT_ARG_42 + "," + INT_ARG_1 + ")";
	private static final String inputTest18 = BAD_FUNC_EXACT + "(" + INT_ARG_42 + "," + INT_ARG_1 + ")";
	private static final String inputTest19 = BAD_FUNC_MORE + "(" + INT_ARG_42 + "," + INT_ARG_1 + ")";
	private static final String inputTest20 = EMPTY_STRING + "(" + INT_ARG_42 + "," + INT_ARG_1 + ")";
	private static final String inputTest21 = ADD + "(" + NEGATIVE_CELL_COORDS + "," + INT_ARG_1 + ")";
	private static final String inputTest22 = ADD + "(" + EMPTY_STRING + "," + INT_ARG_1 + ")";
	private static final String inputTest23 = ADD + "(" + POTATO_STRING + "," + INT_ARG_1 + ")";
	private static final String inputTest24 = ADD + "(" + OVER9K_STRING + "," + INT_ARG_1 + ")";
	private static final String inputTest25 = ADD + "(" + INT_ARG_1 + "," + NEGATIVE_CELL_COORDS + ")";
	private static final String inputTest26 = ADD + "(" + INT_ARG_1 + "," + EMPTY_STRING + ")";
	private static final String inputTest27 = ADD + "(" + INT_ARG_1 + "," + POTATO_STRING + ")";
	private static final String inputTest28 = ADD + "(" + INT_ARG_1 + "," + OVER9K_STRING + ")";
	private static final String inputTestRest = ADD + "(" + INT_ARG_42 + "," + INT_ARG_1 + ")";

	
	
	@Override
    public void populate4Test() {
		//create users
        User u = createUser(USERNAME, USER_PASS, USER_NAME);
        createUser(WRITE_USER_NAME, USER_PASS, USER_NAME);
    	createUser(READ_USER_NAME, USER_PASS, USER_NAME);
    	createUser(NO_RIGHTS_NAME, USER_PASS, USER_NAME);
    	
    	//create sheet
        createSpreadSheet(u, SHEET_NAME, 4, 4);
        
        
        //add users to sessions
        this.userToken = addUserToSession(u.getUsername());
        this.writeToken = addUserToSession(WRITE_USER_NAME);
    	this.readToken = addUserToSession(READ_USER_NAME);
    	this.noRightsToken = addUserToSession(NO_RIGHTS_NAME);
    	
    	// Assign a literal to some cell
    	AssignLiteralCell addLiteral = new AssignLiteralCell(userToken, 1, LEFT_COORDS, INT_ARG_42);
    	addLiteral.nonAtomicExecute();
    	
    	// Assign a literal to some cell
    	AssignLiteralCell addLiteral2 = new AssignLiteralCell(userToken, 1, RIGHT_COORDS, INT_ARG_1);
    	addLiteral2.nonAtomicExecute();
    	
    	// Assign a literal to some cell
    	AssignLiteralCell addLiteral3 = new AssignLiteralCell(userToken, 1, PROTECTED_CELL_COORDS, INT_ARG_1);
    	addLiteral3.nonAtomicExecute();
    	
    	//set some cells as protected
    	setCellAsProtected(SHEET_NAME, PROTECTED_CELL_COORDS);
        
    	//concede write and read permissions
    	addWritePermissions(SHEET_NAME, WRITE_USER_NAME);
    	addReadPermissions(SHEET_NAME, READ_USER_NAME);
    }
	
    private BinaryFunction getBinaryFunction(Sheet s, int row, int column) {
		Cell c = s.getCellByCoords(row, column);
    	return (BinaryFunction)c.getContent();
   	
    }
    

	// ******************** //
    // ***** ADD Tests **** //
    // ******************** //
    
    //Test 9.01 - It tests the assignment of an ADD function with valid parameters, namely two cells.
    @Test
    public void successADD_2CELL() {
        AssignBinaryCellIntegrator service = new AssignBinaryCellIntegrator(userToken, 1, CELLCOORDS, inputTest1);
		service.execute();
		
		//check if the value is the same from the binary function's result
		assertEquals("Failure", ADD_RESULT, service.getResult());
		
        // check if BinaryFunction was created
		Sheet s = getSpreadSheet(SHEET_NAME);
		BinaryFunction b = getBinaryFunction(s, CELL_ROW, CELL_COLUMN);
        assertNotNull("BinaryFunction was not created", b);
    }
    
    //Test 9.02 - It tests the assignment of an ADD function with valid parameters, namely an integer and a cell.
    @Test
    public void successADD_1INT1CELL() {
        AssignBinaryCellIntegrator service = new AssignBinaryCellIntegrator(userToken, 1, CELLCOORDS, inputTest2);
		service.execute();
		
		//check if the value is the same from the binary function's result
		assertEquals("Failure", ADD_RESULT, service.getResult());
		
        // check if BinaryFunction was created
		Sheet s = getSpreadSheet(SHEET_NAME);
		BinaryFunction b = getBinaryFunction(s, CELL_ROW, CELL_COLUMN);
        assertNotNull("BinaryFunction was not created", b);
    }
    
    //Test 9.03 - It tests the assignment of an ADD function with valid parameters, namely two integers.
    @Test
    public void successADD_2INT() {
        AssignBinaryCellIntegrator service = new AssignBinaryCellIntegrator(userToken, 1, CELLCOORDS, inputTest3);
		service.execute();
		
		//check if the value is the same from the binary function's result
		assertEquals("Failure", ADD_RESULT, service.getResult());
		
        // check if BinaryFunction was created
		Sheet s = getSpreadSheet(SHEET_NAME);
		BinaryFunction b = getBinaryFunction(s, CELL_ROW, CELL_COLUMN);
        assertNotNull("BinaryFunction was not created", b);
    }
    
	// ******************** //
    // ***** SUB Tests **** //
    // ******************** //
    
    //Test 9.04 - It tests the assignment of an SUB function with valid parameters, namely two cells.
    @Test
    public void successSUB_2CELL() {
        AssignBinaryCellIntegrator service = new AssignBinaryCellIntegrator(userToken, 1, CELLCOORDS, inputTest4);
		service.execute();
		
		//check if the value is the same from the binary function's result
		assertEquals("Failure", SUB_RESULT, service.getResult());
		
        // check if BinaryFunction was created
		Sheet s = getSpreadSheet(SHEET_NAME);
		BinaryFunction b = getBinaryFunction(s, CELL_ROW, CELL_COLUMN);
        assertNotNull("BinaryFunction was not created", b);
    }
    
    //Test 9.05 - It tests the assignment of an SUB function with valid parameters, namely an integer and a cell.
    @Test
    public void successSUB_1INT1CELL() {
        AssignBinaryCellIntegrator service = new AssignBinaryCellIntegrator(userToken, 1, CELLCOORDS, inputTest5);
		service.execute();
		
		//check if the value is the same from the binary function's result
		assertEquals("Failure", SUB_RESULT, service.getResult());
		
        // check if BinaryFunction was created
		Sheet s = getSpreadSheet(SHEET_NAME);
		BinaryFunction b = getBinaryFunction(s, CELL_ROW, CELL_COLUMN);
        assertNotNull("BinaryFunction was not created", b);
    }
    
    //Test 9.06 - It tests the assignment of an SUB function with valid parameters, namely two integers.
    @Test
    public void successSUB_2INT() {
        AssignBinaryCellIntegrator service = new AssignBinaryCellIntegrator(userToken, 1, CELLCOORDS, inputTest6);
		service.execute();
		
		//check if the value is the same from the binary function's result
		assertEquals("Failure", SUB_RESULT, service.getResult());
		
        // check if BinaryFunction was created
		Sheet s = getSpreadSheet(SHEET_NAME);
		BinaryFunction b = getBinaryFunction(s, CELL_ROW, CELL_COLUMN);
        assertNotNull("BinaryFunction was not created", b);
    }
    
	// ******************** //
    // ***** MUL Tests **** //
    // ******************** //
    
    //Test 9.07 - It tests the assignment of an MUL function with valid parameters, namely two cells.
    @Test
    public void successMUL_2CELL() {
        AssignBinaryCellIntegrator service = new AssignBinaryCellIntegrator(userToken, 1, CELLCOORDS, inputTest7);
		service.execute();
		
		//check if the value is the same from the binary function's result
		assertEquals("Failure", MUL_DIV_RESULT, service.getResult());
		
        // check if BinaryFunction was created
		Sheet s = getSpreadSheet(SHEET_NAME);
		BinaryFunction b = getBinaryFunction(s, CELL_ROW, CELL_COLUMN);
        assertNotNull("BinaryFunction was not created", b);
    }
    
    //Test 9.08 - It tests the assignment of an MUL function with valid parameters, namely an integer and a cell.
    @Test
    public void successMUL_1INT1CELL() {
        AssignBinaryCellIntegrator service = new AssignBinaryCellIntegrator(userToken, 1, CELLCOORDS, inputTest8);
		service.execute();
		
		//check if the value is the same from the binary function's result
		assertEquals("Failure", MUL_DIV_RESULT, service.getResult());
		
        // check if BinaryFunction was created
		Sheet s = getSpreadSheet(SHEET_NAME);
		BinaryFunction b = getBinaryFunction(s, CELL_ROW, CELL_COLUMN);
        assertNotNull("BinaryFunction was not created", b);
    }
    
    //Test 9.09 - It tests the assignment of an MUL function with valid parameters, namely two integers.
    @Test
    public void successMUL_2INT() {
        AssignBinaryCellIntegrator service = new AssignBinaryCellIntegrator(userToken, 1, CELLCOORDS, inputTest9);
		service.execute();
		
		//check if the value is the same from the binary function's result
		assertEquals("Failure", MUL_DIV_RESULT, service.getResult());
		
        // check if BinaryFunction was created
		Sheet s = getSpreadSheet(SHEET_NAME);
		BinaryFunction b = getBinaryFunction(s, CELL_ROW, CELL_COLUMN);
        assertNotNull("BinaryFunction was not created", b);
    }
    
	// ******************** //
    // ***** DIV Tests **** //
    // ******************** //
    
    //Test 9.10 - It tests the assignment of an DIV function with valid parameters, namely two cells.
    @Test
    public void successDIV_2CELL() {
        AssignBinaryCellIntegrator service = new AssignBinaryCellIntegrator(userToken, 1, CELLCOORDS, inputTest10);
		service.execute();
		
		//check if the value is the same from the binary function's result
		assertEquals("Failure", MUL_DIV_RESULT, service.getResult());
		
        // check if BinaryFunction was created
		Sheet s = getSpreadSheet(SHEET_NAME);
		BinaryFunction b = getBinaryFunction(s, CELL_ROW, CELL_COLUMN);
        assertNotNull("BinaryFunction was not created", b);
    }
    
    //Test 9.11 - It tests the assignment of an DIV function with valid parameters, namely an integer and a cell.
    @Test
    public void successDIV_1INT1CELL() {
        AssignBinaryCellIntegrator service = new AssignBinaryCellIntegrator(userToken, 1, CELLCOORDS, inputTest11);
		service.execute();
		
		//check if the value is the same from the binary function's result
		assertEquals("Failure", MUL_DIV_RESULT, service.getResult());
		
        // check if BinaryFunction was created
		Sheet s = getSpreadSheet(SHEET_NAME);
		BinaryFunction b = getBinaryFunction(s, CELL_ROW, CELL_COLUMN);
        assertNotNull("BinaryFunction was not created", b);
    }
    
    //Test 9.12 - It tests the assignment of an DIV function with valid parameters, namely two integers.
    @Test
    public void successDIV_2INT() {
        AssignBinaryCellIntegrator service = new AssignBinaryCellIntegrator(userToken, 1, CELLCOORDS, inputTest12);
		service.execute();
		
		//check if the value is the same from the binary function's result
		assertEquals("Failure", MUL_DIV_RESULT, service.getResult());
		
        // check if BinaryFunction was created
		Sheet s = getSpreadSheet(SHEET_NAME);
		BinaryFunction b = getBinaryFunction(s, CELL_ROW, CELL_COLUMN);
        assertNotNull("BinaryFunction was not created", b);
    }
    
    //Test 9.13 - It tests the assignment of an DIV function with valid parameters, namely two integers, one of them zero.
    @Test(expected = ArithmeticException.class)
    public void DIVbyZero() {
        AssignBinaryCellIntegrator service = new AssignBinaryCellIntegrator(userToken, 1, CELLCOORDS, inputTest13);
		service.execute();
    }
    
	// ************************* //
    // * Protected Cells Tests * //
    // ************************* //
    // Note: The behavior of each BinaryFunction regarding having as an argument a protected cell is the same to every one of them.
    //		 Therefore, only the ADD Function will be tested.
    
    //Test 9.14 - It tests the assignment of an ADD function with valid parameters, namely two cells, one of them protected.
    @Test
    public void successADD_2CELL_1Protected() {
        AssignBinaryCellIntegrator service = new AssignBinaryCellIntegrator(userToken, 1, CELLCOORDS, inputTest14);
		service.execute();
		
		//check if the value is the same from the binary function's result
		assertEquals("Failure", ADD_RESULT, service.getResult());
		
        // check if BinaryFunction was created
		Sheet s = getSpreadSheet(SHEET_NAME);
		BinaryFunction b = getBinaryFunction(s, CELL_ROW, CELL_COLUMN);
        assertNotNull("BinaryFunction was not created", b);
    }
    
    //Test 9.15 - It tests the assignment of an ADD function with valid parameters, namely an integer and a protected cell.
    @Test
    public void successADD_1INT1CELL_1Protected() {
        AssignBinaryCellIntegrator service = new AssignBinaryCellIntegrator(userToken, 1, CELLCOORDS, inputTest15);
		service.execute();
		
		//check if the value is the same from the binary function's result
		assertEquals("Failure", ADD_RESULT, service.getResult());
		
        // check if BinaryFunction was created
		Sheet s = getSpreadSheet(SHEET_NAME);
		BinaryFunction b = getBinaryFunction(s, CELL_ROW, CELL_COLUMN);
        assertNotNull("BinaryFunction was not created", b);
    }
    
    //Test 9.16: It tests the assignment of an ADD function with invalid parameters, namely the target cell parameter, specifically this one being a protected cell.
    @Test(expected = UnauthorizedOperationException.class)
    public void assignToProtected() {
        AssignBinaryCellIntegrator service = new AssignBinaryCellIntegrator(userToken, 1, PROTECTED_CELL_COORDS, inputTest16);
		service.execute();
    } 
    
	// ************************* //
    // * Other Functions Tests * //
    // ************************* //
    
    //Test 9.17: It tests the assignment with invalid parameters, namely the function name parameter, specifically this one having fewer letters than required.
    @Test(expected = InvalidDataException.class)
    public void badFunctionFewer(){
        AssignBinaryCellIntegrator service = new AssignBinaryCellIntegrator(userToken, 1, CELLCOORDS, inputTest17);
		service.execute();
    }
  
    //Test 9.18: It tests the assignment with invalid parameters, namely the function name parameter, specifically this one being a name that doesn't exist in the domain.
    @Test(expected = InvalidDataException.class)
    public void badFunctionExact(){
        AssignBinaryCellIntegrator service = new AssignBinaryCellIntegrator(userToken, 1, CELLCOORDS, inputTest18);
		service.execute();
    }
    
    //Test 9.19: It tests the assignment with invalid parameters, namely the function name parameter, specifically this one having more letters than required.
    @Test(expected = InvalidDataException.class)
    public void badFunctionMore(){
        AssignBinaryCellIntegrator service = new AssignBinaryCellIntegrator(userToken, 1, CELLCOORDS, inputTest19);
		service.execute();
    }
    
    //Test 9.20: It tests the assignment with invalid parameters, namely the function name parameter, specifically this one being an empty string.
    @Test(expected = InvalidDataException.class)
    public void badFunctionEmpty(){
        AssignBinaryCellIntegrator service = new AssignBinaryCellIntegrator(userToken, 1, CELLCOORDS, inputTest20);
		service.execute();
    }
    
    // ************************ //
    // **** Left Arg Tests **** //
    // ************************ //
    
    //Test 9.21: It tests the assignment with invalid parameters, namely the left argument parameter, specifically this one being negative coordinates.
    @Test(expected = InvalidDataException.class)
    public void badLeftArgNegative(){
        AssignBinaryCellIntegrator service = new AssignBinaryCellIntegrator(userToken, 1, CELLCOORDS, inputTest21);
		service.execute();
    }    
    
    //Test 9.22: It tests the assignment with invalid parameters, namely the left argument parameter, specifically this one being empty string.
    @Test(expected = InvalidDataException.class)
    public void badLeftArgEmpty(){
        AssignBinaryCellIntegrator service = new AssignBinaryCellIntegrator(userToken, 1, CELLCOORDS, inputTest22);
		service.execute();
    }    
    
    //Test 9.23: It tests the assignment with invalid parameters, namely the left argument parameter, specifically this one being a string of characters without numbers.
    @Test(expected = InvalidDataException.class)
    public void badLeftArgString(){
        AssignBinaryCellIntegrator service = new AssignBinaryCellIntegrator(userToken, 1, CELLCOORDS, inputTest23);
		service.execute();
    }    
    
    //Test 9.24: It tests the assignment with invalid parameters, namely the left argument parameter, specifically this one being coordinates out of range.
    @Test(expected = InvalidCellCoordinatesException.class)
    public void badLeftArgCellOutOfRange(){
        AssignBinaryCellIntegrator service = new AssignBinaryCellIntegrator(userToken, 1, CELLCOORDS, inputTest24);
		service.execute();
    }
    
    // ************************ //
    // **** Right Arg Tests **** //
    // ************************ //
    
    //Test 9.25: It tests the assignment with invalid parameters, namely the right argument parameter, specifically this one being negative coordinates.
    @Test(expected = InvalidDataException.class)
    public void badRightArgNegative(){
        AssignBinaryCellIntegrator service = new AssignBinaryCellIntegrator(userToken, 1, CELLCOORDS, inputTest25);
		service.execute();
    }  
    
    //Test 9.26: It tests the assignment with invalid parameters, namely the right argument parameter, specifically this one being empty string.
    @Test(expected = InvalidDataException.class)
    public void badRightArgEmpty(){
        AssignBinaryCellIntegrator service = new AssignBinaryCellIntegrator(userToken, 1, CELLCOORDS, inputTest26);
		service.execute();
    }    
    
    //Test 9.27: It tests the assignment with invalid parameters, namely the right argument parameter, specifically this one being a string of characters without numbers.
    @Test(expected = InvalidDataException.class)
    public void badRightArgString(){
        AssignBinaryCellIntegrator service = new AssignBinaryCellIntegrator(userToken, 1, CELLCOORDS, inputTest27);
		service.execute();
    }    
    
    //Test 9.28: It tests the assignment with invalid parameters, namely the right argument parameter, specifically this one being coordinates out of range.
    @Test(expected = InvalidCellCoordinatesException.class)
    public void badRightArgCellOutOfRange(){
        AssignBinaryCellIntegrator service = new AssignBinaryCellIntegrator(userToken, 1, CELLCOORDS, inputTest28);
		service.execute();
    }
    
    // ************************ //
    // **** Sheet ID Tests **** //
    // ************************ //
    
    //Test 9.29: It tests the assignment with invalid parameters, namely the sheet identifier parameter, specifically this one being an inexistent sheet (9001).
    @Test(expected = SheetDoesntExistsException.class)
    public void invalidSheetId9001() {
        AssignBinaryCellIntegrator service = new AssignBinaryCellIntegrator(userToken, 9001, CELLCOORDS, inputTestRest);
		service.execute();
    }
    
    //Test 9.30: It tests the assignment with invalid parameters, namely the sheet identifier parameter, specifically this one being an inexistent sheet (0).
    @Test(expected = SheetDoesntExistsException.class)
    public void invalidSheetId0() {
        AssignBinaryCellIntegrator service = new AssignBinaryCellIntegrator(userToken, 0, CELLCOORDS, inputTestRest);
		service.execute();
    }
    
	// ************************ //
    // *** User Token Tests *** //
    // ************************ //
    
    //Test 9.31 - It tests the assignment with valid parameters but without a valid session
    @Test(expected = UserNotInSessionException.class)
    public void invalidSession() {
    	removeUserFromSession(userToken);
        AssignBinaryCellIntegrator service = new AssignBinaryCellIntegrator(userToken, 1, CELLCOORDS, inputTestRest);
		service.execute();
    } 
    
    //Test 9.32 - It tests the assignment with valid parameters from a user with write permissions
    @Test
    public void writePermissions(){
        AssignBinaryCellIntegrator service = new AssignBinaryCellIntegrator(writeToken, 1, CELLCOORDS, inputTestRest);
		service.execute();
		
		//check if the value is the same from the binary function's result
		assertEquals("Failure", ADD_RESULT, service.getResult());
		
        // check if BinaryFunction was created
		Sheet s = getSpreadSheet(SHEET_NAME);
		BinaryFunction b = getBinaryFunction(s, CELL_ROW, CELL_COLUMN);
        assertNotNull("BinaryFunction was not created", b);
    }
    
    //Test 9.33 - It tests the assignment with valid parameters but from a user with read permissions
    @Test(expected = UnauthorizedOperationException.class)
    public void readPermissions(){
        AssignBinaryCellIntegrator service = new AssignBinaryCellIntegrator(readToken, 1, CELLCOORDS, inputTestRest);
		service.execute();
    }
    
    //Test 9.34 - It tests the assignment with valid parameters from the owner after the write permissions were removed to him
    @Test
    public void ownerNoWritePermissions(){
    	removeWritePermissions(SHEET_NAME, USERNAME);
    	
        AssignBinaryCellIntegrator service = new AssignBinaryCellIntegrator(userToken, 1, CELLCOORDS, inputTestRest);
		service.execute();
		
		//check if the value is the same from the binary function's result
		assertEquals("Failure", ADD_RESULT, service.getResult());
		
        // check if BinaryFunction was created
		Sheet s = getSpreadSheet(SHEET_NAME);
		BinaryFunction b = getBinaryFunction(s, CELL_ROW, CELL_COLUMN);
        assertNotNull("BinaryFunction was not created", b);
    }
    
    //Test 9.35 - It tests the assignment with valid parameters but from a user without any kind of permissions
    @Test(expected = UnauthorizedOperationException.class)
    public void noPermissions(){
        AssignBinaryCellIntegrator service = new AssignBinaryCellIntegrator(noRightsToken, 1, CELLCOORDS, inputTestRest);
		service.execute();
    }
    
    //Test 9.36 - It tests the assignment with invalid parameters, namely an empty string as user's token.
    @Test(expected = UserNotInSessionException.class)
    public void emptyUserToken() {
        AssignBinaryCellIntegrator service = new AssignBinaryCellIntegrator("", 1, CELLCOORDS, inputTestRest);
        service.execute();
    }
    
    //Test 9.37 - It tests the assignment with invalid parameters, namely a null value as user's token.
    @Test(expected = UserNotInSessionException.class)
    public void nullUserToken() {
        AssignBinaryCellIntegrator service = new AssignBinaryCellIntegrator(null, 1, CELLCOORDS, inputTestRest);
        service.execute();
    }
}