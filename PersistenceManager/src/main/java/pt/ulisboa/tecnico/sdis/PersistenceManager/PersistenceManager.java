package pt.ulisboa.tecnico.sdis.PersistenceManager;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.jdom2.output.XMLOutputter;
import org.jdom2.output.Format;






import pt.ulisboa.tecnico.sdis.protocol.Communication.CommunicationHandler;
import pt.ulisboa.tecnico.sdis.protocol.Exception.InvalidDataException;
import pt.ulisboa.tecnico.sdis.protocol.Exception.KerberosException;
import pt.ulisboa.tecnico.sdis.protocol.Messages.*;


public final class PersistenceManager{
	
	
	public static void setInfo(String ticket,String cskey){
		try {
			XMLMessage message = new XMLMessage();
			message.addElement(CommunicationHandler.TICKET_TAG, ticket);
			message.addElement(CommunicationHandler.CS_KEY_TAG, cskey);
			XMLOutputter xmlOutput = new XMLOutputter();
			xmlOutput.setFormat(Format.getPrettyFormat());
			xmlOutput.output(message.getXmlDoc(),new FileOutputStream("keymanager.xml"));
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
	
	public static void setInfo(String tReq){
		try {
			XMLMessage message = new XMLMessage();
			message.addElement(CommunicationHandler.TREQUEST_TAG, tReq);
			XMLOutputter xmlOutput = new XMLOutputter();
			xmlOutput.setFormat(Format.getPrettyFormat());
			xmlOutput.output(message.getXmlDoc(),new FileOutputStream("keymanagerTime.xml"));
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
	
	
	public static String getInfo(String tag){
		Path path1 = Paths.get("keymanager.xml");
		Path path2 = Paths.get("keymanagerTime.xml");
		Path path;
		
		if(tag.equals(CommunicationHandler.TREQUEST_TAG))
			path = path1;
		else
			path = path2;
		
		try {
			byte[] data = Files.readAllBytes(path);
			XMLMessage xmlmessage = new XMLMessage();
			xmlmessage.processReceivedData(data);
			System.out.println(xmlmessage.getElement(tag));
			return xmlmessage.getElement(tag);
		} catch (KerberosException e) {
			return null;
		} catch (IOException e) {
			return null;
		}    
	}
}