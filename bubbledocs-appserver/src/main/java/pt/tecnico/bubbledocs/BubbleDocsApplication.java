 package pt.tecnico.bubbledocs;

import java.io.IOException;

import pt.tecnico.bubbledocs.domain.*;
import pt.tecnico.bubbledocs.exception.*;
import pt.tecnico.bubbledocs.integrator.ImportDocumentIntegrator;
import pt.tecnico.bubbledocs.service.local.AssignLiteralCell;
import pt.tecnico.bubbledocs.service.local.AssignReferenceCell;
import pt.tecnico.bubbledocs.service.local.CreateSpreadSheet;
import pt.tecnico.bubbledocs.service.local.CreateUser;
import pt.tecnico.bubbledocs.service.local.ExportDocument;
import pt.tecnico.bubbledocs.service.local.LoginUser;

import org.jdom2.JDOMException;

import pt.ist.fenixframework.FenixFramework;
import pt.ist.fenixframework.TransactionManager;

import javax.transaction.*;


public class BubbleDocsApplication {

	public static void main(String[] args) throws UserDoesntExistsException, InvalidDataException, IOException, JDOMException{

		TransactionManager tm = FenixFramework.getTransactionManager();
	    boolean committed = false;
	    int fileID = -1;
	   
	    
	    /*
	     * Teste 1 - Escrever a informacao sobre todos os utilizadores registados na aplicacao
	     */
	  
	    	try {
	   		tm.begin();
	   		
	   		BubbleDocsServer bd = BubbleDocsServer.getInstance();
		    populateDomain(bd);
		    
		    System.out.println("=================================================================");
	   		System.out.println("Teste 1 - Escrever os dados de todos os utilizadores");
	   		System.out.println("=================================================================");
	   		
	   		for (User user : bd.getUsersSet()) {
				System.out.println("UserName: " + user.getUsername() + " Name: " + user.getName() + " password: " + user.getPassword());
		    }
	   		
	   		tm.commit();
	   		committed = true;
	   	} catch (UserNotInSessionException | SystemException| NotSupportedException | RollbackException| HeuristicMixedException | HeuristicRollbackException ex) {
		    System.err.println("Error in execution of transaction: " + ex);
		} finally {
		    if (!committed) 
			try {
			    tm.rollback();
			} catch (SystemException ex) {
			    System.err.println("Error in roll back of transaction: " + ex);
			}
	    }
	   	
	   	committed = false;
	   	
	   	/*
	   	 * Teste 2 - Escrever os nomes de todas as folhas de calculo dos utilizadores pf e ra
	   	 */
	    	try {
	   		tm.begin();
	   		
	   		BubbleDocsServer bd = BubbleDocsServer.getInstance();
		    populateDomain(bd);
		    
		    System.out.println("=================================================================");
	   		System.out.println("Teste 2 - Escrever os nomes de todas as folhas de calculo dos utilizadores pf e ra");
	   		System.out.println("=================================================================");
	   		
	   		System.out.println("Sheets pf:");
	   		for (Sheet sheet : bd.getUserSheets("pf")) {
				System.out.println("Name:" + sheet.getName());
		    }
	   		
	   		System.out.println("\nSheets ra:");
	   		for (Sheet sheet : bd.getUserSheets("ra")) {
				System.out.println("Name:" + sheet.getName());
		    }
	   		
	   		tm.commit();
	   		committed = true;
	   	} catch (UserNotInSessionException | SystemException| NotSupportedException | RollbackException| HeuristicMixedException | HeuristicRollbackException ex) {
		    System.err.println("Error in execution of transaction: " + ex);
		} finally {
		    if (!committed) 
			try {
			    tm.rollback();
			} catch (SystemException ex) {
			    System.err.println("Error in roll back of transaction: " + ex);
			}
	    }
	   	
	   	committed = false;
	   	
		/*
	   	 * Teste 3 - Exportar todas as folhas do utilizador pf para XML
	   	 */
	   
	   	try {
	   		tm.begin();
	   		BubbleDocsServer bd = BubbleDocsServer.getInstance();
		    populateDomain(bd);
		    
		    
		    System.out.println("=================================================================");
	   		System.out.println("Teste 3 - Exportar todas as folhas do utilizador pf para XML");
	   		System.out.println("=================================================================");
	   		
	   		//Login
			LoginUser loginPF = new LoginUser("pf","sub");
			loginPF.execute();
			String token = loginPF.getUserToken();
			
	   		System.out.println("Sheets pf:");
	   		for (Sheet sheet : bd.getUserSheets("pf")) {
					System.out.println("\n\nName:" + sheet.getName());
					
					//Export document
					ExportDocument service = new ExportDocument(token,sheet.getId());
					service.execute();
					fileID = sheet.getId();

		    }
	   		tm.commit();
	   		committed = true;
	   	} catch (UserNotInSessionException | SystemException| NotSupportedException | RollbackException| HeuristicMixedException | HeuristicRollbackException ex) {
		    System.err.println("Error in execution of transaction: " + ex);
		} catch (SheetDoesntExistsException e) {
			  System.err.println("Sheet does not exists");
		} catch (UnauthorizedOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
		    if (!committed) 
			try {
			    tm.rollback();
			} catch (SystemException ex) {
			    System.err.println("Error in roll back of transaction: " + ex);
			}
	    }
	   	
	   	committed = false;
	   	
	   	/*
	   	 * Teste 4 - Remover da app a folha Notas Es do utilizador pf
	   	 */
	   
	   	try {
	   		tm.begin();
	   		BubbleDocsServer bd = BubbleDocsServer.getInstance();
		    populateDomain(bd);
		    
		    System.out.println("=================================================================");
	   		System.out.println("Teste 4 - Remover da app a folha Notas Es do utilizador pf");
	   		System.out.println("=================================================================");
	   		
	   		//Login
			LoginUser loginPF = new LoginUser("pf","sub");
			loginPF.execute();
			String token = loginPF.getUserToken();
			User user = bd.getUserFromSession(token);
			
	   		Sheet sheet = bd.getUserSheets("pf","Notas ES");
	   		

	   		bd.removeSheet(user, sheet.getId());
	   		
	   		
	   		tm.commit();
	   		committed = true;
	   	} catch(UnauthorizedOperationException e1) {
	   		System.out.println("Unauthorized operation");
	   	} catch(SheetDoesntExistsException e2) {
		   		System.out.println("Sheet does not exists");
	   	} catch (UserNotInSessionException |SystemException| NotSupportedException | RollbackException| HeuristicMixedException | HeuristicRollbackException ex) {
		    System.err.println("Error in execution of transaction: " + ex);
		} finally {
		    if (!committed) 
			try {
			    tm.rollback();
			} catch (SystemException ex) {
			    System.err.println("Error in roll back of transaction: " + ex);
			}
	    }
	   	
	   	committed = false;
	   	
		/*
	   	 * Teste 5 - Escrever o nomes e ids das folhas do utilizador pf
	   	 */
	   	
	   	try {
	   		tm.begin();
	   		BubbleDocsServer bd = BubbleDocsServer.getInstance();
		    populateDomain(bd);
		    
		    System.out.println("=================================================================");
	   		System.out.println("Teste 5 - Escrever o nomes e ids das folhas do utilizador pf");
	   		System.out.println("=================================================================");
	   		
	   		System.out.println("Sheets pf:");
	   		for (Sheet sheet : bd.getUserSheets("pf")) {
				System.out.println("Name:" + sheet.getName() +" ID:" + sheet.getId());
		    }
	   		tm.commit();
	   		committed = true;
	   	} catch (UserNotInSessionException |SystemException| NotSupportedException | RollbackException| HeuristicMixedException | HeuristicRollbackException ex) {
		    System.err.println("Error in execution of transaction: " + ex);
		} finally {
		    if (!committed) 
			try {
			    tm.rollback();
			} catch (SystemException ex) {
			    System.err.println("Error in roll back of transaction: " + ex);
			}
	    }
	   	
	   	committed = false;
	   	
	   	/*
	   	 * Teste 6 -importar folha para o pf que foi removida"
	   	 */
	   	
	   	try {
	   		tm.begin();
	   		BubbleDocsServer bd = BubbleDocsServer.getInstance();
		    populateDomain(bd);
		    
		    System.out.println("=================================================================");
	   		System.out.println("Teste 6 -importar folha para o pf que foi removida");
	   		System.out.println("=================================================================");
	   		
	   		
	   		try {
	   			LoginUser loginService = new LoginUser("pf", "sub");
	   			loginService.execute();
	   			String token = loginService.getUserToken();
	   			
	   			ImportDocumentIntegrator importService = new ImportDocumentIntegrator(token, fileID);
	   			importService.execute();
	   			
	   		}catch (SheetDoesntExistsException e) {
	   			System.err.println("Sheet does not exists");
			} catch (UnauthorizedOperationException e) {
				System.err.println("Unauthorized operation");
			}
	   		
	   		tm.commit();
	   		committed = true;
	   	} catch (UserNotInSessionException |SystemException| NotSupportedException | RollbackException| HeuristicMixedException | HeuristicRollbackException ex) {
		    System.err.println("Error in execution of transaction: " + ex);
		} finally {
		    if (!committed) 
			try {
			    tm.rollback();
			} catch (SystemException ex) {
			    System.err.println("Error in roll back of transaction: " + ex);
			}
	    }
	   	
	   	
	   	
	   	committed = false;
	   	
		/*
	   	 * Teste 7 - Escrever o nomes e ids das folhas do utilizador pf
	   	 */
	   	
	   	try {
	   		tm.begin();
	   		BubbleDocsServer bd = BubbleDocsServer.getInstance();
		    populateDomain(bd);
		    
		    System.out.println("=================================================================");
	   		System.out.println("Teste 7 - Escrever o nomes e ids das folhas do utilizador pf");
	   		System.out.println("=================================================================");
	   		
	   		System.out.println("Sheets pf:");
	   		for (Sheet sheet : bd.getUserSheets("pf")) {
				System.out.println("Name:" + sheet.getName() +" ID:" + sheet.getId());
		    }
	   		tm.commit();
	   		committed = true;
	   	} catch (UserNotInSessionException |SystemException| NotSupportedException | RollbackException| HeuristicMixedException | HeuristicRollbackException ex) {
		    System.err.println("Error in execution of transaction: " + ex);
		} finally {
		    if (!committed) 
			try {
			    tm.rollback();
			} catch (SystemException ex) {
			    System.err.println("Error in roll back of transaction: " + ex);
			}
	    }
	   	
	   	
		committed = false;
		
	   	/*
	   	 * Teste 8 - Exportar todas as folhas do utilizador pf para XML
	   	 */
	   	/*
	   	try {
	   		tm.begin();
	   		BubbleDocsServer bd = BubbleDocsServer.getInstance();
		    populateDomain(bd);
		   
		    System.out.println("=================================================================");
	   		System.out.println("Teste 8 - Exportar todas as folhas do utilizador pf para XML");
	   		System.out.println("=================================================================");
	   		
	   		System.out.println("Sheets pf:");
	   		for (Sheet sheet : bd.getUserSheets("pf")) {
					System.out.println("\n\nName:" + sheet.getName());
					filename = bd.exportToXML("pf", sheet.getId());
		    }
	   		tm.commit();
	   		committed = true;
	   	} catch (SystemException| NotSupportedException | RollbackException| HeuristicMixedException | HeuristicRollbackException ex) {
		    System.err.println("Error in execution of transaction: " + ex);
		} catch (SheetDoesntExistsException e) {
			  System.err.println("Sheet does not exists");
		} catch (UnauthorizedOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
		    if (!committed) 
			try {
			    tm.rollback();
			} catch (SystemException ex) {
			    System.err.println("Error in roll back of transaction: " + ex);
			}
	    }
	   	*/
	   	
	 }
	
	
	
	
	
