package pt.ulisboa.tecnico.sdis.protocol.Communication;


import java.util.Date;
import pt.ulisboa.tecnico.sdis.protocol.Domain.Authentication;
import pt.ulisboa.tecnico.sdis.protocol.Domain.HMAC;
import pt.ulisboa.tecnico.sdis.protocol.Domain.Ticket;


public class CSRequest {
	
	
	private Ticket ticket;
	private Authentication auth;
	private HMAC hmac;
	private Date tReq;
	
	/*
	 * Constructor
	 */
	public CSRequest(Ticket ticket, Authentication auth, HMAC hmac, Date tReqString) {
		setAuth(auth);
		setHmac(hmac);
		setTicket(ticket);
		settReq(tReqString);
	}
	
	/*
	 * Getters and Setters
	 */
	
	public HMAC getHmac() {
		return hmac;
	}

	public void setHmac(HMAC hmac) {
		this.hmac = hmac;
	}

	public Authentication getAuth() {
		return auth;
	}

	public void setAuth(Authentication auth) {
		this.auth = auth;
	}

	public Ticket getTicket() {
		return ticket;
	}

	public void setTicket(Ticket ticket) {
		this.ticket = ticket;
	}

	public Date gettReq(){
		return this.tReq;
	}
	
	public void settReq(Date tReq) {
		this.tReq =tReq;
	}
}