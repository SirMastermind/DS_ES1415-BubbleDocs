package pt.ulisboa.tecnico.sdis.protocol.Communication;

import java.security.Key;
import java.util.Date;

import pt.ulisboa.tecnico.sdis.protocol.Domain.*;
import pt.ulisboa.tecnico.sdis.protocol.Exception.InvalidDataException;
import pt.ulisboa.tecnico.sdis.protocol.Exception.KerberosException;
import pt.ulisboa.tecnico.sdis.protocol.Messages.*;
import pt.ulisboa.tecnico.sdis.protocol.toolkit.DataConvertor;


public final class CommunicationHandler {
	
	
	public final static String TICKET_TAG = "ticket";
	public final static String AUTH_TAG = "auth";
	public final static String CLIENT_TAG = "client";
	public final static String SERVER_TAG = "server";
	public final static String TREQUEST_TAG = "trequest";
	public final static String NONCE_TAG = "nonce";
	public final static String HMAC_TAG = "hmac";
	public final static String CS_KEY_TAG = "cskey";
	public final static String ORIGINAL_RESUME_TAG = "resume";
	public final static String T1_TAG = "t1";
	public final static String T2_TAG = "t2";

	
	
	//***********************************************************************************************
	/*
	 * Public Methods
	 * 
	 * Methods that the kerberos client and server must use to communicate
	 */
	//***********************************************************************************************
	
	
	/*
	 * Methods to handle the Client -> Auth Server  Request 
	 */
	
	/*
	 * method that kerberos client should use to generate the Auth Server initial request
	 */
	
	public static byte[] createCSautRequest(String clientID, int serverID, int nonce) 
			throws KerberosException{
		
		XMLMessage msg = new XMLMessage();
		
		msg.addElement(CLIENT_TAG, clientID);
		msg.addElement(SERVER_TAG, Integer.toString(serverID));
		msg.addElement(NONCE_TAG, Integer.toString(nonce));

		return msg.getTransmissionData();
	}
	
	/*
	 * method that kerberos Auth Server should use to process each client request
	 */
	
	public static CSautRequest parseCSautRequest(byte[] request) throws KerberosException {
		
		if(request == null || request.length == 0) throw new InvalidDataException();
		
		XMLMessage msg = new XMLMessage();
		msg.processReceivedData(request);
		
		String clientID = msg.getElement(CLIENT_TAG);
		int serverID = Integer.parseInt(msg.getElement(SERVER_TAG));
		int nonce = Integer.parseInt(msg.getElement(NONCE_TAG));
				
		return new CSautRequest(clientID, serverID, nonce);
		
	}
	
	/*
	 * Methods to handle the Auth Server -> Client Response 
	 */
	
	/*
	 * method that kerberos Auth Server should use to generate the Auth Server response
	 */
	
	
	public static byte[] createSAuthResponse(Ticket ticket, int nonce, Key csKey,Key cKey, Key sKey) throws KerberosException{
		

		if(ticket == null || csKey == null || cKey == null || sKey == null) throw new InvalidDataException();

		XMLMessage msg = new XMLMessage();
		Message ticketMessage = new EncryptedXMLMessage(sKey);
		Message clientMessage = new EncryptedXMLMessage(cKey);
		
		ticketMessage.addElement(CLIENT_TAG, ticket.getClientID());
		ticketMessage.addElement(SERVER_TAG, Integer.toString(ticket.getServerID()));
		ticketMessage.addElement(T1_TAG, DataConvertor.dateToString(ticket.getT1()));
		ticketMessage.addElement(T2_TAG, DataConvertor.dateToString(ticket.getT2()));
		ticketMessage.addElement(CS_KEY_TAG, DataConvertor.keyToString(ticket.getCsKey()));
		clientMessage.addElement(CS_KEY_TAG, DataConvertor.keyToString(csKey));
		clientMessage.addElement(NONCE_TAG, Integer.toString(nonce));

		msg.addElement(TICKET_TAG, DataConvertor.getStringFromBytes(ticketMessage.getTransmissionData()));
		msg.addElement(CLIENT_TAG, DataConvertor.getStringFromBytes(clientMessage.getTransmissionData()));

		return msg.getTransmissionData();
		
	}
	
	/*
	 * method that kerberos Client should use to parse the Auth Server response
	 */
	
	public static SAuthResponse parseSAuthResponse(byte[] request, Key cKey) throws KerberosException{
		
		if(request == null || request.length == 0 || cKey == null) throw new InvalidDataException();
		
		Message msg = new XMLMessage();
		msg.processReceivedData(request);
		
		Message clientMessage = new EncryptedXMLMessage(cKey);
		clientMessage.processReceivedData(DataConvertor.getBytesFromBase64String(msg.getElement(CLIENT_TAG)));
		
		int nonce = Integer.parseInt(clientMessage.getElement(NONCE_TAG));
		Key csKey = DataConvertor.stringToKey(clientMessage.getElement(CS_KEY_TAG));
				
		String encodedTicket = msg.getElement(TICKET_TAG); //cant decode on client side

		return new SAuthResponse(encodedTicket, csKey, nonce);		
	}
	
	
	
