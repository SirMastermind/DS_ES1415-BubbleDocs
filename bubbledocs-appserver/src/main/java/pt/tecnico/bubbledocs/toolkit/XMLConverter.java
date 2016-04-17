package pt.tecnico.bubbledocs.toolkit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.jdom2.Element;
import org.jdom2.Document;  
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;
import org.jdom2.output.Format;

import pt.tecnico.bubbledocs.domain.*;




public final class XMLConverter {
	
    //print output to the terminal and create file
    private static void saveXMLFile(Document document, String name) throws FileNotFoundException, IOException{
		XMLOutputter xmlOutput = new XMLOutputter();
		xmlOutput.setFormat(Format.getPrettyFormat());
		xmlOutput.output(document,new FileOutputStream(name+".xml"));
		xmlOutput.output(document,System.out);
		System.out.println(name+".xml Saved!");
    }
    
    private static byte[] outputToByteArray(Document document) 
    		throws FileNotFoundException, IOException{
    	
    	ByteArrayOutputStream data = new ByteArrayOutputStream();
		XMLOutputter xmlOutput = new XMLOutputter();
		xmlOutput.output(document, data);
		return data.toByteArray();
    }
    
    
    /*
     * PUBLIC METHODS
     */
    
    public static byte[] convertToXML(Sheet sheet, String username) 
    		throws FileNotFoundException, IOException{
    	
    		XMLVisitor v = new XMLVisitor();
    		Document document = new Document(sheet.accept(v,username));
    		saveXMLFile(document, sheet.getName()+sheet.getId());
    		return outputToByteArray(document);
    }
    

    
    public static Sheet convertToSheet(byte[] file) throws JDOMException, IOException {
    		
    		
    		BubbleDocsServer bd = BubbleDocsServer.getInstance();
    		int row, column, rows, columns;
    		Content content;
    		SAXBuilder saxBuilder = new SAXBuilder();  
    		String owner, name;
    		boolean cellProtected;
    		
    		Document document = saxBuilder.build(new ByteArrayInputStream(file));
    		Element sheetElm = document.getRootElement();
    		
    		//get sheet attributes
    		rows = Integer.parseInt(sheetElm.getAttributeValue("rows"));
    		columns = Integer.parseInt(sheetElm.getAttributeValue("columns"));
    		owner = sheetElm.getAttributeValue("owner");
    		name = sheetElm.getAttributeValue("name");
    		
    		//create sheet object
    		User ownerUser = bd.getUser(owner);
    		Sheet sheet = new Sheet(name, bd.getSheetNewID()+1, ownerUser, rows, columns);
    		
    		for(Element el: sheetElm.getChildren("cell")) {
    			row = Integer.parseInt(el.getAttributeValue("row"));
    			column = Integer.parseInt(el.getAttributeValue("column"));
    			cellProtected = Boolean.parseBoolean(el.getAttributeValue("protected"));
    			content = produceContent(el.getChildren(), sheet);
    			sheet.changeCellContent(content, row, column);
    			sheet.getCellByCoords(row,column).setIsProtected(cellProtected); 
    		}

    		return sheet;
    }
    
    public static Content produceContent(List<Element> children, Sheet sheet) {
		Element content = children.get(0);
		Argument arg1, arg2;
		int r,c;
		
		switch (content.getAttributeValue("type")) {
		  case "ADD":
			  	arg1 = produceArgs(content.getChildren().get(0), sheet);
			  	arg2 = produceArgs(content.getChildren().get(1), sheet);
			  	return new ADD(arg1, arg2);
		  case "AVG": 
		        return new AVG(null);  //TO DO
		  case "DIV":
			  	arg1 = produceArgs(content.getChildren().get(0), sheet);
			  	arg2 = produceArgs(content.getChildren().get(1), sheet);
			  	return new DIV(arg1, arg2);
		  case "LIT": 
		        return new Literal(Integer.parseInt(content.getAttributeValue("value")));
		  case "MUL":
			  	arg1 = produceArgs(content.getChildren().get(0), sheet);
			  	arg2 = produceArgs(content.getChildren().get(1), sheet);
			  	return new MUL(arg1, arg2);
		  case "PRD": 
			  	return new PRD(null); //TO DO
		  case "SUB":
			  	arg1 = produceArgs(content.getChildren().get(0), sheet);
			  	arg2 = produceArgs(content.getChildren().get(1), sheet);
			  	return new SUB(arg1, arg2);
		  case "REF":
			  	r = Integer.parseInt(content.getAttributeValue("row"));
			  	c = Integer.parseInt(content.getAttributeValue("column"));
			  	return new Reference(sheet.getCellByCoords(r,c));    
		}
		
		return null;
		
    }
    
    private static Argument produceArgs(Element content, Sheet sheet){
    	
    	int r,c;
    	
    	switch (content.getAttributeValue("type")) {
		 
		  case "LIT-ARG": 
		        return new LiteralArg(Integer.parseInt(content.getAttributeValue("value")));
		  case "REF-ARG": 
			r = Integer.parseInt(content.getAttributeValue("row"));
			c = Integer.parseInt(content.getAttributeValue("column"));
			return new ReferenceArg(sheet.getCellByCoords(r,c));
			      
		}
    	
    	return null;
    	
    }
    



}
