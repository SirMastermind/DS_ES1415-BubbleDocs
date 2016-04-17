
package pt.ulisboa.tecnico.sdis.store;

import java.io.*;

import javax.xml.soap.*;
import javax.xml.transform.stream.*;


/**
 *  Abstract handler test suite
 */
public abstract class AbstractHandlerTest {

    // static members

    /** hello-ws SOAP request message captured with LoggingHandler */
    protected static final String SOAP_REQUEST = "<S:Envelope " +
    "xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\" " +
    "xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
    "<SOAP-ENV:Header/>" +
    "<S:Body>" +
    "<ns2:sayHello xmlns:ns2=\"http://ws.example/\">" +
    "<arg0>friend</arg0>" +
    "</ns2:sayHello>" +
    "</S:Body></S:Envelope>";

    /** hello-ws SOAP response message captured with LoggingHandler */
    protected static final String SOAP_RESPONSE = "<S:Envelope " +
    "xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\" " +
    "xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
    "<SOAP-ENV:Header/>" +
    "<S:Body>" +
    "<ns2:sayHelloResponse xmlns:ns2=\"http://ws.example/\">" +
    "<return>Hello friend!</return>" +
    "</ns2:sayHelloResponse>" +
    "</S:Body></S:Envelope>";

    /** SOAP message factory */
    protected static final MessageFactory MESSAGE_FACTORY;

    static {
        try {
            MESSAGE_FACTORY = MessageFactory.newInstance();
        } catch(SOAPException e) {
            throw new RuntimeException(e);
        }
    }


    // helper functions

    protected static SOAPMessage byteArrayToSOAPMessage(byte[] msg) throws Exception {
        ByteArrayInputStream byteInStream = new ByteArrayInputStream(msg);
        StreamSource source = new StreamSource(byteInStream);
        SOAPMessage newMsg =  MESSAGE_FACTORY.createMessage();
        SOAPPart soapPart = newMsg.getSOAPPart();
        soapPart.setContent(source);
        return newMsg;
    }


   

}



