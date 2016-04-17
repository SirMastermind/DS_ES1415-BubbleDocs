package pt.tecnico.bubbledocs.integrator.system.local;

import org.junit.Test;

import pt.tecnico.bubbledocs.domain.BubbleDocsServer;
import pt.tecnico.bubbledocs.domain.Sheet;
import pt.tecnico.bubbledocs.domain.User;
import pt.tecnico.bubbledocs.integrator.LoginUserIntegrator;

public class LocalStoryStoreTest extends LocalStory{
	
	   	private String root;
	    
	    private String token_mas;

	    private static final String USERNAME = "mas";
	    private static final String EMAIL = "miguel_sl23@hotmail.com";
	    private static final String PASSWORD = "mas5";
	    private static final String DOCNAME = "sheet";
	    
	    private static final int ROWS = 3;
	    private static final int COLUMNS = 3;
	    
	    @Override
	    public void populate4Test() {
	        root = addUserToSession("root");
	        introStoreLocal();
	    }
	    
	    private void introStoreLocal() {
	    	System.out.println(">>>>>>>>>> Store Story <<<<<<<<<<");
	    	System.out.println(">>> In this story, only the following functionalities will be tested: ");
	    	System.out.println("> Assign binary function to cell;");
	    	System.out.println("> Assign literal to cell;");
	    	System.out.println("> Assign range function to cell;");
	    	System.out.println("> Assign reference to cell;");
	    	System.out.println("> Create user;");
	    	System.out.println("> Export document;");
	    	System.out.println("> Get spreadsheet content;");
	    	System.out.println("> Import document;");
	    	System.out.println("> Login user;");
	    	System.out.println("<>");
	    }
	    
	    private void introStoreStory() {
	    	System.out.println(">>> The instructions are the following: ");
	    	System.out.println("> Create an user named <Miguel Alexandre Santos>;");
	    	System.out.println("> Login of an user named <Miguel Alexandre Santos>;");
	    	System.out.println("> Create a sheet by the user <Miguel Alexandre Santos>;");
	    	System.out.println("> Assign a literal;");
	    	System.out.println("> Assign a reference;");
	    	System.out.println("> Assign a binary function;");
	    	System.out.println("> Assign a range function;");
	    	System.out.println("> Get the sheet's content;");
	    	System.out.println("> Export the sheet;");
	    	System.out.println("> Delete the sheet;");
	    	System.out.println("> Import the sheet;");
	    	System.out.println("> List the sheets of the user <Miguel Alexandre Santos>;");
	    	System.out.println("<>");
	    }
	    
	    @Test
	    public void StoreStorySuccess() {
	    	
	    	introStoreStory();
	    	
	    	/*****************************************************************************************************************/
	    	
	    	// Creation of a user named <Miguel Alexandre Santos>
	        createUserStory(root, USERNAME, EMAIL, "Miguel Alexandre Santos");
	        
	        /*****************************************************************************************************************/
	        
			// Login of the user <Miguel Alexandre Santos>
	        LoginUserIntegrator service_tma = loginUserStory(USERNAME, PASSWORD);
	        token_mas = service_tma.getUserToken();
	        System.out.println(">>> Token associated with user tma: " + token_mas);
	        
	        /*****************************************************************************************************************/
	        
	        int doc_number = createSpreadSheetStory(token_mas, DOCNAME, ROWS, COLUMNS);
	        System.out.println(">>> Document number: " + doc_number);
	        System.out.println("<>");
	        
	        assignLiteralCellStory(token_mas, doc_number, "1;1","42");
	        
	        assignReferenceCellStory(token_mas, doc_number, "1;2", "1;1");
	        
	        assignBinaryCellStory(token_mas, doc_number, "1;3", "ADD(3,3)");
	        
	        assignRangeCellStory(token_mas, doc_number, "2;1", "AVG(1;1:1;3)");
	        
	        /*****************************************************************************************************************/
	        
	        getSpreadsheetContentStory(token_mas, doc_number);
	        
	        /*****************************************************************************************************************/
	        
	        exportDocumentStory(token_mas, doc_number);
	        
	        /*****************************************************************************************************************/
	        
	        BubbleDocsServer bd = BubbleDocsServer.getInstance();
	        
	   		System.out.println(">>> Remove usertoken mas's spreadsheet");
	   		Sheet sheet = bd.getUserSheets("mas", DOCNAME);
	   		User user = bd.getUserFromSession(token_mas);
	   		bd.removeSheet(user, sheet.getId());
	   		System.out.println("<>");
	   		
	   		/*****************************************************************************************************************/	   		
	    	
	   		importDocumentStory(token_mas, doc_number);
	   		
	   		/*****************************************************************************************************************/
	   		
	   		System.out.println(">>> Usertoken tar's spreadsheet");
	   		for (Sheet sheet_2 : bd.getUserSheets("mas")) {
				System.out.println("Name: " + sheet_2.getName() +" | ID: " + sheet_2.getId());
		    }
	   		System.out.println("<>");
	    }
}
