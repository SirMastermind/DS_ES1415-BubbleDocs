package pt.tecnico.bubbledocs.domain;

import org.jdom2.Element;

import pt.tecnico.bubbledocs.exception.NoValueException;
import pt.tecnico.bubbledocs.toolkit.Visitor;

public abstract class Argument extends Argument_Base {
    
    public Argument() {
        super();
    }
    
    public abstract int getValue() throws NoValueException;
	public abstract void delete();
	public abstract Element accept(Visitor v);
    
}
