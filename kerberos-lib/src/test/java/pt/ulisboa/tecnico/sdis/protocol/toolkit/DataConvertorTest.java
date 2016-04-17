package pt.ulisboa.tecnico.sdis.protocol.toolkit;

import java.security.Key;
import java.util.Arrays;
import java.util.Date;

import static org.junit.Assert.*;

import org.junit.Test;

import pt.ulisboa.tecnico.sdis.protocol.Exception.InvalidDataException;

public class DataConvertorTest {
	
	private static final String BASE64DATA = "Y2VuYXNmaXhlcw==";
	private static final String NOTBASE64DATA = "HELLO wOrLd!@";
	private static final byte[] BYTEDATA = "HELLO wOrLd!@".getBytes();
	private static final int MILITEST = 1000;
			
	private static final Date DATE = new Date();
	
	
	/*
	 * Base64 String <--> Byte[]  Tests
	 */
	
	@Test
	public void testStringBytesConversion() throws InvalidDataException{
		
		byte[] byteDataTest = DataConvertor.getBytesFromBase64String(BASE64DATA);
		String testData = DataConvertor.getStringFromBytes(byteDataTest);
		
		String testData2 = DataConvertor.getStringFromBytes(BYTEDATA);
		byte[] byteDataTest2 = DataConvertor.getBytesFromBase64String(testData2);
		
		
		//should fail because byteDataTest should be base64
		assertFalse(Arrays.equals(BYTEDATA, byteDataTest));
		
		assertEquals(BASE64DATA, testData);
		assertArrayEquals(BYTEDATA, byteDataTest2);
	}
	
	@Test(expected=InvalidDataException.class)
	public void invalidDataTest10() throws InvalidDataException{
		DataConvertor.getBytesFromBase64String(NOTBASE64DATA);
	}
	
	@Test(expected=InvalidDataException.class)
	public void invalidDataTest12() throws InvalidDataException{
		DataConvertor.getBytesFromBase64String(null);
	}
	
	
	@Test(expected=InvalidDataException.class)
	public void invalidDataTest14() throws InvalidDataException{
		DataConvertor.getStringFromBytes(null);
	}
	
	/*
	 * Date <---> String tests
	 */
	
	@Test
	public void testDateStringConversion() throws InvalidDataException{
		
		String dateStringTest = DataConvertor.dateToString(DATE);
		Date dateTest = DataConvertor.stringToDate(dateStringTest);
		
		Date dateTest2 = DataConvertor.stringToDate(dateStringTest);
		String dateStringTest2 = DataConvertor.dateToString(dateTest2);
		
		assertTrue(DATE.getTime() - dateTest.getTime() < MILITEST);
		assertEquals(dateStringTest, dateStringTest2);		
	}
	
	@Test(expected=InvalidDataException.class)
	public void invalidDataTest20() throws InvalidDataException{
		DataConvertor.dateToString(null);
	}
	
	@Test(expected=InvalidDataException.class)
	public void invalidDataTest21() throws InvalidDataException{
		DataConvertor.stringToDate(BASE64DATA);
	}
	
	@Test(expected=InvalidDataException.class)
	public void invalidDataTest22() throws InvalidDataException{
		DataConvertor.stringToDate("");
	}
	
	@Test(expected=InvalidDataException.class)
	public void invalidDataTest23() throws InvalidDataException{
		DataConvertor.stringToDate(null);
	}
	
	/*
	 * Key <---> Base64 String tests
	 */
	
	@Test
	public void testKeyBase64String() throws InvalidDataException{
		
		Key sKey = Encrypt.generateKey();
		
		String sKeyString = DataConvertor.keyToString(sKey);
		Key sKeyTest = DataConvertor.stringToKey(sKeyString);
		
		assertArrayEquals(sKey.getEncoded(), sKeyTest.getEncoded());
	}
	
	@Test(expected=InvalidDataException.class)
	public void invalidDataTest30() throws InvalidDataException{
		DataConvertor.keyToString(null);
	}
	
	@Test(expected=InvalidDataException.class)
	public void invalidDataTest31() throws InvalidDataException{
		DataConvertor.stringToKey(null);
	}
	
	@Test(expected=InvalidDataException.class)
	public void invalidDataTest32() throws InvalidDataException{
		DataConvertor.stringToKey(NOTBASE64DATA);
	}
}
