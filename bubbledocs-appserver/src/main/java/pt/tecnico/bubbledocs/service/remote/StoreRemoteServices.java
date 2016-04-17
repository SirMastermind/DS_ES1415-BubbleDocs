package pt.tecnico.bubbledocs.service.remote;

import javax.xml.registry.JAXRException;

import pt.tecnico.bubbledocs.exception.CannotStoreDocumentException;
import pt.tecnico.bubbledocs.exception.CannotLoadDocumentException;
import pt.tecnico.bubbledocs.exception.RemoteInvocationException;
import pt.ulisboa.tecnico.sdis.exception.DocHasBeenChanged_Exception;
import pt.ulisboa.tecnico.sdis.store.SDStoreClient;
import pt.ulisboa.tecnico.sdis.store.ws.DocAlreadyExists_Exception;
import pt.ulisboa.tecnico.sdis.store.ws.DocDoesNotExist_Exception;
import pt.ulisboa.tecnico.sdis.store.ws.UserDoesNotExist_Exception;

public class StoreRemoteServices {
	private SDStoreClient _client = null;
	
	public StoreRemoteServices() {
		
		try {
			_client = new SDStoreClient("SD-STORE", "http://localhost:8081");
		} catch (JAXRException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void storeDocument(String username, String docName, byte[] document) throws CannotStoreDocumentException, RemoteInvocationException {
		try {
			_client.storeDocument(username, docName, document);
		} catch (DocDoesNotExist_Exception | UserDoesNotExist_Exception e) {
			try {
				_client.createDocument(username, docName);
				_client.storeDocument(username, docName, document);
			} catch (DocDoesNotExist_Exception e1) {
				throw new CannotStoreDocumentException();
			} catch (DocAlreadyExists_Exception e1) { 
				throw new CannotStoreDocumentException();
			} catch (UserDoesNotExist_Exception e1) {
				throw new CannotStoreDocumentException(); //User Does not exist, no files
			}
		}
	}
	
	public byte[] loadDocument(String username, String docName) throws CannotLoadDocumentException, RemoteInvocationException {
		try {
			return _client.loadDocument(username, docName);
		} catch (DocHasBeenChanged_Exception e) {
			throw new CannotStoreDocumentException(); //Document has been corrupted
		} catch (DocDoesNotExist_Exception e) {
			throw new CannotStoreDocumentException(); //Document does not exist
		} catch (UserDoesNotExist_Exception e) {
			throw new CannotStoreDocumentException(); //User Does not exist, no files
		}
	}	
}
