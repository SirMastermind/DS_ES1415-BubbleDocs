package pt.tecnico.bubbledocs.integrator.component;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.Test;
import org.jdom2.*;

import mockit.*;
import pt.tecnico.bubbledocs.domain.Cell;
import pt.tecnico.bubbledocs.domain.Reference;
import pt.tecnico.bubbledocs.domain.Sheet;
import pt.tecnico.bubbledocs.domain.User;
import pt.tecnico.bubbledocs.exception.*;
import pt.tecnico.bubbledocs.integrator.BubbleDocsServiceTest;
import pt.tecnico.bubbledocs.integrator.ExportDocumentIntegrator;
import pt.tecnico.bubbledocs.service.local.AssignLiteralCell;
import pt.tecnico.bubbledocs.service.local.AssignReferenceCell;
import pt.tecnico.bubbledocs.service.local.ImportDocument;
import pt.tecnico.bubbledocs.service.remote.StoreRemoteServices;


public class ExportDocumentTest extends BubbleDocsServiceTest {
	
	private static final String USER = "testUser";
	private static final String READ_USER = "readUser";
	private static final String WRITE_USER = "writUser";
	private static final String NO_RIGHTS_USER = "noRights";
	private static final String USERNAME = "testName";
	private static final String SHEETNAME = "testSheet";
	private static final int BADDOCID = 456;
	private static final int ZERODOCID = 0;
	private static final int NEGATIVEDOCID = -1;
	private static final int ROWS = 10;
	private static final int COLUMNS = 10;
	private static final String BADUSERTOKEN = "errado";
	private static final int CELLSNUM = 3;

	private static final String PASS = "testPass";
	private String userToken;
	private String writeToken;
	private String readToken;
	private String noRightsToken;
	private int sheetID;
	
	
	public void populate4Test() {
		
    	// Creates userS for testing
    	User tester = createUser(USER, PASS, USERNAME);
    	createUser(READ_USER, PASS, USERNAME);
    	createUser(WRITE_USER, PASS, USERNAME);
    	createUser(NO_RIGHTS_USER, PASS, USERNAME);
    	
    	
    	// Creates a new sheet and add it to the database
    	Sheet sheet = createSpreadSheet(tester, SHEETNAME, ROWS, COLUMNS);
    	sheetID = sheet.getId();
    	
    	userToken = addUserToSession(USER);
    	writeToken = addUserToSession(WRITE_USER);
    	readToken = addUserToSession(READ_USER);
    	noRightsToken = addUserToSession(NO_RIGHTS_USER);
    	
    	//concede write and read permissions
    	addWritePermissions(SHEETNAME, WRITE_USER);
    	addReadPermissions(SHEETNAME, READ_USER);
    	
    	// Assign a literal to some cell
    	AssignLiteralCell firstLiteral = new AssignLiteralCell(userToken, sheetID, "1;1", "42");
    	firstLiteral.nonAtomicExecute();;
    	
    	// Assign a literal to some cell
    	AssignLiteralCell secondLiteral = new AssignLiteralCell(userToken, sheetID, "1;2", "666");
    	secondLiteral.nonAtomicExecute();;
    	
    	// Assign a reference to some cell
    	AssignReferenceCell firstReference = new AssignReferenceCell(userToken, sheetID, "1;3", "1;2");
    	firstReference.nonAtomicExecute();;
    }
	
