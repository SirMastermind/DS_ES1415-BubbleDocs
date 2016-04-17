package pt.tecnico.bubbledocs.domain;

import org.jdom2.Element;

import pt.tecnico.bubbledocs.exception.NoValueException;
import pt.tecnico.bubbledocs.toolkit.Visitor;

public class NullContent extends NullContent_Base {
    
    public NullContent() {
        super();
    }

	@Override
	public int getValue() throws NoValueException {
		throw new NoValueException();
	}

	@Override
	public Element accept(Visitor v) {
		return v.processNullContent(this);
	}

	@Override
	public void delete() {
		deleteDomainObject();
	}
    
}
