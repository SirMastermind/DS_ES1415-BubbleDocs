package pt.ulisboa.tecnico.sdis.protocol.domain;

import static org.junit.Assert.*;

import java.security.Key;
import java.util.Date;

import org.junit.Test;

import pt.ulisboa.tecnico.sdis.protocol.Domain.KerberosFactory;
import pt.ulisboa.tecnico.sdis.protocol.Domain.Ticket;
import pt.ulisboa.tecnico.sdis.protocol.Exception.InvalidDataException;
import pt.ulisboa.tecnico.sdis.protocol.toolkit.DataConvertor;
import pt.ulisboa.tecnico.sdis.protocol.toolkit.Encrypt;

public class TicketTest {
	
	private static final String CLIENTID = "1";
	private static final int SERVERID = 1;

	private static final Key KEY = Encrypt.generateKey();
	
	
	//success with valid parameters
	@Test
	public void success() throws InvalidDataException{
		
		String T1 = DataConvertor.dateToString(new Date());
		String T2 = DataConvertor.dateToString(new Date());
		
		Ticket ticket = KerberosFactory.createTicket(CLIENTID, SERVERID, T1, T2, KEY);
		
		assertEquals(CLIENTID, ticket.getClientID());
		assertEquals(SERVERID, ticket.getServerID());
		assertEquals(T1, DataConvertor.dateToString(ticket.getT1()));
		assertEquals(T2, DataConvertor.dateToString(ticket.getT2()));
		assertEquals(DataConvertor.keyToString(KEY), DataConvertor.keyToString(ticket.getCsKey()));
	}
	
	@Test(expected=InvalidDataException.class)
	public void badParameters1() throws InvalidDataException{
		String T1 = DataConvertor.dateToString(new Date());
		String T2 = DataConvertor.dateToString(new Date());
		
		KerberosFactory.createTicket(CLIENTID, SERVERID, T1, T2, null);
	}
	
	@Test(expected=InvalidDataException.class)
	public void badParameters2() throws InvalidDataException{
		KerberosFactory.createTicket(CLIENTID, SERVERID, null, null, KEY);
	}
	
	@Test(expected=InvalidDataException.class)
	public void badParameters3() throws InvalidDataException{
		String T2 = DataConvertor.dateToString(new Date());
		
		KerberosFactory.createTicket(CLIENTID, SERVERID, "", T2, KEY);
	}
	
	@Test(expected=InvalidDataException.class)
	public void badParameters4() throws InvalidDataException{
		String T1 = DataConvertor.dateToString(new Date());
		
		KerberosFactory.createTicket(CLIENTID, SERVERID, T1, CLIENTID, KEY);
	}
	
	@Test(expected=InvalidDataException.class)
	public void badParameters5() throws InvalidDataException{
		String T1 = DataConvertor.dateToString(new Date());
		KerberosFactory.createTicket(null, SERVERID, T1, CLIENTID, KEY);
	}
}
