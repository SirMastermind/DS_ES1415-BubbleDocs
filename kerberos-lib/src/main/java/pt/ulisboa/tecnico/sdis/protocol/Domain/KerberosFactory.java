package pt.ulisboa.tecnico.sdis.protocol.Domain;

import java.security.Key;
import java.util.Date;

import pt.ulisboa.tecnico.sdis.protocol.Exception.InvalidDataException;
import pt.ulisboa.tecnico.sdis.protocol.toolkit.DataConvertor;

public final class KerberosFactory {
	
	private static void validateString(String str) throws InvalidDataException{
		if(str == null || str == "") throw new InvalidDataException();
	}
	
	private static void validateKey(Key key) throws InvalidDataException{
		if(key == null) throw new InvalidDataException();
	}
	
	private static void validateByteArray(byte[] array) throws InvalidDataException{
		if(array == null || array.length == 0) throw new InvalidDataException();
	}
	
	private static Date stringToDate(String stringDate) throws InvalidDataException{
		return DataConvertor.stringToDate(stringDate);
	}
	
	
	public static Ticket createTicket(String clientID, int serverID, String t1String, String t2String , Key csKey) throws InvalidDataException{
		
		validateString(clientID); 
		validateString(t1String); 
		validateString(t2String);
		validateKey(csKey);
		
		return new Ticket(clientID, serverID, stringToDate(t1String), stringToDate(t2String), csKey);
	}
	
	public static Authentication createAuthentication(String clientID, String tRequest) throws InvalidDataException{
		
		validateString(clientID);
		validateString(tRequest);
		
		return new Authentication(clientID, stringToDate(tRequest));
	}
	
	public static HMAC createHMAC(byte[] resume) throws InvalidDataException{
		
		validateByteArray(resume);
		
		return new HMAC(resume);
	}
	


}
