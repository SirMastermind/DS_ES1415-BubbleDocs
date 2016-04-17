package pt.tecnico.bubbledocs.service.local;

import pt.tecnico.bubbledocs.exception.BubbleDocsException;
import pt.tecnico.bubbledocs.exception.InvalidDataException;
import pt.tecnico.bubbledocs.toolkit.BubbledocsFactory;
import pt.tecnico.bubbledocs.toolkit.DataValidator;

public class AssignRangeCell extends ContentModificationService {

	private String _function;
	
	public AssignRangeCell(String userToken, int docId, String cellId, String function) {
		super(userToken, docId, cellId);
		_function = function;
	}

	public String getFunction() {
		return _function;
	}
	
	@Override
	protected void checkData() throws BubbleDocsException {
    	super.checkData();
    	if(!DataValidator.validRangeFunctionString(getFunction()))
    		throw new InvalidDataException(); 
	}

	@Override
	protected void createContent() {
		setContent(BubbledocsFactory.createFunction(getFunction(), super.getDocID()));	
	}
}