    private static void populateDomain(BubbleDocsServer bd) throws UserDoesntExistsException, InvalidDataException, UserNotInSessionException {
		if (!(bd.getUsersSet().size() == 1))
		    return;
		try {
			
			//Logar Root
			LoginUser loginRoot = new LoginUser("root","rootroot");
			loginRoot.execute();
			String rootToken = loginRoot.getUserToken();
			
			//Criar PF
			CreateUser createPF = new CreateUser(rootToken, "pf","sub","Paul Door");
			createPF.execute();
			
			//Criar RA
			CreateUser createRA = new CreateUser(rootToken,"ra","cor","Step Rabbit");
			createRA.execute();
			
			//Logar PF
			LoginUser loginPF = new LoginUser("pf","sub");
			loginPF.execute();
			String pfToken = loginPF.getUserToken();
			
			//Logar RA
			LoginUser loginRA = new LoginUser("ra","cor");
			loginRA.execute();
			String raToken = loginRA.getUserToken();
			
			//Create sheet
			CreateSpreadSheet sheetService = new CreateSpreadSheet(pfToken,"Notas ES", 300, 20);
			sheetService.execute();
			int sheetID = sheetService.getSheetId();
			
			//add literal
			AssignLiteralCell lit1 = new AssignLiteralCell(pfToken, sheetID, "3;4", "5");
			lit1.execute();
			
			//add ref
			AssignReferenceCell ref1 = new AssignReferenceCell(pfToken, sheetID, "1;1", "5;6");
			ref1.execute();
			
			//functions not yet in services
			//Sheet sheet = bd.getSheetByID(sheetID);
			//User user = bd.getUserFromSession(pfToken);
			//bd.addSheetContent(user, sheetID, new ADD(new LiteralArg(2), new ReferenceArg(sheet.getCellByCoords(3,4))), 5,6);
			//bd.addSheetContent(user, sheetID, new DIV(new ReferenceArg(sheet.getCellByCoords(1,1)), new ReferenceArg(sheet.getCellByCoords(3,4))), 2,2);
			//bd.addSheetContent(user, sheetID, new DIV(new ReferenceArg(sheet.getCellByCoords(7,7)), new ReferenceArg(sheet.getCellByCoords(8,8))), 8,2);
			
			bd.logOut(rootToken);
			bd.logOut(pfToken);
			bd.logOut(raToken);
			
		} catch (UnauthorizedOperationException e1) {
			System.out.println("Unauthorized Operation.");
		} catch (UserAlreadyExistsException e2) {
			System.out.println("Username already in use");
		} catch (InvalidDataException e3) {
			System.out.println("Bad input.");
		} catch (UserDoesntExistsException e4) {
			System.out.println("User does not exists");
		}  catch (SheetDoesntExistsException e5) {
			System.out.println("Sheet does not exists");
		}
	}
}