package pt.tecnico.bubbledocs.toolkit;

import org.jdom2.*;

import pt.tecnico.bubbledocs.domain.*;




public class XMLVisitor extends Visitor {
    
    
    @Override
    public  Element processCell(Cell c){
    	Element cellEl = new Element("cell");
    	cellEl.setAttribute(new Attribute("row", Integer.toString(c.getRow())));
		cellEl.setAttribute(new Attribute("column", Integer.toString(c.getColumn())));
		cellEl.setAttribute(new Attribute("protected", String.valueOf(c.getIsProtected())));
		Element contentEl = c.getContent().accept(this);
		if(contentEl == null) return null;
		
    	return cellEl.addContent(contentEl);
    }
    
    @Override
    public  Element processADD(ADD add){
    	Element addEl = new Element("content");
    	addEl.setAttribute(new Attribute("type", "ADD"));
    	addEl.addContent(add.getLeftValue().accept(this));
    	return addEl.addContent(add.getRightValue().accept(this));
    }
    
    @Override
    public  Element processAVG(AVG avg){
    	//TO DO
    	return new Element("content").setAttribute(new Attribute("type", "AVG"));
    }
    
    @Override
    public  Element processDIV(DIV div){
    	Element divEl = new Element("content").setAttribute(new Attribute("type", "DIV"));
    	divEl.addContent(div.getLeftValue().accept(this));
    	return divEl.addContent(div.getRightValue().accept(this));
    }
    
    @Override
    public  Element processMUL(MUL mul){
    	Element mulEl = new Element("content").setAttribute(new Attribute("type", "MUL"));
    	mulEl.addContent(mul.getLeftValue().accept(this));
    	return mulEl.addContent(mul.getRightValue().accept(this));
    }
    
    @Override
    public  Element processPRD(PRD prd){
    	return new Element("content").setAttribute(new Attribute("type", "PRD"));
    }
    
    @Override
    public  Element processSUB(SUB sub){
    	Element subEl = new Element("content").setAttribute(new Attribute("type", "SUB"));
    	subEl.addContent(sub.getLeftValue().accept(this));
    	return subEl.addContent(sub.getRightValue().accept(this));
    }
    
    @Override
    public  Element processReference(Reference ref){
    	Element refEl = new Element("content").setAttribute(new Attribute("type", "REF"));
    	refEl.setAttribute(new Attribute("row", Integer.toString(ref.getCell().getRow())));
    	refEl.setAttribute(new Attribute("column", Integer.toString(ref.getCell().getColumn())));
    	return refEl;
    }
    
    @Override
    public  Element processLiteral(Literal l){
    	Element litEl =new Element("content").setAttribute(new Attribute("value", Integer.toString(l.getValue())));
    	return litEl.setAttribute(new Attribute("type", "LIT"));
    }

	@Override
	public Element processNullContent(NullContent n) {
		return null;
	}
	
	@Override
	public Element processSheet(Sheet sheet, String newOwner){
		
		Element elemSheet = new Element("sheet");
		
		//Set sheet attributes
		elemSheet.setAttribute(new Attribute("name", sheet.getName()));
		elemSheet.setAttribute(new Attribute("owner", newOwner));
		elemSheet.setAttribute(new Attribute("rows", Integer.toString(sheet.getRows())));
		elemSheet.setAttribute(new Attribute("columns", Integer.toString(sheet.getColumns())));
		
	     //parse every cell with contents
		for(Cell cell : sheet.getCellsSet()){
			Element cellElm = cell.accept(this);
			if(cellElm == null) continue;
			elemSheet.addContent(cellElm);  
		}
		
		return elemSheet;
	}

	@Override
	public Element processReferenceArg(ReferenceArg ref) {
    	Element refEl = new Element("content").setAttribute(new Attribute("type", "REF-ARG"));
    	refEl.setAttribute(new Attribute("row", Integer.toString(ref.getCell().getRow())));
    	refEl.setAttribute(new Attribute("column", Integer.toString(ref.getCell().getColumn())));
    	return refEl;
	}

	@Override
	public Element processLiteralArg(LiteralArg l) {
		Element litEl =new Element("content").setAttribute(new Attribute("value", Integer.toString(l.getValue())));
    	return litEl.setAttribute(new Attribute("type", "LIT-ARG"));
	}
}
