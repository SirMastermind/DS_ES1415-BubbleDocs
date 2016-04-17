package pt.ulisboa.tecnico.sdis.protocol.Communication;


public class CSautRequest{
	
	private String clientID;
	private int serverID;
	private int nonce;
	
	/*
	 * Constructor
	 */
	
	protected CSautRequest(String clientID, int serverID, int nonce) {
		setClientID(clientID);
		setServerID(serverID);
		setNonce(nonce);		
	}

	/*
	 * Getters and Setters
	 */
	
	public int getNonce() {
		return nonce;
	}

	public void setNonce(int nonce) {
		this.nonce = nonce;
	}

	public int getServerID() {
		return serverID;
	}

	public void setServerID(int serverID) {
		this.serverID = serverID;
	}

	public String getClientID() {
		return clientID;
	}

	public void setClientID(String clientID) {
		this.clientID = clientID;
	}


	
}