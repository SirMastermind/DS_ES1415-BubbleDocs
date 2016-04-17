package pt.ulisboa.tecnico.sdis.protocol;

import static org.junit.Assert.assertEquals;

import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Date;

import javax.crypto.KeyGenerator;

import org.junit.*;

import pt.ulisboa.tecnico.sdis.protocol.Communication.CommunicationHandler;
import pt.ulisboa.tecnico.sdis.protocol.Communication.SAuthResponse;
import pt.ulisboa.tecnico.sdis.protocol.Domain.KerberosFactory;
import pt.ulisboa.tecnico.sdis.protocol.Domain.Ticket;
import pt.ulisboa.tecnico.sdis.protocol.Exception.*;
import pt.ulisboa.tecnico.sdis.protocol.Messages.EncryptedXMLMessage;
import pt.ulisboa.tecnico.sdis.protocol.Messages.Message;
import pt.ulisboa.tecnico.sdis.protocol.Messages.XMLMessage;
import pt.ulisboa.tecnico.sdis.protocol.toolkit.*;

public class SAuthResponseTest {
	
	private static final String CLIENTID = "dmcalado";
	private static final int SERVERID = 1234;
	private static final int NONCE = 9876;
	public final static String TICKET_TAG = "ticket";
	public final static String CLIENT_TAG = "client";
	public final static String SERVER_TAG = "server";
	public final static String CS_KEY_TAG = "cskey";
	public final static String T1_TAG = "t1";
	public final static String T2_TAG = "t2";
	private static final byte[] empty = {};
	private static final Date dateT1 = new Date(); 
	private static String stringT1;
	private static String stringT2;
	private static final Key csKey = Encrypt.generateKey();
	private static final Key sKey = Encrypt.generateKey();
	private static final Key cKey = Encrypt.generateKey();
	private Ticket ticket;
	
	@BeforeClass
	public static void oneTimeSetup(){
		//NOTHING TO DO
	}
	
	@AfterClass
	public static void oneTimeTearDown(){
		//NOTHING TO DO
	}
	
	
	private void setUp(){
		try {
			stringT1 = DataConvertor.dateToString(dateT1);
			stringT2 = DataConvertor.dateToString(createDateT2(dateT1));
			ticket = KerberosFactory.createTicket(CLIENTID,SERVERID, stringT1, stringT2, csKey);
		} catch (InvalidDataException e) {
			//NOTHING TO DO
		}
	}
	
	
	public static Date createDateT2(Date dateT1){
		Calendar cal = Calendar.getInstance();
	    cal.setTime(dateT1);
	    cal.add(Calendar.HOUR_OF_DAY, 2); 
	    return cal.getTime();
	}
	
	//tests if the client can obtain the right csKey
	@Test
	public void testSharedKey() throws KerberosException{
		setUp();
		
		byte[] request = CommunicationHandler.createSAuthResponse(ticket, NONCE, csKey, cKey, sKey);
		SAuthResponse sauthresponse = CommunicationHandler.parseSAuthResponse(request, cKey);
		
		
		Key tmp_cskey = sauthresponse.getCsKey();
		String stringcsKey = DataConvertor.keyToString(tmp_cskey);

		assertEquals(csKey, tmp_cskey);
		//assertEquals(sauthresponse.getCsKey(), KeyManager.getInstance().getSessionKey());
		assertEquals(DataConvertor.keyToString(csKey), stringcsKey);	
		assertEquals(NONCE, sauthresponse.getNonce());
	}
	
	
	@Test(expected= FailedToParseMessageException.class)
	public void clientDecryptTicket() throws KerberosException{
		setUp();
		
		//SAuth creates the request
		byte[] request = CommunicationHandler.createSAuthResponse(ticket, NONCE, csKey, cKey, sKey);
		
		//client parse the request
		SAuthResponse sauthresponse = CommunicationHandler.parseSAuthResponse(request, cKey);
				
		//create a XML Message to processReceivedData
		Message msg = new XMLMessage();
		msg.addElement(TICKET_TAG, sauthresponse.getTicket());
		
		/* create the ticket object*/
		//client tries to decrypt the encoded ticket and fails!
		Message ticketMessage = new EncryptedXMLMessage(cKey);
		ticketMessage.processReceivedData(DataConvertor.getBytesFromBase64String(msg.getElement(TICKET_TAG)));	
	}
	
	@Test(expected=FailedToParseMessageException.class)
	public void clientWrongKey() throws KerberosException{
		
		setUp();
		
		//SAuth creates the request
		byte[] request = CommunicationHandler.createSAuthResponse(ticket, NONCE, csKey, cKey, sKey);
				
		//client parse the request with an invalid key
		CommunicationHandler.parseSAuthResponse(request, csKey);
	}
	
	@Test(expected=WrongKeyException.class)
	public void clientInvalidKey() throws KerberosException{
		
		setUp();
		
		Key invalidKey = null;
		KeyGenerator keyGen;
		
		try {
			keyGen = KeyGenerator.getInstance("DES");
			keyGen.init(56);
	        invalidKey = keyGen.generateKey();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
		//SAuth creates the request
		byte[] request = CommunicationHandler.createSAuthResponse(ticket, NONCE, csKey, cKey, sKey);
				
		//client parse the request with an invalid key
		CommunicationHandler.parseSAuthResponse(request, invalidKey);
	}
	
	
	@Test(expected=InvalidDataException.class)
	public void invalidData1() throws KerberosException{
		setUp();
		CommunicationHandler.createSAuthResponse(null, NONCE, csKey, cKey, csKey);
	}
	
	@Test(expected=InvalidDataException.class)
	public void invalidData2() throws KerberosException{
		setUp();
		CommunicationHandler.createSAuthResponse(ticket, NONCE, null, cKey, csKey);
	}
	
	@Test(expected=InvalidDataException.class)
	public void invalidData3() throws KerberosException{
		setUp();
		CommunicationHandler.createSAuthResponse(ticket, NONCE, csKey, null, csKey);
	}
	
	@Test(expected=InvalidDataException.class)
	public void invalidData4() throws KerberosException{
		setUp();
		CommunicationHandler.createSAuthResponse(ticket, NONCE, csKey, cKey, null);
	}
	
	@Test(expected=InvalidDataException.class)
	public void invalidData5() throws KerberosException{
		CommunicationHandler.parseSAuthResponse(null, cKey);
	}
	
	@Test(expected=InvalidDataException.class)
	public void invalidData6() throws KerberosException{
		CommunicationHandler.parseSAuthResponse(empty, cKey);
	}
	
	@Test(expected=InvalidDataException.class)
	public void invalidData7() throws KerberosException{
		CommunicationHandler.parseSAuthResponse(CS_KEY_TAG.getBytes(), null);
	}
	
	
}
