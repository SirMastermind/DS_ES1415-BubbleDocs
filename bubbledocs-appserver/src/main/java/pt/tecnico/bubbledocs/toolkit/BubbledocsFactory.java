package pt.tecnico.bubbledocs.toolkit;

import pt.ist.fenixframework.FenixFramework;
import pt.tecnico.bubbledocs.domain.*;
import pt.tecnico.bubbledocs.exception.InvalidCellCoordinatesException;
import pt.tecnico.bubbledocs.exception.InvalidDataException;
import pt.tecnico.bubbledocs.exception.InvalidLiteralException;
import pt.tecnico.bubbledocs.exception.SheetDoesntExistsException;
import pt.tecnico.bubbledocs.toolkit.DataValidator;
import pt.tecnico.bubbledocs.domain.BubbleDocsServer;

public final class BubbledocsFactory {
	
	
	/*
	 *  PUBLIC METHODS
	 */
	
	
	/* method that parses the given string and creates the respective function object*/

	
	public static int[] parseCoordinates(String coordinates){
		
		if(!DataValidator.validCoordinates(coordinates)) 
			throw new InvalidCellCoordinatesException(coordinates);
		
		int[] result = new int[2];
				
		String[] cell = coordinates.split(";");
		result[0] = Integer.parseInt(cell[0]);
    	result[1] = Integer.parseInt(cell[1]);
    	
    	return result;
		
	}
	
	/*
	 * Content creators
	 */
	
	public static Literal createLiteral(String literal){
		
		if(!DataValidator.validLiteral(literal))
			throw new InvalidLiteralException(literal);
		
		return new Literal(Integer.parseInt(literal));
	}

	public static Reference createReference(int sheetId, String reference) {
		
		if(!DataValidator.validCoordinates(reference))
			throw new InvalidCellCoordinatesException(reference);
		
		Sheet sheet = getSheetById(sheetId);
		int[] refCoords = parseCoordinates(reference);
		Cell refCell = sheet.getCellByCoords(refCoords[0], refCoords[1]);
		
		return new Reference(refCell);
	}
	
	public static Function createFunction(String functionInfo, int sheetId){

		String funcName = functionInfo.substring(0, 3);
		String funcArgs = functionInfo.substring(functionInfo.indexOf("(")+1,functionInfo.indexOf(")"));

		if(funcArgs.contains(":")) {
			if(!DataValidator.validRangeFunctionString(functionInfo)) {
				throw new InvalidDataException();
			}
			else return createRangeFunction(funcName, funcArgs, sheetId);
		}

		if(!DataValidator.validBinFunctionString(functionInfo))
			throw new InvalidDataException();
		return createBinaryFunction(funcName, funcArgs, sheetId);
		}
	
	/*
	 * PRIVATE METHODS
	 */
	
	private static BubbleDocsServer getBubbleDocsServer(){
		return FenixFramework.getDomainRoot().getBubbleDocsServer(); 
	}
	
	
	private static Sheet getSheetById(int sheetId){
		Sheet sheet = getBubbleDocsServer().getSheet(sheetId);
		if (sheet == null) throw new SheetDoesntExistsException();
		return sheet;
	}
	
	private static Function createRangeFunction(String funcName, String funcArgs, int sheetId) {
		
		Range rangeArg = createRange(funcArgs, sheetId);
		
		switch (funcName) {
	        case "AVG":
	        	return new AVG(rangeArg);
	        case "PRD":
	        	return new PRD(rangeArg);
	        default:
	        	throw new InvalidDataException();
		}
		
	}

	private static Function createBinaryFunction(String funcName, String funcArgs, int sheetId) {
		
		String   argsArray[] = funcArgs.split(",");
		Argument left = createArgument(argsArray[0], sheetId);
		Argument right = createArgument(argsArray[1], sheetId);
		
		switch (funcName) {
	        case "ADD":
	        	return new ADD(left, right);
	        case "MUL":
	        	return new MUL(left, right);
	        case "DIV":
	        	return new DIV(left, right);
	        case "SUB":
	        	return new SUB(left, right);
	        default:
	        	throw new InvalidDataException();
		}
	}
		
	private static Argument createArgument(String argum, int sheetId) {
		
		Sheet sheet = getSheetById(sheetId);
		
		if(argum.contains(";")){
			int[] cell = parseCoordinates(argum);
			return new ReferenceArg(sheet.getCellByCoords(cell[0], cell[1]));
		}
		else
			return new LiteralArg(Integer.parseInt(argum));
	}
	
	private static Range createRange(String funcArgs, int sheetId) {
		
		int[] startcell;
		int[] endcell;
		
		String[] cell = funcArgs.split(":");
		startcell  = parseCoordinates(cell[0]);
		endcell = parseCoordinates(cell[1]);
		
		Sheet sheet = getSheetById(sheetId);
		Range range = new Range();
		
		if(!sheet.validCoords(startcell[0], startcell[1]) || !sheet.validCoords(endcell[0], endcell[1])) {
			throw new InvalidCellCoordinatesException(funcArgs);
		}
		
		for(int r=startcell[0]; r<=endcell[0]; r++){		//over row
			for(int c=startcell[1]; c<=endcell[1]; c++){		//over column
				range.addCells(sheet.getCellByCoords(r, c));	
			}	
		}
		return range;
	}

}