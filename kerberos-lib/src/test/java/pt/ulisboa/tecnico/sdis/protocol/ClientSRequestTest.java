package pt.ulisboa.tecnico.sdis.protocol;

import static org.junit.Assert.*;

import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Date;

import javax.crypto.KeyGenerator;

import org.junit.Test;

import pt.ulisboa.tecnico.sdis.protocol.Communication.CSRequest;
import pt.ulisboa.tecnico.sdis.protocol.Communication.CommunicationHandler;
import pt.ulisboa.tecnico.sdis.protocol.Domain.*;
import pt.ulisboa.tecnico.sdis.protocol.Exception.FailedToParseMessageException;
import pt.ulisboa.tecnico.sdis.protocol.Exception.InvalidDataException;
import pt.ulisboa.tecnico.sdis.protocol.Exception.KerberosException;
import pt.ulisboa.tecnico.sdis.protocol.Exception.WrongKeyException;
import pt.ulisboa.tecnico.sdis.protocol.Messages.EncryptedXMLMessage;
import pt.ulisboa.tecnico.sdis.protocol.Messages.Message;
import pt.ulisboa.tecnico.sdis.protocol.toolkit.DataConvertor;
import pt.ulisboa.tecnico.sdis.protocol.toolkit.Encrypt;

public class ClientSRequestTest {

	
	private static final String CLIENTID = "andriymz";
	private static final int SERVERID = 321;
	
	private static String stringT1;
	private static String stringT2;
	private static final Key csKey = Encrypt.generateKey();
	private static final Date dateTreq = new Date();
	private static final Key sKey = Encrypt.generateMD5Key("SERVER-ID");
	private static final byte[] empty = {};
	private Ticket ticket; 
	private Authentication auth;
	private HMAC hmac; 
	
	
	private void setUp(){
		try {
			stringT1 = DataConvertor.dateToString(new Date());
			stringT2 = DataConvertor.dateToString(getExpiresInXhoursDate(3));
			ticket = KerberosFactory.createTicket(CLIENTID, SERVERID, stringT1, stringT2, csKey);
			auth = KerberosFactory.createAuthentication(CLIENTID, stringT1);
			hmac = KerberosFactory.createHMAC("bytearray".getBytes());
		} catch (InvalidDataException e) {
			//NOTHING TO DO
		}
	}
	
	/*
	 * Util methods
	 */
	
	public static Date getExpiresInXhoursDate(int hoursToExpire){
		Calendar cal = Calendar.getInstance();
	    cal.setTime(new Date());
	    cal.add(Calendar.HOUR_OF_DAY, hoursToExpire); 
	    return cal.getTime();
	}
	
	public byte[] getXMLTicketBytes(Ticket ticket) throws KerberosException{
		
		Message ticketMessage = new EncryptedXMLMessage(sKey);
		
		ticketMessage.addElement(CommunicationHandler.CLIENT_TAG, ticket.getClientID());
		ticketMessage.addElement(CommunicationHandler.SERVER_TAG, Integer.toString(ticket.getServerID()));
		ticketMessage.addElement(CommunicationHandler.T1_TAG, DataConvertor.dateToString(ticket.getT1()));
		ticketMessage.addElement(CommunicationHandler.T2_TAG, DataConvertor.dateToString(ticket.getT2()));
		ticketMessage.addElement(CommunicationHandler.CS_KEY_TAG, DataConvertor.keyToString(ticket.getCsKey()));
	
		return ticketMessage.getTransmissionData();
	}

	
	/*
	 * Tests
	 */
	
	// Test 1: Tests success at sending request with correct parameters
	@Test
	public void successAtCSRequest() throws KerberosException{
		
		setUp();
		
		byte[] ticketBytes = getXMLTicketBytes(ticket);
		byte[] request = CommunicationHandler.createCSRequest(ticketBytes, auth, hmac, dateTreq, csKey);
		
		CSRequest receivedRequest = CommunicationHandler.parseCSRequest(request, sKey);
		
		assertEquals(CLIENTID, receivedRequest.getTicket().getClientID());
		assertEquals(SERVERID, receivedRequest.getTicket().getServerID());
		assertEquals(stringT1, DataConvertor.dateToString(receivedRequest.getTicket().getT1()));
		assertEquals(stringT2, DataConvertor.dateToString(receivedRequest.getTicket().getT2()));
		assertArrayEquals(csKey.getEncoded(), receivedRequest.getTicket().getCsKey().getEncoded());
		
	}
	
