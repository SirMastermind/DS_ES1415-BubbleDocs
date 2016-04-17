package pt.tecnico.bubbledocs.domain;

import pt.tecnico.bubbledocs.exception.NoValueException;

public class Range extends Range_Base {
    
    public Range() {
        super();
    }
    public int getContentSum() throws NoValueException {
    	int result = 0;
    	for(Cell cell : getCellsSet()) {
    		result += cell.getValue();
    	}
    	return result;
    }
    public int getContentMul() throws NoValueException {
    	int result = 1;
    	for(Cell cell : getCellsSet()) {
    		result *= cell.getValue();
    	}
    	return result;
    }
	public void delete() {
		
		for(Cell cell : getCellsSet()) {
			cell.removeR(this);
  		}
		
		deleteDomainObject();
	}
}
