package pt.tecnico.bubbledocs.exception;

public abstract class BubbleDocsException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2949154280446264002L;
	
	public BubbleDocsException(){
		
	}

	public BubbleDocsException(String msg){ 
		super(msg);
	}

}