package pt.tecnico.bubbledocs.integrator;

import pt.tecnico.bubbledocs.domain.Sheet;
import pt.tecnico.bubbledocs.exception.BubbleDocsException;
import pt.tecnico.bubbledocs.service.local.CreateSpreadSheet;

public class CreateSpreadSheetIntegrator extends BubbleDocsIntegrator {

	CreateSpreadSheet local;
    
    
    public CreateSpreadSheetIntegrator(String userToken, String name, int rows, int columns){
    	local = new CreateSpreadSheet(userToken, name, rows, columns);
    }
	@Override
	protected void dispatch() throws BubbleDocsException {
			local.execute();
	}
	
	 public int getSheetId() {
		 return local.getSheetId();
	 }
	    
	 public String getName(){
	   	return local.getName();
	 }
	 public Sheet getResult() {
		 return local.getResult();
	 }
		
	 public int getRows(){
		 return local.getRows();
	 }
		
	 public int getColumns(){
		 return local.getColumns();
	 }

}
