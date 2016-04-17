package pt.tecnico.bubbledocs.integrator.component;

import static org.junit.Assert.*;

import org.junit.Test;

import pt.tecnico.bubbledocs.domain.Sheet;
import pt.tecnico.bubbledocs.domain.Cell;
import pt.tecnico.bubbledocs.domain.Reference;
import pt.tecnico.bubbledocs.domain.User;
import pt.tecnico.bubbledocs.exception.InvalidCellCoordinatesException;
import pt.tecnico.bubbledocs.exception.SheetDoesntExistsException;
import pt.tecnico.bubbledocs.exception.UnauthorizedOperationException;
import pt.tecnico.bubbledocs.exception.UserNotInSessionException;
import pt.tecnico.bubbledocs.integrator.AssignReferenceCellIntegrator;
import pt.tecnico.bubbledocs.integrator.BubbleDocsServiceTest;
import pt.tecnico.bubbledocs.service.local.AssignLiteralCell;

public class AssignReferenceToCellTest extends BubbleDocsServiceTest {
	
	private String userToken;
	private String writeToken;
	private String readToken;
	private String noRightsToken;
	private static final String CELLCOORDS = "1;1";
	private static final String PROTECTED_CELL_COORDS = "1;4";
	private static final String REFCOORDS = "1;2";
	private static final String PROTECTED_REF_COORDS = "1;3";
	private static final String POTATO_STRING = "potato";
	private static final String OVER9K_STRING = "9001;9001";
	private static final String EMPTY_STRING = "";
	private static final String NEGATIVE_CELL_COORDS = "-1;-1";
	private static final String NEGATIVE_REF_COORDS = "-1;-2";
	private static final String SHEET_NAME = "testSheet";
	private static final String USER_NAME = "testName";
	private static final String USERNAME = "testUser";
	private static final String USER_PASS = "testPass";
	private static final String WRITE_USER_NAME = "write";
	private static final String READ_USER_NAME = "readUser";
	private static final String NO_RIGHTS_NAME = "noRights";
	private static final String INT_ARG ="42";
	private static final int CELL_ROW = 1;
	private static final int CELL_COLUMN = 1;
	private static final int REF_ROW = 1;
	private static final int REF_COLUMN = 2;
	private static final int PROTECTED_REF_ROW = 1;
	private static final int PROTECTED_REF_COLUMN = 3;
	
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
    	AssignLiteralCell addLiteral = new AssignLiteralCell(userToken, 1, REFCOORDS, INT_ARG);
    	addLiteral.nonAtomicExecute();
    	
    	// Assign a literal to some cell
    	AssignLiteralCell addLiteral2 = new AssignLiteralCell(userToken, 1, PROTECTED_REF_COORDS, INT_ARG);
    	addLiteral2.nonAtomicExecute();
    	
    	//set some cells as protected
    	setCellAsProtected(SHEET_NAME, PROTECTED_CELL_COORDS);
        setCellAsProtected(SHEET_NAME, PROTECTED_REF_COORDS);
        
