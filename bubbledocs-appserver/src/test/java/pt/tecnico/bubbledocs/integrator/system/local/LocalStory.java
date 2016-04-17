package pt.tecnico.bubbledocs.integrator.system.local;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
  




import org.jdom2.JDOMException;

import mockit.Mock;
import mockit.MockUp;
import pt.tecnico.bubbledocs.domain.BubbleDocsServer;
import pt.tecnico.bubbledocs.dto.UserDTO;
import pt.tecnico.bubbledocs.integrator.AssignBinaryCellIntegrator;
import pt.tecnico.bubbledocs.integrator.AssignLiteralCellIntegrator;
import pt.tecnico.bubbledocs.integrator.AssignRangeCellIntegrator;
import pt.tecnico.bubbledocs.integrator.AssignReferenceCellIntegrator;
import pt.tecnico.bubbledocs.integrator.CreateUserIntegrator;
import pt.tecnico.bubbledocs.integrator.DeleteUserIntegrator;
import pt.tecnico.bubbledocs.integrator.ExportDocumentIntegrator;
import pt.tecnico.bubbledocs.integrator.GetSpreadSheetContentIntegrator;
import pt.tecnico.bubbledocs.integrator.ImportDocumentIntegrator;
import pt.tecnico.bubbledocs.integrator.LoginUserIntegrator;
import pt.tecnico.bubbledocs.integrator.RenewPasswordIntegrator;
import pt.tecnico.bubbledocs.service.local.CreateSpreadSheet;
import pt.tecnico.bubbledocs.service.local.GetUserInfo;
import pt.tecnico.bubbledocs.service.local.GetUsername4Token;
import pt.tecnico.bubbledocs.service.remote.IDRemoteServices;
import pt.tecnico.bubbledocs.service.remote.StoreRemoteServices;
import pt.tecnico.bubbledocs.toolkit.XMLConverter;

public class LocalStory extends pt.tecnico.bubbledocs.integrator.BubbleDocsServiceTest{

	byte[] docBytes = null;
	
	public void createUserStory(String root, String username, String email, String name) {
		System.out.println("Function called: Create user");
		System.out.println(">>> Arguments: ");
		System.out.println("> root: " + root);
		System.out.println("> username: " + username);
		System.out.println("> email: " + email);
		System.out.println("> name: " + name);
		CreateUserIntegrator service = new CreateUserIntegrator(root, username, email,name);
        
		new MockUp<IDRemoteServices>() {
			   @Mock
			   public void createUser(String username, String email) { }
		};
		
        service.execute();
        System.out.println("Create User successful.");
        System.out.println("<>");
	}
	
	public LoginUserIntegrator loginUserStory(String username, String password) {
		System.out.println("Function called: Login user");
		System.out.println(">>> Arguments: ");
		System.out.println("> username: " + username);
		System.out.println("> password: " + password);
		LoginUserIntegrator service = new LoginUserIntegrator(username, password);
        
   	 new MockUp<IDRemoteServices>() {
   		 @Mock
   		 public void loginUser(String username, String password){
   			 return;
   		 	}
		};
		
        service.execute();
        System.out.println("Login User successful.");
        System.out.println("<>");
        return service;
	}
	
	public void getUserInfoStory(String username) {
		System.out.println("Function called: Get user's info");
		System.out.println(">>> Arguments: ");
		System.out.println("> username: " + username);
		GetUserInfo service = new GetUserInfo(username);
        
		new MockUp<IDRemoteServices>() {
			   @Mock
			   public void createUser(String username, String email) { }
		};
		
        service.execute();
        
        UserDTO user = service.getUserDTO();
        
        System.out.println("<<< User's info");
        System.out.println("< name: " + user.getName());
        System.out.println("< email: " + user.getEmail());
        System.out.println("< username: " + user.getUsername());
        
        System.out.println("Get user's info successful.");
        System.out.println("<>");
	}
	
	public void getUsernameFromTokenStory(String token) {
		System.out.println("Function called: Get username from token");
		System.out.println(">>> Arguments: ");
		System.out.println("> token: " + token);
		GetUsername4Token service = new GetUsername4Token(token);
        
		new MockUp<IDRemoteServices>() {
			   @Mock
			   public void createUser(String username, String email) { }
		};
		
        service.execute();
    
        System.out.println("<<< Username from token");
        System.out.println("< username: " + service.getUsername());
        
        System.out.println("Get username from token successful.");
        System.out.println("<>");
	}
	
	public void renewPasswordStory(String token) {
		System.out.println("Function called: Renew password");
		System.out.println(">>> Arguments: ");
		System.out.println("> token: " + token);
		RenewPasswordIntegrator service = new RenewPasswordIntegrator(token);
	
		new MockUp<IDRemoteServices>(){
			@Mock
			public void renewPassword(String username){
			}
		};
		service.execute();	
        System.out.println("Renew password successful.");
        System.out.println("<>");
	}
	
	public void deleteUserStory(String token, String username) {
		System.out.println("Function called: Delete user");
		System.out.println(">>> Arguments: ");
		System.out.println("> token: " + token);
		System.out.println("> username: " + username);
		DeleteUserIntegrator service = new DeleteUserIntegrator(token, username);
    
		new MockUp<IDRemoteServices>() {
			@Mock
			public void removeUser(String username){}
		};
		
		service.execute();
        System.out.println("Delete user successful.");
        System.out.println("<>");
	}
	
