package pt.tecnico.bubbledocs.service.local;

import java.io.FileNotFoundException;
import java.io.IOException;

import pt.tecnico.bubbledocs.domain.Sheet;
import pt.tecnico.bubbledocs.exception.*;
import pt.tecnico.bubbledocs.toolkit.XMLConverter;

public class ExportDocument extends SessionBasedService {
    private byte[] docXML;
    private int docId;
	private String sheetName;
    private String filename;

    public ExportDocument(String userToken, int docId) {
    	super(userToken);
    	this.docId = docId;
    }
    
    public byte[] getDocXML() {
    	return docXML;
    }
    
    public int getDocID() {
    	return docId;
    }
    
    public String getName(){
    	return sheetName;
    }
    
    public String getFilename(){
    	return filename;
    }
    
    private void setFilename(String filename){
    	this.filename = filename;
    }
    
    
	@Override
	protected void checkData(){
		super.checkData();
    	
    	if(!getBubbleDocsServer().hasSheet(getDocID()))
    		throw new SheetDoesntExistsException();
    	
    	if(!getUser().hasReadPermissions(getDocID()))
    		throw new UnauthorizedOperationException(); 
    		
	} 
	
	@Override
    protected void dispatch() throws BubbleDocsException {
    	
    	try {
			
    		Sheet sheet = getBubbleDocsServer().getSheet(getDocID());
    		this.docXML = XMLConverter.convertToXML(sheet, getUser().getUsername());
			setFilename(sheet.getName()+sheet.getId()+".xml");
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
}