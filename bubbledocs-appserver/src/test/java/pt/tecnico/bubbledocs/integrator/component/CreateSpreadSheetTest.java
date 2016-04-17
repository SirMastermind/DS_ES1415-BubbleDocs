package pt.tecnico.bubbledocs.integrator.component;

import static org.junit.Assert.*;

import org.junit.Test;

import pt.tecnico.bubbledocs.domain.BubbleDocsServer;
import pt.tecnico.bubbledocs.domain.Sheet;
import pt.tecnico.bubbledocs.exception.*;
import pt.tecnico.bubbledocs.integrator.BubbleDocsServiceTest;
import pt.tecnico.bubbledocs.integrator.CreateSpreadSheetIntegrator;

// add needed import declarations

public class CreateSpreadSheetTest extends BubbleDocsServiceTest {

    // the tokens
    private String owner;
  

    private static final String USERNAME = "owner1";
    private static final String PASSWORD = "ownerpass";
    private static final String NAME = "sheet1";
    private static final String NO_NAME = "";
    private static final int DIM = 100;
    private static final int NEG_DIM = -100;
    private static final int EMPTY_DIM = 0;
   

    @Override
    public void populate4Test() {
        createUser(USERNAME, PASSWORD, "Pessoa Qualquer");
        owner = addUserToSession("owner1");
   
    }
    
    //Test 4.1 - It tests the creation of a sheet with valid parameters.
    @Test
    public void successCreateSheet() {
    	int expectedId = getBubbleDocsServer().getSheetNewID() + 1;
    	
        CreateSpreadSheetIntegrator integrator = new CreateSpreadSheetIntegrator(owner, NAME, DIM, DIM);
        integrator.execute();

        //	Sheet is the domain class that represents a Sheet
        Sheet sheet = getSpreadSheet(NAME);
        
        assertNotNull(integrator.getResult());
        assertEquals(NAME, sheet.getName());
        assertEquals(expectedId, sheet.getId());
        assertEquals(USERNAME, sheet.getOwner().getUsername());
        assertEquals(DIM, sheet.getRows());
        assertEquals(DIM, sheet.getColumns());
    }
    
    //Test 4.2 - Tests the creation of 2 sheets with the same name 
    // There's no need to compare the fields of the second sheet created
    // because only the first one with the exact name is returned.
    @Test
    public void sucessCreateSheetSameName() {
    	BubbleDocsServer bd = getBubbleDocsServer();
    	
    	CreateSpreadSheetIntegrator integrator = new CreateSpreadSheetIntegrator(owner, NAME, DIM, DIM);
        integrator.execute();
        integrator.execute();
          
        assertTrue("Wrong number of sheets", bd.getSheetsSet().size() == 2);
        
    }
    
    //Test 4.3 - It tests the creation of a sheet with negative dims
    @Test(expected = InvalidSheetDimensionsException.class)
    public void createSheetNegativeDimensions() {
    	CreateSpreadSheetIntegrator integrator = new CreateSpreadSheetIntegrator(owner, NAME, NEG_DIM, NEG_DIM);
        integrator.execute();
    }
    
    //Test 4.4 - It tests the creation of a sheet with dimensions 0;0
    @Test(expected = InvalidSheetDimensionsException.class)
    public void createSheetEmptyDimensions() {
    	CreateSpreadSheetIntegrator integrator = new CreateSpreadSheetIntegrator(owner, NAME, EMPTY_DIM, EMPTY_DIM);
        integrator.execute();
    }
    
    //Test 4.5 - It tests the creation of a sheet with empty name
    @Test(expected = InvalidSheetNameException.class)
    public void createSheetNoName() {
    	CreateSpreadSheetIntegrator integrator = new CreateSpreadSheetIntegrator(owner, NO_NAME, DIM, DIM);
        integrator.execute();
    }
    
    //Test 4.6 - It tests the creation of a sheet without valid session
    @Test(expected = UserNotInSessionException.class)
    public void invalidSession() {
    	removeUserFromSession(owner);
    	CreateSpreadSheetIntegrator integrator = new CreateSpreadSheetIntegrator(owner, NAME, DIM, DIM);
        integrator.execute();
    }
}