package pt.ulisboa.tecnico.sdis.protocol.Communication;

import java.security.Key;


public class SAuthResponse {
	
	private String ticket; //encoded ticket
	private Key csKey;
	private int nonce;
	
	/*
	 * Constructor
	 */
	
	public SAuthResponse(String ticket, Key csKey, int nonce) {
		this.ticket = ticket;
		this.csKey = csKey;
		this.nonce = nonce;
	}
	
	
	/*
	 * Getters and Setters
	 */
	
	public String getTicket() {
		return ticket;
	}
	
	public void setTicket(String ticket) {
		this.ticket = ticket;
	}
	public Key getCsKey() {
		return csKey;
	}
	public void setCsKey(Key csKey) {
		this.csKey = csKey;
	}
	public int getNonce() {
		return nonce;
	}
	public void setNonce(int nonce) {
		this.nonce = nonce;
	}
 
	
	
}
