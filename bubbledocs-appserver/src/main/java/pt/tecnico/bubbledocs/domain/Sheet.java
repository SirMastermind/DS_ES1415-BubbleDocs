package pt.tecnico.bubbledocs.domain;

import org.joda.time.*;

import pt.tecnico.bubbledocs.toolkit.Visitor;

import org.jdom2.Element;

import pt.tecnico.bubbledocs.exception.*;


public class Sheet extends Sheet_Base {
	
	
    public Sheet(String name, int sheetID, User owner, int rowns, int columns) {
        super();
        	   	
        this.setName(name);
		this.setCreationDate(new LocalDate());
		this.setId(sheetID);
		this.setOwner(owner);
		this.setRows(rowns);
		this.setColumns(columns);
    }
	
	public void delete() {
		for(Cell cell : getCellsSet()) {
			removeCells(cell);
			cell.delete();
  		}
		for(User user : getReadUserSet()){
			user.removePermissions(this);
		}
		for(User user : getWriteUserSet()){
			user.removePermissions(this);
		}
		
		getOwner().removeOwnedSheets(this);
		getBd4().removeSheets(this);
		deleteDomainObject();
		
	}
	
	public boolean validCoords(int r, int c){
		return getRows() >= r && r >= 1 && getColumns() >= c && c >= 1;
	}
	

    public Cell getCellByCoords(int r, int c) throws InvalidDataException {
    	if(!validCoords(r,c)){
			throw new InvalidCellCoordinatesException(r+";"+c);
		}
    	
    	for(Cell cell : getCellsSet()) {
  		    if(cell.getRow() == r && cell.getColumn() == c) {
  		    	return cell;
  			}
  		}
  		
  		Cell cll = new Cell(r,c);
  		addCells(cll);
  		return cll;
  		
  	}
	
	public boolean hasCell(int r, int c) throws InvalidDataException{
		if(!validCoords(r,c)){
			throw new InvalidCellCoordinatesException(r+";"+c);
		}
		
		for(Cell cell : getCellsSet()) {
  		    if(cell.getRow() == r && cell.getColumn() == c) {
  		    	return true;
  			}
  		}
		
		return false;
	}
	
	public void addCell(Cell c) throws InvalidDataException{
		//se a celula ja existir substituimos
		if(hasCell(c.getRow(), c.getColumn())){
			removeCells(getCellByCoords(c.getRow(), c.getColumn()));
		}
		
		addCells(c);
	}
	
	

	public void changeCellContent(Content c, int row, int column) 
			throws UnauthorizedOperationException, InvalidDataException{
		
		if(!hasCell(row,column)){
			addCells(new Cell(row,column,c));
		}
		else{
			getCellByCoords(row, column).changeContent(c);
		}
	}
	
	public Element accept(Visitor v, String newOwner){
		return v.processSheet(this, newOwner);
	}
	
	public void setProtected(boolean bool, int r, int c){
		getCellByCoords(r,c).setIsProtected(bool);
	}
	
	public String getValue(int row, int column){
		
		String result;
		
		try{
			result =  Integer.toString(getCellByCoords(row,column).getValue());
		}
		catch(NoValueException e){
			result = "#VALUE";
		}
		
		return result;
		
	}
	
	
	
    
}
