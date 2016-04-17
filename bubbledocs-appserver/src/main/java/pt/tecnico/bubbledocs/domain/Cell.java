package pt.tecnico.bubbledocs.domain;

import pt.tecnico.bubbledocs.exception.NoValueException;
import pt.tecnico.bubbledocs.toolkit.Visitor;
import pt.tecnico.bubbledocs.exception.UnauthorizedOperationException;

import org.jdom2.Element;

public class Cell extends Cell_Base {
    
    
    public Cell(int row, int column, Content content) {
    	setRow(row);
		setColumn(column);
		setContent(content);
		setIsProtected(false);
	}
    
    public Cell(int row, int column) {
    	setRow(row);
		setColumn(column);
		setContent(new NullContent());
		setIsProtected(false);
	}

	public int getValue() throws NoValueException {
    		return this.getContent().getValue();
    }
    
    public void changeContent(Content c) throws UnauthorizedOperationException {
    	if (this.getIsProtected()) {
    		throw new UnauthorizedOperationException();
    	}
    	
    	if(c == null){
    		this.setContent(new NullContent());
    	}
    	else{
    		this.setContent(c);
    	}
    	
    }
    
    
    
	public void delete() {
		Content c = getContent();
		setContent(null);
		c.delete();
		
		for(Reference ref : getR2Set()){
			ref.setCell(null);  //remove refs
		}
		
		for(Range range : getRSet()){
			range.removeCells(this); //remove refs from range objetcts
		}
		
		for(ReferenceArg ref : getRefArgSet()){
			ref.setCell(null);
		}
		
		deleteDomainObject();
	}
	
	public Element accept(Visitor v){
		return v.processCell(this);
	}
}
