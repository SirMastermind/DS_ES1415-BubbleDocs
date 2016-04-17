package pt.tecnico.bubbledocs.integrator.component;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;

import mockit.Mock;
import mockit.MockUp;

import org.jdom2.JDOMException;
import org.junit.Test;

import pt.tecnico.bubbledocs.domain.Cell;
import pt.tecnico.bubbledocs.domain.Reference;
import pt.tecnico.bubbledocs.domain.Sheet;
import pt.tecnico.bubbledocs.domain.User;
import pt.tecnico.bubbledocs.exception.CannotLoadDocumentException;
import pt.tecnico.bubbledocs.exception.RemoteInvocationException;
import pt.tecnico.bubbledocs.exception.UnavailableServiceException;
import pt.tecnico.bubbledocs.exception.UserNotInSessionException;
import pt.tecnico.bubbledocs.exception.InvalidDataException;
import pt.tecnico.bubbledocs.integrator.BubbleDocsServiceTest;
import pt.tecnico.bubbledocs.integrator.ImportDocumentIntegrator;
import pt.tecnico.bubbledocs.service.local.AssignLiteralCell;
import pt.tecnico.bubbledocs.service.local.AssignReferenceCell;
import pt.tecnico.bubbledocs.service.remote.StoreRemoteServices;
import pt.tecnico.bubbledocs.toolkit.XMLConverter;

public class ImportDocumentTest extends BubbleDocsServiceTest {
	
	private static final String USER1 = "user1";
	private static final String USER2 = "user2";
	private static final String USERNAME = "testName";
	private static final String SHEETNAME1 = "sheet1";
	private static final String SHEETNAME2 = "sheet2";
	private static final int BADDOCID = 456;
	private static final int COLUMNS = 10;
	private static final int ROWS = 10;
	private static final String BADUSERTOKEN = "errado";
	private static final int CELLSNUM = 3;
	private static final String EMAIL1 =  "local@host";
	private static final String EMAIL2 = "local2@host";
	private static final String LIT1CELL = "1;1";
	private static final String LIT2CELL = "1;2";
	private static final String REF1CELL = "1;3";
	private static final String LIT1 = "42";
	private static final String LIT2 = "666";
	private static final String REF1 = "1;2";
	
	private String user1Token;
	private String user2Token;
	private int sheet1ID;
	private int sheet2ID;
	private byte[] doc1Bytes;

