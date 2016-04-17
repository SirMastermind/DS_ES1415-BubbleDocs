package pt.ulisboa.tecnico.sdis.protocol.Messages;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;

import pt.ulisboa.tecnico.sdis.protocol.Exception.FailedToCreateMessageException;
import pt.ulisboa.tecnico.sdis.protocol.Exception.FailedToParseMessageException;
import pt.ulisboa.tecnico.sdis.protocol.Exception.InvalidDataException;
import pt.ulisboa.tecnico.sdis.protocol.Exception.KerberosException;


public class XMLMessage implements Message {
	

	public final static String ROOT_TAG = "root";
	
	private Element xmlDoc;
	
	/*
	 * Constructor
	 */
	
	public XMLMessage(){
		setXmlDoc(new Element(ROOT_TAG));
	}
	
	/*
	 * Getters and Setters
	 */
	
	public Element getXmlDoc() {
		return xmlDoc;
	}

	public void setXmlDoc(Element xmlDoc) {
		this.xmlDoc = xmlDoc;
	}
	
	/*
	 * Interface Related Methods
	 */

	public void addElement(String tag, String content) throws InvalidDataException {
		if(tag == null || content == null || tag == "" || content == "") throw new InvalidDataException();
		getXmlDoc().addContent(new Element(tag).setText(content));
	}

	public String getElement(String tag) throws InvalidDataException {
		
		Element child = getXmlDoc().getChild(tag);
		if(child == null) throw new InvalidDataException();
		return child.getValue();
	}

	public byte[] getTransmissionData() throws KerberosException {
		
		try {
			ByteArrayOutputStream data = new ByteArrayOutputStream();
			XMLOutputter xmlOutput = new XMLOutputter();
			xmlOutput.output(new Document(getXmlDoc()), data);
			return data.toByteArray();
		} catch (Exception e) {
			System.out.println(e.getClass());
			throw new FailedToCreateMessageException();
		}
			
	}

	public void processReceivedData(byte[] data) throws KerberosException {
		
		try {
			SAXBuilder builder = new SAXBuilder();
			Document document;
			document = builder.build(new ByteArrayInputStream(data));
			setXmlDoc(document.getRootElement());
			
		} catch (Exception e) {
			throw new FailedToParseMessageException();
		} 
	}
	
}
