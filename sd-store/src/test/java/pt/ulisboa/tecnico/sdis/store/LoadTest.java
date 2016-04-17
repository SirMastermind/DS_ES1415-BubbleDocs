package pt.ulisboa.tecnico.sdis.store;

import static org.junit.Assert.assertEquals;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.ulisboa.tecnico.sdis.store.domain.Document;
import pt.ulisboa.tecnico.sdis.store.domain.Store;
import pt.ulisboa.tecnico.sdis.store.domain.Tag;
import pt.ulisboa.tecnico.sdis.store.ws.DocDoesNotExist_Exception;
import pt.ulisboa.tecnico.sdis.store.ws.UserDoesNotExist_Exception;

public class LoadTest {

	 // static members
	private static final String CORRECT_USER = "francisco";
	private static final String INCORRECT_USER = "bino";
	private static final String DOC_NAME = "LEL";
	private static final String BAD_DOC_NAME = "LOL";
	private static final String CONTENT = "Hello world!";
	private static Store _store;

    // one-time initialization and clean-up

    @BeforeClass
    public static void oneTimeSetUp() {
    	_store = new Store();
    	Document test = new Document(DOC_NAME, CONTENT.getBytes(), new Tag(0,0));
    	_store.getStore(CORRECT_USER).getDocsStore().getDocCollection().add(test);
    }

    @AfterClass
    public static void oneTimeTearDown() {
    	_store = null;
    }
    
    // tests

    @Test
    public void successLoad() throws DocDoesNotExist_Exception, UserDoesNotExist_Exception {
    	byte[] document = _store.load(CORRECT_USER, DOC_NAME).getDocument();
    	String s = new String(document);
    	assertEquals("Wrong content", CONTENT, s);
    }
    
    @Test(expected = DocDoesNotExist_Exception.class)
    public void unexistentDoc() throws DocDoesNotExist_Exception, UserDoesNotExist_Exception {
    	_store.load(CORRECT_USER, BAD_DOC_NAME).getDocument();
    }
    
    @Test(expected = UserDoesNotExist_Exception.class)
    public void unexistentUser() throws DocDoesNotExist_Exception, UserDoesNotExist_Exception {
    	_store.load(INCORRECT_USER, DOC_NAME).getDocument();
    }
}