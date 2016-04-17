package pt.ulisboa.tecnico.sdis.protocol.Messages;

import pt.ulisboa.tecnico.sdis.protocol.Exception.InvalidDataException;
import pt.ulisboa.tecnico.sdis.protocol.Exception.KerberosException;


public interface Message{
	
	
	/*
     * Classes that implement the interface must manage the message elements
	 */
	
	public abstract void addElement(String tag, String content) throws InvalidDataException;
	public abstract String getElement(String tag) throws InvalidDataException;
	
	
	
	/*
	 * public method that converts the message to a byte array[]
	 */
	
	public abstract byte[] getTransmissionData() throws KerberosException;

	
	/*
	 * public method that converts the bytes recevided and generates the
	 * message
	 */
	
	public abstract void processReceivedData(byte[] data) throws KerberosException;
	
}