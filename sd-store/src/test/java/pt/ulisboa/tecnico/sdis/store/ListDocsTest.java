package pt.ulisboa.tecnico.sdis.store;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.ulisboa.tecnico.sdis.store.domain.Store;
import pt.ulisboa.tecnico.sdis.store.ws.DocAlreadyExists_Exception;
import pt.ulisboa.tecnico.sdis.store.ws.UserDoesNotExist_Exception;

public class ListDocsTest{ 
 // static members
	private static final String CORRECT_USER = "francisco";
	private static final String CORRECT_USER2 = "bruno"; 
	private static final String INCORRECT_USER = "bino";
	private static final String DOC_NAME = "ASDMNHY";
	private static final String DOC_NAME2 = "ULZXKA";
	private static final String DOC_NAME3 = "ABCDEF";
	private static Store _store;

    // one-time initialization and clean-up

    @BeforeClass
    public static void oneTimeSetUp() {
    	_store = new Store();
    }

    @AfterClass
    public static void oneTimeTearDown() {
    	_store = null;
    }

    // tests

    @Test
    public void successListZeroDoc() throws DocAlreadyExists_Exception, UserDoesNotExist_Exception {
    	ArrayList<String> collection = _store.listDocs(CORRECT_USER);
    	assertEquals("Wrong number of files", 0, collection.size());
    }
    
    @Test
    public void successListOneDoc() throws DocAlreadyExists_Exception, UserDoesNotExist_Exception {
    	_store.getStore(CORRECT_USER).createDocument(DOC_NAME);
    	ArrayList<String> collection = _store.listDocs(CORRECT_USER);
    	assertEquals("Wrong number of files", 1, collection.size());
    	assertEquals("Wrong file name", DOC_NAME, collection.get(0));
    }
    @Test
    public void successListTwoDocs() throws DocAlreadyExists_Exception, UserDoesNotExist_Exception {
    	_store.getStore(CORRECT_USER2).createDocument(DOC_NAME2);
    	_store.getStore(CORRECT_USER2).createDocument(DOC_NAME3);
    	ArrayList<String> collection = _store.listDocs(CORRECT_USER2);
    	assertEquals("Wrong number of files", 2, collection.size());
    	assertEquals("Wrong file name", DOC_NAME2, collection.get(0));
    	assertEquals("Wrong file name", DOC_NAME3, collection.get(1));
    }
    @Test(expected = UserDoesNotExist_Exception.class)
    public void unexistentUser() throws UserDoesNotExist_Exception {
    	_store.listDocs(INCORRECT_USER);
    }
}