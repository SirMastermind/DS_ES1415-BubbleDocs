package pt.ulisboa.tecnico.sdis.protocol.domain;

import static org.junit.Assert.*;

import org.junit.Test;

import pt.ulisboa.tecnico.sdis.protocol.Domain.HMAC;
import pt.ulisboa.tecnico.sdis.protocol.Domain.KerberosFactory;
import pt.ulisboa.tecnico.sdis.protocol.Exception.InvalidDataException;

public class HMACTest {
	
	private static final byte[] array = {(byte)0,(byte)1};
	private static final byte[] empty = {};
	
	@Test 
	public void success() throws InvalidDataException{
		
		HMAC hmac = KerberosFactory.createHMAC(array);
		assertArrayEquals(array, hmac.getOriginalResume());
	}
	
	@Test(expected = InvalidDataException.class)
	public void invalidData1() throws InvalidDataException{
		KerberosFactory.createHMAC(null);
	}
	
	@Test(expected = InvalidDataException.class)
	public void invalidData2() throws InvalidDataException{
		KerberosFactory.createHMAC(empty);
	}
}
