package pt.tecnico.bubbledocs.domain;
import pt.tecnico.bubbledocs.toolkit.Visitor;
import org.jdom2.Element;

public class Literal extends Literal_Base {
    
  
    public Literal(int value){
    	super();
    	setValue(value);
    }
    
    public void delete(){
    	//DO NOTHING
    }
    
    public Element accept(Visitor v){
    	return v.processLiteral(this);
    }
    
}