	public void populate4Test() {
		
    	// Creates users for testing
    	User user1 = createUser(USER1, EMAIL1, USERNAME);
    	User user2 = createUser(USER2, EMAIL2, USERNAME);

    	
    	
    	// Creates a new sheet and add it to the database
    	Sheet sheet1 = createSpreadSheet(user1, SHEETNAME1, ROWS, COLUMNS);
    	sheet1ID = sheet1.getId();
    	
    	Sheet sheet2 = createSpreadSheet(user2, SHEETNAME2, ROWS, COLUMNS);
    	sheet2ID = sheet2.getId();
    	
    	//add users to session
    	user1Token = addUserToSession(USER1);
    	user2Token = addUserToSession(USER2);
    	
    	//add contents to sheet
    	
    	// Assign a literal to some cell
    	AssignLiteralCell firstLiteral1 = new AssignLiteralCell(user1Token, sheet1ID, LIT1CELL, LIT1);
    	AssignLiteralCell firstLiteral2 = new AssignLiteralCell(user2Token, sheet2ID, LIT1CELL, LIT1); 
    	firstLiteral1.nonAtomicExecute();
    	firstLiteral2.nonAtomicExecute();
    	
    	// Assign a literal to some cell
    	AssignLiteralCell secondLiteral1 = new AssignLiteralCell(user1Token, sheet1ID, LIT2CELL, LIT2);
    	AssignLiteralCell secondLiteral2 = new AssignLiteralCell(user2Token, sheet2ID, LIT2CELL, LIT2);
    	secondLiteral1.nonAtomicExecute();
    	secondLiteral2.nonAtomicExecute();
    	
    	// Assign a reference to some cell
    	AssignReferenceCell firstReference1 = new AssignReferenceCell(user1Token, sheet1ID, REF1CELL, REF1);
    	AssignReferenceCell firstReference2 = new AssignReferenceCell(user2Token, sheet2ID, REF1CELL, REF1);
    	firstReference1.nonAtomicExecute();
    	firstReference2.nonAtomicExecute();
    	
    	//the documents import result
    	try {
			doc1Bytes = XMLConverter.convertToXML(sheet1, USER1);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    	
    }
	
	//Function that breaks polymorphism and checks the sheet
	private void checkSheetContents(Sheet sheetTest, String owner){
		
		//verify some general sheet info
    	assertEquals("Wrong owner", owner, sheetTest.getOwner().getUsername());
    	assertEquals("Wrong number of rows", ROWS, sheetTest.getRows());
    	assertEquals("Wrong number of columns", COLUMNS, sheetTest.getColumns());
    	assertFalse("The doc ids should be diferent", sheetTest.getId() == sheet1ID);
    	
		//verify that we have the same numbers of cells with content 
		assertEquals("Wrong number of cells with content", sheetTest.getCellsSet().size(), CELLSNUM);
		
		for(Cell cell : sheetTest.getCellsSet()) {
			if(cell.getRow()==1 && cell.getColumn()==1){
				assertEquals("Wrong Content in cell 1;1", cell.getContent().getClass().getSimpleName(), "Literal");
				assertEquals("Wrong value in cell 1;1", Integer.parseInt(LIT1) , cell.getContent().getValue());
				continue;
			}
			if(cell.getRow()==1 && cell.getColumn()==2){
				assertEquals("Wrong Content in cell 1;2", cell.getContent().getClass().getSimpleName(), "Literal");
				assertEquals("Wrong value in cell 1;2", Integer.parseInt(LIT2) , cell.getContent().getValue());
				continue;
			}
			if(cell.getRow()==1 && cell.getColumn()==3){
				assertEquals("Wrong Content in cell 1;3", cell.getContent().getClass().getSimpleName(), "Reference");
				assertEquals("Wrong value in cell 1;3", Integer.parseInt(LIT2) , cell.getContent().getValue());
				Reference ref = (Reference) cell.getContent();
				//assert that we have the right reference
				assertSame("Wrong Reference", sheetTest.getCellByCoords(1, 2), ref.getCell());
				continue;
			}
			
			//not a expected a cell here
			assertEquals("Cell Not expected ", 1,2);
  		}
	}
	
	//Test 14.01 - It tests the import with valid parameters.
	@Test
	public void success() throws JDOMException, IOException {
		
		new MockUp<StoreRemoteServices>() {
			   @Mock
			   public byte[] loadDocument(String username, String docName){
				   return doc1Bytes;
			   }
		};
		
		// Creates service with good parameters
		ImportDocumentIntegrator integrator = new ImportDocumentIntegrator(user1Token, sheet1ID);
		integrator.execute();
  
		
		Sheet sheetTest = integrator.getSheet();
		checkSheetContents(sheetTest, USER1);
		  	
	}
	
    //Test 14.02 - It tests sheet import with invalid parameters, namely a docId that does not exist.
    @Test(expected = CannotLoadDocumentException.class)
    public void NonExistentSheetID() {
    	new MockUp<StoreRemoteServices>() {
			   @Mock
			   public byte[] loadDocument(String username, String docName){
				   throw new CannotLoadDocumentException();
			   }
		};
		
    	ImportDocumentIntegrator integrator = new ImportDocumentIntegrator(user1Token, BADDOCID);
    	integrator.execute();
    }
    
	//Test 14.03 - It tests the import of a sheet with invalid parameters, namely a docId that exists but from another user.
	@Test(expected = CannotLoadDocumentException.class)
    public void invalidSheetID() {
		new MockUp<StoreRemoteServices>() {
			 @Mock
			   public byte[] loadDocument(String username, String docName){
				   throw new CannotLoadDocumentException();
			  }
		};
		
		ImportDocumentIntegrator integrator = new ImportDocumentIntegrator(user1Token, sheet2ID);
    	integrator.execute();
    }
	
	
	//Test 14.04 - It tests the import of a sheet with valid parameters, but the user without valid session.
	@Test(expected = UserNotInSessionException.class)
    public void invalidUserToken() {

		ImportDocumentIntegrator integrator = new ImportDocumentIntegrator(BADUSERTOKEN, sheet1ID);
    	integrator.execute();
    }
	
	//Test 14.05 - It tests the import of a sheet with invalid parameters, namely a null value.
	@Test(expected = InvalidDataException.class)
    public void nullUserToken() {

		ImportDocumentIntegrator integrator = new ImportDocumentIntegrator(null, sheet1ID);
    	integrator.execute();
    }
	
	//Test 14.06 - It tests the import of a sheet with invalid parameters, namely an empty value.
	@Test(expected = UserNotInSessionException.class)
    public void emptyUserToken() {

		ImportDocumentIntegrator integrator = new ImportDocumentIntegrator("", sheet1ID);
    	integrator.execute();
    }
	
	//Test 14.07 - It tests sheet import with valid parameters, but with remote service down.
	@Test(expected = UnavailableServiceException.class)
    public void remoteDown() {
		new MockUp<StoreRemoteServices>() {
			 @Mock
			   public byte[] loadDocument(String username, String docName){
				   throw new RemoteInvocationException();
			  }
		};
		
		ImportDocumentIntegrator integrator = new ImportDocumentIntegrator(user1Token, sheet1ID);
    	integrator.execute();
    }	
}