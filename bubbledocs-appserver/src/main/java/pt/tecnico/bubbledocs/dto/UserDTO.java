package pt.tecnico.bubbledocs.dto;

public class UserDTO {
	private String name;
	private String email;
	private String username;
	
	public UserDTO( String username, String name, String email){
		this.name = name;
		this.email = email;
		this.username = username;
	}
	
    public final String getName() {
        return this.name;
    }

    public final String getEmail() {
        return this.email;
    }

	public String getUsername() {
		return username;
	}
}
