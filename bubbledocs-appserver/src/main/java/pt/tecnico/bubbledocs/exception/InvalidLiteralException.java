package pt.tecnico.bubbledocs.exception;

public class InvalidLiteralException extends BubbleDocsException{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5022423797843290515L;

	public InvalidLiteralException(String literal) {
		super(literal);
	}
}
