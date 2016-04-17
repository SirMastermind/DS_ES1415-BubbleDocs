package pt.tecnico.bubbledocs.service.local;

import pt.tecnico.bubbledocs.exception.*;
import pt.tecnico.bubbledocs.toolkit.BubbledocsFactory;
import pt.tecnico.bubbledocs.toolkit.DataValidator;

// add needed import declarations

public class AssignLiteralCell extends ContentModificationService {
    
    private String _literal;
    
    public AssignLiteralCell(String userToken, int docId, String cellId, String literal) {
    	super(userToken, docId, cellId);
    	_literal = literal;
    }

	public String getLiteral() {
		return _literal;
	}
    
    
    @Override
    protected void checkData(){		
    	super.checkData();
    	if(!DataValidator.validLiteral(getLiteral()))
    		throw new InvalidLiteralException(getLiteral());    	
    }
    
	@Override
	protected void createContent() {
		setContent(BubbledocsFactory.createLiteral(getLiteral()));		
	}
    
}
