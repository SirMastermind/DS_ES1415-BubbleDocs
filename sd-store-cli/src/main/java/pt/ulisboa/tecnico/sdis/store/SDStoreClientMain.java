package pt.ulisboa.tecnico.sdis.store;

import java.util.List;

import javax.xml.registry.JAXRException;

public class SDStoreClientMain {
	
	public static void main(String[] args) throws Exception {
        if (args.length < 4) {
            System.err.println("Argument(s) missing!");
            System.err.printf("Usage: java %s uddiURL name RT WT%n", SDStoreClient.class.getName());
            return;
        }
        String uddiURL = args[0];
        String name = args[1];
        String RT = args[2];
        String WT = args[3];
        if(!RT.matches("[0-9]+")) {
        	System.err.println("Bad argument RT");
        	return;
        }
        if(!WT.matches("[0-9]+")) {
        	System.err.println("Bad argument WT");
        	return;
        }
        try {
        	SDStoreClient client = new SDStoreClient(name, uddiURL, Integer.parseInt(RT), Integer.parseInt(WT));
	        client.createDocument("alice", "kappa");
	        List<String> strings = client.listDocuments("alice");
	        for(String s: strings) {
	        	System.out.println(s);
	        }
	        client.storeDocument("alice", "kappa", "Hello world!".getBytes());
	        String s = new String(client.loadDocument("alice", "kappa"));
	        System.out.println(s);
		} catch (JAXRException e) {
			e.printStackTrace();
		}
        
    }

}
