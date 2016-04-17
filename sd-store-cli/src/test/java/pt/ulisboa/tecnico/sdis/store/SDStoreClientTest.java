package pt.ulisboa.tecnico.sdis.store;


import javax.management.ServiceNotFoundException;
import javax.xml.registry.JAXRException;

import mockit.*;

import org.junit.*;

import pt.ulisboa.tecnico.sdis.uddi.UDDINaming;

public class SDStoreClientTest {
	
	private static final String UDDI_URL = "http://localhost:8081";
	private static final String WS_NAME = "SD-STORE";
	
	@BeforeClass
	public static void oneTimeSetup() { }
	
	@AfterClass
	public static void oneTimeTearDown() { }
	
	//Connection fails to uddi
	@Test(expected=JAXRException.class)
    public void testUDDIAddress(@Mocked final UDDINaming uddinaming)throws JAXRException, ServiceNotFoundException {
	        new Expectations() {{
	        	new UDDINaming(UDDI_URL);
	            result = new JAXRException();
	        }};

	        new SDStoreClient(WS_NAME, UDDI_URL);
	}
	
	//No service on uddi
	@Test(expected=ServiceNotFoundException.class)
    public void testWSNotFound(@Mocked final UDDINaming uddinaming)throws JAXRException, ServiceNotFoundException 
	{
	        new Expectations() {{
	        	uddinaming.list(WS_NAME);
	            result = new ServiceNotFoundException();
	        }};

	        new SDStoreClient(WS_NAME, UDDI_URL);
	}
}