	//Function that breaks polymorphism and checks the sheet
	private void checkSheetContents(Sheet sheetTest, String owner){
		
		//verify some general sheet info
    	assertEquals("Wrong owner", owner, sheetTest.getOwner().getUsername());
    	assertEquals("Wrong number of rows", ROWS, sheetTest.getRows());
    	assertEquals("Wrong number of columns", COLUMNS, sheetTest.getColumns());
    	
		//verify that we have the same numbers of cells with content 
		assertEquals("Wrong number of cells with content", sheetTest.getCellsSet().size(), CELLSNUM);
		
		for(Cell cell : sheetTest.getCellsSet()) {
			if(cell.getRow()==1 && cell.getColumn()==1){
				assertEquals("Wrong Content in cell 1;1", cell.getContent().getClass().getSimpleName(), "Literal");
				assertEquals("Wrong value in cell 1;1", 42 , cell.getContent().getValue());
				continue;
			}
			if(cell.getRow()==1 && cell.getColumn()==2){
				assertEquals("Wrong Content in cell 1;2", cell.getContent().getClass().getSimpleName(), "Literal");
				assertEquals("Wrong value in cell 1;2", 666 , cell.getContent().getValue());
				continue;
			}
			if(cell.getRow()==1 && cell.getColumn()==3){
				assertEquals("Wrong Content in cell 1;3", cell.getContent().getClass().getSimpleName(), "Reference");
				assertEquals("Wrong value in cell 1;3", 666 , cell.getContent().getValue());
				Reference ref = (Reference) cell.getContent();
				//assert that we have the right reference
				assertSame("Wrong Reference", sheetTest.getCellByCoords(1, 2), ref.getCell());
				continue;
			}
			
			//not a expected a cell here
			assertEquals("Cell Not expected ", 1,2);
  		}
	}
	
	//Test 7.01 - It tests the export with valid parameters
	@Test
	public void success() throws JDOMException, IOException {
		new MockUp<StoreRemoteServices>() {
			   @Mock
			   public void storeDocument(String username, String docName, byte[] document){}
		};
		// Creates service with good parameters
		ExportDocumentIntegrator integrator = new ExportDocumentIntegrator(userToken, sheetID);
		integrator.execute();

		// Asserts
		/*
		 * Utilizador
		 * Numero de celulas
		 * Literal 42 na celula 1,1
		 * Literal 666 na celula 1,2
		 * Referencia a 1,2 em 1,3
		 */	  
		
		byte[] fileBytes = Files.readAllBytes(Paths.get(integrator.getFilename()));
		ImportDocument importService = new ImportDocument(userToken);
		importService.setFileBytes(fileBytes);
		importService.execute();
		
		checkSheetContents(importService.getSheet(), USER);
		  	
	}
	
    //Test 7.02 - It tests the export of a sheet with invalid parameters, a docId that does not exist.
    @Test(expected = SheetDoesntExistsException.class)
    public void NonExistentSheetID() {
    	new MockUp<StoreRemoteServices>() {
			   @Mock
			   public void storeDocument(String username, String docName, byte[] document){}
		};
    	ExportDocumentIntegrator integrator = new ExportDocumentIntegrator(userToken, BADDOCID);
    	integrator.execute();
    }
    
	//Test 7.03 - It tests the export of a sheet with invalid parameters, a docId that does not exist.
	@Test(expected = SheetDoesntExistsException.class)
    public void invalidSheetID() {
		new MockUp<StoreRemoteServices>() {
			   @Mock
			   public void storeDocument(String username, String docName, byte[] document){}
		};
    	ExportDocumentIntegrator integrator = new ExportDocumentIntegrator(userToken, ZERODOCID);
    
    	integrator.execute();
    }
	 
	//Test 7.04 - It tests the export of a sheet with invalid parameters, a negative docId.
	@Test(expected = SheetDoesntExistsException.class)
    public void negativeSheetID()  {
		new MockUp<StoreRemoteServices>() {
			   @Mock
			   public void storeDocument(String username, String docName, byte[] document){}
		};
    	ExportDocumentIntegrator integrator = new ExportDocumentIntegrator(userToken, NEGATIVEDOCID);
    
    	integrator.execute();
    }
	 
