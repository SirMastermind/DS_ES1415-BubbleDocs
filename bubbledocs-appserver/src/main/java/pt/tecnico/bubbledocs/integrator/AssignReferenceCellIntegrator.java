package pt.tecnico.bubbledocs.integrator;

import pt.tecnico.bubbledocs.exception.BubbleDocsException;
import pt.tecnico.bubbledocs.service.local.AssignReferenceCell;

public class AssignReferenceCellIntegrator extends BubbleDocsIntegrator {
	private AssignReferenceCell _service;
	
	public AssignReferenceCellIntegrator(String tokenUser, int docId, String cellId, String reference) {
		_service = new AssignReferenceCell(tokenUser, docId, cellId, reference);
	}
	
	@Override
	protected void dispatch() throws BubbleDocsException {
		_service.execute();
	}

	public String getResult() {
		return _service.getResult();
	}
	
	public String getReference() {
		return _service.getReference();
	}
	
}
