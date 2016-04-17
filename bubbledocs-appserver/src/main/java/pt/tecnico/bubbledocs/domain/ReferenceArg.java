package pt.tecnico.bubbledocs.domain;

import org.jdom2.Element;

import pt.tecnico.bubbledocs.exception.NoValueException;
import pt.tecnico.bubbledocs.toolkit.Visitor;

public class ReferenceArg extends ReferenceArg_Base {
    
    public ReferenceArg(Cell c) {
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
    	return v.processReferenceArg(this);
    }
    
}