	public int createSpreadSheetStory(String token, String name, int rows, int columns) {
		System.out.println("Function called: Create spreadsheet");
		System.out.println(">>> Arguments: ");
		System.out.println("> token: " + token);
		System.out.println("> name: " + name);
		System.out.println("> rows: " + rows);
		System.out.println("> columns: " + columns);
		CreateSpreadSheet service = new CreateSpreadSheet(token, name, rows, columns);
        service.execute();
        System.out.println("Create spreadsheet successful.");
        System.out.println("<>");
        return getSpreadSheet(name).getId();
	}
	
	public void assignLiteralCellStory(String token, int docId, String cell, String literal) {
		System.out.println("Function called: Assign literal to cell");
		System.out.println(">>> Arguments: ");
		System.out.println("> token: " + token);
		System.out.println("> docId: " + docId);
		System.out.println("> cell: " + cell);
		System.out.println("> literal: " + literal);
		AssignLiteralCellIntegrator service = new AssignLiteralCellIntegrator(token, docId, cell, literal);
		service.execute();
        System.out.println("Assign literal to cell successful.");
        System.out.println("<>");
	}
	
	public void assignReferenceCellStory(String token, int docId, String cell, String reference) {
		System.out.println("Function called: Assign reference to cell");
		System.out.println(">>> Arguments: ");
		System.out.println("> token: " + token);
		System.out.println("> docId: " + docId);
		System.out.println("> cell: " + cell);
		System.out.println("> reference: " + reference);
		AssignReferenceCellIntegrator service = new AssignReferenceCellIntegrator(token, docId, cell, reference);
		service.execute();
        System.out.println("Assign reference to cell successful.");
        System.out.println("<>");
	}
	
	public void assignBinaryCellStory(String token, int docId, String cell, String function) {
		System.out.println("Function called: Assign binary to cell");
		System.out.println(">>> Arguments: ");
		System.out.println("> token: " + token);
		System.out.println("> docId: " + docId);
		System.out.println("> cell: " + cell);
		System.out.println("> function: " + function);
		AssignBinaryCellIntegrator service = new AssignBinaryCellIntegrator(token, docId, cell, function);
		service.execute();
        System.out.println("Assign binary to cell successful.");
        System.out.println("<>");
	}
	
	public void assignRangeCellStory(String token, int docId, String cell, String function) {
		System.out.println("Function called: Assign range to cell");
		System.out.println(">>> Arguments: ");
		System.out.println("> token: " + token);
		System.out.println("> docId: " + docId);
		System.out.println("> cell: " + cell);
		System.out.println("> function: " + function);
		AssignRangeCellIntegrator service = new AssignRangeCellIntegrator(token, docId, cell, function);
		service.execute();
        System.out.println("Assign range to cell successful.");
        System.out.println("<>");
	}
	
	public void exportDocumentStory(String token, final int docId) {
		System.out.println("Function called: Export document");
		System.out.println(">>> Arguments: ");
		System.out.println("> token: " + token);
		System.out.println("> docId: " + docId);
		
		final GetUsername4Token service_exp = new GetUsername4Token(token);
        
		new MockUp<IDRemoteServices>() {
			   @Mock
			   public void createUser(String username, String email) { }
		};
		
        service_exp.execute();
		new MockUp<StoreRemoteServices>() {
			   @Mock
			   public void storeDocument(String username, String docName, byte[] document){
				   try {
					XMLConverter.convertToXML(BubbleDocsServer.getInstance().getSheetByID(docId), service_exp.getUsername());
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			   }
		};
		
		ExportDocumentIntegrator service = new ExportDocumentIntegrator(token, docId);
		service.execute();
        System.out.println("Export document successful.");
        System.out.println("<>");
	}
	
	public void importDocumentStory(String token, int docId) {
		System.out.println("Function called: Import document");
		System.out.println(">>> Arguments: ");
		System.out.println("> token: " + token);
		System.out.println("> docId: " + docId);
		docBytes = null;
		
		new MockUp<StoreRemoteServices>() {
			   @Mock
			   public byte[] loadDocument(String username, String docName) throws JDOMException, IOException{
				   	FileInputStream fileInputStream = null;
				   
			        File file = new File("sheet1.xml");
			 
			        byte[] bFile = new byte[(int) file.length()];
			 
			        try {
			            //convert file into array of bytes
				    fileInputStream = new FileInputStream(file);
				    fileInputStream.read(bFile);
				    fileInputStream.close();
			 
				    for (int i = 0; i < bFile.length; i++) {
				       	System.out.print((char)bFile[i]);
			            }
			 
				    System.out.println("Done");
			        }catch(Exception e){
			        	e.printStackTrace();
			        }
				   return bFile;
			   }
		};
		
		ImportDocumentIntegrator importService = new ImportDocumentIntegrator(token, docId);
		importService.execute();
        System.out.println("Import document successful.");
        System.out.println("<>");
	}
	
	public void getSpreadsheetContentStory(String token, int docId) {
		System.out.println("Function called: Get spreadsheet content");
		System.out.println(">>> Arguments: ");
		System.out.println("> token: " + token);
		System.out.println("> docId: " + docId);
		GetSpreadSheetContentIntegrator integrator = new GetSpreadSheetContentIntegrator(token, docId);
		integrator.execute();
		BubbleDocsServer bd = BubbleDocsServer.getInstance();
		String[][] s = integrator.getResult();
		for(int i = 0; i < bd.getSheetByID(docId).getRows(); i++) {
			System.out.print("[");
			for(int j = 0; j < bd.getSheetByID(docId).getColumns(); j++) {
				System.out.print(" " + s[i][j] + " ");
			}
			System.out.print("]");
			System.out.println("");
		}
        System.out.println("Get spreadsheet content successful.");
        System.out.println("<>");
	}
}
