package pt.tecnico.bubbledocs.domain;

import pt.tecnico.bubbledocs.exception.NoValueException;
import pt.tecnico.bubbledocs.toolkit.Visitor;

import org.jdom2.Element;

public class AVG extends AVG_Base {
    
    public AVG(Range r) {
        super();
        setArgs(r);
    }
    
	@SuppressWarnings("deprecation")
	public int getValue() throws NoValueException {
    	Range g = getArgs();
    	return g.getContentSum() / g.getCellsCount();
    }
    
    public Element accept(Visitor v){
    	return v.processAVG(this);
    }
}
