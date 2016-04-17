package pt.tecnico.bubbledocs.domain;

import pt.tecnico.bubbledocs.exception.NoValueException;
import pt.tecnico.bubbledocs.toolkit.Visitor;
import org.jdom2.Element;

public class PRD extends PRD_Base {
    
    public PRD(Range r) {
        super();
        setArgs(r);
    }
    
    
    
	public int getValue() throws NoValueException {
		Range g = getArgs();
    	return g.getContentMul();
    }
	
	public Element accept(Visitor v){
		return v.processPRD(this);
	}
	

	
}
