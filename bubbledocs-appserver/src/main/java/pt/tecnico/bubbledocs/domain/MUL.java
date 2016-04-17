package pt.tecnico.bubbledocs.domain;

import pt.tecnico.bubbledocs.exception.NoValueException;
import org.jdom2.Element;
import pt.tecnico.bubbledocs.toolkit.Visitor;

public class MUL extends MUL_Base {
    
    public MUL() {
        super();
    }
    
    public MUL(Argument l, Argument r){
    	super();
    	setLeftValue(l);
    	setRightValue(r);
    }
    
    public int getValue() throws NoValueException {
    	return getLeftValue().getValue() * getRightValue().getValue();
    }
    
    public Element accept(Visitor v){
    	return v.processMUL(this);
    }
}
