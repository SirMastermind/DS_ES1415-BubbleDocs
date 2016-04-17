package pt.tecnico.bubbledocs.integrator;

import pt.tecnico.bubbledocs.exception.BubbleDocsException;
import pt.tecnico.bubbledocs.service.local.AssignRangeCell;

public class AssignRangeCellIntegrator extends BubbleDocsIntegrator {

	private AssignRangeCell _service;
	
	public AssignRangeCellIntegrator(String tokenUser, int docId, String cellId, String function) {
		_service = new AssignRangeCell(tokenUser, docId, cellId, function);
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