package pt.tecnico.bubbledocs.domain;

import pt.tecnico.bubbledocs.exception.NoValueException;
import pt.tecnico.bubbledocs.toolkit.Visitor;
import org.jdom2.Element;

public abstract class Content extends Content_Base {
    
    public Content() {
        super();
    }
    public abstract int getValue() throws NoValueException;
	public abstract void delete();
	public abstract Element accept(Visitor v);
}
