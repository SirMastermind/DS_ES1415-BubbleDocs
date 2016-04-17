package pt.tecnico.bubbledocs.domain;

import org.jdom2.Element;

import pt.tecnico.bubbledocs.toolkit.Visitor;

public class LiteralArg extends LiteralArg_Base {
    
	 public LiteralArg(int value){
	    	super();
	    	setValue(value);
	    }
	    
	    public void delete(){
	    	//DO NOTHING
	    }
	    
	    public Element accept(Visitor v){
	    	return v.processLiteralArg(this);
	    }
    
}
