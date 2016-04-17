package pt.ulisboa.tecnico.sdis.ws.handler;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Key;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;

import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import pt.ulisboa.tecnico.sdis.protocol.Communication.CommunicationHandler;
import pt.ulisboa.tecnico.sdis.protocol.Exception.InvalidDataException;
import pt.ulisboa.tecnico.sdis.protocol.Exception.KerberosException;
import pt.ulisboa.tecnico.sdis.protocol.Messages.XMLMessage;
import pt.ulisboa.tecnico.sdis.protocol.toolkit.DataConvertor;

public class UserKeys implements Serializable {
	private Map<String, Key> _map = new HashMap<String, Key>();
	
	public Key getKey(String user) {
		try{
			return _map.get(user);
		} catch(NullPointerException e){
			Path path = Paths.get("userkeys.xml");
			
			try {
				byte[] data = Files.readAllBytes(path);
				XMLMessage xmlmessage = new XMLMessage();
				xmlmessage.processReceivedData(data);
				Key key = DataConvertor.stringToKey(xmlmessage.getElement(user));
				return key;
			} catch (KerberosException e1) {
				return null;
			} catch (IOException e1) {
				return null;
			}  
		}
	}
	
	public void addEntry(String user, Key key) {
		_map.put(user, key);
	}
	
	public boolean containsKey(String user) {
		return _map.containsKey(user);
	}
	
	public void updateMap(String user, Key key){
			try {
				String keyString = DataConvertor.keyToString(key);
				XMLMessage message = new XMLMessage();
				message.addElement(user, keyString);
				XMLOutputter xmlOutput = new XMLOutputter();
				xmlOutput.setFormat(Format.getPrettyFormat());
				xmlOutput.output(message.getXmlDoc(),new FileOutputStream("userkeys.xml", true));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (InvalidDataException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
}
