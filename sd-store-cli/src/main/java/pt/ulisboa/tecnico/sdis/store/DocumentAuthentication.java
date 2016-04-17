package pt.ulisboa.tecnico.sdis.store;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import pt.ulisboa.tecnico.sdis.exception.DocHasBeenChanged_Exception;

public class DocumentAuthentication {
	private byte[] _document;
	private byte[] _digest = new byte[40];
	private byte[] _combined;
	private static final String algorithm = "SHA-256";
	private SecretKey _key;
	
	public DocumentAuthentication(byte[] array, String s, SecretKey key) throws DocHasBeenChanged_Exception {
		_key = key;
		if(s.equals("document")) {
			_document = array.clone();
			_combined = new byte[_document.length + _digest.length];
		}
		else if (s.equals("combined")) {
			_combined = array.clone();
			if (_combined.length < 32) {
				throw new DocHasBeenChanged_Exception();
			}
			_document = new byte[_combined.length - _digest.length];
		}
		else {
			System.err.println("Wrong parameter: " + s);
		}
	}
	
	public void createDigest() {
		try {
			MessageDigest di;
			di = MessageDigest.getInstance(algorithm);
			
			di.update(_document);
			_digest = di.digest();
			try {
				cipherDigest();
			} catch (Exception e)  { }
			
			System.arraycopy(_document, 0, _combined, 0, _document.length);
			System.arraycopy(_digest, 0, _combined, _document.length, _digest.length);
			
		} catch (NoSuchAlgorithmException e) { System.err.println("Algoritmo errado: " + algorithm); }
	}
	
	public void fillDocumentAndCheckDigest() throws DocHasBeenChanged_Exception {
		try {
			int length = _document.length;
			
			for(int i = 0; i < length; i++) {
				_document[i] = _combined[i];
			}

			for(int i = length; i < _combined.length; i++) {
				_digest[i-length] = _combined[i];
			}
			decipherDigest();
		} catch (Exception e)  { }
		try {
			
			MessageDigest di;
			di = MessageDigest.getInstance(algorithm);
			di.update(_document);
			byte[] digest_supposed = di.digest();

			if (!MessageDigest.isEqual(_digest, digest_supposed)) {
				throw new DocHasBeenChanged_Exception();
			}
		} catch (NoSuchAlgorithmException e) { System.err.println("Algoritmo errado: " + algorithm); }
	}
	
	public byte[] getDocument() {
		return _document;
	}
	
	public byte[] getDigest() {
		return _digest;
	}
	
	public byte[] getCombined() {
		return _combined;
	}
	
	public static SecretKey generate() {
		try {
			KeyGenerator keyGen;
			keyGen = KeyGenerator.getInstance("DES");
		
	        keyGen.init(56);
	        SecretKey key = keyGen.generateKey();
			
	        return key;
		} catch (NoSuchAlgorithmException e) { System.err.println("Algoritmo geracao errado");}
		return null;
    }
	
	public void cipherDigest() throws Exception {
        Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, _key);
        _digest = cipher.doFinal(_digest);
    }
	
	public void decipherDigest() throws Exception {
        Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, _key);
        _digest = cipher.doFinal(_digest);
    }
}
