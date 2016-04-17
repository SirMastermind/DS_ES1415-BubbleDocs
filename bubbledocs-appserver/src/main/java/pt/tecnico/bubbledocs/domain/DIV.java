package pt.tecnico.bubbledocs.domain;

import pt.tecnico.bubbledocs.exception.NoValueException;
import pt.tecnico.bubbledocs.toolkit.Visitor;
import org.jdom2.Element;

public class DIV extends DIV_Base {
    
    public DIV() {
        super();
    }
    
    public DIV(Argument l, Argument r){
    	super();
    	setLeftValue(l);
    	setRightValue(r);
    }
    
    public int getValue() throws NoValueException {
    	return getLeftValue().getValue() / getRightValue().getValue();
    }
    
    public Element accept(Visitor v){
    	return v.processDIV(this);
    }
}
