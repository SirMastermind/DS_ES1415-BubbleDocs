package pt.ulisboa.tecnico.sdis.id;

import static org.junit.Assert.*;


import mockit.Expectations;
import mockit.Mocked;

import org.jdom2.input.SAXBuilder;
import org.junit.*;

import pt.ulisboa.tecnico.sdis.exception.NoAnswerFromServerException;
import pt.ulisboa.tecnico.sdis.exception.SDIDCommunicationException;
import pt.ulisboa.tecnico.sdis.id.ws.*;
import pt.ulisboa.tecnico.sdis.protocol.Communication.SAuthResponse;
import pt.ulisboa.tecnico.sdis.protocol.Exception.FailedToParseMessageException;
import pt.ulisboa.tecnico.sdis.protocol.Exception.KerberosException;


public class RequestAuthenticationTest {

	private static SDIdClient _client;
	private static final String UDDIURL = "http://localhost:8081";
	private static final String WSNAME = "SD-ID";
	private static final String USERNAME = "duarte";
	private static final String PASSWORD = "Ddd4";
	private static final String INVALID_USERNAME = "shosho";
	private static final String INVALID_PASSWORD = "errrwrong";
	private static final int WRONGNONCE = -11;	
	@BeforeClass
	public static void oneTimeSetup() throws SDIDCommunicationException{
		
		_client = new SDIdClient(WSNAME, UDDIURL);
	}
	
	@AfterClass
	public static void oneTimeTearDown() throws UserDoesNotExist_Exception{
		_client = null;
	}
	
	
	//test login with correct parameters
	@Test
    public void requestAuthenticationSuccess() 
    		throws AuthReqFailed_Exception, NoAnswerFromServerException, KerberosException{
		
		SAuthResponse res = _client.requestAuthentication(USERNAME, PASSWORD);
		
		assertNotNull(res.getCsKey());
		assertTrue(res.getNonce() >= 0);
		assertNotNull(res.getTicket());
    }
	
	
	//test login with invalid password
	@Test(expected=AuthReqFailed_Exception.class)
	public void requestAuthenticationInvalidPasswordFailed() 
			throws AuthReqFailed_Exception, NoAnswerFromServerException{
		
		_client.requestAuthentication(USERNAME, INVALID_PASSWORD);

	}
	
	//test login with invalid username
	@Test(expected=AuthReqFailed_Exception.class)
	public void requestAuthenticationInvalidUserFailed() 
			throws AuthReqFailed_Exception, NoAnswerFromServerException{
		
		_client.requestAuthentication(INVALID_USERNAME, PASSWORD);
	}
	
	//test login when server answers with an invalid response
	@Test(expected=AuthReqFailed_Exception.class)
	public void invalidServerResponse(@Mocked final SAXBuilder saxbuilder)
			throws AuthReqFailed_Exception, NoAnswerFromServerException{
		
				/*
				 * hack - unable to mock server we mock the object that 
				 * parses the response from server has it would be processing 
				 * bad input from server
				 */
				new Expectations() {{
					new SAXBuilder();
					result = new FailedToParseMessageException();
				}};

		_client.requestAuthentication(USERNAME, PASSWORD);		
		
	}
	
	//test login when server answers with valid response format but wrong nonce
	@Test(expected=AuthReqFailed_Exception.class)
	public void invalidServerNonce(@Mocked final SAuthResponse sauth)
			throws AuthReqFailed_Exception, NoAnswerFromServerException{
		

			new Expectations() {{
				sauth.getNonce();
				result = WRONGNONCE;
			}};
		
		_client.requestAuthentication(USERNAME, PASSWORD);
	}
	
}
