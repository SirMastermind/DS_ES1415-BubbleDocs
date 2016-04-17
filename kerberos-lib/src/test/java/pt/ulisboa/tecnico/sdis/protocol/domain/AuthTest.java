package pt.ulisboa.tecnico.sdis.protocol.domain;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.junit.Test;

import pt.ulisboa.tecnico.sdis.protocol.Domain.Authentication;
import pt.ulisboa.tecnico.sdis.protocol.Domain.KerberosFactory;
import pt.ulisboa.tecnico.sdis.protocol.Exception.InvalidDataException;
import pt.ulisboa.tecnico.sdis.protocol.toolkit.DataConvertor;

public class AuthTest {
	
	private static final String CLIENTID = "1";
	
	
	@Test
	public void success() throws InvalidDataException{
		String t1 =DataConvertor.dateToString(new Date());
	
		Authentication auth = KerberosFactory.createAuthentication(CLIENTID, t1);
	
		assertEquals(CLIENTID, auth.getClientID());
		assertEquals(t1, DataConvertor.dateToString(auth.gettRequest()));
	}
	
	@Test(expected=InvalidDataException.class)
	public void invalidData1() throws InvalidDataException{
		String t1 =DataConvertor.dateToString(new Date());
		KerberosFactory.createAuthentication(null, t1);
	}
	
	@Test(expected=InvalidDataException.class)
	public void invalidData2() throws InvalidDataException{
		KerberosFactory.createAuthentication(CLIENTID, null);
	}
	
	@Test(expected=InvalidDataException.class)
	public void invalidData3() throws InvalidDataException{
		KerberosFactory.createAuthentication(CLIENTID, CLIENTID);
	}
}
