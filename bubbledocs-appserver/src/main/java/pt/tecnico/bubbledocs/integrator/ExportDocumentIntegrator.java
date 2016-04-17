package pt.tecnico.bubbledocs.integrator;

import pt.tecnico.bubbledocs.domain.User;
import pt.tecnico.bubbledocs.exception.BubbleDocsException;
import pt.tecnico.bubbledocs.exception.RemoteInvocationException;
import pt.tecnico.bubbledocs.exception.UnavailableServiceException;
import pt.tecnico.bubbledocs.service.local.ExportDocument;
import pt.tecnico.bubbledocs.service.remote.StoreRemoteServices;

public class ExportDocumentIntegrator extends BubbleDocsIntegrator {
	ExportDocument serviceLocal;
	StoreRemoteServices serviceRemote;
	
	
	public ExportDocumentIntegrator(String userToken, int docId){
		
		serviceLocal = new ExportDocument(userToken, docId);
		serviceRemote = new StoreRemoteServices();
	}
	
	@Override
	protected void dispatch() throws BubbleDocsException {
		serviceLocal.execute();
		try{
			serviceRemote.storeDocument(getUser().getName(), Integer.toString(getDocID()), getDocXML());
		} catch(RemoteInvocationException e){
			throw new UnavailableServiceException();
		}

	}
	
	public byte[] getDocXML() {
		return serviceLocal.getDocXML();
    }
    
    public int getDocID() {
    	return serviceLocal.getDocID();
    }
    
    public String getName(){
    	return serviceLocal.getName();    	
    }
    
    public String getFilename(){
    	return serviceLocal.getFilename();
    }
	public User getUser() {
		return serviceLocal.getUser();
	}
    
    
}
