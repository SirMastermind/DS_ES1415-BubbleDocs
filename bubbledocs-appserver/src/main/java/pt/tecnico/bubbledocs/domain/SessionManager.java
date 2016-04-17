package pt.tecnico.bubbledocs.domain;

import org.joda.time.LocalTime;
import org.joda.time.Minutes;

import pt.tecnico.bubbledocs.exception.*;

public class SessionManager extends SessionManager_Base {
    
	private static final int SESSION_TTL = 120;
	
    public SessionManager() {
        super();
    }
    
    
    private boolean hasExpired(Session sd){
    	LocalTime currentDate = new LocalTime();
    	int minutes = Minutes.minutesBetween(sd.getLastAccess(), currentDate).getMinutes();
    	if (minutes < 0) minutes += 24*60; 
    	
    	return minutes >= SESSION_TTL;
    }
    
    
    //removes the session if it exists
    private void removeSessionFromUser(User user){
    	Session sd = getSessionFromUser(user);
    	if(sd != null){
    		removeSessions(sd);
    	}
    }

    
    //returns the Session from username
    private Session getSessionFromUser(User user) {
  		for(Session sd : getSessionsSet()) {
  		    if(sd.getUser() == user) { //pointer comparison valid in this case
  		    	return sd;
  			}
  		}
  		return null;
  	}
    
    //returns the SessionDetails from username
    private Session getSessionFromToken(String token) {
  		for(Session sd : getSessionsSet()) {
  		    if(sd.getToken().equals(token)) {
  		    	return sd;
  			}
  		}
  		return null;
  	}
    
    //removes the expired sessions
    private void deleteExpiredSessions(){
    	for(Session sd : getSessionsSet()) {
  		    if(hasExpired(sd)) {
  		    	removeSessions(sd);
  			}
  		}
    }
    

    public String loginUser(User user){
    	removeSessionFromUser(user);
    	Session sd = new Session(user);
    	addSessions(sd);
    	deleteExpiredSessions();
    	return sd.getToken();
    }
    
    public User getUserFromToken(String token) throws UserNotInSessionException {
    	Session sd = getSessionFromToken(token);
    	if(sd == null || hasExpired(sd)) throw new UserNotInSessionException();
    	return sd.getUser();
    }
    
    public void logoutUser(User user){
    	removeSessionFromUser(user);
    }
    
    public void refreshSession(User user) throws UserNotInSessionException{
    	Session sd = getSessionFromUser(user);
    	if(sd == null) throw new UserNotInSessionException();
    	sd.refreshSession();
    }
    
    public LocalTime lastAccessTime(String token) throws InvalidDataException{
    	Session sd = getSessionFromToken(token);
    	if(sd == null) throw new InvalidDataException();
    	return sd.getLastAccess();
    }
    
    
    
}
