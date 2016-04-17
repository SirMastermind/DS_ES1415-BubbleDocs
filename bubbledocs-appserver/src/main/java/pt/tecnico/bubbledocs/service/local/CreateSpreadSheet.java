package pt.tecnico.bubbledocs.service.local;

import pt.tecnico.bubbledocs.domain.BubbleDocsServer;
import pt.tecnico.bubbledocs.domain.Sheet;
import pt.tecnico.bubbledocs.domain.User;
import pt.tecnico.bubbledocs.exception.*;
import pt.tecnico.bubbledocs.toolkit.DataValidator;

public class CreateSpreadSheet extends SessionBasedService {
    
	private int sheetId;  
    private String name;
    private int rows;
    private int columns;
    private Sheet result;


    public CreateSpreadSheet(String userToken, String name, int rows, int columns) {
    	super(userToken);
    	this.name = name;
    	this.rows = rows;
    	this.columns = columns;
    }
    
    
    public int getSheetId() {
        return sheetId;
    }
    
    public String getName(){
    	return name;
    }
	public Sheet getResult() {
        return result;
    }
	
	public int getRows(){
		return rows;
	}
	
	public int getColumns(){
		return columns;
	}
	
	private void setResult(Sheet sheet){
		this.result = sheet;
	}
	
	@Override
	protected void checkData(){
		super.checkData();
		
		if(!DataValidator.validSheetName(getName()))
			throw new InvalidSheetNameException(getName());
		
		if(!DataValidator.validSheetDimensions(getRows(), getColumns()))
			throw new InvalidSheetDimensionsException();
	}

    @Override
    protected void dispatch() throws BubbleDocsException {
    	 	
    	BubbleDocsServer bd = getBubbleDocsServer();
    	User owner = getUser();
    	
    	//create sheet
    	Sheet sheet = new Sheet(getName(), bd.getSheetNewID()+1, owner, getRows(), getColumns()); 
    	bd.addSheets(sheet);
	    owner.addOwnedSheets(sheet); //add permissions to sheet owner
    	setResult(sheet);	
    }

}