	// Test 2: Tests error case when wrong ticket (encripted byte[]) is sent. Server cannot parse and throws exception
	@Test(expected=FailedToParseMessageException.class)
	public void wrongTicketAtCSRequest() throws KerberosException{
		setUp();
		byte[] wrongTicketBytes = "wrong ticket".getBytes();
		byte[] request = CommunicationHandler.createCSRequest(wrongTicketBytes, auth, hmac, dateTreq, csKey);
		CommunicationHandler.parseCSRequest(request, sKey);

	}
	
	// Test 3: Tests the message deconding with a valid size key but not the expected one
	@Test(expected=FailedToParseMessageException.class)
	public void wrongKey() throws KerberosException{
		setUp();
		byte[] ticketBytes = getXMLTicketBytes(ticket);
		byte[] request = CommunicationHandler.createCSRequest(ticketBytes, auth, hmac, dateTreq, csKey);
		CommunicationHandler.parseCSRequest(request, csKey);
	}
	
	// Test 4: Tests the message deconding with a invalid size key
	@Test(expected=WrongKeyException.class)
	public void invalidKey() throws KerberosException{
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
		
		byte[] ticketBytes = getXMLTicketBytes(ticket);
		byte[] request = CommunicationHandler.createCSRequest(ticketBytes, auth, hmac, dateTreq, csKey);
		CommunicationHandler.parseCSRequest(request, invalidKey);
		
	}
	
	//The remaining tests will test the invalid data
	
	@Test(expected=InvalidDataException.class)
	public void invalidData1() throws KerberosException{
		setUp();
		CommunicationHandler.createCSRequest(null, auth, hmac, dateTreq, csKey);
	}
	
	@Test(expected=InvalidDataException.class)
	public void invalidData2() throws KerberosException{
		setUp();
		CommunicationHandler.createCSRequest(empty, auth, hmac, dateTreq, csKey);
	}
	
	@Test(expected=InvalidDataException.class)
	public void invalidData3() throws KerberosException{
		setUp();
		byte[] ticketBytes = getXMLTicketBytes(ticket);
		CommunicationHandler.createCSRequest(ticketBytes, null, hmac, dateTreq, csKey);
	}
	
	@Test(expected=InvalidDataException.class)
	public void invalidData4() throws KerberosException{
		setUp();
		byte[] ticketBytes = getXMLTicketBytes(ticket);
		CommunicationHandler.createCSRequest(ticketBytes, auth, null, dateTreq, csKey);
	}
	
	@Test(expected=InvalidDataException.class)
	public void invalidData5() throws KerberosException{
		setUp();
		byte[] ticketBytes = getXMLTicketBytes(ticket);
		CommunicationHandler.createCSRequest(ticketBytes, auth, hmac, null, csKey);
	}
	
	@Test(expected=InvalidDataException.class)
	public void invalidData6() throws KerberosException{
		setUp();
		byte[] ticketBytes = getXMLTicketBytes(ticket);
		CommunicationHandler.createCSRequest(ticketBytes, auth, hmac, dateTreq, null);
	}
	
	@Test(expected=InvalidDataException.class)
	public void invalidData7() throws KerberosException{
		CommunicationHandler.parseCSRequest(null, csKey);
	}
	
	@Test(expected=InvalidDataException.class)
	public void invalidData8() throws KerberosException{
		CommunicationHandler.parseCSRequest(empty, csKey);
	}
	
	@Test(expected=InvalidDataException.class)
	public void invalidData9() throws KerberosException{
		setUp();
		byte[] ticketBytes = getXMLTicketBytes(ticket);
		CommunicationHandler.parseCSRequest(ticketBytes, null);
	}
	
}
