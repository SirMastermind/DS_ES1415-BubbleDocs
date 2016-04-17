package pt.tecnico.bubbledocs.integrator;

import pt.tecnico.bubbledocs.exception.BubbleDocsException;
import pt.tecnico.bubbledocs.service.local.GetSpreadSheetContent;

public class GetSpreadSheetContentIntegrator extends BubbleDocsIntegrator {
	private GetSpreadSheetContent _service;
	
	public GetSpreadSheetContentIntegrator(String userToken, int sheetID) {
		_service = new GetSpreadSheetContent(userToken, sheetID);
	}

	@Override
	protected void dispatch() throws BubbleDocsException {
		_service.execute();
	}
	
	public String[][] getResult() {
		return _service.getResult().getContent();
	}
}
