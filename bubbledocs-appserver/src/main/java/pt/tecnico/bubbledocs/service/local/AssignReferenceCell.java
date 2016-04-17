package pt.tecnico.bubbledocs.service.local;

import pt.tecnico.bubbledocs.exception.*;
import pt.tecnico.bubbledocs.toolkit.BubbledocsFactory;
import pt.tecnico.bubbledocs.toolkit.DataValidator;

public class AssignReferenceCell extends ContentModificationService {

    private String _reference;

    public AssignReferenceCell(String tokenUser, int docId, String cellId, String reference) {
    	super(tokenUser, docId, cellId);
    	_reference = reference;
    }
    
    public String getReference(){
    	return _reference;
    }
    
    @Override
    protected void checkData(){
    	super.checkData();
    	
    	if(!DataValidator.validCoordinates(getReference()))
    		throw new InvalidCellCoordinatesException(getReference());
    }
    
    @Override
    protected void createContent() {
    	setContent(BubbledocsFactory.createReference(getDocID(), getReference()));
    }


    
    
}
