package pt.ulisboa.tecnico.sdis.id;

import pt.ulisboa.tecnico.sdis.exception.SDIDCommunicationException;


public class SDIdClientMain {
	
	public static void main(String[] args) throws Exception {
    	// Check arguments
        if (args.length < 2) {
            System.err.println("Argument(s) missing!");
            System.err.printf("Usage: java %s uddiURL name%n", SDIdClient.class.getName());
            return;
        }
        String uddiURL = args[0];
        String name = args[1];	
        try {
        	SDIdClient client = new SDIdClient(name, uddiURL);

	        client.createUser("diogo", "dmcalado@gmail.com");
	        client.removeUser("diogo");
	        
	        client = null;
	        
		} 
        catch(SDIDCommunicationException e){ //all comunication related issues
        	System.out.println(e.getMessage());
        }
        catch (Exception e) { //this should not happen, but to prevent unexpected events
			System.out.println("An unexpected error occurred, this program will now terminate");
		}
     
    }

}
