package pt.tecnico.bubbledocs.domain;

import pt.tecnico.bubbledocs.exception.NoValueException;
import pt.tecnico.bubbledocs.toolkit.Visitor;
import org.jdom2.Element;

public class Reference extends Reference_Base {
    
	
    public Reference(Cell c){
    	super();
    	setCell(c);
    }
    
    public int getValue() throws NoValueException {
    	return this.getCell().getValue();
    }
    
    public void delete(){
    	setCell(null);
    }
    
    public Element accept (Visitor v){
    	return v.processReference(this);
    }
}