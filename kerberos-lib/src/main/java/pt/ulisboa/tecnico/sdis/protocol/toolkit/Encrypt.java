package pt.ulisboa.tecnico.sdis.protocol.toolkit;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import pt.ulisboa.tecnico.sdis.protocol.Exception.InvalidDataException;

public final class Encrypt {
	
	
	private static void validateFields(byte[] data, Key key) throws InvalidDataException{
		if(data == null || data.length == 0) throw new InvalidDataException();
		if(key == null) throw new InvalidDataException();
	}
	
	public static Key generateKey(){
		KeyGenerator keyGen;
		try {
			keyGen = KeyGenerator.getInstance("AES");
			keyGen.init(128);
	        return keyGen.generateKey();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	
	public static byte[] generateMD5(String message){
		MessageDigest md;
		byte[] result = null;
		try {
			md = MessageDigest.getInstance("MD5");
			result =  md.digest(message.getBytes("UTF-8"));
		
		} catch (NoSuchAlgorithmException e) {	//never happens 
			e.printStackTrace();	
		}catch (UnsupportedEncodingException e) {	//never happens
			e.printStackTrace();
		}
		
		return result;
	}
	
	
	public static Key generateMD5Key(String message){
		
		return new SecretKeySpec(generateMD5(message), "AES");
	}

	/*
	 * method that decodes an encrypted byte array
	 */
	public static byte[] decode(Key key, byte[] encodedData) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, 
										IllegalBlockSizeException, BadPaddingException, InvalidDataException {
		
		validateFields(encodedData, key);
		
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.DECRYPT_MODE, key);
		
		return cipher.doFinal(encodedData);
	}
	
	/*
	 * method that encodes a plain bytes array
	 */
	
	public static byte[] encode(Key key, byte[] plainBytes) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, 
											IllegalBlockSizeException, BadPaddingException, InvalidDataException{
		
		validateFields(plainBytes, key);
		
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.ENCRYPT_MODE, key);
		
		return cipher.doFinal(plainBytes);
	}
	
	
}