	/*
	 * Methods to handle the Client -> Server Request
	 */
	
	/*
	 * method that kerberos Client should use to generate the Server request
	 */
	
	public static byte[] createCSRequest(byte[] ticket, Authentication auth, HMAC hmac, Date tReq, Key csKey) throws KerberosException{
		
		if(ticket == null || ticket.length == 0 || auth == null || hmac == null || tReq == null || csKey == null) 
			throw new InvalidDataException();
		
		Message msg = new XMLMessage();
		Message authEncrypted = new EncryptedXMLMessage(csKey);
		Message hmacEncrypted = new EncryptedXMLMessage(csKey);
		
		authEncrypted.addElement(CLIENT_TAG, auth.getClientID());
		authEncrypted.addElement(TREQUEST_TAG, DataConvertor.dateToString(auth.gettRequest()));
		hmacEncrypted.addElement(ORIGINAL_RESUME_TAG, DataConvertor.getStringFromBytes(hmac.getOriginalResume()));
		
		
		msg.addElement(TICKET_TAG,  DataConvertor.getStringFromBytes(ticket));
		msg.addElement(AUTH_TAG, DataConvertor.getStringFromBytes(authEncrypted.getTransmissionData()));
		msg.addElement(HMAC_TAG, DataConvertor.getStringFromBytes(hmacEncrypted.getTransmissionData()));
		msg.addElement(TREQUEST_TAG, DataConvertor.dateToString(tReq));
		
		return msg.getTransmissionData();
		
	}
	
	/*
	 * method that kerberos Server should use to parse the Client request
	 */
	
	public static CSRequest parseCSRequest(byte[] request, Key sKey) throws KerberosException{
		
		if(request == null || request.length == 0 || sKey == null) throw new InvalidDataException();
		
		//get the main message XML FILE
		Message msg = new XMLMessage();
		msg.processReceivedData(request);
		
		/* create the ticket object*/
		Message ticketMessage = new EncryptedXMLMessage(sKey);
		ticketMessage.processReceivedData(DataConvertor.getBytesFromBase64String(msg.getElement(TICKET_TAG)));
		
		String clientID = ticketMessage.getElement(CLIENT_TAG);
		int serverID = Integer.parseInt(ticketMessage.getElement(SERVER_TAG));
		String t1 = ticketMessage.getElement(T1_TAG);
		String t2 = ticketMessage.getElement(T2_TAG);
		Key csKey = DataConvertor.stringToKey(ticketMessage.getElement(CS_KEY_TAG));
		
		Ticket ticket = KerberosFactory.createTicket(clientID, serverID, t1, t2, csKey);
		
		/*create Authentication object*/

		Message authMessage = new EncryptedXMLMessage(ticket.getCsKey());
		authMessage.processReceivedData(DataConvertor.getBytesFromBase64String(msg.getElement(AUTH_TAG)));

		
		String clientIDAuth = authMessage.getElement(CLIENT_TAG);
		String tRequest = authMessage.getElement(TREQUEST_TAG);
		
		Authentication auth = KerberosFactory.createAuthentication(clientIDAuth, tRequest);
		
		
		/*create HMAC object*/
		Message hmacMessage = new EncryptedXMLMessage(ticket.getCsKey());
		hmacMessage.processReceivedData(DataConvertor.getBytesFromBase64String(msg.getElement(HMAC_TAG)));
		
		String resume = hmacMessage.getElement(ORIGINAL_RESUME_TAG);
		HMAC hmac = KerberosFactory.createHMAC(DataConvertor.getBytesFromBase64String(resume));
		
		/*get tRequest*/
		Date tReq = DataConvertor.stringToDate(msg.getElement(TREQUEST_TAG));		
		
		return new CSRequest(ticket, auth, hmac, tReq);	
	}
	
	
	/*
	 * Methods to handle the Server -> Client Response
	 */
	
	/*
	 * method that kerberos Server should use to generate the Client response
	 */
	
	public static byte[] createSResponse(Date tReq, Key csKey) throws KerberosException{
		
		if(tReq == null || csKey == null) throw new InvalidDataException();
		
		Message msg = new XMLMessage();
		Message encryptedMsg = new EncryptedXMLMessage(csKey);
		
		encryptedMsg.addElement(TREQUEST_TAG, DataConvertor.dateToString(tReq));
		
		msg.addElement(CLIENT_TAG, DataConvertor.getStringFromBytes(encryptedMsg.getTransmissionData()));
		
		return msg.getTransmissionData();
	}
	
	/*
	 * method that kerberos Client should use to parse the Server response
	 */
	
	public static SResponse parseSResponse(byte[] request, Key csKey) throws KerberosException{
		
		if(request == null || request.length == 0 || csKey == null) throw new InvalidDataException();
		
		Message msg = new XMLMessage();
		msg.processReceivedData(request);
		
		Message clientMessage = new EncryptedXMLMessage(csKey);
		clientMessage.processReceivedData(DataConvertor.getBytesFromBase64String(msg.getElement(CLIENT_TAG)));
		
		Date tReq = DataConvertor.stringToDate(clientMessage.getElement(TREQUEST_TAG));
		
		return new SResponse(tReq);
	}
	
	

}
