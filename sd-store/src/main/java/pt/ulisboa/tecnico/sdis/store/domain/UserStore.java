package pt.ulisboa.tecnico.sdis.store.domain;

import java.util.ArrayList;

import pt.ulisboa.tecnico.sdis.store.dto.DocumentDTO;
import pt.ulisboa.tecnico.sdis.store.ws.*;

public class UserStore {
	
	private DocsStore _docs;
	
	/*
	 * should be changed, can generate errors
	 */
	
	public UserStore() {
	    _docs = new DocsStore();
	}
	
	public DocsStore getDocsStore() {
		return _docs;
	}
	
	public void createDocument(String docname) throws DocAlreadyExists_Exception {
		_docs.createDocument(docname);
	}
	
	public ArrayList<String> listDocs() {
		return _docs.listDocuments();
	}
	
	public void store(String docname, DocumentDTO doc) throws DocDoesNotExist_Exception, UserDoesNotExist_Exception {
		_docs.changeDocument(docname, doc);
	}
	
	public DocumentDTO loadDocument(String docname) throws DocDoesNotExist_Exception {
		return _docs.loadDocument(docname);
	}

	
	
}