    	//concede write and read permissions
    	addWritePermissions(SHEET_NAME, WRITE_USER_NAME);
    	addReadPermissions(SHEET_NAME, READ_USER_NAME);
    }

    private Reference getReference(Sheet s, int row, int column) {
		Cell c = s.getCellByCoords(row, column);
    	return (Reference)c.getContent();
   	
    }

    //Test 6.01 - It tests the assignment with valid parameters
    @Test
    public void success() {
        AssignReferenceCellIntegrator service = new AssignReferenceCellIntegrator(userToken, 1, CELLCOORDS, REFCOORDS);
		service.execute();
		
		
		//check if the value is the same from the ref
		assertEquals("Failure", INT_ARG, service.getResult());
		
        // check if reference was created
		Sheet s = getSpreadSheet(SHEET_NAME);
		Reference r = getReference(s, CELL_ROW, CELL_COLUMN);
        assertNotNull("Reference was not created", r);
        assertSame("Invalid Reference", s.getCellByCoords(REF_ROW, REF_COLUMN), r.getCell());
    }

    //Test 6.02 - It tests the assignment with invalid parameters, namely the docId parameter, specifically a non-existent one
    @Test(expected = SheetDoesntExistsException.class)
    public void invalidSheetId() {
        AssignReferenceCellIntegrator service = new AssignReferenceCellIntegrator(userToken, 9001, CELLCOORDS, REFCOORDS);
		service.execute();
    }
    
    //Test 6.03 - It tests the assignment with invalid parameters, namely the cellId parameter, specifically an out of range cell
    @Test(expected = InvalidCellCoordinatesException.class)
    public void InvalidCellRange() {
        AssignReferenceCellIntegrator service = new AssignReferenceCellIntegrator(userToken, 1, OVER9K_STRING, REFCOORDS);
        service.execute();
    }

    //Test 6.04 - It tests the assignment with valid parameters to a protected cell
    @Test(expected = UnauthorizedOperationException.class)
    public void ProtectedCellValidArguments() {
        AssignReferenceCellIntegrator service = new AssignReferenceCellIntegrator(userToken, 1, PROTECTED_CELL_COORDS, REFCOORDS);
        service.execute();
    }

    //Test 6.05 - It tests the assignment with invalid parameters, namely the reference parameter, specifically this one being a String
    @Test(expected = InvalidCellCoordinatesException.class)
    public void InvalidReferenceParameters_String() {
    	AssignReferenceCellIntegrator service = new AssignReferenceCellIntegrator(userToken, 1, CELLCOORDS, POTATO_STRING);
        service.execute();
    }
    
    //Test 6.06 - It tests the assignment with invalid parameters, namely the reference parameter, specifically the referenced cell being out of the sheet
    @Test(expected = InvalidCellCoordinatesException.class)
    public void ReferenceToInvalidCell() {
        AssignReferenceCellIntegrator service = new AssignReferenceCellIntegrator(userToken, 1, CELLCOORDS, OVER9K_STRING);
        service.execute();
    }
    
    //Test 6.07 - It tests the assignment with invalid parameters, namely the reference parameter, specifically this one being empty (empty string)
    @Test(expected = InvalidCellCoordinatesException.class)
    public void InvalidReferenceParameters_String_Empty() {
    	AssignReferenceCellIntegrator service = new AssignReferenceCellIntegrator(userToken, 1, CELLCOORDS, EMPTY_STRING);
        service.execute();
    }
    
    //Test 6.08 - It tests the assignment with invalid parameters, namely the reference parameter, specifically this one being empty (null value)
    @Test(expected = InvalidCellCoordinatesException.class)
    public void InvalidReferenceParameters_null() {
    	AssignReferenceCellIntegrator service = new AssignReferenceCellIntegrator(userToken, 1, CELLCOORDS, null);
        service.execute();
    }
    
    //Test 6.09 - It tests the assignment with invalid parameters, namely the cellId parameter, specifically this one being empty (null value)
    @Test(expected = InvalidCellCoordinatesException.class)
    public void InvalidCellParameters_null() {
    	AssignReferenceCellIntegrator service = new AssignReferenceCellIntegrator(userToken, 1, null, REFCOORDS);
        service.execute();
    }
    
    //Test 6.10 - It tests the assignment with invalid parameters, namely the reference parameter, specifically this one being empty (empty string)
    @Test(expected = InvalidCellCoordinatesException.class)
    public void InvalidCellParameters_String_Empty() {
    	AssignReferenceCellIntegrator service = new AssignReferenceCellIntegrator(userToken, 1, EMPTY_STRING, REFCOORDS);
        service.execute();
    }
    
    //Test 6.11 - It tests the assignment with invalid parameters, namely the reference parameter, specifically this one with negative coordinates
    @Test(expected = InvalidCellCoordinatesException.class)
    public void NegativeReferenceParameters() {
        AssignReferenceCellIntegrator service = new AssignReferenceCellIntegrator(userToken, 1, CELLCOORDS, NEGATIVE_REF_COORDS);
		service.execute();
    }
    
    //Test 6.12 - It tests the assignment with invalid parameters, namely the reference parameter, specifically this one being a String
    @Test(expected = InvalidCellCoordinatesException.class)
    public void InvalidCellParameters_String() {
    	AssignReferenceCellIntegrator service = new AssignReferenceCellIntegrator(userToken, 1, POTATO_STRING, REFCOORDS);
        service.execute();
    }
    
    //Test 6.13 - It tests the assignment with invalid parameters, namely the reference parameter, specifically this one being a String
    @Test(expected = InvalidCellCoordinatesException.class)
    public void NegativeCellParameters() {
        AssignReferenceCellIntegrator service = new AssignReferenceCellIntegrator(userToken, 1, NEGATIVE_CELL_COORDS, REFCOORDS);
		service.execute();
    }
    
    //Test 6.14 - It tests the assignment with valid parameters with a reference to a protected cell
    @Test
    public void successProtectedCell() {
        AssignReferenceCellIntegrator service = new AssignReferenceCellIntegrator(userToken, 1, CELLCOORDS, PROTECTED_REF_COORDS);
		service.execute();
		
		//check if the value is the same from the ref
		assertEquals("Failure", INT_ARG, service.getResult());
		
        // check if reference was created
		Sheet s = getSpreadSheet(SHEET_NAME);
		Reference r = getReference(s, CELL_ROW, CELL_COLUMN);
        assertNotNull("Reference was not created", r);
        assertSame("Invalid Reference", s.getCellByCoords(PROTECTED_REF_ROW, PROTECTED_REF_COLUMN), r.getCell());
    }
    
  //Test 6.15 - It tests the assignment with valid parameters but withou a valid session
    @Test(expected = UserNotInSessionException.class)
    public void invalidSession() {
    	removeUserFromSession(userToken);
        AssignReferenceCellIntegrator service = new AssignReferenceCellIntegrator(userToken, 1, CELLCOORDS, REFCOORDS);
		service.execute();
    } 
    
    
    //Test 6.16: It tests the assignment with valid parameters from a user with write permissions
    @Test
    public void writePermissions(){
    	 AssignReferenceCellIntegrator service = new AssignReferenceCellIntegrator(writeToken, 1, CELLCOORDS, PROTECTED_REF_COORDS);
 		 service.execute();

 		//check if the value is the same from the ref
 		assertEquals("Failure", INT_ARG, service.getResult());
 		
         // check if reference was created
 		Sheet s = getSpreadSheet(SHEET_NAME);
 		Reference r = getReference(s, CELL_ROW, CELL_COLUMN);
        assertNotNull("Reference was not created", r);
        assertSame("Invalid Reference", s.getCellByCoords(PROTECTED_REF_ROW, PROTECTED_REF_COLUMN), r.getCell());
    }
    
    //Test 6.17: It tests the assignment with valid parameters but from a user with read permissions
    @Test(expected = UnauthorizedOperationException.class)
    public void readPermissions(){
    	AssignReferenceCellIntegrator service = new AssignReferenceCellIntegrator(readToken, 1, CELLCOORDS, REFCOORDS);
		service.execute();
    }
    
    //Test 6.18: It tests the assignment with valid parameters from the owner after the write permissions were removed to him
    @Test
    public void ownerNoWritePermissions(){
    	removeWritePermissions(SHEET_NAME, USERNAME);
    	
    	AssignReferenceCellIntegrator service = new AssignReferenceCellIntegrator(userToken, 1, CELLCOORDS, PROTECTED_REF_COORDS);
		service.execute();

		//check if the value is the same from the ref
		assertEquals("Failure", INT_ARG, service.getResult());
		
        // check if reference was created
		Sheet s = getSpreadSheet(SHEET_NAME);
		Reference r = getReference(s, CELL_ROW, CELL_COLUMN);
        assertNotNull("Reference was not created", r);
        assertSame("Invalid Reference", s.getCellByCoords(PROTECTED_REF_ROW, PROTECTED_REF_COLUMN), r.getCell());
    	
    }
    
  //Test 6.19: It tests the assignment with valid parameters but from a user without any kind of permissions
    @Test(expected = UnauthorizedOperationException.class)
    public void noPermissions(){
    	AssignReferenceCellIntegrator service = new AssignReferenceCellIntegrator(noRightsToken, 1, CELLCOORDS, REFCOORDS);
		service.execute();
    }
}