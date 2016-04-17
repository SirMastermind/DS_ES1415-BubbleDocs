package pt.ulisboa.tecnico.sdis.protocol.Communication;

import java.util.Date;


public class SResponse {
	
	private Date tReq;
	
	/*
	 * Constructor
	 */
	
	public SResponse(Date tReq) {	
		this.tReq = tReq;
	}
	
	/*
	 * Getters and Setters
	 */
	
	public Date gettReq() {
		return tReq;
	}
	public void settReq(Date tReq) {
		this.tReq = tReq;
	}
	
	
		
	



}
