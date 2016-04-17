package pt.tecnico.bubbledocs.integrator.system.remote;

import static org.junit.Assert.*;

import org.junit.Test;

import pt.tecnico.bubbledocs.exception.CannotLoadDocumentException;
import pt.tecnico.bubbledocs.exception.RemoteInvocationException;
import pt.tecnico.bubbledocs.service.remote.StoreRemoteServices;

public class LoadDocumentTestIT {

    private static final String USERNAME = "alice";
    private static final String USERNAME_DOES_NOT_EXIST = "no-one";
    private static final String DOCNAME = "doc1";
    private static final String WRONG_DOC_NAME = "doc2";
    public void populate4Test() {
    	StoreRemoteServices service = new StoreRemoteServices();
    	service.storeDocument(USERNAME, DOCNAME, DOCNAME.getBytes());
    }

    // Test 2.01: Description : It tests the creation with valid parameters.
    @Test
    public void success()throws CannotLoadDocumentException, RemoteInvocationException{
        StoreRemoteServices service = new StoreRemoteServices();
        String doc = service.loadDocument(USERNAME, DOCNAME).toString();
	    assertEquals(DOCNAME,doc);
    }

    // Test 2.02: Description : It tests the loadDocument with a wrong username
    @Test(expected = CannotLoadDocumentException.class)
    public void wrongUsername() throws CannotLoadDocumentException, RemoteInvocationException {
    	StoreRemoteServices service = new  StoreRemoteServices();
        service.loadDocument(USERNAME_DOES_NOT_EXIST, DOCNAME);
    }

    // Test 2.03: Description : It tests the loadDocument of a document that does not exist
    @Test(expected = CannotLoadDocumentException.class)
    public void documentDoesNotExist() throws CannotLoadDocumentException, RemoteInvocationException {
    	StoreRemoteServices service = new StoreRemoteServices();
        service.loadDocument(USERNAME, WRONG_DOC_NAME);

    }
}
