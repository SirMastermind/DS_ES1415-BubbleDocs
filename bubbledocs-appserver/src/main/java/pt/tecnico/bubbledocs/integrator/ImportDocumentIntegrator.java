package pt.tecnico.bubbledocs.integrator;

import pt.tecnico.bubbledocs.domain.Sheet;
import pt.tecnico.bubbledocs.exception.BubbleDocsException;
import pt.tecnico.bubbledocs.exception.RemoteInvocationException;
import pt.tecnico.bubbledocs.exception.UnavailableServiceException;
import pt.tecnico.bubbledocs.service.local.GetUsername4Token;
import pt.tecnico.bubbledocs.service.local.ImportDocument;
import pt.tecnico.bubbledocs.service.remote.StoreRemoteServices;

public class ImportDocumentIntegrator extends BubbleDocsIntegrator {
	
	private GetUsername4Token checkSession;
	private StoreRemoteServices remoteService;
	private ImportDocument localService;
	private int sheetID;
	
	public ImportDocumentIntegrator(String userToken, int sheetID) {
		remoteService = new StoreRemoteServices();
		checkSession = new GetUsername4Token(userToken);
		localService = new ImportDocument(userToken);
		this.sheetID = sheetID;
	}

	@Override
	protected void dispatch() throws BubbleDocsException {
		
		checkSession.execute();
		
		String username = checkSession.getUsername();
		byte[] fileBytes;
		
		try{
			fileBytes = remoteService.loadDocument(username, Integer.toString(sheetID));
		}
		catch(RemoteInvocationException e){
			throw new UnavailableServiceException();
		}
		 
		
		localService.setFileBytes(fileBytes);
		localService.execute();
		
	}

	public Sheet getSheet() {
		return localService.getSheet();
	}

}