	//Test 7.05 - It tests the export of a sheet with valid parameters, but without valid session.
	@Test(expected = UserNotInSessionException.class)
    public void invalidUserToken() {
		new MockUp<StoreRemoteServices>() {
			   @Mock
			   public void storeDocument(String username, String docName, byte[] document){}
		};
    	ExportDocumentIntegrator integrator = new ExportDocumentIntegrator(BADUSERTOKEN, sheetID);
    
    	integrator.execute();
    }
	 
	 
	//Test 7.06 - It tests the export of a sheet with valid parameters, but from a user without permissions.
	@Test(expected = UnauthorizedOperationException.class)
    public void noPermissions() {
		new MockUp<StoreRemoteServices>() {
			   @Mock
			   public void storeDocument(String username, String docName, byte[] document){}
		};
    	ExportDocumentIntegrator integrator = new ExportDocumentIntegrator(noRightsToken, sheetID);
    	integrator.execute();
    }
	 
	//Test 7.07 - It tests the export of the document from a user with read permissions.
	@Test
    public void readPermissions() throws IOException, JDOMException{
		new MockUp<StoreRemoteServices>() {
			   @Mock
			   public void storeDocument(String username, String docName, byte[] document){}
		};
    	ExportDocumentIntegrator integrator = new ExportDocumentIntegrator(readToken, sheetID);
    	integrator.execute();
    	
    	
    	byte[] fileBytes = Files.readAllBytes(Paths.get(integrator.getFilename()));
		ImportDocument importService = new ImportDocument(readToken);
		importService.setFileBytes(fileBytes);
		importService.execute();
		    	
		checkSheetContents(importService.getSheet(), READ_USER);
    }
	 
	//Test 7.08 - It tests the export of the document from a user with write permissions.
	@Test
    public void writePermissions() throws IOException, JDOMException{
		new MockUp<StoreRemoteServices>() {
			   @Mock
			   public void storeDocument(String username, String docName, byte[] document){}
		};
    	ExportDocumentIntegrator integrator = new ExportDocumentIntegrator(writeToken, sheetID);
    	integrator.execute();
    	
    	
    	byte[] fileBytes = Files.readAllBytes(Paths.get(integrator.getFilename()));
		ImportDocument importService = new ImportDocument(writeToken);
		importService.setFileBytes(fileBytes);
		importService.execute();
		
		checkSheetContents(importService.getSheet(), WRITE_USER);
    }
	 
	//Test 7.09 - It tests the export of the document from owner after all his write and read permissions were removed.
    @Test
    public void ownerNoWritePermissions() throws IOException, JDOMException{
		new MockUp<StoreRemoteServices>() {
			   @Mock
			   public void storeDocument(String username, String docName, byte[] document){}
		};
	 	removeWritePermissions(SHEETNAME, USER);
    	ExportDocumentIntegrator integrator = new ExportDocumentIntegrator(userToken, sheetID);
    	integrator.execute();
    	
    	byte[] fileBytes = Files.readAllBytes(Paths.get(integrator.getFilename()));
		ImportDocument importService = new ImportDocument(userToken);
		importService.setFileBytes(fileBytes);
		importService.execute();
		
		checkSheetContents(importService.getSheet(), USER);
    }
    
	//Test 7.10 - It tests the storing of a document without space on storage.
	@Test(expected = CannotStoreDocumentException.class)
	public void storingWithoutSpace() throws IOException, JDOMException{
		new MockUp<StoreRemoteServices>() {
			   @Mock
			   public void storeDocument(String username, String docName, byte[] document) {
				   throw new CannotStoreDocumentException();
			   }
		};
		ExportDocumentIntegrator integrator = new ExportDocumentIntegrator(userToken, sheetID);
		integrator.execute();
	}
	
	//Test 7.11 - It tests the storing of a document with the SD-STORE server down.
	@Test(expected = UnavailableServiceException.class)
	public void storingWithoutConnection() throws IOException, JDOMException{
		new MockUp<StoreRemoteServices>() {
			   @Mock
			   public void storeDocument(String username, String docName, byte[] document) {
				   throw new RemoteInvocationException();
			   }
		};
		ExportDocumentIntegrator integrator = new ExportDocumentIntegrator(userToken, sheetID);
		integrator.execute();
	}
}