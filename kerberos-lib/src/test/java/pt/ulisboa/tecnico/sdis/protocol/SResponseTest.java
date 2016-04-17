package pt.ulisboa.tecnico.sdis.protocol;

import static org.junit.Assert.*;

import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import javax.crypto.KeyGenerator;

import org.junit.Test;

import pt.ulisboa.tecnico.sdis.protocol.Communication.CommunicationHandler;
import pt.ulisboa.tecnico.sdis.protocol.Communication.SResponse;
import pt.ulisboa.tecnico.sdis.protocol.Exception.FailedToParseMessageException;
import pt.ulisboa.tecnico.sdis.protocol.Exception.InvalidDataException;
import pt.ulisboa.tecnico.sdis.protocol.Exception.KerberosException;
import pt.ulisboa.tecnico.sdis.protocol.Exception.WrongKeyException;
import pt.ulisboa.tecnico.sdis.protocol.toolkit.DataConvertor;
import pt.ulisboa.tecnico.sdis.protocol.toolkit.Encrypt;

public class SResponseTest {

	private Date dateTreq = new Date();
	private static final Key CSKEY = Encrypt.generateKey();
	private static final Key WRONGKEY = Encrypt.generateKey();
	private static final byte[] empty = {};
	

	
	/*
	 * Tests
	 */
	
	// Test 1: Tests success at sending response with correct parameters
	@Test
	public void successAtSResponse() throws KerberosException{
		
		byte[] request = CommunicationHandler.createSResponse(dateTreq, CSKEY);
		SResponse receivedRequest = CommunicationHandler.parseSResponse(request, CSKEY);
		
		assertEquals(DataConvertor.dateToString(dateTreq), DataConvertor.dateToString(receivedRequest.gettReq()));
	
	}
	
	// Test 2: Tests error when clients tries to decode server response with wrong key
	@Test(expected=FailedToParseMessageException.class)
	public void wrongKeyAtSResponse() throws KerberosException{
		
		byte[] request = CommunicationHandler.createSResponse(dateTreq, CSKEY);
		SResponse receivedRequest = CommunicationHandler.parseSResponse(request, WRONGKEY);
		
		assertEquals(DataConvertor.dateToString(dateTreq), DataConvertor.dateToString(receivedRequest.gettReq()));
	
	}
	
	// Test 3: Tests error when clients tries to decode server response with invalid key
	@Test(expected=WrongKeyException.class)
	public void invalidKeyAtSResponse() throws KerberosException{
		
		Key invalidKey = null;
		KeyGenerator keyGen;
		
		try {
			keyGen = KeyGenerator.getInstance("DES");
			keyGen.init(56);
	        invalidKey = keyGen.generateKey();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
		byte[] request = CommunicationHandler.createSResponse(dateTreq, CSKEY);
		CommunicationHandler.parseSResponse(request, invalidKey);
		
	}
	
	@Test(expected=InvalidDataException.class)
	public void invalidData1() throws KerberosException{
		CommunicationHandler.createSResponse(null, CSKEY);
	}
	
	@Test(expected=InvalidDataException.class)
	public void invalidData2() throws KerberosException{
		CommunicationHandler.createSResponse(dateTreq, null);
	}
	
	@Test(expected=InvalidDataException.class)
	public void invalidData3() throws KerberosException{
		CommunicationHandler.parseSResponse(null, CSKEY);
	}
	
	@Test(expected=InvalidDataException.class)
	public void invalidData4() throws KerberosException{
		CommunicationHandler.parseSResponse(empty, CSKEY);
	}
	
	@Test(expected=InvalidDataException.class)
	public void invalidData5() throws KerberosException{
		CommunicationHandler.parseSResponse(CSKEY.getEncoded(), null);
	}
}
