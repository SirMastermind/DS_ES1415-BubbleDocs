package pt.ulisboa.tecnico.sdis.id.ws.impl;

import javax.xml.registry.JAXRException;
import javax.xml.ws.Endpoint;

import mockit.Expectations;
import mockit.Mocked;

import org.junit.*;
import static org.junit.Assert.*;
import pt.ulisboa.tecnico.sdis.uddi.UDDINaming;

public class ServicePublishTest {
	private static final String UDDIURL = "http://localhost:8081";
	private static final String WSNAME = "SD-ID";
	private static final String ENDPOINT = "http://localhost:8080/urn:sd_id/endpoint";
	


	@BeforeClass
	public static void oneTimeSetup(){
		//NOTHING TO DO
	}
	
	@AfterClass
	public static void oneTimeTearDown(){
		//NOTHING TO DO
	}
	//Test when uddi does not find the service asked from client
	@Test
    public void testWSNotFound(@Mocked final UDDINaming uddinaming, @Mocked final Endpoint endpoint) 
    		throws  JAXRException {
			
    	String args[] = {UDDIURL, WSNAME, ENDPOINT};
    	
	        new Expectations() {{
	        	//mocked endpoint to avoid errors when server is already running
	        	endpoint.publish(anyString); result = null;
	            uddinaming.rebind(anyString, anyString);
	            result = new JAXRException();
	        }};

	        try
	        {
	        	SDIDMain.main(args);
	        }
	        catch(Exception e)
	        {	//should stop quietly
	        	fail();
	        }
	}
    @Test
    public void testWSNotPublished(@Mocked final Endpoint endpoint) 
    		throws  JAXRException {
			
    	String args[] = {UDDIURL, WSNAME, ENDPOINT};
    	
	        new Expectations() {{
	            endpoint.publish(ENDPOINT);
	            result = new IllegalArgumentException();
	        }};

	        try
	        {
	        	SDIDMain.main(args);
	        }
	        catch(Exception e)
	        {	//should stop quietly
	        	fail();
	        }
	}
}
