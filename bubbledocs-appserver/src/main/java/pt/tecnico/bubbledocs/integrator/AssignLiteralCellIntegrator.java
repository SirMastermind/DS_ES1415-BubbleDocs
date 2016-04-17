package pt.tecnico.bubbledocs.integrator;

import pt.tecnico.bubbledocs.exception.BubbleDocsException;
import pt.tecnico.bubbledocs.service.local.AssignLiteralCell;


public class AssignLiteralCellIntegrator extends BubbleDocsIntegrator {
	private AssignLiteralCell _localservice;
	
	public AssignLiteralCellIntegrator(String tokenUser, int docId, String cellId, String literal) {
		_localservice = new AssignLiteralCell(tokenUser, docId, cellId, literal);
	}
	
	@Override
	protected void dispatch() throws BubbleDocsException {
		_localservice.execute();
	}

	public String getResult() {
		return _localservice.getResult();
	}
	
	public String getLiteral() {
		return _localservice.getLiteral();
	}
}
