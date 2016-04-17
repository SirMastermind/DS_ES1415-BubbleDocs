package pt.tecnico.bubbledocs.service.local;

import pt.tecnico.bubbledocs.domain.BubbleDocsServer;
import pt.tecnico.bubbledocs.domain.Content;
import pt.tecnico.bubbledocs.domain.Sheet;
import pt.tecnico.bubbledocs.exception.InvalidCellCoordinatesException;
import pt.tecnico.bubbledocs.exception.SheetDoesntExistsException;
import pt.tecnico.bubbledocs.exception.UnauthorizedOperationException;
import pt.tecnico.bubbledocs.toolkit.BubbledocsFactory;
import pt.tecnico.bubbledocs.toolkit.DataValidator;

public abstract class ContentModificationService extends SessionBasedService {
	
	private int docID;
	private String cellID;
	private Content content;
	private String result;
	
	protected ContentModificationService(String userToken, int docId, String cellId) {
		super(userToken);
		this.docID = docId;
		this.cellID = cellId;
	}
	
	protected int getDocID(){
		return docID;
	}
	
	protected String getCellID(){
		return cellID;
	}
	
	protected Content getContent(){
		return content;
	}
	
	protected void setContent(Content content){
		this.content = content;
	}
	
	public String getResult(){
		return result;
	}
	
	private void setResult(String result){
		this.result = result;
	}
	
	//sub classes must create their own contents
	protected abstract void createContent();
	
	@Override
	protected void checkData(){
		super.checkData();
    	
    	if(!DataValidator.validCoordinates(getCellID()))
    		throw new InvalidCellCoordinatesException(getCellID());
    	
    	if(!getBubbleDocsServer().hasSheet(getDocID()))
    		throw new SheetDoesntExistsException();
    	
    	if(!getUser().hasWritePermissions(getDocID()))
    		throw new UnauthorizedOperationException(); 
    		
	}
	
	@Override
	protected void dispatch(){
		BubbleDocsServer bd = getBubbleDocsServer();
		int[] cell = BubbledocsFactory.parseCoordinates(getCellID());	

		createContent(); //template method
    	Sheet sheet = bd.getSheet(getDocID());
    	sheet.changeCellContent(getContent(), cell[0], cell[1]);
    	setResult(sheet.getValue(cell[0], cell[1]));
	}
	
}
