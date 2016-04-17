package pt.ulisboa.tecnico.sdis.id.ws.impl;

import static org.junit.Assert.*;

import java.security.Key;
import java.util.Date;

import org.junit.BeforeClass;
import org.junit.Test;

import pt.ulisboa.tecnico.sdis.id.ws.AuthReqFailed_Exception;
import pt.ulisboa.tecnico.sdis.protocol.Communication.CommunicationHandler;
import pt.ulisboa.tecnico.sdis.protocol.Communication.SAuthResponse;
import pt.ulisboa.tecnico.sdis.protocol.Domain.KerberosFactory;
import pt.ulisboa.tecnico.sdis.protocol.Domain.Ticket;
import pt.ulisboa.tecnico.sdis.protocol.Exception.InvalidDataException;
import pt.ulisboa.tecnico.sdis.protocol.Exception.KerberosException;
import pt.ulisboa.tecnico.sdis.protocol.Messages.EncryptedXMLMessage;
import pt.ulisboa.tecnico.sdis.protocol.Messages.Message;
import pt.ulisboa.tecnico.sdis.protocol.toolkit.DataConvertor;
import pt.ulisboa.tecnico.sdis.protocol.toolkit.Encrypt;

public class RequestAuthenticationTest {
	
	private static final String CLIENTID = "duarte";
	private static final String INVALID_CLIENTID = "cenasfixes";
	private static final int INVALID_SERVERID = 100;
	private static final int SERVERID = 1;
	private static final int NONCE = 123;
	private static final String CLIENT_PASSWORD = "Ddd4";
	private static final String SERVER_PASSWORD = "chavefixe";
	private static Key cKey;
	private static Key sKey;
	private static int sessionTIME = 2*3600*1000;
	SDIDImpl server = new SDIDImpl();
	
	@BeforeClass
	public static void oneTimeSetup() {
		cKey = Encrypt.generateMD5Key(CLIENT_PASSWORD);
		sKey = Encrypt.generateMD5Key(SERVER_PASSWORD);
	}
	
	private Ticket parseTicket(String encodedTicket){
		/* create the ticket object*/
		
		try {
			Message ticketMessage = new EncryptedXMLMessage(sKey);
			ticketMessage.processReceivedData(DataConvertor.getBytesFromBase64String(encodedTicket));
			String clientID = ticketMessage.getElement(CommunicationHandler.CLIENT_TAG);
			int serverID = Integer.parseInt(ticketMessage.getElement(CommunicationHandler.SERVER_TAG));
			String t1 = ticketMessage.getElement(CommunicationHandler.T1_TAG);
			String t2 = ticketMessage.getElement(CommunicationHandler.T2_TAG);
			Key csKey = DataConvertor.stringToKey(ticketMessage.getElement(CommunicationHandler.CS_KEY_TAG));
			
			return KerberosFactory.createTicket(clientID, serverID, t1, t2, csKey);
		} catch (InvalidDataException e) {
			return null;
		} catch (KerberosException e) {
			return null;
		}
				
	}
	
	
	@Test
	public void success() throws KerberosException, AuthReqFailed_Exception{
		
		
		byte[] request = CommunicationHandler.createCSautRequest(CLIENTID, SERVERID, NONCE);
		byte[] response = server.requestAuthentication(CLIENTID, request);
		
		//parse response		
		SAuthResponse sauthResponse = CommunicationHandler.parseSAuthResponse(response, cKey);
		
		Key csKey = sauthResponse.getCsKey();
		String encodedTicket = sauthResponse.getTicket();
		int nonce = sauthResponse.getNonce();
		
		Ticket ticket = parseTicket(encodedTicket);
		Date t1 = ticket.getT1();
		Date t2 = ticket.getT2();
		Key csKey2 = ticket.getCsKey();
		String clientID = ticket.getClientID();
		int serverID = ticket.getServerID();
		
		//ensure correct data send
		assertEquals(NONCE, nonce);
		assertEquals(SERVERID, serverID);
		assertEquals(CLIENTID, clientID);
		assertEquals(t2.getTime() - t1.getTime(), sessionTIME);
		long minus = new Date().getTime() - t1.getTime();
		assertTrue(minus < 10000);
		assertArrayEquals(csKey.getEncoded(), csKey2.getEncoded());
	}
	
	@Test(expected=AuthReqFailed_Exception.class)
	public void invalidServer() throws KerberosException, AuthReqFailed_Exception{
		byte[] request = CommunicationHandler.createCSautRequest(CLIENTID, INVALID_SERVERID, NONCE);
		server.requestAuthentication(CLIENTID, request);
	}
	
	@Test(expected=AuthReqFailed_Exception.class)
	public void invalidClient() throws KerberosException, AuthReqFailed_Exception{
		byte[] request = CommunicationHandler.createCSautRequest(INVALID_CLIENTID, SERVERID, NONCE);
		server.requestAuthentication(INVALID_CLIENTID, request);
	}
		
}
