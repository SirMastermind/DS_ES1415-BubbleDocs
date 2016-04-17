package pt.ulisboa.tecnico.sdis.protocol.Domain;

import java.security.Key;
import java.util.Date;


public class Ticket {
	
	private String clientID;
	private int serverID;
	private Date t1;
	private Date t2;
	private Key csKey;
	
	
	/*
	 * Constructor
	 */
	protected Ticket(String clientID, int serverID, Date t1, Date t2 , Key csKey){
		this.clientID = clientID;
		this.serverID = serverID;
		this.t1 = t1;
		this.t2 = t2;
		this.csKey = csKey;
	}
	
	/*
	 * Getters
	 */
	
	public String getClientID() {
		return clientID;
	}

	public int getServerID() {
		return serverID;
	}


	public Date getT1() {
		return t1;
	}

	public Date getT2() {
		return t2;
	}

	public Key getCsKey() {
		return csKey;
	}

}