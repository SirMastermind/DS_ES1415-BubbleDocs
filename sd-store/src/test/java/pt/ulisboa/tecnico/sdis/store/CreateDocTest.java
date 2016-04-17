package pt.ulisboa.tecnico.sdis.store;

import java.util.ArrayList;

import org.junit.*;

import pt.ulisboa.tecnico.sdis.store.ws.*;
import static org.junit.Assert.*;
import pt.ulisboa.tecnico.sdis.store.domain.Document;
import pt.ulisboa.tecnico.sdis.store.domain.Store;

public class CreateDocTest {

	 // static members
	private static final String CORRECT_USER = "francisco";
	private static final String INCORRECT_USER = "bino";
	private static final String DOC_NAME = "ASDMNHY";
	private static final String DOC_NAME2 = "ULZXKA";
	private static final String DOC_NAME3 = "ULZFGZZ";
	private static final String DOC_NAME4 = "ULZFYY";
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

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    	while(!_store.getStore(CORRECT_USER).getDocsStore().getDocCollection().isEmpty()) {
        	_store.getStore(CORRECT_USER).getDocsStore().getDocCollection().remove(0);
        }
    }

    @Test
    public void successCreateDoc() throws DocAlreadyExists_Exception, UserDoesNotExist_Exception {
    	while(!_store.getStore(CORRECT_USER).getDocsStore().getDocCollection().isEmpty()) {
    	_store.getStore(CORRECT_USER).getDocsStore().getDocCollection().remove(0);
    	}
    	_store.createDocument(CORRECT_USER, DOC_NAME);
    	ArrayList<Document> collection = _store.getStore(CORRECT_USER).getDocsStore().getDocCollection();
    	assertEquals("Wrong number of files", 1, collection.size());
    	assertEquals("Wrong file name", DOC_NAME, collection.get(0).getName());
    }
    
    @Test(expected = DocAlreadyExists_Exception.class)
    public void duplicateDoc() throws DocAlreadyExists_Exception, UserDoesNotExist_Exception {
    	_store.createDocument(CORRECT_USER, DOC_NAME2);
    	_store.createDocument(CORRECT_USER, DOC_NAME2);
    }
    
    @Test
    public void successCreateDocStrikesAgain() throws DocAlreadyExists_Exception, UserDoesNotExist_Exception {
    	_store.createDocument(CORRECT_USER, DOC_NAME3);
    	_store.createDocument(CORRECT_USER, DOC_NAME4);
    	ArrayList<Document> collection = _store.getStore(CORRECT_USER).getDocsStore().getDocCollection();
    	assertEquals("Wrong number of files", 2, collection.size());
    	assertEquals("Wrong file name", DOC_NAME4, collection.get(1).getName());
    }
    @Test//(expected = UserDoesNotExist_Exception.class)
    public void UserDoesNotExist() throws DocAlreadyExists_Exception, UserDoesNotExist_Exception {
    	_store.createDocument(INCORRECT_USER, DOC_NAME3);
    }
}