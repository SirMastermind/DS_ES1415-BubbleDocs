package pt.ulisboa.tecnico.sdis.protocol.toolkit;

import static org.junit.Assert.*;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.junit.Test;

import pt.ulisboa.tecnico.sdis.protocol.Exception.InvalidDataException;

public class EncryptTest {

	private static final String MSG = "HELLO WORLD";
	private static final String PASSWORD = "DIOGOBATATA9595";
	
	@Test
	public void testEncrypt() throws UnsupportedEncodingException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidDataException{
		
		Key myKey = Encrypt.generateKey();
		byte[] plainBytes = MSG.getBytes("UTF-8");
		
		
		byte[] encodedBytes = Encrypt.encode(myKey, plainBytes);
		byte[] decodedBytes = Encrypt.decode(myKey, encodedBytes);
		
		String decodedMsg = new String(decodedBytes, "UTF-8");
		
		assertEquals(decodedMsg,MSG);
		
	}
	
	@Test(expected=BadPaddingException.class)
	public void testWrongKey() throws UnsupportedEncodingException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidDataException{
		
		Key myKey = Encrypt.generateKey();
		Key wrongKey = Encrypt.generateKey();
		
		byte[] plainBytes = MSG.getBytes("UTF-8");
		byte[] encodedBytes = Encrypt.encode(myKey, plainBytes);
		
		Encrypt.decode(wrongKey, encodedBytes);
	}
	
	@Test
	public void testCustomKey() throws UnsupportedEncodingException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidDataException{
		
		Key myKey = Encrypt.generateMD5Key(PASSWORD);
		Key myKey2 = Encrypt.generateMD5Key(PASSWORD);
		
		byte[] plainBytes = MSG.getBytes("UTF-8");
		byte[] encodedBytes = Encrypt.encode(myKey, plainBytes);
		byte[] decodedBytes = Encrypt.decode(myKey2, encodedBytes);
		
		String decodedMsg = new String(decodedBytes, "UTF-8");
		assertEquals(decodedMsg,MSG);
		
	}
	
	@Test
	public void testKeyFromStringDecode() throws UnsupportedEncodingException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidDataException{
		
		Key myKey = Encrypt.generateKey();
		byte[] plainBytes = MSG.getBytes("UTF-8");
		byte[] encodedBytes = Encrypt.encode(myKey, plainBytes);
		
		String myKeyString = DataConvertor.keyToString(myKey);
		
		Key myKey2 = DataConvertor.stringToKey(myKeyString);
		
		byte[] decodedBytes = Encrypt.decode(myKey2, encodedBytes);
		
		String decodedMsg = new String(decodedBytes, "UTF-8");
		assertEquals(decodedMsg,MSG);
		
	}
}
