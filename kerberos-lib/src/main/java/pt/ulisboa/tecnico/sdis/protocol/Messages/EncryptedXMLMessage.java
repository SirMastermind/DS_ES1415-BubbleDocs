package pt.ulisboa.tecnico.sdis.protocol.Messages;

import java.security.InvalidKeyException;
import java.security.Key;

import pt.ulisboa.tecnico.sdis.protocol.Exception.FailedToCreateMessageException;
import pt.ulisboa.tecnico.sdis.protocol.Exception.FailedToParseMessageException;
import pt.ulisboa.tecnico.sdis.protocol.Exception.InvalidDataException;
import pt.ulisboa.tecnico.sdis.protocol.Exception.KerberosException;
import pt.ulisboa.tecnico.sdis.protocol.Exception.WrongKeyException;
import pt.ulisboa.tecnico.sdis.protocol.toolkit.Encrypt;

public class EncryptedXMLMessage extends XMLMessage {

	private Key key;
	
	/*
	 * Constructor
	 */
	public EncryptedXMLMessage(Key key){
		setKey(key);
	}
	
	/*
	 * Getters and Setters
	 */
	
	public Key getKey() {
		return key;
	}

	public void setKey(Key key) {
		this.key = key;
	}
	
	/*
	 * Public methods Overrided from super class so that all messages 
	 * get encrypted and decrypted properly 
	 */
	
	@Override
	public byte[] getTransmissionData() throws KerberosException {
		
		try {
			return Encrypt.encode(getKey(), super.getTransmissionData());
		
		} catch (InvalidKeyException e){
			throw new WrongKeyException();
		} catch (InvalidDataException e){
			throw e;
		} catch (Exception e) {
			throw new FailedToCreateMessageException();
		} 
		
	}
	
	@Override
	public void processReceivedData(byte[] data) throws KerberosException{
		
		try {
			super.processReceivedData(Encrypt.decode(getKey(), data));
		
		} catch (InvalidKeyException e){
			throw new WrongKeyException();
		} catch (InvalidDataException e){
			throw e;
		} catch (Exception e) {
			throw new FailedToParseMessageException();
		} 
	}

}
