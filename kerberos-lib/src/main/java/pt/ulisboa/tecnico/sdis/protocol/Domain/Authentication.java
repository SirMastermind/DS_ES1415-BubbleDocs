package pt.ulisboa.tecnico.sdis.protocol.Domain;

import java.util.Date;

public class Authentication {
	
	private String clientID;
	private Date tRequest;
	
	/*
	 * Constructor
	 */
	protected Authentication(String clientID, Date tRequest) {
		this.clientID = clientID;
		this.tRequest = tRequest;
	}
	
	/*
	 * Getters
	 */

	public String getClientID() {
		return clientID;
	}

	public Date gettRequest() {
		return tRequest;
	}
	
}
