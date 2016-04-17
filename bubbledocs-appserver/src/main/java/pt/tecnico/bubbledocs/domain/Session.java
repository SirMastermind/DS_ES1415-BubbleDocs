package pt.tecnico.bubbledocs.domain;

import org.joda.time.LocalTime;

import java.util.Random;

public class Session extends Session_Base {
    
    public Session(User user) {
        super();
        setUser(user);
        setToken(generateToken());
        setLastAccess(new LocalTime());
    }
    

    private String generateToken(){
    	Random randomGenerator = new Random();
    	return getUser().getUsername()+randomGenerator.nextInt(10);
    }
    
    public void refreshSession(){
    	setLastAccess(new LocalTime());
    }
   
    
	public void delete() {
		setUser(null);
		getSm().removeSessions(this);
	}
    
 
}
