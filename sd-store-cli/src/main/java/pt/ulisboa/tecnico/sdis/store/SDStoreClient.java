package pt.ulisboa.tecnico.sdis.store;

import java.util.*;

import javax.crypto.SecretKey;
import javax.xml.registry.JAXRException;

import pt.ulisboa.tecnico.sdis.exception.DocHasBeenChanged_Exception;
import pt.ulisboa.tecnico.sdis.store.ws.*; // classes generated from WSDL


public class SDStoreClient {
	private SecretKey _key = DocumentAuthentication.generate();
	private FrontEnd _frontEnd;
	
	public SDStoreClient(String name, String uddiURL, int RT, int WT) throws JAXRException{
		_frontEnd = new FrontEnd(name, uddiURL, RT, WT);
	}
	
	public SDStoreClient(String name, String uddiURL) throws JAXRException{
		this(name, uddiURL, 0, 0);
	}
	
	
	public void createDocument(String username, String docname) throws DocAlreadyExists_Exception, UserDoesNotExist_Exception {
		DocUserPair doc = new DocUserPair();
		doc.setUserId(username);
		doc.setDocumentId(docname);
		_frontEnd.createDocument(doc);
	}
	
	public List<String> listDocuments(String username) throws UserDoesNotExist_Exception {
		return _frontEnd.listDocuments(username);
	}
	
	public byte[] loadDocument(String username, String docname) throws DocDoesNotExist_Exception, UserDoesNotExist_Exception, DocHasBeenChanged_Exception {
		DocUserPair doc = new DocUserPair();
		doc.setUserId(username);
		doc.setDocumentId(docname);
		byte[] d = _frontEnd.loadDocument(doc);
		DocumentAuthentication da = new DocumentAuthentication(d, "combined", _key);
		da.fillDocumentAndCheckDigest();
		return da.getDocument();
	}
	
	public void storeDocument(String username, String docname, byte[] content) throws DocDoesNotExist_Exception, UserDoesNotExist_Exception {
		DocUserPair doc = new DocUserPair();
		doc.setUserId(username);
		doc.setDocumentId(docname);
		DocumentAuthentication da = new DocumentAuthentication(content, "document", _key);
		da.createDigest();
		_frontEnd.storeDocument(doc, da.getCombined());
	}
}
