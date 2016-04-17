package pt.ulisboa.tecnico.sdis.id.kerberos;

import java.util.Hashtable;
import java.util.Map;




public class ServerStore {
	
	public static final int SD_STORE_ID = 1;
	
	private Map<Integer, String> servers = new Hashtable<Integer, String>();
	
	
	public ServerStore(){
			addServer(SD_STORE_ID,"chavefixe");
	}
	
	public void addServer(int serverID, String strkey){
		this.servers.put(serverID, strkey);
	}
	
	public String getServer(int serverID){
		if (this.servers.containsKey(serverID))
				return this.servers.get(serverID);
		return null;
	}
}