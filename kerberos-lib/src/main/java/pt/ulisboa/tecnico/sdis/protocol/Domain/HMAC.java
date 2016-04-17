package pt.ulisboa.tecnico.sdis.protocol.Domain;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class HMAC {
	
	private MessageDigest digester;
	private byte[] originalResume;

	/*
	 * Constructor
	 */
	protected HMAC(byte[] resume) {
		
		try {
			this.originalResume = resume;
			this.digester = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
	}
	
	
	/*
	 * Getters
	 */
	public MessageDigest getDigester() {
		return digester;
	}
	
	public byte[] getOriginalResume() {
		return originalResume;
	}

	
	/*
	 * Method to update the digester content
	 */
	
	public void update(){
		getDigester().update(getOriginalResume());
	}
	
	/*
	 * Method to check if 2 resumes are equal
	 */
	public boolean isValid(byte[] resume){
		return MessageDigest.isEqual(getOriginalResume(), resume);
	}
	

}
