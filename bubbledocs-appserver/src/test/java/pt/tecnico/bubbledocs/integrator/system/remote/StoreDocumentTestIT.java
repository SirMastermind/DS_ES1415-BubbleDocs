package pt.tecnico.bubbledocs.integrator.system.remote;

import static org.junit.Assert.*;

import org.junit.Test;

import pt.tecnico.bubbledocs.exception.CannotLoadDocumentException;
import pt.tecnico.bubbledocs.exception.CannotStoreDocumentException;
import pt.tecnico.bubbledocs.exception.RemoteInvocationException;
import pt.tecnico.bubbledocs.service.remote.StoreRemoteServices;

public class StoreDocumentTestIT {
    // the tokens


    private static final String USERNAME = "ars";
    private static final String USERNAME_DOES_NOT_EXIST = "no-one";
    private static final String DOC = "abc";

    public void populate4Test() {
    }

    // Test 2.01: Description : It tests the creation with valid parameters.
    @Test
    public void success() throws CannotLoadDocumentException, RemoteInvocationException {
        StoreRemoteServices service = new StoreRemoteServices();
        service.storeDocument(USERNAME, DOC, DOC.getBytes());
        String doc = service.loadDocument(USERNAME, DOC).toString();
        assertEquals(DOC, doc);
    }

    // Test 2.02: Description : It tests the storeDocument with a wrong username
    @Test(expected = CannotStoreDocumentException.class)
    public void wrongUsername() throws CannotStoreDocumentException, RemoteInvocationException{

    	StoreRemoteServices service = new  StoreRemoteServices();
        service.storeDocument(USERNAME_DOES_NOT_EXIST, DOC, DOC.getBytes());
    }
    // Test 2.03: Description : It tests the storeDocument of a document that alreadyExists
    @Test(expected = CannotStoreDocumentException.class)
    public void documentAlreadyExist() throws CannotStoreDocumentException, RemoteInvocationException {
    	StoreRemoteServices service = new StoreRemoteServices();
        service.storeDocument(USERNAME, DOC, DOC.getBytes());

    }

    // Test 2.04: Description : It tests the storeDocument with a null content
    /*@Test(expected = CannotStoreDocumentException.class)
    public void nullContent() {

    	StoreRemoteServices service = new  StoreRemoteServices();
        service.storeDocument()
    }
	*/
}
