package pt.tecnico.bubbledocs.service.local;

import pt.tecnico.bubbledocs.domain.Sheet;
import pt.tecnico.bubbledocs.dto.SheetContentDTO;
import pt.tecnico.bubbledocs.exception.BubbleDocsException;
import pt.tecnico.bubbledocs.exception.SheetDoesntExistsException;
import pt.tecnico.bubbledocs.exception.UnauthorizedOperationException;


public class GetSpreadSheetContent extends SessionBasedService {
	private int _sheetID;
	private String[][] _result;
	
	public GetSpreadSheetContent(String userToken, int sheetID) {
		super(userToken);
		_sheetID = sheetID;
		Sheet sheet = getBubbleDocsServer().getSheet(_sheetID);
		_result = new String[sheet.getRows()][sheet.getColumns()];
	}
	
	
	public int getsheetID() {
		return _sheetID;
	}
	
	@Override
	protected void dispatch() throws BubbleDocsException {
		Sheet sheet = getBubbleDocsServer().getSheet(_sheetID);
		int rows = sheet.getRows(), columns = sheet.getColumns();
		for(int i = 0; i < rows; i++) {
			for(int j = 0; j < columns; j++) {
				_result[i][j] = sheet.getValue(i+1, j+1);
			}
		}
	}

	@Override
	protected void checkData() throws BubbleDocsException {
		super.checkData();
    	
    	if(!getBubbleDocsServer().hasSheet(getsheetID()))
    		throw new SheetDoesntExistsException();
    	
    	if(!getUser().hasReadPermissions(getsheetID()))
    		throw new UnauthorizedOperationException();
	}

	public SheetContentDTO getResult() {
		return new SheetContentDTO(_result);
	}
}
