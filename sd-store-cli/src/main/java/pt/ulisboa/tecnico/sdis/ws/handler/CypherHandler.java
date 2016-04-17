package pt.ulisboa.tecnico.sdis.ws.handler;

import static javax.xml.bind.DatatypeConverter.parseBase64Binary;
import static javax.xml.bind.DatatypeConverter.printBase64Binary;

import java.io.IOException;
import java.security.Key;
import java.util.Set;

import javax.crypto.Cipher;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.*;
import javax.xml.soap.SOAPPart;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPBody;

import org.w3c.dom.*;

import pt.ulisboa.tecnico.sdis.protocol.toolkit.Encrypt;

public class CypherHandler implements SOAPHandler<SOAPMessageContext> {

	private static UserKeys _map = new UserKeys();
	
    public Set<QName> getHeaders() {
        return null;
    }

    public boolean handleMessage(SOAPMessageContext smc) {
        handleMessageCypher(smc);
        return true;
    }

    public boolean handleFault(SOAPMessageContext smc) {
        handleMessageCypher(smc);
        return true;
    }

    public void close(MessageContext messageContext) {
    }

    private void handleMessageCypher(SOAPMessageContext smc) {
        Boolean outbound = (Boolean) smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
        if (outbound) {
        	System.out.println("OUTBOUND_BEFORE_CIPHER*************");
        	try { smc.getMessage().writeTo(System.out);
			} catch (SOAPException e) { e.printStackTrace();
			} catch (IOException e) { e.printStackTrace(); }
        	System.out.println("\n*********************");
        	testHandlerOut(smc);
            cypherMessage(smc);
            testHandlerOutAftCipher(smc);
            System.out.println("OUTBOUND_AFTER_CIPHER*************");
        	try { smc.getMessage().writeTo(System.out);
			} catch (SOAPException e) { e.printStackTrace();
			} catch (IOException e) { e.printStackTrace(); }
        	System.out.println("\n*********************");

        } else {
        	System.out.println("INBOUND_BEFORE_DECIPHER*************");
        	try { smc.getMessage().writeTo(System.out);
			} catch (SOAPException e) { e.printStackTrace();
			} catch (IOException e) { e.printStackTrace(); }
        	System.out.println("\n*********************");
        	testHandlerInBefDecipher(smc);
        	decypherMessage(smc);
        	testHandlerIn(smc);
        	System.out.println("INBOUND_AFTER_DECIPHER*************");
        	try { smc.getMessage().writeTo(System.out);
			} catch (SOAPException e) { e.printStackTrace();
			} catch (IOException e) { e.printStackTrace(); }
        	System.out.println("\n*********************");
        }
    }
	
    private String getUser(SOAPMessageContext smc) {
    	String user = "";
    	try {
        	SOAPMessage message = smc.getMessage();
    		SOAPPart soapPart = message.getSOAPPart();
        	SOAPEnvelope soapEnvelope = soapPart.getEnvelope();
			SOAPBody soapBody = soapEnvelope.getBody();
			Node owner = null;
        	if (soapBody.getFirstChild().getNodeName().equals("ns2:loadResponse")) {
        		for(Node n = soapBody.getFirstChild().getFirstChild(); n != null; n = n.getNextSibling()) {
        			if(n.getNodeName().equals("DocOwner")) {
        				owner = n;
        				break;
        			}
        		}
        		if (owner != null) {
        			user = owner.getTextContent();
        		}
        	}
        	else if (soapBody.getFirstChild().getNodeName().equals("ns2:store")) {
        		for(Node n = soapBody.getFirstChild().getFirstChild(); n != null; n = n.getNextSibling()) {
        			if(n.getNodeName().equals("docUserPair")) {
        				for(Node s = n.getFirstChild(); s != null; s = s.getNextSibling()) {
        					if(s.getNodeName().equals("userId")) {
        						owner = s;
        						break;
        					}
        				}
        			}
        		}
        		if (owner != null) {
        			user = owner.getTextContent();
        		}
        	}
    	} catch (SOAPException e) { e.printStackTrace(); }
    	//System.out.println("DocOwner: " + user);
    	return user;
    }
	
