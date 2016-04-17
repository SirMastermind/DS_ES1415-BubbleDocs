package pt.ulisboa.tecnico.sdis.id.domain;

import java.util.Iterator;
import java.util.Map;
import java.util.Hashtable;


import pt.ulisboa.tecnico.sdis.id.ws.*;


public class UsersStore {

	private Map<String, User> users = new Hashtable<String, User>();
	
	private boolean validEmail(String email){
		return email != null && email.matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9]+(\\.[a-zA-Z]+)?");
		
	}						
	
	private boolean validUsername(String username){
		return username != null && !username.isEmpty();
	}
	
	private boolean userExists (String username) {
		return username != null && this.users.containsKey(username);
	}
	
	private boolean emailAlreadyExists(String email){
		Iterator<Map.Entry<String, User>> it = this.users.entrySet().iterator();

		while (it.hasNext()) {
			Map.Entry<String, User> entry = it.next();

			if(entry.getValue().getEmail().equals(email)){
				return true;
			}
		}
		
		return false;
	}
	
		
	private void checkUserData(String username, String email) 
			throws InvalidEmail_Exception, InvalidUser_Exception, UserAlreadyExists_Exception, EmailAlreadyExists_Exception{
				
				//validate email format
				if(!validEmail(email)){
					InvalidEmail issue = new InvalidEmail();
					issue.setEmailAddress(email);
					throw new InvalidEmail_Exception("Email not valid", issue);
				}
				
				//validate username format
				if(!validUsername(username)){
					InvalidUser issue = new InvalidUser();
					issue.setUserId(username);
					throw new InvalidUser_Exception("username not valid", issue);
				}
				
				//check if the username is unique
				if(userExists(username)){
					UserAlreadyExists issue = new UserAlreadyExists();
					issue.setUserId(username);
					throw new UserAlreadyExists_Exception("username already exists", issue);
				}
				
				//check if the email was never used
				if(emailAlreadyExists(email)){
					EmailAlreadyExists issue = new EmailAlreadyExists();
					issue.setEmailAddress(email);
					throw new EmailAlreadyExists_Exception("email already exists", issue);
				}		
	}
	
		
	
	public UsersStore(){
		User root = new User("root", "root@localhost");
		root.setPassword("rootroot".getBytes());
		this.users.put(root.getUsername(), root);
	}
	
	public User getUser(String username) throws UserDoesNotExist_Exception{
		
		if(!userExists(username)){
			UserDoesNotExist issue = new UserDoesNotExist();
			issue.setUserId(username);
			throw new UserDoesNotExist_Exception("username does not exists", issue);
		}
		
		return this.users.get(username);
	}
	
	
	/*
	 * Services implementation
	 */

	public void createNewUser(String username, String email) 
			throws EmailAlreadyExists_Exception, InvalidEmail_Exception, UserAlreadyExists_Exception, InvalidUser_Exception{
				
		checkUserData(username, email);
		User newUser = new User(username, email);
		this.users.put(username, newUser);
		
		System.out.println("\n----------------Create USER--------------");
		System.out.println("UserName: " + newUser.getUsername());
		System.out.println("Password: " + newUser.getPassword());
		System.out.println("Email: " + newUser.getUsername());
		System.out.println("-----------------------------------------\n");
	}
	
	public void renewPassword(String username) throws UserDoesNotExist_Exception{
		User user = getUser(username);
		user.generateNewPassword();
		System.out.println("\n----------------Renew PASSWORD--------------");
		System.out.println("UserName: " + user.getUsername());
		System.out.println("Password: " + user.getPassword());
		System.out.println("--------------------------------------------\n");
	}
	
	public void removeUser(String username) throws UserDoesNotExist_Exception{
		getUser(username);
		System.out.println("\n----------------DELETED USER--------------");
		System.out.println("UserName: " + username);
		System.out.println("-----------------------------------------\n");
		this.users.remove(username);
		
	}

	public void populate4Tests() {
		
		String usernames[] = {"alice", "bruno", "carla", "duarte", "eduardo"};
		String passwords[] = {"Aaa1", "Bbb2", "Ccc3", "Ddd4", "Eee5"};
		String emails[] = {"alice@tecnico.pt", "bruno@tecnico.pt", "carla@tecnico.pt", "duarte@tecnico.pt", "eduardo@tecnico.pt"};
		
		for(int i=0; i<usernames.length; i++){
			User newUser = new User(usernames[i], emails[i]);
			newUser.setPassword(passwords[i].getBytes());
			this.users.put(usernames[i], newUser);
		}
	}
}
