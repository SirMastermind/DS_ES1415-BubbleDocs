package pt.tecnico.bubbledocs.integrator.system.local;

import org.junit.Test;

import pt.tecnico.bubbledocs.domain.BubbleDocsServer;
import pt.tecnico.bubbledocs.domain.Sheet;
import pt.tecnico.bubbledocs.domain.User;
import pt.tecnico.bubbledocs.integrator.LoginUserIntegrator;

public class LocalStoryMainTest  extends LocalStory {
	 	private String root;
	    
	    private String token_tar;
	    private String token_pmj;
	    
	    private static final String USERNAME_A = "tar";
	    private static final String EMAIL_A = "tiago.rechau@sapo.pt";
	    private static final String PASSWORD_A = "tar1";
	    
	    private static final String USERNAME_B = "pmj";
	    private static final String EMAIL_B = "pedro.joaquim@hotmail.com";
	    private static final String PASSWORD_B = "pmj";
	    
	    private static final String DOCNAME = "sheet";
	    
	    private static final int ROWS = 3;
	    private static final int COLUMNS = 3;
	    
	    @Override
	    public void populate4Test() {
	        root = addUserToSession("root");
	        introMainLocal();
	    }
	    
	    private void introMainLocal() {
	    	System.out.println(">>>>>>>>>> Main Story <<<<<<<<<<");
	    	System.out.println(">>> In this story, only the following functionalities will be tested: ");
	    	System.out.println("> Assign literal to cell;");
	    	System.out.println("> Assign reference to cell;");
	    	System.out.println("> Create spreadsheet");
	    	System.out.println("> Create user; and");
	    	System.out.println("> Login user.");
	    	System.out.println("<>");
	    }
	    
	    private void introMainStory() {
	    	System.out.println(">>> The instructions are the following: ");
	    	System.out.println("> Creation of an user named <Tiago de Almeida Rechau>;");
	    	System.out.println("> Creation of an user named <Pedro Marcos Joaquim>;");
	    	System.out.println("> Login of the user <Tiago de Almeida Rechau>;");
	    	System.out.println("> Login of the user <Pedro Marcos Joaquim>;");
	    	System.out.println("> User <Tiago de Almeida Rechau> creates a spreadsheet;");
	    	System.out.println("> User <Tiago de Almeida Rechau> assigns a literal;");
	    	System.out.println("> User <Tiago de Almeida Rechau> assigns a reference;");
	    	System.out.println("> Print users' informations;");
	    	System.out.println("> Print users' spreadsheets;");
	    	System.out.println("> Export <Tiago de Almeida Rechau> spreadsheet;");
	    	System.out.println("> Print <Tiago de Almeida Rechau>'s spreadsheets;");
	    	System.out.println("> Import a spreadsheet to <Tiago de Almeida Rechau>;");
	    	System.out.println("> Print <Tiago de Almeida Rechau>'s spreadsheets;");
	    	System.out.println("> Export <Tiago de Almeida Rechau> spreadsheet;");
	    	System.out.println("<>");
	    }
	    
	    @Test
	    public void MainStorySuccess() {
	    	
	    	introMainStory();
	    	
	    	/*****************************************************************************************************************/
	    	
	    	// Creation of a user named <Tomas Martins Alves>
	        createUserStory(root, USERNAME_A, EMAIL_A, "Tiago de Almeida Rechau");
	        
	        // Creation of a user named <Andriy Zabolotnyy>
	        createUserStory(root, USERNAME_B, EMAIL_B, "Pedro Marcos Joaquim");
	        
	        /*****************************************************************************************************************/
			
			// Login of the user <Tomas Martins Alves>
	        LoginUserIntegrator service_tar = loginUserStory(USERNAME_A, PASSWORD_A);
	        token_tar = service_tar.getUserToken();
	        System.out.println(">>> Token associated with user tar: " + token_tar);
			
			// Login of the user <Andriy Zabolotnyy>
	        LoginUserIntegrator service_pmj = loginUserStory(USERNAME_B, PASSWORD_B);
	        token_pmj = service_pmj.getUserToken();
	        System.out.println(">>> Token associated with user pmj: " + token_pmj);
	        
	        /*****************************************************************************************************************/
	        
	        int doc_number_export = createSpreadSheetStory(token_tar, DOCNAME, ROWS, COLUMNS);
	        System.out.println(">>> Document number: " + doc_number_export);
	        System.out.println("<>");
	        
	        assignLiteralCellStory(token_tar, doc_number_export, "1;1","42");
	        
	        assignReferenceCellStory(token_tar, doc_number_export, "1;2","1;1");
	        
	        /*****************************************************************************************************************/
	        
	        System.out.println(">>> Users information");
	        BubbleDocsServer bd = BubbleDocsServer.getInstance();
	   		for (User user : bd.getUsersSet()) {
				System.out.println("UserName: " + user.getUsername() + " | Name: " + user.getName() + " | Password: " + user.getPassword());
		    }
	   		System.out.println("<>");
	   		
	   		/*****************************************************************************************************************/
	   		
	   		System.out.println(">>> Users documents");
	   		System.out.println(">>> Usertoken: tar");
	   		for (Sheet sheet : bd.getUserSheets("tar")) {
				System.out.println("Name: " + sheet.getName());
		    }
	   		
	   		System.out.println(">>> Usertoken: pmj");
	   		for (Sheet sheet : bd.getUserSheets("pmj")) {
				System.out.println("Name: " + sheet.getName());
		    }
	   		System.out.println("<>");
	   		
	   		/*****************************************************************************************************************/
	   		
	   		System.out.println(">>> Usertoken tar's spreadsheet");
	   		for (Sheet sheet_1 : bd.getUserSheets("tar")) {
					System.out.println("Name: " + sheet_1.getName());
					exportDocumentStory(token_tar, sheet_1.getId());
		    }
	   		System.out.println("<>");
	   		
	   		/*****************************************************************************************************************/
	   		
	   		System.out.println(">>> Remove usertoken tar's spreadsheet");
	   		Sheet sheet = bd.getUserSheets("tar",DOCNAME);
	   		User user = bd.getUserFromSession(token_tar);
	   		bd.removeSheet(user, sheet.getId());
	   		System.out.println("<>");
	   		
	   		/*****************************************************************************************************************/
	   		
	   		System.out.println(">>> Usertoken tar's spreadsheet");
	   		for (Sheet sheet_2 : bd.getUserSheets("tar")) {
				System.out.println("Name: " + sheet_2.getName() +" | ID: " + sheet_2.getId());
		    }
	   		System.out.println("<>");
	   		
	   		/*****************************************************************************************************************/	   		
	    	
	   		importDocumentStory(token_tar, doc_number_export);
	   		
	   		/*****************************************************************************************************************/
	   		
	   		System.out.println(">>> Usertoken tar's spreadsheet");
	   		for (Sheet sheet_3 : bd.getUserSheets("tar")) {
				System.out.println("Name: " + sheet_3.getName() +" | ID: " + sheet_3.getId());
		    }
	   		System.out.println("<>");
	   		
	   		/*****************************************************************************************************************/
	   		
	   		System.out.println(">>> Usertoken tar's spreadsheet");
	   		for (Sheet sheet_4 : bd.getUserSheets("tar")) {
					System.out.println("Name: " + sheet_4.getName());
					exportDocumentStory(token_tar, sheet_4.getId());
		    }
	   		System.out.println("<>");
	    }
}