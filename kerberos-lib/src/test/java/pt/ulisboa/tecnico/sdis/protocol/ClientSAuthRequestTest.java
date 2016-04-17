package pt.ulisboa.tecnico.sdis.protocol;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;
import java.util.Date;

import org.junit.*;

import pt.ulisboa.tecnico.sdis.protocol.Communication.CSautRequest;
import pt.ulisboa.tecnico.sdis.protocol.Communication.CommunicationHandler;
import pt.ulisboa.tecnico.sdis.protocol.Exception.FailedToParseMessageException;
import pt.ulisboa.tecnico.sdis.protocol.Exception.InvalidDataException;
import pt.ulisboa.tecnico.sdis.protocol.Exception.KerberosException;

public class ClientSAuthRequestTest {
	
	private static final String CLIENTID = "dmcalado";
	private static final int SERVERID = 1234;
	private static final int NONCE = 9876;
	private static final byte[] empty = {};
	
	@BeforeClass
	public static void oneTimeSetup(){
		//NOTHING TO DO
	}
	
	@AfterClass
	public static void oneTimeTearDown(){
		//NOTHING TO DO
	}
	
	
	public static Date createDateT2(Date dateT1){
		Calendar cal = Calendar.getInstance();
	    cal.setTime(dateT1);
	    cal.add(Calendar.HOUR_OF_DAY, 2); 
	    return cal.getTime();
	}
	
	@Test
	public void testCSautRequest() 
			throws KerberosException{
		
		byte[] request = CommunicationHandler.createCSautRequest(CLIENTID, SERVERID, NONCE);
		
		CSautRequest csautRequest = CommunicationHandler.parseCSautRequest(request);

		assertEquals(CLIENTID,csautRequest.getClientID());
		assertEquals(SERVERID,csautRequest.getServerID());
		assertEquals(NONCE,csautRequest.getNonce());
	}
	
	@Test (expected = FailedToParseMessageException.class)
	public void badClientInput() throws KerberosException{
		byte[] badData = CLIENTID.getBytes();
		CommunicationHandler.parseCSautRequest(badData);
		
	}
	
	@Test(expected=InvalidDataException.class)
	public void badData1() throws KerberosException{
		CommunicationHandler.createCSautRequest(null,SERVERID, NONCE);
	}
	
	@Test(expected=InvalidDataException.class)
	public void badData2() throws KerberosException{
		CommunicationHandler.parseCSautRequest(null);
	}
	
	@Test(expected=InvalidDataException.class)
	public void badData3() throws KerberosException{
		CommunicationHandler.parseCSautRequest(empty);
	}
	
	
	
	
	
	
	
}
