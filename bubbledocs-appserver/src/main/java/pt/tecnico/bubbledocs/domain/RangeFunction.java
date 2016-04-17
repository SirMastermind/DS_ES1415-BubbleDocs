package pt.tecnico.bubbledocs.domain;

public abstract class RangeFunction extends RangeFunction_Base {
    
    public RangeFunction() {
        super();
    }
    
    public void delete(){
    	Range r = getArgs();
    	setArgs(null);
    	r.delete();
    	deleteDomainObject();
    }
    
}
