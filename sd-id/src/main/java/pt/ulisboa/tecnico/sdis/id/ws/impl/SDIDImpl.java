package pt.ulisboa.tecnico.sdis.id.ws.impl;

import java.security.Key;
import java.util.Date;

import javax.jws.*;

import pt.ulisboa.tecnico.sdis.id.domain.User;
import pt.ulisboa.tecnico.sdis.id.domain.UsersStore;
import pt.ulisboa.tecnico.sdis.id.kerberos.ServerStore;
import pt.ulisboa.tecnico.sdis.id.ws.*; // classes generated from WSDL
import pt.ulisboa.tecnico.sdis.protocol.Communication.CSautRequest;
import pt.ulisboa.tecnico.sdis.protocol.Communication.CommunicationHandler;
import pt.ulisboa.tecnico.sdis.protocol.Domain.KerberosFactory;
import pt.ulisboa.tecnico.sdis.protocol.Domain.Ticket;
import pt.ulisboa.tecnico.sdis.protocol.Exception.InvalidDataException;
import pt.ulisboa.tecnico.sdis.protocol.Exception.KerberosException;
import pt.ulisboa.tecnico.sdis.protocol.toolkit.DataConvertor;
import pt.ulisboa.tecnico.sdis.protocol.toolkit.Encrypt;



@WebService(
	    endpointInterface="pt.ulisboa.tecnico.sdis.id.ws.SDId",
	    wsdlLocation="SD-ID.1_1.wsdl",
	    name="SdId",
	    portName="SDIdImplPort",
	    targetNamespace="urn:pt:ulisboa:tecnico:sdis:id:ws",
	    serviceName="SDId"
)
public class SDIDImpl implements SDId {
	
	private static final int SESSION_TTL = 2;
	private UsersStore usersStore;
	private ServerStore serverStore;
	
	public SDIDImpl(){
		this.usersStore = new UsersStore();
		this.serverStore = new ServerStore();
		this.usersStore.populate4Tests();
	}
	
	public void setUsersStore(UsersStore store){
		this.usersStore = store;
	}
	

	public void createUser(String userId, String emailAddress)
			throws EmailAlreadyExists_Exception, InvalidEmail_Exception,
			InvalidUser_Exception, UserAlreadyExists_Exception {
		
		usersStore.createNewUser(userId, emailAddress);
		
	}

	public void renewPassword(String userId) throws UserDoesNotExist_Exception {
		usersStore.renewPassword(userId);
		
	}

	public void removeUser(String userId) throws UserDoesNotExist_Exception {
		usersStore.removeUser(userId);
		
	}

	public byte[] requestAuthentication(String userId, byte[] reserved) throws AuthReqFailed_Exception{
		
		
		try {
			CSautRequest request = CommunicationHandler.parseCSautRequest(reserved);
			Ticket ticket = generateTicket(userId);
			int nonce = request.getNonce();
			int serverID = request.getServerID();
			Key csKey = ticket.getCsKey();
			Key cKey = getClientKey(userId);
			Key sKey = getServerKey(serverID);
			System.out.println("\n----------Request Authentication------------");
			System.out.println("CSKey: " + DataConvertor.keyToString(csKey));
			System.out.println("CKey: " + DataConvertor.keyToString(cKey));
			System.out.println("SKey: " + DataConvertor.keyToString(sKey));
			System.out.println("--------------------------------------------\n");
			return CommunicationHandler.createSAuthResponse(ticket, nonce, csKey, cKey, sKey);
					
		} catch (KerberosException e1) {
			AuthReqFailed issue = new AuthReqFailed();
			issue.setReserved(reserved);
			throw new AuthReqFailed_Exception("Invalid Data Received", issue);
		}

				
	}
	
	private Ticket generateTicket(String clientID) throws InvalidDataException{
		
		Date dateT1 = new Date();
		Date dateT2 = DataConvertor.createDateT2(dateT1, SESSION_TTL);
		String t1String = DataConvertor.dateToString(dateT1);		
		String t2String = DataConvertor.dateToString(dateT2);
		Key csKey = Encrypt.generateKey();
		
		return KerberosFactory.createTicket(clientID, 1, t1String, t2String, csKey);

	}

	private Key getServerKey(int serverID) throws AuthReqFailed_Exception{
		String strKey = serverStore.getServer(serverID);
		
		if (strKey == null){
			AuthReqFailed issue = new AuthReqFailed();
			throw new AuthReqFailed_Exception("Server does not exist", issue);
		}
		return Encrypt.generateMD5Key(strKey);
	}
	
	private Key getClientKey(String clientID) throws AuthReqFailed_Exception{
	
		try {
			User user = usersStore.getUser(clientID);
			return Encrypt.generateMD5Key(new String(user.getPassword()));
		} catch (UserDoesNotExist_Exception e) {
			AuthReqFailed issue = new AuthReqFailed();
			throw new AuthReqFailed_Exception("Invalid User", issue);
		}
						
	}
}