	public Key manageUserKey(String user) {
		if(user == null) {
			return null;
		}
		if (_map.containsKey(user)) {
			Key oldKey = _map.getKey(user);
			return oldKey;
		}
		else {
			Key key = generateKey(user);
			_map.addEntry(user, key);
			_map.updateMap(user, key);
			return key;
		}
	}
	
	public static Key generateKey(String user) {
		/*Key key = null;
		try{
		    KeyGenerator keyGen = KeyGenerator.getInstance("DES");
		    keyGen.init(56);
		    key = keyGen.generateKey();
		} catch (Exception e) { }
		return key;*/
		return Encrypt.generateMD5Key(user);
	}
	
	public static Cipher generateCipher() {
		Cipher cipher = null;
		try {
		    cipher = Cipher.getInstance("AES");
		} catch (Exception e) {}
	    return cipher;
	}
	
    private void cypherMessage(SOAPMessageContext smc) {
    	SOAPMessage message = smc.getMessage();
    	try {
            SOAPPart soapPart = message.getSOAPPart();
            SOAPEnvelope soapEnvelope = soapPart.getEnvelope();

            SOAPBody soapBody = soapEnvelope.getBody();  
            
            {
            	Node n = soapBody.getFirstChild();
            	Node content = null;
            	if (n.getNodeName().equals("ns2:store")) {
	                for(Node s = n.getFirstChild(); s != null; s = s.getNextSibling()) {
	                	if(s.getNodeName().equals("contents")) {
	                		content = s;
	                		break;
	                	}
	                }
	                if (content != null) {
	                	String user = getUser(smc);
	                	
	                	Key key= manageUserKey(user);
	                	Cipher cipher = Cipher.getInstance("AES");
	                	
	                	String s = content.getTextContent();
	                	//System.out.println("Before cipher, content is: " + s);
	                    cipher.init(Cipher.ENCRYPT_MODE, key);
	                    byte[] cipherBytes = cipher.doFinal(s.getBytes());
	                	
	                    String cipherText = printBase64Binary(cipherBytes);
	                    //System.out.println("After cipher, content is: " + cipherText);
	                	content.setTextContent(cipherText);
	                }
            	}
            }
        } catch (Exception e) {
            // print error information
            //System.out.printf("I couldn't cipher this");
            //System.out.printf("Caught exception in main method: %s%n", e);
        }
    }
    
    private void decypherMessage(SOAPMessageContext smc) { 
    	SOAPMessage message = smc.getMessage();
    	try {
            SOAPPart soapPart = message.getSOAPPart();
            SOAPEnvelope soapEnvelope = soapPart.getEnvelope();

            SOAPBody soapBody = soapEnvelope.getBody();
            
            {
            	Node n = soapBody.getFirstChild();
            	Node content = null;
            	if (n.getNodeName().equals("ns2:loadResponse")) {
	                for(Node s = n.getFirstChild(); s != null; s = s.getNextSibling()) {
	                	if(s.getNodeName().equals("contents")) {
	                		content = s;
	                		break;
	                	}
	                }
	                if (content != null) {
	                	String user = getUser(smc);
	                	
	                	Key key = manageUserKey(user);
	                	
	                	Cipher cipher = Cipher.getInstance("AES");

	                	String s = content.getTextContent();
	                	//System.out.println("Before decipher, content is: " + s);
	                    byte[] cipherBodyBytes = parseBase64Binary(s);
	                    
	                    cipher.init(Cipher.DECRYPT_MODE, key);
	                    byte[] newPlainBytes = cipher.doFinal(cipherBodyBytes);

	                    String newPlainText = new String(newPlainBytes);
	                    //System.out.println("After decipher, content is: " + newPlainText);
	                	content.setTextContent(newPlainText);
	                }
            	}
            }
        } catch (Exception e) {
        }
    }
    
    private void testHandlerOut(SOAPMessageContext smc) { }
    private void testHandlerIn(SOAPMessageContext smc) { }
    private void testHandlerOutAftCipher(SOAPMessageContext smc) { }
    private void testHandlerInBefDecipher(SOAPMessageContext smc)  { }
}
