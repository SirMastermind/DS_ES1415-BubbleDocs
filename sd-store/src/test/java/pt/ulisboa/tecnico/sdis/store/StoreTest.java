package pt.ulisboa.tecnico.sdis.store;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import pt.ulisboa.tecnico.sdis.store.domain.Document;
import pt.ulisboa.tecnico.sdis.store.domain.Store;
import pt.ulisboa.tecnico.sdis.store.ws.DocAlreadyExists_Exception;
import pt.ulisboa.tecnico.sdis.store.ws.UserDoesNotExist_Exception;

public class StoreTest {
	
	// static members
		private static final String CORRECT_USER = "guilherme";
		private static final String DOC_NAME = "ASDMNHY";
		private static final String DOC_NAME2 = "ULZFGY";
		private static Store _store;

	    // one-time initialization and clean-up

	    @BeforeClass
	    public static void oneTimeSetUp() throws DocAlreadyExists_Exception, UserDoesNotExist_Exception {
	    	_store = new Store();
	    	_store.getStore(CORRECT_USER).getDocsStore().getDocCollection().add(new Document(DOC_NAME, null));
	    	_store.getStore(CORRECT_USER).getDocsStore().getDocCollection().add(new Document(DOC_NAME2, null));
	    }

	    @AfterClass
	    public static void oneTimeTearDown() {
	    	_store = null;
	    }
/*
	    @Test
	    public void successStore() throws CapacityExceeded_Exception, DocDoesNotExist_Exception, UserDoesNotExist_Exception {
	    	_store.store(CORRECT_USER, DOC_NAME, SAMPLE_CONTENT.getBytes());
	    	ArrayList<Document> collection = _store.getStore(CORRECT_USER).getDocsStore().getDocCollection();
	    	String s = new String(collection.get(0).getContent());
	    	assertEquals(SAMPLE_CONTENT, s);
	    }
	    
	    @Test(expected = DocDoesNotExist_Exception.class)
	    public void NonExistentDoc() throws CapacityExceeded_Exception, DocDoesNotExist_Exception, UserDoesNotExist_Exception {
	    	_store.store(CORRECT_USER, INCORRECT_DOC_NAME, SAMPLE_CONTENT.getBytes());
	    }
	    
	    @Test(expected = UserDoesNotExist_Exception.class)
	    public void NonExistentUser() throws CapacityExceeded_Exception, DocDoesNotExist_Exception, UserDoesNotExist_Exception {
	    	_store.store(INCORRECT_USER, DOC_NAME, SAMPLE_CONTENT.getBytes());
	    }
	    
	    @Test(expected = CapacityExceeded_Exception.class)
	    public void CapacityReached() throws DocDoesNotExist_Exception, UserDoesNotExist_Exception, CapacityExceeded_Exception {
	    	StringBuilder t = new StringBuilder();
	    	while(t.length() < 10240) {
	    		t.append("a");
	    	}
	    	String s = t.toString();
	    	byte[] test = s.getBytes();
	    	byte[] test2 = SAMPLE_CONTENT.getBytes();
	    	_store.store(CORRECT_USER, DOC_NAME, test);
	    	_store.store(CORRECT_USER, DOC_NAME2, test2);
	    }*/
}
