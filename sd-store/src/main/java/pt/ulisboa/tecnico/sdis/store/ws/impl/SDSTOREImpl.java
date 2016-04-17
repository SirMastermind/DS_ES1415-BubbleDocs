package pt.ulisboa.tecnico.sdis.store.ws.impl;

import java.util.List;

import javax.jws.*;

import pt.ulisboa.tecnico.sdis.store.ws.*; // classes generated from WSDL
import pt.ulisboa.tecnico.sdis.store.ws.handler.DocOwnerHandler;
import pt.ulisboa.tecnico.sdis.store.ws.handler.TagHandler;
import pt.ulisboa.tecnico.sdis.store.domain.Store;
import pt.ulisboa.tecnico.sdis.store.domain.Tag;
import pt.ulisboa.tecnico.sdis.store.dto.DocumentDTO;

import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;
import javax.annotation.Resource;

@WebService(
	    endpointInterface="pt.ulisboa.tecnico.sdis.store.ws.SDStore",
	    wsdlLocation="SD-STORE.1_1.wsdl",
	    name="SdStore",
	    portName="SDStoreImplPort",
	    targetNamespace="urn:pt:ulisboa:tecnico:sdis:store:ws",
	    serviceName="SDStore"
)
@HandlerChain(file="/handler-chain.xml")
public class SDSTOREImpl implements SDStore {
	
	
	@Resource
    private WebServiceContext webServiceContext;
	
	private Store _store = new Store();
	
	public void createDoc(DocUserPair docUserPair) throws DocAlreadyExists_Exception, UserDoesNotExist_Exception {
		MessageContext messageContext = webServiceContext.getMessageContext();
		messageContext.put(TagHandler.RESPONSE_TYPE, "createDocResponse");
		_store.createDocument(docUserPair.getUserId(), docUserPair.getDocumentId());
	}

	public List<String> listDocs(String userId) throws UserDoesNotExist_Exception {
		MessageContext messageContext = webServiceContext.getMessageContext();
		messageContext.put(TagHandler.RESPONSE_TYPE, "listDocsResponse");
		return _store.listDocs(userId);
	}

	public void store(DocUserPair docUserPair, byte[] contents) throws DocDoesNotExist_Exception, UserDoesNotExist_Exception {
		MessageContext messageContext = webServiceContext.getMessageContext();
		Tag t = (Tag)messageContext.get(TagHandler.REQUEST_PROPERTY);
		
		_store.store(docUserPair.getUserId(), docUserPair.getDocumentId(), new DocumentDTO(contents.clone(), t));
		
		messageContext.put(TagHandler.RESPONSE_TYPE, "storeResponse");
		
	}

	public byte[] load(DocUserPair docUserPair) throws DocDoesNotExist_Exception, UserDoesNotExist_Exception {
		
		DocumentDTO doc = _store.load(docUserPair.getUserId(), docUserPair.getDocumentId());
		
		MessageContext messageContext = webServiceContext.getMessageContext();
		messageContext.put(TagHandler.RESPONSE_TYPE, "loadResponse");
		messageContext.put(TagHandler.RESPONSE_PROPERTY, doc.getTag().toString());
		messageContext.put(DocOwnerHandler.DOC_OWNER, docUserPair.getUserId());
		
		return doc.getDocument();
	}

}
