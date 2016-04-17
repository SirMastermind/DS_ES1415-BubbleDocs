package pt.ulisboa.tecnico.sdis.store.domain;

import java.util.ArrayList;

import pt.ulisboa.tecnico.sdis.store.dto.DocumentDTO;
import pt.ulisboa.tecnico.sdis.store.ws.*;

public class DocsStore {

    // Attributes
    private ArrayList<Document> _collection;
    
    public ArrayList<Document> getDocCollection() {
    	return _collection;
    }
    
    public DocsStore() {
    	_collection = new ArrayList<Document>();
    }
    
    public void addDocument(Document document) {
        _collection.add(document);
    }
    
    public Document getDocument(String name) {
        for(Document doc: _collection) {
            if(doc.getName().equals(name))
               return doc;
        }
        return null;
    }
    
    public boolean checkIfExists(String toCheck) {
        for(Document doc: _collection) {
            if(doc.getName().equals(toCheck))
               return true;
        }
        return false;
    }
    
    public void checkDocName(String name) throws DocAlreadyExists_Exception {
    	// Check if the document exists
    	if(checkIfExists(name)) { 
    		throw new DocAlreadyExists_Exception(name, new DocAlreadyExists());
    	}
    }
    
    public void checkInputCreating(String name, byte[] content) throws DocDoesNotExist_Exception {
    	// Check if the document exists
    	if(!checkIfExists(name)) { throw new DocDoesNotExist_Exception(name, new DocDoesNotExist()); }
    	
    }
    
 
    public void createDocument(String name) throws DocAlreadyExists_Exception {
        
        // Checks input
    	checkDocName(name);
        
        // Creates the document and adds it to the collection
        Document document = new Document(name, new Tag(0,0));
        addDocument(document);
    }
    
    public ArrayList<String> listDocuments() {
        ArrayList<String> export = new ArrayList<String>();
        for(Document doc: _collection) {
            export.add(doc.getName());
        }
        return export;
    }
    
    public void changeDocument(String name, DocumentDTO doc) throws DocDoesNotExist_Exception {
    	checkInputCreating(name, doc.getDocument());

        // Add the content
    	Document document = getDocument(name);
    	document.setContent(doc.getDocument());
    	document.setTag(doc.getTag());
    }

    public DocumentDTO loadDocument(String name) throws DocDoesNotExist_Exception {
       	// Checks if the document exists
        if(!checkIfExists(name)) { throw new DocDoesNotExist_Exception(name, new DocDoesNotExist()); }
        
        // Gets the document
        Document doc = getDocument(name);
        //if (doc.getContent() != null) {
        	return new DocumentDTO(doc.getContent(), doc.getTag());
       /* } else {
        	return new DocumentDTO("".getBytes(), doc.getTag());
        }*/
    }
}