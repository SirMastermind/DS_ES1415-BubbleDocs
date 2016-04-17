package pt.tecnico.bubbledocs.toolkit;

public final class DataValidator {
	

	
	public  static boolean validEmail(String email){
		return validString(email) 
				&& email.matches("^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$");
	}
	
	
	public static boolean validCoordinates(String coordinates){
		return validString(coordinates) && coordinates.matches("[0-9]+;[0-9]+");
	}
	
	public static boolean validSheetName(String sheetName){
		return validString(sheetName) && !sheetName.matches("[a-zA-Z_ ]");
	}
	
	public static boolean validBinFunctionString(String funcInfo){
		return validString(funcInfo) 
				&& funcInfo.matches("[A-Za-z]{3}\\(([0-9]+|[0-9]+;[0-9]+),([0-9]+|[0-9]+;[0-9]+)\\)");
	}
	
	public static boolean validRangeFunctionString(String funcInfo){
		return validString(funcInfo) 
				&& funcInfo.matches("[A-Za-z]{3}\\(([0-9]+|[0-9]+;[0-9]+):([0-9]+|[0-9]+;[0-9]+)\\)");
	}
	
	public static boolean validLiteral(String literal){
		return validString(literal) && literal.matches("[0-9]+");
	}
	
	public static boolean validSheetDimensions(int rows, int columns){
		return rows > 0 && columns >0;
	}
	
	public static boolean validString(String str){
		return str != null && str != "";
	}
}
