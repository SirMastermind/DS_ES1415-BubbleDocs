package pt.ulisboa.tecnico.sdis.id.domain;

import java.util.Random;

public class User {

	private String username;
	private String email;
	private byte[] password;
	
	public User(String username, String email){
		this.username = username;
		this.email = email;
		generateNewPassword();
	}
	
	//Getters and Setters
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public byte[] getPassword() {
		return password;
	}

	public void setPassword(byte[] password) {
		this.password = password;
	}
	
	public void generateNewPassword(){
		byte[] b = new byte[20];
		new Random().nextBytes(b);
		setPassword(b);
	}
	

}
