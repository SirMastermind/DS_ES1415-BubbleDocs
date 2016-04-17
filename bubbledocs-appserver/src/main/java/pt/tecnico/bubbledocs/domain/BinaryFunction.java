package pt.tecnico.bubbledocs.domain;

public abstract class BinaryFunction extends BinaryFunction_Base {
	
    public BinaryFunction() {
        super();
    }
    
    public void delete(){
    	Argument c1 = getLeftValue();
    	Argument c2 = getRightValue();
    	setLeftValue(null);
    	setRightValue(null);
    	c1.delete();
    	c2.delete();
    	deleteDomainObject();
    }
}
