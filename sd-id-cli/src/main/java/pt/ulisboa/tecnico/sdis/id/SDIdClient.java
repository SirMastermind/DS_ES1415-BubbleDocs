package pt.ulisboa.tecnico.sdis.id;


import java.security.Key;
import java.util.*;

import javax.xml.registry.JAXRException;
import javax.xml.ws.*;

import pt.ulisboa.tecnico.sdis.PersistenceManager.PersistenceManager;
import pt.ulisboa.tecnico.sdis.exception.*;
import pt.ulisboa.tecnico.sdis.id.ws.*;
import pt.ulisboa.tecnico.sdis.protocol.Communication.CommunicationHandler;
import pt.ulisboa.tecnico.sdis.protocol.Communication.SAuthResponse;
import pt.ulisboa.tecnico.sdis.protocol.Exception.KerberosException;
import pt.ulisboa.tecnico.sdis.protocol.toolkit.DataConvertor;
import pt.ulisboa.tecnico.sdis.protocol.toolkit.Encrypt;
import pt.ulisboa.tecnico.sdis.uddi.UDDINaming;
import static javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY;


public class SDIdClient {
	
	private SDId_Service service = null;
	private SDId port = null;
	private final static int SERVERID = 1;
	
	public SDIdClient(String name, String uddiURL) throws  WServiceNotFoundException, NoAnswerFromServerException{
		String endpointAddress = connectUDDI(name, uddiURL);
		createStub(endpointAddress);
	}
	
	
	private String connectUDDI(String name, String uddiURL) throws WServiceNotFoundException, NoAnswerFromServerException {
		
		String endpointAddress;
		
		try{
			System.out.printf("Contacting UDDI at %s%n", uddiURL);
	        UDDINaming uddiNaming = new UDDINaming(uddiURL);
	        
			System.out.printf("Looking for '%s'%n", name);
			endpointAddress = uddiNaming.lookup(name);
			
		    
		    //if webservice name is not found in UDDI server, null is returned
		    if (endpointAddress == null) {
		    	throw new WServiceNotFoundException("Web Service: " + name + " not found at: " + uddiURL);
		    } 
		    else {
		        System.out.printf("Found %s%n", endpointAddress);
		    }
		}
		catch(JAXRException e) { 
			throw new NoAnswerFromServerException("No Response from uddi server at: " + uddiURL);
		}
		
	    return endpointAddress;
	   
	}
	
	
	private void createStub(String endpointAddress) {
        System.out.println("Creating stub ...");
        service = new SDId_Service();
        port = service.getSDIdImplPort();

        System.out.println("Setting endpoint address ...");
        BindingProvider bindingProvider = (BindingProvider) port;
        Map<String, Object> requestContext = bindingProvider.getRequestContext();
        requestContext.put(ENDPOINT_ADDRESS_PROPERTY, endpointAddress);
    }
	
	
	public void createUser(String username, String email) 
			throws EmailAlreadyExists_Exception, InvalidEmail_Exception, InvalidUser_Exception, 
				UserAlreadyExists_Exception, NoAnswerFromServerException {
			
			try{
				port.createUser(username, email);
			}
			catch(WebServiceException e) { 
				throw new NoAnswerFromServerException("Failed to communicate with ws host server");
			}
	}
	
	public void removeUser(String username) 
			throws UserDoesNotExist_Exception, NoAnswerFromServerException{
			
		try{
			port.removeUser(username);
		}
		catch(WebServiceException e) { 
			throw new NoAnswerFromServerException("Failed to communicate with ws host server");
		}	
	}
	
	public void renewPassword(String username) 
			throws UserDoesNotExist_Exception, NoAnswerFromServerException{
		
		try{
			port.renewPassword(username);
		}
		catch(WebServiceException e) { 
			throw new NoAnswerFromServerException("Failed to communicate with ws host server");
		}	
	}
	
	
	public SAuthResponse requestAuthentication(String username, String password) 
			throws AuthReqFailed_Exception, NoAnswerFromServerException{
		
		try{
			int nonce = generateNonce();
			byte[] reserved = CommunicationHandler.createCSautRequest(username, SERVERID, nonce);
			byte[] answerRaw = port.requestAuthentication(username, reserved);
			Key cKey = Encrypt.generateMD5Key(password);
			
			SAuthResponse answer = CommunicationHandler.parseSAuthResponse(answerRaw, cKey);
			if(answer.getNonce() != nonce) throw new AuthReqFailed_Exception("Failed to log in", new AuthReqFailed());			
			
			/*
			 * add ticket and session key to the Persistence Manager
			 */
			
			PersistenceManager.setInfo(answer.getTicket(), DataConvertor.keyToString(answer.getCsKey()));
			
			return answer;
			
		}catch(KerberosException e){
			throw new AuthReqFailed_Exception("Failed to log in", new AuthReqFailed());			
		}catch(WebServiceException e) { 
			throw new NoAnswerFromServerException("Failed to communicate with ws host server");
		}	
	
			
	}
	
	private int generateNonce(){
		Random randomGenerator = new Random();
		return randomGenerator.nextInt(10000);
	}
}
