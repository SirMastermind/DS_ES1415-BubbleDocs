package pt.tecnico.bubbledocs.domain;

import pt.tecnico.bubbledocs.exception.NoValueException;
import pt.tecnico.bubbledocs.toolkit.Visitor;
import org.jdom2.Element;

public class ADD extends ADD_Base {
    public ADD() {
        super();
    }
    
    public ADD(Argument l, Argument r){
    	super();
    	setLeftValue(l);
    	setRightValue(r);
    }
    
    public int getValue() throws NoValueException {
    	return getLeftValue().getValue() + getRightValue().getValue();
    }
    
    public Element accept(Visitor v){
    	return v.processADD(this);
    }

}
