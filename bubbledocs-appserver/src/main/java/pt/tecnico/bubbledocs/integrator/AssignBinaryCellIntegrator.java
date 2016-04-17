package pt.tecnico.bubbledocs.integrator;

import pt.tecnico.bubbledocs.exception.BubbleDocsException;
import pt.tecnico.bubbledocs.service.local.AssignBinaryCell;

public class AssignBinaryCellIntegrator extends BubbleDocsIntegrator {

	private AssignBinaryCell _service;
	
	public AssignBinaryCellIntegrator(String tokenUser, int docId, String cellId, String function) {
		_service = new AssignBinaryCell(tokenUser, docId, cellId, function);
	}
	
	@Override
	protected void dispatch() throws BubbleDocsException {
		_service.execute();
	}

	public String getResult() {
		return _service.getResult();
	}
	
	public String getFunction() {
		return _service.getFunction();
	}
}